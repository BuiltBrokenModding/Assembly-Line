package dark.fluid.common.machines;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import universalelectricity.prefab.network.IPacketReceiver;

import com.google.common.io.ByteArrayDataInput;

import dark.api.ColorCode;
import dark.core.common.DarkMain;
import dark.core.network.PacketHandler;
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
        FluidStack stack = new FluidStack(FluidRegistry.WATER, 0);
        if (this.getTank().getFluid() != null)
        {
            stack = this.getTank().getFluid();
        }
        return PacketHandler.instance().getPacket(DarkMain.CHANNEL, this, stack.writeToNBT(new NBTTagCompound()));
    }

    @Override
    public void handlePacketData(INetworkManager network, int packetType, Packet250CustomPayload packet, EntityPlayer player, ByteArrayDataInput data)
    {
        try
        {
            this.getTank().setFluid(FluidStack.loadFluidStackFromNBT(PacketHandler.instance().readNBTTagCompound(data)));
        }
        catch (Exception e)
        {
            e.printStackTrace();
            System.out.println("Fail reading data for fluid sink");
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
    public boolean setColor(Object obj)
    {
        return false;
    }

    @Override
    public ColorCode getColor()
    {
        return ColorCode.BLUE;
    }
}
