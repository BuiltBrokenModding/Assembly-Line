package dark.machines.common.blocks;

import java.io.IOException;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.packet.Packet;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fluids.FluidStack;

import com.google.common.io.ByteArrayDataInput;

import cpw.mods.fml.common.network.Player;
import dark.core.network.ISimplePacketReceiver;
import dark.core.network.PacketHandler;
import dark.machines.common.DarkMain;

public class TileEntityGasBlock extends TileEntity implements ISimplePacketReceiver
{
    private FluidStack stack = null;

    public FluidStack getFluidStack()
    {
        return stack;
    }

    public void setStack(FluidStack stack)
    {
        this.stack = stack;
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt)
    {
        super.readFromNBT(nbt);
        this.stack = FluidStack.loadFluidStackFromNBT(nbt.getCompoundTag("Fluid"));
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt)
    {
        super.writeToNBT(nbt);
        nbt.setCompoundTag("fluid", this.getFluidStack().writeToNBT(new NBTTagCompound()));
    }

    @Override
    public boolean canUpdate()
    {
        return false;
    }

    @Override
    public boolean simplePacket(String id, ByteArrayDataInput data, Player player)
    {
        try
        {
            if (id.equalsIgnoreCase("Desc"))
            {
                this.stack = FluidStack.loadFluidStackFromNBT(PacketHandler.instance().readNBTTagCompound(data));
                return true;
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public Packet getDescriptionPacket()
    {
        if (this.stack != null)
        {
            return PacketHandler.instance().getTilePacket(DarkMain.CHANNEL, this, "Desc", this.stack.writeToNBT(new NBTTagCompound()));
        }
        return null;
    }
}
