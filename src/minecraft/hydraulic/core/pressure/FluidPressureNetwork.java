package hydraulic.core.pressure;

import hydraulic.core.implement.IFluidPipe;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.liquids.LiquidStack;
import universalelectricity.core.vector.Vector3;
import cpw.mods.fml.common.FMLLog;
import fluidmech.api.liquids.LiquidHandler;

public class FluidPressureNetwork
{
	private final HashMap<TileEntity, FluidPacket> producers = new HashMap<TileEntity, FluidPacket>();
	private final HashMap<TileEntity, FluidPacket> consumers = new HashMap<TileEntity, FluidPacket>();

	public final List<IFluidPipe> conductors = new ArrayList<IFluidPipe>();
	
	public LiquidStack stack = new LiquidStack(0,0,0);
	
	public FluidPressureNetwork(IFluidPipe conductor)
	{
		this.addConductor(conductor);
	}

	/**
	 * Sets this tile entity to start producing energy in this network.
	 */
	public void startProducing(TileEntity tileEntity, FluidPacket pack)
	{
		if (tileEntity != null && pack.liquidStack != null && LiquidHandler.isEqual(stack, pack.liquidStack))
		{
			this.producers.put(tileEntity, pack);
		}
	}

	public void startProducing(TileEntity tileEntity, double pressure, LiquidStack stack)
	{
		this.startProducing(tileEntity, new FluidPacket(pressure, stack));
	}

	public boolean isProducing(TileEntity tileEntity)
	{
		return this.producers.containsKey(tileEntity);
	}

	/**
	 * Sets this tile entity to stop producing energy in this network.
	 */
	public void stopProducing(TileEntity tileEntity)
	{
		this.producers.remove(tileEntity);
	}

	/**
	 * Sets this tile entity to start producing energy in this network.
	 */
	public void startRequesting(TileEntity tileEntity, FluidPacket pack)
	{
		if (tileEntity != null && pack.liquidStack != null && LiquidHandler.isEqual(stack, pack.liquidStack))
		{
			this.consumers.put(tileEntity, pack);
		}
	}

	public void startRequesting(TileEntity tileEntity, double pressure, LiquidStack stack)
	{
		this.startRequesting(tileEntity, new FluidPacket(pressure, stack));
	}

	public boolean isRequesting(TileEntity tileEntity)
	{
		return this.consumers.containsKey(tileEntity);
	}

	/**
	 * Sets this tile entity to stop producing energy in this network.
	 */
	public void stopRequesting(TileEntity tileEntity)
	{
		this.consumers.remove(tileEntity);
	}

	/**
	 * @return The electricity produced in this electricity network
	 */
	public FluidPacket getProduced(LiquidStack stack)
	{
		FluidPacket totalElectricity = new FluidPacket(0, new LiquidStack(stack.itemID,0,stack.itemMeta));

		Iterator it = this.producers.entrySet().iterator();

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

				FluidPacket pack = (FluidPacket) pairs.getValue();

				if (pairs.getKey() != null && pairs.getValue() != null && pack != null && totalElectricity.liquidStack != null && pack.liquidStack != null)
				{
					int volume = totalElectricity.liquidStack.amount + pack.liquidStack.amount;
					double pressure = Math.max(totalElectricity.pressure, pack.pressure);

					totalElectricity.liquidStack = new LiquidStack(stack.itemID,volume,stack.itemMeta);
					totalElectricity.pressure = pressure;
				}
			}
		}

		return totalElectricity;
	}

	/**
	 * @return How much electricity this network needs.
	 */
	public FluidPacket getRequest(LiquidStack stack)
	{
		FluidPacket totalElectricity = this.getRequestWithoutReduction(stack);
		LiquidStack a = totalElectricity.liquidStack;
		LiquidStack b = this.getProduced(stack).liquidStack;
		if(a != null && b != null)
		{
		    int amount = Math.max(a.amount - b.amount, 0);
		    totalElectricity.liquidStack.amount = amount;
		}

		return totalElectricity;
	}

	public FluidPacket getRequestWithoutReduction(LiquidStack stack)
	{
		FluidPacket totalElectricity = new FluidPacket(0, new LiquidStack(stack.itemID,0,stack.itemMeta));

		Iterator it = this.consumers.entrySet().iterator();

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

				FluidPacket pack = (FluidPacket) pairs.getValue();

				if (pack != null && pack.liquidStack != null)
				{
					totalElectricity.liquidStack.amount += pack.liquidStack.amount;
					totalElectricity.pressure = Math.max(totalElectricity.pressure, pack.pressure);
				}
			}
		}

		return totalElectricity;
	}

	

	/**
	 * @return Returns all producers in this electricity network.
	 */
	public HashMap<TileEntity, FluidPacket> getProducers()
	{
		return this.producers;
	}

	/**
	 * @return Returns all consumers in this electricity network.
	 */
	public HashMap<TileEntity, FluidPacket> getConsumers()
	{
		return this.consumers;
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

	/**
	 * Get only the electric units that can receive electricity from the given side.
	 */
	public List<TileEntity> getReceivers()
	{
		List<TileEntity> receivers = new ArrayList<TileEntity>();

		Iterator it = this.consumers.entrySet().iterator();

		while (it.hasNext())
		{
			Map.Entry pairs = (Map.Entry) it.next();

			if (pairs != null)
			{
				receivers.add((TileEntity) pairs.getKey());
			}
		}

		return receivers;
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
