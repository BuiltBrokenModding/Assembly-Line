package steampower.turbine;

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
import steampower.SteamPowerMain;
import steampower.TileEntityMachine;
import universalelectricity.Vector3;
import universalelectricity.electricity.ElectricityManager;
import universalelectricity.extend.IElectricUnit;
import universalelectricity.extend.TileEntityConductor;
import universalelectricity.network.IPacketReceiver;
import basicpipes.pipes.api.ILiquidConsumer;
import basicpipes.pipes.api.ILiquidProducer;

import com.google.common.io.ByteArrayDataInput;

public class TileEntitySteamPiston extends TileEntityMachine implements IPacketReceiver, IElectricUnit,ILiquidConsumer,ILiquidProducer, IInventory, ISidedInventory
{
	//Maximum possible generation rate of watts in SECONDS
	public int maxGenerateRate = 1000;
	public int waterStored = 0;
	public int steamStored = 0;
	public int steamConsumed = 0;
	public float position = 0;
	public int count = 0;
	//Current generation rate based on hull heat. In TICKS.
	public float generateRate = 0;
	//public TileEntityConductor connectedWire = null;
	 /**
     * The number of ticks that a fresh copy of the currently-burning item would keep the furnace burning for
     */
    public int genTime = 0;
	 /**
     * The ItemStacks that hold the items currently being used in the battery box
     */
    private ItemStack[] containingItems = new ItemStack[1];
    public TileEntityConductor connectedElectricUnit = null;  
    public boolean isConnected = false;
	private boolean posT = true;
    @Override
    public boolean canConnect(ForgeDirection side)
    {
    	return true;
    }
    public int getTickInterval()
    {
		return 10;
    	
    }
    /**
     * Allows the entity to update its state. Overridden in most subclasses, e.g. the mob spawner uses this to count
     * ticks and creates a new spawn inside its implementation.
     */
    public void onUpdate(float watts, float voltage, ForgeDirection side)
	{ 
    	super.onUpdate(watts, voltage, side);
    	count++;
    	float cPercent = (generateRate/10);
    	int cCount = 1;
    	if(cPercent > 25f)
    	{
    		cCount = 2;
    	}
    	if(cPercent > 50f)
    	{
    		cCount = 3;
    	}
    	if(cPercent > 75f)
    	{
    		cCount = 4;
    	}
    	if(generateRate > 0)
    	{
    		
    		if(position < 9f && posT )
    		{
    		position+= cCount;
    		}
    		else
    		{
    			posT = false;
    		}
    		if(position > 1f && !posT )
    		{
    		position-= cCount;
    		}
    		else
    		{
    			posT = true;
    		}
    		count =0;
    	}
    	TileEntity ent = worldObj.getBlockTileEntity(xCoord, yCoord+1, zCoord);
    	if(ent instanceof TileEntitytopGen)
    	{
    		isConnected = true;
    	}
    	
    	this.connectedElectricUnit = null;
    	//Check nearby blocks and see if the conductor is full. If so, then it is connected
    	for(int i = 0;i<6;i++)
    	{
        TileEntity tileEntity = Vector3.getUEUnitFromSide(this.worldObj, new Vector3(this.xCoord, this.yCoord, this.zCoord), 
        		ForgeDirection.getOrientation(i));
        if (tileEntity instanceof TileEntityConductor)
        {
            if (ElectricityManager.instance.electricityRequired(((TileEntityConductor)tileEntity).connectionID) > 0)
            {
                this.connectedElectricUnit = (TileEntityConductor)tileEntity;
            }
        }
    	}
    	
    	if(!this.worldObj.isRemote)
    	{
    	
    	
    	if(!this.isDisabled())
    	{	
    		//Adds time to runTime by consuming steam	    	
	                if(this.genTime <= 0)
	                {
	                	if(steamStored > 0)
	            		{
	            			--steamStored;
	            			++steamConsumed;
	            			if(steamConsumed >= 10)
	            			{
	            			++waterStored;
	            			steamConsumed = 0;
	            			}
	            		genTime += 65;
	            		}
	            	}
	          
	    	//Empties water from tank to buckets
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
    	
	    	//Starts generating electricity if the device is heated up
	    	if (this.genTime > 0)
	        {
	            this.genTime --;
	            
	            if(this.connectedElectricUnit != null)
	            {
	            	this.generateRate = (float)Math.min(this.generateRate+Math.min((this.generateRate)*0.01+0.015, 0.05F), this.maxGenerateRate/20);
	            }
	        }
	
	    	if(this.connectedElectricUnit == null || this.genTime <= 0)
	    	{
	        	this.generateRate = (float)Math.max(this.generateRate-0.05, 0);
	        }
	    	
	    	if(this.generateRate > 1)
	    	{
	    		ElectricityManager.instance.produceElectricity(this.connectedElectricUnit, this.generateRate*this.getTickInterval(), this.getVoltage());
	    	}
    	}
    }    	
    }
    /**
     * Reads a tile entity from NBT.
     */
    public void readFromNBT(NBTTagCompound par1NBTTagCompound)
    {
    	super.readFromNBT(par1NBTTagCompound);
    	this.genTime = par1NBTTagCompound.getInteger("itemCookTime");
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
    	par1NBTTagCompound.setInteger("itemCookTime", (int)this.genTime);
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
	public int getStartInventorySide(ForgeDirection side)
	{
		return 0;
	}
	@Override
	public int getSizeInventorySide(ForgeDirection side) { return 0; }
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
	public void onDisable(int duration) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isDisabled() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int onProduceLiquid(int type, int Vol, ForgeDirection side) {
		if(type == 1)
		{
			if(this.waterStored >= 1)
			{	 
				this.waterStored--;
				return 1;
			}
		}
		return 0;
	}

	@Override
	public boolean canProduceLiquid(int type, ForgeDirection side) {
		if(type == 1)
		{
			return true;
		}
		return false;
	}

	@Override
	public int onReceiveLiquid(int type, int vol, ForgeDirection side) {
		if(type == 0)
		{
			int rejectedSteam = Math.max((this.steamStored + vol) - 100, 0);
			this.steamStored += vol - rejectedSteam;		 
			return rejectedSteam;
		}
		return vol;
	}

	@Override
	public boolean canRecieveLiquid(int type, ForgeDirection side) {
		if(type == 0)
		{
			return true;
		}
		return false;
	}

	@Override
	public int getStoredLiquid(int type) {
		if(type == 0)
		{
			return this.steamStored;
		}
		if(type == 1)
		{
			return this.waterStored;
		}
		return 0;
	}

	@Override
	public int getLiquidCapacity(int type) {
		if(type == 0)
		{
			return 100;
		}
		return 0;
	}
	@Override
	public Object[] getSendData()
	{
		return new Object[]{(int)facing,(int)waterStored,(int)steamStored,(int)steamConsumed,(float)generateRate,(int)genTime};
	}
	
	@Override
	public void handlePacketData(NetworkManager network,
			Packet250CustomPayload packet, EntityPlayer player,
			ByteArrayDataInput dataStream) {
		try
		{
		facing = dataStream.readInt();
		waterStored = dataStream.readInt();
		steamStored = dataStream.readInt();
		steamConsumed = dataStream.readInt();
		generateRate = dataStream.readFloat();
		genTime = dataStream.readInt();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
	}
}
