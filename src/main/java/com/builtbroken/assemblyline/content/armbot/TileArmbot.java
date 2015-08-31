package com.builtbroken.assemblyline.content.armbot;

import com.builtbroken.mc.prefab.tile.Tile;
import net.minecraft.block.material.Material;

/**
 * Created by DarkGuardsman on 8/31/2015.
 */
public class TileArmbot extends Tile
{
    public TileArmbot()
    {
        super("armbot", Material.iron);
    }

    @Override
    public Tile newTile()
    {
        return new TileArmbot();
    }
}
