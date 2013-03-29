package assemblyline.common.machine.armbot;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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
import net.minecraft.util.MathHelper;
import net.minecraftforge.common.ForgeDirection;
import universalelectricity.core.block.IElectricityStorage;
import universalelectricity.core.vector.Vector3;
import universalelectricity.prefab.TranslationHelper;
import universalelectricity.prefab.multiblock.IMultiBlock;
import universalelectricity.prefab.network.IPacketReceiver;
import universalelectricity.prefab.network.PacketManager;
import assemblyline.api.IArmbot;
import assemblyline.common.AssemblyLine;
import assemblyline.common.machine.TileEntityAssemblyNetwork;
import assemblyline.common.machine.command.Command;
import assemblyline.common.machine.command.CommandDrop;
import assemblyline.common.machine.command.CommandFire;
import assemblyline.common.machine.command.CommandGrab;
import assemblyline.common.machine.command.CommandManager;
import assemblyline.common.machine.command.CommandReturn;
import assemblyline.common.machine.command.CommandRotateBy;
import assemblyline.common.machine.command.CommandRotateTo;
import assemblyline.common.machine.command.CommandUse;
import assemblyline.common.machine.encoder.ItemDisk;

import com.google.common.io.ByteArrayDataInput;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.relauncher.Side;
import dan200.computer.api.IComputerAccess;
import dan200.computer.api.IPeripheral;
import dark.minecraft.helpers.ItemWorldHelper;

public class TileEntityArmbot extends TileEntityAssemblyNetwork implements IMultiBlock, IInventory, IPacketReceiver, IElectricityStorage, IArmbot, IPeripheral
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

	private String displayText = "";

	public boolean isProvidingPower = false;

	/**
	 * An entity that the Armbot is grabbed onto. Entity Items are held separately.
	 */
	private final List<Entity> grabbedEntities = new ArrayList<Entity>();
	private final List<ItemStack> grabbedItems = new ArrayList<ItemStack>();

	/**
	 * Client Side Object Storage
	 */
	public EntityItem renderEntityItem = null;

	@Override
	public void onUpdate()
	{
		Vector3 handPosition = this.getHandPosition();

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

		if (this.isRunning())
		{
			if (FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER)
			{
				if (this.disk == null && this.computersAttached == 0)
				{
					this.commandManager.clear();

					if (this.grabbedEntities.size() > 0 || this.grabbedItems.size() > 0)
					{
						this.addCommand(CommandDrop.class);
					}
					else
					{
						if (!this.commandManager.hasTasks())
						{
							if (Math.abs(this.rotationYaw - CommandReturn.IDLE_ROTATION_YAW) > 0.01 || Math.abs(this.rotationPitch - CommandReturn.IDLE_ROTATION_PITCH) > 0.01)
							{
								this.addCommand(CommandReturn.class);
							}
						}
					}

					this.commandManager.setCurrentTask(0);
				}
			}
			if (!this.worldObj.isRemote)
				this.commandManager.onUpdate();

			this.ticksSincePower = 0;
		}
		else
		{
			this.ticksSincePower++;
		}

		if (!this.worldObj.isRemote)
		{
			if (!this.commandManager.hasTasks())
			{
				this.displayText = "";
			}
			else
			{
				try
				{
					Command curCommand = this.commandManager.getCommands().get(this.commandManager.getCurrentTask());
					if (curCommand != null)
					{
						this.displayText = curCommand.toString();
					}
				}
				catch (Exception ex)
				{
				}
			}
		}

		// System.out.println("Ren: " + this.renderYaw + "; Rot: " +
		// this.rotationYaw);
		if (Math.abs(this.renderYaw - this.rotationYaw) > 0.001f)
		{
			float speedYaw;
			if (this.renderYaw > this.rotationYaw)
			{
				if (Math.abs(this.renderYaw - this.rotationYaw) >= 180)
					speedYaw = this.ROTATION_SPEED;
				else
					speedYaw = -this.ROTATION_SPEED;
			}
			else
			{
				if (Math.abs(this.renderYaw - this.rotationYaw) >= 180)
					speedYaw = -this.ROTATION_SPEED;
				else
					speedYaw = this.ROTATION_SPEED;
			}

			this.renderYaw += speedYaw;

			// keep it within 0 - 360 degrees so ROTATE commands work properly
			while (this.renderYaw < 0)
				this.renderYaw += 360;
			while (this.renderYaw > 360)
				this.renderYaw -= 360;

			if (this.ticks % 5 == 0 && FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT)
			{
				// sound is 0.25 seconds long (20 ticks/second)
				this.worldObj.playSound(this.xCoord, this.yCoord, this.zCoord, "mods.assemblyline.conveyor", 0.8f, 1.7f, true);
			}

			if (Math.abs(this.renderYaw - this.rotationYaw) < this.ROTATION_SPEED + 0.1f)
			{
				this.renderYaw = this.rotationYaw;
			}

			for (Entity e : (ArrayList<Entity>) this.worldObj.getEntitiesWithinAABB(Entity.class, AxisAlignedBB.getBoundingBox(this.xCoord, this.yCoord + 2, this.zCoord, this.xCoord + 1, this.yCoord + 3, this.zCoord + 1)))
			{
				e.rotationYaw = this.renderYaw;
			}
		}

		if (Math.abs(this.renderPitch - this.rotationPitch) > 0.001f)
		{
			float speedPitch;
			if (this.renderPitch > this.rotationPitch)
			{
				speedPitch = -this.ROTATION_SPEED;
			}
			else
			{
				speedPitch = this.ROTATION_SPEED;
			}

			this.renderPitch += speedPitch;

			while (this.renderPitch < 0)
				this.renderPitch += 60;
			while (this.renderPitch > 60)
				this.renderPitch -= 60;

			if (this.ticks % 4 == 0 && FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT)
				this.worldObj.playSound(this.xCoord, this.yCoord, this.zCoord, "mods.assemblyline.conveyor", 2f, 2.5f, true);

			if (Math.abs(this.renderPitch - this.rotationPitch) < this.ROTATION_SPEED + 0.1f)
			{
				this.renderPitch = this.rotationPitch;
			}

			for (Entity e : (ArrayList<Entity>) this.worldObj.getEntitiesWithinAABB(Entity.class, AxisAlignedBB.getBoundingBox(this.xCoord, this.yCoord + 2, this.zCoord, this.xCoord + 1, this.yCoord + 3, this.zCoord + 1)))
			{
				e.rotationPitch = this.renderPitch;
			}
		}

		while (this.rotationYaw < 0)
			this.rotationYaw += 360;
		while (this.rotationYaw > 360)
			this.rotationYaw -= 360;
		while (this.rotationPitch < 0)
			this.rotationPitch += 60;
		while (this.rotationPitch > 60)
			this.rotationPitch -= 60;

		if (FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER && this.ticks % 20 == 0)
		{
			PacketManager.sendPacketToClients(this.getDescriptionPacket(), this.worldObj, new Vector3(this), 50);
		}
	}

	public Command getCurrentCommand()
	{
		if (this.commandManager.hasTasks() && this.commandManager.getCurrentTask() >= 0 && this.commandManager.getCurrentTask() < this.commandManager.getCommands().size())
			return this.commandManager.getCommands().get(this.commandManager.getCurrentTask());
		return null;
	}

	/**
	 * @return The current hand position of the armbot.
	 */
	public Vector3 getHandPosition()
	{
		Vector3 position = new Vector3(this);
		position.add(0.5);
		position.add(this.getDeltaHandPosition());
		return position;
	}

	public Vector3 getDeltaHandPosition()
	{
		// The distance of the position relative to the main position.
		double distance = 1f;
		Vector3 delta = new Vector3();
		// The delta Y of the hand.
		delta.y = Math.sin(Math.toRadians(this.renderPitch)) * distance * 2;
		// The horizontal delta of the hand.
		double dH = Math.cos(Math.toRadians(this.renderPitch)) * distance;
		// The delta X and Z.
		delta.x = Math.sin(Math.toRadians(-this.renderYaw)) * dH;
		delta.z = Math.cos(Math.toRadians(-this.renderYaw)) * dH;
		return delta;
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

	public String getCommandDisplayText()
	{
		return this.displayText;
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

		if (this.worldObj != null)
		{
			if (this.worldObj.isRemote)
			{
				this.displayText = nbt.getString("cmdText");
			}
		}

		this.commandManager.setCurrentTask(nbt.getInteger("curTask"));

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

		NBTTagList items = nbt.getTagList("items");
		this.grabbedItems.clear();
		for (int i = 0; i < items.tagCount(); i++)
		{
			NBTTagCompound itemTag = (NBTTagCompound) items.tagAt(i);
			if (itemTag != null)
			{
				ItemStack item = ItemStack.loadItemStackFromNBT(itemTag);
				this.grabbedItems.add(item);
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

		nbt.setString("cmdText", this.displayText);

		nbt.setInteger("curTask", this.commandManager.getCurrentTask());

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

		NBTTagList items = new NBTTagList();

		for (ItemStack itemStack : grabbedItems)
		{
			if (itemStack != null)
			{
				NBTTagCompound entityNBT = new NBTTagCompound();
				itemStack.writeToNBT(entityNBT);
				items.appendTag(entityNBT);
			}
		}

		nbt.setTag("items", items);
	}

	@Override
	public double getJoules()
	{
		return this.wattsReceived;
	}

	@Override
	public void setJoules(double joules)
	{
		this.wattsReceived = joules;
	}

	@Override
	public double getMaxJoules()
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

					this.addCommand(command, commandParameters.toArray(new String[0]));
				}
			}
		}
		else
		{
			this.addCommand(Command.getCommand("DROP"));
			this.addCommand(Command.getCommand("RETURN"));
		}
	}

	public void addCommand(Class<? extends Command> command)
	{
		this.commandManager.addCommand(this, command);
		if (FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER)
		{
			PacketManager.sendPacketToClients(this.getDescriptionPacket(), this.worldObj, new Vector3(this), 50);
		}
	}

	public void addCommand(Class<? extends Command> command, String[] parameters)
	{
		this.commandManager.addCommand(this, command, parameters);
		if (FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER)
		{
			PacketManager.sendPacketToClients(this.getDescriptionPacket(), this.worldObj, new Vector3(this), 50);
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
		this.worldObj.setBlock(this.xCoord, this.yCoord, this.zCoord, 0, 0, 3);
		this.worldObj.setBlock(this.xCoord, this.yCoord + 1, this.zCoord, 0, 0, 3);
	}

	@Override
	public String getType()
	{
		return "ArmBot";
	}

	@Override
	public String[] getMethodNames()
	{
		return new String[] { "rotateBy", "rotateTo", "grab", "drop", "reset", "isWorking", "touchingEntity", "use", "fire", "return", "clear", "isHolding" };
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
						double yaw = (Double) arguments[0];
						double pitch = (Double) arguments[1];
						this.addCommand(CommandRotateBy.class, new String[] { Double.toString(yaw), Double.toString(pitch) });
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
			case 1:
			{
				// rotateTo: rotates to a specific rotation
				if (arguments.length > 0)
				{
					try

					{// try to cast to Float
						double yaw = (Double) arguments[0];
						double pitch = (Double) arguments[1];
						this.addCommand(CommandRotateTo.class, new String[] { Double.toString(yaw), Double.toString(pitch) });
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
			case 2:
			{
				// grab: grabs an item
				this.addCommand(CommandGrab.class);
				break;
			}
			case 3:
			{
				// drop: drops an item
				this.addCommand(CommandDrop.class);
				break;
			}
			case 4:
			{
				// reset: equivalent to calling .clear() then .return()
				this.commandManager.clear();
				this.addCommand(CommandReturn.class);
				break;
			}
			case 5:
			{
				// isWorking: returns whether or not the ArmBot is executing
				// commands
				return new Object[] { this.commandManager.hasTasks() };
			}
			case 6:
			{
				// touchingEntity: returns whether or not the ArmBot is touching an
				// entity it is
				// able to pick up
				Vector3 serachPosition = this.getHandPosition();
				List<Entity> found = this.worldObj.getEntitiesWithinAABB(Entity.class, AxisAlignedBB.getBoundingBox(serachPosition.x - 0.5f, serachPosition.y - 0.5f, serachPosition.z - 0.5f, serachPosition.x + 0.5f, serachPosition.y + 0.5f, serachPosition.z + 0.5f));

				if (found != null && found.size() > 0)
				{
					for (int i = 0; i < found.size(); i++)
					{
						if (found.get(i) != null && !(found.get(i) instanceof EntityPlayer) && found.get(i).ridingEntity == null)
						{
							return new Object[] { true };
						}
					}
				}

				return new Object[] { false };
			}
			case 7:
			{
				if (arguments.length > 0)
				{
					try
					{
						// try to cast to Float
						int times = (Integer) arguments[0];
						this.addCommand(CommandUse.class, new String[] { Integer.toString(times) });
					}
					catch (Exception ex)
					{
						ex.printStackTrace();
						throw new IllegalArgumentException("expected number");
					}
				}
				else
				{
					this.addCommand(CommandUse.class);
				}
				break;
			}
			case 8: // fire: think "flying pig"
			{
				if (arguments.length > 0)
				{
					try
					{
						// try to cast to Float
						float strength = (float) ((double) ((Double) arguments[0]));
						this.addCommand(CommandFire.class, new String[] { Float.toString(strength) });
					}
					catch (Exception ex)
					{
						ex.printStackTrace();
						throw new IllegalArgumentException("expected number");
					}
				}
				else
				{
					this.addCommand(CommandFire.class);
				}
				break;
			}
			case 9:
			{
				// return: returns to home position
				this.addCommand(CommandReturn.class);
				break;
			}
			case 10:
			{
				// clear: clears commands
				this.commandManager.clear();
				break;
			}
			case 11:
			{
				// isHolding: returns whether or not it is holding something
				return new Object[] { this.grabbedEntities.size() > 0 };
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

	@Override
	public List<Entity> getGrabbedEntities()
	{
		return this.grabbedEntities;
	}

	@Override
	public List<ItemStack> getGrabbedItems()
	{
		return this.grabbedItems;
	}

	@Override
	public void grabEntity(Entity entity)
	{
		if (entity instanceof EntityItem)
		{
			this.grabItem(((EntityItem) entity).getEntityItem());
			entity.setDead();
		}
		else
		{
			this.grabbedEntities.add(entity);
		}
	}

	@Override
	public void grabItem(ItemStack itemStack)
	{
		this.grabbedItems.add(itemStack);
	}

	@Override
	public void dropEntity(Entity entity)
	{
		this.grabbedEntities.remove(entity);
	}

	@Override
	public void dropItem(ItemStack itemStack)
	{
		Vector3 handPosition = this.getHandPosition();
		this.worldObj.spawnEntityInWorld(new EntityItem(worldObj, handPosition.x, handPosition.y, handPosition.z, itemStack));
		this.grabbedItems.remove(itemStack);
	}

	@Override
	public void dropAll()
	{
		Vector3 handPosition = this.getHandPosition();
		Iterator<ItemStack> it = this.grabbedItems.iterator();

		while (it.hasNext())
		{
			ItemWorldHelper.dropItemStackExact(worldObj, handPosition.x, handPosition.y, handPosition.z, it.next());
		}

		this.grabbedEntities.clear();
		this.grabbedItems.clear();
	}

	/**
	 * called by the block when another checks it too see if it is providing power to a direction
	 */
	public boolean isProvidingPowerSide(ForgeDirection dir)
	{
		return this.isProvidingPower && dir.getOpposite() == this.getFacingDirectionFromAngle();
	}

	/**
	 * gets the facing direction using the yaw angle
	 */
	public ForgeDirection getFacingDirectionFromAngle()
	{
		float angle = MathHelper.wrapAngleTo180_float(this.rotationYaw);
		if (angle >= -45 && angle <= 45)
		{
			return ForgeDirection.SOUTH;
		}
		else if (angle >= 45 && angle <= 135)
		{

			return ForgeDirection.WEST;
		}
		else if (angle >= 135 && angle <= -135)
		{

			return ForgeDirection.NORTH;
		}
		else
		{
			return ForgeDirection.EAST;
		}
	}

	@Override
	public boolean canConnect(ForgeDirection direction)
	{
		return direction == ForgeDirection.DOWN;
	}

	@Override
	public boolean isInvNameLocalized()
	{
		return false;
	}

	@Override
	public boolean isStackValidForSlot(int i, ItemStack itemstack)
	{
		return false;
	}

}
