package SteamPower.turbine;

import com.google.common.io.ByteArrayDataInput;

import net.minecraft.src.EntityPlayer;
import net.minecraft.src.IInventory;
import net.minecraft.src.ItemStack;
import net.minecraft.src.NetworkManager;
import net.minecraft.src.Packet250CustomPayload;
import net.minecraft.src.TileEntity;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.common.ISidedInventory;
import universalelectricity.extend.IElectricUnit;
import universalelectricity.network.IPacketReceiver;
import BasicPipes.pipes.api.ILiquidConsumer;
import BasicPipes.pipes.api.ILiquidProducer;
import SteamPower.TileEntityMachine;

public class TileEntitytopGen extends TileEntityMachine implements IElectricUnit,ILiquidConsumer,ILiquidProducer {
public TileEntityGenerator genB = null;
	public void onUpdate(float watts, float voltage, ForgeDirection side)
	{ 
		if(!this.worldObj.isRemote)
		{
			super.onUpdate(watts, voltage, side);
			TileEntity ent = worldObj.getBlockTileEntity(xCoord, yCoord-1, xCoord);
			if(ent instanceof TileEntityGenerator)
			{
				genB = (TileEntityGenerator)ent;
			}
		}
	}
	@Override
	public int onProduceLiquid(int type, int maxVol, ForgeDirection side) {
		// TODO Auto-generated method stub
		return genB !=null ? genB.onProduceLiquid(type, maxVol, side) : 0;
	}

	@Override
	public boolean canProduceLiquid(int type, ForgeDirection side) {
		// TODO Auto-generated method stub
		return genB !=null ? genB.canProduceLiquid(type, side) : false;
	}

	@Override
	public int onReceiveLiquid(int type, int vol, ForgeDirection side) {
		// TODO Auto-generated method stub
		return genB !=null ?  genB.onReceiveLiquid(type, vol, side) : vol;
	}

	@Override
	public boolean canRecieveLiquid(int type, ForgeDirection side) {
		// TODO Auto-generated method stub
		return genB !=null ?  genB.canRecieveLiquid(type, side): false;
	}

	@Override
	public int getStoredLiquid(int type) {
		// TODO Auto-generated method stub
		return genB !=null ? genB.getStoredLiquid(type): 0;
	}

	@Override
	public int getLiquidCapacity(int type) {
		// TODO Auto-generated method stub
		return genB !=null ? genB.getLiquidCapacity(type): 0;
	}

}
