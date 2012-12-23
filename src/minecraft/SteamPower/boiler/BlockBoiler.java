package dark.SteamPower.boiler;

import java.util.List;

import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import universalelectricity.prefab.BlockMachine;
import dark.Library.Util.MetaGroupingHelper;
import dark.Library.prefab.TileEntityMachine;
import dark.SteamPower.ItemRenderHelperS;

public class BlockBoiler extends BlockMachine
{

    /**
     * Quick enum to help sort out meta data info
     */
    public enum Boilers
    {
        Basic("Boiler", TileEntityBoiler.class, -1),
        e("", null, -1),
        ee("", null, -1),
        eee("", null, -1);

        public String name;
        public Class<? extends TileEntity> ent;
        public int gui;

        private Boilers(String name, Class<? extends TileEntity> tileEntity, int gui)
        {
            this.name = name;
            this.gui = gui;
            this.ent = tileEntity;
        }
    }

    public BlockBoiler(int par1)
    {
        super("Boilers", par1, Material.iron);
        this.setRequiresSelfNotify();
        this.setCreativeTab(CreativeTabs.tabBlock);
        this.setHardness(1f);
        this.setResistance(3f);
    }

    @Override
    public int damageDropped(int metadata)
    {
        return MetaGroupingHelper.getGroupStartMeta(MetaGroupingHelper.getGrouping(metadata));
    }
    @Override
    public TileEntity createNewTileEntity(World var1, int meta)
    {
        try
        {
            return Boilers.values()[MetaGroupingHelper.getGrouping(meta)].ent.newInstance();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Called when the block is placed in the world.
     */
    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLiving user)
    {
        int angle = MathHelper.floor_double((user.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;
        int metadata = world.getBlockMetadata(x, y, z);
        // world.setBlockMetadata(x, y, z, angle);
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
