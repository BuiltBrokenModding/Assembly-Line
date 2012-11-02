package basicpipes.machines;

import net.minecraft.src.TileEntity;
import net.minecraftforge.common.ForgeDirection;
import universalelectricity.implement.IElectricityReceiver;
import universalelectricity.prefab.TileEntityElectricityReceiver;
import basicpipes.pipes.api.ILiquidProducer;
import basicpipes.pipes.api.Liquid;
import basicpipes.pipes.api.MHelper;

public class TileEntityPump extends TileEntityElectricityReceiver implements ILiquidProducer,IElectricityReceiver {
 int dCount = 0;
 float eStored = 0;
 float eMax = 2000;
 int lStored = 0;
 int wMax = 10;
 public Liquid type = Liquid.DEFUALT;
 public TileEntity[] sList = {null,null,null,null,null,null};
 private int count = 0;
	@Override
	public void onDisable(int duration) {
		dCount = duration;
	}

	@Override
	public boolean isDisabled() {
		if(dCount <= 0)
		{
			return false;
		}
		return true;
	}

	@Override
	public void updateEntity() {
		super.updateEntity();
		if(count++ >= 40)
		{
			count = 0;
			sList = MHelper.getSourounding(worldObj,xCoord, yCoord, zCoord);
			int bBlock = worldObj.getBlockId(xCoord, yCoord -1, zCoord);
			Liquid bellow = Liquid.getLiquidByBlock(bBlock);
			
			if(bellow != null && this.lStored <= 0)
			{
				this.type = bellow;
			}
			
			if(!worldObj.isRemote)
			{
				if(bBlock == type.Still && this.eStored >= 200 && this.lStored < this.wMax)
				{
					eStored -= 200;
					lStored += 1;
					worldObj.setBlockAndMetadataWithNotify(xCoord, yCoord-1, zCoord, 0, 0);
					
				}
			}
		} 
	}

	@Override
	public boolean canReceiveFromSide(ForgeDirection side) {
		if(side != ForgeDirection.DOWN)
		{
		return true;
		}
		return false;
	}
	@Override
	public int onProduceLiquid(Liquid type, int maxVol, ForgeDirection side) {
		if(type == this.type && lStored > 0)
		{
			lStored -= 1;
	        return 1;
		}
		return 0;
	}

	@Override
	public boolean canProduceLiquid(Liquid type, ForgeDirection side) {
		int meta = worldObj.getBlockMetadata(xCoord, yCoord, zCoord);
		int facing = 0;
		 switch(meta)
	        {
	        	case 0: facing = 2;break;
	        	case 1: facing = 5;break;
	        	case 2: facing = 3;break;
	        	case 3: facing = 4;break;
	        }	
		
		if(type == this.type && side != ForgeDirection.DOWN && side != ForgeDirection.UP && side != ForgeDirection.getOrientation(facing).getOpposite())
		{
			return true;
		}
		return false;
	}

	@Override
	public int presureOutput(Liquid type, ForgeDirection side) {
		if(type == Liquid.WATER)
		{
			return 32;
		}else
		if(type == Liquid.LAVA)
		{
			return 10;
		}else
		if(type == this.type)
		{
			return 50;	
		}
		return 0;
	}

	@Override
	public boolean canProducePresure(Liquid type, ForgeDirection side) {
		if(type == this.type)
		{
			return true;
		}
		return false;
	}

	@Override
	public void onReceive(TileEntity sender, double watts, double voltage,
			ForgeDirection side) {
		if (wattRequest() > 0 && canConnect(side))
        {
            float rejectedElectricity = (float) Math.max((this.eStored + watts) - this.eMax, 0.0);
            this.eStored = (float) Math.max(this.eStored + watts - rejectedElectricity, 0.0);
        }
		
	}

	@Override
	public double wattRequest() {
		return Math.max(eMax - eStored,0);
	}
}
