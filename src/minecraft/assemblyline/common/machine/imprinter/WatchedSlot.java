package assemblyline.common.machine.imprinter;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;

public class WatchedSlot extends Slot
{
	ISlotWatcher slotWatcher;

	public WatchedSlot(IInventory inventory, int id, int xPosition, int yPosition, ISlotWatcher slotWatcher)
	{
		super(inventory, id, xPosition, yPosition);
		this.slotWatcher = slotWatcher;
	}
	
	@Override
	public void onSlotChanged()
	{
		if (slotWatcher != null)
		{
			slotWatcher.slotContentsChanged();
		}
	}

}
