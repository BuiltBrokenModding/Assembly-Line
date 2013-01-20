package assemblyline.common.machine.armbot;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
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
import assemblyline.common.machine.command.CommandGrab;
import assemblyline.common.machine.command.CommandManager;
import assemblyline.common.machine.command.CommandReturn;
import assemblyline.common.machine.command.CommandRotate;
import assemblyline.common.machine.command.CommandUse;
import assemblyline.common.machine.encoder.ItemDisk;

import com.google.common.io.ByteArrayDataInput;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.relauncher.Side;
import dan200.computer.api.IComputerAccess;
import dan200.computer.api.IPeripheral;

public class TileEntityArmbot extends TileEntityAssemblyNetwork implements IMultiBlock, IInventory, IPacketReceiver, IJouleStorage, IPeripheral
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
	private int computersAttached = 0;
	private List<IComputerAccess> connectedComputers = new ArrayList<IComputerAccess>();
	/**
	 * The rotation of the arms. In Degrees.
	 */
	public float rotationPitch = 0;
	public float rotationYaw = 0;
	public float renderPitch = 0;
	public float renderYaw = 0;
	private int ticksSincePower = 0;
	public final float ROTATION_SPEED = 1.3f;

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
		Vector3 handPosition = this.getHandPosition();
		if (this.isRunning())
		{
			if (FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER)
			{
				/**
				 * Break the block if the hand hits a solid block.
				 */
				Block block = Block.blocksList[handPosition.getBlockID(this.worldObj)];

				if (block != null)
				{
					if (Block.isNormalCube(block.blockID))
					{
						block.dropBlockAsItem(this.worldObj, this.xCoord, this.yCoord, this.zCoord, handPosition.getBlockMetadata(this.worldObj), 0);
						handPosition.setBlockWithNotify(this.worldObj, 0);
					}
				}

				if (this.disk == null && this.computersAttached == 0)
				{
					this.commandManager.clear();
					if (this.grabbedEntities.size() > 0)
					{
						this.commandManager.addCommand(this, CommandDrop.class);
					}
					else
					{
						if (!this.commandManager.hasTasks())
						{
							if (Math.abs(this.rotationYaw - CommandReturn.IDLE_ROTATION_YAW) > 0.01)
							{
								this.commandManager.addCommand(this, CommandReturn.class);
							}
						}
					}

					this.commandManager.setCurrentTask(0);
				}

				if (FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER)
					this.commandManager.onUpdate();

			}
			this.ticksSincePower = 0;
		}
		else
		{
			this.ticksSincePower++;
		}

		for (Entity entity : this.grabbedEntities)
		{
			if (entity != null)
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
		}

		// keep it within 0 - 360 degrees so ROTATE commands work properly
		if (this.rotationPitch <= -360)
		{
			this.rotationPitch += 360;
		}
		if (this.rotationPitch >= 360)
		{
			this.rotationPitch -= 360;
		}
		if (this.rotationYaw <= -360)
		{
			this.rotationYaw += 360;
		}
		if (this.rotationYaw >= 360)
		{
			this.rotationYaw -= 360;
		}

		if (Math.abs(this.renderYaw - this.rotationYaw) > 0.001f)
		{
			float speed;
			if (this.renderYaw > this.rotationYaw)
				if (Math.abs(this.renderYaw - this.rotationYaw) > 180)
					speed = this.ROTATION_SPEED;
				else
					speed = -this.ROTATION_SPEED;
			else
				if (Math.abs(this.renderYaw - this.rotationYaw) > 180)
					speed = -this.ROTATION_SPEED;
				else
					speed = this.ROTATION_SPEED;
			
			this.renderYaw += speed;
			
			if (this.renderYaw <= -360)
			{
				this.renderYaw += 360;
			}
			if (this.renderYaw >= 360)
			{
				this.renderYaw -= 360;
			}
			
			if (this.ticks % 5 == 0 && FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT) // sound is 0.5 seconds long (20 ticks/second)
				Minecraft.getMinecraft().sndManager.playSound("assemblyline.conveyor", this.xCoord, this.yCoord, this.zCoord, 2f, 1.7f);
			if (Math.abs(this.renderYaw - this.rotationYaw) < this.ROTATION_SPEED + 0.1f)
			{
				this.renderYaw = this.rotationYaw;
			}
			if (Math.abs(this.renderYaw - this.rotationYaw) > 720f) // something's wrong!
			{
				this.renderYaw = this.rotationYaw;
			}
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
		double distance = 1f;
		Vector3 delta = new Vector3();
		// The delta Y of the hand.
		delta.y = Math.sin(Math.toRadians(this.renderPitch)) * distance;
		// The horizontal delta of the hand.
		double dH = Math.cos(Math.toRadians(this.renderPitch)) * distance;
		// The delta X and Z.
		delta.x = Math.sin(Math.toRadians(-this.renderYaw)) * dH;
		delta.z = Math.cos(Math.toRadians(-this.renderYaw)) * dH;
		position.add(delta);
		return position;
	}

	@Override
	public Packet getDescriptionPacket()
	{
		NBTTagCompound nbt = new NBTTagCompound();
		writeToNBT(nbt);
		return PacketManager.getPacket(AssemblyLine.CHANNEL, this, this.powerTransferRange, nbt);
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
				this.powerTransferRange = dis.readInt();
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

		NBTTagList entities = nbt.getTagList("entities");
		this.grabbedEntities.clear();
		for (int i = 0; i < entities.tagCount(); i++)
		{
			NBTTagCompound entityTag = (NBTTagCompound) entities.tagAt(i);
			if (entityTag != null)
			{
				Entity entity = EntityList.createEntityFromNBT(entityTag, worldObj);
				this.grabbedEntities.add(entity);
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

		NBTTagCompound diskNBT = new NBTTagCompound();

		if (this.disk != null)
		{
			this.disk.writeToNBT(diskNBT);
		}

		nbt.setTag("disk", diskNBT);
		nbt.setFloat("yaw", this.rotationYaw);
		nbt.setFloat("pitch", this.rotationPitch);
		nbt.setInteger("currentTask", this.commandManager.getCurrentTask());

		NBTTagList entities = new NBTTagList();
		for (Entity entity : grabbedEntities)
		{
			if (entity != null)
			{
				NBTTagCompound entityNBT = new NBTTagCompound();
				entity.writeToNBT(entityNBT);
				entity.addEntityID(entityNBT);
				entities.appendTag(entityNBT);
			}
		}

		nbt.setTag("entities", entities);
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
		else
		{
			this.commandManager.addCommand(this, Command.getCommand("DROP"));
			this.commandManager.addCommand(this, Command.getCommand("RETURN"));
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

	@Override
	public String getType()
	{
		return "ArmBot";
	}

	@Override
	public String[] getMethodNames()
	{
		return new String[] { "rotate", "grab", "drop", "reset", "isWorking", "touchingEntity", "use" };
	}

	@Override
	public Object[] callMethod(IComputerAccess computer, int method, Object[] arguments) throws Exception
	{		
		switch (method)
		{
			case 0: // rotateBy: rotates by a certain amount
			{
				if (arguments.length > 0)
				{
					try
					// try to cast to Float
					{
						double angle = (Double) arguments[0];
						this.commandManager.addCommand(this, CommandRotate.class, new String[] { Double.toString(angle) });
					}
					catch (Exception ex)
					{
						ex.printStackTrace();
						throw new IllegalArgumentException("expected number");
					}
				}
				else
				{
					throw new IllegalArgumentException("expected number");
				}
				break;
			}
			case 1: // grab: grabs an item
			{
				this.commandManager.addCommand(this, CommandGrab.class);
				break;
			}
			case 2: // drop: drops an item
			{
				this.commandManager.addCommand(this, CommandDrop.class);
				break;
			}
			case 3: // reset: clears the queue and calls the RETURN command
			{
				this.commandManager.clear();
				this.commandManager.addCommand(this, CommandReturn.class);
				break;
			}
			case 4: // isWorking: returns whether or not the ArmBot is executing commands
			{
				return new Object[] { this.commandManager.hasTasks() };
			}
			case 5: // touchingEntity: returns whether or not the ArmBot is touching an entity it is able to pick up
			{
				Vector3 serachPosition = this.getHandPosition();
				List<Entity> found = this.worldObj.getEntitiesWithinAABB(Entity.class, AxisAlignedBB.getBoundingBox(serachPosition.x - 0.5f, serachPosition.y - 0.5f, serachPosition.z - 0.5f, serachPosition.x + 0.5f, serachPosition.y + 0.5f, serachPosition.z + 0.5f));

				if (found != null && found.size() > 0)
				{
					for (int i = 0; i < found.size(); i++)
					{
						if (found.get(i) != null && !(found.get(i) instanceof EntityPlayer) && found.get(i).ridingEntity == null) // isn't null, isn't a player, and isn't riding anything
						{ return new Object[] { true }; }
					}
				}

				return new Object[] { false };
			}
			case 6:
			{
				this.commandManager.addCommand(this, CommandUse.class);
				break;
			}
		}
		return null;
	}

	@Override
	public boolean canAttachToSide(int side)
	{
		return side != ForgeDirection.UP.ordinal();
	}

	@Override
	public void attach(IComputerAccess computer)
	{
		computersAttached++;
		synchronized (connectedComputers)
		{
			connectedComputers.add(computer);
		}
	}

	@Override
	public void detach(IComputerAccess computer)
	{
		computersAttached--;
		synchronized (connectedComputers)
		{
			connectedComputers.remove(computer);
		}
	}

}
