package assemblyline.common.machine;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.Random;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.ForgeDirection;
import universalelectricity.core.electricity.ElectricityPack;
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
	private TileEntity[] connectedTiles = new TileEntity[6];
	/** Cached power source to reduce the need to path find for a new one each tick */
	public TileEntityAssembly powerSource;
	/** Random instance */
	public Random random = new Random();
	/** Number of ticks this can go without power */
	private int powerTicks = 0;
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
		super.updateEntity();
		boolean prevRun = this.running;
		if (ticks % updateTick == 0)
		{
			this.updateTick = ((int) random.nextInt(10) + 20);
			this.updateNetworkConnections();
		}
		if (this.wattsReceived >= this.getRequest().getWatts())
		{
			this.wattsReceived -= getRequest().getWatts();
			this.powered = true;
			this.powerTicks = 2;

		}
		else if (this.powerTicks > 0)
		{
			this.powerTicks--;
			this.powered = true;
		}
		else
		{
			this.powered = false;
		}
		if (this.getTileNetwork() instanceof NetworkAssembly)
		{
			NetworkAssembly net = ((NetworkAssembly) this.getTileNetwork());
			net.markAsPowerSource(this, this.powered);
		}
		if (!this.worldObj.isRemote)
		{
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
			boolean running = AssemblyLine.REQUIRE_NO_POWER || this.powered;
			if (!running && this.powerSource != null && this.getTileNetwork() instanceof NetworkAssembly)
			{
				NetworkAssembly net = ((NetworkAssembly) this.getTileNetwork());
				running = net.getNetworkMemebers().contains(this.powerSource) && this.powerSource.powered;
				if (!running)
				{
					this.lastPoweredTicks++;
					if (this.lastPoweredTicks >= 20 && !net.getNetworkMemebers().contains(this.powerSource))
					{
						this.powerSource = null;
					}
				}
			}
			if (!running && this.getTileNetwork() instanceof NetworkAssembly)
			{
				NetworkAssembly net = ((NetworkAssembly) this.getTileNetwork());
				this.powerSource = net.canRun(this);
				running = this.powerSource != null && this.powerSource.powered;
			}
			return running;
		}
		else
		{
			return this.running;
		}

	}

	@Override
	public ElectricityPack getRequest()
	{
		int voltage = 120;
		double amps = .1D;
		if (this.getTileNetwork() instanceof NetworkAssembly)
		{

		}
		return new ElectricityPack(amps, voltage);
	}

	@Override
	public boolean canTileConnect(TileEntity entity, ForgeDirection dir)
	{
		return entity != null && entity instanceof TileEntityAssembly;
	}

	/** Validates and adds a connection on a given side from a given tileEntity */
	public void validateConnectionSide(TileEntity tileEntity, ForgeDirection side)
	{
		if (!this.worldObj.isRemote)
		{
			if (tileEntity instanceof TileEntityAssembly)
			{
				this.getTileNetwork().merge(((TileEntityAssembly) tileEntity).getTileNetwork(), this);
				connectedTiles[side.ordinal()] = tileEntity;
			}
		}
	}

	@Override
	public void updateNetworkConnections()
	{
		if (this.worldObj != null && !this.worldObj.isRemote)
		{
			this.connectedTiles = new TileEntity[6];

			for (int i = 0; i < 6; i++)
			{
				ForgeDirection dir = ForgeDirection.getOrientation(i);
				this.validateConnectionSide(this.worldObj.getBlockTileEntity(xCoord + dir.offsetX, yCoord + dir.offsetY, zCoord + dir.offsetZ), dir);

			}
		}
	}

	@Override
	public TileEntity[] getNetworkConnections()
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
		boolean packetSize = true;
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
