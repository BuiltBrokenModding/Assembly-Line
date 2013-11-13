package dark.assembly.machine.red;

import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityHopper;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Facing;
import net.minecraft.util.Icon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import dark.core.common.DMCreativeTab;
import dark.core.common.DarkMain;
import dark.core.prefab.machine.BlockMachine;

public class BlockAdvancedHopper extends BlockMachine
{
    private final Random rand = new Random();
    @SideOnly(Side.CLIENT)
    public static Icon hopperIcon;
    @SideOnly(Side.CLIENT)
    public static Icon hopperTopIcon;
    @SideOnly(Side.CLIENT)
    public static Icon hopperInsideIcon;

    public BlockAdvancedHopper(int par1)
    {
        super(DarkMain.CONFIGURATION, "DMHopper", Material.iron);
        this.setCreativeTab(DMCreativeTab.tabAutomation);
        this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
    }

    /** Updates the blocks bounds based on its current state. Args: world, x, y, z */
    public void setBlockBoundsBasedOnState(IBlockAccess par1IBlockAccess, int par2, int par3, int par4)
    {
        this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
    }

    /** Adds all intersecting collision boxes to a list. (Be sure to only add boxes to the list if
     * they intersect the mask.) Parameters: World, X, Y, Z, mask, list, colliding entity */
    public void addCollisionBoxesToList(World par1World, int par2, int par3, int par4, AxisAlignedBB par5AxisAlignedBB, List par6List, Entity par7Entity)
    {
        this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.625F, 1.0F);
        super.addCollisionBoxesToList(par1World, par2, par3, par4, par5AxisAlignedBB, par6List, par7Entity);
        float f = 0.125F;
        this.setBlockBounds(0.0F, 0.0F, 0.0F, f, 1.0F, 1.0F);
        super.addCollisionBoxesToList(par1World, par2, par3, par4, par5AxisAlignedBB, par6List, par7Entity);
        this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, f);
        super.addCollisionBoxesToList(par1World, par2, par3, par4, par5AxisAlignedBB, par6List, par7Entity);
        this.setBlockBounds(1.0F - f, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
        super.addCollisionBoxesToList(par1World, par2, par3, par4, par5AxisAlignedBB, par6List, par7Entity);
        this.setBlockBounds(0.0F, 0.0F, 1.0F - f, 1.0F, 1.0F, 1.0F);
        super.addCollisionBoxesToList(par1World, par2, par3, par4, par5AxisAlignedBB, par6List, par7Entity);
        this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
    }

    /** Called when a block is placed using its ItemBlock. Args: World, X, Y, Z, side, hitX, hitY,
     * hitZ, block metadata */
    public int onBlockPlaced(World par1World, int par2, int par3, int par4, int par5, float par6, float par7, float par8, int par9)
    {
        int j1 = Facing.oppositeSide[par5];

        if (j1 == 1)
        {
            j1 = 0;
        }

        return j1;
    }

    /** Returns a new instance of a block's tile entity class. Called on placing the block. */
    public TileEntity createNewTileEntity(World par1World)
    {
        return new TileEntityAdvancedHopper();
    }

    /** Called when the block is placed in the world. */
    public void onBlockPlacedBy(World par1World, int par2, int par3, int par4, EntityLivingBase par5EntityLivingBase, ItemStack par6ItemStack)
    {
        super.onBlockPlacedBy(par1World, par2, par3, par4, par5EntityLivingBase, par6ItemStack);

        if (par6ItemStack.hasDisplayName())
        {
            TileEntityAdvancedHopper tileentityhopper = getHopperTile(par1World, par2, par3, par4);
            tileentityhopper.setInventoryName(par6ItemStack.getDisplayName());
        }
    }

    /** Called whenever the block is added into the world. Args: world, x, y, z */
    public void onBlockAdded(World par1World, int par2, int par3, int par4)
    {
        super.onBlockAdded(par1World, par2, par3, par4);
        this.updateMetadata(par1World, par2, par3, par4);
    }

    /** Called upon block activation (right click on the block.) */
    public boolean onBlockActivated(World par1World, int par2, int par3, int par4, EntityPlayer par5EntityPlayer, int par6, float par7, float par8, float par9)
    {
        if (par1World.isRemote)
        {
            return true;
        }
        else
        {
            TileEntityAdvancedHopper tileentityhopper = getHopperTile(par1World, par2, par3, par4);

            if (tileentityhopper != null)
            {
                par5EntityPlayer.displayGUIHopper(tileentityhopper);
            }

            return true;
        }
    }

    /** Lets the block know when one of its neighbor changes. Doesn't know which neighbor changed
     * (coordinates passed are their own) Args: x, y, z, neighbor blockID */
    public void onNeighborBlockChange(World par1World, int par2, int par3, int par4, int par5)
    {
        this.updateMetadata(par1World, par2, par3, par4);
    }

    /** Updates the Metadata to include if the Hopper gets powered by Redstone or not */
    private void updateMetadata(World par1World, int par2, int par3, int par4)
    {
        int l = par1World.getBlockMetadata(par2, par3, par4);
        int i1 = getDirectionFromMetadata(l);
        boolean flag = !par1World.isBlockIndirectlyGettingPowered(par2, par3, par4);
        boolean flag1 = getIsBlockNotPoweredFromMetadata(l);

        if (flag != flag1)
        {
            par1World.setBlockMetadataWithNotify(par2, par3, par4, i1 | (flag ? 0 : 8), 4);
        }
    }

    /** Called on server worlds only when the block has been replaced by a different block ID, or the
     * same block with a different metadata value, but before the new metadata value is set. Args:
     * World, x, y, z, old block ID, old metadata */
    public void breakBlock(World par1World, int par2, int par3, int par4, int par5, int par6)
    {
        TileEntityAdvancedHopper tileentityhopper = (TileEntityAdvancedHopper) par1World.getBlockTileEntity(par2, par3, par4);

        if (tileentityhopper != null)
        {
            for (int j1 = 0; j1 < tileentityhopper.getSizeInventory(); ++j1)
            {
                ItemStack itemstack = tileentityhopper.getStackInSlot(j1);

                if (itemstack != null)
                {
                    float f = this.rand.nextFloat() * 0.8F + 0.1F;
                    float f1 = this.rand.nextFloat() * 0.8F + 0.1F;
                    float f2 = this.rand.nextFloat() * 0.8F + 0.1F;

                    while (itemstack.stackSize > 0)
                    {
                        int k1 = this.rand.nextInt(21) + 10;

                        if (k1 > itemstack.stackSize)
                        {
                            k1 = itemstack.stackSize;
                        }

                        itemstack.stackSize -= k1;
                        EntityItem entityitem = new EntityItem(par1World, (double) ((float) par2 + f), (double) ((float) par3 + f1), (double) ((float) par4 + f2), new ItemStack(itemstack.itemID, k1, itemstack.getItemDamage()));

                        if (itemstack.hasTagCompound())
                        {
                            entityitem.getEntityItem().setTagCompound((NBTTagCompound) itemstack.getTagCompound().copy());
                        }

                        float f3 = 0.05F;
                        entityitem.motionX = (double) ((float) this.rand.nextGaussian() * f3);
                        entityitem.motionY = (double) ((float) this.rand.nextGaussian() * f3 + 0.2F);
                        entityitem.motionZ = (double) ((float) this.rand.nextGaussian() * f3);
                        par1World.spawnEntityInWorld(entityitem);
                    }
                }
            }

            par1World.func_96440_m(par2, par3, par4, par5);
        }

        super.breakBlock(par1World, par2, par3, par4, par5, par6);
    }

    /** The type of render function that is called for this block */
    public int getRenderType()
    {
        return 38;
    }

    /** If this block doesn't render as an ordinary block it will return False (examples: signs,
     * buttons, stairs, etc) */
    public boolean renderAsNormalBlock()
    {
        return false;
    }

    /** Is this block (a) opaque and (b) a full 1m cube? This determines whether or not to render the
     * shared face of two adjacent blocks and also whether the player can attach torches, redstone
     * wire, etc to this block. */
    public boolean isOpaqueCube()
    {
        return false;
    }

    public static int getDirectionFromMetadata(int par0)
    {
        return par0 & 7;
    }

    @SideOnly(Side.CLIENT)
    /**
     * Returns true if the given side of this block type should be rendered, if the adjacent block is at the given
     * coordinates.  Args: blockAccess, x, y, z, side
     */
    public boolean shouldSideBeRendered(IBlockAccess par1IBlockAccess, int par2, int par3, int par4, int par5)
    {
        return true;
    }

    @SideOnly(Side.CLIENT)
    /**
     * From the specified side and block metadata retrieves the blocks texture. Args: side, metadata
     */
    public Icon getIcon(int par1, int par2)
    {
        return par1 == 1 ? this.hopperTopIcon : this.hopperIcon;
    }

    public static boolean getIsBlockNotPoweredFromMetadata(int par0)
    {
        return (par0 & 8) != 8;
    }

    /** If this returns true, then comparators facing away from this block will use the value from
     * getComparatorInputOverride instead of the actual redstone signal strength. */
    public boolean hasComparatorInputOverride()
    {
        return true;
    }

    /** If hasComparatorInputOverride returns true, the return value from this is used instead of the
     * redstone signal strength when this block inputs to a comparator. */
    public int getComparatorInputOverride(World par1World, int par2, int par3, int par4, int par5)
    {
        return Container.calcRedstoneFromInventory(getHopperTile(par1World, par2, par3, par4));
    }

    @SideOnly(Side.CLIENT)
    /**
     * When this method is called, your block should register all the icons it needs with the given IconRegister. This
     * is the only chance you get to register icons.
     */
    public void registerIcons(IconRegister par1IconRegister)
    {
        this.hopperIcon = par1IconRegister.registerIcon("hopper_outside");
        this.hopperTopIcon = par1IconRegister.registerIcon("hopper_top");
        this.hopperInsideIcon = par1IconRegister.registerIcon("hopper_inside");
    }

    @SideOnly(Side.CLIENT)
    /**
     * Gets the icon name of the ItemBlock corresponding to this block. Used by hoppers.
     */
    public String getItemIconName()
    {
        return "hopper";
    }

    public static TileEntityAdvancedHopper getHopperTile(IBlockAccess par0IBlockAccess, int par1, int par2, int par3)
    {
        return (TileEntityAdvancedHopper) par0IBlockAccess.getBlockTileEntity(par1, par2, par3);
    }
}
