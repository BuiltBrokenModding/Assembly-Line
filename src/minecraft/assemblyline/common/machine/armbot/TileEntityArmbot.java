package assemblyline.common.machine.armbot;

import java.util.ArrayList;
import java.util.EnumSet;
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
import net.minecraft.network.packet.Packet132TileEntityData;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.ForgeDirection;
import universalelectricity.core.electricity.ElectricityConnections;
import universalelectricity.core.implement.IJouleStorage;
import universalelectricity.core.vector.Vector3;
import universalelectricity.prefab.TranslationHelper;
import universalelectricity.prefab.multiblock.IMultiBlock;
import universalelectricity.prefab.network.IPacketReceiver;
import universalelectricity.prefab.network.PacketManager;
import assemblyline.common.AssemblyLine;
import assemblyline.common.machine.TileEntityAssemblyNetwork;
import assemblyline.common.machine.command.Command;
import assemblyline.common.machine.command.CommandIdle;
import assemblyline.common.machine.command.CommandManager;
import assemblyline.common.machine.command.CommandReturn;
import assemblyline.common.machine.encoder.ItemDisk;

import com.google.common.io.ByteArrayDataInput;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;

public class TileEntityArmbot extends TileEntityAssemblyNetwork implements IMultiBlock, IInventory, IPacketReceiver, IJouleStorage
{
	private final CommandManager commandManager = new CommandManager();
	private static final int PACKET_COMMANDS = 128;

	/**
	 * The items this container contains.
	 */
	protected ItemStack disk = null;

	public final double WATT_REQUEST = 20;

	public double wattsReceived = 0;

	private int playerUsing = 0;

	/**
	 * The rotation of the arms. In Degrees.
	 */
	public float rotationPitch = 0;
	public float rotationYaw = 0;

	/**
	 * An entity that the armbot is grabbed onto.
	 */
	public final List<Entity> grabbedEntities = new ArrayList<Entity>();

	@Override
	public void initiate()
	{
		ElectricityConnections.registerConnector(this, EnumSet.range(ForgeDirection.DOWN, ForgeDirection.EAST));
	}

	public void onUpdate()
	{
		if (this.disk != null)
		{
			try
			{
				if (this.commandManager.hasTasks())
				{
					if (this.commandManager.getCommands().get(0) instanceof CommandReturn)
					{
						this.commandManager.clearTasks();
					}
				}
				if (!this.commandManager.hasTasks())
				{
					List<String> commands = ItemDisk.getCommands(this.disk);

					for (String commandString : commands)
					{
						String commandName = commandString.split(" ")[0];

						Class<? extends Command> command = Command.getCommand(commandName);

						if (command != null)
						{
							Command newCommand = command.newInstance();
							newCommand.world = this.worldObj;
							newCommand.tileEntity = this;

							List<String> commandParameters = new ArrayList<String>();

							for (String param : commandString.split(" "))
							{
								if (!param.equals(commandName))
								{
									commandParameters.add(param);
								}
							}

							newCommand.setParameters(commandParameters.toArray(new String[0]));

							newCommand.onTaskStart();
							this.commandManager.addTask(this, newCommand);
						}
					}
				}
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		else
		{
			this.commandManager.clearTasks();
			this.commandManager.addTask(this, new CommandReturn());
		}
		
		if (this.isRunning())
		{
			for (Entity entity : this.grabbedEntities)
			{
				entity.motionX = 0;
				entity.motionY = 0;
				entity.motionZ = 0;
			}

			if (FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER)
			{
				this.commandManager.onUpdate();
			}

			//keep it within [0, 360) so ROTATE commands work properly
			if (this.rotationPitch <= -360)
				this.rotationPitch += 360;
			if (this.rotationPitch >= 360)
				this.rotationPitch -= 360;
			if (this.rotationYaw <= -360)
				this.rotationYaw += 360;
			if (this.rotationYaw >= 360)
				this.rotationYaw -= 360;

		}

		// Simulates smoothness on client side
		if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT)
		{
			this.commandManager.onUpdate();
		}
	}

	@Override
	public double getVoltage()
	{
		return 120;
	}

	@Override
	public Packet getDescriptionPacket()
	{
		NBTTagCompound nbt = new NBTTagCompound();
		writeToNBT(nbt);
		Packet132TileEntityData data = new Packet132TileEntityData(xCoord, yCoord, zCoord, 0, nbt);
		return data;
	}

	@Override
	public void onDataPacket(INetworkManager netManager, Packet132TileEntityData packet)
	{
		if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT)
		{
			xCoord = packet.xPosition;
			yCoord = packet.yPosition;
			zCoord = packet.zPosition;
			readFromNBT(packet.customParam1);
		}
	}

	/**
	 * Data
	 */
	@Override
	public void handlePacketData(INetworkManager network, int packetType, Packet250CustomPayload packet, EntityPlayer player, ByteArrayDataInput dataStream)
	{
		/*if (packetType == PACKET_COMMANDS)
		{
			String commandString = dataStream.readUTF();
			String[] commands = commandString.split("|");
			
		}*/
	}

	/**
	 * Inventory
	 */
	@Override
	public int getSizeInventory()
	{
		return 1;
	}

	@Override
	public String getInvName()
	{
		return TranslationHelper.getLocal("tile.armbot.name");
	}

	/**
	 * Inventory functions.
	 */
	@Override
	public ItemStack getStackInSlot(int par1)
	{
		return this.disk;
	}

	@Override
	public ItemStack decrStackSize(int par1, int par2)
	{
		if (this.disk != null)
		{
			ItemStack var3;

			if (this.disk.stackSize <= par2)
			{
				var3 = this.disk;
				this.disk = null;
				return var3;
			}
			else
			{
				var3 = this.disk.splitStack(par2);

				if (this.disk.stackSize == 0)
				{
					this.disk = null;
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
		if (this.disk != null)
		{
			ItemStack var2 = this.disk;
			this.disk = null;
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
		this.disk = par2ItemStack;

		if (par2ItemStack != null && par2ItemStack.stackSize > this.getInventoryStackLimit())
		{
			par2ItemStack.stackSize = this.getInventoryStackLimit();
		}
	}

	@Override
	public int getInventoryStackLimit()
	{
		return 1;
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

		NBTTagCompound diskNBT = nbt.getCompoundTag("disk");
		if (diskNBT != null)
		{
			disk = ItemStack.loadItemStackFromNBT(diskNBT);
		}

		this.rotationYaw = nbt.getFloat("yaw");
		this.rotationPitch = nbt.getFloat("pitch");
	}

	/**
	 * Writes a tile entity to NBT.
	 */
	@Override
	public void writeToNBT(NBTTagCompound nbt)
	{
		super.writeToNBT(nbt);
		
		NBTTagCompound diskNBT = new NBTTagCompound();

		if (this.disk != null)
		{
			this.disk.writeToNBT(diskNBT);
		}

		nbt.setTag("disk", diskNBT);
		nbt.setFloat("yaw", this.rotationYaw);
		nbt.setFloat("pitch", this.rotationPitch);
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

	@Override
	public boolean onActivated(EntityPlayer player)
	{
		ItemStack containingStack = this.getStackInSlot(0);

		if (containingStack != null)
		{
			if (FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER)
			{
				EntityItem dropStack = new EntityItem(this.worldObj, player.posX, player.posY, player.posZ, containingStack);
				dropStack.delayBeforeCanPickup = 0;
				this.worldObj.spawnEntityInWorld(dropStack);
			}

			this.setInventorySlotContents(0, null);
			onInventoryChanged();
			return true;
		}
		else
		{
			if (player.getCurrentEquippedItem() != null)
			{
				if (player.getCurrentEquippedItem().getItem() instanceof ItemDisk)
				{
					this.setInventorySlotContents(0, player.getCurrentEquippedItem());
					onInventoryChanged();
					player.inventory.setInventorySlotContents(player.inventory.currentItem, null);
					return true;
				}
			}
		}

		return false;
	}

	@Override
	public void onInventoryChanged()
	{
	}

	@Override
	public void onCreate(Vector3 placedPosition)
	{
		AssemblyLine.blockMulti.makeFakeBlock(this.worldObj, Vector3.add(placedPosition, new Vector3(0, 1, 0)), placedPosition);
	}

	@Override
	public void onDestroy(TileEntity callingBlock)
	{
		Vector3 destroyPosition = new Vector3(callingBlock);
		destroyPosition.add(new Vector3(0, 1, 0));
		destroyPosition.setBlockWithNotify(this.worldObj, 0);
		this.worldObj.setBlockWithNotify(this.xCoord, this.yCoord, this.zCoord, 0);
	}

}
