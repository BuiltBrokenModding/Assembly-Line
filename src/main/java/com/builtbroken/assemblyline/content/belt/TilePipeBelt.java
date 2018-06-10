package com.builtbroken.assemblyline.content.belt;

import com.builtbroken.assemblyline.AssemblyLine;
import com.builtbroken.assemblyline.api.IInserterAccess;
import com.builtbroken.assemblyline.content.belt.gen.TileEntityWrappedPipeBelt;
import com.builtbroken.assemblyline.content.belt.pipe.BeltType;
import com.builtbroken.assemblyline.content.belt.pipe.PipeInventory;
import com.builtbroken.assemblyline.content.belt.pipe.data.BeltInventoryFilter;
import com.builtbroken.assemblyline.content.belt.pipe.data.BeltSideData;
import com.builtbroken.assemblyline.content.belt.pipe.data.BeltSideState;
import com.builtbroken.assemblyline.content.belt.pipe.data.BeltSideStateIterator;
import com.builtbroken.assemblyline.content.belt.pipe.gui.ContainerPipeBelt;
import com.builtbroken.assemblyline.content.belt.pipe.gui.GuiPipeBelt;
import com.builtbroken.jlib.type.Pair;
import com.builtbroken.mc.api.data.IPacket;
import com.builtbroken.mc.api.tile.ConnectionType;
import com.builtbroken.mc.api.tile.IRotatable;
import com.builtbroken.mc.api.tile.access.IGuiTile;
import com.builtbroken.mc.api.tile.connection.ConnectionColor;
import com.builtbroken.mc.api.tile.connection.IAdjustableConnections;
import com.builtbroken.mc.api.tile.provider.IInventoryProvider;
import com.builtbroken.mc.codegen.annotations.TileWrapped;
import com.builtbroken.mc.core.Engine;
import com.builtbroken.mc.core.network.packet.PacketType;
import com.builtbroken.mc.data.Direction;
import com.builtbroken.mc.framework.block.imp.IBlockStackListener;
import com.builtbroken.mc.framework.block.imp.IWrenchListener;
import com.builtbroken.mc.framework.logic.TileNode;
import com.builtbroken.mc.imp.transform.rotation.EulerAngle;
import com.builtbroken.mc.imp.transform.vector.Location;
import com.builtbroken.mc.prefab.inventory.BasicInventory;
import com.builtbroken.mc.prefab.inventory.InventoryUtility;
import cpw.mods.fml.common.network.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentText;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.HashMap;
import java.util.Iterator;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 11/14/2017.
 */
@TileWrapped(className = ".gen.TileEntityWrappedPipeBelt", wrappers = "ExternalInventory")
public class TilePipeBelt extends TileNode implements IRotatable, IInventoryProvider<PipeInventory>, IGuiTile, IWrenchListener, IInserterAccess, IAdjustableConnections, IBlockStackListener
{
    //============================================================
    //==================== Constants =============================
    //============================================================
    public static final int PACKET_INVENTORY = 1;
    public static final int PACKET_GUI_BUTTON = 2;
    public static final int PACKET_GUI_OPEN = 3;

    public static final int BUTTON_ITEM_PULL = 20;
    public static final int BUTTON_RENDER_TOP = 21;
    public static final int BUTTON_ITEM_EJECT = 22;

    public static int GUI_MAIN = 0;
    public static int GUI_SETTINGS = 1;
    public static int GUI_UPGRADES = 2;

    public static final String NBT_BELT_SIDE_DATA = "beltSideData";
    public static final String NBT_BELT_SIDE_DATA_INDEX = "side";
    public static final String NBT_BELT_SIDE_DATA_OUTPUT = "output";
    public static final String NBT_BELT_SIDE_DATA_ENABLE = "enabled";

    public static final String NBT_INVENTORY = "inventory";
    public static final String NBT_BELT_TYPE =  "beltType";
    public static final String NBT_PULL_ITEMS = "pullItems";
    public static final String NBT_EJECT_ITEMS = "ejectItems";
    public static final String NBT_RENDER_TOP = "renderTubeTop";

    /** Cached state map of direction to input sides & slots */
    public static HashMap<Direction, BeltSideState>[/* belt type */][/* rotation */] cachedBeltStates; //TODO change over to hashmap like object

    public static int[] centerSlots = new int[]{2};
    public static int[] centerEndSlots = new int[]{1}; //TODO move to enum

    //============================================================

    //Main inventory
    private PipeInventory inventory;

    //Cached facing direction
    private ForgeDirection _direction;

    //Type of belt
    public BeltType type = BeltType.NORMAL;

    //Send inventory update to client
    private boolean sendInvToClient = true;
    /** Should pipe suck items out of machines from connected inputs */
    public boolean pullItems = false;
    /** Should outputs dump items on the ground if no connections */
    public boolean shouldEjectItems = false;
    /** Should pipe render with its cage top */
    public boolean renderTop = true;
    /** Should the pipe trigger a re-render */
    public boolean shouldUpdateRender = false;

    /** Time in ticks until next movement */
    public int movementTimer = 20;
    /** Time to set movement timer each time an item is moved */
    public int movementDelay = 20;

    public BasicInventory renderInventory;

    private BeltSideStateIterator inputIterator;
    private BeltSideStateIterator outputIterator;

    private BeltSideData[] beltSideData;

    public TilePipeBelt()
    {
        super("pipeBelt", AssemblyLine.DOMAIN);
        inputIterator = new BeltSideStateIterator(this, false);
        outputIterator = new BeltSideStateIterator(this, true);
    }

    @Override
    public void update(long ticks)
    {
        super.update(ticks);
        if (isServer())
        {
            if (movementTimer > 0)
            {
                movementTimer--;
            }

            //Only moves items if there has been a chance in inventory
            if (movementTimer <= 0)
            {
                //Inventory movement
                //1. OUTPUT:                    Push items from output to tiles
                //2. PUSH CENTER -> OUTPUTS:    Move center slots to outputs (sorting)
                //3. PUSH INPUTS -> CENTER:     Move inputs to center (round-robin / priority queue)
                //4. INPUT:                     Pull items into inputs from tiles

                exportItems();
                centerToOutput();
                inputToCenter();
                importItems();
            }

            if (sendInvToClient) //TODO add settings to control packet update times
            {
                sendInvToClient = false;
                sendInventoryPacket();
            }

            if (shouldUpdateRender)
            {
                sendDescPacket();
                shouldUpdateRender = false;
            }
        }
        else
        {
            if (shouldUpdateRender)
            {
                world().unwrap().markBlockRangeForRenderUpdate(xi(), yi(), zi(), xi(), yi(), zi());
                shouldUpdateRender = false;
            }
        }
    }

    @Override
    public ItemStack toStack()
    {
        return new ItemStack(AssemblyLine.pipeBelt, 1, type.ordinal());
    }

    //<editor-fold desc="player interaction">
    @Override
    public boolean onPlayerRightClickWrench(EntityPlayer player, int side, float hitX, float hitY, float hitZ)
    {
        if (isServer())
        {
            if (player.isSneaking())
            {
                setDirection(getDirection().getOpposite());
            }
            else
            {
                switch (getDirection())
                {
                    case NORTH:
                        setDirection(ForgeDirection.EAST);
                        break;
                    case EAST:
                        setDirection(ForgeDirection.SOUTH);
                        break;
                    case SOUTH:
                        setDirection(ForgeDirection.WEST);
                        break;
                    case WEST:
                    default:
                        setDirection(ForgeDirection.NORTH);
                        break;
                }
            }
        }
        return true;
    }

    @Override
    public boolean onToolUsed(ItemStack stack, EntityPlayer player, ConnectionColor color, ConnectionType connectionType, ForgeDirection side, float hitX, float hitY, float hitZ)
    {
        if (connectionType == ConnectionType.INVENTORY)
        {
            if (type == BeltType.JUNCTION || type == BeltType.INTERSECTION)
            {
                if (isServer())
                {
                    Direction direction = Direction.getOrientation(side.ordinal());
                    if (getBeltStateMap().containsKey(direction)) //TODO replace with trace system that will detect of face clicked
                    {
                        setOutputForSide(side.ordinal(), !canOutputForSide(direction));
                        player.addChatComponentMessage(new ChatComponentText("Side set to output: " + canOutputForSide(direction)));
                        return true;
                    }
                }
                return true;
            }
        }
        return false;
    }

    // </editor-folder>

    //<editor-fold desc="item movement">

    /**
     * @param fromSlot slot, or -1 to note external
     * @param toSlot   slot, or -1 to note external
     * @param stack    stack moved
     */
    protected void onItemMoved(int fromSlot, int toSlot, ItemStack stack)
    {
        movementTimer = movementDelay + world().unwrap().rand.nextInt(5);
        if (Engine.runningAsDev)
        {
            //System.out.println("TilePipeBelt#OnItemMoved(" + fromSlot + ", " + toSlot + ", " + stack + ")");
        }
    }

    /**
     * Pushes output slot items either
     * to connections or into the world
     * if eject is enabled.
     */
    protected void exportItems()
    {
        //Push outputs to next belt or machine
        Iterator<BeltSideState> states = beltOutputIterator();
        if (states != null && centerSlots != null)
        {
            while (states.hasNext())
            {
                BeltSideState slotState = states.next();
                if (slotState != null)
                {
                    //Get output position
                    final Location outputLocation = toLocation().add(slotState.side);

                    //Get stack in slot
                    ItemStack stack = getInventory().getStackInSlot(slotState.slotID);
                    if (stack != null)
                    {
                        ItemStack prev = stack.copy();
                        //Drop
                        if (outputLocation.isAirBlock())
                        {
                            if (shouldEjectItems) //TODO allow each connection to be customized
                            {
                                final Location ejectPosition = toLocation().add(slotState.side, 0.6f);
                                EntityItem entityItem = InventoryUtility.dropItemStack(ejectPosition, stack);
                                if (entityItem != null)
                                {
                                    //Clear item
                                    stack = null;

                                    //Add some speed just for animation reasons
                                    entityItem.motionX = slotState.side.offsetX * 0.1f;
                                    entityItem.motionY = slotState.side.offsetY * 0.1f;
                                    entityItem.motionZ = slotState.side.offsetZ * 0.1f;
                                }
                            }
                        }
                        //Push into tile
                        else
                        {
                            stack = InventoryUtility.insertStack(outputLocation, stack, slotState.side.getOpposite().ordinal(), false);
                        }

                        //Fire events, if stack changed
                        if (!InventoryUtility.stacksMatchExact(stack, prev))
                        {
                            getInventory().setInventorySlotContents(slotState.slotID, stack);
                            onItemMoved(slotState.slotID, -1, stack);
                        }
                    }
                }
            }
        }
    }

    /**
     * Pushes center slots to output slots.
     * Does sorting if enabled to ensure items get
     * to correct output slots.
     */
    protected void centerToOutput()
    {
        int[] centerSlots = getCenterSlots(); //TODO change to inventory

        //Center to output
        Iterator<BeltSideState> states = beltOutputIterator();
        if (states != null && centerSlots != null)
        {
            while (states.hasNext())
            {
                BeltSideState slotState = states.next();
                if (slotState != null)
                {
                    if (getInventory().getStackInSlot(slotState.slotID) == null)
                    {
                        for (int centerSlot : centerSlots)
                        {
                            ItemStack stackToMove = getInventory().getStackInSlot(centerSlot);
                            if (stackToMove != null) //TODO apply filter for sorting
                            {
                                getInventory().setInventorySlotContents(slotState.slotID, stackToMove);
                                getInventory().setInventorySlotContents(centerSlot, null);

                                //Fire event
                                onItemMoved(centerSlot, slotState.slotID, stackToMove);
                                break;
                            }
                        }
                    }
                }
            }
        }
    }


    /**
     * Pushes input slots to center. Does round robin
     * logic and sorting to ensure correct order of
     * inputs or prevent jamming if possible.
     */
    protected void inputToCenter()
    {
        //TODO round robin to ensure each belt has a chance to move to center
        //TODO store inputs as list object with current index
        //TODO only move index forward if we moved an item
        //Index will be used to note starting point for loop
        //Example: 0 1 2 (moved item) next tick > 1 2 0 (moved item) > 2 0 1

        Iterator<BeltSideState> states = beltInputIterator();
        if (states != null)
        {
            while (states.hasNext())
            {
                BeltSideState slotState = states.next();
                if (slotState != null)
                {
                    ItemStack stackToMove = getInventory().getStackInSlot(slotState.slotID);
                    if (stackToMove != null)
                    {
                        for (int centerSlot : centerSlots)
                        {
                            if (getInventory().getStackInSlot(centerSlot) == null)
                            {
                                getInventory().setInventorySlotContents(centerSlot, stackToMove);
                                getInventory().setInventorySlotContents(slotState.slotID, null);

                                //Fire event
                                onItemMoved(slotState.slotID, centerSlot, stackToMove);
                                break;
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Pulls items from input connections into the pipe
     */
    protected void importItems()
    {
        Iterator<BeltSideState> states = beltInputIterator();
        if (states != null)
        {
            while (states.hasNext())
            {
                BeltSideState slotState = states.next();
                if (slotState != null)
                {
                    ItemStack currentItem = getInventory().getStackInSlot(slotState.slotID);
                    if (currentItem == null)
                    {
                        //Get inventory
                        IInventory tileInv = null;
                        TileEntity tile = toLocation().add(slotState.side).getTileEntity();
                        if (tile instanceof IInventory)
                        {
                            tileInv = (IInventory) tile;
                        }

                        //Has inventory try to find item
                        if (tileInv != null && (tile instanceof TileEntityWrappedPipeBelt || pullItems))
                        {
                            Pair<ItemStack, Integer> slotData = InventoryUtility.findFirstItemInInventory(tileInv,
                                    slotState.side.getOpposite().ordinal(), getItemsToPullPerCycle(), getFilterForSide(slotState.side.ordinal()));
                            if (slotData != null)
                            {
                                ItemStack inputStack = tileInv.decrStackSize(slotData.right(), slotData.left().stackSize);
                                getInventory().setInventorySlotContents(slotState.slotID, inputStack);

                                //Fire event
                                onItemMoved(-1, slotState.slotID, inputStack);
                            }
                        }
                    }
                }
            }
        }
    }


    public int getItemsToPullPerCycle()
    {
        return 1;
    }

    public int[] getCenterSlots()
    {
        if (type == BeltType.END_CAP)
        {
            return centerEndSlots;
        }
        return centerSlots;
    }
    // </editor-folder> item movement

    //<editor-fold desc="inventory">
    @Override
    public PipeInventory getInventory()
    {
        if (inventory == null)
        {
            inventory = new PipeInventory(this);
        }
        return inventory;
    }

    @Override
    public boolean canStore(ItemStack stack, int slot, ForgeDirection side)
    {
        for (BeltSideState state : beltInputIterator())
        {
            if (state != null && slot == state.slotID)
            {
                return state.side == side;
            }
        }
        return false;
    }

    @Override
    public boolean canRemove(ItemStack stack, int slot, ForgeDirection side)
    {
        for (BeltSideState state : beltOutputIterator())
        {
            if (state != null && slot == state.slotID)
            {
                return state.side == side;
            }
        }
        return false;
    }

    @Override
    public void onInventoryChanged(int slot, ItemStack prev, ItemStack item)
    {
        sendInvToClient = true;
    }

    //</editor-fold>

    //<editor-fold desc="inserter">
    @Override
    public ItemStack takeInserterItem(EulerAngle angle, ForgeDirection side, int count, boolean remove)
    {
        //TODO get item based on angle/position

        //Allow output side priority
        for (BeltSideState state : beltOutputIterator())
        {
            if (state != null && state.side == side)
            {
                if (getInventory().getStackInSlot(state.slotID) != null)
                {
                    return getItemForRemoval(state.slotID, count, remove);
                }
                return null;
            }
        }

        if (!renderTop)
        {
            final int slot = getCenterSlots()[0];
            if (getInventory().getStackInSlot(slot) != null)
            {
                if (type == BeltType.NORMAL || type == BeltType.RIGHT_ELBOW || type == BeltType.LEFT_ELBOW)
                {
                    boolean armNS = side == ForgeDirection.NORTH || side == ForgeDirection.SOUTH;
                    boolean beltEW = getDirection() == ForgeDirection.WEST || getDirection() == ForgeDirection.EAST;
                    if (armNS)
                    {
                        if (beltEW)
                        {
                            return getItemForRemoval(slot, count, remove);
                        }
                        return null;
                    }
                    else if (!beltEW)
                    {
                        return getItemForRemoval(slot, count, remove);
                    }
                    return null;
                }
                else if (type == BeltType.END_CAP)
                {
                    return getItemForRemoval(slot, count, remove);
                }
            }
        }
        return null;
    }

    private ItemStack getItemForRemoval(int slot, int count, boolean remove)
    {
        ItemStack stack = getInventory().getStackInSlot(slot);
        if (remove)
        {
            int removeCount = Math.min(stack.stackSize, count);
            if (remove)
            {
                return getInventory().decrStackSize(slot, removeCount);
            }
            else
            {
                stack = stack.copy();
                stack.stackSize = removeCount;
                return stack;
            }
        }
        return stack;
    }

    @Override
    public ItemStack giveInserterItem(EulerAngle angle, ForgeDirection side, ItemStack stack, boolean doInsert)
    {
        //Allow input side priority
        for (BeltSideState state : beltInputIterator())
        {
            if (state != null && state.side == side)
            {
                if (getInventory().getStackInSlot(state.slotID) != null)
                {
                    return getItemAfterInsert(state.slotID, stack, doInsert);
                }
                return null;
            }
        }

        if (!renderTop)
        {
            final int slot = getCenterSlots()[0];
            if (getInventory().getStackInSlot(slot) == null) //We can only have 1 item, so do a null check
            {
                if (type == BeltType.NORMAL || type == BeltType.RIGHT_ELBOW || type == BeltType.LEFT_ELBOW)
                {
                    boolean armNS = side == ForgeDirection.NORTH || side == ForgeDirection.SOUTH;
                    boolean beltEW = getDirection() == ForgeDirection.WEST || getDirection() == ForgeDirection.EAST;
                    if (armNS)
                    {
                        if (beltEW)
                        {
                            return getItemAfterInsert(slot, stack, doInsert);
                        }
                        return null;
                    }
                    else if (!beltEW)
                    {
                        return getItemAfterInsert(slot, stack, doInsert);
                    }
                    return null;
                }
                else if (type == BeltType.END_CAP)
                {
                    return getItemAfterInsert(slot, stack, doInsert);
                }
            }
        }
        return stack;
    }

    private ItemStack getItemAfterInsert(int slot, ItemStack insertStack, boolean doInsert)
    {
        ItemStack slotStack = getInventory().getStackInSlot(slot);
        if (slotStack == null) //At the moment the inventory can only handle 1 stack size, so might as well null check
        {
            ItemStack stack = insertStack.copy();
            ItemStack newSlotStack = stack.splitStack(1);
            if (doInsert)
            {
                getInventory().setInventorySlotContents(slot, newSlotStack);
            }

            if (stack.stackSize <= 0)
            {
                return null;
            }
            return stack;
        }
        return insertStack;
    }

    //</editor-fold> end inserter

    //<editor-fold desc="belt data and accessors">

    public BeltSideStateIterator beltInputIterator()
    {
        return inputIterator.reset(); //Not thread safe
    }

    public BeltSideStateIterator beltOutputIterator()
    {
        return outputIterator.reset(); //Not thread safe
    }

    public HashMap<Direction, BeltSideState> getBeltStateMap()
    {
        if (cachedBeltStates[type.ordinal()] != null)
        {
            return cachedBeltStates[type.ordinal()][getDirection().ordinal()];
        }
        return new HashMap();
    }

    public boolean hasSortingUpgrade()
    {
        return false;
    }

    protected void setLocalBeltState()
    {
        beltSideData = new BeltSideData[6];
        for (Direction direction : Direction.DIRECTIONS)
        {
            BeltSideState state = getBeltStateMap().get(direction);
            if (state != null)
            {
                beltSideData[direction.ordinal()] = new BeltSideData(state.output);
            }
        }
    }

    public BeltSideData getBeltSideData(int side)
    {
        if (beltSideData == null)
        {
            setLocalBeltState();
        }
        return beltSideData[side];
    }


    public void setOutputForSide(int side, boolean output)
    {
        BeltSideData data = getBeltSideData(side);
        if (data != null)
        {
            data.output = output;
            shouldUpdateRender = true;
        }
    }

    public boolean canOutputForSide(Direction direction)
    {
        BeltSideData data = getBeltSideData(direction.ordinal());
        if (data != null)
        {
            return data.output;
        }
        BeltSideState state = getBeltStateMap().get(direction);
        if(state != null)
        {
            return state.output;
        }
        return false;
    }

    public BeltInventoryFilter getFilterForSide(int side)
    {
        BeltSideData data = getBeltSideData(side);
        if (data != null)
        {
            return data.filter;
        }
        return null;
    }

    @Override
    public ForgeDirection getDirection()
    {
        if (_direction == null)
        {
            _direction = ForgeDirection.getOrientation(world().unwrap().getBlockMetadata(xi(), yi(), zi()));
        }
        return _direction;
    }

    @Override
    public void setDirection(ForgeDirection direction)
    {
        if (direction != null && direction != getDirection()
                && direction.ordinal() >= 2 && direction.ordinal() < 6)
        {
            _direction = null;
            getHost().setMetaValue(direction.ordinal());
        }
    }

    //</editor-fold> end belt data and accessors

    //<editor-fold desc="save load">
    @Override
    public void load(NBTTagCompound nbt)
    {
        super.load(nbt);
        type = BeltType.get(nbt.getInteger(NBT_BELT_TYPE));
        if (nbt.hasKey(NBT_INVENTORY))
        {
            getInventory().load(nbt.getCompoundTag(NBT_INVENTORY));
        }
        if (nbt.hasKey(NBT_BELT_SIDE_DATA))
        {
            loadBeltStates(nbt.getTagList(NBT_BELT_SIDE_DATA, 10));
        }
        else
        {
            beltSideData = null;
        }
        pullItems = nbt.getBoolean(NBT_PULL_ITEMS);
        shouldEjectItems = nbt.getBoolean(NBT_EJECT_ITEMS);
        renderTop = nbt.getBoolean(NBT_RENDER_TOP);
    }

    protected void loadBeltStates(NBTTagList list)
    {
        beltSideData = new BeltSideData[6];
        for (int i = 0; i < list.tagCount(); i++)
        {
            NBTTagCompound tag = list.getCompoundTagAt(i);
            int index = tag.getInteger(NBT_BELT_SIDE_DATA_INDEX);

            beltSideData[index] = new BeltSideData(tag.getBoolean(NBT_BELT_SIDE_DATA_OUTPUT));
            beltSideData[index].enabled = tag.getBoolean(NBT_BELT_SIDE_DATA_ENABLE);
            //TODO load filter
        }
    }

    @Override
    public NBTTagCompound save(NBTTagCompound nbt)
    {
        super.save(nbt);
        if (!getInventory().isEmpty())
        {
            NBTTagCompound invSave = new NBTTagCompound();
            getInventory().save(invSave);
            nbt.setTag(NBT_INVENTORY, invSave);
        }
        if (beltSideData != null)
        {
            nbt.setTag(NBT_BELT_SIDE_DATA, saveBeltState());
        }
        nbt.setBoolean(NBT_PULL_ITEMS, pullItems);
        nbt.setBoolean(NBT_EJECT_ITEMS, shouldEjectItems);
        nbt.setBoolean(NBT_RENDER_TOP, renderTop);
        nbt.setInteger(NBT_BELT_TYPE, type != null ? type.ordinal() : 0);
        return nbt;
    }

    protected NBTTagList saveBeltState()
    {
        NBTTagList list = new NBTTagList();
        for (Direction direction : Direction.DIRECTIONS)
        {
            BeltSideData data = beltSideData[direction.ordinal()];
            if (data != null)
            {
                NBTTagCompound tag = new NBTTagCompound();

                tag.setInteger(NBT_BELT_SIDE_DATA_INDEX, direction.ordinal());
                tag.setBoolean(NBT_BELT_SIDE_DATA_OUTPUT, data.output);
                tag.setBoolean(NBT_BELT_SIDE_DATA_ENABLE, data.enabled);

                list.appendTag(tag);
            }
        }
        return list;
    }

    //</editor-fold>

    //<editor-fold desc="gui">
    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player)
    {
        return new ContainerPipeBelt(player, this, ID);
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player)
    {
        return new GuiPipeBelt(player, this, ID);
    }

    @Override
    public boolean openGui(EntityPlayer player, int requestedID)
    {
        if (requestedID >= 0 && requestedID < 3)
        {
            player.openGui(AssemblyLine.INSTANCE, requestedID, world().unwrap(), xi(), yi(), zi());
            return true;
        }
        return false;
    }
    //</editor-fold"> end gui

    //<editor-fold desc="packet">
    @Override
    public boolean read(ByteBuf buf, int id, EntityPlayer player, PacketType type)
    {
        if (!super.read(buf, id, player, type))
        {
            if (isServer() && id == PACKET_GUI_OPEN)
            {
                openGui(player, buf.readInt());
                return true;
            }
            else if (id == PACKET_INVENTORY)
            {
                readInvPacket(buf);
                return true;
            }
            else if (id == PACKET_GUI_BUTTON)
            {
                int buttonID = buf.readInt();
                boolean enabled = buf.readBoolean();

                if (buttonID == BUTTON_ITEM_PULL)
                {
                    this.pullItems = enabled;
                }
                else if (buttonID == BUTTON_RENDER_TOP)
                {
                    this.renderTop = enabled;
                    this.shouldUpdateRender = true;
                }
                else if (buttonID == BUTTON_ITEM_EJECT)
                {
                    this.shouldEjectItems = enabled;
                }
                return true;
            }
            return false;
        }
        return true;
    }

    @Override
    public void readDescPacket(ByteBuf buf)
    {
        super.readDescPacket(buf);
        type = BeltType.values()[buf.readInt()];
        shouldUpdateRender = buf.readBoolean();
        shouldEjectItems = buf.readBoolean();
        pullItems = buf.readBoolean();
        renderTop = buf.readBoolean();
        readInvPacket(buf);
        if (buf.readBoolean())
        {
            loadBeltStates(ByteBufUtils.readTag(buf).getTagList("beltStates", 10));
        }
    }

    @Override
    public void writeDescPacket(ByteBuf buf)
    {
        super.writeDescPacket(buf);
        buf.writeInt(type.ordinal());
        buf.writeBoolean(shouldUpdateRender);
        buf.writeBoolean(shouldEjectItems);
        buf.writeBoolean(pullItems);
        buf.writeBoolean(renderTop);
        writeInvPacket(buf);
        buf.writeBoolean(beltSideData != null);
        if (beltSideData != null)
        {
            NBTTagCompound tag = new NBTTagCompound();
            tag.setTag("beltStates", saveBeltState());
            ByteBufUtils.writeTag(buf, tag);
        }
    }

    @Override
    protected void writeGuiPacket(EntityPlayer player, ByteBuf buf)
    {
        buf.writeBoolean(shouldEjectItems);
        buf.writeBoolean(pullItems);
        buf.writeBoolean(renderTop);
    }

    @Override
    protected void readGuiPacket(EntityPlayer player, ByteBuf buf)
    {
        shouldEjectItems = buf.readBoolean();
        pullItems = buf.readBoolean();
        renderTop = buf.readBoolean();
    }

    public void readInvPacket(ByteBuf buf)
    {
        int size = buf.readInt();
        if (renderInventory == null || renderInventory.getSizeInventory() != size)
        {
            renderInventory = new BasicInventory(size);
        }
        renderInventory.load(ByteBufUtils.readTag(buf));
    }

    public void writeInvPacket(ByteBuf buf)
    {
        buf.writeInt(getInventory().getSizeInventory());
        ByteBufUtils.writeTag(buf, getInventory().save(new NBTTagCompound()));
    }


    public void sendInventoryPacket()
    {
        IPacket packet = getHost().getPacketForData(PACKET_INVENTORY);
        writeInvPacket(packet.data());
        getHost().sendPacketToClient(packet, 64);
    }

    public void sendButtonEvent(int id, boolean checked)
    {
        IPacket packet = getHost().getPacketForData(PACKET_GUI_BUTTON);
        packet.data().writeInt(id);
        packet.data().writeBoolean(checked);
        getHost().sendPacketToServer(packet);
    }

    //</editor-fold> end packet

    static
    {
        generateBeltStates();
    }

    public static void generateBeltStates()
    {
        cachedBeltStates = new HashMap[BeltType.values().length][6];
        Direction[] rotations = new Direction[]{Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST};
        for (Direction direction : rotations)
        {
            //Generate cached state for normal belt for side
            HashMap<Direction, BeltSideState> normalBeltState = new HashMap();
            normalBeltState.put(direction, new BeltSideState(0, direction, false));
            normalBeltState.put(direction.getOpposite(), new BeltSideState(1, direction.getOpposite(), true));
            cachedBeltStates[BeltType.NORMAL.ordinal()][direction.ordinal()] = normalBeltState;

            //Generate cached state for end belt for side
            HashMap<Direction, BeltSideState> endBeltState = new HashMap();
            endBeltState.put(direction.getOpposite(), new BeltSideState(0, direction.getOpposite(), true));
            cachedBeltStates[BeltType.END_CAP.ordinal()][direction.ordinal()] = endBeltState;

            //Get rotation
            Direction turn;
            switch (direction)
            {
                case NORTH:
                    turn = Direction.EAST;
                    break;
                case SOUTH:
                    turn = Direction.WEST;
                    break;
                case EAST:
                    turn = Direction.SOUTH;
                    break;
                default:
                    turn = Direction.NORTH;
                    break;
            }

            //Generate left elbow
            HashMap<Direction, BeltSideState> leftBeltState = new HashMap();
            leftBeltState.put(direction.getOpposite(), new BeltSideState(0, direction.getOpposite(), true));
            leftBeltState.put(direction, new BeltSideState(1, turn, false));
            cachedBeltStates[BeltType.LEFT_ELBOW.ordinal()][direction.ordinal()] = leftBeltState;

            //Generate right elbow
            HashMap<Direction, BeltSideState> rightBeltState = new HashMap();
            rightBeltState.put(direction.getOpposite(), new BeltSideState(0, direction.getOpposite(), true));
            rightBeltState.put(direction, new BeltSideState(1, turn.getOpposite(), false));
            cachedBeltStates[BeltType.RIGHT_ELBOW.ordinal()][direction.ordinal()] = rightBeltState;

            //Generate junction
            HashMap<Direction, BeltSideState> junctionBeltState = new HashMap();
            junctionBeltState.put(direction.getOpposite(), new BeltSideState(0, direction.getOpposite(), true));
            junctionBeltState.put(turn, new BeltSideState(1, turn, false));
            junctionBeltState.put(turn.getOpposite(), new BeltSideState(3, turn.getOpposite(), false));
            cachedBeltStates[BeltType.JUNCTION.ordinal()][direction.ordinal()] = junctionBeltState;


            //Generate junction
            HashMap<Direction, BeltSideState> intersectionBeltState = new HashMap();
            intersectionBeltState.put(direction.getOpposite(), new BeltSideState(0, direction.getOpposite(), true));
            intersectionBeltState.put(turn, new BeltSideState(1, turn, false));
            intersectionBeltState.put(turn.getOpposite(), new BeltSideState(3, turn.getOpposite(), false));
            intersectionBeltState.put(direction, new BeltSideState(4, direction, false));
            cachedBeltStates[BeltType.INTERSECTION.ordinal()][direction.ordinal()] = intersectionBeltState;
        }
    }
}
