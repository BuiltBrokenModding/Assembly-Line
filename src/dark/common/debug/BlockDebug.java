package dark.common.debug;

import java.util.List;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Icon;
import net.minecraft.world.World;
import net.minecraftforge.common.Configuration;
import dark.core.DarkMain;
import dark.prefab.BlockMachine;

public class BlockDebug extends BlockMachine
{
    Icon load, source, vod, fluid;

    public static enum debugBlocks
    {
        INF_POWER("infSource", TileEntityInfSupply.class),
        INF_FLUID("infFluid", TileEntityInfFluid.class),
        VOID("void", TileEntityVoid.class),
        INF_LOAD("infLoad", TileEntityInfLoad.class);

        public String name;
        public Class<? extends TileEntity> clazz;

        private debugBlocks(String name, Class<? extends TileEntity> clazz)
        {
            this.name = name;
            this.clazz = clazz;
        }
    }

    public BlockDebug(int blockID, Configuration config)
    {
        super("DebugBlock", config, blockID, Material.clay);
        this.setCreativeTab(CreativeTabs.tabRedstone);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void registerIcons(IconRegister iconReg)
    {
        super.registerIcons(iconReg);
        this.source = iconReg.registerIcon(DarkMain.getInstance().PREFIX + "infSource");
        this.load = iconReg.registerIcon(DarkMain.getInstance().PREFIX + "infLoad");
        this.vod = iconReg.registerIcon(DarkMain.getInstance().PREFIX + "void");
        this.fluid = iconReg.registerIcon(DarkMain.getInstance().PREFIX + "infFluid");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public Icon getIcon(int side, int meta)
    {
        switch (meta)
        {
            case 0:
                return this.source;
            case 1:
                return this.fluid;
            case 2:
                return this.vod;
            case 3:
                return this.load;
            default:
                return this.blockIcon;
        }
    }

    @Override
    public TileEntity createTileEntity(World world, int metadata)
    {
        if (metadata < debugBlocks.values().length)
        {
            try
            {
                return debugBlocks.values()[metadata].clazz.newInstance();
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
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void getSubBlocks(int par1, CreativeTabs par2CreativeTabs, List par3List)
    {
        for (int i = 0; i < debugBlocks.values().length; i++)
        {
            par3List.add(new ItemStack(par1, 1, i));
        }
    }

}
