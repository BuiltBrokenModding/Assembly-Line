package dark.fluid.common.machines;

import java.util.List;
import java.util.Set;

import com.builtbroken.common.Pair;

import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import dark.fluid.common.BlockFM;

public class BlockBoiler extends BlockFM
{

    public BlockBoiler()
    {
        super(BlockBoiler.class, "Boilers", Material.iron);
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
    @SideOnly(Side.CLIENT)
    public boolean renderAsNormalBlock()
    {
        return false;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public int getRenderType()
    {
        return -1;
    }

    @Override
    public void getSubBlocks(int par1, CreativeTabs par2CreativeTabs, List par3List)
    {
        par3List.add(new ItemStack(par1, 1, 0));
    }

    @Override
    public void getTileEntities(int blockID, Set<Pair<String, Class<? extends TileEntity>>> list)
    {
        list.add(new Pair<String, Class<? extends TileEntity>>("FMSteamBoiler", TileEntityBoiler.class));

    }
}
