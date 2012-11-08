package assemblyline.machines.crafter;

import net.minecraft.src.Entity;
import net.minecraft.src.EntityItem;
import net.minecraft.src.ItemStack;
import net.minecraft.src.NBTTagCompound;
import net.minecraft.src.World;
import universalelectricity.core.Vector3;

public class EntityCraftingArm extends Entity
{
	/**
	 * Used to ID the type of arm
	 */
	static enum armType
	{
		ARM, SOLDER, DRILL, BREAKER
	}

	/**
	 * type of arm this robotic arm currently is
	 */
	public armType arm = armType.ARM;
	/**
	 * stack this arm is holding if any
	 */
	public ItemStack stack = null;
	/**
	 * TileEntity this arm is working with
	 */
	public TileEntityCraftingArm blockArm = null;
	/**
	 * position that the arms claw should be at
	 */
	public Vector3 clawPos = new Vector3();

	public boolean isWorking = false;

	public EntityCraftingArm(World par1World)
	{
		super(par1World);
	}

	@Override
	protected void entityInit()
	{

	}

	@Override
	protected void readEntityFromNBT(NBTTagCompound nbt)
	{
		this.arm = armType.values()[nbt.getInteger("type")];

	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound nbt)
	{
		nbt.setInteger("type", arm.ordinal());
	}

	public boolean grabItem(EntityItem item)
	{
		if (this.stack == null)
		{
			// TODO set current stack to item as
			// soon as it reaches coords
		}
		return false;
	}
}
