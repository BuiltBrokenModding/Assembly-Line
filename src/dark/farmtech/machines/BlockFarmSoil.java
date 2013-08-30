package dark.farmtech.machines;

import java.util.List;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.util.Icon;
import dark.farmtech.FarmTech;

/** Generic block set containing farm blocks: mulch, fertilizer, fertile dirt, mud
 *
 * mulch/fertilizer -> fertileDirt -> mud -> dirt
 *
 * @mulch is a decor version of fertilizer made from
 * wood. decays very slowly when actually used for crops. Design is to be used with small plant for
 * decor
 *
 * @fertilizer enriches the soil and is used to grow crops faster fertilizer can be created
 * from anything using several means
 *
 * @author darkguardsman */
public class BlockFarmSoil extends Block
{
    Icon mulch, mulch_top, fertilizer, fertilizer_top, fertileDirt_top, mud, mud_top;

    public BlockFarmSoil(int blockID)
    {
        super(FarmTech.CONFIGURATION.getBlock("Soil", blockID).getInt(), Material.clay);
        this.setUnlocalizedName("FarmBlock");
        this.setCreativeTab(FarmTech.TabFarmTech);
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
    public void getSubBlocks(int par1, CreativeTabs par2CreativeTabs, List par3List)
    {

    }

}
