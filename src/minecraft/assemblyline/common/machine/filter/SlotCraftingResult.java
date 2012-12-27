package assemblyline.common.machine.filter;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class SlotCraftingResult extends Slot
{
	private ContainerStamper container;

	public SlotCraftingResult(ContainerStamper container, IInventory par1iInventory, int par2, int par3, int par4)
	{
		super(par1iInventory, par2, par3, par4);
		this.container = container;
	}

	@Override
	public boolean isItemValid(ItemStack par1ItemStack)
	{
		return false;
	}

	@Override
	public void onPickupFromSlot(EntityPlayer entityPlayer, ItemStack par2ItemStack)
	{
		super.onPickupFromSlot(entityPlayer, par2ItemStack);

		if (this.getStack() != null)
		{
			ItemStack[] requiredItems = this.container.getIdealRecipe(this.getStack()).clone();

			if (requiredItems != null)
			{
				for (ItemStack searchStack : requiredItems)
				{
					for (int i = 0; i < entityPlayer.inventory.getSizeInventory(); i++)
					{
						ItemStack checkStack = entityPlayer.inventory.getStackInSlot(i);

						if (checkStack != null)
						{
							if (searchStack.isItemEqual(checkStack))
							{
								entityPlayer.inventory.decrStackSize(i, 1);
								break;
							}
						}
					}
				}
			}
		}
	}
}
