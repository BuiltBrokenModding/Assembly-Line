package basicpipes.pipes.api;

import net.minecraft.src.TileEntity;
import net.minecraftforge.common.ForgeDirection;

public class MHelper {
/**
 * 
 * @param entity -  entity at center of search
 * @return an Array containing TileEntities around the TileEntity
 */
	public static TileEntity[] getSourounding(TileEntity te)
	{	
		TileEntity[] list = new TileEntity[]{null,null,null,null,null,null};
		for(int i =0; i< 6;i++)
		{
			ForgeDirection d = ForgeDirection.getOrientation(i);
			TileEntity aEntity = te.worldObj.getBlockTileEntity(te.xCoord+d.offsetX, te.yCoord+d.offsetY, te.zCoord+d.offsetZ);			
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
	public static int shareLiquid(TileEntity te, Liquid type,int vol)
	{
		int currentVol = vol;
		boolean rise = type.isGas;
		ForgeDirection st = ForgeDirection.getOrientation(rise ? 1 : 0);
		TileEntity first = te.worldObj.getBlockTileEntity(te.xCoord+st.offsetX, te.yCoord+st.offsetX, te.zCoord+st.offsetX);
		//trades to the first, bottom for liquid, top for gas
		if(first instanceof ILiquidConsumer && ((ILiquidConsumer) first).getStoredLiquid(type) < ((ILiquidConsumer) first).getLiquidCapacity(type))
		{
			currentVol = ((ILiquidConsumer) first).onReceiveLiquid(type, vol, st);
		}
		//trades to side if anything is left
		for(int i = 2; i < 6;i++)
		{
			ForgeDirection side = ForgeDirection.getOrientation(i);
			TileEntity sSide = te.worldObj.getBlockTileEntity(te.xCoord+side.offsetX, te.yCoord+side.offsetX, te.zCoord+side.offsetX);
			if(sSide instanceof ILiquidConsumer && ((ILiquidConsumer) sSide).getStoredLiquid(type) < ((ILiquidConsumer) sSide).getLiquidCapacity(type)
					&& currentVol > 0)
			{
				currentVol = ((ILiquidConsumer) sSide).onReceiveLiquid(type, vol, st);
			}
		}
		//trades to the opposite of the first if anything is left
		if(currentVol > 0)
		{
			TileEntity last = te.worldObj.getBlockTileEntity(te.xCoord+st.getOpposite().offsetX, te.yCoord+st.getOpposite().offsetX, te.zCoord+st.getOpposite().offsetX);
			if(last instanceof ILiquidConsumer && ((ILiquidConsumer) last).getStoredLiquid(type) < ((ILiquidConsumer) last).getLiquidCapacity(type))
			{
				currentVol = ((ILiquidConsumer) last).onReceiveLiquid(type, vol, st);
			}
		}
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
		 TileEntity[] en =  getSourounding(entity);
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
