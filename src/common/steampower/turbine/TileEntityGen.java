package steampower.turbine;

import net.minecraft.src.EntityPlayer;
import net.minecraft.src.NetworkManager;
import net.minecraft.src.Packet250CustomPayload;
import net.minecraftforge.common.ForgeDirection;
import steampower.TileEntityMachine;
import universalelectricity.extend.IElectricUnit;
import universalelectricity.network.IPacketReceiver;

import com.google.common.io.ByteArrayDataInput;

public class TileEntityGen extends TileEntityMachine implements IPacketReceiver,IElectricUnit
{

	@Override
	public void handlePacketData(NetworkManager network,
			Packet250CustomPayload packet, EntityPlayer player,
			ByteArrayDataInput dataStream) {
		// TODO Auto-generated method stub
		
	}
	public void onUpdate(float watts, float voltage, ForgeDirection side)
	{
		super.onUpdate(watts, voltage, side);
		if(!worldObj.isRemote)
		{
			
		}
	}

 
    public float electricityRequest()
    {
		return 0;
    
    }
    public boolean canConnect(ForgeDirection side)
    {
    	int face = this.facing;
    	if(side != ForgeDirection.UP && side != ForgeDirection.DOWN)
    	{
    		return true;
    	}
		return false;
    	
    }
    public boolean canReceiveFromSide(ForgeDirection side)
    {    	
		return false;    	
    }
    public float getVoltage()
    {
    	return 120;
    }
    
    public int getTickInterval()
    {
    	return 10;
    }

}
