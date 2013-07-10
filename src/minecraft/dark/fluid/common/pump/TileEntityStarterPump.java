package dark.fluid.common.pump;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidHandler;
import universalelectricity.core.vector.Vector3;
import universalelectricity.core.vector.VectorHelper;
import universalelectricity.prefab.network.IPacketReceiver;

import com.google.common.io.ByteArrayDataInput;

import dark.core.api.ColorCode;
import dark.core.api.IColorCoded;
import dark.core.api.ITileConnector;
import dark.core.api.IToolReadOut;
import dark.core.hydraulic.helpers.FluidHelper;
import dark.core.hydraulic.helpers.FluidRestrictionHandler;
import dark.helpers.MetaGroup;
import dark.library.machine.TileEntityRunnableMachine;

public class TileEntityStarterPump extends TileEntityRunnableMachine implements IPacketReceiver, IToolReadOut, ITileConnector
{
	public final static float WATTS_PER_TICK = 20;
	private double percentPumped = 0.0;

	public int pos = 0;

	public ColorCode color = ColorCode.BLUE;

	ForgeDirection wireConnection = ForgeDirection.EAST;
	ForgeDirection pipeConnection = ForgeDirection.EAST;

	public TileEntityStarterPump()
	{
		super(20);
		// TODO Auto-generated constructor stub
	}

	/** gets the side connection for the wire and pipe */
	public void getConnections()
	{
		int notchMeta = MetaGroup.getFacingMeta(worldObj.getBlockMetadata(xCoord, yCoord, zCoord));

		wireConnection = ForgeDirection.getOrientation(notchMeta);
		pipeConnection = VectorHelper.getOrientationFromSide(wireConnection, ForgeDirection.WEST);

		if (notchMeta == 2 || notchMeta == 3)
		{
			pipeConnection = pipeConnection.getOpposite();
		}
	}

	@Override
	public void initiate()
	{
		this.getConnections();
	}

	@Override
	public void updateEntity()
	{
		super.updateEntity();

		this.getConnections();

		if (!this.worldObj.isRemote && !this.isDisabled())
		{
			if (this.canPump(new Vector3(xCoord, yCoord - 1, zCoord)) && this.canRun())
			{
				if (percentPumped < 10)
				{
					percentPumped++;
				}
				else if (percentPumped >= 10 && this.drainBlock(new Vector3(xCoord, yCoord - 1, zCoord)))
				{
					percentPumped = 0;
				}

				/* DO ANIMATION CHANGE */
				this.pos++;
				if (pos >= 8)
				{
					pos = 0;
				}
			}
			if (this.ticks % 10 == 0)
			{
				// Packet packet = PacketManager.getPacket(FluidMech.CHANNEL, this, color.ordinal(),
				// this.wattsReceived);
				// PacketManager.sendPacketToClients(packet, worldObj, new Vector3(this), 60);
			}
		}

	}

	@Override
	public void handlePacketData(INetworkManager network, int packetType, Packet250CustomPayload packet, EntityPlayer player, ByteArrayDataInput data)
	{
		try
		{
			this.color = ColorCode.get(data.readInt());
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

	}

	@Override
	public float getRequest(ForgeDirection side)
	{
		return this.WATTS_PER_TICK;
	}

	/** checks to see if this pump can pump the selected target block
	 * 
	 * @param x y z - location of the block, use the tileEntities world
	 * @return true if it can pump */
	boolean canPump(Vector3 vec)
	{
		FluidStack stack = FluidHelper.drainBlock(this.worldObj, vec, false);
		return stack != null;
	}

	/** drains the block(removes) at the location given
	 * 
	 * @param loc - vector 3 location
	 * @return true if the block was drained */
	boolean drainBlock(Vector3 loc)
	{
		FluidStack stack = FluidHelper.drainBlock(this.worldObj, loc, false);
		if (FluidRestrictionHandler.isValidLiquid(color, stack.getFluid()) && this.fillAroundTile(stack, false) >= FluidContainerRegistry.BUCKET_VOLUME)
		{
			return this.fillAroundTile(FluidHelper.drainBlock(this.worldObj, loc, true), true) > 0;
		}
		return false;
	}

	public int fillAroundTile(FluidStack stack, boolean doFill)
	{
		if (stack != null && stack.getFluid() != null)
		{
			int amount = stack.amount;
			for (ForgeDirection direction : ForgeDirection.VALID_DIRECTIONS)
			{
				TileEntity entity = new Vector3(this).modifyPositionFromSide(direction).getTileEntity(this.worldObj);
				if (direction != ForgeDirection.DOWN && entity instanceof IFluidHandler)
				{
					amount -= ((IFluidHandler) entity).fill(direction.getOpposite(), FluidHelper.getStack(stack, amount), doFill);
				}
				if (amount <= 0)
				{
					break;
				}
			}
			return amount;
		}
		return 0;
	}

	@Override
	public String getMeterReading(EntityPlayer user, ForgeDirection side, EnumTools tool)
	{
		return String.format("%.2f/%.2f  %f Done", this.getEnergyStored(), this.getMaxEnergyStored(), this.percentPumped);
	}

	@Override
	public boolean canConnect(ForgeDirection direction)
	{
		return direction != ForgeDirection.DOWN;
	}

	@Override
	public boolean canTileConnect(TileEntity entity, ForgeDirection dir)
	{
		if (dir == this.pipeConnection.getOpposite() && entity instanceof IFluidHandler)
		{
			return entity != null && entity instanceof IColorCoded && (((IColorCoded) entity).getColor() == ColorCode.NONE || ((IColorCoded) entity).getColor() == this.color);
		}
		return false;
	}

}
