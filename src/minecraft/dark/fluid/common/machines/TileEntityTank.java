package dark.fluid.common.machines;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityComparator;
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

import com.google.common.io.ByteArrayDataInput;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import dark.core.api.ColorCode;
import dark.core.api.IColorCoded;
import dark.core.api.IToolReadOut;
import dark.core.hydraulic.helpers.FluidRestrictionHandler;
import dark.core.network.fluid.NetworkFluidContainers;
import dark.core.network.fluid.NetworkFluidTiles;
import dark.core.tile.network.NetworkTileEntities;
import dark.fluid.api.INetworkFluidPart;
import dark.fluid.api.INetworkPipe;
import dark.fluid.common.FluidMech;
import dark.fluid.common.prefab.TileEntityFluidDevice;

public class TileEntityTank extends TileEntityFluidDevice implements ITankContainer, IToolReadOut, IColorCoded, INetworkFluidPart, IPacketReceiver
{
	/* CURRENTLY CONNECTED TILE ENTITIES TO THIS */
	private TileEntity[] connectedBlocks = new TileEntity[6];
	public int[] renderConnection = new int[6];

	private LiquidTank tank = new LiquidTank(this.getTankSize());

	/* NETWORK INSTANCE THAT THIS PIPE USES */
	private NetworkFluidContainers fluidNetwork;

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
		if (this.worldObj.isRemote)
		{
			int id = dataStream.readInt();
			if (id == 0)
			{
				this.tank.setLiquid(new LiquidStack(dataStream.readInt(), dataStream.readInt(), dataStream.readInt()));
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
		LiquidStack stack = new LiquidStack(0, 0, 0);
		if (this.getTank().getLiquid() != null)
		{
			stack = this.getTank().getLiquid();
		}
		return PacketManager.getPacket(FluidMech.CHANNEL, this, 0, stack.itemID, stack.amount, stack.itemMeta, this.renderConnection[0], this.renderConnection[1], this.renderConnection[2], this.renderConnection[3], this.renderConnection[4], this.renderConnection[5]);
	}

	/** gets the current color mark of the pipe */
	@Override
	public ColorCode getColor()
	{
		if (this.worldObj == null)
		{
			return ColorCode.NONE;
		}
		return ColorCode.get(worldObj.getBlockMetadata(xCoord, yCoord, zCoord));
	}

	/** sets the current color mark of the pipe */
	@Override
	public void setColor(Object cc)
	{
	}

	@Override
	public String getMeterReading(EntityPlayer user, ForgeDirection side, EnumTools tool)
	{
		/* DEBUG CODE ACTIVATERS */
		boolean testNetwork = true;

		/* NORMAL OUTPUT */
		String string = "Fluid>" + (this.getTileNetwork() instanceof NetworkFluidTiles ? ((NetworkFluidTiles) this.getTileNetwork()).getNetworkFluid() : "Error");

		if (testNetwork)
		{
			string += "\nNetID>" + this.getTileNetwork().toString();
		}

		return string;
	}

	@Override
	public int fill(ForgeDirection from, LiquidStack resource, boolean doFill)
	{
		return this.fill(0, resource, doFill);
	}

	@Override
	public int fill(int tankIndex, LiquidStack resource, boolean doFill)
	{
		if (tankIndex != 0 || resource == null || !FluidRestrictionHandler.isValidLiquid(this.getColor(), resource) || this.worldObj.isRemote)
		{
			return 0;
		}
		return ((NetworkFluidContainers) this.getTileNetwork()).storeFluidInSystem(resource, doFill);
	}

	@Override
	public LiquidStack drain(ForgeDirection from, int maxDrain, boolean doDrain)
	{
		return this.drain(0, maxDrain, doDrain);
	}

	@Override
	public LiquidStack drain(int tankIndex, int maxDrain, boolean doDrain)
	{
		if (tankIndex != 0 || this.worldObj.isRemote)
		{
			return null;
		}
		return ((NetworkFluidContainers) this.getTileNetwork()).drainFluidFromSystem(maxDrain, doDrain);
	}

	@Override
	public ILiquidTank[] getTanks(ForgeDirection direction)
	{
		return new ILiquidTank[] { ((NetworkFluidContainers) this.getTileNetwork()).combinedStorage() };
	}

	@Override
	public ILiquidTank getTank(ForgeDirection direction, LiquidStack type)
	{
		if (FluidRestrictionHandler.isValidLiquid(this.getColor(), type))
		{
			return ((NetworkFluidContainers) this.getTileNetwork()).combinedStorage();
		}
		return null;
	}

	/** Checks to make sure the connection is valid to the tileEntity
	 * 
	 * @param tileEntity - the tileEntity being checked
	 * @param side - side the connection is too */
	public void validateConnectionSide(TileEntity tileEntity, ForgeDirection side)
	{
		if (!this.worldObj.isRemote)
		{
			this.renderConnection[side.ordinal()] = tileEntity instanceof TileEntityTank && ((TileEntityTank) tileEntity).getColor() == this.getColor() ? 2 : (tileEntity instanceof INetworkPipe ? 1 : tileEntity != null ? 3 : 0);

			if (tileEntity != null)
			{
				if (tileEntity instanceof TileEntityTank)
				{
					if (((TileEntityTank) tileEntity).getColor() == this.getColor())
					{
						this.getTileNetwork().merge(((TileEntityTank) tileEntity).getTileNetwork(), this);
						connectedBlocks[side.ordinal()] = tileEntity;
					}
				}
				if(tileEntity instanceof TileEntityComparator)
				{
					this.worldObj.markBlockForUpdate(tileEntity.xCoord, tileEntity.yCoord, tileEntity.zCoord);
				}
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

			}
			/** Only send packet updates if visuallyConnected changed. */
			if (!Arrays.areEqual(previousConnections, this.renderConnection))
			{
				this.worldObj.markBlockForUpdate(this.xCoord, this.yCoord, this.zCoord);
			}
		}
	}

	@Override
	public boolean canTileConnect(TileEntity entity, ForgeDirection dir)
	{
		return entity != null && entity instanceof ITankContainer;
	}

	@Override
	public NetworkTileEntities getTileNetwork()
	{
		if (this.fluidNetwork == null)
		{
			this.setTileNetwork(new NetworkFluidContainers(this.getColor(), this));
		}
		return this.fluidNetwork;
	}

	@Override
	public void setTileNetwork(NetworkTileEntities network)
	{
		if (network instanceof NetworkFluidContainers)
		{
			this.fluidNetwork = ((NetworkFluidContainers) network);
		}
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
	public void readFromNBT(NBTTagCompound nbt)
	{
		super.readFromNBT(nbt);

		LiquidStack liquid = LiquidStack.loadLiquidStackFromNBT(nbt.getCompoundTag("stored"));
		if (liquid != null)
		{
			tank.setLiquid(liquid);
		}
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt)
	{
		super.writeToNBT(nbt);
		if (this.tank.containsValidLiquid())
		{
			nbt.setTag("stored", this.tank.getLiquid().writeToNBT(new NBTTagCompound()));
		}
	}

	public int getTankSize()
	{
		return LiquidContainerRegistry.BUCKET_VOLUME * 8;
	}

	@Override
	public ILiquidTank getTank()
	{
		if (this.tank == null)
		{
			this.tank = new LiquidTank(this.getTankSize());
		}
		return this.tank;
	}

	@Override
	public void setTankContent(LiquidStack stack)
	{
		if (this.tank == null)
		{
			this.tank = new LiquidTank(this.getTankSize());
		}
		this.tank.setLiquid(stack);
	}

	public int getRedstoneLevel()
	{
		if (this.getTileNetwork() != null && this.getTileNetwork() instanceof NetworkFluidTiles)
		{
			ILiquidTank tank = ((NetworkFluidTiles) this.getTileNetwork()).combinedStorage();
			if (tank != null && tank.getLiquid() != null)
			{
				int max = tank.getCapacity();
				int current = tank.getLiquid().amount;
				double percent = current / max;
				return ((int) (15 * percent));
			}
		}
		return 0;
	}
}
