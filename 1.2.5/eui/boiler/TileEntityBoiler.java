package net.minecraft.src.eui.boiler;
import net.minecraft.src.*;
import net.minecraft.src.eui.BlockMachine;
import net.minecraft.src.eui.TileEntityMachine;
import net.minecraft.src.eui.api.*;
import net.minecraft.src.eui.burner.TileEntityFireBox;
import net.minecraft.src.forge.ForgeHooks;
import net.minecraft.src.forge.ISidedInventory;
import net.minecraft.src.pipes.api.ILiquidConsumer;
import net.minecraft.src.pipes.api.ILiquidProducer;
import net.minecraft.src.universalelectricity.UniversalElectricity;

public class TileEntityBoiler extends TileEntityMachine implements IInventory, ISidedInventory,ILiquidProducer, ILiquidConsumer
{
	
	    /**
	     * The ItemStacks that hold the items currently being used in the furnace
	     */
	    private ItemStack[] furnaceItemStacks = new ItemStack[1];

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
	    int count = 0;
	    boolean hullHeated = false;
	    TileEntity[] connectedBlocks = {null, null, null, null, null, null};
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
	   @Override
	    public void updateEntity(){
	    	count++;
	    	if(count >= 20){
	    	isBeingHeated = getIsHeated();	    	
	        addWater();//adds water from container slot
			shareWater();
			count = 0;
	    	}
	    	
	    	//changed hullHeat max depending on contents of boiler
	    	if(waterStored > 0)
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
	    public void shareWater()	
		{
			int wSum = getStoredLiquid(1); //pre-sets the sum to the first tanks current volume
			int tankCount = 1; //amount of tanks around this tank, used to get avarage liquid ammount
			boolean bottom = false; // whether or not this tanks need to worry about what is bellow it
			TileEntity entityBellow = worldObj.getBlockTileEntity(this.xCoord,this.yCoord-1, this.zCoord);
			TileEntity entityAbove = worldObj.getBlockTileEntity(this.xCoord,this.yCoord+1, this.zCoord);
			//checks wether or not the block bellow it is a tank to move liquid too
			if(entityBellow instanceof TileEntityBoiler)
			{
				int bWater = ((TileEntityBoiler) entityBellow).getStoredLiquid(1);
				int bMax = ((TileEntityBoiler) entityBellow).getLiquidCapacity(1);
				//checks if that tank has room to get liquid.
				
					if(bWater < bMax)
					{
						int emptyVol = Math.max( bMax - bWater,0);
						int tradeVol = Math.min(emptyVol, waterStored);
						int rejected = ((TileEntityBoiler) entityBellow).onReceiveLiquid(1, tradeVol, (byte) 1);
						waterStored = Math.max(waterStored - rejected,0);
						wSum -= rejected;
					}
				else 
				{
					bottom = true;
				}
			}
			else
			{
				//there was no tank bellow this tank
				bottom = true;
			}			
			//if this is the bottom tank or bottom tank is full then trade liquid with tanks around it.
			if(bottom)
			{
			//get average water around center tank
			for(int i = 0; i<4;i++)
			{
				int x = this.xCoord;
				int z = this.zCoord;
				//switch to check each side TODO rewrite for side values 
				switch(i)	
				{
				case 0: --x;
				case 1: ++x;
				case 2: --z;
				case 3: ++z;
				}
				TileEntity entity = worldObj.getBlockTileEntity(x,this.yCoord, z);
				if(entity instanceof TileEntityBoiler)
				{
					//if is a tank add to the sum
					wSum += ((TileEntityBoiler) entity).getStoredLiquid(1);
					tankCount += 1;
				}
			}
			}
			//transfers water
			for(int i = 0; i<4;i++)
			{
				int average = wSum / tankCount;// takes the sum and makes it an average
				int x2 = this.xCoord;
				int z2 = this.zCoord;
				int tradeSum = 0;
				//switch to check each side TODO rewrite for side values 
				switch(i)	
				{
				case 0: --x2;
				case 1: ++x2;
				case 2: --z2;
				case 3: ++z2;
				}
				TileEntity entity = worldObj.getBlockTileEntity(x2,this.yCoord, z2);
				if(entity instanceof TileEntityBoiler)
				{
					int targetW = ((TileEntityBoiler) entity).getStoredLiquid(1);
					if(targetW < average)
					{
						tradeSum = Math.min(average, waterStored); //gets the ammount to give to the target tank
						int rejectedAm = ((TileEntityBoiler) entity).onReceiveLiquid(1, tradeSum, (byte) i); //send that ammount with safty incase some comes back
						waterStored =rejectedAm + waterStored - tradeSum; //counts up current water sum after trade
					}
				}
			}
			if(entityAbove instanceof TileEntityBoiler)
			{
				int bWater = ((TileEntityBoiler) entityAbove).getStoredLiquid(1);
				int bMax = ((TileEntityBoiler) entityAbove).getLiquidCapacity(1);
				if(bottom && waterStored > 0)
				{
					if(bWater < bMax)
					{
						int emptyVolS = Math.max( bMax - bWater,0);
						int tradeVolS = Math.min(emptyVolS, steamStored);
						int rejectedS = ((TileEntityBoiler) entityAbove).addSteam(tradeVolS);
						waterStored = Math.max(waterStored - rejectedS,0);
						wSum -= rejectedS;
					}				
				}
			}
			
									
					
					
					
		}
	    public int addSteam(int watt) {
	    	int rejectedElectricity = Math.max((this.steamStored + watt) - steamMax, 0);
			this.steamStored += watt - rejectedElectricity;
			return rejectedElectricity;			
		}
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
	        return 0;
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
			int rejectedElectricity = Math.max((this.waterStored + vol) - this.getLiquidCapacity(1), 0);
			 this.waterStored += vol - rejectedElectricity;
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
			if(type == 0)
			{
				return steamMax;
			}
			return 0;
		}
		@Override
		public int onProduceLiquid(int type, int maxVol, int side) {			
			if(type == 0)
			{
				if(steamStored > 1)
				{
					this.steamStored -= 1;
					return 1;
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
