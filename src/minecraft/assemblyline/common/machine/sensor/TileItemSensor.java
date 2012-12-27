package assemblyline.common.machine.sensor;

import java.util.ArrayList;

import assemblyline.common.AssemblyLine;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet132TileEntityData;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;

public class TileItemSensor extends TileEntity implements IInventory
{
	private boolean		powering;
	private boolean		invertItemCheck;
	private ItemStack[]	iContents	= new ItemStack[27];
	
	public TileItemSensor()
	{
		powering = false;
	}
	
	public boolean hasItems()
	{
		for (int i = 0; i < iContents.length; i++)
		{
			if (iContents[i] != null)
			{
				return true;
			}
		}
		
		return false;
	}
	
	@Override
	public void updateEntity()
	{
		int meta = worldObj.getBlockMetadata(xCoord, yCoord, zCoord) & 3;
		AxisAlignedBB testArea = AxisAlignedBB.getBoundingBox(xCoord, yCoord - 1, zCoord, xCoord + 1, yCoord, zCoord + 1);
		
		ArrayList<Entity> entities = (ArrayList<Entity>) worldObj.getEntitiesWithinAABB(EntityItem.class, testArea);
		boolean tPowering = false;
		if (entities.size() > 0)
		{
			if (hasItems())
			{
				for (int i = 0; i < entities.size(); i++)
				{
					EntityItem e = (EntityItem) entities.get(i);
					ItemStack item = e.func_92014_d();
					boolean tFound = false;
					
					for (int ii = 0; ii < iContents.length; ii++)
					{
						ItemStack compare = iContents[ii];
						if (compare != null)
						{
							if (invertItemCheck)
							{
								if (item.itemID == compare.itemID)
								{
									if (item.getItemDamage() == compare.getItemDamage())
									{
										if (item.hasTagCompound())
										{
											if (item.getTagCompound().equals(compare.getTagCompound()))
											{
												tFound = true;
												break;
											}
										}
										else
										{
											tFound = true;
											break;
										}
									}
								}
							}
							else
							{
								if (item.itemID == compare.itemID)
								{
									if (item.getItemDamage() == compare.getItemDamage())
									{
										if (item.hasTagCompound())
										{
											if (item.getTagCompound().equals(compare.getTagCompound()))
											{
												tPowering = true;
												break;
											}
										}
										else
										{
											tPowering = true;
											break;
										}
									}
								}
							}
						}
					}
					if (invertItemCheck)
					{
						if (!tFound)
						{
							tPowering = true;
							break;
						}
					}
				}
			}
			else
			{
				tPowering = true;
			}
		}
		else
		{
			tPowering = false;
		}
		if (tPowering != powering)
		{
			powering = tPowering;
			worldObj.notifyBlocksOfNeighborChange(xCoord, yCoord, zCoord, AssemblyLine.blockSensor.blockID);
			worldObj.notifyBlocksOfNeighborChange(xCoord, yCoord + 1, zCoord, AssemblyLine.blockSensor.blockID);
			for (int x = xCoord - 1; x <= xCoord + 1; x++)
			{
				for (int z = zCoord - 1; z <= zCoord + 1; z++)
				{
					worldObj.notifyBlocksOfNeighborChange(x, yCoord + 1, z, AssemblyLine.blockSensor.blockID);
				}
			}
		}
	}
	
	public boolean isPowering()
	{
		return powering;
	}
	
	public boolean isItemCheckInverted()
	{
		return invertItemCheck;
	}
	
	public void setItemCheckInverted(boolean inverted)
	{
		invertItemCheck = inverted;
	}
	
	@Override
	public Packet getDescriptionPacket()
	{
		Packet132TileEntityData pack = new Packet132TileEntityData();
		pack.xPosition = xCoord;
		pack.yPosition = yCoord;
		pack.zPosition = zCoord;
		pack.actionType = 0;
		NBTTagCompound tag = new NBTTagCompound();
		writeToNBT(tag);
		pack.customParam1 = tag;
		return pack;
	}
	
	@Override
	public void onDataPacket(INetworkManager netManager, Packet132TileEntityData packet)
	{
		super.onDataPacket(netManager, packet);
		readFromNBT(packet.customParam1);
	}
	
	@Override
	public void readFromNBT(NBTTagCompound tag)
	{
		super.readFromNBT(tag);
		NBTTagList itemList = tag.getTagList("Items");
		this.iContents = new ItemStack[this.getSizeInventory()];
		
		for (int i = 0; i < itemList.tagCount(); ++i)
		{
			NBTTagCompound itemAt = (NBTTagCompound) itemList.tagAt(i);
			int itemSlot = itemAt.getByte("Slot") & 255;
			
			if (itemSlot >= 0 && itemSlot < this.iContents.length)
			{
				this.iContents[itemSlot] = ItemStack.loadItemStackFromNBT(itemAt);
			}
		}
		
		invertItemCheck = tag.getBoolean("inverted");
	}
	
	@Override
	public void writeToNBT(NBTTagCompound tag)
	{
		super.writeToNBT(tag);
		NBTTagList itemList = new NBTTagList();
		
		for (int i = 0; i < this.iContents.length; ++i)
		{
			if (this.iContents[i] != null)
			{
				NBTTagCompound itemAt = new NBTTagCompound();
				itemAt.setByte("Slot", (byte) i);
				this.iContents[i].writeToNBT(itemAt);
				itemList.appendTag(itemAt);
			}
		}
		
		tag.setTag("Items", itemList);
		tag.setBoolean("inverted", invertItemCheck);
	}
	
	@Override
	public int getSizeInventory()
	{
		return 27;
	}
	
	@Override
	public ItemStack getStackInSlot(int slot)
	{
		return iContents[slot];
	}
	
	@Override
	public ItemStack decrStackSize(int slot, int amount)
	{
		if (this.iContents[slot] != null)
		{
			ItemStack var3;
			
			if (this.iContents[slot].stackSize <= amount)
			{
				var3 = this.iContents[slot];
				this.iContents[slot] = null;
				this.onInventoryChanged();
				return var3;
			}
			else
			{
				var3 = this.iContents[slot].splitStack(amount);
				
				if (this.iContents[slot].stackSize == 0)
				{
					this.iContents[slot] = null;
				}
				
				this.onInventoryChanged();
				return var3;
			}
		}
		else
		{
			return null;
		}
	}
	
	@Override
	public ItemStack getStackInSlotOnClosing(int slot)
	{
		if (this.iContents[slot] != null)
		{
			ItemStack var2 = this.iContents[slot];
			this.iContents[slot] = null;
			return var2;
		}
		else
		{
			return null;
		}
	}
	
	@Override
	public void setInventorySlotContents(int slot, ItemStack stack)
	{
		this.iContents[slot] = stack;
		
		if (stack != null && stack.stackSize > this.getInventoryStackLimit())
		{
			stack.stackSize = this.getInventoryStackLimit();
		}
		
		this.onInventoryChanged();
	}
	
	@Override
	public String getInvName()
	{
		return "container.itemSensor";
	}
	
	@Override
	public int getInventoryStackLimit()
	{
		return 64;
	}
	
	@Override
	public boolean isUseableByPlayer(EntityPlayer player)
	{
		return this.worldObj.getBlockTileEntity(this.xCoord, this.yCoord, this.zCoord) != this ? false : player.getDistanceSq((double) this.xCoord + 0.5D, (double) this.yCoord + 0.5D, (double) this.zCoord + 0.5D) <= 64.0D;
	}
	
	@Override
	public void openChest()
	{
	}
	
	@Override
	public void closeChest()
	{
	}
}
