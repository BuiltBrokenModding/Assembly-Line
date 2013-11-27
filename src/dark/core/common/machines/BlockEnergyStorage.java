package dark.core.common.machines;

import java.util.List;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Icon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import universalelectricity.core.UniversalElectricity;
import universalelectricity.core.block.IConductor;
import universalelectricity.core.vector.Vector3;
import dark.core.common.DarkMain;
import dark.core.helpers.MathHelper;
import dark.core.prefab.machine.BlockMachine;

/** Block for energy storage devices
 *
 * @author Rseifert */
public class BlockEnergyStorage extends BlockMachine
{
    public static final int BATTERY_BOX_METADATA = 0;

    public BlockEnergyStorage()
    {
        super(DarkMain.CONFIGURATION, "DMEnergyStorage", UniversalElectricity.machine);
    }

    public Icon getBlockTexture(IBlockAccess world, int x, int y, int z, int side)
    {
        Vector3 vec = new Vector3(x, y, z);
        int meta = vec.getBlockMetadata(world);
        if (side == 0 || side == 1)
        {
            return this.blockIcon;
        }
        if(side == (meta - BlockEnergyStorage.BATTERY_BOX_METADATA + 2))
        {
            return this.iconOutput;
        }
        return vec.clone().modifyPositionFromSide(ForgeDirection.getOrientation(side)).getTileEntity(world) instanceof IConductor ? this.iconInput : this.blockIcon;

    }

    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase entityLiving, ItemStack itemStack)
    {
        super.onBlockPlacedBy(world, x, y, z, entityLiving, itemStack);
        int metadata = world.getBlockMetadata(x, y, z);
        int angle = MathHelper.floor_double((entityLiving.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;
        world.setBlockMetadataWithNotify(x, y, z, ((metadata / 4) * 4) + angle, 3);
    }

    @Override
    public boolean onUseWrench(World world, int x, int y, int z, EntityPlayer entityPlayer, int side, float hitX, float hitY, float hitZ)
    {
        if (world.getBlockMetadata(x, y, z) % 4 < 3)
        {
            world.setBlockMetadataWithNotify(x, y, z, world.getBlockMetadata(x, y, z) + 1, 3);
            return true;
        }
        else
        {
            world.setBlockMetadataWithNotify(x, y, z, world.getBlockMetadata(x, y, z) - 3, 3);
            return true;
        }
    }

    @Override
    public TileEntity createTileEntity(World world, int metadata)
    {
        switch (metadata / 4)
        {
            case 0:
                return new TileEntityBatteryBox();

        }
        return super.createTileEntity(world, metadata);

    }

    @Override
    public void getSubBlocks(int par1, CreativeTabs par2CreativeTabs, List par3List)
    {
        par3List.add(new ItemStack(par1, 1, 0));
    }

    @Override
    public int damageDropped(int metadata)
    {
        return metadata / 4;
    }

    @Override
    public ItemStack getPickBlock(MovingObjectPosition target, World world, int x, int y, int z)
    {
        return new ItemStack(this, 1, (world.getBlockMetadata(x, y, z) / 4) * 4);
    }

}
