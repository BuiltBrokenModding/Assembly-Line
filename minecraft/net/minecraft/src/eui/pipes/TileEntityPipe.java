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
	protected int type = 0;
	//The maximum amount of electricity this conductor can take
	protected int capacity = 5;

	//Stores information on all connected blocks around this tile entity
	public TileEntity[] connectedBlocks = {null, null, null, null, null, null};

	//Checks if this is the first the tile entity updates
	protected boolean firstUpdate = true;
	
	/**
	 * The tile entity of the closest electric consumer. Null if none. Use this to detect if electricity
	 * should transfer
	 */
	
	
	/**
	 * This function adds a connection between this conductor and the UE unit
	 * @param tileEntity - Must be either a producer, consumer or a conductor
	 * @param side - side in which the connection is coming from
	 */	
	public void addConnection(TileEntity tileEntity, byte side)
	{
		if(tileEntity instanceof TileEntityPipe)
		{
			if(((TileEntityPipe)tileEntity).getType() == this.getType())
			{
				this.connectedBlocks[side] = tileEntity;
			}
			else
			{
				this.connectedBlocks[side] = null;
			}
		}
		else
		{
		if(tileEntity instanceof ILiquidConsumer || tileEntity instanceof ILiquidProducer)
		{
			this.connectedBlocks[side] = tileEntity;
		}
		else
		{
			this.connectedBlocks[side] = null;
		}
		}
	}
	
	
	
	/**
	 * onRecieveElectricity is called whenever a Universal Electric conductor sends a packet of electricity to the consumer (which is this block).
	 * @param vols - The amount of vol this block recieved
	 * @param side - The side of the block in which the electricity came from
	 * @return vol - The amount of rejected power to be sent back into the conductor
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
		((BlockPipe)this.getBlockType()).updateConductorTileEntity(this.worldObj, this.xCoord, this.yCoord, this.zCoord);
		
		//Find the connected unit with the least amount of electricity and give more to them
		if(!this.worldObj.isRemote)
        {
			//Spread the electricity to neighboring blocks
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
			
			averageVolume = Math.max(averageVolume/connectedConductors,0);
			if(connectedUnits > 0)
			{
				for(byte i = 0; i < 6; i++)
		        {
					if(connectedBlocks[i] != null)
					{
						//Spread the electricity among the different blocks
						if(connectedBlocks[i] instanceof ILiquidConsumer && this.liquidStored > 0)
						{						
							if(((ILiquidConsumer)connectedBlocks[i]).canRecieveLiquid(this.type,UniversalElectricity.getOrientationFromSide(i, (byte)2)))
							{
								int transferVolumeAmount  = 0;
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
	 * @return Return the stored electricity in this consumer. Called by conductors to spread electricity to this unit.
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

	@Override
	public boolean canRecieveLiquid(int type, byte side) {
		if(type == this.type)
		{
			return true;
		}
		return false;
	}
	public int getType() {		
		return this.type;
	}


	public void setType(int rType) {
		this.type = rType;
		
	}
	
}

