package fluidmech.common.pump;

import fluidmech.common.FluidMech;
import fluidmech.common.pump.path.PathfinderCheckerFindFillable;
import fluidmech.common.pump.path.PathfinderCheckerLiquid;
import fluidmech.common.pump.path.PathfinderFindHighestSource;
import hydraulic.api.IDrain;
import hydraulic.fluidnetwork.IFluidNetworkPart;
import hydraulic.helpers.FluidHelper;
import hydraulic.prefab.tile.TileEntityFluidDevice;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.liquids.ILiquid;
import net.minecraftforge.liquids.ILiquidTank;
import net.minecraftforge.liquids.ITankContainer;
import net.minecraftforge.liquids.LiquidContainerRegistry;
import net.minecraftforge.liquids.LiquidDictionary;
import net.minecraftforge.liquids.LiquidStack;
import universalelectricity.core.vector.Vector3;
import universalelectricity.core.vector.VectorHelper;

public class TileEntityDrain extends TileEntityFluidDevice implements ITankContainer, IDrain
{
	/* MAX BLOCKS DRAINED PER 1/2 SECOND */
	public static int MAX_DRAIN_PER_PROCESS = 30;
	private int currentDrains = 0;
	public int yFillStart = 0;
	/* LIST OF PUMPS AND THERE REQUESTS FOR THIS DRAIN */
	private HashMap<TileEntityConstructionPump, LiquidStack> requestMap = new HashMap<TileEntityConstructionPump, LiquidStack>();

	private List<Vector3> targetSources = new ArrayList<Vector3>();
	private List<Vector3> updateQue = new ArrayList<Vector3>();
	
	@Override
	public String getMeterReading(EntityPlayer user, ForgeDirection side)
	{
		return "Set to "+ (canDrainSources() ? "input liquids" : "output liquids");
	}
	public boolean canDrainSources()
	{
		int meta = 0;
		if (worldObj != null)
		{
			meta = worldObj.getBlockMetadata(xCoord, yCoord, zCoord);
		}
		return meta < 6;
	}
	public ForgeDirection getFacing()
	{
		int meta = 0;
		if (worldObj != null)
		{
			meta = worldObj.getBlockMetadata(xCoord, yCoord, zCoord);
			if(meta > 5)
			{
				meta -= 6;
			}
		}
		return ForgeDirection.getOrientation(meta);
	}

	@Override
	public void updateEntity()
	{

		if (!this.worldObj.isRemote)
		{
			if (this.ticks % (20 + new Random().nextInt(100)) == 0 && updateQue.size() > 0)
			{
				Iterator pp = this.updateQue.iterator();
				while (pp.hasNext())
				{
					Vector3 vec = (Vector3) pp.next();
					worldObj.markBlockForUpdate(vec.intX(), vec.intY(), vec.intZ());
					pp.remove();
				}
			}
			if (this.ticks % 20 == 0)
			{
				/* CLEANUP MAP */
				Iterator mn = this.requestMap.entrySet().iterator();
				while (mn.hasNext())
				{
					Entry<TileEntityConstructionPump, LiquidStack> entry = (Entry<TileEntityConstructionPump, LiquidStack>) mn.next();
					TileEntity entity = entry.getKey();
					if (entity == null)
					{
						mn.remove();
					}
					else if (entity.isInvalid())
					{
						mn.remove();
					}
				}

				this.currentDrains = 0;
				/* MAIN LOGIC PATH FOR DRAINING BODIES OF LIQUID */
				if (this.canDrainSources())
				{
					TileEntity pipe = VectorHelper.getTileEntityFromSide(worldObj, new Vector3(this), this.getFacing().getOpposite());

					if (pipe instanceof IFluidNetworkPart)
					{
						if (this.requestMap.size() > 0)
						{
							this.getNextFluidBlock();

							for (Entry<TileEntityConstructionPump, LiquidStack> request : requestMap.entrySet())
							{
								if (this.currentDrains >= MAX_DRAIN_PER_PROCESS)
								{
									break;
								}
								if (((IFluidNetworkPart) pipe).getNetwork().isConnected(request.getKey()) && targetSources.size() > 0)
								{
									Iterator it = this.targetSources.iterator();
									int m = 0;
									while (it.hasNext())
									{
										Vector3 loc = (Vector3) it.next();
										if (this.currentDrains >= MAX_DRAIN_PER_PROCESS)
										{
											break;
										}

										if (FluidHelper.isSourceBlock(this.worldObj, loc))
										{
											LiquidStack stack = FluidHelper.getLiquidFromBlockId(loc.getBlockID(this.worldObj));
											LiquidStack requestStack = request.getValue();

											if (stack != null && requestStack != null && (requestStack.isLiquidEqual(stack) || requestStack.itemID == -1))
											{
												if (request.getKey().fill(0, stack, false) > 0)
												{
													int requestAmmount = requestStack.amount - request.getKey().fill(0, stack, true);
													if (requestAmmount <= 0)
													{
														this.requestMap.remove(request);
													}
													else
													{
														request.setValue(FluidHelper.getStack(requestStack, requestAmmount));
													}
													if (++m >= 3 && !this.updateQue.contains(loc))
													{
														this.updateQue.add(loc);
													}
													loc.setBlock(this.worldObj, 0, 0, 2);
													this.currentDrains++;
													it.remove();

												}
											}
										}
									}
								}
							}
						}
					}
				}
				// END OF DRAIN
				else
				{
					// TODO time to have fun finding a place for this block to exist
					//this.fillArea(LiquidDictionary.getLiquid("Water", LiquidContainerRegistry.BUCKET_VOLUME * 5), true);
				}
			}
		}
	}

	@Override
	public int fillArea(LiquidStack resource, boolean doFill)
	{
		int drained = 0;
		if (yFillStart == 0 || yFillStart >= 255)
		{
			yFillStart = this.yCoord + this.getFacing().offsetY;
		}

		if (!this.canDrainSources())
		{
			System.out.println("Filling Area: " + doFill);
			if (resource == null || resource.amount < LiquidContainerRegistry.BUCKET_VOLUME)
			{
				System.out.println("Invalid Resource");
				return 0;
			}
			System.out.println("Resource: " + LiquidDictionary.findLiquidName(resource) + ":" + resource.amount);
			int blockID = resource.itemID;
			int meta = resource.itemMeta;
			if (resource.itemID == Block.waterStill.blockID)
			{
				blockID = Block.waterStill.blockID;
				meta = 0;
			}
			else if (resource.itemID != Block.lavaStill.blockID)
			{
				blockID = Block.lavaStill.blockID;
				meta = 0;
			}
			else if (Block.blocksList[resource.itemID] instanceof ILiquid)
			{
				ILiquid liquidBlock = (ILiquid) Block.blocksList[resource.itemID];
				blockID = liquidBlock.stillLiquidId();
				meta = liquidBlock.stillLiquidMeta();
			}
			else
			{
				return 0;
			}

			int blocks = (resource.amount / LiquidContainerRegistry.BUCKET_VOLUME);

			PathfinderCheckerFindFillable pathFinder = new PathfinderCheckerFindFillable(this.worldObj);
			pathFinder.init(new Vector3(this.xCoord + this.getFacing().offsetX, yFillStart, this.zCoord + this.getFacing().offsetZ));
			System.out.println("Nodes: " + pathFinder.closedSet.size());
			int fillable = 0;
			for (Vector3 loc : pathFinder.closedSet)
			{
				if (blocks <= 0)
				{
					break;
				}
				LiquidStack stack = FluidHelper.getLiquidFromBlockId(loc.getBlockID(worldObj));
				if (stack != null && stack.isLiquidEqual(resource) && loc.getBlockMetadata(worldObj) != 0)
				{
					fillable++;
					drained += LiquidContainerRegistry.BUCKET_VOLUME;
					blocks--;
					if (doFill)
					{
						System.out.println("PlacedAt:Flowing: " + loc.toString());
						loc.setBlock(worldObj, blockID, meta);
						if (!this.updateQue.contains(loc))
						{
							this.updateQue.add(loc);
						}
					}
				}

			}

			for (Vector3 loc : pathFinder.closedSet)
			{
				if (blocks <= 0)
				{
					break;
				}
				if (loc.getBlockID(worldObj) == 0)
				{
					fillable++;
					drained += LiquidContainerRegistry.BUCKET_VOLUME;
					blocks--;
					if (doFill)
					{
						System.out.println("PlacedAt:Air: " + loc.toString());
						loc.setBlock(worldObj, blockID, meta);
						if (!this.updateQue.contains(loc))
						{
							this.updateQue.add(loc);
						}
					}
				}
			}
			if (fillable == 0)
			{
				this.yFillStart++;
			}
		}
		return drained;
	}

	@Override
	public boolean canPipeConnect(TileEntity entity, ForgeDirection dir)
	{
		return dir == this.getFacing();
	}

	@Override
	public void requestLiquid(TileEntityConstructionPump pump, LiquidStack stack)
	{
		this.requestMap.put(pump, stack);
	}

	@Override
	public void stopRequesting(TileEntity pump)
	{
		if (this.requestMap.containsKey(pump))
		{
			this.requestMap.remove(pump);
		}
	}

	public void addVectorToQue(Vector3 vector)
	{
		if (!this.targetSources.contains(vector))
		{
			this.targetSources.add(vector);
		}
	}

	/**
	 * Finds more liquid blocks using a path finder to be drained
	 */
	public void getNextFluidBlock()
	{
		/* FIND HIGHEST DRAIN POINT FIRST */
		PathfinderFindHighestSource path = new PathfinderFindHighestSource(this.worldObj, null);
		path.init(new Vector3(this.xCoord + this.getFacing().offsetX, this.yCoord + this.getFacing().offsetY, this.zCoord + this.getFacing().offsetZ));
		int y = path.highestY;

		/* FIND 10 UNMARKED SOURCES */
		PathfinderCheckerLiquid pathFinder = new PathfinderCheckerLiquid(this.worldObj, new Vector3(this), null);
		pathFinder.init(new Vector3(this.xCoord, y, this.zCoord));
		for (Vector3 loc : pathFinder.closedSet)
		{
			if (!this.targetSources.contains(loc))
			{
				this.targetSources.add(loc);
			}
		}
	}

	@Override
	public int fill(ForgeDirection from, LiquidStack resource, boolean doFill)
	{
		if (this.canDrainSources())
		{
			return 0;
		}
		return this.fill(0, resource, doFill);
	}

	@Override
	public int fill(int tankIndex, LiquidStack resource, boolean doFill)
	{
		if (resource == null || tankIndex != 0)
		{
			return 0;
		}
		return this.fillArea(resource, doFill);
	}

	@Override
	public LiquidStack drain(ForgeDirection from, int maxDrain, boolean doDrain)
	{
		if (from != this.getFacing().getOpposite())
		{
			return null;
		}
		return this.drain(0, maxDrain, doDrain);
	}

	@Override
	public LiquidStack drain(int tankIndex, int maxDrain, boolean doDrain)
	{
		return null;
	}

	@Override
	public ILiquidTank[] getTanks(ForgeDirection direction)
	{
		return null;
	}

	@Override
	public ILiquidTank getTank(ForgeDirection direction, LiquidStack type)
	{
		return null;
	}
}
