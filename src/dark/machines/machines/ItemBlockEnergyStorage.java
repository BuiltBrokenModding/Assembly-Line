package dark.machines.machines;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import universalelectricity.core.vector.Vector3;
import dark.machines.CoreRecipeLoader;

/** Item version of the enrgy storage block
 *
 * @author Darkguardsman */
public class ItemBlockEnergyStorage extends ItemBlock
{
    public ItemBlockEnergyStorage(int id)
    {
        super(id);
        this.setMaxDamage(0);
        this.setHasSubtypes(true);
    }

    @Override
    public int getMetadata(int damage)
    {
        return damage;
    }

    @Override
    public String getUnlocalizedName(ItemStack itemStack)
    {
        return Block.blocksList[this.getBlockID()].getUnlocalizedName() + "." + itemStack.getItemDamage();
    }

    @Override
    public boolean placeBlockAt(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ, int metadata)
    {
        if (!world.setBlock(x, y, z, this.getBlockID(), side, 3))
        {
            return false;
        }

        if (world.getBlockId(x, y, z) == this.getBlockID())
        {
            Block.blocksList[this.getBlockID()].onBlockPlacedBy(world, x, y, z, player, stack);
            Block.blocksList[this.getBlockID()].onPostBlockPlaced(world, x, y, z, metadata);
            if (stack.getTagCompound() != null && stack.getTagCompound().hasKey("wrenched"))
            {
                TileEntity entity = world.getBlockTileEntity(x, y, z);
                if (entity instanceof TileEntityBatteryBox)
                {
                    ((TileEntityBatteryBox) entity).getInventory().loadInv(stack.getTagCompound());
                    ((TileEntityBatteryBox) entity).setEnergyStored(stack.getTagCompound().getFloat("energy"));
                }
            }
        }

        return true;
    }

    public static ItemStack getWrenchedBatteryBox(World world, Vector3 vec)
    {
        ItemStack itemStack = new ItemStack(CoreRecipeLoader.blockBatBox);
        TileEntity entity = vec.getTileEntity(world);
        if (entity instanceof TileEntityBatteryBox)
        {
            if (itemStack.getTagCompound() == null)
            {
                itemStack.setTagCompound(new NBTTagCompound());
            }
            itemStack.getTagCompound().setBoolean("wrenched", true);
            itemStack.getTagCompound().setFloat("energy", ((TileEntityBatteryBox) entity).getEnergyStored());
            ((TileEntityBatteryBox) entity).getInventory().saveInv(itemStack.getTagCompound());
            ((TileEntityBatteryBox) entity).getInventory().clear();
        }
        return itemStack;
    }

    public int getItemStackLimit(ItemStack stack)
    {
        if (stack.getTagCompound() != null && stack.getTagCompound().hasKey("wrenched"))
        {
            return 1;
        }
        return this.getItemStackLimit();
    }
}
