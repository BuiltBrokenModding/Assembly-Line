package dark.farmtech.machines;

import dark.api.farm.DecayMatterList;
import dark.interfaces.IInvBox;
import dark.prefab.InvChest;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.ForgeDirection;

public class TileEntityCompBox extends TileEntityFT
{
    @Override
    public IInvBox getInventory()
    {
        if (inventory == null)
        {
            inventory = new InvChest(this, 6);
        }
        return inventory;
    }
    @Override
    public boolean canStore(ItemStack stack, int slot, ForgeDirection side)
    {
        return stack != null && DecayMatterList.isDecayMatter(stack);
    }

    @Override
    public boolean canRemove(ItemStack stack, int slot, ForgeDirection side)
    {
        return stack != null && !DecayMatterList.isDecayMatter(stack);
    }
}
