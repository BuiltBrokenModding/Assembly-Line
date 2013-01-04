package assemblyline.common.machine.crafter;

import java.util.EnumSet;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.ForgeDirection;
import universalelectricity.core.electricity.ElectricityConnections;
import universalelectricity.core.implement.IConductor;
import universalelectricity.core.implement.IJouleStorage;
import universalelectricity.core.vector.Vector3;
import universalelectricity.prefab.network.IPacketReceiver;
import universalelectricity.prefab.tile.TileEntityElectricityReceiver;
import assemblyline.common.machine.armbot.CommandManager;

import com.google.common.io.ByteArrayDataInput;

public class TileEntityArmbot extends TileEntityElectricityReceiver implements IInventory, IPacketReceiver, IJouleStorage
{
	/**
	 * The items this container contains.
	 */
	protected ItemStack[] containingItems = new ItemStack[this.getSizeInventory()];

	private CommandManager taskManager = new CommandManager();

	/**
	 * Entity robotic arm to be used with this tileEntity
	 */
	public EntityCraftingArm EntityArm = null;

	public final double WATT_REQUEST = 20;

	public double wattsReceived = 0;

	private int playerUsing = 0;

	@Override
	public void initiate()
	{
		ElectricityConnections.registerConnector(this, EnumSet.of(ForgeDirection.DOWN, ForgeDirection.SOUTH, ForgeDirection.NORTH, ForgeDirection.EAST, ForgeDirection.WEST));
	}

	public void updateEntity()
	{
		super.updateEntity();

		if (!this.worldObj.isRemote)
		{
			for (int i = 0; i < 6; i++)
			{
				ForgeDirection inputDirection = ForgeDirection.getOrientation(i);

				if (inputDirection != ForgeDirection.UP)
				{
					TileEntity inputTile = Vector3.getTileEntityFromSide(this.worldObj, new Vector3(this), inputDirection);

					if (inputTile != null)
					{
						if (inputTile instanceof IConductor)
						{
							if (this.getJoules() >= this.getMaxJoules())
							{
								((IConductor) inputTile).getNetwork().stopRequesting(this);
							}
							else
							{
								((IConductor) inputTile).getNetwork().startRequesting(this, this.WATT_REQUEST / this.getVoltage(), this.getVoltage());
								this.setJoules(this.getJoules() + ((IConductor) inputTile).getNetwork().consumeElectricity(this).getWatts());
							}
						}
					}
				}
			}
		}

		taskManager.onUpdate();

		if (this.ticks % 5 == 0 && !this.isDisabled() && this.taskManager.hasTasks() && EntityArm != null)
		{
			this.wattsReceived -= this.WATT_REQUEST;
			this.doWork();
		}
	}

	/**
	 * controls the robotic arm into doing a set task
	 */
	public void doWork()
	{

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

	@Override
	public double getJoules(Object... data)
	{
		return this.wattsReceived;
	}

	@Override
	public void setJoules(double joules, Object... data)
	{
		this.wattsReceived = joules;
	}

	@Override
	public double getMaxJoules(Object... data)
	{
		return 1000;
	}

}
