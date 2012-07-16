package net.minecraft.src.eui.robotics;
import net.minecraft.src.*;

public class EntityRobot extends EntityCreature {
	protected int attackStrength = 0;
	public EntityRobot(World par1World) {
		super(par1World);
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
    }

 public void readEntityFromNBT(NBTTagCompound par1NBTTagCompound)
    {
        super.readEntityFromNBT(par1NBTTagCompound);
    }
	@Override
	public int getMaxHealth() {
		// TODO Auto-generated method stub
		return 0;
	}
}
