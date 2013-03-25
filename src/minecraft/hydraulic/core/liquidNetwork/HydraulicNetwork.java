package hydraulic.core.liquidNetwork;

import hydraulic.api.ColorCode;
import hydraulic.api.IPipeConnector;
import hydraulic.api.IPsiCreator;
import hydraulic.api.ILiquidNetworkPart;
import hydraulic.api.IPsiReciever;
import hydraulic.helpers.connectionHelper;

import java.util.ArrayList;
import java.util.List;

import universalelectricity.prefab.network.IPacketReceiver;

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.liquids.ILiquidTank;
import net.minecraftforge.liquids.ITankContainer;
import net.minecraftforge.liquids.LiquidStack;

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
	public final List<ILiquidNetworkPart> conductors = new ArrayList<ILiquidNetworkPart>();

	/* MACHINES THAT USE THE FORGE LIQUID API TO RECEIVE LIQUID ** */
	public final List<TileEntity> receivers = new ArrayList<TileEntity>();

	public ColorCode color = ColorCode.NONE;
	/* PRESSURE OF THE NETWORK AS A TOTAL. ZERO AS IN DEFUALT */
	public double pressureProduced = 0;
	/* PRESSURE OF THE NETWORK'S LOAD AS A TOTAL. ZERO AS IN DEFUALT */
	public double pressureLoad = 0;

	public HydraulicNetwork(ILiquidNetworkPart conductor, ColorCode color)
	{
		this.addConductor(conductor, color);
		this.color = color;
	}

	/**
	 * updates the pressure in the network
	 * 
	 * @param ent
	 */
	public void updatePressure()
	{
		this.pressureLoad = 0;
		this.pressureProduced = 0;
		for (TileEntity ent : receivers)
		{
			if (ent instanceof IPipeConnector && ((IPipeConnector) ent).canConnect(ForgeDirection.UNKNOWN, (LiquidStack[]) this.color.getAllLiquidStacks().toArray()))
			{
				if (ent instanceof IPsiReciever)
				{
					pressureLoad += ((IPsiReciever) ent).getPressureLoad();
				}
				else if (ent instanceof IPsiCreator)
				{
					pressureProduced += ((IPsiCreator) ent).getPressureOut(color.getLiquidData().getStack(), ForgeDirection.UNKNOWN);
				}
			}
		}
	}

	/**
	 * Tries to add the liquid stack to the network's valid machines. Same as the fill method for
	 * ITankContainer in that it will fill machines, however it also includes pressure if the
	 * machine also adds pressure to the network. Called mostly by pipes as they are filled from
	 * other mod sources
	 * 
	 * @return The amount of Liquid used.
	 */
	public int addFluidToNetwork(LiquidStack stack, double pressure)
	{
		int used = 0;
		if (stack != null && canAcceptLiquid(stack))
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

			for (TileEntity ent : receivers)
			{
				TileEntity[] surroundings = connectionHelper.getSurroundingTileEntities(ent);

				if (ent instanceof ITankContainer)
				{
					ITankContainer tank = (ITankContainer) ent;

					for (int i = 0; i < 6; i++)
					{
						if (surroundings[i] instanceof ILiquidNetworkPart && ((ILiquidNetworkPart) surroundings[i]).getNetwork() == this)
						{
							ForgeDirection dir = ForgeDirection.getOrientation(i).getOpposite();
							ILiquidTank storage = tank.getTank(dir, stack);
							int fill = tank.fill(dir, stack, false);
							/*
							 * if the TileEntity uses the getTank method
							 */
							if (storage != null)
							{
								LiquidStack stored = storage.getLiquid();
								if (stored == null)
								{
									fillTarget = tank;
									found = true;
									fillDir = dir;
									break;
								}
								else if (stored.amount < volume)
								{
									fillTarget = tank;
									volume = stored.amount;
								}
							}
							else if (fill > 0 && fill > mostFill)
							{
								otherFillTarget = tank;
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
			if (fillTarget != null)
			{
				used = fillTarget.fill(fillDir, stack, true);
			}
			else if (otherFillTarget != null)
			{
				used = otherFillTarget.fill(fillDir, stack, true);
			}
		}

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
		for (ILiquidNetworkPart conductor : this.conductors)
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
	public void addEntity(TileEntity ent)
	{
		if (!receivers.contains(ent) && (ent instanceof ITankContainer || ent instanceof IPsiReciever || ent instanceof IPsiCreator))
		{
			receivers.add(ent);
		}
	}

	public void addConductor(ILiquidNetworkPart newConductor, ColorCode code)
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

	public void resetConductors()
	{
		for (int i = 0; i < conductors.size(); i++)
		{
			conductors.get(i).reset();
		}
	}

	public void setNetwork()
	{
		this.cleanConductors();

		for (ILiquidNetworkPart conductor : this.conductors)
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
			ILiquidNetworkPart part = conductors.get(i);
			if (part.getMaxPressure(ForgeDirection.UNKNOWN) < this.pressureProduced && part.onOverPressure(true))
			{
				this.conductors.remove(part);
				this.cleanConductors();
			}

		}
	}

	/**
	 * This function is called to refresh all conductors in this network
	 */
	public void refreshConductors()
	{
		for (int j = 0; j < this.conductors.size(); j++)
		{
			this.conductors.get(j).refreshConnectedBlocks();
		}
	}

}
