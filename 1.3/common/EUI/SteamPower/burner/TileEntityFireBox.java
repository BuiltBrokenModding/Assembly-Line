package EUI.SteamPower.burner;

import com.google.common.io.ByteArrayDataInput;

import universalelectricity.network.IPacketReceiver;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.IInventory;
import net.minecraft.src.Item;
import net.minecraft.src.ItemStack;
import net.minecraft.src.NBTTagCompound;
import net.minecraft.src.NBTTagList;
import net.minecraft.src.NetworkManager;
import net.minecraft.src.Packet250CustomPayload;
import net.minecraft.src.TileEntity;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.common.ISidedInventory;
import EUI.SteamPower.SteamPower;
import EUI.SteamPower.TileEntityMachine;
import EUI.SteamPower.api.IHeatProducer;
import EUI.SteamPower.boiler.*;

public class TileEntityFireBox extends TileEntityMachine implements IPacketReceiver,IInventory, ISidedInventory, IHeatProducer
{
	//max heat generated per second
   
	public boolean isConnected = false;
	public TileEntity[] connectedBlocks = {null, null, null, null, null, null}; 
	private int connectedUnits = 0;
	public static int maxGenerateRate = 250;
	//Current generation rate based on hull heat. In TICKS.
	public int generateRate = 0;
    int count = 0;
    public int itemCookTime = 0;
    public ItemStack[] containingItems = new ItemStack[1];   
    public void updateEntity()
    {if (!this.worldObj.isRemote){
    	
    	if(count == 20)
    	{
    	addConnection();
    	sharCoal();
    	
    	count = 0;
    	}
    	count++;
    	maxGenerateRate = SteamPower.fireOutput + (connectedUnits*5);
    		TileEntity blockEntity =  worldObj.getBlockTileEntity(this.xCoord, this.yCoord + 1, this.zCoord);
	    	if(blockEntity instanceof TileEntityBoiler)
	    	{
	    		isConnected = true;
	    	}
	    	else
	    	{
	    		isConnected = false;
	    	}
	    	//The top slot is for recharging items. Check if the item is a electric item. If so, recharge it.
	    	if (this.containingItems[0] != null && isConnected)
	        {
	            if (this.containingItems[0].getItem().shiftedIndex == Item.coal.shiftedIndex)
	            {
	                if(this.itemCookTime <= 0)
	                {
	            		itemCookTime = Math.max(1600 - (int)(this.generateRate*20), 400);
	            		this.decrStackSize(0, 1);
	            	}
	            }
	        }
	    	
        }
    	//Starts generating electricity if the device is heated up
    	if (this.itemCookTime > 0)
        {
            this.itemCookTime --;
            if(isConnected)
            {
            	this.generateRate = Math.min(this.generateRate+Math.min((this.generateRate)+1, 1), this.maxGenerateRate/20);
            }
        }
    	//Loose heat when the generator is not connected or if there is no coal in the inventory.
    	if(this.itemCookTime <= 0 || !isConnected)
        {
        	this.generateRate =  Math.max(this.generateRate-5, 0);
        }
    }
    
	//gets all connected fireBoxes and shares its supply of coal
    public void sharCoal(){
    	for(int i =0; i<6;i++)
    	{
    		
    		if(connectedBlocks[i] instanceof TileEntityFireBox)
    		{ 
    			TileEntityFireBox connectedConsumer = (TileEntityFireBox) connectedBlocks[i];
    			if(this.containingItems[0] != null)
    			{
    				if(this.containingItems[0].getItem().shiftedIndex == Item.coal.shiftedIndex && this.containingItems[0].stackSize > 0)
    				{		
    					if(connectedConsumer.containingItems[0] != null)
    					{
			    			if(connectedConsumer.containingItems[0].getItem().shiftedIndex == Item.coal.shiftedIndex)
			    			{
									if(connectedConsumer.containingItems[0].getItem().shiftedIndex == Item.coal.shiftedIndex)
									{	
										int CSum = Math.round(this.containingItems[0].stackSize + connectedConsumer.containingItems[0].stackSize)/2;						
										if(this.containingItems[0].stackSize > connectedConsumer.containingItems[0].stackSize)
										{			
											int transferC = 0;
											transferC = Math.round(CSum - connectedConsumer.containingItems[0].stackSize);	
											connectedConsumer.containingItems[0].stackSize = connectedConsumer.containingItems[0].stackSize + transferC;
											this.containingItems[0].stackSize = this.containingItems[0].stackSize - transferC;
										}
									}
			    			}
    					}
    					else
		    			{	
		    				connectedConsumer.containingItems[0] = new ItemStack(this.containingItems[0].getItem());
		    				this.containingItems[0].stackSize -= 1;
		    			}
    				}
    			}    		 
    		}
    	}
    		
    			
    }
    public void addConnection()
	{			
    	connectedUnits = 0;
		for(int i = 0; i<6; i++)
		{	
			
		TileEntity aEntity = getSteamMachine(i);			
		if(aEntity instanceof TileEntityFireBox && i != 0 && i != 1)
		{
			this.connectedBlocks[i] = aEntity;
			connectedUnits += 1;
		}
		else
		{
			this.connectedBlocks[i] = null;
		}
		}
		}
    /**
     * Reads a tile entity from NBT.
     */
    public void readFromNBT(NBTTagCompound par1NBTTagCompound)
    {
    	super.readFromNBT(par1NBTTagCompound);
    	this.itemCookTime = par1NBTTagCompound.getInteger("itemCookTime");
    	this.generateRate = par1NBTTagCompound.getInteger("generateRate");
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
    	par1NBTTagCompound.setInteger("generateRate", (int)this.generateRate);
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
	public int getStartInventorySide(ForgeDirection side)
	{
		if (side == ForgeDirection.DOWN)
        {
            return 1;
        }
        if (side == ForgeDirection.UP)
        {
            return 0;
        }
        return 2;
	}
	@Override
	public int getSizeInventorySide(ForgeDirection side) { return getSizeInventory(); }
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
		return "FireBox";
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
	public float onProduceHeat(float jouls, int side) {
		// TODO Auto-generated method stub
		return Math.min(generateRate,jouls);
	}
	@Override
	public Object[] getSendData()
	{
		return new Object[]{(int)facing,(int)connectedUnits,(int)generateRate,(int)itemCookTime};
	}
	
	@Override
	public void handlePacketData(NetworkManager network,
			Packet250CustomPayload packet, EntityPlayer player,
			ByteArrayDataInput dataStream) {
		try
		{
		facing = dataStream.readInt();
		connectedUnits = dataStream.readInt();
		generateRate = dataStream.readInt();
		itemCookTime = dataStream.readInt();
		}
		catch(Exception e)
		{
		e.printStackTrace();	
		}
		
	}
}
