package net.minecraft.src.eui.turbine;
import net.minecraft.src.eui.TileEntityMachine;
import net.minecraft.src.eui.api.*;
import net.minecraft.src.forge.ForgeHooks;
import net.minecraft.src.*;
import net.minecraft.src.universalelectricity.*;
import net.minecraft.src.forge.ISidedInventory;

public class TileEntityGenerator extends TileEntityMachine implements UEIProducer,ISteamConsumer,IWaterProducer, IInventory, ISidedInventory
{
	//Maximum possible generation rate of watts in SECONDS
	public int maxGenerateRate = 1000;
	public int waterStored = 0;
	public int steamStored = 0;
	public int steamConsumed = 0;
	//Current generation rate based on hull heat. In TICKS.
	public float generateRate = 0;
	public UETileEntityConductor connectedWire = null;
	 /**
     * The number of ticks that a fresh copy of the currently-burning item would keep the furnace burning for
     */
    public int itemCookTime = 0;
	 /**
     * The ItemStacks that hold the items currently being used in the battery box
     */
    private ItemStack[] containingItems = new ItemStack[1];
    
    @Override
	public int onProduceElectricity(int maxWatts, int voltage, byte side)
    {
		//Only produce electricity on the back side.
    	if(canProduceElectricity(side) && maxWatts > 0)
		{
	        return Math.min(maxWatts, (int)generateRate);
		}
    	return 0;
	}
    
    @Override
    public boolean canProduceElectricity(byte side)
    {
    	return true;
    }
    
    /**
     * Allows the entity to update its state. Overridden in most subclasses, e.g. the mob spawner uses this to count
     * ticks and creates a new spawn inside its implementation.
     */
    public void updateEntity()
    {
    	//Check nearby blocks and see if the conductor is full. If so, then it is connected
    	TileEntity tileEntity = UEBlockConductor.getUEUnit(this.worldObj, this.xCoord, this.yCoord, this.zCoord, UniversalElectricity.getOrientationFromSide(this.getDirection(), (byte)2));
    	
    	if(tileEntity instanceof UETileEntityConductor)
    	{
    		if(((UETileEntityConductor)tileEntity).closestConsumer != null)
    		{
    			this.connectedWire = (UETileEntityConductor)tileEntity;
    		}
    		else
        	{
        		this.connectedWire = null;
        	}
    	}
    	else
    	{
    		this.connectedWire = null;
    	}
    	
    	if (!this.worldObj.isRemote)
        {
	    	//The top slot is for recharging items. Check if the item is a electric item. If so, recharge it.
	    	if (this.containingItems[0] != null)
	        {
	            if(this.containingItems[0].getItem().shiftedIndex == Item.bucketEmpty.shiftedIndex)
	            {
	               if(this.waterStored > 0)
	               {
	            		this.containingItems[0] = new ItemStack(Item.bucketWater,1);
	            		--waterStored;
	            	}
	            }
	        }
        }
    	//Starts generating electricity if the device is heated up
    	if (this.itemCookTime > 0)
        {
            this.itemCookTime --;
            
            if(this.connectedWire != null && this.connectedWire.getStoredElectricity() < this.connectedWire.getElectricityCapacity())
            {
            	this.generateRate = (float)Math.min(this.generateRate+Math.min((this.generateRate)*0.01+0.015, 0.05F), this.maxGenerateRate/20);
            }
        }
    	else
    	{
    		if(steamStored > 0)
    		{
    			--steamStored;
    			++steamConsumed;
    			if(steamConsumed == mod_EUIndustry.steamOutBoiler)
    			{
    			++waterStored;
    			steamConsumed = 0;
    			}
    		itemCookTime = itemCookTime + 65;
    		}
    	}

    	if(this.connectedWire == null || this.itemCookTime <= 0)
    	{
        	this.generateRate = (float)Math.max(this.generateRate-0.05, 0);
        }
    }
    /**
     * Reads a tile entity from NBT.
     */
    public void readFromNBT(NBTTagCompound par1NBTTagCompound)
    {
    	super.readFromNBT(par1NBTTagCompound);
    	this.itemCookTime = par1NBTTagCompound.getInteger("itemCookTime");
    	this.waterStored = par1NBTTagCompound.getInteger("waterStored");
    	this.steamConsumed = par1NBTTagCompound.getInteger("steamConsumed");
    	this.steamStored = par1NBTTagCompound.getInteger("steamStored");
    	this.generateRate = par1NBTTagCompound.getFloat("generateRate");
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
    	par1NBTTagCompound.setInteger("waterStored", (int)this.waterStored);
    	par1NBTTagCompound.setInteger("steamConsumed", (int)this.steamConsumed);
    	par1NBTTagCompound.setInteger("steamStored", (int)this.steamStored);
    	par1NBTTagCompound.setFloat("generateRate", (int)this.generateRate);
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
	public int onReceiveSteam(int vol, byte side) {
		
		
			if(steamStored + vol <= 100)
			{
			steamStored = steamStored + vol;
			return 0;
			}
		return vol;
		
	}

	@Override
	public boolean canRecieveSteam(byte side) {
		
		return true;
	}

	@Override
	public int getStoredSteam() {
		
		return this.steamStored;		
	}

	@Override
	public int getSteamCapacity() {
		return 100;
	}

	@Override
	public int onProduceWater(int maxVol, int side) {
		
			if(this.waterStored > 0)
			{
				--waterStored;
				return 1;
			}
		
		return 0;
	}

	@Override
	public boolean canProduceWater(byte side) {
		
			return true;
	}

	
	@Override
	public int getVolts() {
		// TODO Auto-generated method stub
		return 120;
	}
}
