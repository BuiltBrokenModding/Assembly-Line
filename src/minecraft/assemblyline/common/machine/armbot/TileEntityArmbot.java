package assemblyline.common.machine.armbot;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Random;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
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
import assemblyline.common.machine.command.CommandDrop;
import assemblyline.common.machine.command.CommandManager;
import assemblyline.common.machine.command.CommandReturn;
import assemblyline.common.machine.encoder.ItemDisk;

import com.google.common.io.ByteArrayDataInput;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.FMLLog;
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

	private int ticksSincePower = 0;

	/**
	 * An entity that the armbot is grabbed onto.
	 */
	public final List<Entity> grabbedEntities = new ArrayList<Entity>();

	@Override
	public void initiate()
	{
		ElectricityConnections.registerConnector(this, EnumSet.range(ForgeDirection.DOWN, ForgeDirection.EAST));
		this.onInventoryChanged();
	}

	@Override
	public void onUpdate()
	{
		if (this.isRunning())
		{
			Vector3 handPosition = this.getHandPosition();

			for (Entity entity : this.grabbedEntities)
			{
				entity.setPosition(handPosition.x, handPosition.y, handPosition.z);
				entity.motionX = 0;
				entity.motionY = 0;
				entity.motionZ = 0;

				if (entity instanceof EntityItem)
				{
					((EntityItem) entity).delayBeforeCanPickup = 20;
					((EntityItem) entity).age = 0;
				}
			}

			if (this.disk == null)
			{
				this.commandManager.clear();

				if (this.grabbedEntities.size() > 0)
				{
					this.commandManager.addCommand(this, CommandDrop.class);
				}
				else
				{
					this.commandManager.addCommand(this, CommandReturn.class);
				}

				this.commandManager.setCurrentTask(0);
			}

			this.commandManager.onUpdate();

			// keep it within 0 - 360 degrees so ROTATE commands work properly
			if (this.rotationPitch <= -360)
				this.rotationPitch += 360;
			if (this.rotationPitch >= 360)
				this.rotationPitch -= 360;
			if (this.rotationYaw <= -360)
				this.rotationYaw += 360;
			if (this.rotationYaw >= 360)
				this.rotationYaw -= 360;

			this.ticksSincePower = 0;
		}
		else
		{
			this.ticksSincePower++;
			if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT && ticksSincePower <= 20)
				this.commandManager.onUpdate();
		}

		if (FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER && this.ticks % 20 == 0)
		{
			PacketManager.sendPacketToClients(this.getDescriptionPacket(), this.worldObj, new Vector3(this), 50);
		}
	}

	/**
	 * @return The current hand position of the armbot.
	 */
	public Vector3 getHandPosition()
	{
		Vector3 position = new Vector3(this);
		position.add(0.5);
		// The distance of the position relative to the main position.
		double distance = 1.7f;
		Vector3 delta = new Vector3();
		// The delta Y of the hand.
		delta.y = Math.sin(Math.toRadians(this.rotationPitch)) * distance;
		// The horizontal delta of the hand.
		double dH = Math.cos(Math.toRadians(this.rotationPitch)) * distance;
		// The delta X and Z.
		delta.x = Math.sin(Math.toRadians(-this.rotationYaw)) * dH;
		delta.z = Math.cos(Math.toRadians(-this.rotationYaw)) * dH;
		position.add(delta);
		// TODO: Use Smoke Spawning to Determine Hand Calculation Position. Delete when done
		// developing this part.
		// this.worldObj.spawnParticle("smoke", position.x, position.y, position.z, 0, 0, 0);
		return position;
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
		return PacketManager.getPacket(AssemblyLine.CHANNEL, this, this.commandManager.getCurrentTask(), nbt);
	}

	/**
	 * Data
	 */
	@Override
	public void handlePacketData(INetworkManager network, int packetType, Packet250CustomPayload packet, EntityPlayer player, ByteArrayDataInput dataStream)
	{
		if (this.worldObj.isRemote)
		{
			try
			{
				ByteArrayInputStream bis = new ByteArrayInputStream(packet.data);
				DataInputStream dis = new DataInputStream(bis);
				int id, x, y, z;
				id = dis.readInt();
				x = dis.readInt();
				y = dis.readInt();
				z = dis.readInt();
				this.commandManager.setCurrentTask(dis.readInt());
				NBTTagCompound tag = Packet.readNBTTagCompound(dis);
				readFromNBT(tag);
			}
			catch (IOException e)
			{
				FMLLog.severe("Failed to receive packet for Armbot");
				e.printStackTrace();
			}
		}
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
		this.onInventoryChanged();
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
			this.disk = ItemStack.loadItemStackFromNBT(diskNBT);
		}
		else
		{
			this.disk = null;
		}

		this.rotationYaw = nbt.getFloat("yaw");
		this.rotationPitch = nbt.getFloat("pitch");
		this.commandManager.setCurrentTask(nbt.getInteger("currentTask"));
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
		nbt.setInteger("currentTask", this.commandManager.getCurrentTask());
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
			return true;
		}
		else
		{
			if (player.getCurrentEquippedItem() != null)
			{
				if (player.getCurrentEquippedItem().getItem() instanceof ItemDisk)
				{
					this.setInventorySlotContents(0, player.getCurrentEquippedItem());
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
		this.commandManager.clear();

		if (this.disk != null)
		{
			List<String> commands = ItemDisk.getCommands(this.disk);

			for (String commandString : commands)
			{
				String commandName = commandString.split(" ")[0];

				Class<? extends Command> command = Command.getCommand(commandName);

				if (command != null)
				{
					List<String> commandParameters = new ArrayList<String>();

					for (String param : commandString.split(" "))
					{
						if (!param.equals(commandName))
						{
							commandParameters.add(param);
						}
					}

					this.commandManager.addCommand(this, command, commandParameters.toArray(new String[0]));
				}
			}
		}
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
