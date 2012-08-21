package net.minecraft.src.eui.robotics;
import java.util.List;

import net.minecraft.src.*;
import net.minecraft.src.universalelectricity.Vector3;

public class EntityRobot extends EntityCreature {
	public int attackStrength = 0;
	public int battery = 0; //how long this but will run in, 2 = 20ticks
	public int disabled = 0; //disabled timer
	public int updateCount = 0; //used to control how fast the bot calls its updates
	public boolean isDisabled = false;//is not updating
    public boolean hasTask = false; //has a working task
	public Vector3 taskLocation = null; //task location usual a block or item
	int[] taskLoc = {0,0,0};
	public String currentTask = "none"; //type of task this bot can perform
	public boolean isLinked = false; // is linked to a controller
	public int[] linkFrq = {0,0,0}; //not so much a link number but controller location
	public EntityRobot(World par1World) {
		super(par1World);
	}
	@Override
	public void onEntityUpdate()
    {
		super.onEntityUpdate();
		if(!worldObj.isRemote)
		{
		updateCount++;
		//used for emping of bot or empty battery
		isDisabled = true;
		if(disabled <=0)
		{
			
			isDisabled = false;
		}
		if(!isDisabled && updateCount >= 10)
		{
			updateCount =0;
			battery += 2; //TODO remove after testing
			--battery;
			botUpdate();
		}
		}
		
    }
	//update function to control how fast the bot updates to reduce load
	public void botUpdate()
	{
		if(taskLocation != null)
		{
		taskLoc[0] = taskLocation.intX();
		taskLoc[1] = taskLocation.intY();
		taskLoc[2] = taskLocation.intZ();
		}
		//links the bot to a controler if it is not already linked
		if(!isLinked)
		{
			this.setDead();
			/**
			ModLoader.getMinecraftInstance().thePlayer.addChatMessage("linking To Controler");
			TileEntityComp targetComp = this.getEmptyControler(this.posX,this.posY,this.posZ, 50);
			if(targetComp != null){
			boolean added = targetComp.addBot(this);
			if(added)
			{
				isLinked = true;
				linkFrq[0] = targetComp.xCoord;
				linkFrq[1] = targetComp.yCoord;
				linkFrq[2] = targetComp.zCoord;
				ModLoader.getMinecraftInstance().thePlayer.addChatMessage("linked To Controler");
			}
			}
			**/
		}
		else
		{
			//TODO add logic too tell controler this bot is alive, current task, hp, location,etc....
			TileEntity comp = worldObj.getBlockTileEntity(this.linkFrq[0], this.linkFrq[1], this.linkFrq[2]);
			if(comp instanceof TileEntityComp)
			{
			Boolean linked = ((TileEntityComp) comp).addBot(this);
			if(!linked)
			{
				this.isLinked = false;	
			}
			}
			else
			{
				this.isLinked = true;
			}
		}
	}
	 /**
	  * used to find the closest controller
	  * @param par1 - x
	  * @param par3 - y
	  * @param par5 - z
	  * @param par7 - range in blocks
	  * @return the nearest controller
	  */
 	 public TileEntityComp getClosestControler(double par1, double par3, double par5, double par7)
	    {
		 double var9 = -1.0D;
		    TileEntityComp var11 = null;
		    List itemList = worldObj.getEntitiesWithinAABB(TileEntityComp.class, this.boundingBox.expand(par7, 4.0D, par7));
		    for (int var12 = 0; var12 < itemList.size(); ++var12)
		    {
		    	TileEntityComp var13 = (TileEntityComp)itemList.get(var12);
		        double var14 = var13.getDistanceFrom(par1, par3, par5);

		        if ((par7 < 0.0D || var14 < par7 * par7) && (var9 == -1.0D || var14 < var9))
		        {
		            var9 = var14;
		            var11 = var13;
		        }
		    }

		    return var11;
	    }
	 /**
	  * used to find the closest controller with an empty slot mainly used to find and add a robot to the controller
	  * @param par1 - x
	  * @param par3 - y
	  * @param par5 - z
	  * @param par7 - range in blocks
	  * @return the nearest controller block with an empty control slot
	  */
	 public TileEntityComp getEmptyControler(double par1, double par3, double par5, double par7)
	    {
		 double var9 = -1.0D;
		    TileEntityComp var11 = null;
		    List itemList = worldObj.getEntitiesWithinAABB(TileEntityComp.class, this.boundingBox.expand(par7, 4.0D, par7));
		    for (int var12 = 0; var12 < itemList.size(); ++var12)
		    {
		    	TileEntityComp var13 = (TileEntityComp)itemList.get(var12);
		        double var14 = var13.getDistanceFrom(par1, par3, par5);

		        if ((par7 < 0.0D || var14 < par7 * par7) && (var9 == -1.0D || var14 < var9))
		        {
		        	for(int c = 0; c < 4; c++)
		        	{
		        	if(var13.BotList[c]==null)
		        	{
		        		 var11 = var13;
		        	}
		        	}
		            var9 = var14;		           
		        }
		    }

		    return var11;
	    }
	public boolean isAIEnabled()
	 {
	    return false;
	 }
	 protected boolean canDespawn()
	    {
	        return false;
	    }
	@Override
	public void writeEntityToNBT(NBTTagCompound par1NBTTagCompound)
    {
        super.writeEntityToNBT(par1NBTTagCompound);
        par1NBTTagCompound.setInteger("batt", this.battery);
        par1NBTTagCompound.setIntArray("linkFrq", this.linkFrq);
        par1NBTTagCompound.setBoolean("linked", isLinked);
        par1NBTTagCompound.setBoolean("hasTask", hasTask);        
        par1NBTTagCompound.setIntArray("taskLoc", taskLoc);
    }

 public void readEntityFromNBT(NBTTagCompound par1NBTTagCompound)
    {
        super.readEntityFromNBT(par1NBTTagCompound);
        this.battery = par1NBTTagCompound.getInteger("batt");
        this.linkFrq = par1NBTTagCompound.getIntArray("linkFrq");
        this.isLinked = par1NBTTagCompound.getBoolean("linked");
        this.hasTask = par1NBTTagCompound.getBoolean("hasTask");
        taskLoc = par1NBTTagCompound.getIntArray("taskLoc");
        this.taskLocation = new Vector3(taskLoc[0],taskLoc[1],taskLoc[2]);
    }
	@Override
	public int getMaxHealth() {
		return 1;
	}
	public String getTaskType() {
		return "n/a";
		
	}
	public boolean isIdle() {
		// TODO Auto-generated method stub
		return !hasTask;
	}
	public boolean setWorkTask(Vector3 taskLoc, String task) {
		int x = taskLoc.intX();
		int y = taskLoc.intY();
		int z = taskLoc.intZ();
		Vector3 thisBot = new Vector3(this.posX,this.posY,this.posZ);
		PathEntity PathToItem = this.worldObj.getEntityPathToXYZ(this, x, y, z, 30, true, false, false, true);
	    if(PathToItem != null){
		    if(task.toLowerCase() == this.getTaskType().toLowerCase())
		    {
		    	ModLoader.getMinecraftInstance().thePlayer.addChatMessage("TaskSet");
		    	this.taskLocation = taskLoc;
		    	this.currentTask  = task;
		    	hasTask = true;
		    	return true;
		    }
	    }
	   
		return false;
	}
public boolean harvest(Vector3 BlockLoc)
{
	int x = BlockLoc.intX();
	int y = BlockLoc.intY();
	int z = BlockLoc.intZ();
	Vector3 thisBot = new Vector3(this.posX,this.posY,this.posZ);
	PathEntity PathToItem = this.worldObj.getEntityPathToXYZ(this, x, y, z, 30, true, false, false, true);
    if(PathToItem != null){
	    this.setPathToEntity(PathToItem);
	    this.moveSpeed = 1.0F;
	    if(thisBot.distanceTo(BlockLoc) < 4)
	    {
	    	int blockTargetc = worldObj.getBlockId(x, y, z);
        	boolean harvested = worldObj.setBlock(x, y, z, 0);
        	if(blockTargetc ==0)
        	{
        		harvested = true;
        	}
        	ModLoader.getMinecraftInstance().thePlayer.addChatMessage("Harvesting Block "+blockTargetc);
        	if(blockTargetc > 0 && harvested)
        	{
        	EntityItem dropedItem = new EntityItem(worldObj, x, y - 0.3D, z, new ItemStack(blockTargetc,1,1));
        	worldObj.spawnEntityInWorld(dropedItem);
        	if(getController() != null)
        	{
        	getController().ClearFromList(BlockLoc);
        	}
        	}
        	return harvested;
	    }				 
    }
	return false;
}
public TileEntityComp getController()
{
	TileEntity comp = worldObj.getBlockTileEntity(this.linkFrq[0], this.linkFrq[1], this.linkFrq[2]);
	if(comp instanceof TileEntityComp)
	{
		return (TileEntityComp) comp;
	}
	return null;
}
public EntityItem findClosestItem(double par1, double par3, double par5, double par7)
{
    double var9 = -1.0D;
    EntityItem var11 = null;
    List itemList = worldObj.getEntitiesWithinAABB(EntityItem.class, this.boundingBox.expand(par7, 4.0D, par7));
    for (int var12 = 0; var12 < itemList.size(); ++var12)
    {
        EntityItem var13 = (EntityItem)itemList.get(var12);
        double var14 = var13.getDistanceSq(par1, par3, par5);

        if ((par7 < 0.0D || var14 < par7 * par7) && (var9 == -1.0D || var14 < var9))
        {
            var9 = var14;
            var11 = var13;
        }
    }

    return var11;
}
public boolean collectItem(EntityItem targetItem)
{
	 if(targetItem == null)
	    {
		    targetItem = findClosestItem(this.posX, this.posY, this.posZ,30);
	    }
	    else
	    {
	    	if(!targetItem.isEntityAlive())
		    {
		    	targetItem = null;		    	
		    }	
	    }
	    if(targetItem != null)
	    {
	    	
	    	PathEntity PathToItem = this.worldObj.getPathEntityToEntity(this, targetItem, 30, true, false, false, true);
		    if(hasPath()){
			    this.setPathToEntity(PathToItem);
			    this.moveSpeed = 1.0F;
			    if(targetItem.getDistanceSq(this.posX,this.posY,this.posZ) < 2)
			    {
				 targetItem.setDead();
				 //TODO add item to inventory
				 targetItem = null;
			    }				 
		    }
		    else
		    {
		    	this.moveSpeed = 0.23F;
		    }
	    }
		return false;
}
public void clearTask() {
	this.taskLocation = null;
	this.currentTask = "none";
	this.hasTask = false;
	
}
}
