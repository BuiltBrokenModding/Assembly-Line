package steampower.boiler;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.Item;
import net.minecraft.src.ItemStack;
import net.minecraft.src.NBTTagCompound;
import net.minecraft.src.NetworkManager;
import net.minecraft.src.Packet250CustomPayload;
import net.minecraft.src.TileEntity;
import net.minecraftforge.common.ForgeDirection;
import steampower.SteamPowerMain;
import steampower.TileEntityMachine;
import steampower.TradeHelper;
import steampower.burner.TileEntityFireBox;
import universalelectricity.network.IPacketReceiver;
import basicpipes.pipes.api.ILiquidConsumer;
import basicpipes.pipes.api.ILiquidProducer;

import com.google.common.io.ByteArrayDataInput;

public class TileEntityBoiler extends TileEntityMachine implements IPacketReceiver,ILiquidProducer, ILiquidConsumer
{
	
	    /**
	     * The ItemStacks that hold the items currently being used in the furnace
	     */
	    

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
	    public int hullHeatMax = 4700;
	    private int heatTick = 0;
	    public int tankCount = 0;
	    private int heatNeeded = SteamPowerMain.boilerHeat; // kilo joules	
	    int count = 0;
	    boolean hullHeated = false;
	    public TileEntity[] connectedBlocks = {null, null, null, null, null, null};
	    int steamMax = 140;
	    public boolean isBeingHeated = false;
	    public String getInvName()
	    {
	        return "container.boiler";
	    }
	    public Object[] getSendData()
		{
			return new Object[]{(int)facing,(int)RunTime,(int)energyStore,(int)waterStored,
					(int)steamStored,(int)heatStored,(int)hullHeat,(int)heatTick};
		}
		
		@Override
		public void handlePacketData(NetworkManager network,
				Packet250CustomPayload packet, EntityPlayer player,
				ByteArrayDataInput dataStream) {
			try
			{
				facing  = dataStream.readInt();
				RunTime = dataStream.readInt();
				energyStore = dataStream.readInt();
				waterStored = dataStream.readInt();
				steamStored = dataStream.readInt();
				heatStored = dataStream.readInt();
				hullHeat = dataStream.readInt();
				heatTick  = dataStream.readInt();
			}
			catch(Exception e)
			{
				e.printStackTrace();	
			}
			
		}
	    /**
	     * Reads a tile entity from NBT.
	     */
	    public void readFromNBT(NBTTagCompound par1NBTTagCompound)
	    {	     
	    	super.readFromNBT(par1NBTTagCompound);
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
	        
	    }
	    /**
	     * Allows the entity to update its state. Overridden in most subclasses, e.g. the mob spawner uses this to count
	     * ticks and creates a new spawn inside its implementation.
	     */    
	   @Override
	   public void onUpdate(float watts, float voltage, ForgeDirection side)
	    {
		   
		   //update/resets connection list
		   TileEntity[] entityList = TradeHelper.getSourounding(this);
		   tankCount = 0;
		   for(int c = 0; c< 6; c++)
		   {
			   if(entityList[c] instanceof TileEntityBoiler)
			   {
				   connectedBlocks[c] = entityList[c];
				   if(c != 0 && c != 1)
				   {
					   tankCount++;
				   }
			   }
			   else
			   {
				   connectedBlocks[c] = null;
			   }
		   }
		   
		   	hullHeated = false;
			if(hullHeat >= hullHeatMax)
	    	{
				hullHeated = true;
			}
	    	else
	    	{
	    		if(!worldObj.isRemote)
	    		{
	    			hullHeat = Math.min(hullHeat + heatStored, hullHeatMax);
	    		}
	    	}
		   if(!worldObj.isRemote)
		   {
			    emptyBuckets();//adds water from container slot
				this.waterStored = TradeHelper.shareLiquid(this, 1, false);
				this.steamStored = TradeHelper.shareLiquid(this, 0, true);
				if(waterStored > 0 && hullHeated && heatStored > heatNeeded)
				{
					heatStored = Math.max(heatStored - heatNeeded, 0);
					--waterStored;
					steamStored = Math.min(steamStored + SteamPowerMain.steamOutBoiler,this.steamMax);
				}
				TileEntity blockE = worldObj.getBlockTileEntity(xCoord, yCoord -1, zCoord);
				this.isBeingHeated = false;
				if(blockE instanceof TileEntityFireBox)
				{
					this.isBeingHeated = true;
					heatStored = (int) Math.min((heatStored + ((TileEntityFireBox)blockE).onProduceHeat(SteamPowerMain.fireOutput*getTickInterval(), 1)), heatMax);
				}
		   }
		   super.onUpdate(watts, voltage, side);
	    }
	    private void emptyBuckets() 
	    {
	    	if (storedItems[0] != null)
            {
                if(storedItems[0].isItemEqual(new ItemStack(Item.bucketWater,1)))
                {
                	if((int)waterStored < getLiquidCapacity(1))
                	{                		
                		++waterStored;
                		this.storedItems[0] = new ItemStack(Item.bucketEmpty,1);
                		this.onInventoryChanged();
                	}
                }
            }
			
		}
		public int precentHeated() {
			int var1 = 0;
			if(hullHeat < 100)
			{
				var1 = (int)(100 *(hullHeat/hullHeatMax));
			}
			else
			{
				var1 = 100;
			}
			return var1;
		}
		@Override
		public int onReceiveLiquid(int type, int vol, ForgeDirection side) {
			if(type == 0)
			{
				int rejectedSteam = Math.max((this.steamStored + vol) - this.getLiquidCapacity(0), 0);
				this.steamStored += vol - rejectedSteam;
				return rejectedSteam;
			}
			if(type == 1)
			{
				int rejectedWater = Math.max((this.waterStored + vol) - this.getLiquidCapacity(1), 0);
				this.waterStored += vol - rejectedWater;
				return rejectedWater;
			}
			return vol;
		}

		@Override
		public boolean canRecieveLiquid(int type,ForgeDirection side) {
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
		public int onProduceLiquid(int type, int maxVol, ForgeDirection side) {			
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
		public boolean canProduceLiquid(int type, ForgeDirection side) {
			if(type == 0)
			{
				return true;
			}
			return false;
		}
		

	}
