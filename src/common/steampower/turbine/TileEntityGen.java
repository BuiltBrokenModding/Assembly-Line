package steampower.turbine;

import net.minecraft.src.EntityPlayer;
import net.minecraft.src.NetworkManager;
import net.minecraft.src.Packet250CustomPayload;
import net.minecraft.src.TileEntity;
import net.minecraftforge.common.ForgeDirection;
import steampower.TileEntityMachine;
import universalelectricity.electricity.ElectricityManager;
import universalelectricity.implement.IConductor;
import universalelectricity.implement.IElectricityProducer;
import universalelectricity.network.IPacketReceiver;

import basicpipes.pipes.api.IMechanical;

import com.google.common.io.ByteArrayDataInput;

public class TileEntityGen extends TileEntityMachine implements IPacketReceiver, IMechanical,IElectricityProducer
{
	ForgeDirection facing = ForgeDirection.DOWN;
	
	public int force = 0;
	public int aForce = 0;
	public int pos = 0;
	public int disableTicks = 0;
	public double genAmmount = 0;
	public int tCount = 0;
	public boolean empProf = false;
	
	IConductor[] wires = {null,null,null,null,null,null};
	public boolean needUpdate()
	{
		return false;
	}
	@Override
	public void updateEntity()
	{
		
		
		this.genAmmount = force/this.getVoltage();
		int meta = worldObj.getBlockMetadata(xCoord, yCoord, zCoord);
		int nMeta = 0;
		int wireCount = 0;
		switch(meta)
		{
			case 0: nMeta = 2;break;
			case 1: nMeta = 5;break;
			case 2: nMeta = 3;break;
			case 3: nMeta = 4;break;
		}
		facing = ForgeDirection.getOrientation(nMeta).getOpposite();
		if(genAmmount > 0)
		{
			//worldObj.setBlock(xCoord, yCoord+1, zCoord, 1);
		}
		for(int i = 0; i < 6; i++)
		{
			ForgeDirection side = ForgeDirection.UNKNOWN;
			switch(i)
			{
				case 0: side = ForgeDirection.UP;break;
				//case 1: side = ForgeDirection.DOWN;break;
				case 2: side = ForgeDirection.NORTH;break;
				case 3: side = ForgeDirection.EAST;break;
				case 4: side = ForgeDirection.SOUTH;break;
				case 5: side = ForgeDirection.WEST;break;
			}
			if(side != facing && side != facing.getOpposite())
			{
		        TileEntity tileEntity = worldObj.getBlockTileEntity(xCoord+side.offsetX, yCoord+side.offsetY, zCoord+side.offsetZ);
		
		        if (tileEntity instanceof IConductor)
		        {
		            if (ElectricityManager.instance.getElectricityRequired( ((IConductor)tileEntity).getConnectionID()) > 0)
		            {
		                this.wires[i] = (IConductor)tileEntity;
		                wireCount++;
		            }
		            else
		            {
		                this.wires[i] = null;
		            }
		        }
		        else
		        {
		            this.wires[i] = null;
		        }
			}
			
		}
		for(int side =0; side < 6; side++)
		{
			if(wires[side] instanceof IConductor)
			{
				double max = wires[side].getMaxAmps();
	            ElectricityManager.instance.produceElectricity(this, wires[side],Math.min(genAmmount/wireCount,max), this.getVoltage());
			}
		}
		super.updateEntity();
	}
	@Override
	public void handlePacketData(NetworkManager network, int packetType,
			Packet250CustomPayload packet, EntityPlayer player,
			ByteArrayDataInput data) {
		// TODO Auto-generated method stub
		
	}
	//------------------------------
	//Mechanics
	//------------------------------
	@Override
	public int getForceSide(ForgeDirection side) {
		if(side == facing.getOpposite())
		{
			return aForce;
		}
		return 0;
	}

	@Override
	public int getForce() {
		// TODO Auto-generated method stub
		return this.force;
	}

	@Override
	public boolean canOutputSide(ForgeDirection side) {
		if(side == facing.getOpposite())
		{
			return true;
		}
		return false;
	}

	@Override
	public boolean canInputSide(ForgeDirection side) {
		if(side == facing)
		{
			return true;
		}
		return false;
	}

	@Override
	public int applyForce(int force) {
		this.force = force;
		return force;
	}

	@Override
	public int getAnimationPos() {
		return pos;
	}
	//------------------------------
	//Electric
	//------------------------------
	@Override
	public void onDisable(int duration) 
	{
		this.disableTicks = duration;		
	}

	@Override
	public boolean isDisabled() {
		if(disableTicks-- <= 0)
		{
			return false;
		}
		return true;
	}

	@Override
	public double getVoltage() {
		return 120;
	}

	@Override
	public boolean canConnect(ForgeDirection side) {
		if(side != ForgeDirection.DOWN && side != facing && side != facing.getOpposite())
		{
			return true;
		}
		return false;
	}

}
