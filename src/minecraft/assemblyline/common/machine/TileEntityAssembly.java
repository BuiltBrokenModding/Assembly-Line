package assemblyline.common.machine;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.ForgeDirection;
import universalelectricity.core.UniversalElectricity;
import universalelectricity.core.block.IConductor;
import universalelectricity.core.electricity.ElectricityDisplay;
import universalelectricity.core.electricity.ElectricityDisplay.ElectricUnit;
import universalelectricity.core.electricity.IElectricityNetwork;
import universalelectricity.core.vector.Vector3;
import universalelectricity.prefab.network.IPacketReceiver;
import universalelectricity.prefab.network.PacketManager;
import assemblyline.common.AssemblyLine;
import buildcraft.api.power.PowerProvider;

import com.google.common.io.ByteArrayDataInput;

import dark.core.api.INetworkPart;
import dark.core.tile.network.NetworkTileEntities;
import dark.library.machine.TileEntityRunnableMachine;

/** A class to be inherited by all machines on the assembly line. This class acts as a single peace
 * in a network of similar tiles allowing all to share power from one or more sources
 * 
 * @author DarkGuardsman */
public abstract class TileEntityAssembly extends TileEntityRunnableMachine implements INetworkPart, IPacketReceiver, IConductor
{
	/** Is the tile currently powered allowing it to run */
	public boolean running = false;
	/** Network used to link assembly machines together */
	private NetworkAssembly assemblyNetwork;
	/** Tiles that are connected to this */
	public List<TileEntity> connectedTiles = new ArrayList<TileEntity>();
	/** Random instance */
	public Random random = new Random();
	/** Random rate by which this tile updates its connections */
	private int updateTick = 1;

	public TileEntityAssembly()
	{
		this.powerProvider = new AssemblyPowerProvider(this);
		powerProvider.configure(0, 0, 100, 0, 200);
	}

	public static enum AssemblyTilePacket
	{
		POWER(),
		NBT();
	}

	@Override
	public void invalidate()
	{
		NetworkAssembly.invalidate(this);
		if (this.getTileNetwork() != null)
		{
			this.getTileNetwork().splitNetwork(this.worldObj, this);
		}
		super.invalidate();
	}

	@Override
	public void updateEntity()
	{
		if (!this.worldObj.isRemote)
		{
			boolean prevRun = this.running;
			super.updateEntity();
			if (ticks % updateTick == 0)
			{
				this.updateTick = ((int) random.nextInt(10) + 20);
				this.updateNetworkConnections();
			}
			this.running = ((NetworkAssembly) this.getTileNetwork()).consumePower(this);
			if (running != prevRun)
			{
				Packet packet = PacketManager.getPacket(AssemblyLine.CHANNEL, this, AssemblyTilePacket.POWER.ordinal(), this.running);
				PacketManager.sendPacketToClients(packet, worldObj, new Vector3(this), 64);
			}
		}

		this.onUpdate();
	}

	@Override
	public void onReceive(ForgeDirection side, double voltage, double amperes)
	{
		if (voltage <= 0 || amperes <= 0)
		{
			return;
		}
		if (voltage > this.getVoltage())
		{
			this.onDisable(2);
			return;
		}
		if (this.getTileNetwork() instanceof NetworkAssembly)
		{
			((NetworkAssembly) this.getTileNetwork()).addPower(voltage * amperes);
			//System.out.println("Tile got power Side:" + side.toString() + " " + ElectricityDisplay.getDisplaySimple(voltage, ElectricUnit.VOLTAGE, 2) + " " + ElectricityDisplay.getDisplaySimple(amperes, ElectricUnit.AMPERE, 2));
		}
	}

	/** Same as updateEntity */
	public abstract void onUpdate();

	/** Checks to see if this assembly tile can run using several methods */
	public boolean isRunning()
	{
		if (!this.worldObj.isRemote)
		{
			return this.running || AssemblyLine.REQUIRE_NO_POWER;
		}
		else
		{
			return this.running;
		}

	}

	/** Amount of energy this tile runs on per tick */
	public double getWattLoad()
	{
		return 1;
	}

	/** Amount of energy the network needs at any given time */
	@Override
	public double getRequest(ForgeDirection side)
	{
		if (this.getTileNetwork() instanceof NetworkAssembly)
		{
			NetworkAssembly net = ((NetworkAssembly) this.getTileNetwork());
			double room = net.getMaxBattery() - net.getCurrentBattery();
			return Math.min(100, Math.max(0, room));
		}
		return 0;
	}

	@Override
	public boolean canTileConnect(TileEntity entity, ForgeDirection dir)
	{
		return entity != null && entity instanceof TileEntityAssembly;
	}

	@Override
	public void updateNetworkConnections()
	{
		if (this.worldObj != null && !this.worldObj.isRemote)
		{
			this.connectedTiles.clear();

			for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS)
			{
				TileEntity tileEntity = new Vector3(this).modifyPositionFromSide(dir).getTileEntity(this.worldObj);
				if (tileEntity instanceof TileEntityAssembly && ((TileEntityAssembly) tileEntity).canTileConnect(this, dir.getOpposite()))
				{
					this.getTileNetwork().merge(((TileEntityAssembly) tileEntity).getTileNetwork(), this);
					connectedTiles.add(tileEntity);
				}
			}
		}
	}

	@Override
	public List<TileEntity> getNetworkConnections()
	{
		return this.connectedTiles;
	}

	@Override
	public NetworkTileEntities getTileNetwork()
	{
		if (this.assemblyNetwork == null)
		{
			this.assemblyNetwork = new NetworkAssembly(this);
		}
		return this.assemblyNetwork;
	}

	@Override
	public void setTileNetwork(NetworkTileEntities network)
	{
		if (network instanceof NetworkAssembly)
		{
			this.assemblyNetwork = (NetworkAssembly) network;
		}

	}

	public String toString()
	{
		return "[AssemblyTile]@" + (new Vector3(this).toString());
	}

	@Override
	public void handlePacketData(INetworkManager network, int packetType, Packet250CustomPayload packet, EntityPlayer player, ByteArrayDataInput dataStream)
	{
		boolean packetSize = false;
		try
		{
			ByteArrayInputStream bis = new ByteArrayInputStream(packet.data);
			DataInputStream dis = new DataInputStream(bis);

			int id = dis.readInt();
			int x = dis.readInt();
			int y = dis.readInt();
			int z = dis.readInt();
			int pId = dis.readInt();

			this.simplePacket(pId, dis, player);

			/** DEBUG PACKET SIZE AND INFO */
			if (packetSize)
			{
				System.out.println("TileEntityAssembly>" + new Vector3(this) + ">>>Debug>>Packet" + pId + ">>Size>>bytes>>" + packet.data.length);
			}
		}
		catch (Exception e)
		{
			System.out.println("Error Reading Packet for a TileEntityAssembly");
			e.printStackTrace();
		}

	}

	/** Handles reduced data from the main packet method
	 * 
	 * @param id - packet ID
	 * @param dis - data
	 * @param player - player
	 * @return true if the packet was used */
	public boolean simplePacket(int id, DataInputStream dis, EntityPlayer player)
	{
		try
		{
			if (this.worldObj.isRemote)
			{
				if (id == AssemblyTilePacket.POWER.ordinal())
				{
					this.running = dis.readBoolean();
					return true;
				}
				if (id == AssemblyTilePacket.NBT.ordinal())
				{
					this.readFromNBT(Packet.readNBTTagCompound(dis));
				}
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public IElectricityNetwork getNetwork()
	{
		return (this.getTileNetwork() instanceof IElectricityNetwork ? (IElectricityNetwork) this.getTileNetwork() : null);
	}

	@Override
	public void setNetwork(IElectricityNetwork network)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public TileEntity[] getAdjacentConnections()
	{
		return new TileEntity[6];
	}

	@Override
	public void updateAdjacentConnections()
	{
		this.updateNetworkConnections();

	}

	@Override
	public double getResistance()
	{
		return 0.01;
	}

	@Override
	public double getCurrentCapcity()
	{
		return 1000;
	}

	class AssemblyPowerProvider extends PowerProvider
	{
		public TileEntityAssembly tileEntity;

		public AssemblyPowerProvider(TileEntityAssembly tile)
		{
			tileEntity = tile;
		}

		@Override
		public void receiveEnergy(float quantity, ForgeDirection from)
		{
			powerSources[from.ordinal()] = 2;
			
			if (tileEntity.getTileNetwork() instanceof NetworkAssembly)
			{
				((NetworkAssembly) tileEntity.getTileNetwork()).addPower(UniversalElectricity.BC3_RATIO * quantity);
				System.out.println("BuildCraft Power Reciver>>>PlugPower>>>"+quantity);
			}
			else
			{
				this.energyStored += quantity;
			}
		}
	}

}
