package steampower.boiler;
import net.minecraft.src.*;

public class ContainerBoiler extends Container
{
    private TileEntityBoiler boiler;
    private int lastCookTime = 0;
    private int lastBurnTime = 0;
    private int lastItemBurnTime = 0;

    public ContainerBoiler(InventoryPlayer par1InventoryPlayer, TileEntityBoiler par2TileEntityboiler)
    {
        this.boiler = par2TileEntityboiler;
        this.addSlotToContainer(new Slot(par2TileEntityboiler, 0, 56, 17));
        int line;
        for (line = 0; line < 3; ++line)
        {
            for (int slot = 0; slot < 9; ++slot)
            {
                this.addSlotToContainer(new Slot(par1InventoryPlayer, slot + line * 9 + 9, 8 + slot * 18, 84 + line * 18));
            }
        }

        for (line = 0; line < 9; ++line)
        {
            this.addSlotToContainer(new Slot(par1InventoryPlayer, line, 8 + line * 18, 142));
        }
    }

    

    public void updateProgressBar(int par1, int par2)
    {
        if (par1 == 0)
        {
           // this.boiler.furnaceCookTime = par2;
        }

        if (par1 == 1)
        {
            //this.boiler.boilerRunTime = par2;
        }

    }

    public boolean canInteractWith(EntityPlayer par1EntityPlayer)
    {
        return this.boiler.isUseableByPlayer(par1EntityPlayer);
    }

    /**
     * Called to transfer a stack from one inventory to the other eg. when shift clicking.
     */
    public ItemStack transferStackInSlot(int par1)
    {        
        return null;
    }
}
