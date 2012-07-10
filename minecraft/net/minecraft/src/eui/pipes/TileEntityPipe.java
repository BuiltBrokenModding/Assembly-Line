package net.minecraft.src.eui.pipes;
import net.minecraft.src.NBTTagCompound;
import net.minecraft.src.TileEntity;
import net.minecraft.src.World;
import net.minecraft.src.eui.pipes.api.ILiquidConsumer;
import net.minecraft.src.eui.pipes.api.ILiquidProducer;
import net.minecraft.src.universalelectricity.UniversalElectricity;
import net.minecraft.src.universalelectricity.Vector3;

public class TileEntityPipe extends TileEntity implements ILiquidConsumer
{
	//The amount stored in the conductor
	protected int liquidStored = 0;
	//the current set type of the pipe 0-5
	protected int type = 0;
	//The maximum amount of electricity this conductor can take
	protected int capacity = 5;

	//Stores information on all connected blocks around this tile entity
	public TileEntity[] connectedBlocks = {null, null, null, null, null, null};

	//Checks if this is the first the tile entity updates
	protected boolean firstUpdate = true;
	/**
	 * This function adds a connection between this pipe and other blocks
	 * @param tileEntity - Must be either a producer, consumer or a conductor
	 * @param side - side in which the connection is coming from
	 */	
	public void addConnection(TileEntity tileEntity, byte side)
	{
		this.connectedBlocks[side] = null;
		if(tileEntity instanceof ILiquidConsumer)
		{
			if(((ILiquidConsumer)tileEntity).canRecieveLiquid(this.type, side))
			{
				this.connectedBlocks[side] = tileEntity;
			}
		}
		if(tileEntity instanceof ILiquidProducer)
		{
			if(((ILiquidProducer)tileEntity).canProduceLiquid(this.type, side))
			{
			this.connectedBlocks[side] = tileEntity;
			}
		}
	}
	
	
	
	/**
	 * onRecieveLiquid is called whenever a something sends a volume to the pipe (which is this block).
	 * @param vols - The amount of vol source is trying to give to this pipe
	 * @param side - The side of the block in which the liquid came from
	 * @return vol - The amount of rejected liquid that can't enter the pipe
	 */
	@Override
	public int onReceiveLiquid(int type,int vol, byte side)
	{
		if(type == this.type)
		{
		int rejectedVolume = Math.max((this.getStoredLiquid(type) + vol) - this.capacity, 0);
		 this.liquidStored = vol - rejectedVolume;
		return rejectedVolume;
		}
		return vol;
	}
	@Override
	public void updateEntity()
	{	
		//cause the block to update itself every tick needs to be change to .5 seconds to reduce load
		((BlockPipe)this.getBlockType()).updateConductorTileEntity(this.worldObj, this.xCoord, this.yCoord, this.zCoord);
		
		
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
							if(((ILiquidConsumer)connectedBlocks[i]).canRecieveLiquid(this.type,UniversalElectricity.getOrientationFromSide(i, (byte)2)))
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
								
								int rejectedVolume = connectedConsumer.onReceiveLiquid(this.type,transferVolumeAmount, UniversalElectricity.getOrientationFromSide(i, (byte)2));
								this.liquidStored = Math.max(Math.min(this.liquidStored - transferVolumeAmount + rejectedVolume, 5), 0);
							}
						}
						
						if(connectedBlocks[i] instanceof ILiquidProducer && this.liquidStored < this.getLiquidCapacity(type))
						{
							if(((ILiquidProducer)connectedBlocks[i]).canProduceLiquid(this.type,UniversalElectricity.getOrientationFromSide(i, (byte)2)))
							{
								int gainedVolume = ((ILiquidProducer)connectedBlocks[i]).onProduceLiquid(this.type,5-this.liquidStored,  UniversalElectricity.getOrientationFromSide(i, (byte)2));
								this.onReceiveLiquid(this.type, gainedVolume, i);
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
	public int getStoredLiquid(int type)
    {
    		return this.liquidStored;
    }
    
    
    @Override
    public int getLiquidCapacity(int type)
	{
		return 5;
	}
	
	/**
     * Reads a tile entity from NBT.
     */
    public void readFromNBT(NBTTagCompound par1NBTTagCompound)
    {
        super.readFromNBT(par1NBTTagCompound);
        this.liquidStored = par1NBTTagCompound.getInteger("liquid");
        this.type = par1NBTTagCompound.getInteger("type");
    }

    /**
     * Writes a tile entity to NBT.
     */
    public void writeToNBT(NBTTagCompound par1NBTTagCompound)
    {
    	super.writeToNBT(par1NBTTagCompound);
    	par1NBTTagCompound.setInteger("liquid", this.liquidStored);
    	par1NBTTagCompound.setInteger("type", this.type);
    }
//find wether or not this side of X block can recieve X liquid type. Also use to determine connection of a pipe
	@Override
	public boolean canRecieveLiquid(int type, byte side) {
		if(type == this.type)
		{
			return true;
		}
		return false;
	}
	//returns liquid type
	public int getType() {		
		return this.type;
	}

	//used by the item to set the liquid type on spawn
	public void setType(int rType) {
		this.type = rType;
		
	}
	
}

