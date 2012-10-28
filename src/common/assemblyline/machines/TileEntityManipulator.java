package assemblyline.machines;

import java.util.List;

import com.google.common.io.ByteArrayDataInput;

import net.minecraft.src.AxisAlignedBB;
import net.minecraft.src.EntityItem;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.IInventory;
import net.minecraft.src.INetworkManager;
import net.minecraft.src.ItemStack;
import net.minecraft.src.NBTTagCompound;
import net.minecraft.src.Packet;
import net.minecraft.src.Packet250CustomPayload;
import net.minecraft.src.TileEntity;
import net.minecraft.src.TileEntityChest;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.common.ISidedInventory;
import universalelectricity.core.Vector3;
import universalelectricity.electricity.ElectricInfo;
import universalelectricity.implement.IRedstoneReceptor;
import universalelectricity.prefab.TileEntityElectricityReceiver;
import universalelectricity.prefab.network.IPacketReceiver;
import universalelectricity.prefab.network.PacketManager;
import assemblyline.AssemblyLine;
import assemblyline.machines.BlockMulti.MachineType;

public class TileEntityManipulator extends TileEntityElectricityReceiver implements IRedstoneReceptor, IPacketReceiver
{
	/**
	 * Joules required to run this thing.
	 */
	public static final int JOULES_REQUIRED = 15;

	/**
	 * The amount of watts received.
	 */
	public double wattsReceived = 0;

	/**
	 * Is the manipulator wrenched to turn into
	 * output mode?
	 */
	public boolean isOutput = false;

	private boolean isPowered = false;

	@Override
	public double wattRequest()
	{
		return JOULES_REQUIRED;
	}

	@Override
	public void updateEntity()
	{
		super.updateEntity();

		if (!this.worldObj.isRemote)
		{
			if (!this.isDisabled() && this.wattsReceived >= this.JOULES_REQUIRED)
			{
				if (!this.isOutput)
				{
					/**
					 * Find items going into the
					 * manipulator and input them
					 * into an inventory behind
					 * this manipulator.
					 */
					Vector3 inputPosition = Vector3.get(this);

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
								EntityItem entityItem = new EntityItem(worldObj, outputPosition.x + 0.5, outputPosition.y + 0.8, outputPosition.z + 0.5, remainingStack);
								entityItem.motionX = 0;
								entityItem.motionZ = 0;
								worldObj.spawnEntityInWorld(entityItem);
							}
						}

						entity.setDead();
					}
				}
				else
				{
					/**
					 * Finds the connected inventory and outputs the items upon a redstone pulse.
					 */
					if(this.isPowered)
					{
						this.onPowerOff();
						
						Vector3 inputPosition = Vector3.get(this);
						inputPosition.modifyPositionFromSide(this.getBeltDirection().getOpposite());

						Vector3 outputPosition = Vector3.get(this);
						outputPosition.modifyPositionFromSide(this.getBeltDirection());
						
						ItemStack itemStack = this.tryGrabFromPosition(inputPosition);
						
						if(itemStack != null)
						{
							if(itemStack.stackSize > 0)
							{
								EntityItem entityItem = new EntityItem(worldObj, outputPosition.x + 0.5, outputPosition.y + 0.8, outputPosition.z + 0.5, itemStack);
								entityItem.motionX = 0;
								entityItem.motionZ = 0;
								entityItem.motionY /= 4;
								worldObj.spawnEntityInWorld(entityItem);
							}
						}
					}
				}
				
				this.wattsReceived = 0;
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

		if (tileEntity != null && itemStack != null)
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
					for (int i = 0; i < chest.getSizeInventory(); i++)
					{
						itemStack = this.addStackToInventory(i, chest, itemStack);
						if(itemStack == null) return null;
					}
				}
			}
			else if (tileEntity instanceof ISidedInventory)
			{
				ISidedInventory inventory = (ISidedInventory) tileEntity;

				int startIndex = inventory.getStartInventorySide(this.getBeltDirection());

				for (int i = startIndex; i < inventory.getSizeInventorySide(this.getBeltDirection()); i++)
				{
					itemStack = this.addStackToInventory(startIndex, inventory, itemStack);
					if(itemStack == null) return null;
				}
			}
			else if (tileEntity instanceof IInventory)
			{
				IInventory inventory = (IInventory) tileEntity;

				for (int i = 0; i < inventory.getSizeInventory(); i++)
				{
					itemStack = this.addStackToInventory(i, inventory, itemStack);
					if(itemStack == null) return null;
				}
			}
		}

		if (itemStack.stackSize <= 0) { return null; }

		return itemStack;
	}

	public ItemStack addStackToInventory(int slotIndex, IInventory inventory, ItemStack itemStack)
	{
		ItemStack stackInChest = inventory.getStackInSlot(slotIndex);

		if (stackInChest == null)
		{
			inventory.setInventorySlotContents(slotIndex, itemStack);
			return null;
		}
		else if (stackInChest.getItem().equals(itemStack.getItem()) && stackInChest.getItemDamage() == itemStack.getItemDamage())
		{
			int rejectedAmount = Math.max((stackInChest.stackSize + itemStack.stackSize) - stackInChest.getItem().getItemStackLimit(), 0);
			stackInChest.stackSize = Math.min(Math.max((stackInChest.stackSize + itemStack.stackSize - rejectedAmount), 0), stackInChest.getItem().getItemStackLimit());
			itemStack.stackSize = rejectedAmount;
			inventory.setInventorySlotContents(slotIndex, stackInChest);

			if (itemStack.stackSize <= 0) { return null; }
		}
		
		return itemStack;
	}
	
	/**
	 * Tries to take a item from a inventory at a specific position.
	 * @param position
	 * @return
	 */
	private ItemStack tryGrabFromPosition(Vector3 position)
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

					if (searchPosition.getTileEntity(this.worldObj).getClass() == chests[0].getClass())
					{
						chests[1] = (TileEntityChest) searchPosition.getTileEntity(this.worldObj);
						break;
					}
				}

				for (TileEntityChest chest : chests)
				{
					for (int i = 0; i < chest.getSizeInventory(); i++)
					{
						ItemStack itemStack = this.removeStackFromInventory(i, chest);
						if(itemStack != null) return itemStack;
					}
				}
			}
			else if (tileEntity instanceof ISidedInventory)
			{
				ISidedInventory inventory = (ISidedInventory) tileEntity;

				int startIndex = inventory.getStartInventorySide(this.getBeltDirection());

				for (int i = startIndex; i < inventory.getSizeInventorySide(this.getBeltDirection()); i++)
				{
					ItemStack itemStack = this.removeStackFromInventory(i, inventory);
					if(itemStack != null) return itemStack;
				}
			}
			else if (tileEntity instanceof IInventory)
			{
				IInventory inventory = (IInventory) tileEntity;

				for (int i = 0; i < inventory.getSizeInventory(); i++)
				{
					ItemStack itemStack = this.removeStackFromInventory(i, inventory);
					if(itemStack != null) return itemStack;
				}
			}
		}

		return null;
	}
	
	public ItemStack removeStackFromInventory(int slotIndex, IInventory inventory)
	{
		if(inventory.getStackInSlot(slotIndex) != null)
		{
			ItemStack itemStack = inventory.getStackInSlot(slotIndex).copy();
			itemStack.stackSize = 1;
			inventory.decrStackSize(slotIndex, 1);
			return itemStack;
		}
		
		return null;
	}

	public ForgeDirection getBeltDirection()
	{
		return ForgeDirection.getOrientation(MachineType.getDirection(this.worldObj.getBlockMetadata(this.xCoord, this.yCoord, this.zCoord)) + 2);
	}
	
	@Override
	public Packet getDescriptionPacket()
    {
		return PacketManager.getPacket(AssemblyLine.CHANNEL, this, this.isOutput);
    }

	@Override
	public boolean canReceiveFromSide(ForgeDirection side)
	{
		return true;
	}

	@Override
	public void onReceive(TileEntity sender, double amps, double voltage, ForgeDirection side)
	{
		this.wattsReceived += ElectricInfo.getWatts(amps, voltage);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt)
	{
		super.readFromNBT(nbt);
		this.isOutput = nbt.getBoolean("isWrenchedToOutput");
	}

	/**
	 * Writes a tile entity to NBT.
	 */
	@Override
	public void writeToNBT(NBTTagCompound nbt)
	{
		super.writeToNBT(nbt);
		nbt.setBoolean("isWrenchedToOutput", this.isOutput);
	}

	@Override
	public void onPowerOn()
	{
		this.isPowered  = true;
	}

	@Override
	public void onPowerOff()
	{
		this.isPowered = false;
	}

	@Override
	public void handlePacketData(INetworkManager network, int packetType, Packet250CustomPayload packet, EntityPlayer player, ByteArrayDataInput dataStream)
	{
		try
		{
			this.isOutput = dataStream.readBoolean();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
}
