package fluidmech.common.item;

import fluidmech.common.item.ItemParts.Parts;
import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

public class ItemPipe extends ItemBlock
{

	public ItemPipe(int id)
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
		return "tile." + Block.blocksList[this.getBlockID()].getUnlocalizedName() + "." + itemStack.getItemDamage();
	}
}
