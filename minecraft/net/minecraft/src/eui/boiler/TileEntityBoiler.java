package net.minecraft.src.eui.boiler;
import net.minecraft.src.*;
import net.minecraft.src.eui.BlockMachine;
import net.minecraft.src.eui.TileEntityMachine;
import net.minecraft.src.eui.api.*;
import net.minecraft.src.eui.burner.TileEntityFireBox;
import net.minecraft.src.eui.pipes.api.ILiquidConsumer;
import net.minecraft.src.eui.pipes.api.ILiquidProducer;
import net.minecraft.src.forge.ForgeHooks;
import net.minecraft.src.forge.ISidedInventory;
import net.minecraft.src.universalelectricity.UniversalElectricity;

public class TileEntityBoiler extends TileEntityMachine implements IInventory, ISidedInventory,ILiquidProducer, ILiquidConsumer
{
	
	    /**
	     * The ItemStacks that hold the items currently being used in the furnace
	     */
	    private ItemStack[] furnaceItemStacks = new ItemStack[3];

	    /** The number of ticks that the boiler will keep burning */
	    public int RunTime = 0;
	    /** The ammount of energy stored before being add to run Timer */
	    public int energyStore = 0;
	    /** The ammount of water stored */
	    public int waterStored = 0;
	    /** The ammount of steam stored */
	    public int steamStored = 0;
	    /** The ammount of heat stored */
	    public int heatStored = 0;
	    public int heatMax = 10000;
	    /** The ammount of heat stored */
	    public int hullHeat = 0;
	    public int hullHeatMax = 10000;
	    private int heatTick = 0;
	    public TileEntity[] connectedBlocks = {null, null, null, null, null, null};
	    public TileEntity[] connectedFaces = {null, null, null, null, null, null, null, null};
	    int steamMax = 140;
	    public boolean isBeingHeated = false;
	    /**
	     * Returns the number of slots in the inventory.
	     */
	    public int getSizeInventory()
	    {
	        return this.furnaceItemStacks.length;
	    }

	    /**
	     * Returns the stack in slot i
	     */
	    public ItemStack getStackInSlot(int par1)
	    {
	        return this.furnaceItemStacks[par1];
	    }

	    /**
	     * Decrease the size of the stack in slot (first int arg) by the amount of the second int arg. Returns the new
	     * stack.
	     */
	    public ItemStack decrStackSize(int par1, int par2)
	    {
	        if (this.furnaceItemStacks[par1] != null)
	        {
	            ItemStack var3;

	            if (this.furnaceItemStacks[par1].stackSize <= par2)
	            {
	                var3 = this.furnaceItemStacks[par1];
	                this.furnaceItemStacks[par1] = null;
	                return var3;
	            }
	            else
	            {
	                var3 = this.furnaceItemStacks[par1].splitStack(par2);

	                if (this.furnaceItemStacks[par1].stackSize == 0)
	                {
	                    this.furnaceItemStacks[par1] = null;
	                }

	                return var3;
	            }
	        }
	        else
	        {
	            return null;
	        }
	    }

	    /**
	     * When some containers are closed they call this on each slot, then drop whatever it returns as an EntityItem -
	     * like when you close a workbench GUI.
	     */
	    public ItemStack getStackInSlotOnClosing(int par1)
	    {
	        if (this.furnaceItemStacks[par1] != null)
	        {
	            ItemStack var2 = this.furnaceItemStacks[par1];
	            this.furnaceItemStacks[par1] = null;
	            return var2;
	        }
	        else
	        {
	            return null;
	        }
	    }

	    /**
	     * Sets the given item stack to the specified slot in the inventory (can be crafting or armor sections).
	     */
	    public void setInventorySlotContents(int par1, ItemStack par2ItemStack)
	    {
	        this.furnaceItemStacks[par1] = par2ItemStack;

	        if (par2ItemStack != null && par2ItemStack.stackSize > this.getInventoryStackLimit())
	        {
	            par2ItemStack.stackSize = this.getInventoryStackLimit();
	        }
	    }

	    /**
	     * Returns the name of the inventory.
	     */
	    public String getInvName()
	    {
	        return "container.boiler";
	    }

	    /**
	     * Reads a tile entity from NBT.
	     */
	    public void readFromNBT(NBTTagCompound par1NBTTagCompound)
	    {
	        super.readFromNBT(par1NBTTagCompound);
	        NBTTagList var2 = par1NBTTagCompound.getTagList("Items");
	        this.furnaceItemStacks = new ItemStack[this.getSizeInventory()];

	        for (int var3 = 0; var3 < var2.tagCount(); ++var3)
	        {
	            NBTTagCompound var4 = (NBTTagCompound)var2.tagAt(var3);
	            byte var5 = var4.getByte("Slot");

	            if (var5 >= 0 && var5 < this.furnaceItemStacks.length)
	            {
	                this.furnaceItemStacks[var5] = ItemStack.loadItemStackFromNBT(var4);
	            }
	        }

	        this.RunTime = par1NBTTagCompound.getShort("BurnTime");
	        this.energyStore = par1NBTTagCompound.getInteger("energyStore");
	        this.steamStored = par1NBTTagCompound.getInteger("steamStore");
	        this.heatStored = par1NBTTagCompound.getInteger("heatStore");
	        this.waterStored = par1NBTTagCompound.getInteger("waterStore");
	        this.hullHeat = par1NBTTagCompound.getInteger("hullHeat");
	    }

	    /**
	     * Writes a tile entity to NBT.
	     */
	    public void writeToNBT(NBTTagCompound par1NBTTagCompound)
	    {
	        super.writeToNBT(par1NBTTagCompound);
	        par1NBTTagCompound.setShort("BurnTime", (short)this.RunTime);
	        par1NBTTagCompound.setInteger("energyStore", (int)this.energyStore);
	        par1NBTTagCompound.setInteger("steamStore", (int)this.steamStored);
	        par1NBTTagCompound.setInteger("heatStore", (int)this.heatStored);
	        par1NBTTagCompound.setInteger("waterStore", (int)this.waterStored);
	        par1NBTTagCompound.setInteger("hullHeat", (int)this.hullHeat);
	        NBTTagList var2 = new NBTTagList();

	        for (int var3 = 0; var3 < this.furnaceItemStacks.length; ++var3)
	        {
	            if (this.furnaceItemStacks[var3] != null)
	            {
	                NBTTagCompound var4 = new NBTTagCompound();
	                var4.setByte("Slot", (byte)var3);
	                this.furnaceItemStacks[var3].writeToNBT(var4);
	                var2.appendTag(var4);
	            }
	        }

	        par1NBTTagCompound.setTag("Items", var2);
	    }

	    /**
	     * Returns the maximum stack size for a inventory slot. Seems to always be 64, possibly will be extended. *Isn't
	     * this more of a set than a get?*
	     */
	    public int getInventoryStackLimit()
	    {
	        return 64;
	    }	    

	    /**
	     * Allows the entity to update its state. Overridden in most subclasses, e.g. the mob spawner uses this to count
	     * ticks and creates a new spawn inside its implementation.
	     */
	    private boolean getIsHeated() {
	    	TileEntity blockEntity =  worldObj.getBlockTileEntity(this.xCoord, this.yCoord - 1, this.zCoord);
	    	if(blockEntity instanceof TileEntityFireBox)
	    	{
	    		return true;
	    	}
	    	else
	    	{
	    		return false;
	    	}
		}
	    private int getTextureFront() {
	    	
			return 0;
		}	
	    public TileEntity getFacingBoilers(int i)
		{	
			
				int x = this.xCoord;
		    	int y = this.yCoord;
		    	int z = this.zCoord; 
	    		switch(i){
				case 0: y = y - 1;break;
				case 1: y = y + 1;break;
				case 2: z = z + 1;break;
				case 3: z = z - 1;break;
				case 4: x = x + 1;break;
				case 5: x = x - 1;break;
	    		}
			TileEntity aEntity = worldObj.getBlockTileEntity(x, y, z);			
			if(aEntity instanceof TileEntityBoiler)
			{
				return aEntity;
			}
			
			return null;
		}
	    int count = 0;
	    public void updateEntity(){
	    	if(count == 20){
	    	isBeingHeated = getIsHeated();
			addConnection();
			shareResources();
			count = 0;
	    	}
	    	else
	    	{
	    		count++;
	    	}
	    	boolean hullHeated = false;
	    	//changed hullHeat max depending on contents of boiler
	    	if(waterStored>0)
	    	{
	    		hullHeatMax = 4700;
	    		if(hullHeat > hullHeatMax)
	    		{
	    			hullHeat = 4700;
	    		}
	    	}
	    	else
	    	{
	    		hullHeatMax = 10000;	    		
	    	}
	    	//Checks if the hull is heated
			if(hullHeat >= hullHeatMax)
	    	{
				hullHeated = true;
				}
	    	else
	    	{
	    	hullHeat = Math.min(hullHeat + heatStored, hullHeatMax);
	    	}
			//checks if heat level hit max
			if(hullHeat >= 10000)
			{
				if(heatTick >= 1200)
				{
					// TODO remove block and set fire
					heatTick = 0;
				}
				else
				{
					heatTick += 1;
				}
			}
			//adds water from container slot
	        addWater();
	        
	        int heatNeeded = mod_EUIndustry.boilerHeat; // kilo joules	
	        //if hull heated do work
			if(hullHeated)
			{
					if(heatStored > mod_EUIndustry.fireOutput)
					{					
					if(waterStored >= 1){
						if(heatStored >= heatNeeded)
						{
					heatStored = Math.max(heatStored - heatNeeded, 0);
					--waterStored;
					steamStored = Math.min(steamStored + mod_EUIndustry.steamOutBoiler,this.steamMax);	
						}						
					}
					else
					{
						heatStored = 0;
					}
					}
			}
			TileEntity blockE = worldObj.getBlockTileEntity(xCoord, yCoord -1, zCoord);
			if(blockE instanceof TileEntityFireBox)
			{
				if(!hullHeated || waterStored > 0)
				{
				heatStored = (int) Math.min((heatStored + ((TileEntityFireBox)blockE).onProduceHeat(mod_EUIndustry.fireOutput, 1)), heatMax);
				}
			}
			
	    }	  
	    	int transferW  = 0;
			int transferS  = 0;
			int transferH  = 0;
	    public void shareResources()
	    {
	    	
	    	for(int i = 0; i<6; i++)
			{
			if(connectedBlocks[i] instanceof TileEntityBoiler)
			{			
			TileEntityBoiler connectedConsumer = (TileEntityBoiler)connectedBlocks[i];	
								//add steam to other boiler if less
			boolean canTradeSteam;
			if( i ==0)
			{
				if(this.steamStored == this.steamMax)
				{
					canTradeSteam = true;
				}
				else
				{
					canTradeSteam = false;	
				}
			}
			else
			{
			canTradeSteam = true;
			}
			if(canTradeSteam)
			{
				if( this.steamStored > 0)
				{
						int SSum = (this.steamStored + connectedConsumer.steamStored)/2;
						if(i == 1 && connectedConsumer.steamStored < connectedConsumer.steamMax && this.steamStored > 0){
						if(this.steamStored >= 10 )
						{														
							
								int rejectedW = connectedConsumer.addSteam(10);
								this.steamStored = Math.max(Math.min(this.steamStored - 10 + rejectedW, this.steamMax), 0);
						}
						else
						{
							int rejectedW = connectedConsumer.addSteam(this.steamStored);
							this.steamStored = Math.max(Math.min(this.steamStored - this.steamStored + rejectedW, this.steamMax), 0);
						}
						}
						if(this.steamStored > connectedConsumer.steamStored)
						{														
							transferS = SSum - connectedConsumer.steamStored;
							int rejectedS = connectedConsumer.addSteam(transferS);
						    this.steamStored = Math.max(Math.min(this.steamStored - transferS + rejectedS, 140), 0);							
						}
				}
			}
				//add water to other boiler if less
				if( this.waterStored > 0)
				{	
					boolean canTradeWater;
					if( i ==1)
					{
						if(this.waterStored == this.getLiquidCapacity(1))
						{
							canTradeWater = true;
						}
						else
						{
							canTradeWater = false;	
						}
					}
					else
					{
					canTradeWater = true;
					}
					if(canTradeWater)
					{					
						int WSum = (this.waterStored + connectedConsumer.waterStored)/2;
						if(i == 0 && this.waterStored > 0 && connectedConsumer.waterStored < connectedConsumer.getLiquidCapacity(1))
						{														
							
								int rejectedW = connectedConsumer.addwater(1);
								this.waterStored = Math.max(Math.min(this.waterStored - 1 + rejectedW, this.getLiquidCapacity(1)), 0);
						}
						if(this.waterStored > connectedConsumer.waterStored)
						{														
							transferW =Math.round(WSum - connectedConsumer.waterStored);
							if(transferW > 0)
							{
								int rejectedW = connectedConsumer.addwater(transferW);
								this.waterStored = Math.max(Math.min(this.waterStored - transferW + rejectedW, this.getLiquidCapacity(1)), 0);
							}
						}
						
						
						}
						
					}
				
				
				//add heat to other boiler if less
				boolean canTradeHeat;
				
					if(this.heatStored == heatMax  || !isBeingHeated)
					{
						canTradeHeat = true;
					}
					else
					{						
						canTradeHeat = false;							
					}
				
				if(canTradeHeat)
				{
					if( this.heatStored > 0)
					{					
							
							if(this.heatStored > connectedConsumer.heatStored )
							{
								int HSum = (this.heatStored + connectedConsumer.heatStored)/2;							
								 transferH = HSum - connectedConsumer.heatStored;
								int rejectedH = connectedConsumer.onReceiveHeat(transferH);
								this.heatStored = Math.max(Math.min(this.waterStored - transferW + rejectedH, heatMax), 0);
							}	
							
						
					}
				}
			}
			}
			
			
		  }		
			
	    
		private int onProduceWater(int t, int i) {
			if(waterStored - t > 0)
			{
				waterStored = waterStored - t;
				return t;
			}
			
			return 0;
			
		}	
		public void addConnection()
		{			
			for(int i = 0; i<6; i++)
			{				
	    		
			TileEntity aEntity = getSteamMachine(i);			
			if(aEntity instanceof TileEntityBoiler)
			{
				this.connectedBlocks[i] = aEntity;
			}
			else
			{
				this.connectedBlocks[i] = null;
			}
			}
			}
		
	    
	    public int addSteam(int watt) {
	    	int rejectedElectricity = Math.max((this.steamStored + watt) - steamMax, 0);
			this.steamStored += watt - rejectedElectricity;
			return rejectedElectricity;			
		}
	    public int addwater(int watt) {
			int rejectedElectricity = Math.max((this.waterStored + watt) - this.getLiquidCapacity(1), 0);
			this.waterStored += watt - rejectedElectricity;
			return rejectedElectricity;				
		}
	    public int onReceiveHeat(int watt) {
	    	if(heatStored < heatMax)
	    	{
	    	int rejectedElectricity = Math.max((this.heatStored + watt) - heatMax, 0);
			this.heatStored += watt - rejectedElectricity;
			return rejectedElectricity;
	    	}
	    	return watt;
		}
		/**
	     * adds water too the system
	     */
	    private void addWater() {
	    	if (this.furnaceItemStacks[0] != null)
            {
                if(this.furnaceItemStacks[0].isItemEqual(new ItemStack(Item.bucketWater,1)))
                {
                	if((int)waterStored < getLiquidCapacity(1))
                	{                		
                		++waterStored;
                	this.furnaceItemStacks[0] = new ItemStack(Item.bucketEmpty,1);
                	this.onInventoryChanged();
                	}
                }
            }
			
		}

		/**
	     * Do not make give this method the name canInteractWith because it clashes with Container
	     */
	    public boolean isUseableByPlayer(EntityPlayer par1EntityPlayer)
	    {
	        return this.worldObj.getBlockTileEntity(this.xCoord, this.yCoord, this.zCoord) != this ? false : par1EntityPlayer.getDistanceSq((double)this.xCoord + 0.5D, (double)this.yCoord + 0.5D, (double)this.zCoord + 0.5D) <= 64.0D;
	    }

	    public void openChest() {}

	    public void closeChest() {}

	    @Override
	    public int getStartInventorySide(int side) 
	    {
	        if (side == 0) return 1;
	        if (side == 1) return 0;
	        return 2;
	    }

	    @Override
	    public int getSizeInventorySide(int side) 
	    {
	        return 1;
	    }
		public int precentHeated() {
			int var1;
			if(hullHeat < 100)
			{
			var1 = (int)(100 *(hullHeat/100));
			}
			else
			{
				var1 = 100;
			}
			return var1;
		}
		@Override
		public int onReceiveLiquid(int type, int vol, byte side) {
			if(type == 1)
			{
			int rejectedElectricity = Math.max((this.waterStored + vol) - 14, 0);
			 this.waterStored = vol - rejectedElectricity;
			return rejectedElectricity;
			}
			return vol;
		}

		@Override
		public boolean canRecieveLiquid(int type, byte side) {
			if(type == 1)
			{
				return true;
			}
			return false;
		}

		@Override
		public int getStoredLiquid(int type) {
			if(type == 1)
			{
				return this.waterStored;
			}
			if(type == 0)
			{
				return this.steamStored;
			}
			return 0;
		}

		@Override
		public int getLiquidCapacity(int type) {
			if(type ==1)
			{
				return 14;
			}
			return 0;
		}
		@Override
		public int onProduceLiquid(int type, int maxVol, int side) {
			if(count == 10 || count == 20)
			{
			if(type == 0)
			{
				if(steamStored > maxVol)
				{
					this.steamStored -= maxVol;
					return maxVol;
				}
			}
			}
			return 0;
		}

		@Override
		public boolean canProduceLiquid(int type, byte side) {
			if(type == 0)
			{
				return true;
			}
			return false;
		}

	}
