package EUI.SteamPower.turbine;

import net.minecraft.src.*;

public class ContainerGenerator extends Container
{
	private TileEntityGenerator tileEntity;

    public ContainerGenerator(InventoryPlayer par1InventoryPlayer, TileEntityGenerator tileEntity)
    {
        this.tileEntity = tileEntity;
        this.addSlotToContainer(new Slot(tileEntity, 0, 33, 34));
        int var3;

        for (var3 = 0; var3 < 3; ++var3)
        {
            for (int var4 = 0; var4 < 9; ++var4)
            {
                this.addSlotToContainer(new Slot(par1InventoryPlayer, var4 + var3 * 9 + 9, 8 + var4 * 18, 84 + var3 * 18));
            }
        }

        for (var3 = 0; var3 < 9; ++var3)
        {
            this.addSlotToContainer(new Slot(par1InventoryPlayer, var3, 8 + var3 * 18, 142));
        }
    }

    public boolean canInteractWith(EntityPlayer par1EntityPlayer)
    {
        return this.tileEntity.isUseableByPlayer(par1EntityPlayer);
    }
    
    /**
     * Called to transfer a stack from one inventory to the other eg. when shift clicking.
     */
    public ItemStack transferStackInSlot(int par1)
    {  
        return null;
    }
}
