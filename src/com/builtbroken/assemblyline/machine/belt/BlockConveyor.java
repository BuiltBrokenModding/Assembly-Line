package com.builtbroken.assemblyline.machine.belt;

import java.util.List;

import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;

import com.builtbroken.assemblyline.client.render.BlockRenderingHandler;
import com.builtbroken.assemblyline.client.render.RenderConveyorBelt;
import com.builtbroken.assemblyline.machine.BlockAssembly;
import com.builtbroken.assemblyline.machine.belt.TileEntityConveyorBelt.SlantType;
import com.builtbroken.common.Pair;
import com.dark.tilenetwork.prefab.NetworkItemSupply;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockConveyor extends BlockAssembly
{

    public BlockConveyor()
    {
        super("ConveyorBelt", Material.iron);
        this.setBlockBounds(0, 0, 0, 1, 0.3f, 1);
    }

    @Override
    public void onEntityCollidedWithBlock(World world, int x, int y, int z, Entity entity)
    {
        TileEntity tileEntity = world.getBlockTileEntity(x, y, z);
        if (tileEntity instanceof TileEntityConveyor)
        {
            if (((TileEntityConveyor) tileEntity).getTileNetwork() instanceof NetworkItemSupply)
            {
                if (((TileEntityConveyor) tileEntity).getTileNetwork().isTrackingEntity(entity))
                {
                    return;
                }
            }
            if (((TileEntityConveyor) tileEntity).isFunctioning())
            {
            }
        }
    }

    @Override
    public void setBlockBoundsBasedOnState(IBlockAccess world, int x, int y, int z)
    {
        if (world.getBlockTileEntity(x, y, z) instanceof TileEntityConveyorBelt)
        {
            TileEntityConveyorBelt tileEntity = (TileEntityConveyorBelt) world.getBlockTileEntity(x, y, z);

            if (tileEntity.getSlant() == SlantType.UP || tileEntity.getSlant() == SlantType.DOWN)
            {
                this.setBlockBounds(0f, 0f, 0f, 1f, 0.96f, 1f);
                return;
            }
            if (tileEntity.getSlant() == SlantType.TOP)
            {
                this.setBlockBounds(0f, 0.68f, 0f, 1f, 0.96f, 1f);
                return;
            }
        }

        this.setBlockBounds(0f, 0f, 0f, 1f, 0.3f, 1f);
    }

    @Override
    public AxisAlignedBB getSelectedBoundingBoxFromPool(World world, int x, int y, int z)
    {
        TileEntity t = world.getBlockTileEntity(x, y, z);

        if (t != null && t instanceof TileEntityConveyorBelt)
        {
            TileEntityConveyorBelt tileEntity = (TileEntityConveyorBelt) t;

            if (tileEntity.getSlant() == SlantType.UP || tileEntity.getSlant() == SlantType.DOWN)
            {
                return AxisAlignedBB.getAABBPool().getAABB(x + this.minX, y + this.minY, z + this.minZ, (double) x + 1, (double) y + 1, (double) z + 1);
            }
            if (tileEntity.getSlant() == SlantType.TOP)
            {
                return AxisAlignedBB.getAABBPool().getAABB(x + this.minX, (double) y + 0.68f, z + this.minZ, x + this.maxX, (double) y + 0.98f, z + this.maxZ);
            }
        }

        return AxisAlignedBB.getAABBPool().getAABB(x + this.minX, y + this.minY, z + this.minZ, x + this.maxX, y + this.maxY, z + this.maxZ);
    }

    @Override
    public void addCollisionBoxesToList(World world, int x, int y, int z, AxisAlignedBB par5AxisAlignedBB, List par6List, Entity par7Entity)
    {

        TileEntity t = world.getBlockTileEntity(x, y, z);

        if (t != null && t instanceof TileEntityConveyorBelt)
        {
            TileEntityConveyorBelt tileEntity = (TileEntityConveyorBelt) t;

            if (tileEntity.getSlant() == SlantType.UP || tileEntity.getSlant() == SlantType.DOWN)
            {
                AxisAlignedBB boundBottom = AxisAlignedBB.getAABBPool().getAABB(x, y, z, x + 1, y + 0.3, z + 1);
                AxisAlignedBB boundTop = null;

                ForgeDirection direction = tileEntity.getDirection();

                if (tileEntity.getSlant() == SlantType.UP)
                {
                    if (direction.offsetX > 0)
                    {
                        boundTop = AxisAlignedBB.getAABBPool().getAABB(x + (float) direction.offsetX / 2, y, z, x + 1, y + 0.8, z + 1);
                    }
                    else if (direction.offsetX < 0)
                    {
                        boundTop = AxisAlignedBB.getAABBPool().getAABB(x, y, z, x + (float) direction.offsetX / -2, y + 0.8, z + 1);
                    }
                    else if (direction.offsetZ > 0)
                    {
                        boundTop = AxisAlignedBB.getAABBPool().getAABB(x, y, z + (float) direction.offsetZ / 2, x + 1, y + 0.8, z + 1);
                    }
                    else if (direction.offsetZ < 0)
                    {
                        boundTop = AxisAlignedBB.getAABBPool().getAABB(x, y, z, x + 1, y + 0.8, z + (float) direction.offsetZ / -2);
                    }
                }
                else if (tileEntity.getSlant() == SlantType.DOWN)
                {
                    if (direction.offsetX > 0)
                    {
                        boundTop = AxisAlignedBB.getAABBPool().getAABB(x, y, z, x + (float) direction.offsetX / 2, y + 0.8, z + 1);
                    }
                    else if (direction.offsetX < 0)
                    {
                        boundTop = AxisAlignedBB.getAABBPool().getAABB(x + (float) direction.offsetX / -2, y, z, x + 1, y + 0.8, z + 1);
                    }
                    else if (direction.offsetZ > 0)
                    {
                        boundTop = AxisAlignedBB.getAABBPool().getAABB(x, y, z, x + 1, y + 0.8, z + (float) direction.offsetZ / 2);
                    }
                    else if (direction.offsetZ < 0)
                    {
                        boundTop = AxisAlignedBB.getAABBPool().getAABB(x, y, z + (float) direction.offsetZ / -2, x + 1, y + 0.8, z + 1);
                    }
                }

                if (par5AxisAlignedBB.intersectsWith(boundBottom))
                {
                    par6List.add(boundBottom);
                }
                if (boundTop != null && par5AxisAlignedBB.intersectsWith(boundTop))
                {
                    par6List.add(boundTop);
                }

                return;
            }

            if (tileEntity.getSlant() == SlantType.TOP)
            {
                AxisAlignedBB newBounds = AxisAlignedBB.getAABBPool().getAABB(x, y + 0.68, z, x + 1, y + 0.98, z + 1);

                if (newBounds != null && par5AxisAlignedBB.intersectsWith(newBounds))
                {
                    par6List.add(newBounds);
                }

                return;
            }
        }

        AxisAlignedBB newBounds = AxisAlignedBB.getAABBPool().getAABB(x, y, z, x + 1, y + 0.3, z + 1);

        if (newBounds != null && par5AxisAlignedBB.intersectsWith(newBounds))
        {
            par6List.add(newBounds);
        }
    }

    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase par5EntityLiving, ItemStack stack)
    {
        super.onBlockPlacedBy(world, x, y, z, par5EntityLiving, stack);
        int angle = MathHelper.floor_double((par5EntityLiving.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;
        int change = 2;

        switch (angle)
        {
            case 0:
                change = 3;
                break;
            case 1:
                change = 4;
                break;
            case 2:
                change = 2;
                break;
            case 3:
                change = 5;
                break;

        }
        world.setBlockMetadataWithNotify(x, y, z, change, 3);
    }

    @Override
    public boolean onUseWrench(World world, int x, int y, int z, EntityPlayer par5EntityPlayer, int side, float hitX, float hitY, float hitZ)
    {
        int original = world.getBlockMetadata(x, y, z);
        int change = 2;

        switch (original)
        {
            case 2:
                change = 4;
                break;
            case 3:
                change = 5;
                break;
            case 4:
                change = 3;
                break;
            case 5:
                change = 2;
                break;

        }

        world.setBlockMetadataWithNotify(x, y, z, change, 3);

        return true;
    }

    @Override
    public boolean onSneakUseWrench(World world, int x, int y, int z, EntityPlayer par5EntityPlayer, int side, float hitX, float hitY, float hitZ)
    {
        TileEntityConveyorBelt tileEntity = (TileEntityConveyorBelt) world.getBlockTileEntity(x, y, z);

        int slantOrdinal = tileEntity.getSlant().ordinal() + 1;

        if (slantOrdinal >= SlantType.values().length)
        {
            slantOrdinal = 0;
        }

        tileEntity.setSlant(SlantType.values()[slantOrdinal]);

        return true;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void getClientTileEntityRenderers(List<Pair<Class<? extends TileEntity>, TileEntitySpecialRenderer>> list)
    {
        list.add(new Pair<Class<? extends TileEntity>, TileEntitySpecialRenderer>(TileEntityConveyorBelt.class, new RenderConveyorBelt()));
    }

    /** Returns the TileEntity used by this block. */
    @Override
    public TileEntity createNewTileEntity(World var1)
    {
        return new TileEntityConveyor();
    }

    @SideOnly(Side.CLIENT)
    @Override
    public int getRenderType()
    {
        return BlockRenderingHandler.BLOCK_RENDER_ID;
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
    public int damageDropped(int par1)
    {
        return 0;
    }
}
