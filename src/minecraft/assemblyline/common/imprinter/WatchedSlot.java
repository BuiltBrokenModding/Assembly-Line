package assemblyline.common.imprinter;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;

public class WatchedSlot extends Slot
{
	private ISlotWatcher slotWatcher;

	public WatchedSlot(IInventory inventory, int id, int xPosition, int yPosition, ISlotWatcher slotWatcher)
	{
		super(inventory, id, xPosition, yPosition);
		this.slotWatcher = slotWatcher;
	}

	@Override
	public void onSlotChanged()
	{
		if (this.slotWatcher != null)
		{
			this.slotWatcher.slotContentsChanged();
		}
	}

}
