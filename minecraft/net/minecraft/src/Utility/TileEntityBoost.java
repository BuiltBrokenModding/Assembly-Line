package net.minecraft.src.Utility;
import net.minecraft.src.eui.TileEntityMachine;
import net.minecraft.src.eui.api.*;
import net.minecraft.src.forge.ForgeHooks;
import net.minecraft.src.*;
import net.minecraft.src.universalelectricity.*;
import net.minecraft.src.forge.ISidedInventory;

public class TileEntityBoost extends TileEntityTotum implements UEIConsumer, IInventory, ISidedInventory
{
	//Maximum possible generation rate of watts in SECONDS
	public UETileEntityConductor connectedWire = null;
	 /**
     * The number of ticks that a fresh copy of the currently-burning item would keep the furnace burning for
     */
    public int itemCookTime = 0;
    public int eStored = 0;
    public int hStored = 0;
    public int tCount = 0;
	 /**
     * The ItemStacks that hold the items currently being used in the battery box
     */
    private ItemStack[] containingItems = new ItemStack[1];
    
    /**
     * Allows the entity to update its state. Overridden in most subclasses, e.g. the mob spawner uses this to count
     * ticks and creates a new spawn inside its implementation.
     */
    public void updateEntity()
    {
    	if(eStored >= 1000 && hStored < 100)
    	{
    		eStored -= 1000;
    		hStored += 1;
    	}
    	if(hStored > 0 && tCount > 40)
    	{
    		if(this.containingItems[0] != null && this.containingItems[0].itemID == 373)
    		{
    			
    		EntityPlayer player = this.worldObj.getClosestPlayer(xCoord, yCoord, zCoord, 4.0F);
    		if(player != null){
    			//player.addPotionEffect(par1PotionEffect);
    			hStored -= 1;
    			tCount = 0;
    		}
    		}
    		
    		
    	}
    	tCount++;
    }
    /**
     * Reads a tile entity from NBT.
     */
    public void readFromNBT(NBTTagCompound par1NBTTagCompound)
    {
    	super.readFromNBT(par1NBTTagCompound);
    	this.itemCookTime = par1NBTTagCompound.getInteger("itemCookTime");
    	this.eStored = par1NBTTagCompound.getInteger("EU");
    	this.hStored = par1NBTTagCompound.getInteger("HP");
    	NBTTagList var2 = par1NBTTagCompound.getTagList("Items");
        this.containingItems = new ItemStack[this.getSizeInventory()];
        for (int var3 = 0; var3 < var2.tagCount(); ++var3)
        {
            NBTTagCompound var4 = (NBTTagCompound)var2.tagAt(var3);
            byte var5 = var4.getByte("Slot");
            if (var5 >= 0 && var5 < this.containingItems.length)
            {
                this.containingItems[var5] = ItemStack.loadItemStackFromNBT(var4);
            }
        }
    }
    /**
     * Writes a tile entity to NBT.
     */
    public void writeToNBT(NBTTagCompound par1NBTTagCompound)
    {
    	super.writeToNBT(par1NBTTagCompound);
    	par1NBTTagCompound.setInteger("itemCookTime", (int)this.itemCookTime);
    	par1NBTTagCompound.setInteger("EU", (int)this.eStored);
    	par1NBTTagCompound.setInteger("HP", (int)this.hStored);
    	NBTTagList var2 = new NBTTagList();
        for (int var3 = 0; var3 < this.containingItems.length; ++var3)
        {
            if (this.containingItems[var3] != null)
            {
                NBTTagCompound var4 = new NBTTagCompound();
                var4.setByte("Slot", (byte)var3);
                this.containingItems[var3].writeToNBT(var4);
                var2.appendTag(var4);
            }
        }
        par1NBTTagCompound.setTag("Items", var2);
    }
	@Override
	public int getStartInventorySide(int side)
	{
		if (side == 0)
        {
            return 1;
        }
        if (side == 1)
        {
            return 0;
        }
        return 2;
	}
	@Override
	public int getSizeInventorySide(int side) { return getSizeInventory(); }
	@Override
	public int getSizeInventory() { return this.containingItems.length; }
	@Override
	public ItemStack getStackInSlot(int par1) { return this.containingItems[par1]; }
	@Override
	public ItemStack decrStackSize(int par1, int par2)
	{
		if (this.containingItems[par1] != null)
        {
            ItemStack var3;
            if (this.containingItems[par1].stackSize <= par2)
            {
                var3 = this.containingItems[par1];
                this.containingItems[par1] = null;
                return var3;
            }
            else
            {
                var3 = this.containingItems[par1].splitStack(par2);
                if (this.containingItems[par1].stackSize == 0)
                {
                    this.containingItems[par1] = null;
                }
                return var3;
            }
        }
        else
        {
            return null;
        }
	}
	@Override
	public ItemStack getStackInSlotOnClosing(int par1)
	{
		if (this.containingItems[par1] != null)
        {
            ItemStack var2 = this.containingItems[par1];
            this.containingItems[par1] = null;
            return var2;
        }
        else
        {
            return null;
        }
	}
	@Override
	public void setInventorySlotContents(int par1, ItemStack par2ItemStack)
	{
		this.containingItems[par1] = par2ItemStack;
        if (par2ItemStack != null && par2ItemStack.stackSize > this.getInventoryStackLimit())
        {
            par2ItemStack.stackSize = this.getInventoryStackLimit();
        }
	}
	@Override
	public String getInvName() {
		return "SteamGen";
	}
	@Override
	public int getInventoryStackLimit()
	{
		return 64;
	}
	@Override
	public boolean isUseableByPlayer(EntityPlayer par1EntityPlayer)
	{
        return this.worldObj.getBlockTileEntity(this.xCoord, this.yCoord, this.zCoord) != this ? false : par1EntityPlayer.getDistanceSq((double)this.xCoord + 0.5D, (double)this.yCoord + 0.5D, (double)this.zCoord + 0.5D) <= 64.0D;
	}
	@Override
	public void openChest() { }
	@Override
	public void closeChest() { }
	

	
	@Override
	public int getVolts() {
		// TODO Auto-generated method stub
		return 12000;
	}
	@Override
	public int onReceiveElectricity(int watts, int voltage, byte side) {
		if(this.eStored < this.getElectricityCapacity())
		{
	    	int rejectedElectricity = Math.max((this.eStored + watts) - this.getElectricityCapacity(), 0);
			this.eStored += watts - rejectedElectricity;
			return rejectedElectricity;
		}
    	return watts;
	}
	@Override
	public boolean canReceiveElectricity(byte side) {
		if(side == 0)
		{
			return true;
		}
		return false;
	}
	@Override
	public int getStoredElectricity() {
		return eStored;
	}
	@Override
	public int getElectricityCapacity() {
		return 1000;
	}
	@Override
	public void onDisable(int duration) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public boolean isDisabled() {
		// TODO Auto-generated method stub
		return false;
	}
	
}
