package net.minecraft.src.eui.steam;

import net.minecraft.src.NBTTagCompound;
import net.minecraft.src.TileEntity;
import net.minecraft.src.eui.api.IWaterConsumer;
import net.minecraft.src.eui.api.IWaterProducer;
import net.minecraft.src.universalelectricity.UniversalElectricity;
import net.minecraft.src.universalelectricity.Vector3;

public class TileEntityPipeWater extends TileEntity implements IWaterConsumer
{
	//The amount of electricity stored in the conductor
	protected int waterStored = 0;
	
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
	public TileEntity closestConsumer = null;
	
	/**
	 * This function adds a connection between this conductor and the UE unit
	 * @param tileEntity - Must be either a producer, consumer or a conductor
	 * @param side - side in which the connection is coming from
	 */
	public void addConnection(TileEntity tileEntity, byte side)
	{
		if(tileEntity instanceof IWaterConsumer || tileEntity instanceof IWaterProducer)
		{
			this.connectedBlocks[side] = tileEntity;
		}
		else
		{
			this.connectedBlocks[side] = null;
		}
	}
	
	/**
	 * onRecieveElectricity is called whenever a Universal Electric conductor sends a packet of electricity to the consumer (which is this block).
	 * @param watts - The amount of watt this block recieved
	 * @param side - The side of the block in which the electricity came from
	 * @return watt - The amount of rejected power to be sent back into the conductor
	 */
	@Override
	public int onReceiveWater(int watt, byte side)
	{
		int rejectedElectricity = Math.max((this.waterStored + watt) - this.capacity, 0);
		this.waterStored += watt - rejectedElectricity;
		return rejectedElectricity;
	}
	
	/**
     * Allows the entity to update its state. Overridden in most subclasses, e.g. the mob spawner uses this to count
     * ticks and creates a new spawn inside its implementation.
     */
	@Override
    public void updateEntity()
	{
		if(this.firstUpdate)
		{
			//Update some variables
			BlockPipeWater conductorBlock = (BlockPipeWater)this.getBlockType();			
			this.capacity = (conductorBlock).conductorCapacity();
			((BlockPipeWater)this.getBlockType()).updateConductorTileEntity(this.worldObj, this.xCoord, this.yCoord, this.zCoord);
			this.firstUpdate = false;
		}

		//Spread the electricity to neighboring blocks
		byte connectedUnits = 0;
		byte connectedConductors = 1;
		int averageElectricity = this.waterStored;
		this.closestConsumer = null;
		
		Vector3 currentPosition = new Vector3(this.xCoord, this.yCoord, this.zCoord);

		//Find the connected unit with the least amount of electricity and give more to them
		for(byte i = 0; i < 6; i++)
        {
			if(connectedBlocks[i] != null)
			{
				if(connectedBlocks[i] instanceof IWaterConsumer)
				{
					connectedUnits ++;
					
					if(connectedBlocks[i].getClass() == this.getClass())
					{
						averageElectricity += ((TileEntityPipeWater)connectedBlocks[i]).waterStored;
						
						TileEntity tileEntity = ((TileEntityPipeWater)connectedBlocks[i]).closestConsumer;
						
						if(tileEntity != null)
						{
							this.closestConsumer = tileEntity;
						}
					
						connectedConductors ++;
					}	
					else if(connectedBlocks[i] instanceof IWaterConsumer)
					{
						if(((IWaterConsumer)connectedBlocks[i]).canRecieveWater(UniversalElectricity.getOrientationFromSide(i, (byte)2)))
						{
							this.closestConsumer = connectedBlocks[i];
						}
					}
						
				}
			}
        }
		
		averageElectricity = averageElectricity/connectedConductors;
		
		
		float averageWatt = 0;

		if(connectedUnits > 0)
		{
			for(byte i = 0; i < 6; i++)
	        {
				if(connectedBlocks[i] != null)
				{
					//Spread the electricity among the different blocks
					if(connectedBlocks[i] instanceof IWaterConsumer && this.waterStored > 0)
					{
						if(((IWaterConsumer)connectedBlocks[i]).canRecieveWater(UniversalElectricity.getOrientationFromSide(i, (byte) 2)))
						{
							int transferElectricityAmount  = 0;
							IWaterConsumer connectedConsumer = ((IWaterConsumer)connectedBlocks[i]);
							
							if(connectedBlocks[i].getClass() == this.getClass() && this.waterStored > ((TileEntityPipeWater)connectedConsumer).waterStored)
							{
								transferElectricityAmount = Math.max(Math.min(averageElectricity - ((TileEntityPipeWater)connectedConsumer).waterStored, this.waterStored), 0);
							}
							else if(!(connectedConsumer instanceof TileEntityPipeWater))
							{
								transferElectricityAmount = this.waterStored;
							}
							
							int rejectedElectricity = connectedConsumer.onReceiveWater(transferElectricityAmount, UniversalElectricity.getOrientationFromSide(i, (byte)2));
							this.waterStored = Math.max(Math.min(this.waterStored - transferElectricityAmount + rejectedElectricity, this.capacity), 0);
						}
					}
					
					if(connectedBlocks[i] instanceof IWaterProducer && this.waterStored < this.getWaterCapacity())
					{
						if(((IWaterProducer)connectedBlocks[i]).canProduceWater(UniversalElectricity.getOrientationFromSide(i, (byte)2)))
						{
							int gainedElectricity = ((IWaterProducer)connectedBlocks[i]).onProduceWater(this.capacity-this.waterStored, UniversalElectricity.getOrientationFromSide(i, (byte)2));
							this.waterStored = Math.max(Math.min(this.waterStored + gainedElectricity, this.capacity), 0);
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
	public int getStoredWater()
    {
    	return this.waterStored;
    }
    
    @Override
    public int getWaterCapacity()
	{
		return this.capacity;
	}
	
	/**
     * Reads a tile entity from NBT.
     */
    public void readFromNBT(NBTTagCompound par1NBTTagCompound)
    {
        super.readFromNBT(par1NBTTagCompound);
        this.waterStored = par1NBTTagCompound.getInteger("waterStored");
        this.capacity = par1NBTTagCompound.getInteger("capacity");
    }

    /**
     * Writes a tile entity to NBT.
     */
    public void writeToNBT(NBTTagCompound par1NBTTagCompound)
    {
    	super.writeToNBT(par1NBTTagCompound);
    	par1NBTTagCompound.setInteger("waterStored", this.waterStored);
    	par1NBTTagCompound.setInteger("capacity", this.capacity);
    }

	@Override
	public boolean canRecieveWater(byte side) {
		// TODO Auto-generated method stub
		return true;
	}
	
}

