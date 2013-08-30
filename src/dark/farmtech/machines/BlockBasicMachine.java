package dark.farmtech.machines;

import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Icon;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockBasicMachine extends BlockFT
{
    Icon generic_side, wood_side, box_Top;

    enum basicMachine
    {
        COMP_BOX("compostBox", TileEntityCompBox.class);
        String name;
        Class<? extends TileEntity> tile;

        private basicMachine(String name, Class<? extends TileEntity> tile)
        {
            this.name = name;
            this.tile = tile;
        }

    }

    public BlockBasicMachine(String name, int blockID, Material material)
    {
        super(name, blockID, material);
        // TODO Auto-generated constructor stub
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void registerIcons(IconRegister iconReg)
    {
        super.registerIcons(iconReg);
        //this.source = iconReg.registerIcon(DarkMain.getInstance().PREFIX + "infSource");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public Icon getIcon(int side, int meta)
    {
        switch (meta)
        {
            default:
                return this.blockIcon;
        }
    }

    @Override
    public TileEntity createTileEntity(World world, int metadata)
    {
        if (metadata < basicMachine.values().length)
        {
            try
            {
                return basicMachine.values()[metadata].tile.newInstance();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        return super.createTileEntity(world, metadata);
    }

    @Override
    public TileEntity createNewTileEntity(World world)
    {
        return null;
    }

}
