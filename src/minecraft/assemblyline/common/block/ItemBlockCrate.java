package assemblyline.common.block;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class ItemBlockCrate extends ItemBlock
{
	public ItemBlockCrate(int par1)
	{
		super(par1);
		this.setMaxDamage(0);
		this.setMaxStackSize(1);
	}

	@Override
	public void addInformation(ItemStack itemStack, EntityPlayer par2EntityPlayer, List par3List, boolean par4)
	{
		ItemStack containingStack = getContainingItemStack(itemStack);

		if (containingStack != null)
		{
			par3List.add(containingStack.getDisplayName());
			par3List.add("Amount: " + containingStack.stackSize);
		}
		else
		{
			par3List.add("Empty");

		}
	}

	public static void setContainingItemStack(ItemStack itemStack, ItemStack containingStack)
	{
		if (itemStack.stackTagCompound == null)
		{
			itemStack.setTagCompound(new NBTTagCompound());
		}

		containingStack.writeToNBT(itemStack.getTagCompound());
	}

	public static ItemStack getContainingItemStack(ItemStack itemStack)
	{
		if (itemStack.stackTagCompound == null)
		{
			itemStack.setTagCompound(new NBTTagCompound());
		}

		return ItemStack.loadItemStackFromNBT(itemStack.getTagCompound());
	}

	@Override
	public boolean placeBlockAt(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ, int metadata)
	{
		if (super.placeBlockAt(stack, player, world, x, y, z, side, hitX, hitY, hitZ, metadata))
		{
			ItemStack containingItem = getContainingItemStack(stack);

			if (world.getBlockTileEntity(x, y, z) != null && containingItem != null)
			{
				if (containingItem.stackSize > 0)
				{
					TileEntityCrate tileEntity = (TileEntityCrate) world.getBlockTileEntity(x, y, z);
					tileEntity.containingItems[0] = containingItem;
				}
			}
		}

		return true;
	}
}
