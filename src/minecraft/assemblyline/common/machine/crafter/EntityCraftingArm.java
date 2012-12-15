package assemblyline.common.machine.crafter;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import universalelectricity.core.vector.Vector3;

/**
 * Paradigm: The Crafting Arm is made of *TO BE DETERMINED* jointed segments. We do not save any of
 * these. They are automatically 'created' by the Renderer to connect the 'Claw' to the TileEntity
 * fittingly. That's right they do not actually exist. MAGIC.
 */
public class EntityCraftingArm extends Entity
{
	/**
	 * Used to ID the type of arm
	 */

	public static enum CraftingArmType
	{
		ARM, SOLDER, DRILL, BREAKER
	}

	/**
	 * Maximal extended length of the Crafting Arm, to abort grabbings if the target moves out of
	 * range.
	 */
	private static final float MAX_GRAB_DISTANCE = 3F;

	/**
	 * At which distance from the target Item entity should the claw 'grab' it
	 */
	private static final float GRAB_CONNECT_DISTANCE = 0.2F;

	/**
	 * type of arm this robotic arm currently is
	 */
	private CraftingArmType currentArmType;

	/**
	 * stack this arm is holding or null if not
	 */
	private ItemStack itemStackBeingHeld;

	/**
	 * TileEntity this arm is originating from
	 */
	private TileEntityCraftingArm tileEntityCraftingArm;

	/**
	 * position that the arms claw is at
	 */
	private Vector3 clawPos;

	/**
	 * The Item Entity the claw is moving to grab. Not necessarily immobile!
	 */
	private EntityItem itemEntityTarget;

	private boolean isWorking;

	public EntityCraftingArm(World world)
	{
		super(world);
		currentArmType = CraftingArmType.ARM;
		itemStackBeingHeld = null;
		tileEntityCraftingArm = null;
		clawPos = new Vector3();
		itemEntityTarget = null;
		isWorking = false;
	}

	@Override
	protected void entityInit()
	{
		dataWatcher.addObject(5, String.valueOf(clawPos.x));
		dataWatcher.addObject(6, String.valueOf(clawPos.y));
		dataWatcher.addObject(7, String.valueOf(clawPos.z));
	}

	@Override
	protected void readEntityFromNBT(NBTTagCompound nbt)
	{
		currentArmType = CraftingArmType.values()[nbt.getInteger("type")];

		clawPos.x = nbt.getDouble("clawX");
		clawPos.y = nbt.getDouble("clawY");
		clawPos.z = nbt.getDouble("clawZ");

		if (nbt.hasKey("itemStackBeingHeld"))
		{
			itemStackBeingHeld = ItemStack.loadItemStackFromNBT(nbt.getCompoundTag("itemStackBeingHeld"));
		}
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound nbt)
	{
		nbt.setInteger("type", currentArmType.ordinal());

		nbt.setDouble("clawX", clawPos.x);
		nbt.setDouble("clawY", clawPos.y);
		nbt.setDouble("clawZ", clawPos.z);

		if (itemStackBeingHeld != null)
		{
			NBTTagCompound itemNBT = new NBTTagCompound();
			itemNBT = itemStackBeingHeld.writeToNBT(itemNBT);
			nbt.setCompoundTag("itemStackBeingHeld", itemNBT);
		}
	}

	@Override
	public void onUpdate()
	{
		super.onUpdate();

		if (!worldObj.isRemote)
		{
			// server only computations here, eg claw movement
			updateClawMovement();
		}

		updateNetworkedClawPosition();
	}

	@Override
	public void setDead()
	{
		// TODO deal with this error case
		super.setDead();
	}

	@Override
	public void setFire(int fire) // fire proof.
	{
	}

	@Override
	public AxisAlignedBB getBoundingBox()
	{
		// TODO bounding box
		return null;
	}

	@Override
	public AxisAlignedBB getCollisionBox(Entity par1Entity)
	{
		// TODO bounding box
		return null;
	}

	@Override
	public void applyEntityCollision(Entity collidingEnt) // immovable
	{
	}

	/**
	 * Called when a player right clicks the crafting Arm
	 */
	@Override
	public boolean interact(EntityPlayer entPlayer)
	{
		// TODO let the player take whatever the arm is holding?
		return false;
	}

	/**
	 * Deals with the Claw moving to and hitting the target Item Entity, also aborts if that moves
	 * out of range.
	 */
	private void updateClawMovement()
	{
		if (itemEntityTarget != null)
		{
			float distance = getDistanceToEntity(itemEntityTarget);
			if (distance > MAX_GRAB_DISTANCE)
			{
				itemEntityTarget = null;
			}
			else
			{
				if (distance < GRAB_CONNECT_DISTANCE)
				{
					grabItem();
				}
				else
				{
					double diffX = posX - itemEntityTarget.posX;
					double diffY = posY - itemEntityTarget.posY;
					double diffZ = posZ - itemEntityTarget.posZ;
					// TODO decide on how fast the claw should move toward the target, and move it
				}
			}
		}
		else
		{
			// TODO claw should return to some sort of 'waiting' position
		}
	}

	/**
	 * On serverside, writes the current local claw position into the dataWatchers. On client,
	 * retrieves the current remote claw values. Yes DataWatchers lack a Double getter method.
	 */
	private void updateNetworkedClawPosition()
	{
		if (worldObj.isRemote)
		{
			clawPos.x = Double.valueOf(dataWatcher.getWatchableObjectString(5));
			clawPos.y = Double.valueOf(dataWatcher.getWatchableObjectString(6));
			clawPos.z = Double.valueOf(dataWatcher.getWatchableObjectString(7));
		}
		else
		{
			dataWatcher.updateObject(5, String.valueOf(clawPos.x));
			dataWatcher.updateObject(6, String.valueOf(clawPos.x));
			dataWatcher.updateObject(7, String.valueOf(clawPos.x));
		}
	}

	public ItemStack getCurrentlyHeldItem()
	{
		return itemStackBeingHeld;
	}

	/**
	 * Designate an Item Entity as target for the claw to go to and grab.
	 * 
	 * @param entItem Item Entity instance to grab
	 */
	private void setTargetEntityItem(EntityItem entItem)
	{
		itemEntityTarget = entItem;
	}

	/**
	 * Kills the targeted Item Entity, saves the ItemStack into the CraftingArm, creates FX at grab.
	 * Then resets the Item Entity to null.
	 */
	private void grabItem()
	{
		itemStackBeingHeld = itemEntityTarget.item;
		playGrabbingEffects(itemEntityTarget.posX, itemEntityTarget.posY, itemEntityTarget.posZ);
		itemEntityTarget.setDead();
		itemEntityTarget = null;
	}

	/**
	 * Displays particles and/or play's sounds to emphasize the claw having grabbed something
	 * 
	 * @param posX coordinate of grab
	 * @param posY coordinate of grab
	 * @param posZ coordinate of grab
	 */
	private void playGrabbingEffects(double posX, double posY, double posZ)
	{
		// TODO sound, particles, etc
	}
}
