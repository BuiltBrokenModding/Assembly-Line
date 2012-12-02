package steampower;

import net.minecraft.src.EntityPlayer;
import net.minecraft.src.IInventory;
import net.minecraft.src.ItemStack;
import net.minecraft.src.NBTTagCompound;
import net.minecraft.src.NBTTagList;
import net.minecraft.src.Packet;
import net.minecraft.src.TileEntity;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.common.ISidedInventory;
import universalelectricity.core.Vector3;
import universalelectricity.prefab.network.PacketManager;
public class TileEntityMachine extends TileEntity implements  IInventory, ISidedInventory
{
	public int facing = 0;
	private int count = 0;
	public ItemStack[] storedItems = new ItemStack[this.getInvSize()];
	private int getInvSize() {
		return 1;
	}
	public int getDirection()
	{
		return this.facing;
	}
	
	public void setDirection(int i)
	{		
		this.facing = i;
	}
	
	@Override
	public void writeToNBT(NBTTagCompound par1NBTTagCompound)
	{
	        super.writeToNBT(par1NBTTagCompound);
	        par1NBTTagCompound.setInteger("facing", this.facing);
	        //inventory
	        NBTTagList var2 = new NBTTagList();

	        for (int var3 = 0; var3 < this.storedItems.length; ++var3)
	        {
	            if (this.storedItems[var3] != null)
	            {
	                NBTTagCompound var4 = new NBTTagCompound();
	                var4.setByte("Slot", (byte)var3);
	                this.storedItems[var3].writeToNBT(var4);
	                var2.appendTag(var4);
	            }
	        }

	        par1NBTTagCompound.setTag("Items", var2);
	}
	
	@Override
	public void readFromNBT(NBTTagCompound par1NBTTagCompound)
	{
		super.readFromNBT(par1NBTTagCompound);
		//inventory
        NBTTagList var2 = par1NBTTagCompound.getTagList("Items");
        this.storedItems = new ItemStack[this.getSizeInventory()];

        for (int var3 = 0; var3 < var2.tagCount(); ++var3)
        {
            NBTTagCompound var4 = (NBTTagCompound)var2.tagAt(var3);
            byte var5 = var4.getByte("Slot");

            if (var5 >= 0 && var5 < this.storedItems.length)
            {
                this.storedItems[var5] = ItemStack.loadItemStackFromNBT(var4);
            }
        }
        //vars
	        this.facing = par1NBTTagCompound.getInteger("facing");
	}
	@Override
    public boolean canUpdate()
    {
        return true;
    }
	public Object[] getSendData()
	{
		return new Object[]{};
	}
	public boolean needUpdate()
	{
		return true;
	}
	@Override
	public void updateEntity()
    {
		super.updateEntity();
		
			if(count ++ >= 10 && !worldObj.isRemote && needUpdate())
			{count = 0;
				Packet packet = PacketManager.getPacket(SteamPowerMain.channel,this,  getSendData());
				PacketManager.sendPacketToClients(packet, worldObj, Vector3.get(this), 40);
			}
    }
	
	
	//////////////////////////
	//I Inventory shit
	/////////////////////////
	public int getSizeInventory()
    {
        return this.storedItems.length;
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
     * Do not make give this method the name canInteractWith because it clashes with Container
     */
    public boolean isUseableByPlayer(EntityPlayer par1EntityPlayer)
    {
        return this.worldObj.getBlockTileEntity(this.xCoord, this.yCoord, this.zCoord) != this ? false : par1EntityPlayer.getDistanceSq((double)this.xCoord + 0.5D, (double)this.yCoord + 0.5D, (double)this.zCoord + 0.5D) <= 64.0D;
    }

    /**
     * Returns the stack in slot i
     */
    public ItemStack getStackInSlot(int par1)
    {
        return this.storedItems[par1];
    }
   
    public ItemStack decrStackSize(int par1, int par2)
    {
        if (this.storedItems[par1] != null)
        {
            ItemStack var3;

            if (this.storedItems[par1].stackSize <= par2)
            {
                var3 = this.storedItems[par1];
                this.storedItems[par1] = null;
                return var3;
            }
            else
            {
                var3 = this.storedItems[par1].splitStack(par2);

                if (this.storedItems[par1].stackSize == 0)
                {
                    this.storedItems[par1] = null;
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
        if (this.storedItems[par1] != null)
        {
            ItemStack var2 = this.storedItems[par1];
            this.storedItems[par1] = null;
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
        this.storedItems[par1] = par2ItemStack;

        if (par2ItemStack != null && par2ItemStack.stackSize > this.getInventoryStackLimit())
        {
            par2ItemStack.stackSize = this.getInventoryStackLimit();
        }
    }
	@Override
	public int getStartInventorySide(ForgeDirection side) {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public int getSizeInventorySide(ForgeDirection side) {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public String getInvName() {
		// TODO Auto-generated method stub
		return "SteamMachine";
	}
	@Override
	public void openChest() {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void closeChest() {
		// TODO Auto-generated method stub
		
	}
}