package assemblyline.machines.crafter;

import net.minecraft.src.AxisAlignedBB;
import net.minecraft.src.Entity;
import net.minecraft.src.TileEntity;
import net.minecraft.src.World;
import assemblyline.ai.Task;

/**
 * Used by arms to search for entities in a region
 * 
 * @author Calclavia
 */
public class TaskArmSearch extends Task
{
	private TileEntityCraftingArm tileEntity;

	/**
	 * The item to be collected.
	 */
	private Class<? extends Entity> entityToInclude;

	private float searchSpeed;

	private World worldObj;

	private double radius;

	private Entity foundEntity;

	public TaskArmSearch(Class<? extends Entity> entityToInclude, double radius, float searchSpeed)
	{
		this.entityToInclude = entityToInclude;
		this.radius = radius;
		this.searchSpeed = searchSpeed;
	}
	
	@Override
	public void onTaskStart()
	{
		this.foundEntity = (Entity) this.worldObj.getEntitiesWithinAABB(this.entityToInclude, AxisAlignedBB.getBoundingBox(this.tileEntity.xCoord - this.radius, this.tileEntity.yCoord - this.radius, this.tileEntity.zCoord - this.radius, this.tileEntity.xCoord + this.radius, this.tileEntity.yCoord + this.radius, this.tileEntity.zCoord + this.radius)).get(0);
	}

	@Override
	protected boolean doTask()
	{
		super.doTask();

		if (this.entityToInclude == null || this.foundEntity == null) { return false; }

		/**
		 * Move the robotic arm around and emulate
		 * an item search. Then initiate a collect task.
		 */
		
		
		return true;
	}

	@Override
	public void setTileEntity(TileEntity tileEntity)
	{
		this.tileEntity = (TileEntityCraftingArm) tileEntity;
		this.worldObj = this.tileEntity.worldObj;
	}
}
