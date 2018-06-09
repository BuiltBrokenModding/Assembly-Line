package com.builtbroken.assemblyline.content.inserter;

import com.builtbroken.assemblyline.AssemblyLine;
import com.builtbroken.assemblyline.api.IInserterAccess;
import com.builtbroken.assemblyline.content.parts.ALParts;
import com.builtbroken.jlib.data.vector.IPos3D;
import com.builtbroken.mc.api.automation.IAutomatedCrafter;
import com.builtbroken.mc.api.automation.IAutomation;
import com.builtbroken.mc.api.tile.multiblock.IMultiTile;
import com.builtbroken.mc.api.tile.multiblock.IMultiTileHost;
import com.builtbroken.mc.api.tile.node.ITileNodeHost;
import com.builtbroken.mc.api.tile.provider.IInventoryProvider;
import com.builtbroken.mc.core.Engine;
import com.builtbroken.mc.core.network.IPacketIDReceiver;
import com.builtbroken.mc.core.registry.implement.IRecipeContainer;
import com.builtbroken.mc.framework.multiblock.EnumMultiblock;
import com.builtbroken.mc.framework.multiblock.MultiBlockHelper;
import com.builtbroken.mc.imp.transform.region.Cube;
import com.builtbroken.mc.imp.transform.rotation.EulerAngle;
import com.builtbroken.mc.imp.transform.vector.Location;
import com.builtbroken.mc.imp.transform.vector.Pos;
import com.builtbroken.mc.lib.helper.recipe.OreNames;
import com.builtbroken.mc.prefab.inventory.InventoryUtility;
import com.builtbroken.mc.prefab.tile.Tile;
import com.builtbroken.mc.prefab.tile.TileModuleMachine;
import com.builtbroken.mc.prefab.tile.module.TileModuleInventory;
import cpw.mods.fml.common.network.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentText;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Robotic arm that inserts stuff into boxes
 *
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 11/9/2016.
 */
public class TileInsertArm extends TileModuleMachine implements IAutomation, IMultiTileHost, IPacketIDReceiver, IRecipeContainer
{
    public static final int MAX_SPEED_UPGRADES = 18;
    /** Speed at which the arm rotates in degrees per tick */
    public static final float DEFAULT_SPEED = 1f;
    /** Multi-block cache */
    public static final HashMap<IPos3D, String> tileMapCache = new HashMap();

    static
    {
        tileMapCache.put(new Pos(0, 1, 0), EnumMultiblock.TILE.getTileName());
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
        this.hardness = 1;
        this.resistance = 1;
        this.renderTileEntity = true;
    }

    @Override
    protected IInventory createInventory()
    {
        return new TileModuleInventory(this, 2);
    }

    @Override
    public void genRecipes(final List<IRecipe> recipes)
    {
        recipes.add(newShapedRecipe(AssemblyLine.blockInserter, "HW", "AW", "CW", 'H', ALParts.ROBOTIC_HAND.toStack(), 'A', ALParts.ROBOTIC_ARM_ASSEMBLY.toStack(), 'C', ALParts.ROBOTIC_BASE.toStack(), 'W', OreNames.WIRE_COPPER));
    }

    @Override
    public Tile newTile()
    {
        return new TileInsertArm();
    }

    @Override
    public void firstTick()
    {
        super.firstTick();
        MultiBlockHelper.buildMultiBlock(oldWorld(), this, true);
        facing = ForgeDirection.getOrientation(getMetadata());
        markRender();
    }

    @Override
    public void update()
    {
        super.update();
        if (isServer())
        {
            if (hasPower())
            {
                boolean facingInput = isFacingInput();
                boolean facingOutput = isFacingOutput();
                boolean hasItem = getHeldItem() != null;
                if (facingInput && !hasItem)
                {
                    takeItem();
                }
                else if (facingOutput && hasItem)
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
                setFacing(ForgeDirection.EAST);
            }
            else if (getDirection() == ForgeDirection.EAST)
            {
                setFacing(ForgeDirection.SOUTH);
            }
            else if (getDirection() == ForgeDirection.SOUTH)
            {
                setFacing(ForgeDirection.WEST);
            }
            else
            {
                setFacing(ForgeDirection.NORTH);
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
        rotation.clampTo360();
        sendDescPacket();
    }

    /**
     * Called to take an item from the input direction
     */
    protected void takeItem()
    {
        if (getHeldItem() == null)
        {
            Object input = findInput();
            if (input instanceof IInserterAccess)
            {
                setHeldItem(((IInserterAccess) input).takeInserterItem(rotation, getDirection(), insertAmount, true));
            }
            else if (input instanceof IAutomatedCrafter)
            {
                final int[] slots = ((IAutomatedCrafter) input).getCraftingOutputSlots(this, getDirection());
                for (int index = 0; index < slots.length; index++)
                {
                    final ItemStack stack = ((IAutomatedCrafter) input).getInventory().getStackInSlot(slots[index]);
                    if (stack != null)
                    {
                        if (((IAutomatedCrafter) input).canRemove(stack, slots[index], getDirection()))
                        {
                            final ItemStack take = stack.copy();
                            take.stackSize = 1;
                            setHeldItem(take);

                            stack.stackSize--;
                            if (stack.stackSize <= 0)
                            {
                                ((IAutomatedCrafter) input).getInventory().setInventorySlotContents(slots[index], null);
                            }
                            else
                            {
                                ((IAutomatedCrafter) input).getInventory().setInventorySlotContents(slots[index], stack);
                            }
                        }
                    }
                }
            }
            else
            {
                IInventory inventory = toInventory(input);
                if (inventory != null)
                {
                    setHeldItem(InventoryUtility.takeTopItemFromInventory(inventory, getDirection().ordinal(), insertAmount));
                }
            }
        }
    }

    /**
     * Called to inser the item into the output inventory
     */
    protected void insertItem()
    {
        if (getHeldItem() != null)
        {
            Object output = findOutput();
            if (output instanceof IInserterAccess)
            {
                setHeldItem(((IInserterAccess) output).giveInserterItem(rotation, getDirection().getOpposite(), getHeldItem(), true));
            }
            else if (output instanceof IAutomatedCrafter)
            {
                int[] slots = ((IAutomatedCrafter) output).getCraftingInputSlots(this, getDirection().getOpposite());
                ItemStack heldItem = getHeldItem();
                final ForgeDirection side = getDirection();
                for (int index = 0; index < slots.length; index++)
                {
                    final int slot = slots[index];
                    //TODO add rendered error responses when items can be stored (Ex. invalid filter, no room)
                    if (((IAutomatedCrafter) output).canStore(heldItem, slot, side))
                    {
                        heldItem = ((IAutomatedCrafter) output).insertRequiredItem(heldItem, slot, this, side);
                        if (heldItem == null || heldItem.stackSize <= 0)
                        {
                            setHeldItem(null);
                            break;
                        }
                        else
                        {
                            setHeldItem(heldItem);
                        }
                    }
                }
            }
            else
            {
                IInventory inventory = toInventory(output);
                if (inventory != null)
                {
                    setHeldItem(InventoryUtility.putStackInInventory(inventory, getHeldItem(), getDirection().getOpposite().ordinal(), false));
                }
            }
        }
    }

    private IInventory toInventory(Object object)
    {
        if (object instanceof IInventory)
        {
            return (IInventory) object;
        }
        else if (object instanceof IInventoryProvider)
        {
            return ((IInventoryProvider) object).getInventory();
        }
        return null;
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
                return -90;
            case WEST:
                return 90;
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
    protected Object findInput()
    {
        Object tile = toLocation().add(getDirection().getOpposite()).getTileEntity();
        if (tile instanceof IMultiTile)
        {
            tile = (TileEntity) ((IMultiTile) tile).getHost();
        }

        if (tile instanceof ITileNodeHost)
        {
            tile = ((ITileNodeHost) tile).getTileNode();
        }
        return tile;
    }

    /**
     * Finds the output tile in the facing direction
     *
     * @return the tile
     */
    protected Object findOutput()
    {
        Object tile = toLocation().add(getDirection()).getTileEntity();
        if (tile instanceof IMultiTile)
        {
            tile = (TileEntity) ((IMultiTile) tile).getHost();
        }

        if (tile instanceof ITileNodeHost)
        {
            tile = ((ITileNodeHost) tile).getTileNode();
        }
        return tile;
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
    public void readFromNBT(NBTTagCompound nbt)
    {
        super.readFromNBT(nbt);
        rotation.readFromNBT(nbt.getCompoundTag("rotation"));
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt)
    {
        super.writeToNBT(nbt);
        nbt.setTag("rotation", rotation.writeNBT(new NBTTagCompound()));
    }

    @Override
    public void onInventoryChanged(int slot, ItemStack prev, ItemStack item)
    {
        if (oldWorld() != null)
        {
            sendDescPacket();
        }
    }

    @Override
    public void onMultiTileAdded(IMultiTile tileMulti)
    {
        if (tileMulti instanceof TileEntity)
        {
            if (tileMapCache.containsKey(new Pos((TileEntity) this).sub(new Pos((TileEntity) tileMulti))))
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
            Pos pos = new Pos((TileEntity) tileMulti).sub(new Pos((TileEntity) this));

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
    public boolean onMultiTileActivated(IMultiTile tile, EntityPlayer player, int side, float xHit, float yHit, float zHit)
    {
        return this.onPlayerRightClick(player, side, new Pos(xHit, yHit, zHit));
    }

    @Override
    public void onMultiTileClicked(IMultiTile tile, EntityPlayer player)
    {

    }

    @Override
    public HashMap<IPos3D, String> getLayoutOfMultiBlock()
    {
        HashMap<IPos3D, String> map = new HashMap();
        Pos center = new Pos((TileEntity) this);
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

    @Override
    public boolean useMetaForFacing()
    {
        return true;
    }
}
