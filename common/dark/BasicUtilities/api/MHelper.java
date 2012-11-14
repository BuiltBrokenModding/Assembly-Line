package dark.BasicUtilities.api;

import net.minecraft.src.TileEntity;
import net.minecraft.src.World;
import net.minecraftforge.common.ForgeDirection;

public class MHelper {
/**
 * you will have to tell your TileEntity to trade up if liquid or down if gas. I suggest having your onRecieveLiquid 
 * pump liquid one block up if you block is full.
 * @param entity -  entity at center of search
 * @return an Array containing TileEntities around the TileEntity
 */
	public static TileEntity[] getSourounding(World world, int x, int y, int z)
	{	
		TileEntity[] list = new TileEntity[]{null,null,null,null,null,null};
		for(int i =0; i< 6;i++)
		{
			ForgeDirection d = ForgeDirection.getOrientation(i);
			TileEntity aEntity = world.getBlockTileEntity(x+d.offsetX, y+d.offsetY, z+d.offsetZ);			
			if(aEntity instanceof TileEntity)
			{
				list[i] = aEntity;
			}
		}
		return list;
	}
	/**
	 * Used to help trade liquid without having to do too much work
	 * @param blockEntity - tile entity trading the liquid
	 * @param type - liquid type being traded
	 * @param vol - the volume to be traded
	 * @return the remaining untraded liquid
	 */
	public static int shareLiquid(World world, int x, int y, int z,int vol,int max, Liquid type)
	{
		TileEntity ent = world.getBlockTileEntity(x, y, z);
		int currentVol = vol;
		
		int tCount = 1;
		boolean rise = type.isGas;
		if(currentVol <= 0)
		{
			return 0;
		}
		
		
		ForgeDirection st = ForgeDirection.getOrientation(rise ? 1 : 0);
		TileEntity first = world.getBlockTileEntity(x+st.offsetX, y+st.offsetY, z+st.offsetZ);
		//trades to the first, bottom for liquid, top for gas
		if(first instanceof IStorageTank && currentVol > 0 && ((IStorageTank) first).getStoredLiquid(type) < ((IStorageTank) first).getLiquidCapacity(type))
		{
			currentVol = ((ILiquidConsumer) first).onReceiveLiquid(type, currentVol, st);
		}
		int vAve = currentVol;
		TileEntity[] TeA = MHelper.getSourounding(world,x,y,z);
		for(int i = 2; i < 6; i++)
		{
			if(TeA[i] instanceof IStorageTank)
			{
				vAve += ((IStorageTank)TeA[i]).getStoredLiquid(type);
				tCount++;
			}
		}
		vAve = (int)(vAve/tCount);
		//trades to side if anything is left
		for(int i = 2; i < 6;i++)
		{
			ForgeDirection side = ForgeDirection.getOrientation(i);
			TileEntity sSide = world.getBlockTileEntity(x+side.offsetX, y+side.offsetY, z+side.offsetZ);
			if(currentVol <= 0 || currentVol <= vAve)
			{
				break;
			}
			if(sSide instanceof IStorageTank &&((IStorageTank) sSide).getStoredLiquid(type) < vAve)
			{
				int tA = vAve -Math.max((vAve - currentVol), 0);
				currentVol = ((ILiquidConsumer) sSide).onReceiveLiquid(type, tA, st) -tA + currentVol;
			}
		}
		//trades to the opposite of the first if anything is left
		/**
		 * need to find a way to solve it just trading back the ammount on next cycle
		if(currentVol > 0)
		{
			TileEntity last = world.getBlockTileEntity(x+st.getOpposite().offsetX, y+st.getOpposite().offsetY, z+st.getOpposite().offsetZ);
			if(last instanceof IStorageTank && currentVol == max)
			{
				currentVol = ((ILiquidConsumer) last).onReceiveLiquid(type, max/10, st.getOpposite()) + currentVol -(max/10);
			}
		}
		*/
		return Math.max(currentVol,0);	
	}
	 /**
	  * 
	  * @param entity - entity in question
	  * @return 1-4 if corner 0 if not a corner
	  * you have to figure out which is which depending on what your using this for
	  * 1 should be north east 2 south east
	  */
	 public static int corner(TileEntity entity)
	 {
		 TileEntity[] en =  getSourounding(entity.worldObj, entity.xCoord,entity.yCoord,entity.zCoord);
		 if(en[4] != null && en[2] != null && en[5] == null && en[3] == null)
		 {
			return 3; 
		 }
		 if(en[2] != null && en[5] != null && en[3] == null && en[4] == null)
		 {
			return 4; 
		 }
		 if(en[5] != null && en[3] != null && en[4] == null && en[2] == null)
		 {
			 return 1; 
		 }
		 if(en[3] != null && en[4] != null && en[2] == null && en[5] == null)
		 {
			 return 2;
		 }
		 
		 return 0;
		 
	 }
}
