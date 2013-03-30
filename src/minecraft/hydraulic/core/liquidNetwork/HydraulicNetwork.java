package hydraulic.core.liquidNetwork;

import hydraulic.api.ColorCode;
import hydraulic.api.IFluidNetworkPart;
import hydraulic.core.path.PathfinderCheckerPipes;
import hydraulic.helpers.connectionHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.liquids.ILiquidTank;
import net.minecraftforge.liquids.ITankContainer;
import net.minecraftforge.liquids.LiquidContainerRegistry;
import net.minecraftforge.liquids.LiquidStack;
import net.minecraftforge.liquids.LiquidTank;
import universalelectricity.core.block.IConnectionProvider;
import universalelectricity.core.path.Pathfinder;
import universalelectricity.core.path.PathfinderChecker;
import cpw.mods.fml.common.FMLLog;

/**
 * Side note: the network should act like this when done {@link http
 * ://www.e4training.com/hydraulic_calculators/B1.htm} as well as stay compatible with the forge
 * Liquids
 * 
 * @author Rseifert
 * 
 */
public class HydraulicNetwork
{
	/* BLOCK THAT ACT AS FLUID CONVEYORS ** */
	public final List<IFluidNetworkPart> fluidParts = new ArrayList<IFluidNetworkPart>();
	/* MACHINES THAT USE THE FORGE LIQUID API TO RECEIVE LIQUID ** */
	public final List<ITankContainer> fluidTanks = new ArrayList<ITankContainer>();
	/* MACHINES THAT USE THE PRESSURE SYSTEM TO DO WORK ** */
	private final HashMap<TileEntity, FluidPressurePack> pressureProducers = new HashMap<TileEntity, FluidPressurePack>();
	private final HashMap<TileEntity, FluidPressurePack> pressureLoads = new HashMap<TileEntity, FluidPressurePack>();

	public ColorCode color = ColorCode.NONE;
	/* PRESSURE OF THE NETWORK AS A TOTAL. ZERO AS IN NO PRODUCTION */
	public double pressureProduced = 0;
	/* PRESSURE OF THE NETWORK'S LOAD AS A TOTAL. ZERO AS IN NO LOAD */
	public double pressureLoad = 0;
	/* IS IT PROCESSING AN ADD LIQUID EVENT */
	private boolean processingRequest = false;
	/* COMBINED TEMP STORAGE FOR ALL PIPES IN THE NETWORK */
	private LiquidTank combinedStorage = new LiquidTank(LiquidContainerRegistry.BUCKET_VOLUME);

	public HydraulicNetwork(ColorCode color, IFluidNetworkPart... parts)
	{
		this.fluidParts.addAll(Arrays.asList(parts));
		this.color = color;
	}

	/**
	 * sets this tileEntity to produce a pressure and flow rate in the network
	 */
	public void startProducingPressure(TileEntity tileEntity, FluidPressurePack fluidPack)
	{
		if (tileEntity != null && fluidPack.liquidStack != null && fluidPack.liquidStack.amount > 0)
		{
			this.pressureProducers.put(tileEntity, fluidPack);
		}
	}

	/**
	 * sets this tileEntity to produce a pressure and flow rate in the network
	 */
	public void startProducingPressure(TileEntity tileEntity, LiquidStack stack, double pressure)
	{
		this.startProducingPressure(tileEntity, new FluidPressurePack(stack, pressure));
	}

	/**
	 * is this tile entity producing a pressure
	 */
	public boolean isProducingPressure(TileEntity tileEntity)
	{
		return this.pressureProducers.containsKey(tileEntity);
	}

	/**
	 * Sets this tile entity to stop producing pressure and flow in this network
	 */
	public void stopProducing(TileEntity tileEntity)
	{
		this.pressureProducers.remove(tileEntity);
	}

	/**
	 * Sets this tile entity to act as a load on the system
	 */
	public void addLoad(TileEntity tileEntity, FluidPressurePack fluidPack)
	{
		if (tileEntity != null && fluidPack.liquidStack != null && fluidPack.liquidStack.amount > 0)
		{
			this.pressureLoads.put(tileEntity, fluidPack);
		}
	}

	/**
	 * Sets this tile entity to act as a load on the system
	 */
	public void addLoad(TileEntity tileEntity, LiquidStack stack, double pressure)
	{
		this.addLoad(tileEntity, new FluidPressurePack(stack, pressure));
	}

	/**
	 * is this tileEntity a load in the network
	 */
	public boolean isLoad(TileEntity tileEntity)
	{
		return this.pressureLoads.containsKey(tileEntity);
	}

	/**
	 * removes this tileEntity from being a load on the network
	 */
	public void removeLoad(TileEntity tileEntity)
	{
		this.pressureLoads.remove(tileEntity);
	}

	/**
	 * Removes a tileEntity from any of the valid lists
	 */
	public void removeEntity(TileEntity ent)
	{
		if (fluidTanks.contains(ent))
		{
			fluidTanks.remove(ent);
		}
	}

	/**
	 * Adds a tileEntity to the list if its valid
	 */
	public void addEntity(ITankContainer ent)
	{
		if (ent == null)
		{
			return;
		}
		if (ent instanceof IFluidNetworkPart)
		{
			this.addNetworkPart((IFluidNetworkPart) ent);
		}
		if (!fluidTanks.contains(ent))
		{
			fluidTanks.add(ent);
		}
	}

	public void addNetworkPart(IFluidNetworkPart newConductor)
	{
		this.cleanConductors();

		if (newConductor.getColor() == this.color && !fluidParts.contains(newConductor))
		{
			fluidParts.add(newConductor);
			newConductor.setNetwork(this);
		}
	}

	/**
	 * @param ignoreTiles The TileEntities to ignore during this calculation. Null will make it not
	 * ignore any.
	 * @return The electricity produced in this electricity network
	 */
	public double getPressureProduced(TileEntity... ignoreTiles)
	{
		int totalPressure = 0;

		Iterator it = this.pressureProducers.entrySet().iterator();

		loop:
		while (it.hasNext())
		{
			Map.Entry pairs = (Map.Entry) it.next();

			if (pairs != null)
			{
				TileEntity tileEntity = (TileEntity) pairs.getKey();

				if (tileEntity == null)
				{
					it.remove();
					continue;
				}

				if (tileEntity.isInvalid())
				{
					it.remove();
					continue;
				}

				if (tileEntity.worldObj.getBlockTileEntity(tileEntity.xCoord, tileEntity.yCoord, tileEntity.zCoord) != tileEntity)
				{
					it.remove();
					continue;
				}

				if (ignoreTiles != null)
				{
					for (TileEntity ignoreTile : ignoreTiles)
					{
						if (tileEntity == ignoreTile)
						{
							continue loop;
						}
					}
				}

				FluidPressurePack pack = (FluidPressurePack) pairs.getValue();

				if (pairs.getKey() != null && pairs.getValue() != null && pack != null)
				{
					totalPressure += pack.pressure;
				}
			}
		}

		return totalPressure;
	}

	/**
	 * Tries to add the liquid stack to the network's valid machines. Same as the fill method for
	 * ITankContainer in that it will fill machines, however it also includes pressure if the
	 * machine also adds pressure to the network. Called mostly by pipes as they are filled from
	 * other mod sources
	 * 
	 * @return The amount of Liquid used.
	 */
	public int addFluidToNetwork(TileEntity source, LiquidStack stack, double pressure, boolean doFill)
	{
		int used = 0;

		if (!this.processingRequest && stack != null && color.isValidLiquid(stack))
		{
			if (this.combinedStorage.getLiquid() != null && !stack.isLiquidEqual(this.combinedStorage.getLiquid()))
			{
				// TODO cause mixing
			}
			if (stack.amount > this.getMaxFlow(stack))
			{
				stack = new LiquidStack(stack.itemID, this.getMaxFlow(stack), stack.itemMeta);
			}

			/* Main fill target to try to fill with the stack */
			ITankContainer primaryFill = null;
			int volume = Integer.MAX_VALUE;
			ForgeDirection fillDir = ForgeDirection.UNKNOWN;

			/* Secondary fill target if the main target is not found */
			ITankContainer secondayFill = null;
			int mostFill = 0;
			ForgeDirection otherFillDir = ForgeDirection.UNKNOWN;

			boolean found = false;

			/* FIND THE FILL TARGET FROM THE LIST OF FLUID RECIEVERS */
			for (ITankContainer tankContainer : fluidTanks)
			{
				if (tankContainer instanceof TileEntity && tankContainer != source)
				{
					TileEntity[] connectedTiles = connectionHelper.getSurroundingTileEntities((TileEntity) tankContainer);

					for (int i = 0; i < 6; i++)
					{
						if (connectedTiles[i] instanceof IFluidNetworkPart && ((IFluidNetworkPart) connectedTiles[i]).getNetwork() == this)
						{
							ForgeDirection dir = ForgeDirection.getOrientation(i).getOpposite();
							ILiquidTank targetTank = tankContainer.getTank(dir, stack);
							int fill = tankContainer.fill(dir, stack, false);

							/* USE GET TANK FROM SIDE METHOD FIRST */
							if (targetTank != null)
							{
								LiquidStack stackStored = targetTank.getLiquid();
								if (stackStored == null)
								{
									primaryFill = tankContainer;
									found = true;
									fillDir = dir;
									break;
								}
								else if (stackStored.amount < targetTank.getCapacity() && stackStored.amount < volume)
								{
									primaryFill = tankContainer;
									volume = stackStored.amount;
								}
							}/* USE FILL METHOD IF GET TANK == NULL */
							else if (fill > 0 && fill > mostFill)
							{
								secondayFill = tankContainer;
								mostFill = fill;
								otherFillDir = dir;
							}
						}
					}
				}
				if (found)
				{
					break;
				}
			}// End of tank finder
			boolean filledMain = false;
			if (primaryFill != null)
			{
				System.out.println("Primary Target");
				used = primaryFill.fill(fillDir, stack, doFill);
			}
			else if (secondayFill != null)
			{
				System.out.println("Seconday Target");
				used = secondayFill.fill(fillDir, stack, doFill);
			}
			else if (this.combinedStorage.getLiquid() == null || this.combinedStorage.getLiquid().amount < this.combinedStorage.getCapacity())
			{
				used = this.combinedStorage.fill(stack, doFill);
				System.out.println("Network Target filled for " + used);
				filledMain = true;
			}
			/* IF THE COMBINED STORAGE OF THE PIPES HAS LIQUID MOVE IT FIRST */
			if (!filledMain && used > 0 && this.combinedStorage.getLiquid() != null && this.combinedStorage.getLiquid().amount > 0)
			{

				LiquidStack drainStack = new LiquidStack(0, 0, 0);
				if (this.combinedStorage.getLiquid().amount >= used)
				{
					drainStack = this.combinedStorage.drain(used, doFill);
					used = 0;
				}
				else
				{
					int pUsed = used;
					used = Math.min(used, Math.max(used - this.combinedStorage.getLiquid().amount, 0));
					drainStack = this.combinedStorage.drain(pUsed - used, doFill);
				}
				System.out.println("Pulling " + stack.amount + " from combined");
			}
		}
		this.processingRequest = false;
		return used;
	}

	/**
	 * gets the flow rate of the network by getting the pipe with the lowest flow rate.
	 * 
	 * @return units of liquid per tick, default 20B/s
	 */
	public int getMaxFlow(LiquidStack stack)
	{
		int flow = 1000;
		for (IFluidNetworkPart conductor : this.fluidParts)
		{
			// TODO change the direction to actual look for connected only directions and pipes
			// along
			// the path to the target
			int cFlow = conductor.getMaxFlowRate(stack, ForgeDirection.UNKNOWN);
			if (cFlow < flow)
			{
				flow = cFlow;
			}
		}
		return flow;
	}

	public void cleanConductors()
	{
		for (int i = 0; i < fluidParts.size(); i++)
		{
			if (fluidParts.get(i) == null)
			{
				fluidParts.remove(i);
			}
			else if (((TileEntity) fluidParts.get(i)).isInvalid())
			{
				fluidParts.remove(i);
			}
		}
	}

	public void setNetwork()
	{
		this.cleanConductors();

		for (IFluidNetworkPart conductor : this.fluidParts)
		{
			conductor.setNetwork(this);
		}
	}

	public void onPresureChange()
	{
		this.cleanConductors();

		for (int i = 0; i < fluidParts.size(); i++)
		{
			// TODO change to actual check connected sides only && get true value from settings file
			IFluidNetworkPart part = fluidParts.get(i);
			if (part.getMaxPressure(ForgeDirection.UNKNOWN) < this.pressureProduced && part.onOverPressure(true))
			{
				this.fluidParts.remove(part);
				this.cleanConductors();
			}

		}
	}

	public void cleanUpConductors()
	{
		Iterator it = this.fluidParts.iterator();
		int capacity = 0;

		while (it.hasNext())
		{
			IFluidNetworkPart conductor = (IFluidNetworkPart) it.next();

			if (conductor == null)
			{
				it.remove();
			}
			else if (((TileEntity) conductor).isInvalid())
			{
				it.remove();
			}
			else if (conductor.getColor() != this.color)
			{
				it.remove();
			}
			else
			{
				conductor.setNetwork(this);
				capacity += conductor.getTankSize();
			}
		}
		this.combinedStorage.setCapacity(capacity);
	}

	/**
	 * This function is called to refresh all conductors in this network
	 */
	public void refreshConductors()
	{
		this.cleanUpConductors();

		try
		{
			Iterator<IFluidNetworkPart> it = this.fluidParts.iterator();

			while (it.hasNext())
			{
				IFluidNetworkPart conductor = it.next();
				conductor.updateAdjacentConnections();
			}
		}
		catch (Exception e)
		{
			FMLLog.severe("Universal Electricity: Failed to refresh conductor.");
			e.printStackTrace();
		}
	}

	public List<IFluidNetworkPart> getFluidNetworkParts()
	{
		return this.fluidParts;
	}

	public void mergeNetworks(HydraulicNetwork network)
	{
		if (network != null && network != this && network.color == this.color)
		{
			if (this.combinedStorage.getLiquid() != null && network.combinedStorage.getLiquid() != null && !this.combinedStorage.getLiquid().isLiquidEqual(network.combinedStorage.getLiquid()))
			{
				this.causingMixing(this.combinedStorage.getLiquid(), network.combinedStorage.getLiquid());
			}
			else
			{
				LiquidStack stack = new LiquidStack(0, 0, 0);
				if (this.combinedStorage.getLiquid() != null && network.combinedStorage.getLiquid() != null && this.combinedStorage.getLiquid().isLiquidEqual(network.combinedStorage.getLiquid()))
				{
					stack = this.combinedStorage.getLiquid();
					stack.amount += network.combinedStorage.getLiquid().amount;
				}
				else if (this.combinedStorage.getLiquid() == null && network.combinedStorage.getLiquid() != null)
				{
					stack = network.combinedStorage.getLiquid();
				}
				else if (this.combinedStorage.getLiquid() != null && network.combinedStorage.getLiquid() == null)
				{
					stack = this.combinedStorage.getLiquid();
				}
				HydraulicNetwork newNetwork = new HydraulicNetwork(this.color);

				newNetwork.getFluidNetworkParts().addAll(this.getFluidNetworkParts());
				newNetwork.getFluidNetworkParts().addAll(network.getFluidNetworkParts());

				newNetwork.cleanUpConductors();
				newNetwork.combinedStorage.setLiquid(stack);
			}
		}
	}

	public void causingMixing(LiquidStack stack, LiquidStack stack2)
	{
		// TODO cause mixing of liquids based on types and volume. Also apply damage to pipes/parts
		// as needed
	}

	public void splitNetwork(IConnectionProvider splitPoint)
	{
		if (splitPoint instanceof TileEntity)
		{
			this.getFluidNetworkParts().remove(splitPoint);

			/**
			 * Loop through the connected blocks and attempt to see if there are connections between
			 * the two points elsewhere.
			 */
			TileEntity[] connectedBlocks = splitPoint.getAdjacentConnections();

			for (int i = 0; i < connectedBlocks.length; i++)
			{
				TileEntity connectedBlockA = connectedBlocks[i];

				if (connectedBlockA instanceof IConnectionProvider)
				{
					for (int pipeCount = 0; pipeCount < connectedBlocks.length; pipeCount++)
					{
						final TileEntity connectedBlockB = connectedBlocks[pipeCount];

						if (connectedBlockA != connectedBlockB && connectedBlockB instanceof IConnectionProvider)
						{
							Pathfinder finder = new PathfinderCheckerPipes((IConnectionProvider) connectedBlockB, splitPoint);
							finder.init((IConnectionProvider) connectedBlockA);

							if (finder.results.size() > 0)
							{
								/* STILL CONNECTED SOMEWHERE ELSE */
								for (IConnectionProvider node : finder.iteratedNodes)
								{
									if (node instanceof IFluidNetworkPart)
									{
										if (node != splitPoint)
										{
											((IFluidNetworkPart) node).setNetwork(this);
										}
									}
								}
							}
							else
							{
								/* NO LONGER CONNECTED ELSE WHERE SO SPLIT AND REFRESH */
								HydraulicNetwork newNetwork = new HydraulicNetwork(this.color);
								int parts = 0;
								for (IConnectionProvider node : finder.iteratedNodes)
								{
									if (node instanceof IFluidNetworkPart)
									{
										if (node != splitPoint)
										{
											newNetwork.getFluidNetworkParts().add((IFluidNetworkPart) node);
											parts++;
										}
									}
								}

								newNetwork.cleanUpConductors();

								LiquidStack stack = this.combinedStorage.getLiquid();
								if (stack != null)
								{
									newNetwork.combinedStorage.setLiquid(new LiquidStack(stack.itemID, parts * this.getVolumePerPart(), stack.itemMeta));
								}
							}
						}
					}
				}
			}
		}
	}

	/**
	 * gets the amount of liquid stored in each part in the system
	 */
	public int getVolumePerPart()
	{
		int volumePerPart = 0;
		int cap = 0;
		LiquidStack stack = this.combinedStorage.getLiquid();
		if (stack != null)
		{
			for (IFluidNetworkPart par : this.fluidParts)
			{
				cap += par.getTankSize();
			}
			volumePerPart = this.combinedStorage.getLiquid().amount / cap;
		}
		return volumePerPart;
	}

	/**
	 * Drain a set volume from the system
	 */
	public LiquidStack drainVolumeFromSystem(int volume, boolean doDrain)
	{
		LiquidStack stack = null;
		if (this.combinedStorage.getLiquid() != null)
		{
			stack = this.combinedStorage.drain(this.getVolumePerPart(), doDrain);
		}
		return stack;
	}

	@Override
	public String toString()
	{
		return "HydraulicNetwork[" + this.hashCode() + "|parts:" + this.fluidParts.size() + "]";
	}

	public String getStorageFluid()
	{
		if (combinedStorage.getLiquid() == null)
		{
			return "Zero";
		}
		return String.format("%d/%d %S Stored", combinedStorage.getLiquid().amount / LiquidContainerRegistry.BUCKET_VOLUME, combinedStorage.getCapacity() / LiquidContainerRegistry.BUCKET_VOLUME, LiquidHandler.getName(this.combinedStorage.getLiquid()));
	}
}
