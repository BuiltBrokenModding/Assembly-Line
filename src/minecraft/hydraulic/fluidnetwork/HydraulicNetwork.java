package hydraulic.fluidnetwork;

import hydraulic.api.ColorCode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.liquids.ILiquidTank;
import net.minecraftforge.liquids.ITankContainer;
import net.minecraftforge.liquids.LiquidContainerRegistry;
import net.minecraftforge.liquids.LiquidDictionary;
import net.minecraftforge.liquids.LiquidStack;
import net.minecraftforge.liquids.LiquidTank;
import universalelectricity.core.path.Pathfinder;
import universalelectricity.core.vector.Vector3;
import cpw.mods.fml.common.FMLLog;
import dark.library.helpers.ConnectionHelper;

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
		if (tileEntity != null && fluidPack.liquidStack != null)
		{
			if ((this.combinedStorage.getLiquid() == null || fluidPack.liquidStack.isLiquidEqual(this.combinedStorage.getLiquid())) && fluidPack.liquidStack.amount > 0)
			{
				this.pressureProducers.put(tileEntity, fluidPack);
			}
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
	public void removeSource(TileEntity tileEntity)
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
		this.removeLoad(ent);
		this.removeSource(ent);
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
		else if (ent instanceof IFluidNetworkPart)
		{
			this.addNetworkPart((IFluidNetworkPart) ent);
		}
		else if (!fluidTanks.contains(ent))
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
	 * Checks too see if the tileEntity is part of or connected too the network
	 */
	public boolean isConnected(TileEntity tileEntity)
	{
		if (this.fluidParts.contains(tileEntity))
		{
			return true;
		}
		else if (this.fluidTanks.contains(tileEntity))
		{
			return true;
		}
		else
		{
			return false;
		}
	}

	/**
	 * @param ignoreTiles The TileEntities to ignore during this calculation. Null will make it not
	 * ignore any.
	 * @return The electricity produced in this electricity network
	 */
	public double getPressureProduced(TileEntity... ignoreTiles)
	{
		// TODO pressure is not added as a sum but rather as a collective sum of the largest
		// pressures. IF the pressure is to small it will be ignored and stop producing pressure.
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
	 * Adds FLuid to this network from one of the connected Pipes
	 * 
	 * @param source - Were this liquid came from
	 * @param stack - LiquidStack to be sent
	 * @param doFill - actually fill the tank or just check numbers
	 * @return the amount of liquid consumed from the init stack
	 */
	public int addFluidToNetwork(TileEntity source, LiquidStack stack, boolean doFill)
	{
		return this.addFluidToNetwork(source, stack, doFill, false);
	}

	/**
	 * Adds FLuid to this network from one of the connected Pipes
	 * 
	 * @param source - Were this liquid came from
	 * @param stack - LiquidStack to be sent
	 * @param doFill - actually fill the tank or just check numbers
	 * @param allowStore - allows the network to store this liquid in the pipes
	 * @return the amount of liquid consumed from the init stack
	 */
	public int addFluidToNetwork(TileEntity source, LiquidStack stack, boolean doFill, boolean allowStore)
	{
		int used = 0;
		LiquidStack prevCombined = this.combinedStorage.getLiquid();
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
				if (tankContainer instanceof TileEntity && tankContainer != source && !(tankContainer instanceof IFluidNetworkPart))
				{
					TileEntity[] connectedTiles = ConnectionHelper.getSurroundingTileEntities((TileEntity) tankContainer);

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
				used = primaryFill.fill(fillDir, stack, doFill);
				//System.out.println("Primary Target " + used + doFill);
			}
			else if (secondayFill != null)
			{
				used = secondayFill.fill(fillDir, stack, doFill);
				//System.out.println("Seconday Target " + used + doFill);
			}
			else if (allowStore && (this.combinedStorage.getLiquid() == null || this.combinedStorage.getLiquid().amount < this.combinedStorage.getCapacity()))
			{
				used = this.combinedStorage.fill(stack, doFill);
				//System.out.println("Network Target filled for " + used + doFill);
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
				//System.out.println("Pulling " + (drainStack != null ? drainStack.amount : 0) + " from combined leaving " + (this.combinedStorage.getLiquid() != null ? this.combinedStorage.getLiquid().amount : 0));

			}
			if (prevCombined != null && this.combinedStorage.getLiquid() != null && prevCombined.amount != this.combinedStorage.getLiquid().amount)
			{
				this.moveAndSumVolume(false);
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
				capacity += LiquidContainerRegistry.BUCKET_VOLUME;
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
				this.moveAndSumVolume(false);
				network.moveAndSumVolume(false);
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
				newNetwork.moveAndSumVolume(true);
			}
		}
	}

	public void causingMixing(LiquidStack stack, LiquidStack stack2)
	{
		// TODO cause mixing of liquids based on types and volume. Also apply damage to pipes/parts
		// as needed
	}

	public void splitNetwork(World world, IFluidNetworkPart splitPoint)
	{
		if (splitPoint instanceof TileEntity)
		{
			this.getFluidNetworkParts().remove(splitPoint);
			this.moveAndSumVolume(false);
			/**
			 * Loop through the connected blocks and attempt to see if there are connections between
			 * the two points elsewhere.
			 */
			TileEntity[] connectedBlocks = splitPoint.getAdjacentConnections();

			for (int i = 0; i < connectedBlocks.length; i++)
			{
				TileEntity connectedBlockA = connectedBlocks[i];

				if (connectedBlockA instanceof IFluidNetworkPart)
				{
					for (int pipeCount = 0; pipeCount < connectedBlocks.length; pipeCount++)
					{
						final TileEntity connectedBlockB = connectedBlocks[pipeCount];

						if (connectedBlockA != connectedBlockB && connectedBlockB instanceof IFluidNetworkPart)
						{
							Pathfinder finder = new PathfinderCheckerPipes(world, (IFluidNetworkPart) connectedBlockB, splitPoint);
							finder.init(new Vector3(connectedBlockA));

							if (finder.results.size() > 0)
							{
								/* STILL CONNECTED SOMEWHERE ELSE */
								for (Vector3 node : finder.closedSet)
								{
									TileEntity entity = node.getTileEntity(world);
									if (entity instanceof IFluidNetworkPart)
									{
										if (node != splitPoint)
										{
											((IFluidNetworkPart) entity).setNetwork(this);
										}
									}
								}
							}
							else
							{
								/* NO LONGER CONNECTED ELSE WHERE SO SPLIT AND REFRESH */
								HydraulicNetwork newNetwork = new HydraulicNetwork(this.color);
								int parts = 0;
								for (Vector3 node : finder.closedSet)
								{
									TileEntity entity = node.getTileEntity(world);
									if (entity instanceof IFluidNetworkPart)
									{
										if (node != splitPoint)
										{
											newNetwork.getFluidNetworkParts().add((IFluidNetworkPart) entity);
											parts++;
										}
									}
								}

								newNetwork.cleanUpConductors();
								newNetwork.moveAndSumVolume(true);
							}
						}
					}
				}
			}
		}
	}

	/**
	 * Moves the volume stored in the network to the parts or sums up the volume from the parts and
	 * loads it to the network. Assumes that all liquidStacks stored are equal
	 * 
	 * @param load - loads the volume from the parts before leveling out the volumes
	 */
	public void moveAndSumVolume(boolean load)
	{
		int volume = 0;
		int itemID = 0;
		int itemMeta = 0;
		if (load)
		{
			for (IFluidNetworkPart part : this.fluidParts)
			{

				if (part.getTank() != null && part.getTank().getLiquid() != null)
				{
					if (itemID == 0)
					{
						itemID = part.getTank().getLiquid().itemID;
						itemMeta = part.getTank().getLiquid().itemMeta;
					}
					volume += part.getTank().getLiquid().amount;
				}
			}
			this.combinedStorage.setLiquid(new LiquidStack(itemID, volume, itemMeta));
		}

		if (this.combinedStorage.getLiquid() != null && this.fluidParts.size() > 0)
		{
			volume = this.combinedStorage.getLiquid().amount / this.fluidParts.size();
			itemID = this.combinedStorage.getLiquid().itemID;
			itemMeta = this.combinedStorage.getLiquid().itemMeta;

			for (IFluidNetworkPart part : this.fluidParts)
			{
				part.setTankContent(null);
				part.setTankContent(new LiquidStack(itemID, volume, itemMeta));
			}
		}
	}

	@Override
	public String toString()
	{
		return "HydraulicNetwork[" + this.hashCode() + "|parts:" + this.fluidParts.size() + "]";
	}

	public String getStorageFluid()
	{
		if (!combinedStorage.containsValidLiquid())
		{
			return "Zero";
		}
		return String.format("%d/%d %S Stored", combinedStorage.getLiquid().amount / LiquidContainerRegistry.BUCKET_VOLUME, combinedStorage.getCapacity() / LiquidContainerRegistry.BUCKET_VOLUME, LiquidDictionary.findLiquidName(this.combinedStorage.getLiquid()));
	}

	public ILiquidTank getTank()
	{
		if (this.combinedStorage == null)
		{
			this.combinedStorage = new LiquidTank(0);
		}
		return this.combinedStorage;
	}
}
