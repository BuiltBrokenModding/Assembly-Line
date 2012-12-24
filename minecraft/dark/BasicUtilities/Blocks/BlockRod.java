package dark.BasicUtilities.Blocks;

import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import dark.BasicUtilities.BasicUtilitiesMain;
import dark.BasicUtilities.BlockRenderHelper;
import dark.BasicUtilities.PipeTab;
import dark.BasicUtilities.Tile.TileEntityRod;

public class BlockRod extends universalelectricity.prefab.BlockMachine
{

    public BlockRod(int par1)
    {
        super("MechanicRod", par1, Material.iron);
        this.setCreativeTab(PipeTab.INSTANCE);
        this.setHardness(1f);
        this.setResistance(5f);
    }

    @Override
    public int damageDropped(int metadata)
    {
        return 0;
    }

    @Override
    public void onBlockPlacedBy(World world, int i, int j, int k, EntityLiving player)
    {
        int angle = MathHelper.floor_double((player.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;
        int meta = 0;
        ForgeDirection idr;
        int dZ = 0;
        int dX = 0;
        switch (angle)
        {
            case 0:
                meta = 2;
                dZ--;
                break;
            case 1:
                meta = 5;
                dX--;
                break;
            case 2:
                meta = 3;
                dZ++;
                break;
            case 3:
                meta = 4;
                dX++;
                break;
        }
        world.setBlockAndMetadataWithUpdate(i, j, k, blockID, meta, true);
    }

    @Override
    public boolean onUseWrench(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ)
    {
        int meta = world.getBlockMetadata(x, y, z);
        if (meta >= 5)
        {
            world.setBlockMetadataWithNotify(x, y, z, 0);
        }
        else
        {
            world.setBlockMetadataWithNotify(x, y, z, meta + 1);
        }
        return true;
    }

    @Override
    public TileEntity createNewTileEntity(World var1)
    {
        return new TileEntityRod();
    }

    public boolean isOpaqueCube()
    {
        return false;
    }
    @Override
    public ItemStack getPickBlock(MovingObjectPosition target, World world, int x, int y, int z)
    {
        return new ItemStack(BasicUtilitiesMain.rod,1,0);        
    }
    /**
     * If this block doesn't render as an ordinary block it will return False
     * (examples: signs, buttons, stairs, etc)
     */
    public boolean renderAsNormalBlock()
    {
        return false;
    }

    /**
     * The type of render function that is called for this block
     */
    public int getRenderType()
    {
        return BlockRenderHelper.renderID;
    }

}
