package com.builtbroken.assemblyline.content.inserter;

import com.builtbroken.mc.lib.transform.rotation.EulerAngle;
import com.builtbroken.mc.prefab.tile.Tile;
import com.builtbroken.mc.prefab.tile.TileModuleMachine;
import cpw.mods.fml.common.network.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import net.minecraft.block.material.Material;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;

/**
 * Robotic arm that inserts stuff into boxes
 *
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 11/9/2016.
 */
public class TileInsertArm extends TileModuleMachine
{
    public static final float rotationSpeed = 360 / 60; //Angle / ticks
    //Facing directions is output direction
    /** Direction to take items from */
    protected ForgeDirection inputDirection = facing.getOpposite();

    /** Rotation of the base of the arm */
    protected EulerAngle rotation = new EulerAngle(0, 0);

    public TileInsertArm()
    {
        super("tileInsertArm", Material.iron);
    }

    @Override
    public Tile newTile()
    {
        return new TileInsertArm();
    }

    @Override
    public void update()
    {
        super.update();
        if (hasPower())
        {
            if (isFacingInput() && getHeldItem() == null)
            {
                takeItem();
            }
            else if (isFacingOutput())
            {
                insertItem();
            }
            else
            {
                updateRotation();
            }
        }
    }

    /**
     * Updates the rotation
     */
    protected void updateRotation()
    {
        int desiredRotation = getRotation(getHeldItem() == null ? inputDirection : facing);
        rotation.moveYaw(desiredRotation, rotationSpeed, 1);
        sendDescPacket();
    }

    protected void takeItem()
    {
        TileEntity input = findInput();
    }

    protected void insertItem()
    {
        TileEntity output = findOutput();
    }

    protected void dropItem()
    {

    }

    protected boolean hasPower()
    {
        return true;
    }

    /**
     * Checks if the arm is within an acceptable rotation to
     * access the input tile.
     *
     * @return true if yes
     */
    protected boolean isFacingInput()
    {
        return isFacing(inputDirection);
    }

    /**
     * Checks if the arm is within an acceptable rotation to
     * access the output tile.
     *
     * @return true if yes
     */
    protected boolean isFacingOutput()
    {
        return isFacing(facing);
    }

    /**
     * Checks if the arm is within an acceptable rotation
     * to the facing direction.
     *
     * @param dir - direction to face
     * @return true if yes
     */
    protected boolean isFacing(ForgeDirection dir)
    {
        return rotation.isYawWithin(getRotation(dir), 3);
    }

    /**
     * Gets the rotation for the direction
     *
     * @param dir - direction to face
     * @return rotation value i 90 degree slices
     */
    protected int getRotation(ForgeDirection dir)
    {
        switch (dir)
        {
            case SOUTH:
                return 180;
            case EAST:
                return 90;
            case WEST:
                return -90;
            default:
                return 0;
        }
    }


    /**
     * Gets the held item
     *
     * @return item or null if none
     */
    protected ItemStack getHeldItem()
    {
        return getStackInSlot(0);
    }

    /**
     * Sets the held item
     *
     * @param stack - stack, can be null
     */
    protected void setHeldItem(ItemStack stack)
    {
        this.setInventorySlotContents(0, stack);
    }

    /**
     * Finds the input tile
     *
     * @return the tile
     */
    protected TileEntity findInput()
    {
        return toLocation().add(getDirection()).getTileEntity();
    }

    /**
     * Finds the output tile in the facing direction
     *
     * @return the tile
     */
    protected TileEntity findOutput()
    {
        return toLocation().add(getDirection()).getTileEntity();
    }

    @Override
    public void writeDescPacket(ByteBuf buf)
    {
        super.writeDescPacket(buf);
        rotation.writeBytes(buf);
        buf.writeBoolean(getHeldItem() != null);
        if (getHeldItem() != null)
        {
            ByteBufUtils.writeItemStack(buf, getHeldItem());
        }
    }
}
