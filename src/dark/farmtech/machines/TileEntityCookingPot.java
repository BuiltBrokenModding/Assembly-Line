package dark.farmtech.machines;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import dark.core.interfaces.IBlockActivated;
import dark.core.prefab.machine.TileEntityMachine;

public class TileEntityCookingPot extends TileEntityMachine implements IBlockActivated
{
    protected boolean hasWatter = false;
    protected boolean hasWood = false;
    protected boolean isWoodLit = false;
    protected boolean isDone = false;

    protected int cookTime = 0;
    protected int fuelLeft = 0;

    protected int slotOne = 0;
    protected int slotTwo = 1;
    protected int slotThree = 2;
    protected int slotFour = 3;
    protected int output = 4;

    public TileEntityCookingPot()
    {
        this.invSlots = 5;
    }

    @Override
    public boolean onActivated(EntityPlayer entityPlayer)
    {
        if (entityPlayer != null)
        {
            ItemStack stack = entityPlayer.getHeldItem();
            if (stack != null)
            {
                if ((!this.hasWood || fuelLeft < 10) && stack.getItem().itemID == Block.wood.blockID)
                {
                    //TODO add wood to fire under pot
                    return true;
                }
                else if (stack.getItem().itemID == Item.bowlEmpty.itemID && this.getStackInSlot(output) != null)
                {
                    //TODO fill bowl
                    return true;
                }
                else
                {
                    //TODO check item and see if its a valid ingredient to cook food, as well see if its valid to add but will destroy the food
                }
            }
        }
        return false;
    }
}
