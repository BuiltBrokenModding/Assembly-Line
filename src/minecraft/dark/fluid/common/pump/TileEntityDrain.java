package dark.fluid.common.pump;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;
import universalelectricity.core.vector.Vector2;
import universalelectricity.core.vector.Vector3;
import universalelectricity.core.vector.VectorHelper;
import dark.api.fluid.IDrain;
import dark.api.fluid.INetworkPipe;
import dark.core.helpers.FluidHelper;
import dark.fluid.common.prefab.TileEntityFluidDevice;

public class TileEntityDrain extends TileEntityFluidDevice implements IFluidHandler, IDrain
{
	/* MAX BLOCKS DRAINED PER 1/2 SECOND */
	public static int MAX_WORLD_EDITS_PER_PROCESS = 30;
	private int currentWorldEdits = 0;

	/* LIST OF PUMPS AND THERE REQUESTS FOR THIS DRAIN */
	private HashMap<TileEntity, FluidStack> requestMap = new HashMap<TileEntity, FluidStack>();

	private List<Vector3> targetSources = new ArrayList<Vector3>();
	private List<Vector3> updateQue = new ArrayList<Vector3>();
	private LiquidPathFinder pathLiquid;

	public LiquidPathFinder getLiquidFinder()
	{
		if (pathLiquid == null)
		{
			pathLiquid = new LiquidPathFinder(this.worldObj, 1000, 100);
		}
		return pathLiquid;
	}

	@Override
	public String getMeterReading(EntityPlayer user, ForgeDirection side, EnumTools tool)
	{
		return "Set to " + (canDrainSources() ? "input liquids" : "output liquids");
	}

	@Override
	public void invalidate()
	{
		super.invalidate();
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
		super.updateEntity();
		/* MAIN LOGIC PATH FOR DRAINING BODIES OF LIQUID */
		if (!this.worldObj.isRemote && this.ticks % 30 == 0)
		{
			this.currentWorldEdits = 0;
			this.doCleanup();

			if (this.canDrainSources() && this.requestMap.size() > 0)
			{
				/* ONLY FIND NEW SOURCES IF OUR CURRENT LIST RUNS DRY */
				if (this.targetSources.size() < TileEntityDrain.MAX_WORLD_EDITS_PER_PROCESS + 10)
				{
					this.getNextFluidBlock();
				}
				for (Entry<TileEntity, FluidStack> request : requestMap.entrySet())
				{
					if (this.currentWorldEdits >= MAX_WORLD_EDITS_PER_PROCESS)
					{
						break;
					}

					if (request.getKey() instanceof IFluidHandler)
					{
						IFluidHandler tank = (IFluidHandler) request.getKey();

						Vector3[] sortedList = this.sortedDrainList();

						for (int i = 0; sortedList != null && i < sortedList.length; i++)
						{
							Vector3 loc = sortedList[i];

							if (this.currentWorldEdits >= MAX_WORLD_EDITS_PER_PROCESS)
							{
								break;
							}
							FluidStack stack = FluidHelper.drainBlock(this.worldObj, loc, false);
							if (stack != null)
							{
								/* GET STACKS */

								FluidStack requestStack = request.getValue();

								if (stack != null && requestStack != null && (requestStack.isFluidEqual(stack) || requestStack.getFluid().getBlockID() == -111))
								{
									if (tank.fill(ForgeDirection.UNKNOWN, stack, false) > FluidContainerRegistry.BUCKET_VOLUME)
									{

										/* EDIT REQUEST IN MAP */
										int requestAmmount = requestStack.amount - tank.fill(ForgeDirection.UNKNOWN, stack, true);
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
										FluidHelper.drainBlock(this.worldObj, loc, true);
										this.currentWorldEdits++;
									}
								}
							}
						}
					}
				}
			}
		}
	}

	/** Finds more liquid blocks using a path finder to be drained */
	public void getNextFluidBlock()
	{

		getLiquidFinder().reset();
		getLiquidFinder().init(new Vector3(this.xCoord + this.getFacing().offsetX, this.yCoord + this.getFacing().offsetY, this.zCoord + this.getFacing().offsetZ), false);
		// System.out.println("Nodes:" + pathFinder.nodes.size() + "Results:" +
		// pathFinder.results.size());
		for (Vector3 vec : getLiquidFinder().nodes)
		{
			this.addVectorToQue(vec);
		}
	}

	@SuppressWarnings("unchecked")
	public void doCleanup()
	{
		/* CALL UPDATE ON EDITED BLOCKS */
		if (this.ticks % 100 == 0 && updateQue.size() > 0)
		{
			Iterator<Vector3> pp = this.updateQue.iterator();
			int up = 0;
			while (pp.hasNext() && up < TileEntityDrain.MAX_WORLD_EDITS_PER_PROCESS)
			{
				Vector3 vec = pp.next();
				worldObj.notifyBlockChange(vec.intX(), vec.intY(), vec.intZ(), vec.getBlockID(this.worldObj));
				worldObj.notifyBlockOfNeighborChange(vec.intX(), vec.intY(), vec.intZ(), vec.getBlockID(this.worldObj));
				pp.remove();
				up++;
			}
		}
		/* CLEANUP REQUEST MAP AND REMOVE INVALID TILES */
		Iterator requests = this.requestMap.entrySet().iterator();
		TileEntity pipe = VectorHelper.getTileEntityFromSide(worldObj, new Vector3(this), this.getFacing().getOpposite());

		while (requests.hasNext())
		{
			Entry<TileEntityConstructionPump, FluidStack> entry = (Entry<TileEntityConstructionPump, FluidStack>) requests.next();
			TileEntity entity = entry.getKey();
			if (entity == null)
			{
				requests.remove();
			}
			else if (entity.isInvalid())
			{
				requests.remove();
			}
			else if (pipe instanceof INetworkPipe && !((INetworkPipe) pipe).getTileNetwork().isPartOfNetwork(entry.getKey()))
			{
				requests.remove();
			}
		}

	}

	public Vector3[] sortedDrainList()
	{
		try
		{
			/* CLEANUP TARGET LIST AND REMOVE INVALID SOURCES */
			Iterator<Vector3> targetIt = this.targetSources.iterator();
			while (targetIt.hasNext())
			{
				Vector3 vec = targetIt.next();
				if (FluidHelper.drainBlock(this.worldObj, vec, false) == null)
				{
					targetIt.remove();
				}
			}

			Vector3[] sortedList = new Vector3[this.targetSources.size()];
			for (int b = 0; b < this.targetSources.size(); b++)
			{
				sortedList[b] = this.targetSources.get(b);
			}

			/* SORT RESULTS TO PUT THE HiGHEST AND FURTHEST AT THE TOP */
			Vector2 machine = new Vector3(this).toVector2();
			for (int i = 0; i < sortedList.length; i++)
			{
				Vector3 vec = sortedList[i].clone();
				Vector2 first = vec.toVector2();
				if (i + 1 < sortedList.length)
				{
					Vector3 highest = vec;
					int b = 0;
					for (b = i + 1; b < sortedList.length; b++)
					{
						Vector3 checkVec = sortedList[b].clone();
						if (checkVec != null)
						{
							Vector2 second = checkVec.toVector2();

							if (second.distanceTo(machine) > vec.toVector2().distanceTo(machine))
							{
								highest = checkVec.clone();
							}
						}
					}
					if (b < sortedList.length)
					{
						sortedList[i] = vec;
						sortedList[b] = highest;
					}
				}
			}
			for (int i = 0; i < sortedList.length; i++)
			{
				Vector3 vec = sortedList[i].clone();
				if (i + 1 < sortedList.length)
				{
					Vector3 highest = vec;
					int b = 0;
					for (b = i + 1; b < sortedList.length; b++)
					{
						Vector3 checkVec = sortedList[b].clone();
						if (checkVec != null)
						{
							if (checkVec.intY() > highest.intY())
							{
								highest = checkVec.clone();

							}
						}
					}
					if (b < sortedList.length)
					{
						sortedList[i] = vec;
						sortedList[b] = highest;
					}
				}
			}
			return sortedList;
		}
		catch (Exception e)
		{
			System.out.println("FluidMech: Critical Error Processing Drain List");
			e.printStackTrace();
		}

		return null;
	}

	@Override
	public int fillArea(FluidStack resource, boolean doFill)
	{
		int drained = 0;

		if (!this.canDrainSources() && this.currentWorldEdits < MAX_WORLD_EDITS_PER_PROCESS)
		{
			/* ID LIQUID BLOCK AND SET VARS FOR BLOCK PLACEMENT */
			if (resource == null || resource.amount < FluidContainerRegistry.BUCKET_VOLUME)
			{
				return 0;
			}

			int blockID = resource.getFluid().getBlockID();
			int blocks = (resource.amount / FluidContainerRegistry.BUCKET_VOLUME);

			/* FIND ALL VALID BLOCKS ON LEVEL OR BELLOW */
			final Vector3 faceVec = new Vector3(this.xCoord + this.getFacing().offsetX, this.yCoord + this.getFacing().offsetY, this.zCoord + this.getFacing().offsetZ);
			getLiquidFinder().init(faceVec, true);
			//System.out.println("Drain:FillArea: Targets -> " + getLiquidFinder().results.size());

			/* SORT RESULTS TO PUT THE LOWEST AND CLOSEST AT THE TOP */
			try
			{
				if (getLiquidFinder().results.size() > 1)
				{
					Collections.sort(getLiquidFinder().results, new Comparator()
					{
						@Override
						public int compare(Object o1, Object o2)
						{
							if (o1 == o2)
							{
								return 0;
							}
							Vector3 a = (Vector3) o1;
							Vector3 b = (Vector3) o2;
							double da = Vector3.distance(a, faceVec);
							double db = Vector3.distance(b, faceVec);
							;
							if (a.equals(b))
							{
								return 0;
							}
							if (Integer.compare(a.intY(), b.intY()) != 0)
							{
								return Integer.compare(a.intY(), b.intY());
							}
							return Double.compare(da, db);
						}
					});
				}
			}
			catch (Exception e)
			{
				System.out.println("FluidMech: Error sorting fill collection");
				e.printStackTrace();
			}
			for (Vector3 loc : getLiquidFinder().results)
			{
				if (blocks <= 0)
				{
					break;
				}
				Fluid stack = FluidHelper.getFluidFromBlockID(loc.getBlockID(worldObj));
				if (stack != null && stack.getBlockID() == blockID && loc.getBlockMetadata(worldObj) != 0)
				{
					drained += FluidContainerRegistry.BUCKET_VOLUME;
					blocks--;
					if (doFill)
					{
						loc.setBlock(worldObj, blockID, 0);
						this.currentWorldEdits++;
						if (!this.updateQue.contains(loc))
						{
							this.updateQue.add(loc);
						}
					}
				}

			}

			for (Vector3 loc : getLiquidFinder().results)
			{
				if (blocks <= 0)
				{
					break;
				}
				if (loc.getBlockID(worldObj) == 0)
				{
					drained += FluidContainerRegistry.BUCKET_VOLUME;
					blocks--;
					if (doFill)
					{
						loc.setBlock(worldObj, blockID, 0);
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

	@Override
	public boolean canTileConnect(TileEntity entity, ForgeDirection dir)
	{
		return dir == this.getFacing();
	}

	@Override
	public void requestLiquid(TileEntity pump, Fluid fluid, int amount)
	{
		this.requestMap.put(pump, new FluidStack(-111, amount));
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
	public int fill(ForgeDirection from, FluidStack resource, boolean doFill)
	{
		if (this.canDrainSources() || resource == null)
		{
			return 0;
		}
		return this.fillArea(resource, doFill);
	}

	@Override
	public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain)
	{
		return null;
	}

	@Override
	public boolean canFill(ForgeDirection from, Fluid fluid)
	{
		return this.getFacing() == from;
	}

	@Override
	public boolean canDrain(ForgeDirection from, Fluid fluid)
	{
		return false;
	}

	@Override
	public FluidTankInfo[] getTankInfo(ForgeDirection from)
	{
		return null;
	}
}
