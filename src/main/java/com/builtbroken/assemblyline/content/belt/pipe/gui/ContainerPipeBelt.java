package com.builtbroken.assemblyline.content.belt.pipe.gui;

import com.builtbroken.assemblyline.content.belt.TilePipeBelt;
import com.builtbroken.assemblyline.content.belt.pipe.BeltType;
import com.builtbroken.mc.prefab.gui.ContainerBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 11/17/2017.
 */
public class ContainerPipeBelt extends ContainerBase<TilePipeBelt>
{
    protected final int id;

    public ContainerPipeBelt(EntityPlayer player, TilePipeBelt node, int id)
    {
        super(player, node);
        this.id = id;

        int x = 30;

        if (id == TilePipeBelt.GUI_MAIN)
        {
            if (node.type == BeltType.NORMAL || node.type == BeltType.LEFT_ELBOW || node.type == BeltType.RIGHT_ELBOW)
            {
                addSlotToContainer(new Slot(node.getInventory(), 0, x + 10, 20));
                addSlotToContainer(new Slot(node.getInventory(), 2, x + 50, 20));
                addSlotToContainer(new Slot(node.getInventory(), 1, x + 90, 20));
            }
            else if(node.type == BeltType.END_CAP)
            {
                addSlotToContainer(new Slot(node.getInventory(), 0, x + 10, 20));
                addSlotToContainer(new Slot(node.getInventory(), 1, x + 50, 20));
            }
            else if (node.type == BeltType.JUNCTION)
            {
                addSlotToContainer(new Slot(node.getInventory(), 1, x + 10, 20));
                addSlotToContainer(new Slot(node.getInventory(), 2, x + 50, 20));
                addSlotToContainer(new Slot(node.getInventory(), 3, x + 90, 20));

                addSlotToContainer(new Slot(node.getInventory(), 0, x + 50, 40));
            }
            else if (node.type == BeltType.INTERSECTION || node.type == BeltType.JUNCTION)
            {
                addSlotToContainer(new Slot(node.getInventory(), 1, x + 30, 40));
                addSlotToContainer(new Slot(node.getInventory(), 2, x + 50, 40));
                addSlotToContainer(new Slot(node.getInventory(), 3, x + 70, 40));
                addSlotToContainer(new Slot(node.getInventory(), 4, x + 50, 20));

                addSlotToContainer(new Slot(node.getInventory(), 0, x + 50, 60));
            }

            addPlayerInventory(player);
        }
    }
}
