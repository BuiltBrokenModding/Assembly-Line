package universalelectricity.prefab.tile;

import java.util.EnumSet;

import net.minecraft.src.EntityPlayer;
import net.minecraft.src.INetworkManager;
import net.minecraft.src.Packet250CustomPayload;
import net.minecraft.src.TileEntity;
import net.minecraftforge.common.ForgeDirection;
import universalelectricity.core.electricity.Electricity;
import universalelectricity.core.electricity.ElectricityConnections;
import universalelectricity.core.electricity.ElectricityNetwork;
import universalelectricity.core.implement.IConductor;
import universalelectricity.core.vector.Vector3;
import universalelectricity.prefab.network.IPacketReceiver;

import com.google.common.io.ByteArrayDataInput;

/**
 * This tile entity pre-fabricated for all conductors.
 * 
 * @author Calclavia
 * 
 */
public abstract class TileEntityConductor extends TileEntityAdvanced implements IConductor, IPacketReceiver
{
	private ElectricityNetwork network;

	/**
	 * Stores information on the blocks that this conductor is connected to
	 */
	public TileEntity[] connectedBlocks =
	{ null, null, null, null, null, null };

	public TileEntityConductor()
	{
		ElectricityConnections.registerConnector(this, EnumSet.range(ForgeDirection.DOWN, ForgeDirection.EAST));
		this.reset();
	}

	@Override
	public ElectricityNetwork getNetwork()
	{
		return this.network;
	}

	@Override
	public void setNetwork(ElectricityNetwork network)
	{
		this.network = network;
	}

	@Override
	public TileEntity[] getConnectedBlocks()
	{
		return this.connectedBlocks;
	}

	@Override
	public void updateConnection(TileEntity tileEntity, ForgeDirection side)
	{
		if (tileEntity != null)
		{
			if (ElectricityConnections.isConnector(tileEntity))
			{
				this.connectedBlocks[side.ordinal()] = tileEntity;

				if (tileEntity.getClass() == this.getClass())
				{
					Electricity.instance.mergeConnection(this.getNetwork(), ((TileEntityConductor) tileEntity).getNetwork());
				}

				return;
			}
		}

		if (this.connectedBlocks[side.ordinal()] != null)
		{
			if (this.connectedBlocks[side.ordinal()] instanceof IConductor)
			{
				Electricity.instance.splitConnection(this, (IConductor) this.getConnectedBlocks()[side.ordinal()]);
			}
		}

		this.connectedBlocks[side.ordinal()] = null;
	}

	@Override
	public void updateConnectionWithoutSplit(TileEntity tileEntity, ForgeDirection side)
	{
		if (tileEntity != null)
		{
			if (ElectricityConnections.isConnector(tileEntity))
			{
				this.connectedBlocks[side.ordinal()] = tileEntity;

				if (tileEntity.getClass() == this.getClass())
				{
					Electricity.instance.mergeConnection(this.getNetwork(), ((TileEntityConductor) tileEntity).getNetwork());
				}

				return;
			}
		}

		this.connectedBlocks[side.ordinal()] = null;
	}

	@Override
	public void handlePacketData(INetworkManager network, int type, Packet250CustomPayload packet, EntityPlayer player, ByteArrayDataInput dataStream)
	{
		if (this.worldObj.isRemote)
		{
			this.refreshConnectedBlocks();
		}
	}

	@Override
	public void initiate()
	{
		this.refreshConnectedBlocks();
	}

	@Override
	public void reset()
	{
		this.network = null;

		if (Electricity.instance != null)
		{
			Electricity.instance.registerConductor(this);
		}
	}

	@Override
	public void refreshConnectedBlocks()
	{
		if (this.worldObj != null)
		{
			for (byte i = 0; i < 6; i++)
			{
				this.updateConnection(Vector3.getConnectorFromSide(this.worldObj, Vector3.get(this), ForgeDirection.getOrientation(i)), ForgeDirection.getOrientation(i));
			}
		}
	}
}
