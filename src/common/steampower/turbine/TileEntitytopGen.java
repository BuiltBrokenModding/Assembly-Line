package steampower.turbine;

import net.minecraft.src.TileEntity;
import net.minecraftforge.common.ForgeDirection;
import steampower.TileEntityMachine;
import universalelectricity.extend.IElectricUnit;
import basicpipes.pipes.api.ILiquidConsumer;
import basicpipes.pipes.api.ILiquidProducer;
import basicpipes.pipes.api.Liquid;

public class TileEntitytopGen extends TileEntityMachine implements IElectricUnit,ILiquidConsumer,ILiquidProducer {
public TileEntitySteamPiston genB = null;
	public void onUpdate(float watts, float voltage, ForgeDirection side)
	{ 
		if(!this.worldObj.isRemote)
		{
			super.onUpdate(watts, voltage, side);
			TileEntity ent = worldObj.getBlockTileEntity(xCoord, yCoord-1, xCoord);
			if(ent instanceof TileEntitySteamPiston)
			{
				genB = (TileEntitySteamPiston)ent;
			}
		}
	}
	@Override
	public int onProduceLiquid(Liquid type, int maxVol, ForgeDirection side) {
		// TODO Auto-generated method stub
		return genB !=null ? genB.onProduceLiquid(type, maxVol, side) : 0;
	}

	@Override
	public boolean canProduceLiquid(Liquid type, ForgeDirection side) {
		if(type == Liquid.WATER)
		{
			return true;
		}
		return genB !=null ? genB.canProduceLiquid(type, side) : false;
	}

	@Override
	public int onReceiveLiquid(Liquid type, int vol, ForgeDirection side) {
		// TODO Auto-generated method stub
		return genB !=null ?  genB.onReceiveLiquid(type, vol, side) : vol;
	}

	@Override
	public boolean canRecieveLiquid(Liquid type, ForgeDirection side) {
		if(type == Liquid.STEAM)
		{
			return true;
		}
		return genB !=null ?  genB.canRecieveLiquid(type, side): false;
	}

	@Override
	public int getStoredLiquid(Liquid type) {
		// TODO Auto-generated method stub
		return genB !=null ? genB.getStoredLiquid(type): 0;
	}

	@Override
	public int getLiquidCapacity(Liquid type) {
		// TODO Auto-generated method stub
		return genB !=null ? genB.getLiquidCapacity(type): 0;
	}

}
