package basicpipes.conductors;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.NBTTagCompound;
import net.minecraft.src.NetworkManager;
import net.minecraft.src.Packet;
import net.minecraft.src.Packet250CustomPayload;
import net.minecraft.src.TileEntity;
import net.minecraftforge.common.ForgeDirection;
import universalelectricity.network.IPacketReceiver;
import universalelectricity.network.PacketManager;
import universalelectricity.prefab.Vector3;
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
	public int capacity = 2;
	public int hPressure = this.presure;
	private int count = 0;
	private boolean intiUpdate = true;
	//Stores information on all connected blocks around this tile entity
	public TileEntity[] connectedBlocks = {null, null, null, null, null, null};
	//Checks if this is the first the tile entity updates
	protected boolean firstUpdate = true;
	public int presure = 0;
	/**
	 * This function adds a connection between this pipe and other blocks
	 * @param tileEntity - Must be either a producer, consumer or a conductor
	 * @param side - side in which the connection is coming from
	 */	
	public void addConnection(TileEntity tileEntity, ForgeDirection side)
	{
		this.connectedBlocks[side.ordinal()] = null;
		if(tileEntity instanceof ILiquidConsumer)
		{
			if(((ILiquidConsumer)tileEntity).canRecieveLiquid(this.type, side))
			{
				this.connectedBlocks[side.ordinal()] = tileEntity;
			}
		}else
		if(tileEntity instanceof ILiquidProducer)
		{
			if(((ILiquidProducer)tileEntity).canProduceLiquid(this.type, side))
			{
			this.connectedBlocks[side.ordinal()] = tileEntity;
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
		if(++count >= 10 || firstUpdate)
		{count = 0;firstUpdate = false;
			BlockPipe.updateConductorTileEntity(this.worldObj, this.xCoord, this.yCoord, this.zCoord);
			if(!this.worldObj.isRemote){
				//update connections
				
				//send packet with liquid type data
				Packet packet = PacketManager.getPacket("Pipes",this, new Object[]{this.type.ordinal()});
				PacketManager.sendPacketToClients(packet, worldObj, Vector3.get(this), 60);
				
			
						
				int connectedUnits = 0;
				int pipes = 1;
				int producers = 0;
				int averageVolume = this.liquidStored;			
				int aProducerPressure = 0;
				
				for(int i = 0; i < 6; i++)
		        {
					if(connectedBlocks[i] instanceof ILiquidConsumer || connectedBlocks[i] instanceof ILiquidProducer)
					{
						connectedUnits ++;
						if(connectedBlocks[i] instanceof ILiquidProducer)
						{
							if(((ILiquidProducer)connectedBlocks[i]).canProducePresure(this.type, ForgeDirection.getOrientation(i)))
							{
								aProducerPressure += ((ILiquidProducer)connectedBlocks[i]).presureOutput(this.type,ForgeDirection.getOrientation(i));
								producers++;
							}
						}
							
						if(connectedBlocks[i] instanceof TileEntityPipe)
						{
							pipes ++;
							//add pipes volume to average collection value
							averageVolume += ((TileEntityPipe)connectedBlocks[i]).liquidStored;
							//get the current pipes pressure
							int pPressure =  ((TileEntityPipe)connectedBlocks[i]).presure ;
							if(pPressure > hPressure)
							{
								this.hPressure = pPressure;
							}							
						}
					}
				}        
				//turn average collection into actual average pipe volume
				averageVolume = Math.max(averageVolume/pipes,0);
				//sets the pressure of the pipe to the producer pressure or to the highest pipe pressure -1
				if(producers > 0)
				{
					aProducerPressure = Math.max(aProducerPressure/producers,0);
					this.presure = aProducerPressure;
				}
				else
				if(connectedUnits > 0)
				{
					this.presure = hPressure - 1;
				}else
				{
					this.presure = 1;
				}
				//only trade liquid if there is more than one thing connect and its pressure is higher than 1
				if(connectedUnits > 0 && this.presure > 0)
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
									if(connectedConsumer instanceof TileEntityPipe)
									{
										if(((TileEntityPipe)connectedBlocks[i]).presure < this.presure)
										{
											transferVolumeAmount = this.liquidStored;
										}
									}
									else
									{
										transferVolumeAmount = this.liquidStored;
									}								
									
									int rejectedVolume = connectedConsumer.onReceiveLiquid(this.type,transferVolumeAmount, ForgeDirection.getOrientation(i));
									this.liquidStored = Math.max(Math.min(this.liquidStored - transferVolumeAmount + rejectedVolume, this.capacity), 0);
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
    		return this.capacity;
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

