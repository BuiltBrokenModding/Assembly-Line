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
import universalelectricity.core.vector.Vector3;
import universalelectricity.prefab.network.IPacketReceiver;
import universalelectricity.prefab.network.PacketManager;
import assemblyline.common.AssemblyLine;

import com.google.common.io.ByteArrayDataInput;

import dark.core.api.INetworkPart;
import dark.core.tile.network.NetworkTileEntities;
import dark.library.machine.TileEntityRunnableMachine;

/** A class to be inherited by all machines on the assembly line. This will allow all machines to be
 * able to be powered through the powering of only one machine.
 * 
 * @author Calclavia */
public abstract class TileEntityAssembly extends TileEntityRunnableMachine implements INetworkPart, IPacketReceiver
{
	/** Is this tile being powered by a non-network connection */
	public boolean powered = false;
	public boolean running = false;
	/** Network used to link assembly machines together */
	private NetworkAssembly assemblyNetwork;
	/** Tiles that are connected to this */
	public List<TileEntity> connectedTiles = new ArrayList<TileEntity>();
	/** Random instance */
	public Random random = new Random();
	/** percent tick rate this tile will update at */
	private int updateTick = 1;
	/** ticks sync this tile has gotten power */
	private int lastPoweredTicks = 0;

	public static enum AssemblyTilePacket
	{
		POWER(),
		NBT();
	}

	@Override
	public void invalidate()
	{
		NetworkAssembly.invalidate(this);
		super.invalidate();
	}

	@Override
	public void updateEntity()
	{
		boolean prevRun = this.running;
		this.powered = false;
		super.updateEntity();
		if (!this.worldObj.isRemote)
		{
			if (ticks % updateTick == 0)
			{
				this.updateTick = ((int) random.nextInt(10) + 20);
				this.updateNetworkConnections();
			}
			this.running = this.isRunning();
			if (running != prevRun)
			{
				Packet packet = PacketManager.getPacket(AssemblyLine.CHANNEL, this, AssemblyTilePacket.POWER.ordinal(), this.running);
				PacketManager.sendPacketToClients(packet, worldObj, new Vector3(this), 64);
			}
		}

		this.onUpdate();
	}

	/** Same as updateEntity */
	public abstract void onUpdate();

	/** Checks to see if this assembly tile can run using several methods */
	public boolean isRunning()
	{
		if (!worldObj.isRemote)
		{
			boolean running = AssemblyLine.REQUIRE_NO_POWER;
			if (!running && this.getTileNetwork() instanceof NetworkAssembly)
			{
				running = ((NetworkAssembly) this.getTileNetwork()).canRun(this);
			}
			return running;
		}
		else
		{
			return this.running;
		}

	}

	public double getRequest()
	{
		return .1;
	}

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
				if (tileEntity instanceof TileEntityAssembly)
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
		return "AssemblyTile>>>At>>>" + (new Vector3(this).toString());
	}

	@Override
	public void handlePacketData(INetworkManager network, int packetType, Packet250CustomPayload packet, EntityPlayer player, ByteArrayDataInput dataStream)
	{
		boolean packetSize = false;
		try
		{
			ByteArrayInputStream bis = new ByteArrayInputStream(packet.data);
			DataInputStream dis = new DataInputStream(bis);
			int id, x, y, z;
			id = dis.readInt();
			x = dis.readInt();
			y = dis.readInt();
			z = dis.readInt();
			int pId = dis.readInt();
			this.handlePacket(pId, dis, player);
			if (packetSize)
			{
				System.out.println("TileEntityAssembly>" + new Vector3(this) + ">>>Debug>>Packet" + pId + ">>Size>>bytes>>" + packet.data.length);
			}
		}
		catch (Exception e)
		{
			System.out.println("Errror Reading Packet for a TileEntityAssembly instance");
			e.printStackTrace();
		}

	}

	/** Handles reduced data from the main packet method
	 * 
	 * @param id - packet ID
	 * @param dis - data
	 * @param player - player
	 * @return true if the packet was used */
	public boolean handlePacket(int id, DataInputStream dis, EntityPlayer player)
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
}
