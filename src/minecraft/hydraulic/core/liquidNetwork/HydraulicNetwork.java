package hydraulic.core.liquidNetwork;

import hydraulic.api.ColorCode;
import hydraulic.api.IFluidNetworkPart;
import hydraulic.api.IPipeConnection;
import hydraulic.api.IPsiCreator;
import hydraulic.api.IPsiReciever;
import hydraulic.helpers.connectionHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

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
	public final List<IFluidNetworkPart> conductors = new ArrayList<IFluidNetworkPart>();

	/* MACHINES THAT USE THE FORGE LIQUID API TO RECEIVE LIQUID ** */
	public final List<ITankContainer> receivers = new ArrayList<ITankContainer>();

	public ColorCode color = ColorCode.NONE;
	/* PRESSURE OF THE NETWORK AS A TOTAL. ZERO AS IN NO PRODUCTION */
	public double pressureProduced = 0;
	/* PRESSURE OF THE NETWORK'S LOAD AS A TOTAL. ZERO AS IN NO LOAD */
	public double pressureLoad = 0;
	/* IS IT PROCESSING AN ADD LIQUID EVENT */
	private boolean processingRequest = false;

	public HydraulicNetwork(ColorCode color, IFluidNetworkPart... parts)
	{
		this.conductors.addAll(Arrays.asList(parts));
		this.color = color;
	}

	public void registerLoad(TileEntity entity)
	{

	}

	public void registerProducer(TileEntity entity)
	{

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

			for (ITankContainer tankContainer : receivers)
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
		for (IFluidNetworkPart conductor : this.conductors)
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
		if (receivers.contains(ent))
		{
			receivers.remove(ent);
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
		if (!receivers.contains(ent))
		{
			receivers.add(ent);
		}
	}

	public void addNetworkPart(IFluidNetworkPart newConductor, ColorCode code)
	{
		this.cleanConductors();

		if (code == this.color && !conductors.contains(newConductor))
		{
			conductors.add(newConductor);
			newConductor.setNetwork(this);
		}
	}

	public void cleanConductors()
	{
		for (int i = 0; i < conductors.size(); i++)
		{
			if (conductors.get(i) == null)
			{
				conductors.remove(i);
			}
			else if (((TileEntity) conductors.get(i)).isInvalid())
			{
				conductors.remove(i);
			}
		}
	}

	public void setNetwork()
	{
		this.cleanConductors();

		for (IFluidNetworkPart conductor : this.conductors)
		{
			conductor.setNetwork(this);
		}
	}

	public void onPresureChange()
	{
		this.cleanConductors();

		for (int i = 0; i < conductors.size(); i++)
		{
			// TODO change to actual check connected sides only && get true value from settings file
			IFluidNetworkPart part = conductors.get(i);
			if (part.getMaxPressure(ForgeDirection.UNKNOWN) < this.pressureProduced && part.onOverPressure(true))
			{
				this.conductors.remove(part);
				this.cleanConductors();
			}

		}
	}

	public void cleanUpConductors()
	{
		Iterator it = this.conductors.iterator();

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
			Iterator<IFluidNetworkPart> it = this.conductors.iterator();

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
		return this.conductors;
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
		return "hydraulicNetwork[" + this.hashCode() + "|parts:" + this.conductors.size() + "]";
	}
}
