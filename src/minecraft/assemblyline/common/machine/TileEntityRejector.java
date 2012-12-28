package assemblyline.common.machine;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraftforge.common.ForgeDirection;
import universalelectricity.core.vector.Vector3;
import universalelectricity.prefab.TranslationHelper;
import universalelectricity.prefab.implement.IRotatable;
import universalelectricity.prefab.network.IPacketReceiver;
import universalelectricity.prefab.network.PacketManager;
import universalelectricity.prefab.network.PacketManager.PacketType;
import assemblyline.api.IFilterable;
import assemblyline.common.AssemblyLine;
import assemblyline.common.machine.filter.ItemFilter;

import com.google.common.io.ByteArrayDataInput;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.relauncher.Side;

/**
 * 
 * @author Darkguardsman
 * 
 */
public class TileEntityRejector extends TileEntityAssemblyNetwork implements IRotatable, IFilterable, IPacketReceiver, IInventory
{
	/**
	 * The items this container contains.
	 */
	protected ItemStack[] containingItems = new ItemStack[1];

	/**
	 * Used to id the packet types
	 */
	private enum PacketTypes
	{
		ANIMATION, INVENTORY, SETTINGON
	}

	/**
	 * should the piston fire, or be extended
	 */
	public boolean firePiston = false;
	/**
	 * on/off value for the GUI buttons
	 */
	public boolean[] guiButtons = new boolean[] { true, true, true, true, true };

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
			Vector3 searchPosition = new Vector3(this);
			searchPosition.modifyPositionFromSide(this.getDirection());
			TileEntity tileEntity = searchPosition.getTileEntity(this.worldObj);

			try
			{
				boolean flag = false;

				if (this.isRunning())
				{
					/**
					 * Find all entities in the position in which this block is facing and attempt
					 * to push it out of the way.
					 */
					AxisAlignedBB bounds = AxisAlignedBB.getBoundingBox(searchPosition.x, searchPosition.y, searchPosition.z, searchPosition.x + 1, searchPosition.y + 1, searchPosition.z + 1);
					List<Entity> entitiesInFront = this.worldObj.getEntitiesWithinAABB(Entity.class, bounds);

					for (Entity entity : entitiesInFront)
					{
						if (this.canEntityBeThrow(entity))
						{
							this.throwItem(this.getDirection(), entity);
							flag = true;
						}
					}
				}

				/**
				 * If a push happened, send a packet to the client to notify it for an animation.
				 */
				if (!this.worldObj.isRemote && flag)
				{
					// Packet packet = PacketManager.getPacket(AssemblyLine.CHANNEL, this,
					// this.getPacketData(PacketTypes.ANIMATION));
					// PacketManager.sendPacketToClients(packet, this.worldObj, new Vector3(this),
					// 30);
					PacketManager.sendPacketToClients(getDescriptionPacket());
				}

			}
			catch (Exception e)
			{
				e.printStackTrace();
			}

			if (FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER && this.playerUsing > 0)
			{
				PacketManager.sendPacketToClients(this.getDescriptionPacket(), this.worldObj, new Vector3(this), 10);
			}
		}
	}

	/*
	 * @Override public Packet getDescriptionPacket() { return
	 * PacketManager.getPacket(AssemblyLine.CHANNEL, this,
	 * this.getPacketData(PacketTypes.INVENTORY)); }
	 */

	/**
	 * Used to move after it has been rejected
	 * 
	 * @param side - used to do the offset
	 * @param entity - Entity being thrown
	 */
	public void throwItem(ForgeDirection side, Entity entity)
	{
		this.firePiston = true;

		entity.motionX = (double) side.offsetX * 0.1;
		entity.motionY += 0.10000000298023224D;
		entity.motionZ = (double) side.offsetZ * 0.1;
		
		PacketManager.sendPacketToClients(getDescriptionPacket());
	}

	public boolean canEntityBeThrow(Entity entity)
	{
		// TODO Add other things than items
		if (entity instanceof EntityItem)
		{
			EntityItem entityItem = (EntityItem) entity;
			ItemStack itemStack = entityItem.func_92014_d();

			if (this.containingItems[0] != null)
			{
				ArrayList<ItemStack> checkStacks = ItemFilter.getFilters(this.containingItems[0]);

				// Reject matching items
				for (int i = 0; i < checkStacks.size(); i++)
				{
					if (checkStacks.get(i) != null)
					{
						if (checkStacks.get(i).isItemEqual(itemStack)) { return true; }
					}
				}
			}
		}

		return false;
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

	/*
	 * public Object[] getPacketData(PacketTypes id) { if (id == PacketTypes.ANIMATION) { return new
	 * Object[] { id.ordinal(), this.firePiston }; } if (id == PacketTypes.INVENTORY) { Object[] da
	 * = new Object[this.guiButtons.length + 1]; da[0] = id.ordinal();
	 * 
	 * for (int i = 0; i < this.guiButtons.length; i++) { da[i + 1] = guiButtons[i]; } return da; }
	 * return new Object[] { id.ordinal() }; }
	 */

	/*
	 * @Override public void handlePacketData(INetworkManager network, int packetType,
	 * Packet250CustomPayload packet, EntityPlayer player, ByteArrayDataInput dataStream) { try {
	 * int id = dataStream.readInt(); PacketTypes pID = PacketTypes.values()[id]; DataInputStream
	 * inputStream = new DataInputStream((InputStream) dataStream);
	 * 
	 * if (pID == PacketTypes.ANIMATION) { this.firePiston = dataStream.readBoolean(); } else if
	 * (pID == PacketTypes.INVENTORY) { for (int i = 0; i < this.guiButtons.length; i++) {
	 * this.guiButtons[i] = dataStream.readBoolean(); } } else if (pID == PacketTypes.SETTINGON) {
	 * int num = dataStream.readInt(); this.changeOnOff(num); }
	 * 
	 * } catch (Exception e) { e.printStackTrace(); } }
	 */

	/**
	 * inventory methods
	 */
	@Override
	public String getInvName()
	{
		return TranslationHelper.getLocal("tile.rejector.name");
	}

	@Override
	public int getSizeInventory()
	{
		return this.containingItems.length;
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

		/*for (int i = 0; i < this.guiButtons.length; i++)
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
		}*/
		
		NBTTagCompound filter = nbt.getCompoundTag("filter");
		setInventorySlotContents(0, ItemStack.loadItemStackFromNBT(filter));
		firePiston = nbt.getBoolean("piston");
	}

	/**
	 * Writes a tile entity to NBT.
	 */
	@Override
	public void writeToNBT(NBTTagCompound nbt)
	{
		super.writeToNBT(nbt);

		/*for (int i = 0; i < this.guiButtons.length; i++)
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
		nbt.setTag("Items", var2);*/
		NBTTagCompound filter = new NBTTagCompound();
		if (getStackInSlot(0) != null)
			getStackInSlot(0).writeToNBT(filter);
		nbt.setCompoundTag("filter", filter);
		nbt.setBoolean("piston", firePiston);
	}

	@Override
	public int getInventoryStackLimit()
	{
		return 1;
	}

	@Override
	public void setFilter(ItemStack filter)
	{
		this.setInventorySlotContents(0, filter);
		PacketManager.sendPacketToClients(getDescriptionPacket());
	}

	@Override
	public ItemStack getFilter()
	{
		return this.getStackInSlot(0);
	}

	@Override
	public ForgeDirection getDirection()
	{
		return ForgeDirection.getOrientation(this.getBlockMetadata());
	}

	@Override
	public void setDirection(ForgeDirection facingDirection)
	{
		this.worldObj.setBlockMetadataWithNotify(xCoord, yCoord, zCoord, facingDirection.ordinal());
	}

	@Override
	public Packet getDescriptionPacket()
	{
		Packet250CustomPayload packet = new Packet250CustomPayload();
		packet.channel = AssemblyLine.CHANNEL;
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(bos);
		try
		{
			dos.writeInt(PacketType.TILEENTITY.ordinal());
			dos.writeInt(xCoord);
			dos.writeInt(yCoord);
			dos.writeInt(zCoord);
			NBTTagCompound tag = new NBTTagCompound();
			writeToNBT(tag);
			PacketManager.writeNBTTagCompound(tag, dos);
			packet.data = bos.toByteArray();
			packet.length = bos.size();
			return packet;
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public void handlePacketData(INetworkManager network, int packetType, Packet250CustomPayload packet, EntityPlayer player, ByteArrayDataInput dataStream)
	{
		ByteArrayInputStream bis = new ByteArrayInputStream(packet.data);
		DataInputStream dis = new DataInputStream(bis);
		int id, x, y, z;
		try
		{
			id = dis.readInt();
			x = dis.readInt();
			y = dis.readInt();
			z = dis.readInt();
			NBTTagCompound tag = Packet.readNBTTagCompound(dis);
			readFromNBT(tag);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
}
