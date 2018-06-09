package com.builtbroken.assemblyline.api;

import com.builtbroken.mc.imp.transform.rotation.EulerAngle;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.ForgeDirection;

/**
 * Interface that allows controlling how robotic arms access inventory
 *
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 6/9/2018.
 */
public interface IInserterAccess
{
    /**
     * Called to take an item from the tile to place into the
     * inserter's hand.
     *
     * @param angle  - rotation of the arm
     * @param side   - side being access of this tile
     * @param count  - number of items to take
     * @param remove - true to take the item, false to simulate
     * @return stack to give to the inserter
     */
    ItemStack takeInserterItem(EulerAngle angle, ForgeDirection side, int count, boolean remove);

    /**
     * Called to give an item to the tile to place from the
     * inserter's hand.
     *
     * @param angle  - rotation of the arm
     * @param side   - side being access of this tile
     * @param stack  - stack to insert, do not edit
     * @param doInsert - true to insert item, false to simulate
     * @return stack left over after insert
     */
    ItemStack giveInserterItem(EulerAngle angle, ForgeDirection side, ItemStack stack, boolean doInsert);
}
