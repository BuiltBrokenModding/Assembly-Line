package com.builtbroken.assemblyline.armbot;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.packet.Packet;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import universalelectricity.api.vector.Vector2;
import universalelectricity.api.vector.Vector3;

import com.builtbroken.assemblyline.ALRecipeLoader;
import com.builtbroken.assemblyline.api.IArmbot;
import com.builtbroken.assemblyline.api.coding.IProgram;
import com.builtbroken.assemblyline.api.coding.ProgramHelper;
import com.builtbroken.assemblyline.machine.TileEntityAssembly;
import com.builtbroken.assemblyline.machine.encoder.ItemDisk;
import com.builtbroken.common.Pair;
import com.builtbroken.minecraft.DarkCore;
import com.builtbroken.minecraft.TranslationHelper;
import com.builtbroken.minecraft.helpers.DarksHelper;
import com.builtbroken.minecraft.helpers.MathHelper;
import com.builtbroken.minecraft.interfaces.IMultiBlock;
import com.builtbroken.minecraft.network.PacketHandler;
import com.builtbroken.minecraft.prefab.BlockMulti;
import com.google.common.io.ByteArrayDataInput;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.Player;
import cpw.mods.fml.relauncher.Side;
import dan200.computer.api.IComputerAccess;
import dan200.computer.api.ILuaContext;
import dan200.computer.api.IPeripheral;

public class TileEntityArmbot extends TileEntityAssembly implements IMultiBlock, IArmbot, IPeripheral
{
    protected final float ROTATION_SPEED = 2.0f;

    /** The rotation of the arms. In Degrees. */
    protected float rotationPitch = 0, rotationYaw = 0;
    protected float actualPitch = 0, actualYaw = 0;

    protected boolean hasTask = false;
    protected boolean spawnEntity = false;

    protected String displayText = "";

    /** An entity that the Armbot is grabbed onto. Entity Items are held separately. */
    protected Object grabbedObject = null;

    protected List<IComputerAccess> connectedComputers = new ArrayList<IComputerAccess>();

    protected ProgramHelper programHelper;

    protected Pair<World, Vector3> location;

    public EntityItem renderEntityItem;

    public TileEntityArmbot()
    {
        super(20);
        programHelper = new ProgramHelper(this).setMemoryLimit(20);
    }

    @Override
    public void updateEntity()
    {
        super.updateEntity();
        Vector3 handPosition = this.getHandPos();
        if (this.location == null || !this.location.left().equals(this.worldObj) || this.xCoord != this.location.right().intX() || this.yCoord != this.location.right().intY() || this.zCoord != this.location.right().intZ())
        {
            this.location = new Pair<World, Vector3>(this.worldObj, new Vector3(this));
        }
        if (this.grabbedObject instanceof Entity)
        {
            if (this.spawnEntity)
            {
                this.worldObj.spawnEntityInWorld((Entity) this.grabbedObject);
                this.spawnEntity = false;
            }
            ((Entity) this.grabbedObject).setPosition(handPosition.x, handPosition.y, handPosition.z);
            ((Entity) this.grabbedObject).motionX = 0;
            ((Entity) this.grabbedObject).motionY = 0;
            ((Entity) this.grabbedObject).motionZ = 0;

            if (this.grabbedObject instanceof EntityItem)
            {
                ((EntityItem) this.grabbedObject).delayBeforeCanPickup = 20;
                ((EntityItem) this.grabbedObject).age = 0;
            }
        }

        if (this.isFunctioning())
        {
            if (!this.worldObj.isRemote)
            {
                float preYaw = this.rotationYaw, prePitch = this.rotationPitch;
                this.updateLogic();
                if (this.rotationYaw != preYaw || this.rotationPitch != prePitch)
                {
                    this.sendRotationPacket();
                }
            }
            this.updateRotation();
        }
    }

    public void updateLogic()
    {
        if (this.programHelper == null)
        {
            this.programHelper = new ProgramHelper(this);
        }
        this.programHelper.onUpdate(this.worldObj, new Vector3(this));
    }

    public void updateRotation()
    {
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

        this.rotationYaw = nbt.getFloat("yaw");
        this.rotationPitch = nbt.getFloat("pitch");

        if (nbt.hasKey("grabbedEntity"))
        {
            NBTTagCompound tag = nbt.getCompoundTag("grabbedEntity");
            Entity entity = EntityList.createEntityFromNBT(tag, worldObj);
            if (entity != null)
            {
                this.grabbedObject = entity;
            }
        }
        else if (nbt.hasKey("grabbedItem"))
        {
            ItemStack stack = ItemStack.loadItemStackFromNBT(nbt.getCompoundTag("grabbedItem"));
            if (stack != null)
            {
                this.grabbedObject = stack;
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

        if (this.grabbedObject instanceof Entity)
        {
            NBTTagCompound entityNBT = new NBTTagCompound();
            ((Entity) this.grabbedObject).writeToNBT(entityNBT);
            ((Entity) this.grabbedObject).writeToNBTOptional(entityNBT);
            nbt.setCompoundTag("grabbedEntity", entityNBT);
        }
        else if (this.grabbedObject instanceof ItemStack)
        {
            NBTTagCompound itemTag = new NBTTagCompound();
            ((Entity) this.grabbedObject).writeToNBT(itemTag);
            nbt.setCompoundTag("grabbedItem", itemTag);
        }

    }

    @Override
    public Packet getDescriptionPacket()
    {
        return PacketHandler.instance().getTilePacket(this.getChannel(), "armbot", this, this.functioning, this.rotationYaw, this.rotationPitch);
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

    @Override
    public void onCreate(Vector3 placedPosition)
    {
        if (DarkCore.multiBlock instanceof BlockMulti)
        {
            DarkCore.multiBlock.makeFakeBlock(this.worldObj, Vector3.translate(placedPosition, new Vector3(0, 1, 0)), placedPosition);
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
        synchronized (connectedComputers)
        {
            connectedComputers.add(computer);
        }
    }

    @Override
    public void detach(IComputerAccess computer)
    {
        synchronized (connectedComputers)
        {
            connectedComputers.remove(computer);
        }
    }

    @Override
    public Object getGrabbedObject()
    {
        return this.grabbedObject;
    }

    @Override
    public boolean grab(Object entity)
    {
        if (entity instanceof ItemStack)
        {
            this.grabbedObject = entity;
            return true;
        }
        else if (entity instanceof EntityItem)
        {
            this.grabbedObject = ((EntityItem) entity).getEntityItem();
            ((EntityItem) entity).setDead();
            return true;
        }
        else if (entity instanceof Entity)
        {
            this.grabbedObject = entity;
            return true;
        }
        return false;
    }

    @Override
    public boolean drop(Object object)
    {
        if (object != null)
        {
            boolean drop = object instanceof String && ((String) object).equalsIgnoreCase("all");

            if (object.equals(this.grabbedObject) || drop)
            {
                if (object instanceof ItemStack && this.grabbedObject instanceof ItemStack)
                {
                    Vector3 handPosition = this.getHandPos();
                    DarksHelper.dropItemStack(worldObj, handPosition, (ItemStack) object, false);
                }
                this.grabbedObject = null;
                return true;
            }

        }
        return false;
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
        if (this.hasTask)
        {
            return .4;//400w
        }
        return .03;//30w
    }

    @Override
    public boolean isItemValidForSlot(int i, ItemStack itemstack)
    {
        return itemstack != null && itemstack.itemID == ALRecipeLoader.itemDisk.itemID;
    }

    @Override
    public Vector3 getHandPos()
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
    public Vector2 getRotation()
    {
        return new Vector2(this.actualYaw, this.actualPitch);
    }

    @Override
    public void setRotation(float yaw, float pitch)
    {
        if (!this.worldObj.isRemote)
        {
            this.actualYaw = yaw;
            this.actualPitch = pitch;
        }

    }

    @Override
    public boolean moveArmTo(float yaw, float pitch)
    {
        if (!this.worldObj.isRemote)
        {
            this.rotationYaw = yaw;
            this.rotationPitch = pitch;
            return true;
        }
        return false;
    }

    @Override
    public boolean moveTo(ForgeDirection direction)
    {
        if (direction == ForgeDirection.SOUTH)
        {
            this.rotationYaw = 0;
            return true;
        }
        else if (direction == ForgeDirection.EAST)
        {
            this.rotationYaw = 90;
            return true;
        }
        else if (direction == ForgeDirection.NORTH)
        {

            this.rotationYaw = 180;
            return true;
        }
        else if (direction == ForgeDirection.WEST)
        {
            this.rotationYaw = 270;
            return true;
        }
        return false;
    }

    @Override
    public IProgram getCurrentProgram()
    {
        if (this.programHelper == null)
        {
            this.programHelper = new ProgramHelper(this);
        }
        if (this.programHelper != null)
        {
            return this.programHelper.getProgram();
        }
        return null;
    }

    @Override
    public void setCurrentProgram(IProgram program)
    {
        if (this.programHelper == null)
        {
            this.programHelper = new ProgramHelper(this);
        }
        if (this.programHelper != null)
        {
            this.programHelper.setProgram(program);
        }
    }

    @Override
    public boolean clear(Object object)
    {
        if (this.grabbedObject != null && this.grabbedObject.equals(object))
        {
            this.grabbedObject = null;
            return true;
        }
        return false;
    }

    @Override
    public Pair<World, Vector3> getLocation()
    {
        return this.location;
    }
}
