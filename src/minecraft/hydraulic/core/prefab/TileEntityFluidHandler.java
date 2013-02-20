package hydraulic.core.prefab;

import hydraulic.core.implement.IColorCoded;
import hydraulic.core.implement.IFluidPipe;
import hydraulic.core.implement.IPsiCreator;
import hydraulic.core.implement.IPsiMachine;
import hydraulic.core.liquids.Hydraulic;
import hydraulic.core.liquids.HydraulicNetwork;
import hydraulic.core.liquids.LiquidData;
import hydraulic.core.liquids.LiquidHandler;
import net.minecraft.network.packet.Packet;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.liquids.ITankContainer;

import org.bouncycastle.util.Arrays;

import universalelectricity.core.vector.Vector3;
import universalelectricity.prefab.network.IPacketReceiver;
import universalelectricity.prefab.network.PacketManager;
import universalelectricity.prefab.tile.TileEntityAdvanced;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * A Pre-made TileEntity for creating new pipe like TileEntities
 * 
 * @author Calclavia,DarkGuardsman
 * 
 */
public abstract class TileEntityFluidHandler extends TileEntityAdvanced implements IFluidPipe, IPacketReceiver
{
	private HydraulicNetwork network;

	/**
	 * Used client side to render.
	 */
	public boolean[] visuallyConnected = { false, false, false, false, false, false };

	/**
	 * Stores information on the blocks that this conductor is connected to.
	 */
	public TileEntity[] connectedBlocks = { null, null, null, null, null, null };

	protected String channel = "";

	protected LiquidData liquidData = LiquidHandler.unkown;

	public TileEntityFluidHandler()
	{
		this.reset();
	}

	@Override
	public HydraulicNetwork getNetwork()
	{
		return this.network;
	}

	@Override
	public void setNetwork(HydraulicNetwork network)
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
		if (!this.worldObj.isRemote)
		{
			if (tileEntity != null)
			{
				if (this.canConnect(tileEntity, side.getOpposite()))
				{
					this.connectedBlocks[side.ordinal()] = tileEntity;
					this.visuallyConnected[side.ordinal()] = true;

					if (tileEntity.getClass() == this.getClass())
					{
						Hydraulic.instance.mergeConnection(this.getNetwork(), ((IFluidPipe) tileEntity).getNetwork());
					}

					return;
				}
			}

			if (this.connectedBlocks[side.ordinal()] != null)
			{
				if (this.connectedBlocks[side.ordinal()] instanceof IFluidPipe)
				{
					Hydraulic.instance.splitConnection(this, (IFluidPipe) this.getConnectedBlocks()[side.ordinal()]);
				}

				this.getNetwork().stopProducing(this.connectedBlocks[side.ordinal()]);
				this.getNetwork().stopRequesting(this.connectedBlocks[side.ordinal()]);
			}

			this.connectedBlocks[side.ordinal()] = null;
			this.visuallyConnected[side.ordinal()] = false;
		}
	}

	/**
	 * checks to see if this pipe can connect to the tileEntity
	 */
	public boolean canConnect(TileEntity tileEntity, ForgeDirection opposite)
	{
		if (tileEntity instanceof IPsiMachine && ((IPsiMachine) tileEntity).getMaxPressure(opposite) <= this.getMaxPressure(opposite))
		{
			return true;
		}
		else if (tileEntity instanceof IPsiCreator && ((IPsiCreator) tileEntity).getCanPressureTo(getLiquidData(), opposite))
		{
			return true;
		}
		else if (tileEntity instanceof ITankContainer)
		{
			ITankContainer tank = (ITankContainer) tileEntity;
			return tank.getTank(opposite, getLiquidData().getStack()) != null || getLiquidData() == LiquidHandler.unkown;
		}
		else if (tileEntity instanceof IFluidPipe)
		{
			return true;
		}

		return false;
	}

	/**
	 * gets the sample stack to use for pipe connection checks
	 * 
	 * @return
	 */
	public LiquidData getLiquidData()
	{
		return LiquidHandler.unkown;
	}

	@Override
	public void updateConnectionWithoutSplit(TileEntity tileEntity, ForgeDirection side)
	{
		if (!this.worldObj.isRemote)
		{
			if (tileEntity != null)
			{
				if (this.canConnect(tileEntity, side.getOpposite()))
				{
					this.connectedBlocks[side.ordinal()] = tileEntity;
					this.visuallyConnected[side.ordinal()] = true;

					if (tileEntity.getClass() == this.getClass())
					{
						Hydraulic.instance.mergeConnection(this.getNetwork(), ((IFluidPipe) tileEntity).getNetwork());
					}

					return;
				}
			}

			this.connectedBlocks[side.ordinal()] = null;
			this.visuallyConnected[side.ordinal()] = false;
		}
	}

	@Override
	public void initiate()
	{
		this.refreshConnectedBlocks();
	}

	@Override
	public void updateEntity()
	{
		super.updateEntity();

		if (this.ticks % 300 == 0)
		{
			this.refreshConnectedBlocks();
		}
	}

	@Override
	public void reset()
	{
		this.network = null;

		if (Hydraulic.instance != null)
		{
			Hydraulic.instance.registerConductor(this);
		}
	}

	@Override
	public void refreshConnectedBlocks()
	{
		if (this.worldObj != null)
		{
			if (!this.worldObj.isRemote)
			{
				boolean[] previousConnections = this.visuallyConnected.clone();

				for (byte i = 0; i < 6; i++)
				{
					this.updateConnection(Vector3.getConnectorFromSide(this.worldObj, new Vector3(this), ForgeDirection.getOrientation(i)), ForgeDirection.getOrientation(i));
				}

				/**
				 * Only send packet updates if visuallyConnected changed.
				 */
				if (!Arrays.areEqual(previousConnections, this.visuallyConnected))
				{
					this.worldObj.markBlockForUpdate(this.xCoord, this.yCoord, this.zCoord);
				}
			}

		}
	}

	@Override
	public Packet getDescriptionPacket()
	{
		return PacketManager.getPacket(this.channel, this, this.visuallyConnected[0], this.visuallyConnected[1], this.visuallyConnected[2], this.visuallyConnected[3], this.visuallyConnected[4], this.visuallyConnected[5]);
	}

	@SideOnly(Side.CLIENT)
	public AxisAlignedBB getRenderBoundingBox()
	{
		return INFINITE_EXTENT_AABB;
	}
}
