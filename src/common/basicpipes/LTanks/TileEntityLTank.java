package basicpipes.LTanks;

import com.google.common.io.ByteArrayDataInput;

import universalelectricity.network.IPacketReceiver;
import universalelectricity.network.PacketManager;
import universalelectricity.prefab.Vector3;
import basicpipes.BasicPipesMain;
import basicpipes.pipes.api.ILiquidConsumer;
import basicpipes.pipes.api.ILiquidProducer;
import basicpipes.pipes.api.Liquid;
import basicpipes.pipes.api.MHelper;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.ItemStack;
import net.minecraft.src.NBTTagCompound;
import net.minecraft.src.NBTTagList;
import net.minecraft.src.NetworkManager;
import net.minecraft.src.Packet;
import net.minecraft.src.Packet250CustomPayload;
import net.minecraft.src.TileEntity;
import net.minecraftforge.common.ForgeDirection;

public class TileEntityLTank extends TileEntity implements ILiquidConsumer,ILiquidProducer,IPacketReceiver{
public TileEntity[] cc = {null,null,null,null,null,null};
public Liquid type = Liquid.DEFUALT;
public int LStored = 0;
public int pLStored = 0;
public int LMax = 4;
private int count = 0;
private int count2 = 0;
private boolean firstUpdate = true;
public void updateEntity()
{	
	if(++count >= 10)
	{ 
		count = 0;	
		this.cc = MHelper.getSourounding(this);
		if(!worldObj.isRemote)
		{
			MHelper.shareLiquid(this, type, LStored);
			if(firstUpdate ||(this.LStored != pLStored)|| count2 >= 100)
			{
				count2 = 0;
				firstUpdate = false;
				Packet packet = PacketManager.getPacket(BasicPipesMain.channel, this, new Object[]{type.ordinal(),LStored});
				PacketManager.sendPacketToClients(packet, worldObj, Vector3.get(this), 20);
			}
			this.pLStored = this.LStored;
		}
	}
}
public void writeToNBT(NBTTagCompound nbt)
{
    super.writeToNBT(nbt);
    nbt.setInteger("Vol", this.LStored);
    nbt.setInteger("type", this.type.ordinal());
}

@Override
public void readFromNBT(NBTTagCompound nbt)
{
	super.readFromNBT(nbt);
	this.LStored = nbt.getInteger("Vol");
	this.type = Liquid.getLiquid(nbt.getInteger("type"));
}

//--------------------
//Liquid stuff
//------------------------------------
@Override
public int onReceiveLiquid(Liquid type, int vol, ForgeDirection side) 
{
	if(type == this.type)
	{
		int rejectedVolume = Math.max((this.getStoredLiquid(type) + vol) - this.LMax, 0);
		this.LStored = Math.min(Math.max((LStored + vol - rejectedVolume),0),this.LMax);
		return rejectedVolume;
	}
	return vol;
}

@Override
public boolean canRecieveLiquid(Liquid type, ForgeDirection side) {
	if(type == this.type)
	{
		if(this.type.isGas && side == ForgeDirection.UP)
		{
			return false;
		}
		if(!this.type.isGas && side == ForgeDirection.DOWN)
		{
			return false;
		}
		return true;
	}
	return false;
}

@Override
public int getStoredLiquid(Liquid type) {
	if(type == this.type)
	{
		return LStored;
	}
	return 0;
}

@Override
public int getLiquidCapacity(Liquid type) {
	if(type == this.type)
	{
		return LMax;
	}
	return 0;
}

public Liquid getType() {
	// TODO Auto-generated method stub
	return type;
}

@Override
public int onProduceLiquid(Liquid type, int vol, ForgeDirection side) {
	if(type == this.type)
	{
		int aVol = Math.max(((this.getStoredLiquid(type) - vol) + this.LMax)-vol, 0);
		this.LStored = Math.min(Math.max((LStored - aVol),0),this.LMax);
		return aVol;
	}
	return 0;
}

@Override
public boolean canProduceLiquid(Liquid type, ForgeDirection side) {
	if(type == this.type)
	{
		if(this.type.isGas && side == ForgeDirection.UP)
		{
			return true;
		}
		if(!this.type.isGas && side == ForgeDirection.DOWN)
		{
			return true;
		}
	}
	return false;
}

@Override
public boolean canProducePresure(Liquid type, ForgeDirection side) {
	if(type == this.type)
	{
		if(this.type.isGas && side == ForgeDirection.UP)
		{
			return true;
		}
		if(!this.type.isGas && side == ForgeDirection.DOWN)
		{
			return true;
		}
	}
	return false;
}

@Override
public int presureOutput(Liquid type, ForgeDirection side) {
	if(type == this.type)
	{
		if(this.type.isGas && side == ForgeDirection.UP)
		{
			return this.type.defaultPresure;
		}
		if(!this.type.isGas && side == ForgeDirection.DOWN)
		{
			return this.type.defaultPresure;
		}
	}
	return 0;
}


@Override
public void handlePacketData(NetworkManager network, int packetType,
		Packet250CustomPayload packet, EntityPlayer player,
		ByteArrayDataInput data) {
	try
	{
		this.type = Liquid.getLiquid(data.readInt());
		this.LStored = data.readInt();
	}catch(Exception e)
	{
		e.printStackTrace();
		System.out.print("Fail reading data for Storage tank \n");
	}
	
}
public void setType(Liquid dm) {
	this.type = dm;
	
}
}
