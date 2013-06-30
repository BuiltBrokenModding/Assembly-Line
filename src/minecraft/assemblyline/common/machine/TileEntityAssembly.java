package assemblyline.common.machine;

import java.util.Random;

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.ForgeDirection;
import universalelectricity.core.electricity.ElectricityPack;
import universalelectricity.core.vector.Vector3;
import assemblyline.common.AssemblyLine;
import dark.core.api.INetworkPart;
import dark.core.tile.network.NetworkTileEntities;
import dark.library.machine.TileEntityRunnableMachine;

/** A class to be inherited by all machines on the assembly line. This will allow all machines to be
 * able to be powered through the powering of only one machine.
 * 
 * @author Calclavia */
public abstract class TileEntityAssembly extends TileEntityRunnableMachine implements INetworkPart
{
	/** Is this tile being powered by a non-network connection */
	public boolean powered = false;
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
	private int updateTick = 1;

	@Override
	public void updateEntity()
	{
		super.updateEntity();

		if (ticks % updateTick == 0)
		{
			this.updateTick = ((int) random.nextInt(10) + 20);
			this.updateNetworkConnections();
			this.powerSource = null;
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

		this.onUpdate();
	}

	/** Same as updateEntity */
	public abstract void onUpdate();

	/** Checks to see if this assembly tile can run using several methods */
	public boolean isRunning()
	{
		boolean running = AssemblyLine.REQUIRE_NO_POWER || this.powered;
		if (!running && this.powerSource != null)
		{
			running = this.powerSource.powered;
		}
		if (!running && this.getTileNetwork() instanceof NetworkAssembly)
		{
			NetworkAssembly net = ((NetworkAssembly) this.getTileNetwork());
			this.powerSource = net.canRun(this);
			running = this.powerSource != null && this.powerSource.powered;
		}
		return running;
	}

	@Override
	public ElectricityPack getRequest()
	{
		return new ElectricityPack(1, this.getVoltage());
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
}
