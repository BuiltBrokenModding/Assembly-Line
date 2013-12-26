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
import com.builtbroken.assemblyline.armbot.command.TaskDrop;
import com.builtbroken.assemblyline.armbot.command.TaskGOTO;
import com.builtbroken.assemblyline.armbot.command.TaskGrabItem;
import com.builtbroken.assemblyline.armbot.command.TaskReturn;
import com.builtbroken.assemblyline.armbot.command.TaskRotateBy;
import com.builtbroken.assemblyline.armbot.command.TaskRotateTo;
import com.builtbroken.assemblyline.machine.TileEntityAssembly;
import com.builtbroken.assemblyline.machine.encoder.ItemDisk;
import com.builtbroken.common.Pair;
import com.builtbroken.minecraft.DarkCore;
import com.builtbroken.minecraft.TranslationHelper;
import com.builtbroken.minecraft.helpers.DarksHelper;
import com.builtbroken.minecraft.helpers.MathHelper;
import com.builtbroken.minecraft.interfaces.IMultiBlock;
import com.builtbroken.minecraft.network.PacketHandler;
import com.google.common.io.ByteArrayDataInput;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.Player;
import cpw.mods.fml.relauncher.Side;
import dan200.computer.api.IComputerAccess;
import dan200.computer.api.ILuaContext;
import dan200.computer.api.IPeripheral;

public class TileEntityArmbot extends TileEntityAssembly implements IMultiBlock, IArmbot
{
    protected int ROTATION_SPEED = 2;

    /** The rotation of the arms. In Degrees. */
    protected int rotationPitch = 0, rotationYaw = 0;
    protected int actualPitch = 0, actualYaw = 0;

    protected boolean spawnEntity = false;

    protected String displayText = "";

    /** An entity that the Armbot is grabbed onto. Entity Items are held separately. */
    protected Object grabbedObject = null;
    /** Helper class that does all the logic for the armbot's program */
    protected ProgramHelper programHelper;
    /** Cached location of the armbot to feed to program tasks */
    protected Pair<World, Vector3> location;
    /** Var used by the armbot renderer */
    public EntityItem renderEntityItem;

    public TileEntityArmbot()
    {
        super(20);
        programHelper = new ProgramHelper(this).setMemoryLimit(20);
        Program program = new Program();
        program.setTaskAt(0, 0, new TaskDrop());
        program.setTaskAt(0, 1, new TaskRotateTo(180, 0));
        program.setTaskAt(0, 2, new TaskGrabItem());
        program.setTaskAt(0, 3, new TaskReturn());
        program.setTaskAt(0, 4, new TaskGOTO(0, 0));
        programHelper.setProgram(program);
    }

    /************************************ Armbot logic update methods *************************************/

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
            float preYaw = this.rotationYaw, prePitch = this.rotationPitch;
            if (!this.worldObj.isRemote && this.ticks % 3 == 0)
            {
                this.programHelper.onUpdate(this.worldObj, new Vector3(this));
                if (this.rotationYaw != preYaw || this.rotationPitch != prePitch)
                {
                    this.sendRotationPacket();
                }
            }
            this.updateRotation();
        }
    }

    public void updateRotation()
    {
        if (Math.abs(this.actualYaw - this.rotationYaw) > 1)
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

            this.rotationYaw = (int) MathHelper.clampAngleTo360(this.rotationYaw);

            if (Math.abs(this.actualYaw - this.rotationYaw) < this.ROTATION_SPEED)
            {
                this.actualYaw = this.rotationYaw;
            }
            this.playRotationSound();
        }

        if (Math.abs(this.actualPitch - this.rotationPitch) > 1)
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

            this.rotationPitch = (int) MathHelper.clampAngle(this.rotationPitch, 0, 60);

            if (Math.abs(this.actualPitch - this.rotationPitch) < this.ROTATION_SPEED)
            {
                this.actualPitch = this.rotationPitch;
            }
            this.playRotationSound();
        }

        this.rotationYaw = (int) MathHelper.clampAngleTo360(this.rotationYaw);
        this.rotationPitch = (int) MathHelper.clampAngle(this.rotationPitch, 0, 60);
    }

    public void playRotationSound()
    {
        if (this.ticks % 5 == 0 && this.worldObj.isRemote)
        {
            this.worldObj.playSound(this.xCoord, this.yCoord, this.zCoord, "mods.assemblyline.conveyor", 2f, 2.5f, true);
        }
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

    /************************************ Save and load code *************************************/

    /** NBT Data */
    @Override
    public void readFromNBT(NBTTagCompound nbt)
    {
        super.readFromNBT(nbt);

        this.rotationYaw = nbt.getInteger("armYaw");
        this.rotationPitch = nbt.getInteger("armPitch");
        this.actualYaw = nbt.getInteger("armYawActual");
        this.actualPitch = nbt.getInteger("armPitchActual");

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

        nbt.setInteger("armYaw", this.rotationYaw);
        nbt.setInteger("armPitch", this.rotationPitch);
        nbt.setInteger("armYawActual", this.actualYaw);
        nbt.setInteger("armPitchActual", this.actualPitch);

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

    /************************************ Network Packet code *************************************/

    @Override
    public Packet getDescriptionPacket()
    {
        return PacketHandler.instance().getTilePacket(this.getChannel(), "armbot", this, this.functioning, this.rotationYaw, this.rotationPitch);
    }

    public void sendRotationPacket()
    {
        PacketHandler.instance().sendPacketToClients(PacketHandler.instance().getPacket(this.getChannel(), "arbotRotation", this.rotationYaw, this.rotationPitch, this.actualYaw, this.actualPitch), worldObj, new Vector3(this).translate(new Vector3(.5f, 1f, .5f)), 40);
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
                    this.rotationYaw = dis.readInt();
                    this.rotationPitch = dis.readInt();
                    return true;
                }
                else if (id.equalsIgnoreCase("arbotRotation"))
                {
                    this.rotationYaw = dis.readInt();
                    this.rotationPitch = dis.readInt();
                    this.actualYaw = dis.readInt();
                    this.actualPitch = dis.readInt();
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

    /************************************ Multi Block code *************************************/

    @Override
    public void onCreate(Vector3 placedPosition)
    {
        DarkCore.multiBlock.makeFakeBlock(this.worldObj, Vector3.translate(placedPosition, new Vector3(0, 1, 0)), new Vector3(this));
    }

    @Override
    public void onDestroy(TileEntity callingBlock)
    {
        this.worldObj.setBlock(this.xCoord, this.yCoord, this.zCoord, 0, 0, 3);
        this.worldObj.setBlock(this.xCoord, this.yCoord + 1, this.zCoord, 0, 0, 3);
    }

    /************************************ Armbot API methods *************************************/
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
            this.actualYaw = (int) yaw;
            this.actualPitch = (int) pitch;
        }

    }

    @Override
    public boolean moveArmTo(float yaw, float pitch)
    {
        if (!this.worldObj.isRemote)
        {
            this.rotationYaw = (int) yaw;
            this.rotationPitch = (int) pitch;
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
