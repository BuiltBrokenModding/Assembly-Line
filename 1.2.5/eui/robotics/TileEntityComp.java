package net.minecraft.src.eui.robotics;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.src.AxisAlignedBB;
import net.minecraft.src.Block;
import net.minecraft.src.EntityItem;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.ItemStack;
import net.minecraft.src.Material;
import net.minecraft.src.ModLoader;
import net.minecraft.src.NBTTagCompound;
import net.minecraft.src.NBTTagList;
import net.minecraft.src.TileEntity;
import net.minecraft.src.universalelectricity.Vector3;
import net.minecraft.src.universalelectricity.electricity.TileEntityElectricUnit;

public class TileEntityComp extends TileEntityElectricUnit {
	public EntityRobot[] BotList = {null,null,null,null};
	
	int updateCount = 0;
	int scanCount = 0;
	boolean hasScanned = false;
	int lastScanXDif=0;
	int lastScanYDif=0;
	int lastScanZDif=0;
	Vector3[] harvestList={null,null,null,null,null,null,null,null,null,null};
	public void onUpdate(float watts, float voltage, byte side)
    { 
		if(!worldObj.isRemote)
		{
		++updateCount;
		cleanList();
		for(int b =0;b<4;b++)
		{
			if(BotList[b] ==null)
			{
				//spawn bot for testing
				EntityShoeBot bot = new EntityShoeBot(worldObj);
				bot.setLocationAndAngles(this.xCoord, this.yCoord+1, this.zCoord, 10, 10);
				bot.linkFrq[0]=this.xCoord;
				bot.linkFrq[1]=this.yCoord;
				bot.linkFrq[2]=this.zCoord;
				bot.isLinked = true;
	        	worldObj.spawnEntityInWorld(bot);
	        	BotList[b]=bot;
			}
		}
		if(updateCount >= 50 && worldObj.checkChunksExist(xCoord, yCoord, zCoord, xCoord+1, yCoord+1, zCoord+1))
		{
			++scanCount;
			if(scanCount < 10){hasScanned = true;}else{scanCount = 0;hasScanned=false;}
			Vector3 thisBlock = new Vector3(this.xCoord,this.yCoord,this.zCoord);
			updateCount = 0;
       Vector3 targetVec = findBlock(thisBlock,Block.leaves,20,hasScanned);
        if(targetVec == null)
        { 
        	targetVec = findBlock(thisBlock,Block.wood,20,hasScanned);        
        }
        if(targetVec != null)
        {
        	int Targetx = targetVec.intX();
    		int Targety = targetVec.intY();
    		int Targetz = targetVec.intZ();
    		ModLoader.getMinecraftInstance().thePlayer.addChatMessage("rb:"+Targetx+"X:"+Targety+"Y:"+Targetz+"Z");
    		int blockTargetc = worldObj.getBlockId(Targetx, Targety, Targetz);
    		boolean taskreceived = sendTask(targetVec,"harvest");
    		if(taskreceived)
    		{
    			ModLoader.getMinecraftInstance().thePlayer.addChatMessage("Harvest Task sent to robot");
    		}
    		else
    		{
    			ModLoader.getMinecraftInstance().thePlayer.addChatMessage("Task not sent");
    		}
        }
        else
        {
        	ModLoader.getMinecraftInstance().thePlayer.addChatMessage("N/A");//nothing found from scan
        	scanCount = 0;
        }        
		}
		}
    }
	/**
	 * 
	 * @param taskLoc - location of the task
	 * @param task - what is the task harvest, replace, mine, build
	 * @param bot - bot being given the task
	 * @return whether or not the task was received
	 */
	public boolean sendTask(Vector3 taskLoc,String task)
	{
		for(int i = 0;i < BotList.length;i++)
		{
			if(BotList[i] instanceof EntityRobot)
			{
				String botTaskMain = BotList[i].getTaskType();
				if(botTaskMain.toLowerCase() == task.toLowerCase())
				{
					if(BotList[i].isIdle())
					{
						 return BotList[i].setWorkTask(taskLoc,task);
					}
				}
			}
		}
		return false;
	}
/**
 * 
 * @param startSpot - center of the scan radius
 * @param block - block being looked for
 * @param range - block count from center to scan
 * @param resume - whether or not to resume scan from returned block
 * @return location vector3 of the block equaling scan args
 */
	public Vector3 findBlock(Vector3 startSpot,Block block,int range,boolean resume)
	{
		
		
		int Startx = startSpot.intX();
		int Starty = startSpot.intY();
		int Startz = startSpot.intZ();
		int distanceX = (range * 2) + 1;
		int distanceZ = (range * 2) + 1;
		int distanceY = (range * 2) + 1;
		Boolean negX = Startx < 0;
		Boolean negZ = Startz < 0;
		int xChange = -1;
		int zChange = -1;
		int yChange = -1;
		Startx += range;
		Startz += range;
		Starty += range;			
		int pauseCount = 0;		
		//ModLoader.getMinecraftInstance().thePlayer.addChatMessage("starting Scan For " + block.getBlockName());		
		int y = Starty;
		for(int iY=0;iY < (distanceY*2);iY++)
		{
			
			pauseCount++;
			if(pauseCount >= 2)
			{
			int x = Startx;
			int z = Startz;
			
			for(int iX=0;iX < distanceY;iX++)
			{				
				for(int iZ=0;iZ < distanceZ;iZ++)
				{				
										
					int blockTargetID = worldObj.getBlockId(x, y, z);
					//System.out.println("BlockAt:"+x+"x:"+y+"y:"+z+"z:"+blockTargetID+"ID");
					if(blockTargetID == block.blockID)
					{
						Vector3 targetBlock = new Vector3(x,y,z);
						if(!onHarvestList(targetBlock))
						{
						//ModLoader.getMinecraftInstance().thePlayer.addChatMessage("Target Block Found");
							boolean taskAdded = addHarvest(targetBlock);
							if(taskAdded)
							{
								return targetBlock;
							}
						}
						
					}
					
					z += zChange;
				}
				x += xChange;
				z = Startz;	
			}
			pauseCount =0;
			y += yChange;
			}
			
				
		}
		return null;
		
	}
	private boolean addHarvest(Vector3 targetBlock) {
		for(int i = 0;i < 10;i++)
		{
			if(harvestList[i] != targetBlock)
			{
				harvestList[i]=targetBlock;
						return true;
			}
		}
	return false;
	
}
	private boolean onHarvestList(Vector3 targetBlock) {
		for(int i = 0;i < 10;i++)
		{
			if(harvestList[i] == targetBlock)
			{
				return true;
			}
		}
	return false;
	}
	public boolean ClearFromList(Vector3 targetBlock) {
		for(int i = 0;i < 10;i++)
		{
			if(harvestList[i] == targetBlock)
			{
				harvestList[i] = null;
				return true;
			}
				
		}
		return false;
		}
	
	

	public EntityRobot[] getControlList()
	{
		return BotList;
		
	}
	public boolean addBot(EntityRobot bot)
	{
		for(int i = 0; i < 4; i++)
		{
			if(BotList[i] == bot)
			{
				return true;
			}
		}
		for(int i = 0; i < 4; i++)
		{
			if(BotList[i] == null)
			{
				ModLoader.getMinecraftInstance().thePlayer.addChatMessage("Bot Added");
				BotList[i] = bot;
				return true;
			}
		}
		return false;
	}
	/**will be used to update the bot list on restart or reload of the world. 
		This way entity ids that are not bots are not stored in the list. 
		Generally the bots themselves will send the controller there ids when they load 
		into the world. The controller will then tell the bot its linked to the controller.
		**/
	public void cleanList()
	{
		for(int i = 0;i<4;i++)
		{
			if(BotList[i] instanceof EntityRobot)
			{
				EntityRobot Bot = BotList[i];
				Vector3 thisLoc = new Vector3(this.xCoord,this.yCoord,this.zCoord);
				Vector3 botFrq = new Vector3(Bot.linkFrq[0],Bot.linkFrq[1],Bot.linkFrq[2]);
				if(!botFrq.isEqual(thisLoc))
				{
					BotList[i] = null;
				}
			}
			else
			{
				BotList[i] = null;
			}
		}
		
	}
	@Override
	public float electricityRequest() {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public boolean canReceiveFromSide(byte side) {
		// TODO Auto-generated method stub
		return true;
	}
	public boolean canUpdate()
    {
        return true;
    }
	 public void readFromNBT(NBTTagCompound par1NBTTagCompound)
	    {
	    	super.readFromNBT(par1NBTTagCompound);
	    	
	    }
	    /**
	     * Writes a tile entity to NBT.
	     */
	    public void writeToNBT(NBTTagCompound par1NBTTagCompound)
	    {
	    	super.writeToNBT(par1NBTTagCompound);
	    }
}
