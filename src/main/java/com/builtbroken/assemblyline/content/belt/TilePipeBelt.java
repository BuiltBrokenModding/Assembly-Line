package com.builtbroken.assemblyline.content.belt;

import com.builtbroken.assemblyline.AssemblyLine;
import com.builtbroken.assemblyline.api.IInserterAccess;
import com.builtbroken.assemblyline.content.belt.gen.TileEntityWrappedPipeBelt;
import com.builtbroken.assemblyline.content.belt.pipe.BeltType;
import com.builtbroken.assemblyline.content.belt.pipe.PipeInventory;
import com.builtbroken.assemblyline.content.belt.pipe.data.BeltInventoryFilter;
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
import com.builtbroken.mc.framework.block.imp.IBlockStackListener;
import com.builtbroken.mc.framework.block.imp.IWrenchListener;
import com.builtbroken.mc.framework.logic.TileNode;
import com.builtbroken.mc.imp.transform.rotation.EulerAngle;
import com.builtbroken.mc.imp.transform.vector.Location;
import com.builtbroken.mc.prefab.inventory.BasicInventory;
import com.builtbroken.mc.prefab.inventory.InventoryUtility;
import com.google.common.collect.ImmutableList;
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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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

    /** Cached state map of direction to input sides & slots */
    public static List<BeltSideState>[/* belt type */][/* rotation */] cachedBeltStates;

    public static int[] centerSlots = new int[]{2};

    public static final List<BeltSideState> EMPTY_LIST = ImmutableList.of();

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

    protected List<BeltSideState> localBeltState;

    private BeltSideStateIterator inputIterator;
    private BeltSideStateIterator outputIterator;

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
            if (type == BeltType.JUNCTION)
            {
                if (isServer())
                {
                    for (BeltSideState state : getBeltStates())
                    {
                        if (state.side == side) //TODO replace with trace system that will detect of face clicked
                        {
                            setOutputForSide(state, !state.output);
                            player.addChatComponentMessage(new ChatComponentText("Side set to output: " + state.output));
                            return true;
                        }
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
                                    slotState.side.getOpposite().ordinal(), getItemsToPullPerCycle(), slotState.filter);
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

        if (!renderTop && (type == BeltType.NORMAL || type == BeltType.RIGHT_ELBOW || type == BeltType.LEFT_ELBOW))
        {
            final int slot = getCenterSlots()[0];
            if (getInventory().getStackInSlot(slot) != null)
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

        if (!renderTop && (type == BeltType.NORMAL || type == BeltType.RIGHT_ELBOW || type == BeltType.LEFT_ELBOW))
        {
            final int slot = getCenterSlots()[0];
            if (getInventory().getStackInSlot(slot) == null) //We can only have 1 item, so do a null check
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

    public List<BeltSideState> getBeltStates()
    {
        if (localBeltState != null)
        {
            return localBeltState;
        }
        if (cachedBeltStates[type.ordinal()] != null)
        {
            return cachedBeltStates[type.ordinal()][getDirection().ordinal()];
        }
        return EMPTY_LIST;
    }

    public boolean hasSortingUpgrade()
    {
        return false;
    }

    protected void setLocalBeltState()
    {
        this.localBeltState = null;

        List<BeltSideState> localBeltState = new ArrayList();
        for (BeltSideState sideState : getBeltStates())
        {
            localBeltState.add(sideState.copy(true));
        }
        this.localBeltState = localBeltState;
    }

    public void setOutputForSide(int slotID, boolean output)
    {
        if (type == BeltType.INTERSECTION || type == BeltType.JUNCTION)
        {
            if (localBeltState == null)
            {
                setLocalBeltState();
            }
            for (BeltSideState state : getBeltStates())
            {
                if (state.slotID == slotID)
                {
                    setOutputForSide(state, output);
                    break;
                }
            }
        }
    }

    public void setOutputForSide(BeltSideState state, boolean output)
    {
        state.output = output;
        shouldUpdateRender = true;
    }

    public BeltInventoryFilter getFilterForSide(int slotID)
    {
        if (localBeltState != null)
        {
            for (BeltSideState state : getBeltStates())
            {
                if (state.slotID == slotID)
                {
                    return state.filter;
                }
            }
        }
        return null;
    }

    public void enableFilterSide(int slotID)
    {
        if (type == BeltType.INTERSECTION || type == BeltType.JUNCTION)
        {
            if (localBeltState == null)
            {
                setLocalBeltState();
            }
            for (BeltSideState state : getBeltStates())
            {
                if (state.slotID == slotID)
                {
                    state.filter = new BeltInventoryFilter();
                    break;
                }
            }
        }
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
        type = BeltType.get(nbt.getInteger("beltType"));
        if (nbt.hasKey("inventory"))
        {
            getInventory().load(nbt.getCompoundTag("inventory"));
        }
        if (nbt.hasKey("beltStates"))
        {
            setLocalBeltState();
            NBTTagList list = nbt.getTagList("beltStates", 10);
            for (int i = 0; i < list.tagCount(); i++)
            {
                NBTTagCompound tag = list.getCompoundTagAt(i);
                int slotID = tag.getInteger("id");
                for (BeltSideState state : getBeltStates()) //TODO improve O(n^2)
                {
                    if (state.slotID == slotID)
                    {
                        state.load(tag);
                        break;
                    }
                }
            }
        }
        pullItems = nbt.getBoolean("pullItems");
        shouldEjectItems = nbt.getBoolean("ejectItems");
        renderTop = nbt.getBoolean("renderTubeTop");
    }

    @Override
    public NBTTagCompound save(NBTTagCompound nbt)
    {
        super.save(nbt);
        if (!getInventory().isEmpty())
        {
            NBTTagCompound invSave = new NBTTagCompound();
            getInventory().save(invSave);
            nbt.setTag("inventory", invSave);
        }
        if (localBeltState != null)
        {
            NBTTagList list = new NBTTagList();
            for (BeltSideState sideState : localBeltState)
            {
                NBTTagCompound tag = new NBTTagCompound();
                sideState.save(tag);
                tag.setInteger("id", sideState.slotID);
                list.appendTag(tag);
            }
            nbt.setTag("beltStates", list);
        }
        nbt.setBoolean("pullItems", pullItems);
        nbt.setBoolean("ejectItems", shouldEjectItems);
        nbt.setBoolean("renderTubeTop", renderTop);
        nbt.setInteger("beltType", type != null ? type.ordinal() : 0);
        return nbt;
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
        cachedBeltStates = new ArrayList[BeltType.values().length][6];
        ForgeDirection[] rotations = new ForgeDirection[]{ForgeDirection.NORTH, ForgeDirection.SOUTH, ForgeDirection.EAST, ForgeDirection.WEST};
        for (ForgeDirection direction : rotations)
        {
            //Generate cached state for normal belt for side
            List<BeltSideState> normalBeltState = new ArrayList();
            normalBeltState.add(new BeltSideState(0, direction, false, false));
            normalBeltState.add(new BeltSideState(1, direction.getOpposite(), false, true));
            cachedBeltStates[BeltType.NORMAL.ordinal()][direction.ordinal()] = normalBeltState;

            //Get rotation
            ForgeDirection turn;
            switch (direction)
            {
                case NORTH:
                    turn = ForgeDirection.EAST;
                    break;
                case SOUTH:
                    turn = ForgeDirection.WEST;
                    break;
                case EAST:
                    turn = ForgeDirection.SOUTH;
                    break;
                default:
                    turn = ForgeDirection.NORTH;
                    break;
            }

            //Generate left elbow
            List<BeltSideState> leftBeltState = new ArrayList();
            leftBeltState.add(new BeltSideState(0, direction.getOpposite(), false, true));
            leftBeltState.add(new BeltSideState(1, turn, false, false));
            cachedBeltStates[BeltType.LEFT_ELBOW.ordinal()][direction.ordinal()] = leftBeltState;

            //Generate right elbow
            List<BeltSideState> rightBeltState = new ArrayList();
            rightBeltState.add(new BeltSideState(0, direction.getOpposite(), false, true));
            rightBeltState.add(new BeltSideState(1, turn.getOpposite(), false, false));
            cachedBeltStates[BeltType.RIGHT_ELBOW.ordinal()][direction.ordinal()] = rightBeltState;

            //Generate junction
            List<BeltSideState> junctionBeltState = new ArrayList();
            junctionBeltState.add(new BeltSideState(0, direction.getOpposite(), false, true));
            junctionBeltState.add(new BeltSideState(1, turn, false, false));
            junctionBeltState.add(new BeltSideState(3, turn.getOpposite(), false, false));
            cachedBeltStates[BeltType.JUNCTION.ordinal()][direction.ordinal()] = junctionBeltState;


            //Generate junction
            List<BeltSideState> intersectionBeltState = new ArrayList();
            intersectionBeltState.add(new BeltSideState(0, direction.getOpposite(), false, true));
            intersectionBeltState.add(new BeltSideState(1, turn, false, false));
            intersectionBeltState.add(new BeltSideState(3, turn.getOpposite(), false, false));
            intersectionBeltState.add(new BeltSideState(4, direction, false, false));
            cachedBeltStates[BeltType.INTERSECTION.ordinal()][direction.ordinal()] = intersectionBeltState;
        }
    }
}
