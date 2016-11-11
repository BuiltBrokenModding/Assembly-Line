package com.builtbroken.assemblyline.content.inserter;

import com.builtbroken.jlib.data.vector.IPos3D;
import com.builtbroken.mc.api.automation.IAutomatedCrafter;
import com.builtbroken.mc.api.automation.IAutomation;
import com.builtbroken.mc.api.tile.multiblock.IMultiTile;
import com.builtbroken.mc.api.tile.multiblock.IMultiTileHost;
import com.builtbroken.mc.core.Engine;
import com.builtbroken.mc.core.network.IPacketIDReceiver;
import com.builtbroken.mc.core.registry.implement.IPostInit;
import com.builtbroken.mc.lib.transform.region.Cube;
import com.builtbroken.mc.lib.transform.rotation.EulerAngle;
import com.builtbroken.mc.lib.transform.vector.Location;
import com.builtbroken.mc.lib.transform.vector.Pos;
import com.builtbroken.mc.prefab.inventory.InventoryUtility;
import com.builtbroken.mc.prefab.tile.Tile;
import com.builtbroken.mc.prefab.tile.TileModuleMachine;
import com.builtbroken.mc.prefab.tile.multiblock.EnumMultiblock;
import com.builtbroken.mc.prefab.tile.multiblock.MultiBlockHelper;
import cpw.mods.fml.common.network.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentText;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.HashMap;
import java.util.Map;

/**
 * Robotic arm that inserts stuff into boxes
 *
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 11/9/2016.
 */
public class TileInsertArm extends TileModuleMachine implements IAutomation, IMultiTileHost, IPostInit, IPacketIDReceiver
{
    public static final int MAX_SPEED_UPGRADES = 18;
    /** Speed at which the arm rotates in degrees per tick */
    public static final float DEFAULT_SPEED = 1f;
    /** Multi-block cache */
    public static final HashMap<IPos3D, String> tileMapCache = new HashMap();

    static
    {
        tileMapCache.put(new Pos(0, 1, 0), EnumMultiblock.TILE.getName());
    }

    //Facing directions is output direction
    /** Rotation of the base of the arm */
    protected EulerAngle rotation = new EulerAngle(0, 0);

    /** Stack size to pickup/insert into machines */
    protected int insertAmount = 1;

    /** Are we destroying the structure */
    private boolean _destroyingStructure = false;

    protected static final Cube blockBounds = new Cube(0, 0, 0, 1, .3, 1);

    public TileInsertArm()
    {
        super("tileInsertArm", Material.iron);
        this.addInventoryModule(2);
        this.hardness = 1;
        this.resistance = 1;
        this.renderTileEntity = true;
    }

    @Override
    public void onPostInit()
    {
        //TODO add recipe
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
        if (isServer())
        {
            if (hasPower())
            {
                if (isFacingInput() && getHeldItem() == null)
                {
                    takeItem();
                }
                else if (isFacingOutput() && getHeldItem() != null)
                {
                    insertItem();
                }
                else
                {
                    updateRotation();
                }
            }
            else
            {
                dropItem();
            }
        }
    }

    @Override
    protected boolean onPlayerRightClickWrench(EntityPlayer player, int side, Pos hit)
    {
        if (isServer())
        {
            if (getDirection() == ForgeDirection.NORTH)
            {
                facing = ForgeDirection.EAST;
            }
            else if (getDirection() == ForgeDirection.EAST)
            {
                facing = ForgeDirection.SOUTH;
            }
            else if (getDirection() == ForgeDirection.SOUTH)
            {
                facing = ForgeDirection.WEST;
            }
            else if (getDirection() == ForgeDirection.WEST)
            {
                facing = ForgeDirection.NORTH;
            }
            if (isServer())
            {
                player.addChatComponentMessage(new ChatComponentText("Rotation set to " + getDirection().toString().toLowerCase()));
            }
            sendDescPacket();
        }
        return true;
    }

    @Override
    protected boolean onPlayerRightClick(EntityPlayer player, int side, Pos hit)
    {
        if (player.getHeldItem() != null)
        {
            if (Engine.runningAsDev && player.getHeldItem().getItem() == Items.stick)
            {
                if (isServer())
                {
                    player.addChatComponentMessage(new ChatComponentText("Output: " + getDirection() + " R: " + rotation));
                }
                return true;
            }
            else if (player.getHeldItem().getItem() == Items.redstone)
            {
                if (isServer())
                {
                    ItemStack stack = getStackInSlot(1);
                    if (stack == null)
                    {
                        stack = new ItemStack(Items.redstone);
                        setInventorySlotContents(1, stack);
                        player.addChatComponentMessage(new ChatComponentText("Upgrades: " + getSpeedUpdates()));
                        player.inventory.decrStackSize(player.inventory.currentItem, 1);
                        player.inventoryContainer.detectAndSendChanges();
                    }
                    else if (stack.stackSize < MAX_SPEED_UPGRADES)
                    {
                        stack.stackSize += 1;
                        setInventorySlotContents(1, stack);
                        player.addChatComponentMessage(new ChatComponentText("Upgrades: " + getSpeedUpdates()));
                        player.inventory.decrStackSize(player.inventory.currentItem, 1);
                        player.inventoryContainer.detectAndSendChanges();
                    }
                    else
                    {
                        player.addChatComponentMessage(new ChatComponentText("Max updates of " + MAX_SPEED_UPGRADES + " has been reached"));
                    }
                }
                return true;
            }
        }
        return false;
    }

    /**
     * Updates the rotation
     */
    protected void updateRotation()
    {
        int desiredRotation = getRotation(getHeldItem() == null ? getDirection().getOpposite() : getDirection());
        rotation.moveYaw(desiredRotation, DEFAULT_SPEED + DEFAULT_SPEED * getSpeedUpdates(), 1);
        sendDescPacket();
    }

    /**
     * Called to take an item from the input direction
     */
    protected void takeItem()
    {
        TileEntity input = findInput();
        if (input instanceof IInventory)
        {
            setHeldItem(InventoryUtility.takeTopItemFromInventory((IInventory) input, getDirection().ordinal(), insertAmount));
        }
    }

    /**
     * Called to inser the item into the output inventory
     */
    protected void insertItem()
    {
        TileEntity output = findOutput();
        if (output instanceof IAutomatedCrafter)
        {
            //TODO add rendered error responses when items can be stored (Ex. invalid filter, no room)
            if (((IAutomatedCrafter) output).canStore(getHeldItem(), getDirection().getOpposite()))
            {
                setHeldItem(((IAutomatedCrafter) output).insertRequiredItem(getHeldItem(), this));
            }
        }
        else if (output instanceof IInventory)
        {
            setHeldItem(InventoryUtility.putStackInInventory((IInventory) output, getHeldItem(), getDirection().getOpposite().ordinal(), false));
        }
    }

    /**
     * Called to drop the item
     */
    protected void dropItem()
    {
        //TODO drop where the hand actually is
        Location location = toLocation().add(rotation.toPos());
        InventoryUtility.dropItemStack(location, getHeldItem());
        setHeldItem(null);
    }

    /**
     * Checks if the machine has power to work
     *
     * @return true if it has enough for one more tick
     */
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
        return isFacing(getDirection().getOpposite());
    }

    /**
     * Checks if the arm is within an acceptable rotation to
     * access the output tile.
     *
     * @return true if yes
     */
    protected boolean isFacingOutput()
    {
        return isFacing(getDirection());
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
     * Number of installed speed upgrades
     *
     * @return # upgrades
     */
    protected int getSpeedUpdates()
    {
        ItemStack stack = getStackInSlot(1);
        return stack != null ? stack.stackSize : 0;
    }

    /**
     * Finds the input tile
     *
     * @return the tile
     */
    protected TileEntity findInput()
    {
        return toLocation().add(getDirection().getOpposite()).getTileEntity();
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

    @Override
    public void onInventoryChanged(int slot, ItemStack prev, ItemStack item)
    {
        sendDescPacket();
    }

    @Override
    public void firstTick()
    {
        super.firstTick();
        MultiBlockHelper.buildMultiBlock(world(), this, true);
    }

    @Override
    public void onMultiTileAdded(IMultiTile tileMulti)
    {
        if (tileMulti instanceof TileEntity)
        {
            if (tileMapCache.containsKey(new Pos(this).sub(new Pos((TileEntity) tileMulti))))
            {
                tileMulti.setHost(this);
            }
        }
    }

    @Override
    public boolean onMultiTileBroken(IMultiTile tileMulti, Object source, boolean harvest)
    {
        if (!_destroyingStructure && tileMulti instanceof TileEntity)
        {
            Pos pos = new Pos((TileEntity) tileMulti).sub(new Pos(this));

            if (tileMapCache.containsKey(pos))
            {
                MultiBlockHelper.destroyMultiBlockStructure(this, harvest, false, true);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean canPlaceBlockOnSide(ForgeDirection side)
    {
        return side == ForgeDirection.UP;
    }

    @Override
    public boolean removeByPlayer(EntityPlayer player, boolean willHarvest)
    {
        MultiBlockHelper.destroyMultiBlockStructure(this, false, false, false);
        if (willHarvest && getHeldItem() != null)
        {
            InventoryUtility.dropItemStack(toLocation(), getHeldItem());
            setInventorySlotContents(0, null);
        }
        return super.removeByPlayer(player, willHarvest);
    }

    @Override
    public void onTileInvalidate(IMultiTile tileMulti)
    {

    }

    @Override
    public boolean onMultiTileActivated(IMultiTile tile, EntityPlayer player, int side, IPos3D hit)
    {
        return this.onPlayerRightClick(player, side, new Pos(hit));
    }

    @Override
    public void onMultiTileClicked(IMultiTile tile, EntityPlayer player)
    {

    }

    @Override
    public HashMap<IPos3D, String> getLayoutOfMultiBlock()
    {
        HashMap<IPos3D, String> map = new HashMap();
        Pos center = new Pos(this);
        for (Map.Entry<IPos3D, String> entry : tileMapCache.entrySet())
        {
            map.put(center.add(entry.getKey()), entry.getValue());
        }
        return map;
    }

    @Override
    public Cube getBlockBounds()
    {
        return blockBounds;
    }

    @Override
    public Cube getSelectBounds()
    {
        return blockBounds;
    }
}
