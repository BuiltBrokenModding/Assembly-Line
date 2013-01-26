package assemblyline.common.machine;

import java.util.List;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.AxisAlignedBB;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.common.ISidedInventory;
import universalelectricity.core.vector.Vector3;
import universalelectricity.prefab.implement.IRedstoneReceptor;
import universalelectricity.prefab.implement.IRotatable;
import universalelectricity.prefab.multiblock.TileEntityMulti;
import universalelectricity.prefab.network.PacketManager;
import assemblyline.api.IManipulator;
import assemblyline.common.machine.imprinter.TileEntityFilterable;
import cpw.mods.fml.common.network.PacketDispatcher;

public class TileEntityManipulator extends TileEntityFilterable implements IRotatable, IRedstoneReceptor, IManipulator
{
	public boolean selfPulse = false;

	/**
	 * Is the manipulator wrenched to turn into output mode?
	 */
	private boolean isOutput = false;

	private boolean isRedstonePowered = false;

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
			ItemStack remainingStack = entity.func_92014_d().copy();

			if (this.getFilter() == null || this.isFiltering(remainingStack))
			{
				remainingStack = this.tryPlaceInPosition(remainingStack, outputUp, ForgeDirection.DOWN);

				if (remainingStack != null)
				{
					remainingStack = this.tryPlaceInPosition(remainingStack, outputDown, ForgeDirection.UP);
				}

				if (remainingStack != null)
				{
					remainingStack = this.tryPlaceInPosition(remainingStack, outputPosition, this.getDirection().getOpposite());
				}

				if (remainingStack != null && remainingStack.stackSize > 0)
				{
					this.throwItem(outputPosition, remainingStack);
				}

				entity.setDead();
			}
		}
	}

	/**
	 * Injects items
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

		ItemStack itemStack = this.tryGrabFromPosition(inputUp, ForgeDirection.DOWN);

		if (itemStack == null)
		{
			itemStack = this.tryGrabFromPosition(inputDown, ForgeDirection.UP);
		}

		if (itemStack == null)
		{
			itemStack = this.tryGrabFromPosition(inputPosition, this.getDirection().getOpposite());
		}

		if (itemStack != null)
		{
			if (itemStack.stackSize > 0)
			{
				this.throwItem(outputPosition, itemStack);
			}
		}
	}

	/*
	 * @Override public ArrayList getPacketData() { ArrayList list = super.getPacketData();
	 * list.add(this.isOutput); list.add(this.wattsReceived); return list; }
	 * 
	 * @Override public void handlePacketData(INetworkManager network, int packetType,
	 * Packet250CustomPayload packet, EntityPlayer player, ByteArrayDataInput dataStream) { if
	 * (worldObj.isRemote) { ByteArrayInputStream bis = new ByteArrayInputStream(packet.data);
	 * DataInputStream dis = new DataInputStream(bis); int id, x, y, z; try { id = dis.readInt(); x
	 * = dis.readInt(); y = dis.readInt(); z = dis.readInt(); NBTTagCompound tag =
	 * Packet.readNBTTagCompound(dis); readFromNBT(tag); this.wattsReceived = dis.readDouble();
	 * this.isOutput = dis.readBoolean(); } catch (IOException e) { e.printStackTrace(); } } }
	 */

	/**
	 * Throws the items from the manipulator into the world.
	 * 
	 * @param outputPosition
	 * @param items
	 */
	public void throwItem(Vector3 outputPosition, ItemStack items)
	{
		EntityItem entityItem = new EntityItem(worldObj, outputPosition.x + 0.5, outputPosition.y + 0.8, outputPosition.z + 0.5, items);
		entityItem.motionX = 0;
		entityItem.motionZ = 0;
		entityItem.motionY /= 5;
		entityItem.delayBeforeCanPickup = 20;
		worldObj.spawnEntityInWorld(entityItem);
	}

	/**
	 * Tries to place an itemStack in a specific position if it is an inventory.
	 * 
	 * @return The ItemStack remained after place attempt
	 */
	private ItemStack tryPlaceInPosition(ItemStack itemStack, Vector3 position, ForgeDirection direction)
	{
		TileEntity tileEntity = position.getTileEntity(this.worldObj);

		if (tileEntity != null && itemStack != null)
		{
			/**
			 * Try to put items into a chest.
			 */
			if (tileEntity instanceof TileEntityMulti)
			{
				Vector3 mainBlockPosition = ((TileEntityMulti) tileEntity).mainBlockPosition;

				if (mainBlockPosition != null)
				{
					if (!(mainBlockPosition.getTileEntity(this.worldObj) instanceof TileEntityMulti))
						return tryPlaceInPosition(itemStack, mainBlockPosition, direction);
				}
			}
			else if (tileEntity instanceof TileEntityChest)
			{
				TileEntityChest[] chests = { (TileEntityChest) tileEntity, null };

				/**
				 * Try to find a double chest.
				 */
				for (int i = 2; i < 6; i++)
				{
					ForgeDirection searchDirection = ForgeDirection.getOrientation(i);
					Vector3 searchPosition = position.clone();
					searchPosition.modifyPositionFromSide(searchDirection);

					if (searchPosition.getTileEntity(this.worldObj) != null)
					{
						if (searchPosition.getTileEntity(this.worldObj).getClass() == chests[0].getClass())
						{
							chests[1] = (TileEntityChest) searchPosition.getTileEntity(this.worldObj);
							break;
						}
					}
				}

				for (TileEntityChest chest : chests)
				{
					if (chest != null)
					{
						for (int i = 0; i < chest.getSizeInventory(); i++)
						{
							itemStack = this.addStackToInventory(i, chest, itemStack);
							if (itemStack == null)
								return null;
						}
					}
				}
			}
			else if (tileEntity instanceof ISidedInventory)
			{
				ISidedInventory inventory = (ISidedInventory) tileEntity;

				int startIndex = inventory.getStartInventorySide(direction);

				for (int i = startIndex; i < startIndex + inventory.getSizeInventorySide(direction); i++)
				{
					itemStack = this.addStackToInventory(i, inventory, itemStack);
					if (itemStack == null) { return null; }
				}
			}
			else if (tileEntity instanceof IInventory)
			{
				IInventory inventory = (IInventory) tileEntity;

				for (int i = 0; i < inventory.getSizeInventory(); i++)
				{
					itemStack = this.addStackToInventory(i, inventory, itemStack);
					if (itemStack == null) { return null; }
				}
			}
		}

		if (itemStack.stackSize <= 0) { return null; }

		return itemStack;
	}

	public ItemStack addStackToInventory(int slotIndex, IInventory inventory, ItemStack itemStack)
	{
		if (inventory.getSizeInventory() > slotIndex)
		{
			ItemStack stackInInventory = inventory.getStackInSlot(slotIndex);

			if (stackInInventory == null)
			{
				inventory.setInventorySlotContents(slotIndex, itemStack);
				if (inventory.getStackInSlot(slotIndex) == null) { return itemStack; }
				return null;
			}
			else if (stackInInventory.isItemEqual(itemStack) && stackInInventory.isStackable())
			{
				stackInInventory = stackInInventory.copy();
				int rejectedAmount = Math.max((stackInInventory.stackSize + itemStack.stackSize) - inventory.getInventoryStackLimit(), 0);
				stackInInventory.stackSize = Math.min(Math.max((stackInInventory.stackSize + itemStack.stackSize - rejectedAmount), 0), inventory.getInventoryStackLimit());
				itemStack.stackSize = rejectedAmount;
				inventory.setInventorySlotContents(slotIndex, stackInInventory);
			}
		}

		if (itemStack.stackSize <= 0) { return null; }

		return itemStack;
	}

	/**
	 * Tries to take a item from a inventory at a specific position.
	 * 
	 * @param position
	 * @return
	 */
	private ItemStack tryGrabFromPosition(Vector3 position, ForgeDirection direction)
	{
		ItemStack returnStack = null;
		TileEntity tileEntity = position.getTileEntity(this.worldObj);

		if (tileEntity != null)
		{
			/**
			 * Try to put items into a chest.
			 */
			if (tileEntity instanceof TileEntityMulti)
			{
				Vector3 mainBlockPosition = ((TileEntityMulti) tileEntity).mainBlockPosition;

				if (mainBlockPosition != null)
				{
					if (!(mainBlockPosition.getTileEntity(this.worldObj) instanceof TileEntityMulti))
						return tryGrabFromPosition(mainBlockPosition, direction);
				}
			}
			else if (tileEntity instanceof TileEntityChest)
			{
				TileEntityChest[] chests = { (TileEntityChest) tileEntity, null };

				/**
				 * Try to find a double chest.
				 */
				for (int i = 2; i < 6; i++)
				{
					ForgeDirection searchDirection = ForgeDirection.getOrientation(i);
					Vector3 searchPosition = position.clone();
					searchPosition.modifyPositionFromSide(searchDirection);

					if (searchPosition.getTileEntity(this.worldObj) != null)
					{
						if (searchPosition.getTileEntity(this.worldObj).getClass() == chests[0].getClass())
						{
							chests[1] = (TileEntityChest) searchPosition.getTileEntity(this.worldObj);
							break;
						}
					}
				}

				chestSearch:
				for (TileEntityChest chest : chests)
				{
					if (chest != null)
					{
						for (int i = 0; i < chest.getSizeInventory(); i++)
						{
							ItemStack itemStack = this.removeStackFromInventory(i, chest);

							if (itemStack != null)
							{
								returnStack = itemStack;
								break chestSearch;
							}
						}
					}
				}
			}
			else if (tileEntity instanceof ISidedInventory)
			{
				ISidedInventory inventory = (ISidedInventory) tileEntity;

				int startIndex = inventory.getStartInventorySide(direction);

				for (int i = startIndex; i < startIndex + inventory.getSizeInventorySide(direction); i++)
				{
					ItemStack itemStack = this.removeStackFromInventory(i, inventory);

					if (itemStack != null)
					{
						returnStack = itemStack;
						break;
					}
				}
			}
			else if (tileEntity instanceof IInventory)
			{
				IInventory inventory = (IInventory) tileEntity;

				for (int i = 0; i < inventory.getSizeInventory(); i++)
				{
					ItemStack itemStack = this.removeStackFromInventory(i, inventory);
					if (itemStack != null)
					{
						returnStack = itemStack;
						break;
					}
				}
			}
		}

		return returnStack;
	}

	public ItemStack removeStackFromInventory(int slotIndex, IInventory inventory)
	{
		if (inventory.getStackInSlot(slotIndex) != null)
		{
			ItemStack itemStack = inventory.getStackInSlot(slotIndex).copy();

			if (this.getFilter() == null || this.isFiltering(itemStack))
			{
				itemStack.stackSize = 1;
				inventory.decrStackSize(slotIndex, 1);
				return itemStack;
			}
		}

		return null;
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
}
