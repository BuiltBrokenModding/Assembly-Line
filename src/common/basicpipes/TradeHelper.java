package basicpipes;

import basicpipes.pipes.api.ILiquidConsumer;
import basicpipes.pipes.api.Liquid;
import net.minecraft.src.TileEntity;
import net.minecraftforge.common.ForgeDirection;

public class TradeHelper {
/**
 * 
 * @param entity -  entity at center of search
 * @return an Array containing found entities and nulls of nonEntities
 */
	public static TileEntity[] getSourounding(TileEntity entity)
	{	
		TileEntity[] list = new TileEntity[]{null,null,null,null,null,null};
		for(int i =0; i< 6;i++)
		{
			int x = entity.xCoord;
	    	int y = entity.yCoord;
	    	int z = entity.zCoord; 
	    	
    		switch(i)
    		{
				case 0: y = y - 1;break;//down
				case 1: y = y + 1;break;//up
				case 2: z = z + 1;break;//north
				case 3: z = z - 1;break;//south
				case 4: x = x + 1;break;//east
				case 5: x = x - 1;break;//west
    		}
			TileEntity aEntity = entity.worldObj.getBlockTileEntity(x, y, z);			
			if(aEntity instanceof TileEntity)
			{
				list[i] = aEntity;
			}
		}
		return list;
	}
	/**
	 * Only works for steam Power's boiler. Still needs recode to work for all things
	 * @param blockEntity - tile entity trading the liquid
	 * @param type - liquid type see chart for info
	 * @param rise - does the liquid rise up like a gas
	 * @return the remaining untraded liquid
	 */
	 public static int shareLiquid(TileEntity blockEntity,Liquid type,boolean rise)	
		{
		 	TileEntity[] connectedBlocks = getSourounding(blockEntity);	    	
	    	ILiquidConsumer blockMachine = (ILiquidConsumer) blockEntity;
			int wSum = ((ILiquidConsumer)blockEntity).getStoredLiquid(type);
			int ammountStored = blockMachine.getStoredLiquid(type);
			int tankCount = 1;
			boolean bottom = false;
			TileEntity firstEntity = null;
			TileEntity secondEntity = null;
			if(rise)
			{
				firstEntity = connectedBlocks[1]; 
				secondEntity = connectedBlocks[0];
			}
			else
			{
				firstEntity = connectedBlocks[0];
				secondEntity = connectedBlocks[1];
			}
			//checks wether or not the block bellow it is a tank to move liquid too
			if(firstEntity instanceof ILiquidConsumer)
			{
				int bWater = ((ILiquidConsumer) firstEntity).getStoredLiquid(type);
				int bMax = ((ILiquidConsumer) firstEntity).getLiquidCapacity(type);
				//checks if that tank has room to get liquid.
				
					if(bWater < bMax)
					{
						int tradeVol = 0;
						int emptyVol = Math.max( bMax - bWater,0);
						tradeVol = Math.min(emptyVol, ammountStored);
						int rejected = ((ILiquidConsumer) firstEntity).onReceiveLiquid(type, tradeVol, ForgeDirection.getOrientation(1));
						ammountStored = ammountStored + rejected - tradeVol;
						wSum -= tradeVol;
					}
					else 
					{
						bottom = true;
					}
			}
			else
			{
				//there was no tank bellow this tank
				bottom = true;
			}			
			//if this is the bottom tank or bottom tank is full. Update average water ammount.
			if(bottom)
			{
				//get average water around center tank
				for(int i = 2; i<6;i++)
				{
					TileEntity entityA = connectedBlocks[i];
					if(entityA instanceof ILiquidConsumer)
					{
						//if is a tank add to the sum
						wSum += ((ILiquidConsumer) entityA).getStoredLiquid(type);
						tankCount += 1;
					}
				}
			
				//if this is the bottom tank or bottom tank is full then trade liquid with tanks around it.
				for(int i = 2; i<6;i++)
				{
					int average = Math.round((float)wSum / (float)tankCount);// takes the sum and makes it an average
					int tradeSum = 0;
					TileEntity entity = connectedBlocks[i];
					if(entity instanceof ILiquidConsumer)
					{
						int targetW = ((ILiquidConsumer) entity).getStoredLiquid(type);
						if(targetW < average)
						{
							tradeSum = Math.min(average, ammountStored); //gets the ammount to give to the target tank
							int rejectedAm = ((ILiquidConsumer) entity).onReceiveLiquid(type, tradeSum, ForgeDirection.getOrientation(i)); //send that ammount with safty incase some comes back
							ammountStored =rejectedAm + ammountStored - tradeSum; //counts up current water sum after trade
						}
					}
				}
			
				if(secondEntity instanceof ILiquidConsumer)
				{
					int bWater = ((ILiquidConsumer) secondEntity).getStoredLiquid(type);
					int bMax = ((ILiquidConsumer) secondEntity).getLiquidCapacity(type);
					if(bottom && ammountStored > 0)
					{
						if(bWater < bMax)
						{
							int emptyVolS = Math.max( bMax - bWater,0);
							int tradeVolS = Math.min(emptyVolS, ammountStored);
							int rejectedS = ((ILiquidConsumer) secondEntity).onReceiveLiquid(type, tradeVolS, ForgeDirection.getOrientation(0));;
							ammountStored =rejectedS + ammountStored - tradeVolS;
							wSum -= tradeVolS;
						}				
					}
				}
			}
			return ammountStored;
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
			return 1; 
		 }
		 if(en[2] != null && en[5] != null && en[3] == null && en[4] == null)
		 {
			return 2; 
		 }
		 if(en[5] != null && en[3] != null && en[4] == null && en[2] == null)
		 {
			 return 3; 
		 }
		 if(en[3] != null && en[4] != null && en[2] == null && en[5] == null)
		 {
			 return 4;
		 }
		 
		 return 0;
		 
	 }
}
