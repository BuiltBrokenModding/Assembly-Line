package liquidmechanics.common.block;

import universalelectricity.prefab.tile.TileEntityAdvanced;
import liquidmechanics.client.render.BlockRenderHelper;
import liquidmechanics.common.MetaGroup;
import liquidmechanics.common.TabLiquidMechanics;
import liquidmechanics.common.tileentity.TileEntitySink;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.EnumArmorMaterial;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.liquids.ILiquidTank;
import net.minecraftforge.liquids.LiquidContainerRegistry;
import net.minecraftforge.liquids.LiquidStack;

public class BlockSink extends BlockContainer
{
    public BlockSink(int par1)
    {
        super(par1, Material.iron);
        this.setResistance(4f);
        this.setHardness(4f);
        this.setBlockName("lmSink");
        this.setCreativeTab(TabLiquidMechanics.INSTANCE);
    }

    @Override
    public TileEntity createNewTileEntity(World var1)
    {
        return new TileEntitySink();
    }

    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float sx, float sy, float sz)
    {
        ItemStack heldItem = player.inventory.getCurrentItem();
        TileEntity ent = world.getBlockTileEntity(x, y, z);
        int meta = world.getBlockMetadata(x, y, z);
        int grouping = MetaGroup.getGrouping(meta);

        if (heldItem == null || !(ent instanceof TileEntitySink)) { return false; }
        TileEntitySink sink = (TileEntitySink) ent;

        if (world.isRemote)
        {
            return true;
        }
        else
        // ItemStack var12 = new ItemStack(Item.potion, 1, 0);heldItem.itemID ==
        // Item.glassBottle.itemID
        {
            ILiquidTank tank = sink.getTanks(ForgeDirection.UNKNOWN)[0];
            LiquidStack stack = tank.getLiquid();
            if (grouping == 0 || grouping == 1)
            {
                if (heldItem.itemID == Item.bucketWater.itemID)
                {
                    LiquidStack filling = LiquidContainerRegistry.getLiquidForFilledItem(heldItem);
                    if (stack == null || (stack != null && stack.amount < tank.getCapacity()))
                    {
                        int f = sink.fill(ForgeDirection.getOrientation(side), filling, true);
                        if (f > 0)
                        {
                            player.inventory.setInventorySlotContents(player.inventory.currentItem, new ItemStack(Item.bucketEmpty));
                            return true;
                        }
                    }
                }
                else if (heldItem.itemID == Item.bucketEmpty.itemID)
                {
                    LiquidStack drain = tank.drain(LiquidContainerRegistry.BUCKET_VOLUME, false);
                    if (drain != null && drain.amount >= LiquidContainerRegistry.BUCKET_VOLUME)
                    {
                        player.inventory.setInventorySlotContents(player.inventory.currentItem, new ItemStack(Item.bucketWater));
                        tank.drain(LiquidContainerRegistry.BUCKET_VOLUME, true);
                        return true;
                    }

                }
                else if (heldItem.itemID == Item.glassBottle.itemID)
                {
                    LiquidStack drain = tank.drain(LiquidContainerRegistry.BUCKET_VOLUME / 4, false);
                    if (drain != null && drain.amount >= LiquidContainerRegistry.BUCKET_VOLUME / 4)
                    {
                        player.inventory.setInventorySlotContents(player.inventory.currentItem, new ItemStack(Item.potion, 1, 0));
                        tank.drain(LiquidContainerRegistry.BUCKET_VOLUME / 4, true);
                        return true;
                    }
                }
                else if (heldItem.getItem() instanceof ItemArmor && ((ItemArmor) heldItem.getItem()).getArmorMaterial() == EnumArmorMaterial.CLOTH)
                {
                    ItemArmor var13 = (ItemArmor) heldItem.getItem();
                    var13.removeColor(heldItem);
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLiving par5EntityLiving)
    {
        int meta = world.getBlockMetadata(x, y, z);
        int angle = MathHelper.floor_double((par5EntityLiving.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;
        TileEntity ent = world.getBlockTileEntity(x, y, z);

        world.setBlockMetadata(x, y, z, angle + MetaGroup.getGroupStartMeta(MetaGroup.getGrouping(meta)));
        if (ent instanceof TileEntityAdvanced)
        {
            ((TileEntityAdvanced) world.getBlockTileEntity(x, y, z)).initiate();
        }

        world.notifyBlocksOfNeighborChange(x, y, z, this.blockID);
    }

    @Override
    public ItemStack getPickBlock(MovingObjectPosition target, World world, int x, int y, int z)
    {
        int meta = world.getBlockMetadata(x, y, z);

        return new ItemStack(this, 1, 0);

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
        return BlockRenderHelper.renderID;
    }
}
