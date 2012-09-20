package basicpipes.pipes;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.NBTTagCompound;
import net.minecraft.src.NetworkManager;
import net.minecraft.src.Packet250CustomPayload;
import net.minecraft.src.TileEntity;
import net.minecraftforge.common.ForgeDirection;
import universalelectricity.Vector3;
import universalelectricity.network.IPacketReceiver;
import universalelectricity.network.PacketManager;
import basicpipes.pipes.api.ILiquidConsumer;
import basicpipes.pipes.api.ILiquidProducer;
import basicpipes.pipes.api.Liquid;

import com.google.common.io.ByteArrayDataInput;
public class TileEntityPipe extends TileEntity implements ILiquidConsumer,IPacketReceiver
{
	//The amount stored in the conductor
	protected int liquidStored = 0;
	//the current set type of the pipe 0-5
	protected Liquid type = Liquid.DEFUALT;
	//The maximum amount of electricity this conductor can take
	protected int capacity = 5;
	private int count = 0;
	private boolean intiUpdate = true;
	//Stores information on all connected blocks around this tile entity
	public TileEntity[] connectedBlocks = {null, null, null, null, null, null};

	//Checks if this is the first the tile entity updates
	protected boolean firstUpdate = true;
	/**
	 * This function adds a connection between this pipe and other blocks
	 * @param tileEntity - Must be either a producer, consumer or a conductor
	 * @param side - side in which the connection is coming from
	 */	
	public void addConnection(TileEntity tileEntity, ForgeDirection side)
	{
		int sideN = getNumSide(side);
		this.connectedBlocks[sideN] = null;
		if(tileEntity instanceof ILiquidConsumer)
		{
			if(((ILiquidConsumer)tileEntity).canRecieveLiquid(this.type, side))
			{
				this.connectedBlocks[sideN] = tileEntity;
			}
		}
		if(tileEntity instanceof ILiquidProducer)
		{
			if(((ILiquidProducer)tileEntity).canProduceLiquid(this.type, side))
			{
			this.connectedBlocks[sideN] = tileEntity;
			}
		}
	}
	
	
	
	private int getNumSide(ForgeDirection side) 
	{

			if(side == ForgeDirection.DOWN)
			{
				return 0;
			}
			if(side == ForgeDirection.UP)
			{
				return 1;
			}
			if(side == ForgeDirection.NORTH)
			{
				return 2;
			}
			if(side == ForgeDirection.SOUTH)
			{
			 return 3;	
			}
			if(side == ForgeDirection.WEST)
			{
				return 4;
			}
			if(side == ForgeDirection.EAST)
			{
				return 5;
			}


		return 0;
	}



	/**
	 * onRecieveLiquid is called whenever a something sends a volume to the pipe (which is this block).
	 * @param vols - The amount of vol source is trying to give to this pipe
	 * @param side - The side of the block in which the liquid came from
	 * @return vol - The amount of rejected liquid that can't enter the pipe
	 */
	@Override
	public int onReceiveLiquid(Liquid type,int vol, ForgeDirection side)
	{
		if(type == this.type)
		{
			int rejectedVolume = Math.max((this.getStoredLiquid(type) + vol) - this.capacity, 0);
			this.liquidStored = Math.min(Math.max((liquidStored + vol - rejectedVolume),0),this.capacity);
			return rejectedVolume;
		}
		return vol;
	}
	@Override
	public void updateEntity()
	{	
		//cause the block to update itself every tick needs to be change to .5 seconds to reduce load
		BlockPipe.updateConductorTileEntity(this.worldObj, this.xCoord, this.yCoord, this.zCoord);
		count++;
		if(count >= 30 || intiUpdate)
		{
		PacketManager.sendTileEntityPacket(this, "Pipes", new Object[]{this.type});
		count = 0;
		intiUpdate = false;
		}
		if(!this.worldObj.isRemote)
        {			
			byte connectedUnits = 0;
			byte connectedConductors = 1;
			int averageVolume = this.liquidStored;
			
			Vector3 currentPosition = new Vector3(this.xCoord, this.yCoord, this.zCoord);
			
			for(byte i = 0; i < 6; i++)
	        {
				if(connectedBlocks[i] != null)
				{
					if(connectedBlocks[i] instanceof ILiquidConsumer || connectedBlocks[i] instanceof ILiquidProducer)
					{
						connectedUnits ++;
						
						if(connectedBlocks[i] instanceof TileEntityPipe)
						{
							averageVolume += ((TileEntityPipe)connectedBlocks[i]).liquidStored;
						
							connectedConductors ++;
						}	
					}
				}
	        }
			//average volume used to control volume spread to pipes. Prevent one pipe getting all liquid when another is empty
			averageVolume = Math.max(averageVolume/connectedConductors,0);
			if(connectedUnits > 0)
			{
				for(byte i = 0; i < 6; i++)
		        {
					if(connectedBlocks[i] != null)
					{
						//Spread the liquid among the different blocks
						if(connectedBlocks[i] instanceof ILiquidConsumer && this.liquidStored > 0)
						{						
							if(((ILiquidConsumer)connectedBlocks[i]).canRecieveLiquid(this.type,ForgeDirection.getOrientation(i)))
							{
								int transferVolumeAmount  = 0; //amount to be moved
								ILiquidConsumer connectedConsumer = ((ILiquidConsumer)connectedBlocks[i]);
								
								if(connectedBlocks[i] instanceof TileEntityPipe && this.liquidStored > ((TileEntityPipe)connectedConsumer).liquidStored)
								{
									transferVolumeAmount = Math.max(Math.min(averageVolume - ((TileEntityPipe)connectedConsumer).liquidStored, this.liquidStored), 0);
								}
								else if(!(connectedConsumer instanceof TileEntityPipe))
								{
									transferVolumeAmount = this.liquidStored;
								}
								
								int rejectedVolume = connectedConsumer.onReceiveLiquid(this.type,transferVolumeAmount, ForgeDirection.getOrientation(i));
								this.liquidStored = Math.max(Math.min(this.liquidStored - transferVolumeAmount + rejectedVolume, 5), 0);
							}
						}
						
						if(connectedBlocks[i] instanceof ILiquidProducer && this.liquidStored < this.getLiquidCapacity(type))
						{
							if(((ILiquidProducer)connectedBlocks[i]).canProduceLiquid(this.type,ForgeDirection.getOrientation(i)))
							{
								int gainedVolume = ((ILiquidProducer)connectedBlocks[i]).onProduceLiquid(this.type,this.capacity-this.liquidStored,  ForgeDirection.getOrientation(i));
								this.onReceiveLiquid(this.type, gainedVolume, ForgeDirection.getOrientation(i));
							}
						}
					}
		        }
			}
        }
	}
	
	/**
	 * @return Return the stored volume in this pipe.
	 */
    @Override
	public int getStoredLiquid(Liquid type)
    {
    	if(type == this.type)
    	{
    		return this.liquidStored;
    	}
		return 0;
    }
    
    
    @Override
    public int getLiquidCapacity(Liquid type)
	{
    	if(type == this.type)
    	{
    		return 5;
    	}
    	return 0;
	}
	
	/**
     * Reads a tile entity from NBT.
     */
    public void readFromNBT(NBTTagCompound par1NBTTagCompound)
    {
        super.readFromNBT(par1NBTTagCompound);
        this.liquidStored = par1NBTTagCompound.getInteger("liquid");
        this.type = Liquid.getLiquid(par1NBTTagCompound.getInteger("type"));
    }

    /**
     * Writes a tile entity to NBT.
     */
    public void writeToNBT(NBTTagCompound par1NBTTagCompound)
    {
    	super.writeToNBT(par1NBTTagCompound);
    	par1NBTTagCompound.setInteger("liquid", this.liquidStored);
    	par1NBTTagCompound.setInteger("type", this.type.ordinal());
    }
//find wether or not this side of X block can recieve X liquid type. Also use to determine connection of a pipe
	@Override
	public boolean canRecieveLiquid(Liquid type, ForgeDirection side) {
		if(type == this.type)
		{
			return true;
		}
		return false;
	}
	//returns liquid type
	public Liquid getType() {		
		return this.type;
	}

	//used by the item to set the liquid type on spawn
	public void setType(Liquid rType) {
		this.type = rType;
		
	}



	@Override
	public void handlePacketData(NetworkManager network,
			Packet250CustomPayload packet, EntityPlayer player,
			ByteArrayDataInput data) {
		try
        {
        int type = data.readInt();
		if(worldObj.isRemote)
		{
			this.type = Liquid.getLiquid(type);
		}
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
		
		
	}



	public int getSize() {
		// TODO Auto-generated method stub
		return 6;
	}
	
}

