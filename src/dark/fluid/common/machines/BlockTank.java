package dark.fluid.common.machines;

import java.util.List;

import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidStack;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import dark.api.parts.INetworkPart;
import dark.core.prefab.helpers.AutoCraftingManager;
import dark.core.prefab.helpers.FluidHelper;
import dark.fluid.client.render.BlockRenderHelper;
import dark.fluid.common.BlockFM;

public class BlockTank extends BlockFM
{

    public BlockTank(int id)
    {
        super("FluidTank", id, Material.rock);
        this.setHardness(1f);
        this.setResistance(5f);
    }

    @Override
    public boolean isOpaqueCube()
    {
        return false;
    }

    @Override @SideOnly(Side.CLIENT)
    public boolean renderAsNormalBlock()
    {
        return false;
    }

    @Override @SideOnly(Side.CLIENT)
    public int getRenderType()
    {
        return BlockRenderHelper.renderID;
    }

    @Override
    public int damageDropped(int meta)
    {
        return meta;
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer entityplayer, int side, float hitX, float hitY, float hitZ)
    {
        if (entityplayer.isSneaking())
        {
            return false;
        }
        ItemStack current = entityplayer.inventory.getCurrentItem();
        if (current != null)
        {

            FluidStack liquid = FluidContainerRegistry.getFluidForFilledItem(current);

            TileEntity tileEntity = world.getBlockTileEntity(x, y, z);

            if (tileEntity instanceof TileEntityTank)
            {
                TileEntityTank tank = (TileEntityTank) tileEntity;

                // Handle filled containers
                if (liquid != null)
                {
                    if (current.isItemEqual(new ItemStack(Item.potion)))
                    {
                        liquid = FluidHelper.getStack(liquid, FluidContainerRegistry.BUCKET_VOLUME / 4);
                    }
                    int filled = tank.fill(ForgeDirection.getOrientation(side), liquid, true);

                    if (filled != 0 && !entityplayer.capabilities.isCreativeMode)
                    {
                        entityplayer.inventory.setInventorySlotContents(entityplayer.inventory.currentItem, AutoCraftingManager.consumeItem(current, 1));
                    }

                    return true;

                    // Handle empty containers
                }
                else
                {

                    FluidStack stack = tank.getTank().getFluid();
                    if (stack != null)
                    {
                        ItemStack liquidItem = FluidContainerRegistry.fillFluidContainer(stack, current);

                        liquid = FluidContainerRegistry.getFluidForFilledItem(liquidItem);

                        if (liquid != null)
                        {
                            if (!entityplayer.capabilities.isCreativeMode)
                            {
                                if (current.stackSize > 1)
                                {
                                    if (!entityplayer.inventory.addItemStackToInventory(liquidItem))
                                        return false;
                                    else
                                    {
                                        entityplayer.inventory.setInventorySlotContents(entityplayer.inventory.currentItem, AutoCraftingManager.consumeItem(current, 1));
                                    }
                                }
                                else
                                {
                                    entityplayer.inventory.setInventorySlotContents(entityplayer.inventory.currentItem, AutoCraftingManager.consumeItem(current, 1));
                                    entityplayer.inventory.setInventorySlotContents(entityplayer.inventory.currentItem, liquidItem);
                                }
                            }
                            int ammount = liquid.amount;
                            if (current.isItemEqual(new ItemStack(Item.glassBottle)))
                            {
                                ammount = (FluidContainerRegistry.BUCKET_VOLUME / 4);
                            }
                            tank.drain(ForgeDirection.getOrientation(side), ammount, true);
                            return true;
                        }
                    }
                }
            }
        }

        return false;

    }

    @Override
    public TileEntity createNewTileEntity(World var1)
    {
        return new TileEntityTank();
    }

    @Override
    public void onBlockAdded(World world, int x, int y, int z)
    {
        super.onBlockAdded(world, x, y, z);

        TileEntity tileEntity = world.getBlockTileEntity(x, y, z);

        if (tileEntity instanceof INetworkPart)
        {
            ((INetworkPart) tileEntity).refresh();
        }
    }

    @Override
    public void onNeighborBlockChange(World world, int x, int y, int z, int blockID)
    {
        TileEntity tileEntity = world.getBlockTileEntity(x, y, z);

        if (tileEntity instanceof INetworkPart)
        {
            ((INetworkPart) tileEntity).refresh();
        }
    }

    @Override
    public boolean hasComparatorInputOverride()
    {
        return true;
    }

    @Override
    public int getComparatorInputOverride(World world, int x, int y, int z, int par5)
    {
        TileEntityTank tileEntity = (TileEntityTank) world.getBlockTileEntity(x, y, z);
        if (tileEntity != null)
        {
            return tileEntity.getRedstoneLevel();
        }
        return 0;
    }

    @Override
    public ItemStack getPickBlock(MovingObjectPosition target, World world, int x, int y, int z)
    {
        int meta = world.getBlockMetadata(x, y, z);

        return new ItemStack(this, 1, meta);

    }

    @Override
    public void getSubBlocks(int par1, CreativeTabs par2CreativeTabs, List par3List)
    {
        par3List.add(new ItemStack(par1, 1, 15));
        for (int i = 0; i < 16; i++)
        {
            if (FluidHelper.hasRestrictedStack(i))
            {
                par3List.add(new ItemStack(par1, 1, i));
            }
        }
    }

}
