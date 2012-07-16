package net.minecraft.src.eui.robotics;
import java.util.List;

import net.minecraft.src.*;
public class EntityShoeBot extends EntityRobot {

    EntityItem targetItem = null;
	public EntityShoeBot(World par1World) {
		super(par1World);
		this.moveSpeed = 0.23F;
       // this.tasks.addTask(1, new EntityAIWander(this, this.moveSpeed));
        this.texture = "/mobs/char.png";
	}
	public float getBlockPathWeight(int par1, int par2, int par3)
    {
        return 0.5F + this.worldObj.getLightBrightness(par1, par2, par3);
    }
	@Override
	public void onEntityUpdate()
    {
	    super.onEntityUpdate();
	    
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
			    if(targetItem.getDistanceSq(this.posX,this.posY,this.posZ) < 1.5)
			    {
				 targetItem.setDead();
				 targetItem = null;
			    }				 
		    }
		    else
		    {
		    	this.moveSpeed = 0.23F;
		    }
	    }
    
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
	@Override
	public int getMaxHealth() {
		// TODO Auto-generated method stub
		return 5;
	}

	public String getRenderedName() {
		// TODO Auto-generated method stub
		return "BlockEater";
	}

}
