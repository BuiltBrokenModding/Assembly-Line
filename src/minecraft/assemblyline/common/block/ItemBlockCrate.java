package assemblyline.common.block;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
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

	@Override
	public void onUpdate(ItemStack itemStack, World par2World, Entity entity, int par4, boolean par5)
	{
		if (entity instanceof EntityPlayer)
		{
			EntityPlayer player = (EntityPlayer) entity;
			ItemStack containingStack = getContainingItemStack(itemStack);

			if (containingStack != null)
			{
				player.addPotionEffect(new PotionEffect(Potion.moveSlowdown.id, 5, (int)((float) containingStack.stackSize / (float) TileEntityCrate.MAX_LIMIT) *  5));
			}
		}
	}

	public static void setContainingItemStack(ItemStack itemStack, ItemStack containingStack)
	{
		if (itemStack.stackTagCompound == null)
		{
			itemStack.setTagCompound(new NBTTagCompound());
		}

		if (containingStack != null)
		{
			NBTTagCompound itemTagCompound = new NBTTagCompound();
			containingStack.stackSize = Math.abs(containingStack.stackSize);
			containingStack.writeToNBT(itemTagCompound);
			itemStack.getTagCompound().setTag("Item", itemTagCompound);

			itemStack.getTagCompound().setInteger("Count", containingStack.stackSize);
		}
	}

	public static ItemStack getContainingItemStack(ItemStack itemStack)
	{
		if (itemStack.stackTagCompound == null)
		{
			itemStack.setTagCompound(new NBTTagCompound());
			return null;
		}

		NBTTagCompound itemTagCompound = itemStack.getTagCompound().getCompoundTag("Item");
		ItemStack containingStack = ItemStack.loadItemStackFromNBT(itemTagCompound);

		if (containingStack != null)
		{
			containingStack.stackSize = itemStack.getTagCompound().getInteger("Count");
		}

		return containingStack;
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
					tileEntity.setInventorySlotContents(0, containingItem);
				}
			}
		}

		return true;
	}
}
