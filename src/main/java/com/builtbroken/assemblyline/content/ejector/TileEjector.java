package com.builtbroken.assemblyline.content.ejector;

import com.builtbroken.mc.prefab.tile.Tile;
import net.minecraft.block.material.Material;

/**
 * Created by DarkGuardsman on 8/31/2015.
 */
public class TileEjector extends Tile
{
    public TileEjector()
    {
        super("ejector", Material.iron);
    }

    @Override
    public Tile newTile()
    {
        return new TileEjector();
    }
}
