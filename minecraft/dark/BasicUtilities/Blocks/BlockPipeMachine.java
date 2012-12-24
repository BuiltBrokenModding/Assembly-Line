package dark.BasicUtilities.Blocks;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.liquids.LiquidContainerRegistry;
import net.minecraftforge.liquids.LiquidStack;
import dark.BasicUtilities.BasicUtilitiesMain;
import dark.BasicUtilities.BlockRenderHelper;
import dark.BasicUtilities.PipeTab;
import dark.BasicUtilities.Tile.TileEntityTank;
import dark.BasicUtilities.Tile.TileEntityPump;
import dark.BasicUtilities.api.Liquid;

public class BlockPipeMachine extends BlockContainer
{

    public BlockPipeMachine(int id)
    {
        super(id, Material.iron);
        this.setBlockName("Machine");
        this.setCreativeTab(PipeTab.INSTANCE);
        this.setRequiresSelfNotify();
        this.blockIndexInTexture = 26;
        this.setHardness(1f);
        this.setResistance(5f);
    }

    public boolean isOpaqueCube()
    {
        return false;
    }

    public boolean renderAsNormalBlock()
    {
        return false;
    }
    /**
     * Code thanks to buildcraft src
     */
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer entityplayer, int side, float hitX, float hitY, float hitZ)
    {
        ItemStack current = entityplayer.inventory.getCurrentItem();
        if (current != null) {

            LiquidStack liquid = LiquidContainerRegistry.getLiquidForFilledItem(current);

            TileEntityTank tank = (TileEntityTank) world.getBlockTileEntity(x, y, z);

            // Handle filled containers
            if (liquid != null) {
                int filled = tank.fill(ForgeDirection.UNKNOWN, liquid, true);

                if (filled != 0 &&!entityplayer.capabilities.isCreativeMode) {
                    entityplayer.inventory.setInventorySlotContents(entityplayer.inventory.currentItem, Liquid.consumeItem(current));
                }

                return true;

                // Handle empty containers
            } else {

                LiquidStack stack = tank.tank.getLiquid();
                if (stack != null) {
                    ItemStack liquidItem = LiquidContainerRegistry.fillLiquidContainer(stack, current);

                    liquid = LiquidContainerRegistry.getLiquidForFilledItem(liquidItem);

                    if (liquid != null) {
                        if (!entityplayer.capabilities.isCreativeMode) {
                            if (current.stackSize > 1) {
                                if (!entityplayer.inventory.addItemStackToInventory(liquidItem))
                                    return false;
                                else {
                                    entityplayer.inventory.setInventorySlotContents(entityplayer.inventory.currentItem, Liquid.consumeItem(current));
                                }
                            } else {
                                entityplayer.inventory.setInventorySlotContents(entityplayer.inventory.currentItem, Liquid.consumeItem(current));
                                entityplayer.inventory.setInventorySlotContents(entityplayer.inventory.currentItem, liquidItem);
                            }
                        }
                        tank.tank.drain(liquid.amount, true);
                        return true;
                    }
                }
            }
        }

        return false;

    }

    /**
     * The type of render function that is called for this block
     */
    public int getRenderType()
    {
        return BlockRenderHelper.renderID;
    }

    public int damageDropped(int meta)
    {
        if (meta < 4) { return 0; }
        return meta;
    }

    @Override
    public ItemStack getPickBlock(MovingObjectPosition target, World world, int x, int y, int z)
    {
        int meta = world.getBlockMetadata(x, y, z);
        if (meta < 4) new ItemStack(BasicUtilitiesMain.machine, 1, 0);
        // if(meta == 4) ;
        TileEntity ent = world.getBlockTileEntity(x, y, z);
        if (ent instanceof TileEntityTank) new ItemStack(BasicUtilitiesMain.itemTank, 1, ((TileEntityTank) ent).type.ordinal());
        return null;
    }

    @Override
    public TileEntity createNewTileEntity(World var1, int meta)
    {
        // TODO Auto-generated method stub
        if (meta < 4) { return new TileEntityPump(); }
        if (meta == 4)
        {
            // return new TileEntityCondenser();
        }
        if (meta == 5) { return new TileEntityTank(); }
        return null;
    }

    @Override
    public TileEntity createNewTileEntity(World var1)
    {
        // TODO Auto-generated method stub
        return null;
    }
}
