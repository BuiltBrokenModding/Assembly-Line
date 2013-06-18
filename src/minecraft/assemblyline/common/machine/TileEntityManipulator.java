package assemblyline.common.machine;

import java.util.List;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.AxisAlignedBB;
import net.minecraftforge.common.ForgeDirection;
import universalelectricity.core.vector.Vector3;
import universalelectricity.prefab.implement.IRedstoneReceptor;
import universalelectricity.prefab.implement.IRotatable;
import universalelectricity.prefab.multiblock.TileEntityMulti;
import universalelectricity.prefab.network.PacketManager;
import assemblyline.api.IManipulator;
import assemblyline.common.block.BlockCrate;
import assemblyline.common.block.TileEntityCrate;
import assemblyline.common.imprinter.ItemImprinter;
import assemblyline.common.imprinter.prefab.TileEntityFilterable;
import cpw.mods.fml.common.network.PacketDispatcher;

public class TileEntityManipulator extends TileEntityFilterable implements IRotatable, IRedstoneReceptor, IManipulator
{
	public boolean selfPulse = false;
	/* SET TO OUTPUT MODE */
	private boolean isOutput = false;

	private boolean isRedstonePowered = false;

	private InvExtractionHelper invExtractionHelper;

	/**
	 * Gets the class that managed extracting and placing items into inventories
	 */
	public InvExtractionHelper invHelper()
	{
		if (invExtractionHelper == null)
		{
			this.invExtractionHelper = new InvExtractionHelper(this.worldObj, new Vector3(this), this.getFilter() != null ? ItemImprinter.getFilters(getFilter()) : null, this.isInverted());
		}
		return invExtractionHelper;
	}

	public boolean isOutput()
	{
		return this.isOutput;
	}

	public void setOutput(boolean isOutput)
	{
		this.isOutput = isOutput;

		if (!this.worldObj.isRemote)
		{
			PacketDispatcher.sendPacketToAllPlayers(this.getDescriptionPacket());
		}
	}

	public void toggleOutput()
	{
		this.setOutput(!this.isOutput());
	}

	@Override
	protected void onUpdate()
	{
		if (!this.worldObj.isRemote)
		{
			if (this.ticks % 20 == 0)
			{
				PacketManager.sendPacketToClients(this.getDescriptionPacket(), this.worldObj, new Vector3(this), 20);
			}

			if (!this.isDisabled() && this.isRunning())
			{
				if (!this.isOutput)
				{
					this.inject();
				}
				else
				{
					if (this.selfPulse && this.ticks % 10 == 0)
					{
						this.isRedstonePowered = true;
					}

					/**
					 * Finds the connected inventory and outputs the items upon a redstone pulse.
					 */
					if (this.isRedstonePowered)
					{
						this.eject();
					}
				}
			}
		}
	}

	/**
	 * Find items going into the manipulator and input them into an inventory behind this
	 * manipulator.
	 */
	@Override
	public void inject()
	{
		Vector3 inputPosition = new Vector3(this);

		Vector3 outputUp = new Vector3(this);
		outputUp.modifyPositionFromSide(ForgeDirection.UP);

		Vector3 outputDown = new Vector3(this);
		outputDown.modifyPositionFromSide(ForgeDirection.DOWN);

		Vector3 outputPosition = new Vector3(this);
		outputPosition.modifyPositionFromSide(this.getDirection().getOpposite());

		/**
		 * Prevents manipulators from spamming and duping items.
		 */
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

			/**
			 * Try top first, then bottom, then the sides to see if it is possible to insert the
			 * item into a inventory.
			 */
			ItemStack remainingStack = entity.getEntityItem().copy();

			if (this.getFilter() == null || this.isFiltering(remainingStack))
			{
				remainingStack = invHelper().tryPlaceInPosition(remainingStack, outputUp, ForgeDirection.DOWN);

				if (remainingStack != null)
				{
					remainingStack = invHelper().tryPlaceInPosition(remainingStack, outputDown, ForgeDirection.UP);
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

	/**
	 * Inject items
	 */
	@Override
	public void eject()
	{
		this.onPowerOff();

		Vector3 inputUp = new Vector3(this);
		inputUp.modifyPositionFromSide(ForgeDirection.UP);

		Vector3 inputDown = new Vector3(this);
		inputDown.modifyPositionFromSide(ForgeDirection.DOWN);

		Vector3 inputPosition = new Vector3(this);
		inputPosition.modifyPositionFromSide(this.getDirection().getOpposite());

		Vector3 outputPosition = new Vector3(this);
		outputPosition.modifyPositionFromSide(this.getDirection());

		ItemStack itemStack = invHelper().tryGrabFromPosition(inputUp, ForgeDirection.DOWN,1);

		if (itemStack == null)
		{
			itemStack = invHelper().tryGrabFromPosition(inputDown, ForgeDirection.UP,1);
		}

		if (itemStack == null)
		{
			itemStack = invHelper().tryGrabFromPosition(inputPosition, this.getDirection().getOpposite(),1);
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
		this.selfPulse = nbt.getBoolean("selfpulse");
	}

	/**
	 * Writes a tile entity to NBT.
	 */
	@Override
	public void writeToNBT(NBTTagCompound nbt)
	{
		super.writeToNBT(nbt);
		nbt.setBoolean("isOutput", this.isOutput);
		nbt.setBoolean("selfpulse", this.selfPulse);
	}

	@Override
	public void onPowerOn()
	{
		this.isRedstonePowered = true;
	}

	@Override
	public void onPowerOff()
	{
		this.isRedstonePowered = false;
	}

	@Override
	public boolean canConnect(ForgeDirection dir)
	{
		return dir != this.getDirection();
	}
}
