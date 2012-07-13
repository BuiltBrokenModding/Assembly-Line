package net.minecraft.src.eui;

import net.minecraft.src.NBTTagCompound;
import net.minecraft.src.pipes.api.ILiquidProducer;

public class TileEntityCondenser extends TileEntityMachine implements ILiquidProducer {
	int tickCount = 0;
	int waterStored = 0;
	int energyStored = 0;
	@Override
	public int onProduceLiquid(int type,int maxVol, int side) {
		/**if(type == 1)
		{
			int tradeW = Math.min(maxVol, waterStored);
			waterStored -= tradeW;
	        return tradeW;
		}**/
    	return 1;
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
	public boolean canProduceLiquid(int type, byte side) {
		if(type == 1)
		{
			return true;
		}
		return false;
	}
	

	

}
