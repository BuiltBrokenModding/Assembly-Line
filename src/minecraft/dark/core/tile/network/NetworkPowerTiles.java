package dark.core.tile.network;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.minecraft.tileentity.TileEntity;
import universalelectricity.core.block.IConductor;
import universalelectricity.core.block.IConnectionProvider;
import universalelectricity.core.electricity.ElectricityPack;
import universalelectricity.core.electricity.IElectricityNetwork;
import cpw.mods.fml.common.FMLLog;
import dark.core.api.INetworkPart;

public class NetworkPowerTiles extends NetworkTileEntities implements IElectricityNetwork
{
	private final HashMap<TileEntity, ElectricityPack> producers = new HashMap<TileEntity, ElectricityPack>();
	private final HashMap<TileEntity, ElectricityPack> consumers = new HashMap<TileEntity, ElectricityPack>();

	public NetworkPowerTiles(INetworkPart... conductors)
	{
		super(conductors);
	}
	
	@Override
	public NetworkTileEntities newInstance()
	{
		return new NetworkPowerTiles();
	}

	@Override
	public void startProducing(TileEntity tileEntity, ElectricityPack electricityPack)
	{
		if (tileEntity != null && electricityPack.getWatts() > 0)
		{
			this.producers.put(tileEntity, electricityPack);
		}
	}

	@Override
	public void startProducing(TileEntity tileEntity, double amperes, double voltage)
	{
		this.startProducing(tileEntity, new ElectricityPack(amperes, voltage));
	}

	@Override
	public boolean isProducing(TileEntity tileEntity)
	{
		return this.producers.containsKey(tileEntity);
	}

	/** Sets this tile entity to stop producing energy in this network. */
	@Override
	public void stopProducing(TileEntity tileEntity)
	{
		this.producers.remove(tileEntity);
	}

	/** Sets this tile entity to start producing energy in this network. */
	@Override
	public void startRequesting(TileEntity tileEntity, ElectricityPack electricityPack)
	{
		if (tileEntity != null && electricityPack.getWatts() > 0)
		{
			this.consumers.put(tileEntity, electricityPack);
		}
	}

	@Override
	public void startRequesting(TileEntity tileEntity, double amperes, double voltage)
	{
		this.startRequesting(tileEntity, new ElectricityPack(amperes, voltage));
	}

	@Override
	public boolean isRequesting(TileEntity tileEntity)
	{
		return this.consumers.containsKey(tileEntity);
	}

	/** Sets this tile entity to stop producing energy in this network. */
	@Override
	public void stopRequesting(TileEntity tileEntity)
	{
		this.consumers.remove(tileEntity);
	}

	/** @param ignoreTiles The TileEntities to ignore during this calculation. Null will make it not
	 * ignore any.
	 * @return The electricity produced in this electricity network */
	@Override
	public ElectricityPack getProduced(TileEntity... ignoreTiles)
	{
		ElectricityPack totalElectricity = new ElectricityPack(0, 0);

		Iterator it = this.producers.entrySet().iterator();

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

				ElectricityPack pack = (ElectricityPack) pairs.getValue();

				if (pairs.getKey() != null && pairs.getValue() != null && pack != null)
				{
					double newWatts = totalElectricity.getWatts() + pack.getWatts();
					double newVoltage = Math.max(totalElectricity.voltage, pack.voltage);

					totalElectricity.amperes = newWatts / newVoltage;
					totalElectricity.voltage = newVoltage;
				}
			}
		}

		return totalElectricity;
	}

	/** @return How much electricity this network needs. */
	@Override
	public ElectricityPack getRequest(TileEntity... ignoreTiles)
	{
		ElectricityPack totalElectricity = this.getRequestWithoutReduction();
		totalElectricity.amperes = Math.max(totalElectricity.amperes - this.getProduced(ignoreTiles).amperes, 0);
		return totalElectricity;
	}

	@Override
	public ElectricityPack getRequestWithoutReduction()
	{
		ElectricityPack totalElectricity = new ElectricityPack(0, 0);

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

				ElectricityPack pack = (ElectricityPack) pairs.getValue();

				if (pack != null)
				{
					totalElectricity.amperes += pack.amperes;
					totalElectricity.voltage = Math.max(totalElectricity.voltage, pack.voltage);
				}
			}
		}

		return totalElectricity;
	}

	/** @param tileEntity
	 * @return The electricity being input into this tile entity. */
	@Override
	public ElectricityPack consumeElectricity(TileEntity tileEntity)
	{
		ElectricityPack totalElectricity = new ElectricityPack(0, 0);

		try
		{
			ElectricityPack tileRequest = this.consumers.get(tileEntity);

			if (this.consumers.containsKey(tileEntity) && tileRequest != null)
			{
				// Calculate the electricity this TileEntity is receiving in percentage.
				totalElectricity = this.getProduced();

				if (totalElectricity.getWatts() > 0)
				{
					ElectricityPack totalRequest = this.getRequestWithoutReduction();
					totalElectricity.amperes *= (tileRequest.amperes / totalRequest.amperes);

					double ampsReceived = totalElectricity.amperes - (totalElectricity.amperes * totalElectricity.amperes * this.getTotalResistance()) / totalElectricity.voltage;
					double voltsReceived = totalElectricity.voltage - (totalElectricity.amperes * this.getTotalResistance());

					totalElectricity.amperes = ampsReceived;
					totalElectricity.voltage = voltsReceived;

					return totalElectricity;
				}
			}
		}
		catch (Exception e)
		{
			FMLLog.severe("Failed to consume electricity!");
			e.printStackTrace();
		}

		return totalElectricity;
	}

	@Override
	public HashMap<TileEntity, ElectricityPack> getProducers()
	{
		return this.producers;
	}

	@Override
	public List<TileEntity> getProviders()
	{
		List<TileEntity> providers = new ArrayList<TileEntity>();
		providers.addAll(this.producers.keySet());
		return providers;
	}

	@Override
	public HashMap<TileEntity, ElectricityPack> getConsumers()
	{
		return this.consumers;
	}

	@Override
	public List<TileEntity> getReceivers()
	{
		List<TileEntity> receivers = new ArrayList<TileEntity>();
		receivers.addAll(this.consumers.keySet());
		return receivers;
	}

	@Override
	public void cleanUpConductors()
	{
		this.cleanUpMembers();
	}

	/** This function is called to refresh all conductors in this network */
	@Override
	public void refreshConductors()
	{
		super.refresh();
	}

	@Override
	public double getTotalResistance()
	{
		return 1;
	}

	@Override
	public double getLowestCurrentCapacity()
	{
		return 10000;
	}

	@Override
	public Set<IConductor> getConductors()
	{
		Set<IConductor> cond = new HashSet<IConductor>();
		for (INetworkPart part : this.getNetworkMemebers())
		{
			if (part instanceof IConductor)
			{
				cond.add((IConductor) part);
			}
		}
		return cond;
	}

	@Override
	public void mergeConnection(IElectricityNetwork network)
	{

	}

	@Override
	public void splitNetwork(IConnectionProvider splitPoint)
	{

	}

}
