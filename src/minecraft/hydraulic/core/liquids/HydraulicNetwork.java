package hydraulic.core.liquids;

import hydraulic.core.implement.ColorCode;
import hydraulic.core.implement.IFluidPipe;
import hydraulic.core.implement.IPsiCreator;
import hydraulic.core.implement.IPsiReciever;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.liquids.ITankContainer;
import net.minecraftforge.liquids.LiquidStack;

public class HydraulicNetwork
{
	/* BLOCK THAT ACT AS FLUID CONVEYORS ** */
	public final List<IFluidPipe> conductors = new ArrayList<IFluidPipe>();

	/* MACHINES THAT USE THE FORGE LIQUID API TO RECEIVE LIQUID ** */
	public final List<ITankContainer> fluidReceivers = new ArrayList<ITankContainer>();

	/* MACHINES THAT DEAL WITH PRESSURE ** */
	public final List<IPsiCreator> pressureProducers = new ArrayList<IPsiCreator>();
	public final List<IPsiReciever> pressureReceivers = new ArrayList<IPsiReciever>();

	public ColorCode color;

	public HydraulicNetwork(IFluidPipe conductor)
	{
		this.addConductor(conductor);
		this.color = conductor.getColor();
	}

	/**
	 * Tries to add the liquid stack to the network's valid machines. Same as the fill method for
	 * ITankContainer in that it will
	 * 
	 * @return The amount of Liquid used.
	 */
	public int addFluidToNetwork(LiquidStack stack)
	{
		if (stack != null && canAcceptLiquid(stack))
		{

		}
		return 0;
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
		fluidReceivers.remove(ent);
		pressureProducers.remove(ent);
		pressureReceivers.remove(ent);
	}

	public void addConductor(IFluidPipe newConductor)
	{
		this.cleanConductors();

		if (!conductors.contains(newConductor))
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

		for (IFluidPipe conductor : this.conductors)
		{
			conductor.setNetwork(this);
		}
	}

	public void onOverCharge()
	{
		this.cleanConductors();

		for (int i = 0; i < conductors.size(); i++)
		{
			conductors.get(i).onOverPressure();
		}
	}

	/**
	 * This function is called to refresh all conductors in this network
	 */
	public void refreshConductors()
	{
		for (int j = 0; j < this.conductors.size(); j++)
		{
			IFluidPipe conductor = this.conductors.get(j);
			conductor.refreshConnectedBlocks();
		}
	}

}
