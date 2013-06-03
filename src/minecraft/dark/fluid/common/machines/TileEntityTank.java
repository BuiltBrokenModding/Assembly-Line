package dark.fluid.common.machines;

import hydraulic.api.ColorCode;
import hydraulic.api.IColorCoded;
import hydraulic.api.INetworkPipe;
import hydraulic.api.ITileConnector;
import hydraulic.api.IReadOut;
import hydraulic.helpers.FluidHelper;
import hydraulic.network.PipeNetwork;
import hydraulic.prefab.tile.TileEntityFluidStorage;

import java.util.Random;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
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

import universalelectricity.core.vector.Vector3;
import universalelectricity.core.vector.VectorHelper;
import universalelectricity.prefab.network.IPacketReceiver;
import universalelectricity.prefab.network.PacketManager;

import com.google.common.io.ByteArrayDataInput;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import dark.fluid.common.FluidMech;
import dark.fluid.common.pipes.TileEntityPipe;

public class TileEntityTank extends TileEntityFluidStorage implements ITankContainer, IReadOut, IColorCoded, INetworkPipe, IPacketReceiver
{
	/* TANK TO FAKE OTHER TILES INTO BELIVING THIS HAS AN INTERNAL STORAGE */
	protected LiquidTank fakeTank = new LiquidTank(LiquidContainerRegistry.BUCKET_VOLUME);
	/* CURRENTLY CONNECTED TILE ENTITIES TO THIS */
	private TileEntity[] connectedBlocks = new TileEntity[6];
	public int[] renderConnection = new int[6];
	/* RANDOM INSTANCE USED BY THE UPDATE TICK */
	private Random random = new Random();
	/* NETWORK INSTANCE THAT THIS PIPE USES */
	private PipeNetwork pipeNetwork;

	private boolean shouldAutoDrain = false;

	public enum PacketID
	{
		PIPE_CONNECTIONS, EXTENTION_CREATE, EXTENTION_UPDATE;
	}

	@Override
	public void initiate()
	{
		this.updateNetworkConnections();
	}

	@Override
	public void updateEntity()
	{
		super.updateEntity();
		if (!worldObj.isRemote)
		{
			if (ticks % ((int) random.nextInt(5) * 40 + 20) == 0)
			{
				this.updateNetworkConnections();
			}
		}
	}

	@Override
	public void invalidate()
	{
		if (!this.worldObj.isRemote)
		{
			this.getTileNetwork().splitNetwork(this.worldObj, this);
		}

		super.invalidate();
	}

	@Override
	public void handlePacketData(INetworkManager network, int type, Packet250CustomPayload packet, EntityPlayer player, ByteArrayDataInput dataStream)
	{
		PacketID id = PacketID.values()[dataStream.readInt()];
		if (this.worldObj.isRemote)
		{
			if (id == PacketID.PIPE_CONNECTIONS)
			{
				this.renderConnection[0] = dataStream.readInt();
				this.renderConnection[1] = dataStream.readInt();
				this.renderConnection[2] = dataStream.readInt();
				this.renderConnection[3] = dataStream.readInt();
				this.renderConnection[4] = dataStream.readInt();
				this.renderConnection[5] = dataStream.readInt();
			}
		}
	}

	@Override
	public Packet getDescriptionPacket()
	{
		return PacketManager.getPacket(FluidMech.CHANNEL, this, PacketID.PIPE_CONNECTIONS.ordinal(), this.renderConnection[0], this.renderConnection[1], this.renderConnection[2], this.renderConnection[3], this.renderConnection[4], this.renderConnection[5]);
	}

	/**
	 * Reads a tile entity from NBT.
	 */
	@Override
	public void readFromNBT(NBTTagCompound nbt)
	{
		super.readFromNBT(nbt);
		LiquidStack liquid = LiquidStack.loadLiquidStackFromNBT(nbt.getCompoundTag("tank"));
		if (liquid != null)
		{
			this.fakeTank.setLiquid(liquid);
		}
	}

	/**
	 * Writes a tile entity to NBT.
	 */
	@Override
	public void writeToNBT(NBTTagCompound nbt)
	{
		super.writeToNBT(nbt);
		if (this.fakeTank.containsValidLiquid())
		{
			nbt.setTag("stored", this.fakeTank.getLiquid().writeToNBT(new NBTTagCompound()));
		}
	}

	/**
	 * gets the current color mark of the pipe
	 */
	@Override
	public ColorCode getColor()
	{
		if (this.worldObj == null)
		{
			return ColorCode.NONE;
		}
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
			this.worldObj.setBlockMetadataWithNotify(xCoord, yCoord, zCoord, code.ordinal(), 3);
		}
	}

	@Override
	public String getMeterReading(EntityPlayer user, ForgeDirection side)
	{
		/* DEBUG CODE ACTIVATERS */
		boolean testConnections = false;
		boolean testNetwork = false;
		boolean testSubs = false;

		/* NORMAL OUTPUT */
		String string = this.getTileNetwork().pressureProduced + "p " + this.getTileNetwork().getNetworkFluid() + " Extra";

		/* DEBUG CODE */
		if (testConnections)
		{
			for (int i = 0; i < 6; i++)
			{
				string += ":" + this.renderConnection[i] + (this.getNetworkConnections()[i] != null ? "T" : "F");
			}
		}
		if (testNetwork)
		{
			string += " " + this.getTileNetwork().toString();
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
		TileEntity tile = VectorHelper.getTileEntityFromSide(this.worldObj, new Vector3(this), from);
		return this.getTileNetwork().addFluidToNetwork(tile, resource, doFill);
	}

	@Override
	public int fill(int tankIndex, LiquidStack resource, boolean doFill)
	{
		if (tankIndex != 0 || resource == null || !this.getColor().isValidLiquid(resource))
		{
			return 0;
		}
		return this.getTileNetwork().addFluidToNetwork(this, resource, doFill);
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
			if (tileEntity instanceof ITileConnector)
			{
				if (((ITileConnector) tileEntity).canPipeConnect(this, side))
				{
					if (tileEntity instanceof INetworkPipe)
					{
						if (((INetworkPipe) tileEntity).getColor() == this.getColor())
						{
							this.getTileNetwork().merge(((INetworkPipe) tileEntity).getTileNetwork());
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
	public void updateNetworkConnections()
	{

		if (this.worldObj != null && !this.worldObj.isRemote)
		{

			int[] previousConnections = this.renderConnection.clone();
			this.connectedBlocks = new TileEntity[6];

			for (int i = 0; i < 6; i++)
			{
				ForgeDirection dir = ForgeDirection.getOrientation(i);
				this.validateConnectionSide(this.worldObj.getBlockTileEntity(xCoord + dir.offsetX, yCoord + dir.offsetY, zCoord + dir.offsetZ), dir);

				this.renderConnection[i] = this.connectedBlocks[i] instanceof TileEntityTank ? 2 : (this.connectedBlocks[i] instanceof INetworkPipe ? 1 : this.connectedBlocks[i] != null ? 3 : 0);

				if (this.connectedBlocks[i] instanceof TileEntityTank)
				{
					ITankContainer tankContainer = (ITankContainer) this.connectedBlocks[i];
					this.getTileNetwork().addEntity(tankContainer);
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
	public boolean canPipeConnect(TileEntity entity, ForgeDirection dir)
	{
		return entity != null;
	}

	@Override
	public double getMaxPressure(ForgeDirection side)
	{
		return 350;
	}

	@Override
	public PipeNetwork getTileNetwork()
	{
		if (this.pipeNetwork == null)
		{
			this.setNetwork(new PipeNetwork(this.getColor(), this));
		}
		return this.pipeNetwork;
	}

	@Override
	public void setNetwork(PipeNetwork network)
	{
		this.pipeNetwork = network;
	}

	@Override
	public int getMaxFlowRate(LiquidStack stack, ForgeDirection side)
	{
		return FluidHelper.getDefaultFlowRate(stack) * 3;
	}

	@Override
	public boolean onOverPressure(Boolean damageAllowed)
	{
		if (damageAllowed)
		{
			worldObj.setBlockMetadataWithNotify(xCoord, yCoord, yCoord, 0, 0);
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
	public TileEntity[] getNetworkConnections()
	{
		return this.connectedBlocks;
	}

	@Override
	public int getTankSize()
	{
		return LiquidContainerRegistry.BUCKET_VOLUME * 3;
	}

	@Override
	public ILiquidTank getTank()
	{
		return this.fakeTank;
	}

	@Override
	public void setTankContent(LiquidStack stack)
	{
		this.fakeTank.setLiquid(stack);

	}
}
