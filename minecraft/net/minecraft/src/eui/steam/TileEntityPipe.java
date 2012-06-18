package net.minecraft.src.eui.steam;

import net.minecraft.src.NBTTagCompound;
import net.minecraft.src.TileEntity;
import net.minecraft.src.eui.api.ISteamConsumer;
import net.minecraft.src.eui.api.ISteamProducer;
import net.minecraft.src.universalelectricity.UniversalElectricity;
import net.minecraft.src.universalelectricity.Vector3;

public class TileEntityPipe extends TileEntity implements ISteamConsumer
{
	//The amount of electricity stored in the conductor
	protected int steamStored = 0;
	
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
		if(tileEntity instanceof ISteamConsumer || tileEntity instanceof ISteamProducer)
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
	public int onReceiveSteam(int watt, byte side)
	{
		int rejectedElectricity = Math.max((this.steamStored + watt) - this.capacity, 0);
		this.steamStored += watt - rejectedElectricity;
		return rejectedElectricity;
	}

	/**
	 * You can use this to check if a wire can connect to this UE consumer to properly render the graphics
	 * @return Returns true or false if this consumer can receive electricity at this given tick or moment.
	 */
	public boolean canRecieveSteam(byte side)
	{
		return true;
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
			BlockPipe conductorBlock = (BlockPipe)this.getBlockType();			
			this.capacity = (conductorBlock).conductorCapacity();
			((BlockPipe)this.getBlockType()).updateConductorTileEntity(this.worldObj, this.xCoord, this.yCoord, this.zCoord);
			this.firstUpdate = false;
		}

		//Spread the electricity to neighboring blocks
		byte connectedUnits = 0;
		byte connectedConductors = 1;
		int averageElectricity = this.steamStored;
		this.closestConsumer = null;
		
		Vector3 currentPosition = new Vector3(this.xCoord, this.yCoord, this.zCoord);
		if(this.steamStored * 3.3 > 250)
		{
	// TODO add logic to damage pipe if steam rises to 250PSI
		}
		//Find the connected unit with the least amount of electricity and give more to them
		for(byte i = 0; i < 6; i++)
        {
			if(connectedBlocks[i] != null)
			{
				if(connectedBlocks[i] instanceof ISteamConsumer)
				{
					connectedUnits ++;
					
					if(connectedBlocks[i].getClass() == this.getClass())
					{
						averageElectricity += ((TileEntityPipe)connectedBlocks[i]).steamStored;
						
						TileEntity tileEntity = ((TileEntityPipe)connectedBlocks[i]).closestConsumer;
						
						if(tileEntity != null)
						{
							this.closestConsumer = tileEntity;
						}
					
						connectedConductors ++;
					}	
					else if(connectedBlocks[i] instanceof ISteamConsumer)
					{
						if(((ISteamConsumer)connectedBlocks[i]).canRecieveSteam(UniversalElectricity.getOrientationFromSide(i, (byte)2)))
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
					if(connectedBlocks[i] instanceof ISteamConsumer && this.steamStored > 0)
					{
						if(((ISteamConsumer)connectedBlocks[i]).canRecieveSteam(UniversalElectricity.getOrientationFromSide(i, (byte) 2)))
						{
							int transferElectricityAmount  = 0;
							ISteamConsumer connectedConsumer = ((ISteamConsumer)connectedBlocks[i]);
							
							if(connectedBlocks[i].getClass() == this.getClass() && this.steamStored > ((TileEntityPipe)connectedConsumer).steamStored)
							{
								transferElectricityAmount = Math.max(Math.min(averageElectricity - ((TileEntityPipe)connectedConsumer).steamStored, this.steamStored), 0);
							}
							else if(!(connectedConsumer instanceof TileEntityPipe))
							{
								transferElectricityAmount = this.steamStored;
							}
							
							int rejectedElectricity = connectedConsumer.onReceiveSteam(transferElectricityAmount, UniversalElectricity.getOrientationFromSide(i, (byte)2));
							this.steamStored = Math.max(Math.min(this.steamStored - transferElectricityAmount + rejectedElectricity, this.capacity), 0);
						}
					}
					
					if(connectedBlocks[i] instanceof ISteamProducer && this.steamStored < this.getSteamCapacity())
					{
						if(((ISteamProducer)connectedBlocks[i]).canProduceSteam(UniversalElectricity.getOrientationFromSide(i, (byte)2)))
						{
							int gainedElectricity = ((ISteamProducer)connectedBlocks[i]).onProduceSteam(this.capacity-this.steamStored, UniversalElectricity.getOrientationFromSide(i, (byte)2));
							this.steamStored = Math.max(Math.min(this.steamStored + gainedElectricity, this.capacity), 0);
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
	public int getStoredSteam()
    {
    	return this.steamStored;
    }
    
    @Override
    public int getSteamCapacity()
	{
		return this.capacity;
	}
	
	/**
     * Reads a tile entity from NBT.
     */
    public void readFromNBT(NBTTagCompound par1NBTTagCompound)
    {
        super.readFromNBT(par1NBTTagCompound);
        this.steamStored = par1NBTTagCompound.getInteger("steamStored");
        this.capacity = par1NBTTagCompound.getInteger("capacity");
    }

    /**
     * Writes a tile entity to NBT.
     */
    public void writeToNBT(NBTTagCompound par1NBTTagCompound)
    {
    	super.writeToNBT(par1NBTTagCompound);
    	par1NBTTagCompound.setInteger("steamStored", this.steamStored);
    	par1NBTTagCompound.setInteger("capacity", this.capacity);
    }
	
}

