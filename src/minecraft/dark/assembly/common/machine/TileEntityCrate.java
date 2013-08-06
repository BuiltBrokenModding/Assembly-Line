package dark.assembly.common.machine;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraftforge.common.ForgeDirection;
import universalelectricity.prefab.network.IPacketReceiver;
import universalelectricity.prefab.network.PacketManager;

import com.google.common.io.ByteArrayDataInput;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import dark.api.IExternalInv;
import dark.assembly.common.AssemblyLine;
import dark.core.blocks.TileEntityInv;

public class TileEntityCrate extends TileEntityInv implements IPacketReceiver, IExternalInv
{
    /** Collective total stack of all inv slots */
    private ItemStack sampleStack;

    /** delay from last click */
    public long prevClickTime = -1000;

    @Override
    public InventoryCrate getInventory()
    {
        if (this.inventory == null)
        {
            inventory = new InventoryCrate(this);
        }
        return (InventoryCrate) this.inventory;
    }

    @Override
    public boolean canStore(ItemStack stack, int slot, ForgeDirection side)
    {
        return this.sampleStack == null || stack != null && stack.equals(sampleStack);
    }

    @Override
    public boolean canRemove(ItemStack stack, int slot, ForgeDirection side)
    {
        if (slot >= this.getSlotCount())
        {
            return false;
        }
        return true;
    }

    /** Turns the inventory array into a single stack of matching items. This assumes that all items
     * in the crate are the same TODO eject minority items and only keep the majority that are the
     * same to prevent duplication issues
     *
     * @param force - force a rebuild of the inventory from the single stack created */
    public void buildSampleStack(boolean force)
    {
        int count = 0;
        int id = 0;
        int meta = 0;

        boolean rebuildBase = false;

        /* Creates the sample stack that is used as a collective itemstack */
        for (int i = 0; i < this.getInventory().getContainedItems().length; i++)
        {
            ItemStack stack = this.getInventory().getContainedItems()[i];
            if (stack != null && stack.itemID > 0 && stack.stackSize > 0)
            {
                id = this.getInventory().getContainedItems()[i].itemID;
                meta = this.getInventory().getContainedItems()[i].getItemDamage();
                int ss = this.getInventory().getContainedItems()[i].stackSize;

                count += ss;

                if (ss > this.getInventory().getContainedItems()[i].getMaxStackSize())
                {
                    rebuildBase = true;
                }
            }
        }
        if (id == 0 || count == 0)
        {
            this.sampleStack = null;
        }
        else
        {
            this.sampleStack = new ItemStack(id, count, meta);
        }
        /* if one stack is over sized this rebuilds the inv to redistribute the items in the slots */
        if ((rebuildBase || force || this.getInventory().getContainedItems().length > this.getSlotCount()) && this.sampleStack != null)
        {
            this.getInventory().buildInventory(this.sampleStack);
        }
    }

    public ItemStack getSampleStack()
    {
        if (this.sampleStack == null)
        {
            this.buildSampleStack(false);
        }
        return this.sampleStack;
    }

    public void addToStack(ItemStack stack, int amount)
    {
        if (stack != null)
        {
            this.addToStack(new ItemStack(stack.stackSize, amount, stack.getItemDamage()));
        }
    }

    public void addToStack(ItemStack stack)
    {
        if (stack != null)
        {
            this.buildSampleStack(false);
            boolean flag = false;
            if (this.sampleStack == null)
            {
                this.sampleStack = stack;
                flag = true;
            }
            else if (this.sampleStack.isItemEqual(stack))
            {
                this.sampleStack.stackSize += stack.stackSize;
                flag = true;
            }
            if (flag)
            {
                this.getInventory().buildInventory(this.sampleStack);
                if (FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER)
                {
                    PacketManager.sendPacketToClients(this.getDescriptionPacket(), this.worldObj);
                }
            }
        }
    }

    @Override
    public void onInventoryChanged()
    {
        super.onInventoryChanged();

        if (this.worldObj != null)
        {
            if (FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER)
            {
                PacketManager.sendPacketToClients(this.getDescriptionPacket(), this.worldObj);
            }
        }
    }

    public int getSlotCount()
    {
        return TileEntityCrate.getSlotCount(this.getBlockMetadata());
    }

    public static int getSlotCount(int metadata)
    {
        if (metadata >= 2)
        {
            return 256;
        }
        else if (metadata >= 1)
        {
            return 64;
        }

        return 32;
    }

    @Override
    public boolean canUpdate()
    {
        return false;
    }

    @Override
    public void handlePacketData(INetworkManager network, int packetType, Packet250CustomPayload packet, EntityPlayer player, ByteArrayDataInput dataStream)
    {
        if (this.worldObj.isRemote)
        {
            try
            {
                if (dataStream.readBoolean())
                {
                    if (this.sampleStack == null)
                    {
                        this.sampleStack = new ItemStack(dataStream.readInt(), dataStream.readInt(), dataStream.readInt());
                    }
                    else
                    {
                        this.sampleStack.itemID = dataStream.readInt();
                        this.sampleStack.stackSize = dataStream.readInt();
                        this.sampleStack.setItemDamage(dataStream.readInt());
                    }
                }
                else
                {
                    this.sampleStack = null;
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    @Override
    public Packet getDescriptionPacket()
    {
        this.buildSampleStack(false);
        ItemStack stack = this.getSampleStack();
        if (stack != null)
        {
            return PacketManager.getPacket(AssemblyLine.CHANNEL, this, true, stack.itemID, stack.stackSize, stack.getItemDamage());
        }
        else
        {
            return PacketManager.getPacket(AssemblyLine.CHANNEL, this, false);
        }
    }

    /** NBT Data */
    @Override
    public void readFromNBT(NBTTagCompound nbt)
    {
        super.readFromNBT(nbt);
        /* Load inventory old data if present */
        this.getInventory().loadInv(nbt);
        /* Load current two inv methods */
        ItemStack stack = null;
        if (!nbt.hasKey("Items") && nbt.hasKey("itemID") && nbt.hasKey("itemMeta"))
        {
            stack = new ItemStack(nbt.getInteger("itemID"), nbt.getInteger("Count"), nbt.getInteger("itemMeta"));
        }
        else
        {
            stack = ItemStack.loadItemStackFromNBT(nbt.getCompoundTag("stack"));
        }
        /* Only load sample stack if the read stack is valid */
        if (stack != null && stack.itemID != 0 && stack.stackSize > 0)
        {
            this.sampleStack = stack;
            this.getInventory().buildInventory(this.sampleStack);
        }

    }

    @Override
    public void writeToNBT(NBTTagCompound nbt)
    {
        super.writeToNBT(nbt);
        /* Re-Build sample stack for saving */
        this.buildSampleStack(false);
        /* Save sample stack */
        if (this.getSampleStack() != null)
        {
            NBTTagCompound tag = new NBTTagCompound();
            this.getSampleStack().writeToNBT(tag);
            nbt.setCompoundTag("stack", tag);
        }

    }

}
