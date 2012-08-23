package EUI.SteamPower;

import com.google.common.io.ByteArrayDataInput;

import universalelectricity.electricity.TileEntityElectricUnit;
import universalelectricity.extend.IRotatable;
import universalelectricity.network.IPacketReceiver;
import universalelectricity.network.PacketManager;
import net.minecraft.src.*;
import net.minecraftforge.common.ForgeDirection;
public class TileEntityMachine extends TileEntityElectricUnit
{
	public int facing = 0;
	private int count = 0;
	public TileEntity getSteamMachine(int i)
	{	
			int x = this.xCoord;
	    	int y = this.yCoord;
	    	int z = this.zCoord; 
	    	
    		switch(i)
    		{
			case 0: y = y - 1;break;
			case 1: y = y + 1;break;
			case 2: z = z + 1;break;
			case 3: z = z - 1;break;
			case 4: x = x + 1;break;
			case 5: x = x - 1;break;
    		}
		TileEntity aEntity = worldObj.getBlockTileEntity(x, y, z);			
		if(aEntity instanceof TileEntityMachine)
		{
			return aEntity;
		}
		
		return null;
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
	}
	
	@Override
	public void readFromNBT(NBTTagCompound par1NBTTagCompound)
	{
	        super.readFromNBT(par1NBTTagCompound);
	        this.facing = par1NBTTagCompound.getInteger("facing");
	}

	@Override
	public float electricityRequest() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean canReceiveFromSide(ForgeDirection side) {
		// TODO Auto-generated method stub
		return false;
	}
	

	public int getSizeInventory() {
		// TODO Auto-generated method stub
		return 0;
	}

	public ItemStack getStackInSlot(int var6) {
		// TODO Auto-generated method stub
		return null;
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
	public int getNumSide(ForgeDirection side) 
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
	
	public void onUpdate(float watts, float voltage, ForgeDirection side)
    {
		super.onUpdate(watts, voltage, side);
		count++;
		if(count >= 10)
		{
			if(!worldObj.isRemote)
			{
			PacketManager.sendTileEntityPacket(this, SteamPower.channel, getSendData());
			}
			count =  0;
		}
    }
}