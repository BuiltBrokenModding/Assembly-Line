package fluidmech.common.pump;

import fluidmech.common.pump.path.LiquidPathFinder;
import hydraulic.api.IDrain;
import hydraulic.fluidnetwork.IFluidNetworkPart;
import hydraulic.helpers.FluidHelper;
import hydraulic.prefab.tile.TileEntityFluidDevice;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.liquids.ILiquid;
import net.minecraftforge.liquids.ILiquidTank;
import net.minecraftforge.liquids.ITankContainer;
import net.minecraftforge.liquids.LiquidContainerRegistry;
import net.minecraftforge.liquids.LiquidStack;
import universalelectricity.core.vector.Vector3;
import universalelectricity.core.vector.VectorHelper;

public class TileEntityDrain extends TileEntityFluidDevice implements ITankContainer, IDrain
{
	/* MAX BLOCKS DRAINED PER 1/2 SECOND */
	public static int MAX_WORLD_EDITS_PER_PROCESS = 30;
	private int currentWorldEdits = 0;

	/* LIST OF PUMPS AND THERE REQUESTS FOR THIS DRAIN */
	private HashMap<TileEntityConstructionPump, LiquidStack> requestMap = new HashMap<TileEntityConstructionPump, LiquidStack>();

	private List<Vector3> targetSources = new ArrayList<Vector3>();
	private List<Vector3> updateQue = new ArrayList<Vector3>();

	@Override
	public String getMeterReading(EntityPlayer user, ForgeDirection side)
	{
		return "Set to " + (canDrainSources() ? "input liquids" : "output liquids");
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
			if (meta > 5)
			{
				meta -= 6;
			}
		}
		return ForgeDirection.getOrientation(meta);
	}

	@Override
	public void updateEntity()
	{
		/* MAIN LOGIC PATH FOR DRAINING BODIES OF LIQUID */
		if (!this.worldObj.isRemote && this.ticks % 20 == 0)
		{
			this.currentWorldEdits = 0;
			this.doCleanup();

			if (this.canDrainSources() && this.requestMap.size() > 0)
			{
				/* ONLY FIND NEW SOURCES IF OUR CURRENT LIST RUNS DRY */
				if (this.targetSources.size() < this.MAX_WORLD_EDITS_PER_PROCESS + 10)
				{
					this.getNextFluidBlock();
				}
				for (Entry<TileEntityConstructionPump, LiquidStack> request : requestMap.entrySet())
				{
					if (this.currentWorldEdits >= MAX_WORLD_EDITS_PER_PROCESS)
					{
						break;
					}

					Iterator it = this.targetSources.iterator();
					while (it.hasNext())
					{
						Vector3 loc = (Vector3) it.next();
						if (this.currentWorldEdits >= MAX_WORLD_EDITS_PER_PROCESS)
						{
							break;
						}

						if (FluidHelper.isSourceBlock(this.worldObj, loc))
						{
							/* GET STACKS */
							LiquidStack stack = FluidHelper.getLiquidFromBlockId(loc.getBlockID(this.worldObj));
							LiquidStack requestStack = request.getValue();

							if (stack != null && requestStack != null && (requestStack.isLiquidEqual(stack) || requestStack.itemID == -1))
							{
								if (request.getKey().fill(0, stack, false) > 0)
								{

									/* EDIT REQUEST IN MAP */
									int requestAmmount = requestStack.amount - request.getKey().fill(0, stack, true);
									if (requestAmmount <= 0)
									{
										this.requestMap.remove(request);
									}
									else
									{
										request.setValue(FluidHelper.getStack(requestStack, requestAmmount));
									}

									/* ADD TO UPDATE QUE */
									if (!this.updateQue.contains(loc))
									{
										this.updateQue.add(loc);
									}

									/* REMOVE BLOCK */
									loc.setBlock(this.worldObj, 0, 0, 2);
									this.currentWorldEdits++;
									it.remove();

								}
							}
						}
					}
				}
			}
		}
	}

	/**
	 * Finds more liquid blocks using a path finder to be drained
	 */
	public void getNextFluidBlock()
	{
		LiquidPathFinder pathFinder = new LiquidPathFinder(this.worldObj, false, this.MAX_WORLD_EDITS_PER_PROCESS * 2);
		pathFinder.init(new Vector3(this.xCoord + this.getFacing().offsetX, this.yCoord + this.getFacing().offsetY, this.zCoord + this.getFacing().offsetZ));
		// System.out.println("Nodes:" + pathFinder.nodes.size() + "Results:" +
		// pathFinder.results.size());
		for (Vector3 vec : pathFinder.results)
		{
			this.addVectorToQue(vec);
		}
	}

	public void doCleanup()
	{
		/* CALL UPDATE ON EDITED BLOCKS */
		if (this.ticks % 100 == 0 && updateQue.size() > 0)
		{
			Iterator pp = this.updateQue.iterator();
			while (pp.hasNext())
			{
				Vector3 vec = (Vector3) pp.next();
				worldObj.notifyBlocksOfNeighborChange(vec.intX(), vec.intY(), vec.intZ(), vec.getBlockID(this.worldObj));
				pp.remove();
			}
		}
		/* CLEANUP REQUEST MAP AND REMOVE INVALID TILES */
		Iterator requests = this.requestMap.entrySet().iterator();
		TileEntity pipe = VectorHelper.getTileEntityFromSide(worldObj, new Vector3(this), this.getFacing().getOpposite());

		while (requests.hasNext())
		{
			Entry<TileEntityConstructionPump, LiquidStack> entry = (Entry<TileEntityConstructionPump, LiquidStack>) requests.next();
			TileEntity entity = entry.getKey();
			if (entity == null)
			{
				requests.remove();
			}
			else if (entity.isInvalid())
			{
				requests.remove();
			}
			else if (pipe instanceof IFluidNetworkPart && !((IFluidNetworkPart) pipe).getNetwork().isConnected(entry.getKey()))
			{
				requests.remove();
			}
		}
		/* CLEANUP TARGET LIST AND REMOVE INVALID SOURCES */
		Iterator mn = this.targetSources.iterator();
		while (mn.hasNext())
		{
			Vector3 vec = (Vector3) mn.next();
			if (!FluidHelper.isSourceBlock(this.worldObj, vec))
			{
				mn.remove();
			}
		}
	}

	@Override
	public int fillArea(LiquidStack resource, boolean doFill)
	{
		int drained = 0;

		if (!this.canDrainSources() && this.currentWorldEdits < MAX_WORLD_EDITS_PER_PROCESS)
		{
			/* ID LIQUID BLOCK AND SET VARS FOR BLOCK PLACEMENT */
			if (resource == null || resource.amount < LiquidContainerRegistry.BUCKET_VOLUME)
			{
				return 0;
			}

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

			/* FIND ALL VALID BLOCKS ON LEVEL OR BELLOW */
			LiquidPathFinder pathFinder = new LiquidPathFinder(this.worldObj, true, this.MAX_WORLD_EDITS_PER_PROCESS * 2);
			pathFinder.init(new Vector3(this.xCoord + this.getFacing().offsetX, this.yCoord + this.getFacing().offsetY, this.zCoord + this.getFacing().offsetZ));
			System.out.println("Nodes:" + pathFinder.nodes.size() + "Results:" + pathFinder.results.size());
			/* START FILLING IN OR CHECKING IF CAN FILL AREA */
			int fillable = 0;
			for (Vector3 loc : pathFinder.results)
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
						loc.setBlock(worldObj, blockID, meta);
						this.currentWorldEdits++;
						if (!this.updateQue.contains(loc))
						{
							this.updateQue.add(loc);
						}
					}
				}

			}

			for (Vector3 loc : pathFinder.results)
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
						loc.setBlock(worldObj, blockID, meta);
						this.currentWorldEdits++;
						if (!this.updateQue.contains(loc))
						{
							this.updateQue.add(loc);
						}
					}
				}
			}
		}
		return drained;
	}

	/**
	 * Sorter used by the fill method to maker sure its filling the lowest blocks closest to the
	 * drain first
	 */
	public static void SortListClosestToOrLowest(ArrayList<Vector3> list, Vector3 target)
	{
		if (list.size() > 1) // check if the number of orders is larger than 1
		{
			for (int x = 0; x < list.size(); x++) // bubble sort outer loop
			{
				Vector3 vec = list.get(x);
				double distance = Vector3.distance(vec, target);
				for (int i = 0; i < list.size(); i++)
				{
					Vector3 pos = list.get(x);
					if (Vector3.distance(pos, target) < distance)
					{
						list.set(x, pos);
						list.set(i, vec);
					}
				}
			}
		}

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
