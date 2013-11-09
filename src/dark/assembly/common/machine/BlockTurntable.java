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
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import universalelectricity.core.vector.Vector3;
import universalelectricity.prefab.block.IRotatableBlock;
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
    public Icon getIcon(int side, int meta)
    {
        if (side == meta)
        {
            return this.top;
        }
        return this.blockIcon;
    }

    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase entityLiving, ItemStack stack)
    {
        int angle = MathHelper.floor_double((entityLiving.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;
        int change = 3;

        switch (angle)
        {
            case 0:
                change = 2;
                break;

            case 1:
                change = 5;
                break;

            case 2:
                change = 3;
                break;

            case 3:
                change = 4;
                break;
        }

        world.setBlockMetadataWithNotify(x, y, z, change, 3);

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
                ForgeDirection currentDirection = null;

                Vector3 position = new Vector3(x, y, z).modifyPositionFromSide(direction);
                TileEntity tileEntity = position.getTileEntity(world);
                int blockID = position.getBlockID(world);

                if (tileEntity instanceof IRotatable)
                {
                    currentDirection = ((IRotatable) tileEntity).getDirection();
                }
                else if (Block.blocksList[blockID] instanceof IRotatableBlock)
                {
                    currentDirection = ((IRotatableBlock) Block.blocksList[blockID]).getDirection(world, position.intX(), position.intY(), position.intZ());
                }
                else if (Block.blocksList[blockID] != null)
                {
                    Block.blocksList[blockID].rotateBlock(world, x, y, z, direction.getOpposite());
                }
                if (direction != null)
                {
                    if (direction == ForgeDirection.UP || direction == ForgeDirection.DOWN)
                    {
                        if (currentDirection == ForgeDirection.NORTH)
                        {
                            currentDirection = ForgeDirection.EAST;
                        }
                        else if (currentDirection == ForgeDirection.EAST)
                        {
                            currentDirection = ForgeDirection.SOUTH;
                        }
                        else if (currentDirection == ForgeDirection.SOUTH)
                        {
                            currentDirection = ForgeDirection.WEST;
                        }
                        else if (currentDirection == ForgeDirection.WEST)
                        {
                            currentDirection = ForgeDirection.NORTH;
                        }
                    }
                    else if (direction == ForgeDirection.EAST || direction == ForgeDirection.WEST)
                    {
                        if (currentDirection == ForgeDirection.NORTH)
                        {
                            currentDirection = ForgeDirection.UP;
                        }
                        else if (currentDirection == ForgeDirection.UP)
                        {
                            currentDirection = ForgeDirection.SOUTH;
                        }
                        else if (currentDirection == ForgeDirection.SOUTH)
                        {
                            currentDirection = ForgeDirection.DOWN;
                        }
                        else if (currentDirection == ForgeDirection.DOWN)
                        {
                            currentDirection = ForgeDirection.NORTH;
                        }
                    }
                    else if (direction == ForgeDirection.NORTH || direction == ForgeDirection.SOUTH)
                    {
                        if (currentDirection == ForgeDirection.EAST)
                        {
                            currentDirection = ForgeDirection.UP;
                        }
                        else if (currentDirection == ForgeDirection.UP)
                        {
                            currentDirection = ForgeDirection.WEST;
                        }
                        else if (currentDirection == ForgeDirection.WEST)
                        {
                            currentDirection = ForgeDirection.DOWN;
                        }
                        else if (currentDirection == ForgeDirection.DOWN)
                        {
                            currentDirection = ForgeDirection.EAST;
                        }
                    }
                    world.markBlockForUpdate(position.intX(), position.intY(), position.intZ());
                    world.playSoundEffect(x + 0.5D, y + 0.5D, z + 0.5D, "tile.piston.in", 0.5F, world.rand.nextFloat() * 0.15F + 0.6F);
                    if (tileEntity instanceof IRotatable)
                    {
                        ((IRotatable) tileEntity).setDirection(currentDirection);

                    }
                    else if (Block.blocksList[blockID] instanceof IRotatableBlock)
                    {
                        ((IRotatableBlock) Block.blocksList[blockID]).setDirection(world, position.intX(), position.intY(), position.intZ(), currentDirection);
                    }
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
    public boolean onSneakMachineActivated(World world, int x, int y, int z, EntityPlayer entityPlayer, int side, float hitX, float hitY, float hitZ)
    {
        if (world.isRemote)
        {
            return true;
        }
        world.setBlockMetadataWithNotify(x, y, z, side, 3);
        return true;
    }

    @Override
    public boolean onMachineActivated(World world, int x, int y, int z, EntityPlayer entityPlayer, int side, float hitX, float hitY, float hitZ)
    {
        if (world.isRemote)
        {
            return true;
        }
        ForgeDirection currentDirection = ForgeDirection.getOrientation(world.getBlockMetadata(x, y, z));
        if (currentDirection == ForgeDirection.NORTH)
        {
            currentDirection = ForgeDirection.UP;
        }
        else if (currentDirection == ForgeDirection.UP)
        {
            currentDirection = ForgeDirection.DOWN;
        }
        else if (currentDirection == ForgeDirection.DOWN)
        {
            currentDirection = ForgeDirection.EAST;
        }
        else if (currentDirection == ForgeDirection.EAST)
        {
            currentDirection = ForgeDirection.SOUTH;
        }
        else if (currentDirection == ForgeDirection.SOUTH)
        {
            currentDirection = ForgeDirection.WEST;
        }
        else if (currentDirection == ForgeDirection.WEST)
        {
            currentDirection = ForgeDirection.NORTH;
        }
        world.setBlockMetadataWithNotify(x, y, z, currentDirection.ordinal(), 3);
        return true;
    }

    @Override
    public TileEntity createNewTileEntity(World world)
    {
        // TODO Auto-generated method stub
        return null;
    }
}
