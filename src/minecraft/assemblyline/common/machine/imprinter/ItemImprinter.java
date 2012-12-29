package assemblyline.common.machine.imprinter;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import universalelectricity.prefab.UETab;
import assemblyline.common.AssemblyLine;

public class ItemImprinter extends Item
{
	public ItemImprinter(int id)
	{
		super(id);
		this.setItemName("imprint");
		this.setIconIndex(1);
		this.setCreativeTab(UETab.INSTANCE);
		this.setHasSubtypes(true);
		this.setTextureFile(AssemblyLine.ITEM_TEXTURE_PATH);
	}

	@Override
	public void addInformation(ItemStack itemStack, EntityPlayer par2EntityPlayer, List list, boolean par4)
	{
		List<ItemStack> filterItems = getFilters(itemStack);

		if (filterItems.size() > 0)
		{
			for (ItemStack filterItem : filterItems)
			{
				list.add(filterItem.getDisplayName());
			}
		}
		else
		{
			list.add("No filters");
		}
	}

	/**
	 * Saves the list of items to filter out inside.
	 */
	public static void setFilters(ItemStack itemStack, ArrayList<ItemStack> filterStacks)
	{
		if (itemStack.getTagCompound() == null)
		{
			itemStack.setTagCompound(new NBTTagCompound());
		}

		NBTTagList nbt = new NBTTagList();

		for (int i = 0; i < filterStacks.size(); ++i)
		{
			if (filterStacks.get(i) != null)
			{
				NBTTagCompound newCompound = new NBTTagCompound();
				newCompound.setByte("Slot", (byte) i);
				filterStacks.get(i).writeToNBT(newCompound);
				nbt.appendTag(newCompound);
			}
		}

		itemStack.getTagCompound().setTag("Items", nbt);
	}

	public static ArrayList<ItemStack> getFilters(ItemStack itemStack)
	{
		ArrayList<ItemStack> filterStacks = new ArrayList<ItemStack>();

		if (itemStack.getTagCompound() == null)
		{
			itemStack.setTagCompound(new NBTTagCompound());
		}

		NBTTagCompound nbt = itemStack.getTagCompound();
		NBTTagList tagList = nbt.getTagList("Items");

		for (int i = 0; i < tagList.tagCount(); ++i)
		{
			NBTTagCompound var4 = (NBTTagCompound) tagList.tagAt(i);
			byte var5 = var4.getByte("Slot");
			filterStacks.add(ItemStack.loadItemStackFromNBT(var4));
		}

		return filterStacks;
	}
}
