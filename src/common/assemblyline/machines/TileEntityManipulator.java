package assemblyline.machines;

import java.util.List;

import net.minecraft.src.AxisAlignedBB;
import net.minecraft.src.EntityItem;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.IInventory;
import net.minecraft.src.INetworkManager;
import net.minecraft.src.ItemStack;
import net.minecraft.src.Packet250CustomPayload;
import net.minecraft.src.TileEntity;
import net.minecraft.src.TileEntityChest;
import net.minecraftforge.common.ForgeDirection;
import universalelectricity.core.Vector3;
import universalelectricity.prefab.TileEntityElectricityReceiver;
import universalelectricity.prefab.network.IPacketReceiver;

import assemblyline.machines.BlockInteraction.MachineType;

import com.google.common.io.ByteArrayDataInput;

public class TileEntityManipulator extends TileEntityElectricityReceiver
{
	/**
	 * Joules required to run this thing.
	 */
	public static final int JOULES_REQUIRED = 15;

	/**
	 * The amount of watts received.
	 */
	public double wattsReceived = 0;

	@Override
	public double wattRequest()
	{
		return JOULES_REQUIRED;
	}

	@Override
	public void updateEntity()
	{
		super.updateEntity();

		if (this.worldObj.isRemote)
		{
			if (!this.isDisabled())
			{
				if (!this.isOutput())
				{
					/**
					 * Find items going into the
					 * manipulator and input them
					 * into an inventory behind
					 * this manipulator.
					 */
					Vector3 inputPosition = Vector3.get(this);
					inputPosition.modifyPositionFromSide(this.getBeltDirection());

					Vector3 outputPosition = Vector3.get(this);
					outputPosition.modifyPositionFromSide(this.getBeltDirection().getOpposite());

					AxisAlignedBB bounds = AxisAlignedBB.getBoundingBox(inputPosition.x, inputPosition.y, inputPosition.z, inputPosition.x + 1, inputPosition.y + 1, inputPosition.z + 1);
					List<EntityItem> itemsInBound = this.worldObj.getEntitiesWithinAABB(EntityItem.class, bounds);

					for (EntityItem entity : itemsInBound)
					{
						ItemStack remainingStack = this.tryPlaceInPosition(entity.item.copy(), outputPosition);

						if (remainingStack != null)
						{
							if (remainingStack.stackSize > 0)
							{
								EntityItem var23 = new EntityItem(worldObj, entity.posX, entity.posY + 0.1D, entity.posZ, remainingStack);
								worldObj.spawnEntityInWorld(var23);
							}
						}

						entity.setDead();
					}
				}
			}
		}
	}

	/**
	 * Tries to place an itemStack in a specific
	 * position if it is an inventory.
	 * 
	 * @return The ItemStack remained after place
	 *         attempt
	 */
	private ItemStack tryPlaceInPosition(ItemStack itemStack, Vector3 position)
	{
		TileEntity tileEntity = position.getTileEntity(this.worldObj);

		if (tileEntity != null)
		{
			/**
			 * Try to put items into a chest.
			 */
			if (tileEntity instanceof TileEntityChest)
			{
				TileEntityChest[] chests =
				{ (TileEntityChest) tileEntity, null };

				/**
				 * Try to find a double chest.
				 */
				for (int i = 2; i < 6; i++)
				{
					ForgeDirection searchDirection = ForgeDirection.getOrientation(i);
					Vector3 searchPosition = position.clone();
					searchPosition.modifyPositionFromSide(searchDirection);

					if (searchPosition.getTileEntity(this.worldObj) instanceof TileEntityChest)
					{
						chests[1] = (TileEntityChest) searchPosition.getTileEntity(this.worldObj);
						break;
					}
				}

				for (TileEntityChest chest : chests)
				{
					if (itemStack != null && itemStack.stackSize > 0)
					{
						for (int i = 0; i < chest.getSizeInventory(); i++)
						{
							ItemStack stackInChest = chest.getStackInSlot(i);

							if (stackInChest == null)
							{
								chest.setInventorySlotContents(i, itemStack);
								return null;
							}
							else if (stackInChest.getItem().equals(itemStack.getItem()) && stackInChest.getItemDamage() == itemStack.getItemDamage())
							{
								int rejectedAmount = Math.max((stackInChest.stackSize + itemStack.stackSize) - stackInChest.getItem().getItemStackLimit(), 0);
								stackInChest.stackSize = Math.min(Math.max((stackInChest.stackSize + itemStack.stackSize - rejectedAmount), 0), stackInChest.getItem().getItemStackLimit());
								itemStack.stackSize = rejectedAmount;
								chest.setInventorySlotContents(i, stackInChest);

								if (itemStack.stackSize <= 0) { return null; }

							}

						}
					}
				}
			}
		}

		if (itemStack.stackSize <= 0) { return null; }

		return itemStack;
	}

	/**
	 * If the manipulator is powered, it will
	 * output items instead of input.
	 */
	public boolean isOutput()
	{
		return this.worldObj.isBlockIndirectlyGettingPowered(this.xCoord, this.yCoord, this.zCoord) || this.worldObj.isBlockGettingPowered(this.xCoord, this.yCoord, this.zCoord);
	}

	public ForgeDirection getBeltDirection()
	{
		return ForgeDirection.getOrientation(MachineType.getDirection(this.worldObj.getBlockMetadata(this.xCoord, this.yCoord, this.zCoord)) + 2);
	}

	@Override
	public boolean canReceiveFromSide(ForgeDirection side)
	{
		return true;
	}

	@Override
	public void onReceive(TileEntity sender, double amps, double voltage, ForgeDirection side)
	{
		this.wattsReceived += (amps * voltage);
	}
}
