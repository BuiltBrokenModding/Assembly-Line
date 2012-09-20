package basicpipes.pipes;

import basicpipes.pipes.api.ILiquidProducer;
import basicpipes.pipes.api.Liquid;
import net.minecraft.src.NBTTagCompound;
import net.minecraft.src.TileEntity;
import net.minecraftforge.common.ForgeDirection;
import universalelectricity.extend.IElectricUnit;

public class TileEntityCondenser extends TileEntity implements ILiquidProducer, IElectricUnit {
	int tickCount = 0;
	int waterStored = 0;
	int energyStored = 0;
	@Override
	public int onProduceLiquid(Liquid type,int maxVol, ForgeDirection side) {
		if(type == Liquid.WATER)
		{
			int tradeW = Math.min(maxVol, waterStored);
			waterStored -= tradeW;
	        return tradeW;
		}
    	return 0;
	}
	public void writeToNBT(NBTTagCompound par1NBTTagCompound)
    {
        super.writeToNBT(par1NBTTagCompound);
        par1NBTTagCompound.setInteger("energyStored", (int)this.energyStored);
        par1NBTTagCompound.setInteger("waterStored", (int)this.waterStored);
    }
	
	public void readFromNBT(NBTTagCompound par1NBTTagCompound)
    {
        super.readFromNBT(par1NBTTagCompound);
        this.energyStored = par1NBTTagCompound.getInteger("energyStored");
        this.waterStored = par1NBTTagCompound.getInteger("waterStored");
    }
    public void updateEntity()
	{    	
	if(energyStored > 100 && tickCount > 200 && waterStored < 10)
	{
		energyStored -= 100;
		waterStored += 1;
		tickCount = 0;
	}
	tickCount++;
	}
	@Override
	public boolean canProduceLiquid(Liquid type, ForgeDirection side) {
		if(type == Liquid.WATER)
		{
			return true;
		}
		return false;
	}
	@Override
	public void onDisable(int duration) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public boolean isDisabled() {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public void onUpdate(float amps, float voltage, ForgeDirection side) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public float ampRequest() {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public boolean canConnect(ForgeDirection side) {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public boolean canReceiveFromSide(ForgeDirection side) {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public float getVoltage() {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public int getTickInterval() {
		// TODO Auto-generated method stub
		return 0;
	}
	

	

}
