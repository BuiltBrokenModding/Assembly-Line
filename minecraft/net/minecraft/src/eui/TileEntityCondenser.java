package net.minecraft.src.eui;

import net.minecraft.src.NBTTagCompound;
import net.minecraft.src.eui.api.IWaterProducer;
import net.minecraft.src.universalelectricity.UEIConsumer;

public class TileEntityCondenser extends TileEntityMachine implements IWaterProducer,UEIConsumer {
	int tickCount = 0;
	int waterStored = 0;
	int energyStored = 0;
	@Override
	public int onProduceWater(int maxVol, int side) {
		if( maxVol > 0)
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
	public boolean canProduceWater(byte side) {
		// TODO Auto-generated method stub
		return true;
	}
	@Override
	public int onReceiveElectricity(int watts, int voltage, byte side) {
		int rejectedElectricity = Math.max((this.waterStored + watts) - this.getElectricityCapacity(), 0);
		this.energyStored += watts - rejectedElectricity;
		return rejectedElectricity;	
	}
	@Override
	public boolean canReceiveElectricity(byte side) {
		// TODO Auto-generated method stub
		return true;
	}
	@Override
	public int getStoredElectricity() {
		// TODO Auto-generated method stub
		return this.energyStored;
	}
	@Override
	public int getElectricityCapacity() {
		// TODO Auto-generated method stub
		return 1000;
	}
	@Override
	public int getVolts() {
		// TODO Auto-generated method stub
		return 240;
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
	

	

}
