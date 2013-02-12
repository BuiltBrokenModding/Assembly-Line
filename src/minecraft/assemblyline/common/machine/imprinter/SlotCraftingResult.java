package assemblyline.common.machine.imprinter;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class SlotCraftingResult extends WatchedSlot
{
	private ContainerImprinter container;

	public SlotCraftingResult(ContainerImprinter container, IInventory inventory, int par2, int par3, int par4)
	{
		super(inventory, par2, par3, par4, container);
		this.container = container;
	}

	@Override
	public boolean isItemValid(ItemStack itemStack)
	{
		return false;
	}

	@Override
	public boolean canTakeStack(EntityPlayer player)
	{
		return true;
	}

	@Override
	public void onPickupFromSlot(EntityPlayer entityPlayer, ItemStack itemStack)
	{
		this.container.tileEntity.onPickUpFromResult(entityPlayer, itemStack);
		super.onPickupFromSlot(entityPlayer, itemStack);
	}
}
