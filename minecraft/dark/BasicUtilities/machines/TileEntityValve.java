package dark.BasicUtilities.machines;

import net.minecraft.tileentity.TileEntity;
import dark.BasicUtilities.api.Liquid;
import dark.BasicUtilities.api.MHelper;
import dark.BasicUtilities.pipes.TileEntityPipe;

public class TileEntityValve extends TileEntity{
Liquid type = Liquid.DEFUALT;
int liquidStored = 0;
int lMax = 1;
int tickCount = 0;
TileEntity[] connected = {null,null,null,null,null,null};
boolean on = false;
	@Override
	public void updateEntity()
	{
		 tickCount++;
		 if(tickCount >= 10)
		 {
			 int deltaX = 0;
			 int deltaZ = 0;
			 int deltaY = 0;
			 int facing = 0;
			 int meta = worldObj.getBlockMetadata(xCoord, yCoord, zCoord);
			 if(meta == 0 && meta == 8)
			 {
				 facing = 2;
			 }
			 if(meta == 1 && meta == 9)
			 {
				 facing = 5;
			 }
			 if(meta == 2 && meta == 10)
			 {
				 facing = 3;
			 }
			 if(meta == 3 && meta == 11)
			 {
				 facing = 4;
			 }
			 if((meta > 3 && meta < 8)&&(meta> 11 && meta < 16))
			 {
				 facing = 0;
			 }
			 switch(facing)
			 {
			 	case 0: deltaY++;break;
			 	case 1: deltaY--;break;
				 case 2: deltaZ--;break;
				 case 5: deltaZ++;break;
				 case 3: deltaX--;break;
				 case 4: deltaX++;break;
			 }
			
			 connected = MHelper.getSourounding(worldObj,xCoord, yCoord, zCoord);
			 for(int i = 0;i<6;i++)
			 {
				 if(!(connected[i] instanceof TileEntityPipe))
				 {
					 connected[i] = null;
				 }
			 }
			 if(!worldObj.isRemote)
			 {
				 //TODO send packet
			 }
			 tickCount = 0;
		 }
	}
	

}
