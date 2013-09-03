package dark.illus.blocks.colored;

import java.util.Random;

import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockColorGlass extends BlockColored
{

    public BlockColorGlass(int id, String name)
    {
        super(name, id, Material.glass);
        this.setCreativeTab(CreativeTabs.tabDecorations);
        this.setHardness(.5f);
        this.setResistance(.5f);
        this.setStepSound(soundGlassFootstep);
    }

    @Override
    public int quantityDropped(Random par1Random)
    {
        return 0;
    }

    @SideOnly(Side.CLIENT)
    public int getRenderBlockPass()
    {
        return 1;
    }

    @Override
    public boolean isOpaqueCube()
    {
        return false;
    }

    @Override
    public boolean renderAsNormalBlock()
    {
        return false;
    }

    @Override
    protected boolean canSilkHarvest()
    {
        return true;
    }
}
