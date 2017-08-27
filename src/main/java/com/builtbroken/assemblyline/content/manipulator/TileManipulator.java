package com.builtbroken.assemblyline.content.manipulator;

import com.builtbroken.assemblyline.content.belt.TileBelt;
import com.builtbroken.mc.imp.transform.vector.Pos;
import com.builtbroken.mc.prefab.tile.Tile;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.tileentity.TileEntity;

/**
 * Created by DarkGuardsman on 8/31/2015.
 */
public class TileManipulator extends TileBelt
{
    public TileManipulator()
    {
        super("manipulator");
    }

    @Override
    protected void moveItemToNextBlock()
    {
        Pos pos = toPos().add(facingDirection);
        TileEntity tile = pos.getTileEntity(oldWorld());

        if (tile instanceof ISidedInventory)
        {

        } else if (tile instanceof IInventory)
        {

        } else
        {
            super.moveItemToNextBlock();
        }
    }

    @Override
    public Tile newTile()
    {
        return new TileManipulator();
    }
}
