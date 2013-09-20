package dark.assembly.common.machine;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Icon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import universalelectricity.core.vector.Vector3;
import universalelectricity.prefab.tile.IRotatable;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import dark.assembly.common.AssemblyLine;
import dark.core.registration.ModObjectRegistry.BlockBuildData;

public class BlockTurntable extends BlockAssembly
{
    private Icon top;

    public BlockTurntable()
    {
        super(new BlockBuildData(BlockTurntable.class, "turntable", Material.piston));
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IconRegister iconReg)
    {
        super.registerIcons(iconReg);
        this.top = iconReg.registerIcon(AssemblyLine.instance.PREFIX + "turntable");
    }

    @Override
    public void updateTick(World world, int x, int y, int z, Random par5Random)
    {
        this.updateTurntableState(world, x, y, z);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public Icon getBlockTexture(IBlockAccess world, int x, int y, int z, int side)
    {
        if (side == ForgeDirection.UP.ordinal())
            return this.top;
        return this.machine_icon;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public Icon getIcon(int side, int meta)
    {
        if (side == ForgeDirection.UP.ordinal())
            return this.top;
        return this.machine_icon;
    }

    public static int determineOrientation(World world, int x, int y, int z, EntityPlayer entityPlayer)
    {
        if (MathHelper.abs((float) entityPlayer.posX - x) < 2.0F && MathHelper.abs((float) entityPlayer.posZ - z) < 2.0F)
        {
            double var5 = entityPlayer.posY + 1.82D - entityPlayer.yOffset;

            if (var5 - y > 2.0D)
            {
                return 1;
            }

            if (y - var5 > 0.0D)
            {
                return 0;
            }
        }

        int var7 = MathHelper.floor_double((entityPlayer.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;
        return var7 == 0 ? 2 : (var7 == 1 ? 5 : (var7 == 2 ? 3 : (var7 == 3 ? 4 : 0)));
    }

    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase par5EntityLiving, ItemStack stack)
    {
        int metadata = determineOrientation(world, x, y, z, (EntityPlayer) par5EntityLiving);
        world.setBlockMetadataWithNotify(x, y, z, metadata, 3);

        world.scheduleBlockUpdate(x, y, z, this.blockID, 20);
    }

    @Override
    public void onNeighborBlockChange(World world, int x, int y, int z, int side)
    {
        world.scheduleBlockUpdate(x, y, z, this.blockID, 20);
    }

    private void updateTurntableState(World world, int x, int y, int z)
    {
        if (world.isBlockIndirectlyGettingPowered(x, y, z))
        {
            try
            {
                ForgeDirection direction = ForgeDirection.getOrientation(world.getBlockMetadata(x, y, z));
                Vector3 position = new Vector3(x, y, z);
                position.modifyPositionFromSide(direction);

                IRotatable rotatable = null;
                TileEntity tileEntity = position.getTileEntity(world);
                int blockID = position.getBlockID(world);

                if (tileEntity instanceof IRotatable)
                {
                    rotatable = ((IRotatable) tileEntity);
                }
                else if (Block.blocksList[blockID] instanceof IRotatable)
                {
                    rotatable = ((IRotatable) Block.blocksList[blockID]);
                }

                if (rotatable != null)
                {
                    int newDir = ((IRotatable) tileEntity).getDirection().ordinal();
                    newDir++;

                    while (newDir >= 6)
                    {
                        newDir -= 6;
                    }

                    while (newDir < 0)
                    {
                        newDir += 6;
                    }

                    rotatable.setDirection(ForgeDirection.getOrientation(newDir));

                    world.markBlockForUpdate(position.intX(), position.intY(), position.intZ());
                    world.playSoundEffect(x + 0.5D, y + 0.5D, z + 0.5D, "tile.piston.in", 0.5F, world.rand.nextFloat() * 0.15F + 0.6F);
                }
            }
            catch (Exception e)
            {
                System.out.println("Failed to rotate:");
                e.printStackTrace();
            }
        }
    }

    @Override
    public TileEntity createNewTileEntity(World world)
    {
        // TODO Auto-generated method stub
        return null;
    }
}
