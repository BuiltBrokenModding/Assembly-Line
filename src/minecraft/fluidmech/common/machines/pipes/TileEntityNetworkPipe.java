package fluidmech.common.machines.pipes;

import fluidmech.common.FluidMech;
import hydraulic.api.ColorCode;
import hydraulic.api.IColorCoded;
import hydraulic.api.IPipeConnection;
import hydraulic.api.IFluidNetworkPart;
import hydraulic.api.IReadOut;
import hydraulic.core.liquidNetwork.HydraulicNetwork;

import java.util.Random;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.liquids.ILiquidTank;
import net.minecraftforge.liquids.ITankContainer;
import net.minecraftforge.liquids.LiquidContainerRegistry;
import net.minecraftforge.liquids.LiquidStack;
import net.minecraftforge.liquids.LiquidTank;

import org.bouncycastle.util.Arrays;

import universalelectricity.prefab.network.IPacketReceiver;
import universalelectricity.prefab.network.PacketManager;
import universalelectricity.prefab.tile.TileEntityAdvanced;

import com.google.common.io.ByteArrayDataInput;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class TileEntityNetworkPipe extends TileEntityAdvanced implements ITankContainer, IReadOut, IColorCoded, IFluidNetworkPart, IPacketReceiver
{
	/* TANK TO FAKE OTHER TILES INTO BELIVING THIS HAS AN INTERNAL STORAGE */
	private LiquidTank fakeTank = new LiquidTank(LiquidContainerRegistry.BUCKET_VOLUME);
	/* CURRENTLY CONNECTED TILE ENTITIES TO THIS */
	private TileEntity[] connectedBlocks = new TileEntity[6];
	public boolean[] renderConnection = new boolean[6];
	/* RANDOM INSTANCE USED BY THE UPDATE TICK */
	private Random random = new Random();
	/* NETWORK INSTANCE THAT THIS PIPE USES */
	private HydraulicNetwork pipeNetwork;

	@Override
	public void updateEntity()
	{
		super.updateEntity();
		if (worldObj.isRemote && ticks % ((int) random.nextInt(10) * 10 + 1) == 0)
		{
			this.updateAdjacentConnections();
		}
	}

	@Override
	public void initiate()
	{
		this.updateAdjacentConnections();
	}

	@Override
	public void invalidate()
	{
		if (!this.worldObj.isRemote)
		{
			this.getNetwork().splitNetwork(this);
		}

		super.invalidate();
	}

	@Override
	public void handlePacketData(INetworkManager network, int type, Packet250CustomPayload packet, EntityPlayer player, ByteArrayDataInput dataStream)
	{
		if (this.worldObj.isRemote)
		{
			System.out.print("packet");
			this.renderConnection[0] = dataStream.readBoolean();
			this.renderConnection[1] = dataStream.readBoolean();
			this.renderConnection[2] = dataStream.readBoolean();
			this.renderConnection[3] = dataStream.readBoolean();
			this.renderConnection[4] = dataStream.readBoolean();
			this.renderConnection[5] = dataStream.readBoolean();
		}
	}

	@Override
	public Packet getDescriptionPacket()
	{
		return PacketManager.getPacket(FluidMech.CHANNEL, this, this.renderConnection[0], this.renderConnection[1], this.renderConnection[2], this.renderConnection[3], this.renderConnection[4], this.renderConnection[5]);
	}

	/**
	 * gets the current color mark of the pipe
	 */
	@Override
	public ColorCode getColor()
	{
		return ColorCode.get(worldObj.getBlockMetadata(xCoord, yCoord, zCoord));
	}

	/**
	 * sets the current color mark of the pipe
	 */
	@Override
	public void setColor(Object cc)
	{
		ColorCode code = ColorCode.get(cc);
		if (!worldObj.isRemote && code != this.getColor())
		{
			this.worldObj.setBlockMetadataWithNotify(xCoord, yCoord, zCoord, code.ordinal() & 15, 3);
		}
	}

	@Override
	public String getMeterReading(EntityPlayer user, ForgeDirection side)
	{
		/* DEBUG CODE ACTIVATERS */
		boolean testConnections = false;
		boolean testNetwork = false;

		/* NORMAL OUTPUT */
		String string = this.getNetwork().pressureProduced + "p ";

		/* DEBUG CODE */
		if (testConnections)
		{
			for (int i = 0; i < 6; i++)
			{
				string += ":" + (this.renderConnection[i] ? "T" : "F") + (this.getAdjacentConnections()[i] != null ? "T" : "F");
			}
		}
		if (testNetwork)
		{
			string += " " + this.getNetwork().toString();
		}

		return string;
	}

	@Override
	public int fill(ForgeDirection from, LiquidStack resource, boolean doFill)
	{
		if (resource == null || !this.getColor().isValidLiquid(resource))
		{
			return 0;
		}
		return this.fill(0, resource, doFill);
	}

	@Override
	public int fill(int tankIndex, LiquidStack resource, boolean doFill)
	{
		if (tankIndex != 0 || resource == null || !this.getColor().isValidLiquid(resource))
		{
			return 0;
		}
		return this.getNetwork().addFluidToNetwork(resource, 0, doFill);
	}

	@Override
	public LiquidStack drain(ForgeDirection from, int maxDrain, boolean doDrain)
	{
		return null;
	}

	@Override
	public LiquidStack drain(int tankIndex, int maxDrain, boolean doDrain)
	{
		return null;
	}

	@Override
	public ILiquidTank[] getTanks(ForgeDirection direction)
	{
		return new ILiquidTank[] { this.fakeTank };
	}

	@Override
	public ILiquidTank getTank(ForgeDirection direction, LiquidStack type)
	{
		if (this.getColor().isValidLiquid(type))
		{
			return this.fakeTank;
		}
		return null;
	}

	/**
	 * Checks to make sure the connection is valid to the tileEntity
	 * 
	 * @param tileEntity - the tileEntity being checked
	 * @param side - side the connection is too
	 */
	public void validateConnectionSide(TileEntity tileEntity, ForgeDirection side)
	{
		if (!this.worldObj.isRemote && tileEntity != null)
		{
			if (tileEntity instanceof IPipeConnection)
			{
				if (((IPipeConnection) tileEntity).canConnect(this, side))
				{
					if (tileEntity instanceof IFluidNetworkPart)
					{
						if (((IFluidNetworkPart) tileEntity).getColor() == this.getColor())
						{
							this.getNetwork().mergeNetworks(((IFluidNetworkPart) tileEntity).getNetwork());
							connectedBlocks[side.ordinal()] = tileEntity;
						}
					}
					else
					{
						connectedBlocks[side.ordinal()] = tileEntity;
					}
				}
			}
			else if (tileEntity instanceof IColorCoded)
			{
				if (this.getColor() == ColorCode.NONE || this.getColor() == ((IColorCoded) tileEntity).getColor())
				{
					connectedBlocks[side.ordinal()] = tileEntity;
				}
			}
			else if (tileEntity instanceof ITankContainer)
			{
				connectedBlocks[side.ordinal()] = tileEntity;
			}
		}
	}

	@Override
	public void updateAdjacentConnections()
	{

		if (this.worldObj != null && !this.worldObj.isRemote)
		{
			boolean[] previousConnections = this.renderConnection.clone();
			this.connectedBlocks = new TileEntity[6];

			for (int i = 0; i < 6; i++)
			{
				ForgeDirection dir = ForgeDirection.getOrientation(i);
				this.validateConnectionSide(this.worldObj.getBlockTileEntity(xCoord + dir.offsetX, yCoord + dir.offsetY, zCoord + dir.offsetZ), dir);

				this.renderConnection[i] = this.connectedBlocks[i] != null;
				if (this.connectedBlocks[i] instanceof ITankContainer)
				{
					this.getNetwork().addEntity(this.connectedBlocks[i]);
				}
			}

			/**
			 * Only send packet updates if visuallyConnected changed.
			 */
			if (!Arrays.areEqual(previousConnections, this.renderConnection))
			{
				this.worldObj.markBlockForUpdate(this.xCoord, this.yCoord, this.zCoord);
			}
		}
	}

	@Override
	public boolean canConnect(TileEntity entity, ForgeDirection dir)
	{
		return true;
	}

	@Override
	public double getMaxPressure(ForgeDirection side)
	{
		return 350;
	}

	@Override
	public HydraulicNetwork getNetwork()
	{
		if (this.pipeNetwork == null)
		{
			this.setNetwork(new HydraulicNetwork(this.getColor(), this));
		}
		return this.pipeNetwork;
	}

	@Override
	public void setNetwork(HydraulicNetwork network)
	{
		this.pipeNetwork = network;
	}

	@Override
	public int getMaxFlowRate(LiquidStack stack, ForgeDirection side)
	{
		return LiquidContainerRegistry.BUCKET_VOLUME * 2;
	}

	@Override
	public boolean onOverPressure(Boolean damageAllowed)
	{
		if (damageAllowed)
		{
			worldObj.setBlockAndMetadataWithNotify(xCoord, yCoord, yCoord, 0, 0, 3);
			return true;
		}
		return false;
	}

	@SideOnly(Side.CLIENT)
	public AxisAlignedBB getRenderBoundingBox()
	{
		return AxisAlignedBB.getAABBPool().getAABB(this.xCoord, this.yCoord, this.zCoord, this.xCoord + 1, this.yCoord + 1, this.zCoord + 1);
	}

	@Override
	public TileEntity[] getAdjacentConnections()
	{
		return this.connectedBlocks;
	}

	@Override
	public boolean canConnect(ForgeDirection dir)
	{
		return worldObj.getBlockTileEntity(xCoord + dir.offsetX, yCoord + dir.offsetZ, zCoord + dir.offsetY) instanceof IFluidNetworkPart;
	}

}
