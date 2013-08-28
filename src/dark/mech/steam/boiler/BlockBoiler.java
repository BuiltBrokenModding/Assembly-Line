package dark.mech.steam.boiler;

import java.util.List;

import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLiving;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import dark.core.blocks.BlockMachine;
import dark.mech.steam.ItemRenderHelperS;
import dark.mech.steam.SteamPowerMain;

public class BlockBoiler extends BlockMachine
{

    public BlockBoiler(int par1)
    {
        super("Boilers", SteamPowerMain.config, par1, Material.iron);
        this.setCreativeTab(CreativeTabs.tabBlock);
        this.setHardness(1f);
        this.setResistance(3f);
    }

    @Override
    public int damageDropped(int metadata)
    {
        return 0;
    }

    @Override
    public TileEntity createNewTileEntity(World var1)
    {
        return new TileEntityBoiler();
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
    public int getRenderType()
    {
        return ItemRenderHelperS.renderID;
    }

    @Override
    public void getSubBlocks(int par1, CreativeTabs par2CreativeTabs, List par3List)
    {
        par3List.add(new ItemStack(par1, 1, 0));
    }
}
