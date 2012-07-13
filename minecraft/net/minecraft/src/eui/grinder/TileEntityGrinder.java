package net.minecraft.src.eui.grinder;
import net.minecraft.src.*;
import net.minecraft.src.universalelectricity.electricity.IElectricUnit;
import net.minecraft.src.eui.TileEntityMachine;
import net.minecraft.src.forge.ForgeHooks;
import net.minecraft.src.forge.ISidedInventory;

public class TileEntityGrinder extends TileEntityMachine implements IElectricUnit, IInventory
{
	
	    /**
	     * The ItemStacks that hold the items currently being used in the furnace
	     */
	    private ItemStack[] furnaceItemStacks = new ItemStack[3];

	    /** The number of ticks that the furnace will keep burning */
	    public int GrinderRunTime = 0;
	    /** The ammount of energy stored before turning into runtimer */
	    public int energyStore = 0;

	    /**
	     * The number of ticks that a fresh copy of the currently-burning item would keep the furnace burning for
	     */
	    public int currentItemBurnTime = 0;

	    /** The number of ticks that the current item has been cooking for */
	    public int furnaceCookTime = 0;

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
	        return "container.furnace";
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

	        this.GrinderRunTime = par1NBTTagCompound.getShort("BurnTime");
	        this.furnaceCookTime = par1NBTTagCompound.getShort("CookTime");
	        this.energyStore = par1NBTTagCompound.getInteger("energyStore");
	    }

	    /**
	     * Writes a tile entity to NBT.
	     */
	    public void writeToNBT(NBTTagCompound par1NBTTagCompound)
	    {
	        super.writeToNBT(par1NBTTagCompound);
	        par1NBTTagCompound.setShort("BurnTime", (short)this.GrinderRunTime);
	        par1NBTTagCompound.setShort("CookTime", (short)this.furnaceCookTime);
	        par1NBTTagCompound.setInteger("energyStore", (int)this.energyStore);
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
	     * Returns an integer between 0 and the passed value representing how close the current item is to being completely
	     * cooked
	     */
	    public int getCookProgressScaled(int par1)
	    {
	        return this.furnaceCookTime * par1 / 600;
	    }

	    /**
	     * Returns an integer between 0 and the passed value representing how much burn time is left on the current fuel
	     * item, where 0 means that the item is exhausted and the passed value means that the item is fresh
	     */
	    public int getBurnTimeRemainingScaled(int par1)
	    {
	        
	        return this.GrinderRunTime * par1 / 120;
	    }

	    /**
	     * Returns true if the furnace is currently burning
	     */
	    public boolean isBurning()
	    {
	        return this.GrinderRunTime > 0;
	    }

	    /**
	     * Allows the entity to update its state. Overridden in most subclasses, e.g. the mob spawner uses this to count
	     * ticks and creates a new spawn inside its implementation.
	     */
	    public void updateEntity()
	    {
	        boolean var1 = this.GrinderRunTime > 0;
	        boolean var2 = false;
	        

	        if (!this.worldObj.isRemote)
	        {
	            if(this.GrinderRunTime < 120)
			{			        	
				int varE = (int) (this.energyStore / 50);
				if(GrinderRunTime + varE >= 120)
				{
				this.GrinderRunTime = this.GrinderRunTime  + varE;
				this.energyStore = this.energyStore - (varE * 150);
				}
			}
	        if (this.GrinderRunTime > 0)
	        {
	            --this.GrinderRunTime;
	        }
	            if (this.isBurning() && this.canSmelt())
	            {
	                ++this.furnaceCookTime;

	                if (this.furnaceCookTime == 600)
	                {
	                    this.furnaceCookTime = 0;
	                    this.smeltItem();
	                    var2 = true;
	                }
	            }
	            else
	            {
	                this.furnaceCookTime = 0;
	            }
	            
	        }

	        if (var2)
	        {
	            this.onInventoryChanged();
	        }
	    }

	    /**
	     * Returns true if the furnace can smelt an item, i.e. has a source item, destination stack isn't full, etc.
	     */
	    private boolean canSmelt()
	    {
	        if (this.furnaceItemStacks[0] == null)
	        {
	            return false;
	        }
	        else
	        {
	            ItemStack var1 = GrinderRecipes.smelting().getSmeltingResult(this.furnaceItemStacks[0]);
	            if(var1 == null)
	            	{
	            	return false;
	            	}
	            if(this.furnaceItemStacks[1] == null)
	            {
	            	return true;
	            }
	            if (!this.furnaceItemStacks[1].isItemEqual(var1))
	            	{
	            	return false;
	            	}
	            int result = furnaceItemStacks[1].stackSize + var1.stackSize;
	            return (result <= getInventoryStackLimit() && result <= var1.getMaxStackSize());
	        }
	    }

	    /**
	     * Turn one item from the furnace source stack into the appropriate smelted item in the furnace result stack
	     */
	    public void smeltItem()
	    {
	        if (this.canSmelt())
	        {
	            ItemStack var1 = GrinderRecipes.smelting().getSmeltingResult(this.furnaceItemStacks[0]);
	            if (this.furnaceItemStacks[1] == null)
	            {
	                this.furnaceItemStacks[1] = var1.copy();
	            }
	            if (this.furnaceItemStacks[1].isItemEqual(var1))
	            {
	                this.furnaceItemStacks[1].stackSize += var1.stackSize;
	            }

	            if (this.furnaceItemStacks[0].getItem().func_46056_k())
	            {
	                this.furnaceItemStacks[0] = new ItemStack(this.furnaceItemStacks[0].getItem().setFull3D());
	            }
	            else
	            {
	                --this.furnaceItemStacks[0].stackSize;
	            }

	            if (this.furnaceItemStacks[0].stackSize <= 0)
	            {
	                this.furnaceItemStacks[0] = null;
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
		public boolean canReceiveFromSide(byte side) {
			// TODO Auto-generated method stub
			return true;
		}
		@Override
		public float getVoltage() {
			// TODO Auto-generated method stub
			return 120;
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
		public float electricityRequest()
		{
			return Math.max(this.energyStore - 100,0);
			
		}

		
	}
