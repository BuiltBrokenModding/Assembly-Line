package net.minecraft.src.eui.robotics;
import java.util.List;

import net.minecraft.src.*;
import net.minecraft.src.universalelectricity.Vector3;
public class EntityShoeBot extends EntityRobot {

    EntityItem targetItem = null;
	public EntityShoeBot(World par1World) {
		super(par1World);
		this.setSize(0.6F, 0.5F);
		this.moveSpeed = 0.23F;
        this.texture = "/mobs/char.png";
	}
	@Override
	public void botUpdate()
    {
		super.botUpdate();
		EntityPlayer person = Modloader.getMinecraftInstance().thePlayer;
		person.addChatMessage("CC");
		if(hasTask)
		{
			person.addChatMessage("resuming task");
			if(this.currentTask == this.getTaskType() && this.taskLocation != null)
			{
				boolean harDone = harvest(this.taskLocation);
				if(harDone)
				{
					clearTask();
				}
			}
			else
			{
				clearTask();
			}
			
		}
		else
			{
				//requestTask
			}
	    	 
    }
	
	
	@Override
	public int getMaxHealth() {
		// TODO Auto-generated method stub
		return 5;
	}

	public String getRenderedName() {
		// TODO Auto-generated method stub
		return "Harvester Bot";
	}
	public String getTaskType() {
		return "harvest";
		
	}
}
