package dark.core.blocks;

import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Icon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.Configuration;
import universalelectricity.prefab.block.BlockAdvanced;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import dark.api.INetworkPart;
import dark.core.DarkMain;

/** Basic TileEntity Container class designed to be used by generic machines. It is suggested that
 * each mod using this create there own basic block extending this to reduce need to input config
 * file each time
 *
 * @author Darkguardsman */
public abstract class BlockMachine extends BlockAdvanced implements ITileEntityProvider
{
    /** @param name - The name the block will use for both the config and translation file
     * @param config - configuration reference used to pull blockID from
     * @param blockID - Default block id to be used for the config
     * @param material - Block material used for tool reference? */
    public BlockMachine(String name, Configuration config, int blockID, Material material)
    {
        super(config.getBlock(name, blockID).getInt(), material);
        this.isBlockContainer = true;
        this.setUnlocalizedName(name);

    }

    @SideOnly(Side.CLIENT)
    @Override
    public void registerIcons(IconRegister iconReg)
    {
        this.blockIcon = iconReg.registerIcon(DarkMain.getInstance().PREFIX + "machine");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public Icon getBlockTexture(IBlockAccess par1iBlockAccess, int par2, int par3, int par4, int par5)
    {
        return this.blockIcon;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public Icon getIcon(int par1, int par2)
    {
        return this.blockIcon;
    }

    /** Called whenever the block is added into the world. Args: world, x, y, z */
    @Override
    public void onBlockAdded(World world, int x, int y, int z)
    {
        TileEntity tileEntity = world.getBlockTileEntity(x, y, z);
        if (tileEntity instanceof INetworkPart)
        {
            ((INetworkPart) tileEntity).refresh();
        }
        super.onBlockAdded(world, x, y, z);
    }

    @Override
    public void breakBlock(World world, int x, int y, int z, int par5, int par6)
    {
        super.breakBlock(world, x, y, z, par5, par6);
        this.dropEntireInventory(world, x, y, z, par5, par6);
        world.removeBlockTileEntity(x, y, z);
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

    /** Called when the block receives a BlockEvent - see World.addBlockEvent. By default, passes it
     * on to the tile entity at this location. Args: world, x, y, z, blockID, EventID, event
     * parameter */
    @Override
    public boolean onBlockEventReceived(World world, int x, int y, int z, int blockID, int eventID)
    {
        super.onBlockEventReceived(world, x, y, z, blockID, eventID);
        TileEntity tileentity = world.getBlockTileEntity(x, y, z);
        return tileentity != null ? tileentity.receiveClientEvent(blockID, eventID) : false;
    }

    public void dropEntireInventory(World world, int x, int y, int z, int par5, int par6)
    {
        TileEntity tileEntity = world.getBlockTileEntity(x, y, z);

        if (tileEntity != null)
        {
            if (tileEntity instanceof IInventory)
            {
                IInventory inventory = (IInventory) tileEntity;

                for (int slot = 0; slot < inventory.getSizeInventory(); ++slot)
                {
                    ItemStack itemStack = inventory.getStackInSlot(slot);

                    if (itemStack != null)
                    {
                        float deltaX = world.rand.nextFloat() * 0.8F + 0.1F;
                        float deltaY = world.rand.nextFloat() * 0.8F + 0.1F;
                        float deltaZ = world.rand.nextFloat() * 0.8F + 0.1F;

                        while (itemStack.stackSize > 0)
                        {
                            int stackSplit = world.rand.nextInt(21) + 10;

                            if (stackSplit > itemStack.stackSize)
                            {
                                stackSplit = itemStack.stackSize;
                            }

                            itemStack.stackSize -= stackSplit;
                            EntityItem entityItem = new EntityItem(world, (x + deltaX), (y + deltaY), (z + deltaZ), new ItemStack(itemStack.itemID, stackSplit, itemStack.getItemDamage()));

                            if (itemStack.hasTagCompound())
                            {
                                entityItem.getEntityItem().setTagCompound((NBTTagCompound) itemStack.getTagCompound().copy());
                            }

                            float var13 = 0.05F;
                            entityItem.motionX = ((float) world.rand.nextGaussian() * var13);
                            entityItem.motionY = ((float) world.rand.nextGaussian() * var13 + 0.2F);
                            entityItem.motionZ = ((float) world.rand.nextGaussian() * var13);
                            world.spawnEntityInWorld(entityItem);
                        }
                    }
                }
            }
        }
    }

}
