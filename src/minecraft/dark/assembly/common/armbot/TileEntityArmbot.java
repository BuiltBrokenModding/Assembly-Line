package dark.assembly.common.armbot;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.packet.Packet;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraftforge.common.ForgeDirection;
import universalelectricity.core.vector.Vector3;
import universalelectricity.prefab.TranslationHelper;

import com.google.common.io.ByteArrayDataInput;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.Player;
import cpw.mods.fml.relauncher.Side;
import dan200.computer.api.IComputerAccess;
import dan200.computer.api.ILuaContext;
import dan200.computer.api.IPeripheral;
import dark.api.al.armbot.Command;
import dark.api.al.armbot.IArmbot;
import dark.assembly.common.AssemblyLine;
import dark.assembly.common.armbot.command.CommandDrop;
import dark.assembly.common.armbot.command.CommandFire;
import dark.assembly.common.armbot.command.CommandGrab;
import dark.assembly.common.armbot.command.CommandReturn;
import dark.assembly.common.armbot.command.CommandRotateBy;
import dark.assembly.common.armbot.command.CommandRotateTo;
import dark.assembly.common.armbot.command.CommandUse;
import dark.assembly.common.machine.TileEntityAssembly;
import dark.assembly.common.machine.encoder.ItemDisk;
import dark.core.common.DarkMain;
import dark.core.network.PacketHandler;
import dark.core.prefab.IMultiBlock;
import dark.core.prefab.helpers.ItemWorldHelper;
import dark.core.prefab.helpers.MathHelper;
import dark.core.prefab.machine.BlockMulti;

public class TileEntityArmbot extends TileEntityAssembly implements IMultiBlock, IArmbot, IPeripheral
{
    private final CommandManager commandManager = new CommandManager();
    private int computersAttached = 0;
    private List<IComputerAccess> connectedComputers = new ArrayList<IComputerAccess>();
    /** The rotation of the arms. In Degrees. */
    public float rotationPitch = 0;
    public float rotationYaw = 0;
    public float actualPitch = 0;
    public float actualYaw = 0;
    public final float ROTATION_SPEED = 2.0f;

    private String displayText = "";

    public boolean isProvidingPower = false;

    /** An entity that the Armbot is grabbed onto. Entity Items are held separately. */
    private final List<Entity> grabbedEntities = new ArrayList<Entity>();
    private final List<ItemStack> grabbedItems = new ArrayList<ItemStack>();

    /** Client Side Object Storage */
    public EntityItem renderEntityItem = null;

    public TileEntityArmbot()
    {
        super(.02f);
    }

    @Override
    public void initiate()
    {
        super.initiate();
        if (!this.commandManager.hasTasks())
        {
            this.onInventoryChanged();
        }
    }

    @Override
    //TODO separate out functions of this method to make it easier to read and work with
    public void updateEntity()
    {
        super.updateEntity();
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

        if (this.isFunctioning())
        {
            if (!this.worldObj.isRemote)
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
            {
                this.commandManager.onUpdate();
            }
        }
        else
        {
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
        if (Math.abs(this.actualYaw - this.rotationYaw) > 0.001f)
        {
            float speedYaw;
            if (this.actualYaw > this.rotationYaw)
            {
                if (Math.abs(this.actualYaw - this.rotationYaw) >= 180)
                {
                    speedYaw = this.ROTATION_SPEED;
                }
                else
                {
                    speedYaw = -this.ROTATION_SPEED;
                }
            }
            else
            {
                if (Math.abs(this.actualYaw - this.rotationYaw) >= 180)
                {
                    speedYaw = -this.ROTATION_SPEED;
                }
                else
                {
                    speedYaw = this.ROTATION_SPEED;
                }
            }

            this.actualYaw += speedYaw;

            this.rotationYaw = MathHelper.clampAngleTo360(this.rotationYaw);

            if (this.ticks % 5 == 0 && FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT)
            {
                // sound is 0.25 seconds long (20 ticks/second)
                this.worldObj.playSound(this.xCoord, this.yCoord, this.zCoord, "mods.assemblyline.conveyor", 0.4f, 1.7f, true);
            }

            if (Math.abs(this.actualYaw - this.rotationYaw) < this.ROTATION_SPEED + 0.1f)
            {
                this.actualYaw = this.rotationYaw;
            }

            for (Entity e : (ArrayList<Entity>) this.worldObj.getEntitiesWithinAABB(Entity.class, AxisAlignedBB.getBoundingBox(this.xCoord, this.yCoord + 2, this.zCoord, this.xCoord + 1, this.yCoord + 3, this.zCoord + 1)))
            {
                e.rotationYaw = this.actualYaw;
            }
        }

        if (Math.abs(this.actualPitch - this.rotationPitch) > 0.001f)
        {
            float speedPitch;
            if (this.actualPitch > this.rotationPitch)
            {
                speedPitch = -this.ROTATION_SPEED;
            }
            else
            {
                speedPitch = this.ROTATION_SPEED;
            }

            this.actualPitch += speedPitch;

            this.rotationPitch = MathHelper.clampAngle(this.rotationPitch, 0, 60);

            if (this.ticks % 4 == 0 && this.worldObj.isRemote)
            {
                this.worldObj.playSound(this.xCoord, this.yCoord, this.zCoord, "mods.assemblyline.conveyor", 2f, 2.5f, true);
            }

            if (Math.abs(this.actualPitch - this.rotationPitch) < this.ROTATION_SPEED + 0.1f)
            {
                this.actualPitch = this.rotationPitch;
            }

            for (Entity e : (ArrayList<Entity>) this.worldObj.getEntitiesWithinAABB(Entity.class, AxisAlignedBB.getBoundingBox(this.xCoord, this.yCoord + 2, this.zCoord, this.xCoord + 1, this.yCoord + 3, this.zCoord + 1)))
            {
                e.rotationPitch = this.actualPitch;
            }
        }

        this.rotationYaw = MathHelper.clampAngleTo360(this.rotationYaw);
        this.rotationPitch = MathHelper.clampAngle(this.rotationPitch, 0, 60);

        //TODO reduce this to an event based system were it only updates the client when something changes
        if (!this.worldObj.isRemote && this.ticks % 20 == 0)
        {
            this.sendRotationPacket();
        }
    }

    public Command getCurrentCommand()
    {
        if (this.commandManager.hasTasks() && this.commandManager.getCurrentTask() >= 0 && this.commandManager.getCurrentTask() < this.commandManager.getCommands().size())
        {
            return this.commandManager.getCommands().get(this.commandManager.getCurrentTask());
        }
        return null;
    }

    /** @return The current hand position of the armbot. */
    public Vector3 getHandPosition()
    {
        Vector3 position = new Vector3(this);
        position.translate(0.5);
        position.translate(this.getDeltaHandPosition());
        return position;
    }

    public Vector3 getDeltaHandPosition()
    {
        // The distance of the position relative to the main position.
        double distance = 1f;
        Vector3 delta = new Vector3();
        // The delta Y of the hand.
        delta.y = Math.sin(Math.toRadians(this.actualPitch)) * distance * 2;
        // The horizontal delta of the hand.
        double dH = Math.cos(Math.toRadians(this.actualPitch)) * distance;
        // The delta X and Z.
        delta.x = Math.sin(Math.toRadians(-this.actualYaw)) * dH;
        delta.z = Math.cos(Math.toRadians(-this.actualYaw)) * dH;
        return delta;
    }



    @Override
    public String getInvName()
    {
        return TranslationHelper.getLocal("tile.armbot.name");
    }



    public String getCommandDisplayText()
    {
        return this.displayText;
    }

    /** NBT Data */
    @Override
    public void readFromNBT(NBTTagCompound nbt)
    {
        super.readFromNBT(nbt);

        NBTTagCompound diskNBT = nbt.getCompoundTag("disk");
        ItemStack disk = null;
        if (diskNBT != null)
        {
            disk = ItemStack.loadItemStackFromNBT(diskNBT);
        }

        this.rotationYaw = nbt.getFloat("yaw");
        this.rotationPitch = nbt.getFloat("pitch");

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

    /** Writes a tile entity to NBT. */
    @Override
    public void writeToNBT(NBTTagCompound nbt)
    {
        super.writeToNBT(nbt);

        nbt.setFloat("yaw", this.rotationYaw);
        nbt.setFloat("pitch", this.rotationPitch);

        nbt.setInteger("curTask", this.commandManager.getCurrentTask());

        NBTTagList entities = new NBTTagList();

        for (Entity entity : grabbedEntities)
        {
            if (entity != null)
            {
                NBTTagCompound entityNBT = new NBTTagCompound();
                entity.writeToNBT(entityNBT);
                entity.writeToNBTOptional(entityNBT);
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
    public Packet getDescriptionPacket()
    {
        return PacketHandler.instance().getPacket(this.getChannel(), this, "armbot", this.functioning, this.rotationYaw, this.rotationPitch);
    }

    public void sendRotationPacket()
    {
        PacketHandler.instance().sendPacketToClients(PacketHandler.instance().getPacket(this.getChannel(), "arbotRotation", this.rotationYaw, this.rotationPitch), worldObj, new Vector3(this).translate(new Vector3(.5f, 1f, .5f)), 40);
    }

    @Override
    public boolean simplePacket(String id, ByteArrayDataInput dis, Player player)
    {
        try
        {
            if (this.worldObj.isRemote && !super.simplePacket(id, dis, player))
            {
                if (id.equalsIgnoreCase("armbot"))
                {
                    this.functioning = dis.readBoolean();
                    this.rotationYaw = dis.readFloat();
                    this.rotationPitch = dis.readFloat();
                    return true;
                }
                else if (id.equalsIgnoreCase("arbotRotation"))
                {
                    this.rotationYaw = dis.readFloat();
                    this.rotationPitch = dis.readFloat();
                    return true;
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return false;
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

    public void addCommand(Class<? extends Command> command)
    {
        this.commandManager.addCommand(this, command);
        if (FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER)
        {
            PacketHandler.instance().sendPacketToClients(this.getDescriptionPacket(), this.worldObj, new Vector3(this), 50);
        }
    }

    public void addCommand(Class<? extends Command> command, String[] parameters)
    {
        this.commandManager.addCommand(this, command, parameters);
        if (FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER)
        {
            PacketHandler.instance().sendPacketToClients(this.getDescriptionPacket(), this.worldObj, new Vector3(this), 50);
        }
    }

    @Override
    public void onCreate(Vector3 placedPosition)
    {
        if (DarkMain.blockMulti instanceof BlockMulti)
        {
            DarkMain.blockMulti.makeFakeBlock(this.worldObj, Vector3.translate(placedPosition, new Vector3(0, 1, 0)), placedPosition);
        }
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
    public Object[] callMethod(IComputerAccess computer, ILuaContext context, int method, Object[] arguments) throws Exception
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
    public void drop(Object object)
    {
        if (object instanceof Entity)
        {
            this.grabbedEntities.remove(object);
        }
        if (object instanceof ItemStack)
        {
            Vector3 handPosition = this.getHandPosition();
            ItemWorldHelper.dropItemStack(worldObj, handPosition, (ItemStack) object, false);
            this.grabbedItems.remove(object);
        }
        if (object instanceof String)
        {
            String string = ((String) object).toLowerCase();
            if (string.equalsIgnoreCase("all"))
            {
                Vector3 handPosition = this.getHandPosition();
                Iterator<ItemStack> it = this.grabbedItems.iterator();

                while (it.hasNext())
                {
                    ItemWorldHelper.dropItemStack(worldObj, handPosition, it.next(), false);
                }

                this.grabbedEntities.clear();
                this.grabbedItems.clear();
            }
        }
    }

    /** called by the block when another checks it too see if it is providing power to a direction */
    public boolean isProvidingPowerSide(ForgeDirection dir)
    {
        return this.isProvidingPower && dir.getOpposite() == this.getFacingDirectionFromAngle();
    }

    /** gets the facing direction using the yaw angle */
    public ForgeDirection getFacingDirectionFromAngle()
    {
        float angle = net.minecraft.util.MathHelper.wrapAngleTo180_float(this.rotationYaw);
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
    public double getWattLoad()
    {
        if (this.getCurrentCommand() != null)
        {
            return .4;//400w
        }
        return .03;//30w
    }

    @Override
    public boolean isItemValidForSlot(int i, ItemStack itemstack)
    {
        return itemstack != null && itemstack.itemID == AssemblyLine.recipeLoader.itemDisk.itemID;
    }
}
