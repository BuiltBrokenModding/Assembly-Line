package assemblyline.common.machine;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraftforge.common.ForgeDirection;
import universalelectricity.core.vector.Vector3;
import universalelectricity.prefab.network.IPacketReceiver;
import universalelectricity.prefab.network.PacketManager;
import assemblyline.common.AssemblyLine;
import assemblyline.common.machine.belt.TileEntityConveyorBelt;

import com.google.common.io.ByteArrayDataInput;

import cpw.mods.fml.common.network.PacketDispatcher;

/**
 * 
 * @author Darkguardsman
 * 
 */
public class TileEntityRejector extends TileEntityAssemblyNetwork implements IPacketReceiver, IInventory
{
	/**
	 * The items this container contains.
	 */
	protected ItemStack[] containingItems = new ItemStack[this.getSizeInventory()];

	/**
	 * Used to id the packet types
	 */
	private enum PacketTypes
	{
		ANIMATION, GUI, SETTINGON
	}

	/**
	 * should the piston fire, or be extended
	 */
	public boolean firePiston = false;
	/**
	 * on/off value for the GUI buttons
	 */
	public boolean[] guiButtons = new boolean[] { true, true, true, true, true };
	/**
	 * the belt found in the search area
	 */
	public TileEntityConveyorBelt beltSide = null;

	private int playerUsing = 0;

	@Override
	public void onUpdate()
	{
		/**
		 * Has to update a bit faster than a conveyer belt
		 */
		if (this.ticks % 5 == 0 && !this.isDisabled())
		{
			int meta = worldObj.getBlockMetadata(xCoord, yCoord, zCoord);
			this.firePiston = false;

			// area to search for items
			ForgeDirection searchPosition = Vector3.getOrientationFromSide(ForgeDirection.getOrientation(getDirection(meta)), ForgeDirection.SOUTH);
			TileEntity tileEntity = worldObj.getBlockTileEntity(xCoord + searchPosition.offsetX, yCoord + searchPosition.offsetY, zCoord + searchPosition.offsetZ);

			// find the belt in that search area
			if (tileEntity instanceof TileEntityConveyorBelt)
			{
				this.beltSide = (TileEntityConveyorBelt) tileEntity;
			}
			else
			{
				this.beltSide = null;
			}

			try
			{
				// search area bound box
				AxisAlignedBB bounds = AxisAlignedBB.getBoundingBox(xCoord + searchPosition.offsetX, yCoord + searchPosition.offsetY, zCoord + searchPosition.offsetZ, xCoord + searchPosition.offsetX + 1, yCoord + searchPosition.offsetY + 1, zCoord + searchPosition.offsetZ + 1);
				// EntityItem list
				List<EntityItem> itemsBehind = worldObj.getEntitiesWithinAABB(EntityItem.class, bounds);

				boolean flag = false;

				if (itemsBehind.size() > 0 && this.isRunning())
				{
					// for every item found check
					// if can be thrown then throw
					// item off belt if it can
					for (EntityItem entity : itemsBehind)
					{
						if (this.canItemBeThrow(entity))
						{
							this.throwItem(searchPosition, entity);
							flag = true;
						}
					}
				}
				// send packet with animation data
				// if an item was rejected from
				// the area
				if (!this.worldObj.isRemote && flag)
				{
					Packet packet = PacketManager.getPacket(AssemblyLine.CHANNEL, this, this.getPacketData(PacketTypes.ANIMATION));
					PacketManager.sendPacketToClients(packet, this.worldObj, new Vector3(this), 30);
				}

			}
			catch (Exception e)
			{
				e.printStackTrace();
			}

			if (!this.worldObj.isRemote && this.playerUsing > 0)
			{
				PacketManager.sendPacketToClients(this.getDescriptionPacket(), this.worldObj, new Vector3(this), 10);
			}
		}
	}

	@Override
	public Packet getDescriptionPacket()
	{
		return PacketManager.getPacket(AssemblyLine.CHANNEL, this, this.getPacketData(PacketTypes.GUI));
	}

	/**
	 * Used to move after it has been rejected
	 * 
	 * @param side - used to do the offset
	 * @param entity - Entity being thrown
	 */
	public void throwItem(ForgeDirection side, Entity entity)
	{
		this.firePiston = true;

		entity.motionX = (double) side.offsetX * 0.15;
		entity.motionY += 0.10000000298023224D;
		entity.motionZ = (double) side.offsetZ * 0.15;
	}

	public boolean canItemBeThrow(Entity entity)
	{
		// TODO Add other things than items
		if (entity instanceof EntityItem)
		{
			EntityItem itemE = (EntityItem) entity;
			ItemStack item = itemE.func_92014_d();

			if (this.guiButtons[4])
			{
				// reject matching items
				for (int i = 0; i < this.containingItems.length; i++)
				{
					if (containingItems[i] != null && guiButtons[i])
					{
						if (containingItems[i].itemID == item.itemID && containingItems[i].getItemDamage() == item.getItemDamage()) { return true; }
					}
				}
				return false;

			}
			else if (!this.guiButtons[4])
			{
				// reject all but matching items
				for (int i = 0; i < this.containingItems.length; i++)
				{
					if (containingItems[i] != null && guiButtons[i])
					{
						if (containingItems[i].itemID == item.itemID && containingItems[i].getItemDamage() == item.getItemDamage()) { return false; }
					}
				}
				return true;
			}
		}
		return false;
	}

	public byte getDirection(int meta)
	{

		switch (meta)
		{
			case 0:
				return 2;
			case 1:
				return 5;
			case 2:
				return 3;
			case 3:
				return 4;
		}
		return 0;
	}

	/**
	 * Used to change any one of the boolean value of on/off array After changing the value if it
	 * was changed client side it will send a packet server side with the changes
	 * 
	 * @param i
	 */
	public void changeOnOff(int i)
	{
		if (i >= this.guiButtons.length) { return; }
		if (this.guiButtons[i])
		{
			this.guiButtons[i] = false;
		}
		else
		{
			this.guiButtons[i] = true;
		}
		Packet packet = PacketManager.getPacket("asmLine", this, new Object[] { PacketTypes.SETTINGON.ordinal(), i });
		if (worldObj.isRemote)
		{
			PacketDispatcher.sendPacketToServer(packet);
		}
		else
		{
			PacketManager.sendPacketToClients(packet, worldObj, new Vector3(this), 10);
		}
	}

	public Object[] getPacketData(PacketTypes id)
	{
		if (id == PacketTypes.ANIMATION) { return new Object[] { id.ordinal(), this.firePiston }; }
		if (id == PacketTypes.GUI)
		{
			Object[] da = new Object[this.guiButtons.length + 1];
			da[0] = id.ordinal();

			for (int i = 0; i < this.guiButtons.length; i++)
			{
				da[i + 1] = guiButtons[i];
			}
			return da;
		}
		return new Object[] { id.ordinal() };
	}

	@Override
	public void handlePacketData(INetworkManager network, int packetType, Packet250CustomPayload packet, EntityPlayer player, ByteArrayDataInput dataStream)
	{
		try
		{
			int id = dataStream.readInt();
			PacketTypes pID = PacketTypes.values()[id];

			if (pID == PacketTypes.ANIMATION)
			{
				this.firePiston = dataStream.readBoolean();
			}
			else if (pID == PacketTypes.GUI)
			{
				for (int i = 0; i < this.guiButtons.length; i++)
				{
					this.guiButtons[i] = dataStream.readBoolean();
				}
			}
			else if (pID == PacketTypes.SETTINGON)
			{
				int num = dataStream.readInt();
				this.changeOnOff(num);
			}

		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * inventory methods
	 */
	@Override
	public String getInvName()
	{
		return "Sorter";
	}

	@Override
	public int getSizeInventory()
	{
		return 4;
	}

	/**
	 * UE methods
	 */
	@Override
	public double getVoltage()
	{
		return 120;
	}

	/**
	 * Inventory functions.
	 */
	@Override
	public ItemStack getStackInSlot(int par1)
	{
		return this.containingItems[par1];
	}

	@Override
	public ItemStack decrStackSize(int par1, int par2)
	{
		if (this.containingItems[par1] != null)
		{
			ItemStack var3;

			if (this.containingItems[par1].stackSize <= par2)
			{
				var3 = this.containingItems[par1];
				this.containingItems[par1] = null;
				return var3;
			}
			else
			{
				var3 = this.containingItems[par1].splitStack(par2);

				if (this.containingItems[par1].stackSize == 0)
				{
					this.containingItems[par1] = null;
				}

				return var3;
			}
		}
		else
		{
			return null;
		}
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int par1)
	{
		if (this.containingItems[par1] != null)
		{
			ItemStack var2 = this.containingItems[par1];
			this.containingItems[par1] = null;
			return var2;
		}
		else
		{
			return null;
		}
	}

	@Override
	public void setInventorySlotContents(int par1, ItemStack par2ItemStack)
	{
		this.containingItems[par1] = par2ItemStack;

		if (par2ItemStack != null && par2ItemStack.stackSize > this.getInventoryStackLimit())
		{
			par2ItemStack.stackSize = this.getInventoryStackLimit();
		}
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer par1EntityPlayer)
	{
		return this.worldObj.getBlockTileEntity(this.xCoord, this.yCoord, this.zCoord) != this ? false : par1EntityPlayer.getDistanceSq(this.xCoord + 0.5D, this.yCoord + 0.5D, this.zCoord + 0.5D) <= 64.0D;
	}

	@Override
	public void openChest()
	{
		this.playerUsing++;
	}

	@Override
	public void closeChest()
	{
		this.playerUsing--;
	}

	/**
	 * NBT Data
	 */
	@Override
	public void readFromNBT(NBTTagCompound nbt)
	{
		super.readFromNBT(nbt);

		for (int i = 0; i < this.guiButtons.length; i++)
		{
			this.guiButtons[i] = nbt.getBoolean("guiButton" + i);
		}

		NBTTagList var2 = nbt.getTagList("Items");
		this.containingItems = new ItemStack[this.getSizeInventory()];

		for (int var3 = 0; var3 < var2.tagCount(); ++var3)
		{
			NBTTagCompound var4 = (NBTTagCompound) var2.tagAt(var3);
			byte var5 = var4.getByte("Slot");

			if (var5 >= 0 && var5 < this.containingItems.length)
			{
				this.containingItems[var5] = ItemStack.loadItemStackFromNBT(var4);
			}
		}
	}

	/**
	 * Writes a tile entity to NBT.
	 */
	@Override
	public void writeToNBT(NBTTagCompound nbt)
	{
		super.writeToNBT(nbt);

		for (int i = 0; i < this.guiButtons.length; i++)
		{
			nbt.setBoolean("guiButton" + i, this.guiButtons[i]);
		}

		NBTTagList var2 = new NBTTagList();
		for (int var3 = 0; var3 < this.containingItems.length; ++var3)
		{
			if (this.containingItems[var3] != null)
			{
				NBTTagCompound var4 = new NBTTagCompound();
				var4.setByte("Slot", (byte) var3);
				this.containingItems[var3].writeToNBT(var4);
				var2.appendTag(var4);
			}
		}
		nbt.setTag("Items", var2);
	}

	@Override
	public int getInventoryStackLimit()
	{
		return 1;
	}
}
