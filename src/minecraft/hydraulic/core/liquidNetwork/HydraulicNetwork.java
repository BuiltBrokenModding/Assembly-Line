package hydraulic.core.liquidNetwork;

import hydraulic.api.ColorCode;
import hydraulic.api.IFluidNetworkPart;
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
import net.minecraftforge.liquids.LiquidStack;
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
	public int addFluidToNetwork(LiquidStack stack, double pressure, boolean doFill)
	{
		int used = 0;
		if (!this.processingRequest && stack != null && canAcceptLiquid(stack))
		{
			if (stack.amount > this.getMaxFlow(stack))
			{
				stack = new LiquidStack(stack.itemID, this.getMaxFlow(stack), stack.itemMeta);
			}
			/* Main fill target to try to fill with the stack */
			ITankContainer fillTarget = null;
			int volume = Integer.MAX_VALUE;
			ForgeDirection fillDir = ForgeDirection.UNKNOWN;

			/* Secondary fill target if the main target is not found */
			ITankContainer otherFillTarget = null;
			int mostFill = 0;
			ForgeDirection otherFillDir = ForgeDirection.UNKNOWN;

			boolean found = false;

			for (ITankContainer tankContainer : fluidTanks)
			{
				if (tankContainer instanceof TileEntity)
				{
					TileEntity[] connectedTiles = connectionHelper.getSurroundingTileEntities((TileEntity) tankContainer);

					for (int i = 0; i < 6; i++)
					{
						if (connectedTiles[i] instanceof IFluidNetworkPart && ((IFluidNetworkPart) connectedTiles[i]).getNetwork() == this)
						{
							ForgeDirection dir = ForgeDirection.getOrientation(i).getOpposite();
							ILiquidTank storage = tankContainer.getTank(dir, stack);
							int fill = tankContainer.fill(dir, stack, false);
							/*
							 * if the TileEntity uses the getTank method
							 */
							if (storage != null)
							{
								LiquidStack stored = storage.getLiquid();
								if (stored == null)
								{
									fillTarget = tankContainer;
									found = true;
									fillDir = dir;
									break;
								}
								else if (stored.amount < volume)
								{
									fillTarget = tankContainer;
									volume = stored.amount;
								}
							}
							else if (fill > 0 && fill > mostFill)
							{
								otherFillTarget = tankContainer;
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
				if (stack == null || stack.amount <= 0)
				{
					return used;
				}
			}// End of tank finder
			if (doFill)
			{
				if (fillTarget != null)
				{
					used = fillTarget.fill(fillDir, stack, true);
				}
				else if (otherFillTarget != null)
				{
					used = otherFillTarget.fill(fillDir, stack, true);
				}
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

	/**
	 * can this network can accept the liquid type
	 */
	private boolean canAcceptLiquid(LiquidStack stack)
	{
		return color.isValidLiquid(stack);
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
		if (ent == null || ent instanceof IFluidNetworkPart)
		{
			return;
		}
		if (!fluidTanks.contains(ent))
		{
			fluidTanks.add(ent);
		}
	}

	public void addNetworkPart(IFluidNetworkPart newConductor, ColorCode code)
	{
		this.cleanConductors();

		if (code == this.color && !fluidParts.contains(newConductor))
		{
			fluidParts.add(newConductor);
			newConductor.setNetwork(this);
		}
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
			}
		}
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
			HydraulicNetwork newNetwork = new HydraulicNetwork(this.color);

			newNetwork.getFluidNetworkParts().addAll(this.getFluidNetworkParts());
			newNetwork.getFluidNetworkParts().addAll(network.getFluidNetworkParts());

			newNetwork.cleanUpConductors();
		}
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
					for (int ii = 0; ii < connectedBlocks.length; ii++)
					{
						final TileEntity connectedBlockB = connectedBlocks[ii];

						if (connectedBlockA != connectedBlockB && connectedBlockB instanceof IConnectionProvider)
						{
							Pathfinder finder = new PathfinderChecker((IConnectionProvider) connectedBlockB, splitPoint);
							finder.init((IConnectionProvider) connectedBlockA);

							if (finder.results.size() > 0)
							{
								/**
								 * The connections A and B are still intact elsewhere. Set all
								 * references of wire connection into one network.
								 */

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
								/**
								 * The connections A and B are not connected anymore. Give both of
								 * them a new network.
								 */
								HydraulicNetwork newNetwork = new HydraulicNetwork(this.color);

								for (IConnectionProvider node : finder.iteratedNodes)
								{
									if (node instanceof IFluidNetworkPart)
									{
										if (node != splitPoint)
										{
											newNetwork.getFluidNetworkParts().add((IFluidNetworkPart) node);
										}
									}
								}

								newNetwork.cleanUpConductors();
							}
						}
					}
				}
			}
		}
	}

	@Override
	public String toString()
	{
		return "hydraulicNetwork[" + this.hashCode() + "|parts:" + this.fluidParts.size() + "]";
	}
}
