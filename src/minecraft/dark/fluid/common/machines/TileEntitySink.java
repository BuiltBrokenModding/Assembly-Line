package dark.fluid.common.machines;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidStack;
import universalelectricity.prefab.network.IPacketReceiver;
import universalelectricity.prefab.network.PacketManager;

import com.google.common.io.ByteArrayDataInput;

import dark.core.api.ColorCode;
import dark.fluid.common.FluidMech;
import dark.fluid.common.prefab.TileEntityFluidStorage;

public class TileEntitySink extends TileEntityFluidStorage implements IPacketReceiver
{
	@Override
	public int getTankSize()
	{
		return FluidContainerRegistry.BUCKET_VOLUME * 2;
	}

	@Override
	public void updateEntity()
	{
		if (!worldObj.isRemote)
		{
			if (ticks % (random.nextInt(5) * 10 + 20) == 0)
			{
				this.worldObj.markBlockForUpdate(this.xCoord, this.yCoord, this.zCoord);
			}
		}
	}

	@Override
	public Packet getDescriptionPacket()
	{
		FluidStack stack = this.tank.getFluid();
		if (stack != null && stack.getFluid() != null)
		{
			return PacketManager.getPacket(FluidMech.CHANNEL, this, stack.writeToNBT(new NBTTagCompound()));
		}
		else
		{
			stack = new FluidStack(0, 0);
			return PacketManager.getPacket(FluidMech.CHANNEL, this, stack.writeToNBT(new NBTTagCompound()));
		}
	}

	@Override
	public void handlePacketData(INetworkManager network, int packetType, Packet250CustomPayload packet, EntityPlayer player, ByteArrayDataInput data)
	{
		try
		{
			this.tank.setFluid(FluidStack.loadFluidStackFromNBT(PacketManager.readNBTTagCompound(data)));
		}
		catch (Exception e)
		{
			e.printStackTrace();
			System.out.print("Fail reading data for Storage tank \n");
		}

	}

	@Override
	public int fill(ForgeDirection side, FluidStack resource, boolean doFill)
	{
		int f = super.fill(side, resource, doFill);
		if (doFill && f > 0)
		{
			this.worldObj.markBlockForUpdate(this.xCoord, this.yCoord, this.zCoord);
		}
		return f;
	}

	@Override
	public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean canFill(ForgeDirection from, Fluid fluid)
	{
		return fluid != null && fluid.getName().equalsIgnoreCase("water") && from != ForgeDirection.UP;
	}

	@Override
	public boolean canDrain(ForgeDirection from, Fluid fluid)
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setColor(Object obj)
	{
	}

	@Override
	public ColorCode getColor()
	{
		return ColorCode.BLUE;
	}
}
