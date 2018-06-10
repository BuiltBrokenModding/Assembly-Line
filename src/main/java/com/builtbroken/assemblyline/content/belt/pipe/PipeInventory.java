package com.builtbroken.assemblyline.content.belt.pipe;

import com.builtbroken.assemblyline.content.belt.TilePipeBelt;
import com.builtbroken.assemblyline.content.belt.pipe.data.BeltSideState;
import com.builtbroken.mc.api.tile.node.IExternalInventory;
import com.builtbroken.mc.prefab.inventory.BasicInventory;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.ArrayList;

/**
 * Version of the basic ISidedInventory that is designed to be used as a replacement for
 * the conventional inventory used in machines.
 *
 * @author Darkguardsman
 */
public class PipeInventory extends BasicInventory implements IExternalInventory, ISidedInventory
{
    public static final int[] EMPTY = new int[0];
    /**
     * Host tileEntity
     */
    protected TilePipeBelt host;

    private ForgeDirection rotation;
    private int[][] accessibleSlots;

    public PipeInventory(TilePipeBelt inv)
    {
        super(inv.type.inventorySize);
        this.host = inv;
    }

    @Override
    public int getSizeInventory()
    {
        return host.type.inventorySize;
    }

    @Override
    public boolean isItemValidForSlot(int i, ItemStack itemstack)
    {
        return i < this.getSizeInventory();
    }

    @Override
    public int[] getAccessibleSlotsFromSide(int accessSide) //TODO add a way to get per side
    {
        if (rotation == null || rotation != host.getDirection())
        {
            rotation = host.getDirection();
            accessibleSlots = new int[6][]; //TODO static cache to improve load time

            for(int side = 0; side < 6; side++)
            {
                ArrayList<Integer> list = new ArrayList();
                for(BeltSideState state : host.getBeltStateMap().values())
                {
                    if(state != null && state.side.ordinal() == side)
                    {
                        list.add(state.slotID);
                    }
                }
                if(!list.isEmpty())
                {
                    accessibleSlots[side] = new int[list.size()];
                    for(int y = 0; y < list.size(); y++)
                    {
                        accessibleSlots[side][y] = list.get(y);
                    }
                }
            }
        }
        return accessibleSlots[accessSide] != null ? accessibleSlots[accessSide] : EMPTY;
    }

    @Override
    public boolean canInsertItem(int i, ItemStack itemstack, int j)
    {
        return this.isItemValidForSlot(i, itemstack) && host.canStore(itemstack, i, ForgeDirection.getOrientation(j));
    }

    @Override
    public boolean canExtractItem(int i, ItemStack itemstack, int j)
    {
        return host.canRemove(itemstack, i, ForgeDirection.getOrientation(j));
    }

    @Override
    public int getInventoryStackLimit()
    {
        return 1; //TODO increase for higher level belts
    }

    @Override
    public void markDirty()
    {

    }

    @Override
    protected void onInventoryChanged(int slot, ItemStack prev, ItemStack item)
    {
        host.onInventoryChanged(slot, prev, item);
    }

    @Override
    public boolean isUseableByPlayer(EntityPlayer par1EntityPlayer)
    {
        return true;
    }

    @Override
    public void clear()
    {
        this.inventoryMap.clear();
    }
}
