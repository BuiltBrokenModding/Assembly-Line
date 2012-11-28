package assemblyline.machines.crafter;

import net.minecraft.src.EntityPlayer;
import net.minecraft.src.IInventory;
import net.minecraft.src.INetworkManager;
import net.minecraft.src.ItemStack;
import net.minecraft.src.NBTTagCompound;
import net.minecraft.src.NBTTagList;
import net.minecraft.src.Packet250CustomPayload;
import net.minecraftforge.common.ForgeDirection;
import universalelectricity.prefab.network.IPacketReceiver;
import universalelectricity.prefab.tile.TileEntityElectricityReceiver;
import assemblyline.ai.TaskManager;

import com.google.common.io.ByteArrayDataInput;

public class TileEntityCraftingArm extends TileEntityElectricityReceiver implements IInventory, IPacketReceiver
{
	/**
	 * The items this container contains.
	 */
	protected ItemStack[] containingItems = new ItemStack[this.getSizeInventory()];

	private TaskManager taskManager = new TaskManager();

	/**
	 * Entity robotic arm to be used with this tileEntity
	 */
	public EntityCraftingArm EntityArm = null;

	public double wattUsed = 20;

	public double jouleReceived = 0;

	public double maxJoules = 100;
	/**
	 * does this arm have a task to do
	 */
	public boolean hasTask = true;

	private int playerUsing = 0;

	public void updateEntity()
	{
		super.updateEntity();

		taskManager.onUpdate();

		if (this.ticks % 5 == 0 && !this.isDisabled() && this.hasTask && EntityArm != null)
		{
			this.jouleReceived -= this.wattUsed;
			this.doWork();
		}
	}

	/**
	 * controls the robotic arm into doing a set task
	 */
	public void doWork()
	{

	}

	/**
	 * UE methods
	 */
	@Override
	public void onReceive(Object sender, double amps, double voltage, ForgeDirection side)
	{
		this.jouleReceived = Math.max(jouleReceived + (amps * voltage), maxJoules);

	}

	@Override
	public double wattRequest()
	{
		return maxJoules - jouleReceived;
	}

	@Override
	public boolean canReceiveFromSide(ForgeDirection side)
	{
		if (side != ForgeDirection.UP) { return true; }
		return false;
	}

	@Override
	public boolean canConnect(ForgeDirection side)
	{
		if (side != ForgeDirection.UP) { return true; }
		return false;
	}

	@Override
	public double getVoltage()
	{
		return 120;
	}

	/**
	 * Data
	 */
	@Override
	public void handlePacketData(INetworkManager network, int packetType, Packet250CustomPayload packet, EntityPlayer player, ByteArrayDataInput dataStream)
	{

	}

	/**
	 * inventory
	 */
	@Override
	public int getSizeInventory()
	{
		return 1;
	}

	@Override
	public String getInvName()
	{
		return "RoboticArm";
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
	public int getInventoryStackLimit()
	{
		return 64;
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

}
