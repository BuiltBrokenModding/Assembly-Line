package dark.core.common.machines;

import java.io.DataInputStream;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.Packet;
import net.minecraftforge.common.ForgeDirection;
import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.common.network.Player;
import dark.core.common.machines.BlockBasicMachine.BasicMachineData;
import dark.core.network.PacketHandler;
import dark.core.prefab.machine.TileEntityEnergyMachine;

public class TileEntityBatteryBox extends TileEntityEnergyMachine
{
    int slotBatteryIn = 0, slotBatteryOut = 1;
    public final Set<EntityPlayer> playersUsing = new HashSet<EntityPlayer>();

    public TileEntityBatteryBox()
    {
        this.invSlots = 2;
        this.MAX_WATTS = 2500;
    }

    @Override
    public void updateEntity()
    {
        super.updateEntity();

        if (!this.worldObj.isRemote)
        {
            this.recharge(this.getInventory().getStackInSlot(this.slotBatteryIn));
            this.discharge(this.getInventory().getStackInSlot(this.slotBatteryOut));
        }

        /** Gradually lose energy. */
        this.setEnergyStored(this.getEnergyStored() - 0.00005f);
    }

    @Override
    public void sendGUIPacket(EntityPlayer player)
    {
        if (!this.worldObj.isRemote)
        {
            PacketDispatcher.sendPacketToPlayer(getDescriptionPacket(), (Player) player);
        }
    }

    @Override
    public Packet getDescriptionPacket()
    {
        return PacketHandler.instance().getPacket(this.getChannel(), this, "energy", this.getEnergyStored());
    }

    @Override
    public boolean simplePacket(String id, DataInputStream dis, EntityPlayer player)
    {
        try
        {
            if (!super.simplePacket(id, dis, player) && this.worldObj.isRemote)
            {
                if (id.equalsIgnoreCase("energy"))
                {
                    this.setEnergyStored(dis.readFloat());
                    return true;
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean isItemValidForSlot(int slotID, ItemStack itemstack)
    {
        return this.isBatteryItem(itemstack);
    }

    @Override
    public int[] getAccessibleSlotsFromSide(int slotID)
    {
        return new int[] { 0, 1 };
    }

    @Override
    public boolean canInsertItem(int slotID, ItemStack itemstack, int side)
    {
        return this.isItemValidForSlot(slotID, itemstack);
    }

    @Override
    public boolean canExtractItem(int slotID, ItemStack itemstack, int side)
    {
        return this.isItemValidForSlot(slotID, itemstack);
    }

    @Override
    public float getRequest(ForgeDirection direction)
    {
        return getInputDirections().contains(direction) ? this.getMaxEnergyStored() - this.getEnergyStored() : 0;
    }

    @Override
    public float getProvide(ForgeDirection direction)
    {
        return getOutputDirections().contains(direction) ? Math.min(1.3F, this.getEnergyStored()) : 0;
    }

    @Override
    public EnumSet<ForgeDirection> getInputDirections()
    {
        return EnumSet.of(ForgeDirection.getOrientation(this.getBlockMetadata() - BasicMachineData.BATTERY_BOX.startMeta + 2).getOpposite(), ForgeDirection.UNKNOWN);
    }

    @Override
    public EnumSet<ForgeDirection> getOutputDirections()
    {
        return EnumSet.of(ForgeDirection.getOrientation(this.getBlockMetadata() - BasicMachineData.BATTERY_BOX.startMeta + 2), ForgeDirection.UNKNOWN);
    }
}
