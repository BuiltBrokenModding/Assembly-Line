package assemblyline.common.machine;

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.ForgeDirection;
import universalelectricity.core.electricity.ElectricityPack;
import universalelectricity.prefab.network.PacketManager;
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
	public boolean powered = false;
	/** Network used to link assembly machines together */
	private NetworkAssembly assemblyNetwork;

	public boolean isRunning()
	{
		boolean running = AssemblyLine.REQUIRE_NO_POWER || this.powered;
		if (!running && this.getTileNetwork() instanceof NetworkAssembly)
		{

		}
		return running;
	}

	@Override
	public void updateEntity()
	{
		super.updateEntity();
		this.onUpdate();
		
		if (this.wattsReceived >= this.getRequest().getWatts())
		{
			this.wattsReceived -= getRequest().getWatts();
		}
	}

	public abstract void onUpdate();

	@Override
	public ElectricityPack getRequest()
	{
		return new ElectricityPack(1, this.getVoltage());
	}

	protected int getMaxTransferRange()
	{
		return 30;
	}

	@Override
	public boolean canTileConnect(TileEntity entity, ForgeDirection dir)
	{
		return entity != null && entity instanceof TileEntityAssembly;
	}

	@Override
	public TileEntity[] getNetworkConnections()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void updateNetworkConnections()
	{
		// TODO Auto-generated method stub

	}

	@Override
	public NetworkTileEntities getTileNetwork()
	{
		if (this.assemblyNetwork == null)
		{
			this.assemblyNetwork = new NetworkAssembly(this);
		}
		return null;
	}

	@Override
	public void setTileNetwork(NetworkTileEntities network)
	{
		if(network instanceof NetworkAssembly)
		{
			this.assemblyNetwork = (NetworkAssembly) network;
		}

	}
}
