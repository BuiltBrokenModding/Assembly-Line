package net.minecraft.src.eui.grinder;
import net.minecraft.src.*;

public class ContainerGrinder extends Container
{
    private TileEntityGrinder grinder;
    private int lastCookTime = 0;
    private int lastBurnTime = 0;
    private int lastItemBurnTime = 0;

    public ContainerGrinder(InventoryPlayer par1InventoryPlayer, TileEntityGrinder par2TileEntityGrinder)
    {
        this.grinder = par2TileEntityGrinder;
        this.addSlot(new Slot(par2TileEntityGrinder, 0, 56, 17));
        //this.addSlot(new Slot(par2TileEntityGrinder, 1, 116, 35));
        this.addSlot(new SlotGrinder(par1InventoryPlayer.player, par2TileEntityGrinder, 1, 116, 35));
        int var3;

        for (var3 = 0; var3 < 3; ++var3)
        {
            for (int var4 = 0; var4 < 9; ++var4)
            {
                this.addSlot(new Slot(par1InventoryPlayer, var4 + var3 * 9 + 9, 8 + var4 * 18, 84 + var3 * 18));
            }
        }

        for (var3 = 0; var3 < 9; ++var3)
        {
            this.addSlot(new Slot(par1InventoryPlayer, var3, 8 + var3 * 18, 142));
        }
    }

    /**
     * Updates crafting matrix; called from onCraftMatrixChanged. Args: none
     */
    public void updateCraftingResults()
    {
        super.updateCraftingResults();

        for (int var1 = 0; var1 < this.crafters.size(); ++var1)
        {
            ICrafting var2 = (ICrafting)this.crafters.get(var1);

            if (this.lastCookTime != this.grinder.furnaceCookTime)
            {
                var2.updateCraftingInventoryInfo(this, 0, this.grinder.furnaceCookTime);
            }

            if (this.lastBurnTime != this.grinder.GrinderRunTime)
            {
                var2.updateCraftingInventoryInfo(this, 1, this.grinder.GrinderRunTime);
            }           
        }

        this.lastCookTime = this.grinder.furnaceCookTime;
        this.lastBurnTime = this.grinder.GrinderRunTime;
    }

    public void updateProgressBar(int par1, int par2)
    {
        if (par1 == 0)
        {
            this.grinder.furnaceCookTime = par2;
        }

        if (par1 == 1)
        {
            this.grinder.GrinderRunTime = par2;
        }

    }

    public boolean canInteractWith(EntityPlayer par1EntityPlayer)
    {
        return this.grinder.isUseableByPlayer(par1EntityPlayer);
    }

    /**
     * Called to transfer a stack from one inventory to the other eg. when shift clicking.
     */
    public ItemStack transferStackInSlot(int par1)
    {
        

        return null;
    }
}
