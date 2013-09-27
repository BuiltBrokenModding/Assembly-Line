package dark.core.common.machines;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.packet.Packet;
import net.minecraftforge.common.ForgeDirection;
import universalelectricity.core.item.IItemElectric;

import com.google.common.io.ByteArrayDataInput;

import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.common.network.Player;
import cpw.mods.fml.common.registry.LanguageRegistry;
import dark.core.network.PacketHandler;
import dark.core.prefab.machine.TileEntityEnergyMachine;

public class TileEntityElectricFurnace extends TileEntityEnergyMachine
{
    int batterySlot = 0, inputSlot = 1, outputSlot = 2;

    /** The amount of processing time required. */
    public static final int PROCESS_TIME_REQUIRED = 130;

    /** The amount of ticks this machine has been processing. */
    public int processTicks = 0;

    public TileEntityElectricFurnace()
    {
        super(0.5f);
    }

    @Override
    public void updateEntity()
    {
        super.updateEntity();

        this.discharge(this.getInventory().getStackInSlot(this.batterySlot));

        /** Attempts to smelt an item. */
        if (!this.worldObj.isRemote)
        {
            if (this.canProcess())
            {
                if (this.getEnergyStored() >= WATTS_PER_TICK)
                {
                    if (this.processTicks == 0)
                    {
                        this.processTicks = PROCESS_TIME_REQUIRED;
                    }
                    else if (this.processTicks > 0)
                    {
                        this.processTicks--;

                        /** Process the item when the process timer is done. */
                        if (this.processTicks < 1)
                        {
                            this.smeltItem();
                            this.processTicks = 0;
                        }
                    }
                    else
                    {
                        this.processTicks = 0;
                    }
                }
                else
                {
                    this.processTicks = 0;
                }

                this.setEnergyStored(this.getEnergyStored() - WATTS_PER_TICK);
            }
            else
            {
                this.processTicks = 0;
            }
        }
    }

    @Override
    public float getRequest(ForgeDirection direction)
    {
        if (this.canProcess())
        {
            return WATTS_PER_TICK;
        }
        else
        {
            return 0;
        }
    }

    @Override
    public Packet getDescriptionPacket()
    {
        return PacketHandler.instance().getPacket(this.getChannel(), this, "processTicks", this.processTicks);
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
    public boolean simplePacket(String id, ByteArrayDataInput dis, Player player)
    {
        try
        {
            if (this.worldObj.isRemote)
            {
                if (id.equalsIgnoreCase("processTicks"))
                {
                    this.processTicks = dis.readInt();
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return false;
    }

    /** @return Is this machine able to process its specific task? */
    public boolean canProcess()
    {
        if (FurnaceRecipes.smelting().getSmeltingResult(this.getInventory().getStackInSlot(1)) == null)
        {
            return false;
        }

        if (this.getInventory().getStackInSlot(1) == null)
        {
            return false;
        }

        if (this.getInventory().getStackInSlot(this.outputSlot) != null)
        {
            if (!this.getInventory().getStackInSlot(this.outputSlot).isItemEqual(FurnaceRecipes.smelting().getSmeltingResult(this.getInventory().getStackInSlot(1))))
            {
                return false;
            }

            if (this.getInventory().getStackInSlot(this.outputSlot).stackSize + 1 > 64)
            {
                return false;
            }
        }

        return true;
    }

    /** Turn one item from the furnace source stack into the appropriate smelted item in the furnace
     * result stack */
    public void smeltItem()
    {
        if (this.canProcess())
        {
            ItemStack resultItemStack = FurnaceRecipes.smelting().getSmeltingResult(this.getInventory().getStackInSlot(this.inputSlot));

            if (this.getInventory().getStackInSlot(this.outputSlot) == null)
            {
                this.getInventory().getContainedItems()[this.outputSlot] = resultItemStack.copy();
            }
            else if (this.getInventory().getStackInSlot(this.outputSlot).isItemEqual(resultItemStack))
            {
                this.getInventory().getContainedItems()[this.outputSlot].stackSize++;
            }

            this.getInventory().getStackInSlot(1).stackSize--;

            if (this.getInventory().getStackInSlot(this.inputSlot).stackSize <= 0)
            {
                this.getInventory().getContainedItems()[this.inputSlot] = null;
            }
        }
    }

    /** Reads a tile entity from NBT. */
    @Override
    public void readFromNBT(NBTTagCompound par1NBTTagCompound)
    {
        super.readFromNBT(par1NBTTagCompound);
        this.processTicks = par1NBTTagCompound.getInteger("smeltingTicks");
    }

    /** Writes a tile entity to NBT. */
    @Override
    public void writeToNBT(NBTTagCompound par1NBTTagCompound)
    {
        super.writeToNBT(par1NBTTagCompound);
        par1NBTTagCompound.setInteger("smeltingTicks", this.processTicks);
    }

    @Override
    public String getInvName()
    {
        return LanguageRegistry.instance().getStringLocalization("gui.electricfurnace.name");
    }

    /** Returns true if automation is allowed to insert the given stack (ignoring stack size) into
     * the given slot. */
    @Override
    public boolean isItemValidForSlot(int slotID, ItemStack itemStack)
    {
        return slotID == 1 ? FurnaceRecipes.smelting().getSmeltingResult(itemStack) != null : (slotID == 0 ? itemStack.getItem() instanceof IItemElectric : false);
    }

    /** Get the size of the side inventory. */
    @Override
    public int[] getAccessibleSlotsFromSide(int side)
    {
        return side == 0 ? new int[] { 2 } : (side == 1 ? new int[] { 0, 1 } : new int[] { 0 });
    }

    @Override
    public boolean canInsertItem(int slotID, ItemStack par2ItemStack, int par3)
    {
        return this.isItemValidForSlot(slotID, par2ItemStack);
    }

    @Override
    public boolean canExtractItem(int slotID, ItemStack par2ItemStack, int par3)
    {
        return slotID == 2;
    }

    @Override
    public float getProvide(ForgeDirection direction)
    {
        return 0;
    }
}
