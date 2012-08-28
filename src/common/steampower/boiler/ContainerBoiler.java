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
