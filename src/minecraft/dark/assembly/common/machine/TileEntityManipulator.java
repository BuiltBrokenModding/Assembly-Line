package dark.assembly.common.machine;

import java.util.List;

import dark.assembly.api.IManipulator;
import dark.assembly.common.imprinter.ItemImprinter;
import dark.assembly.common.imprinter.prefab.TileEntityFilterable;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraftforge.common.ForgeDirection;
import universalelectricity.core.vector.Vector3;
import universalelectricity.prefab.tile.IRotatable;

public class TileEntityManipulator extends TileEntityFilterable implements IRotatable, IManipulator
{

	/** True to auto output items with a redstone pulse */
	private boolean selfPulse = false;
	/** True if outputting items */
	private boolean isOutput = false;
	/** True if is currently powered by redstone */
	private boolean isRedstonePowered = false;
	/** The class that interacts with inventories for this machine */
	private InvInteractionHelper invExtractionHelper;

	@Override
	public void onUpdate()
	{
		if (!this.worldObj.isRemote)
		{
			if (!this.isDisabled() && this.isRunning())
			{
				if (!this.isOutput)
				{
					this.inject();
				}
				else
				{
					this.isRedstonePowered = this.worldObj.isBlockIndirectlyGettingPowered(xCoord, yCoord, zCoord);
					if (this.isSelfPulse() && this.ticks % 10 == 0)
					{
						this.isRedstonePowered = true;
					}

					/** Finds the connected inventory and outputs the items upon a redstone pulse. */
					if (this.isRedstonePowered)
					{
						this.eject();
					}
				}
			}
		}
	}

	/** Find items going into the manipulator and input them into an inventory behind this
	 * manipulator. */
	@Override
	public void inject()
	{
		Vector3 inputPosition = new Vector3(this);
		/** output location up */
		Vector3 outputUp = new Vector3(this);
		outputUp.modifyPositionFromSide(ForgeDirection.UP);
		/** output location down */
		Vector3 outputDown = new Vector3(this);
		outputDown.modifyPositionFromSide(ForgeDirection.DOWN);
		/** output location facing */
		Vector3 outputPosition = new Vector3(this);
		outputPosition.modifyPositionFromSide(this.getDirection().getOpposite());

		/** Prevents manipulators from spamming and duping items. */
		if (outputPosition.getTileEntity(this.worldObj) instanceof TileEntityManipulator)
		{
			if (((TileEntityManipulator) outputPosition.getTileEntity(this.worldObj)).getDirection() == this.getDirection().getOpposite())
			{
				return;
			}
		}

		AxisAlignedBB bounds = AxisAlignedBB.getBoundingBox(inputPosition.x, inputPosition.y, inputPosition.z, inputPosition.x + 1, inputPosition.y + 1, inputPosition.z + 1);
		List<EntityItem> itemsInBound = this.worldObj.getEntitiesWithinAABB(EntityItem.class, bounds);

		for (EntityItem entity : itemsInBound)
		{
			if (entity.isDead)
				continue;

			/** Try top first, then bottom, then the sides to see if it is possible to insert the
			 * item into a inventory. */
			ItemStack remainingStack = entity.getEntityItem().copy();

			if (this.getFilter() == null || this.isFiltering(remainingStack))
			{
				remainingStack = invHelper().tryPlaceInPosition(remainingStack, outputUp, ForgeDirection.UP);

				if (remainingStack != null)
				{
					remainingStack = invHelper().tryPlaceInPosition(remainingStack, outputDown, ForgeDirection.DOWN);
				}

				if (remainingStack != null)
				{
					remainingStack = invHelper().tryPlaceInPosition(remainingStack, outputPosition, this.getDirection().getOpposite());
				}

				if (remainingStack != null && remainingStack.stackSize > 0)
				{
					invHelper().throwItem(outputPosition, remainingStack);
				}

				entity.setDead();
			}
		}
	}

	/** Inject items */
	@Override
	public void eject()
	{
		this.isRedstonePowered = false;
		/** input location up */
		Vector3 inputUp = new Vector3(this);
		inputUp.modifyPositionFromSide(ForgeDirection.UP);
		/** input location down */
		Vector3 inputDown = new Vector3(this);
		inputDown.modifyPositionFromSide(ForgeDirection.DOWN);
		/** input location facing */
		Vector3 inputPosition = new Vector3(this);
		inputPosition.modifyPositionFromSide(this.getDirection().getOpposite());
		/** output location facing */
		Vector3 outputPosition = new Vector3(this);
		outputPosition.modifyPositionFromSide(this.getDirection());

		ItemStack itemStack = invHelper().tryGrabFromPosition(inputUp, ForgeDirection.UP, 1);

		if (itemStack == null)
		{
			itemStack = invHelper().tryGrabFromPosition(inputDown, ForgeDirection.DOWN, 1);
		}

		if (itemStack == null)
		{
			itemStack = invHelper().tryGrabFromPosition(inputPosition, this.getDirection().getOpposite(), 1);
		}

		if (itemStack != null)
		{
			if (itemStack.stackSize > 0)
			{
				invHelper().throwItem(outputPosition, itemStack);
			}
		}
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt)
	{
		super.readFromNBT(nbt);
		this.isOutput = nbt.getBoolean("isOutput");
		this.setSelfPulse(nbt.getBoolean("selfpulse"));
	}

	/** Writes a tile entity to NBT. */
	@Override
	public void writeToNBT(NBTTagCompound nbt)
	{
		super.writeToNBT(nbt);
		nbt.setBoolean("isOutput", this.isOutput);
		nbt.setBoolean("selfpulse", this.isSelfPulse());
	}

	@Override
	public boolean canConnect(ForgeDirection dir)
	{
		return dir != this.getDirection();
	}

	public boolean isSelfPulse()
	{
		return selfPulse;
	}

	public void setSelfPulse(boolean selfPulse)
	{
		this.selfPulse = selfPulse;
	}

	/** Gets the class that managed extracting and placing items into inventories */
	public InvInteractionHelper invHelper()
	{
		if (invExtractionHelper == null || invExtractionHelper.world != this.worldObj)
		{
			this.invExtractionHelper = new InvInteractionHelper(this.worldObj, new Vector3(this), this.getFilter() != null ? ItemImprinter.getFilters(getFilter()) : null, this.isInverted());
		}
		return invExtractionHelper;
	}

	@Override
	public void setFilter(ItemStack filter)
	{
		super.setFilter(filter);
		/* Reset inv Helper's filters */
		this.invHelper().setFilter(this.getFilter() != null ? ItemImprinter.getFilters(this.getFilter()) : null, this.isInverted());
	}

	/** Is this manipulator set to output items */
	public boolean isOutput()
	{
		return this.isOutput;
	}

	/** True to output items */
	public void setOutput(boolean isOutput)
	{
		this.isOutput = isOutput;

		if (!this.worldObj.isRemote)
		{
			this.worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
		}
	}

	/** Inverts the current output state */
	public void toggleOutput()
	{
		this.setOutput(!this.isOutput());
	}
}
