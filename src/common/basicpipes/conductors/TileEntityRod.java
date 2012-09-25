package basicpipes.conductors;

import com.google.common.io.ByteArrayDataInput;

import net.minecraft.src.EntityPlayer;
import net.minecraft.src.NetworkManager;
import net.minecraft.src.Packet250CustomPayload;
import net.minecraft.src.TileEntity;
import net.minecraftforge.common.ForgeDirection;
import universalelectricity.network.IPacketReceiver;
import universalelectricity.network.PacketManager;
import basicpipes.BasicPipesMain;
import basicpipes.pipes.api.ILiquidConsumer;
import basicpipes.pipes.api.IMechenical;
import basicpipes.pipes.api.Liquid;

public class TileEntityRod extends TileEntity implements IPacketReceiver,IMechenical {
private int count = 0;
private float rotation = 0;
public int rpm = 0;//rpm received from producer
public int loadRPM = 0; //rpm lost to the load
public int currentRPM = 0;
public TileEntity front = null;
public TileEntity back = null; 
ForgeDirection frontD;
ForgeDirection backD;
	@Override
	public void handlePacketData(NetworkManager network,
			Packet250CustomPayload packet, EntityPlayer player,
			ByteArrayDataInput dataStream) {
		try{
			this.rpm = dataStream.readInt();
			this.loadRPM = dataStream.readInt();
			this.currentRPM = dataStream.readInt();
		}catch(Exception e)
		{
			
		}
		
	}
	public Object[] data()
	{
		return new Object[]{rpm,loadRPM,currentRPM};
	}
	@Override
	public void updateEntity()
	{
		if(count++ >= 5)
		{
			int meta = worldObj.getBlockMetadata(xCoord, yCoord, zCoord);
			frontD = ForgeDirection.getOrientation(meta);
			backD = ForgeDirection.getOrientation(meta).getOpposite();
			count = 0;
			if(!worldObj.isRemote)
			{
				
				
				back = worldObj.getBlockTileEntity(xCoord+backD.offsetX, yCoord+backD.offsetY, zCoord+backD.offsetZ);
				front = worldObj.getBlockTileEntity(xCoord+frontD.offsetX, yCoord+frontD.offsetY, zCoord+frontD.offsetZ);
				
				if(!(back instanceof IMechenical))
				{
					back = null;
				}
				if(!(front instanceof IMechenical))
				{
					front = null;
				}
				
				if(back != null && front != null)
				{
					this.rpm = ((IMechenical) back).getRPM(backD);
					this.loadRPM = ((IMechenical) front).useRPM(rpm)+10;
					this.currentRPM = rpm-loadRPM-10;//minus 10 is what it take to over come the rods friction
					if(currentRPM < 0)
					{
						//TODO add stress to rod and break if left stressed too long
					}
				}else
				{
					if(currentRPM > 0)
					{
						currentRPM-=10;
						if(currentRPM < 0)
						{
							currentRPM = 0;
						}
						
					}
				}
				
				
				
				
				
				
				PacketManager.sendTileEntityPacketWithRange(this, BasicPipesMain.channel, 20, this.data());
				
			}
			this.rotation = currentRPM * 240;
			if(back != null && back instanceof TileEntityRod)
			{
				this.rotation = ((TileEntityRod)front).rotation;
			}
		}
	}
	@Override
	public int getRPM(ForgeDirection side) {
		return this.currentRPM;
	}
	@Override
	public boolean canOutputSide(ForgeDirection side) {
		if(frontD != null && side == frontD)
		{
			return true;
		}
		return false;
	}
	@Override
	public boolean canInputSide(ForgeDirection side) {
		if(backD != null && side == backD)
		{
			return true;
		}
		return false;
	}
	@Override
	public int useRPM(int RPM) {
		// TODO Auto-generated method stub
		return 10+loadRPM;
	}

}
