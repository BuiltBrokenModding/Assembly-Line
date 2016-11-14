package com.builtbroken.assemblyline.content.rail.powered;

import com.builtbroken.assemblyline.AssemblyLine;
import com.builtbroken.assemblyline.content.parts.ALParts;
import com.builtbroken.assemblyline.content.rail.BlockRail;
import com.builtbroken.jlib.type.Pair;
import com.builtbroken.mc.api.rails.IRailInventoryTile;
import com.builtbroken.mc.api.rails.ITransportCart;
import com.builtbroken.mc.api.rails.ITransportCartHasItem;
import com.builtbroken.mc.api.rails.ITransportRail;
import com.builtbroken.mc.core.Engine;
import com.builtbroken.mc.core.content.parts.CraftingParts;
import com.builtbroken.mc.core.network.IPacketIDReceiver;
import com.builtbroken.mc.core.registry.implement.IRecipeContainer;
import com.builtbroken.mc.lib.helper.recipe.OreNames;
import com.builtbroken.mc.lib.helper.recipe.UniversalRecipe;
import com.builtbroken.mc.lib.transform.region.Cube;
import com.builtbroken.mc.lib.transform.vector.Location;
import com.builtbroken.mc.lib.transform.vector.Pos;
import com.builtbroken.mc.prefab.inventory.InventoryUtility;
import com.builtbroken.mc.prefab.tile.Tile;
import com.builtbroken.mc.prefab.tile.TileModuleMachine;
import io.netty.buffer.ByteBuf;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentText;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.List;

/**
 * Handles different functions
 * <p>
 * A) Handles rotation of the block on any axis.
 * <p>
 * B) Handles pushing the carts forward
 * <p>
 * B is the lower tech version of A only pushing the carts and can not actually move the carts. While A can also push and rotate
 * the carts allowing it to be used in place of a B type rail.
 * <p>
 * C) handles directing the cart from one direction
 * into another direction. So long as the direction does not change facing side of the block.
 * Example North side moving into the block, redirect upwards, still on the north side of the block but moving up instead of forwards.
 * <p>
 * D) stops the cart from moving, should be redstone controllable with no redstone being off by default
 *
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 10/29/2016.
 */
public class TilePowerRail extends TileModuleMachine implements ITransportRail, IPacketIDReceiver, IRecipeContainer
{
    //TODO C type rails need to show a stair case symbol to show it moving up or down
    //TODO D type needs to have a redstone upgrade

    /** What type of rail are we */
    protected PoweredRails railType = PoweredRails.POWERED;

    //////////////ROTATION RAIL STUFF
    /** How much to rotate */
    protected int rotateYaw = 90;
    /** Are we rotating to an angle or just rotating by an angle */
    protected boolean rotateToAngle = true;
    /** Are we rotating clockwise, not used if setting angle */
    protected boolean rotateClockwise = true;

    /////////////STOP RAIL STUFF
    /** Should we stop carts */
    protected boolean stopCarts = true;
    /** Should we use restone to override stopping of carts */
    protected boolean useRedstoneToInvertStop = true;

    /////////////LOADER UNLOADER STUFF
    /** Direction to load/unload cargo */
    protected ForgeDirection loadDirection;
    /** Should we trigger connected stop rails when we have cargo or no cargo */
    protected boolean triggerStopRails = true;
    /** Should we emmit redstone when we have cargo or no cargo */
    protected boolean doRedstoneOnLoad = true;

    /** Side of the block we are attached to */
    private ForgeDirection attachedSide;
    /** Direction the rail is pointing towards */
    private ForgeDirection facingDirection = ForgeDirection.NORTH;

    //Collision/Render/Selection boxes
    private static final Cube COLLISION_BOX_DOWN = new Cube(0, .6, 0, 1, 1, 1);

    private static final Cube COLLISION_BOX_NORTH = new Cube(0, 0, .6, 1, 1, 1);
    private static final Cube COLLISION_BOX_SOUTH = new Cube(0, 0, 0, 1, 1, .4);

    private static final Cube COLLISION_BOX_EAST = new Cube(0, 0, 0, .4, 1, 1);
    private static final Cube COLLISION_BOX_WEST = new Cube(.6, 0, 0, 1, 1, 1);

    public TilePowerRail()
    {
        super("cartPowerRail", Material.iron);
        this.bounds = new Cube(0, 0, 0, 1, .4, 1);
        this.itemBlock = ItemBlockPowerRail.class;
    }

    @Override
    public void genRecipes(List<IRecipe> recipes)
    {
        recipes.add(newShapedRecipe(new ItemStack(AssemblyLine.blockPowerRail, 1, PoweredRails.POWERED.ordinal()),
                "RPR", "MLM", "PCP",
                'R', OreNames.ROD_IRON,
                'M', CraftingParts.MOTOR.toStack(),
                'C', UniversalRecipe.CIRCUIT_T1.get(),
                'L', AssemblyLine.blockRail,
                'P', OreNames.PLATE_IRON));
        recipes.add(newShapedRecipe(new ItemStack(AssemblyLine.blockPowerRail, 1, PoweredRails.ROTATION.ordinal()),
                "RPR", "RMR", "WLW",
                'R', OreNames.ROD_IRON,
                'M', CraftingParts.STEPPER_MOTOR.toStack(),
                'L', new ItemStack(AssemblyLine.blockPowerRail, 1, PoweredRails.POWERED.ordinal()),
                'P', OreNames.PLATE_IRON,
                'W', OreNames.WIRE_COPPER));
        recipes.add(newShapedRecipe(new ItemStack(AssemblyLine.blockPowerRail, 1, PoweredRails.STOP.ordinal()),
                "RPR", "QCQ", "WLW",
                'R', ALParts.ROBOTIC_HAND.toStack(),
                'L', new ItemStack(AssemblyLine.blockPowerRail, 1, PoweredRails.POWERED.ordinal()),
                'Q', OreNames.QUARTZ,
                'C', UniversalRecipe.CIRCUIT_T1.get(),
                'P', Blocks.stone_pressure_plate,
                'W', OreNames.REDSTONE));
        recipes.add(newShapedRecipe(new ItemStack(AssemblyLine.blockPowerRail, 1, PoweredRails.LOADER.ordinal()),
                "RPR", "TCT", "WLW",
                'R', AssemblyLine.blockInserter,
                'L', new ItemStack(AssemblyLine.blockPowerRail, 1, PoweredRails.STOP.ordinal()),
                'T', UniversalRecipe.CIRCUIT_T2.get(),
                'C', UniversalRecipe.CIRCUIT_T3.get(),
                'P', OreNames.PLATE_IRON,
                'W', OreNames.REDSTONE));
        recipes.add(newShapelessRecipe(new ItemStack(AssemblyLine.blockPowerRail, 1, PoweredRails.UNLOADER.ordinal()),
                new ItemStack(AssemblyLine.blockPowerRail, 1, PoweredRails.LOADER.ordinal())));
        recipes.add(newShapelessRecipe(new ItemStack(AssemblyLine.blockPowerRail, 1, PoweredRails.LOADER.ordinal()),
                new ItemStack(AssemblyLine.blockPowerRail, 1, PoweredRails.UNLOADER.ordinal())));
    }

    @Override
    public Tile newTile()
    {
        return new TilePowerRail();
    }

    @Override
    public boolean canUpdate()
    {
        return false;
    }

    @Override
    public void onNeighborChanged(Block block)
    {
        super.onNeighborChanged(block);
        if (useRedstoneToInvertStop)
        {
            //TODO move to neighbor block change
            boolean prevRed = stopCarts;
            stopCarts = !isIndirectlyPowered();
            if (prevRed != stopCarts)
            {
                sendDescPacket();
            }
        }
    }

    @Override
    public void tickRailFromCart(ITransportCart cart)
    {
        if (isRotationRail())
        {
            //TODO lerp rotation to provide a transition
            if (rotateToAngle)
            {
                cart.setRotation(rotateYaw, 0);
            }
            else if (rotateClockwise)
            {
                cart.setRotation(rotateYaw + rotateYaw, 0);
            }
            else
            {
                cart.setRotation(rotateYaw - rotateYaw, 0);
            }
            handlePush(cart);
        }
        else if (isPoweredRail())
        {
            handlePush(cart);
        }
        else if (isOrientationRail())
        {
            //TODO implements
        }
        else if (isStopRail())
        {
            if (ticks % 5 == 0)
            {
                final Pos delta = new Pos(this).add(0.5).sub(cart.x(), cart.y(), cart.z());
                boolean stop = true;
                // Moving negative <--- -0.5 -0.4 -0.3 -0.2 -0.1 [0] 0.1 0.2 0.3 0.4 0.5 <- coming into station
                // Moving positive <--- 0.5 0.4 0.3 0.2 0.1 [0] -0.1 -0.2 -0.3 -0.4 -0.5 <- coming into station
                switch (getFacingDirection())
                {
                    case DOWN:
                        stop = delta.y() > -0.05;
                        break;
                    case UP:
                        stop = delta.y() < 0.05;
                        break;
                    case NORTH:
                        stop = delta.z() > -0.05;
                        break;
                    case SOUTH:
                        stop = delta.z() < 0.05;
                        break;
                    case EAST:
                        stop = delta.x() < 0.05;
                        break;
                    case WEST:
                        stop = delta.x() > -0.05;
                        break;
                }
                if (stopCarts)
                {
                    if (stop)
                    {
                        cart.setMotion(0, 0, 0);
                        cart.recenterCartOnRail(this, true);
                    }
                }
                else
                {
                    handlePush(cart);
                    cart.recenterCartOnRail(this, false);
                }
            }
        }
        else if (isLoaderRail())
        {
            if (cart instanceof ITransportCartHasItem)
            {
                if (((ITransportCartHasItem) cart).getTransportedItem() == null)
                {
                    cart.setMotion(0, 0, 0);
                    cart.recenterCartOnRail(this, true);

                    if (!worldObj.isRemote)
                    {
                        final Pair<ItemStack, Integer> stack = takeItemFromTile((ITransportCartHasItem) cart);
                        if (stack != null && stack.left() != null && stack.left().stackSize >= 0)
                        {
                            IRailInventoryTile tile = getLoadTile();
                            ItemStack left = ((ITransportCartHasItem) cart).setTransportedItem(InventoryUtility.copyStack(stack.left(), 1));
                            tile.getInventory().setInventorySlotContents(stack.right(), left);
                        }
                    }
                    //Else nothing happened
                }
                else
                {
                    //TODO trigger redstone 3 ticks
                    handlePush(cart);
                    cart.recenterCartOnRail(this, false);
                    return;
                }
            }
        }
        else if (isUnloadRail())
        {
            if (cart instanceof ITransportCartHasItem)
            {
                if (((ITransportCartHasItem) cart).getTransportedItem() != null)
                {
                    cart.setMotion(0, 0, 0);
                    cart.recenterCartOnRail(this, true);

                    if (!worldObj.isRemote)
                    {
                        final ItemStack prev = ((ITransportCartHasItem) cart).getTransportedItem().copy();
                        ItemStack stack = storeItemInTile(((ITransportCartHasItem) cart).getTransportedItem().copy());

                        if (stack == null || stack.stackSize <= 0)
                        {
                            ((ITransportCartHasItem) cart).setTransportedItem(null);
                        }
                        else if (!InventoryUtility.stacksMatchExact(prev, stack))
                        {
                            ((ITransportCartHasItem) cart).setTransportedItem(stack);
                        }
                    }
                    //Else nothing happened
                }
                else
                {
                    //TODO trigger redstone 3 ticks
                    handlePush(cart);
                    cart.recenterCartOnRail(this, false);
                    return;
                }
            }
        }
    }

    /**
     * Stores the item in the first open slot
     * <p>
     * Consumes the item and places into the inventory
     *
     * @param stack - input stack
     * @return what is left of the stack
     */
    public ItemStack storeItemInTile(ItemStack stack)
    {
        final IRailInventoryTile tile = getLoadTile();
        if (tile != null)
        {
            //Check if we can globally store the item
            if (tile.canStore(stack, getLoadingDirection().getOpposite()))
            {
                final int[] slots = tile.getSlotsToLoad(stack, getLoadingDirection().getOpposite());
                final int stackLimit = tile.getInventory().getInventoryStackLimit();
                final IInventory inventory = tile.getInventory();

                for (int index = 0; index < slots.length; index++)
                {
                    final int slot = slots[index];
                    //Check if we can store the exact item
                    if (tile.canStore(stack, slot, getLoadingDirection().getOpposite()))
                    {
                        final ItemStack slotStack = inventory.getStackInSlot(slot);
                        if (slotStack == null)
                        {
                            if (stack.stackSize > stackLimit)
                            {
                                ItemStack copyStack = stack.copy();
                                copyStack.stackSize = stackLimit;
                                inventory.setInventorySlotContents(slot, copyStack);
                                stack.stackSize -= copyStack.stackSize;
                            }
                            else
                            {
                                inventory.setInventorySlotContents(slot, stack);
                                return null;
                            }
                        }
                        else if (InventoryUtility.stacksMatch(slotStack, stack))
                        {
                            final int roomLeft = stackLimit - slotStack.stackSize;
                            if (roomLeft > 0)
                            {
                                slotStack.stackSize += roomLeft;
                                inventory.setInventorySlotContents(slot, slotStack);
                                stack.stackSize -= roomLeft;
                            }
                        }
                    }
                }
            }
        }
        return stack;
    }

    /**
     * Takes the first stack the matches the requirements.
     * <p>
     * Does not actually consume the item
     *
     * @return the item
     */
    public Pair<ItemStack, Integer> takeItemFromTile(ITransportCartHasItem cart)
    {
        final IRailInventoryTile tile = getLoadTile();
        if (tile != null)
        {
            int[] slots = tile.getSlotsToUnload(getLoadingDirection().getOpposite());
            final IInventory inventory = tile.getInventory();

            for (int index = 0; index < slots.length; index++)
            {
                final int slot = slots[index];
                final ItemStack slotStack = inventory.getStackInSlot(slot);
                if (slotStack != null && tile.canRemove(slotStack, getLoadingDirection().getOpposite()))
                {
                    if (cart.canAcceptItemForTransport(slotStack))
                    {
                        return new Pair(slotStack, slot);
                    }
                }
            }
        }
        return null;
    }

    /**
     * Gets the tile that will be used to load or unload
     * into
     *
     * @return
     */
    public IRailInventoryTile getLoadTile()
    {
        Location location = toLocation().add(getLoadingDirection());
        TileEntity tile = location.getTileEntity();
        if (tile instanceof IRailInventoryTile)
        {
            return (IRailInventoryTile) tile;
        }
        return null;
    }

    @Override
    public boolean isUsableRail()
    {
        return !isLoaderExtendTrack();
    }

    /**
     * Pushs the cart
     *
     * @return
     */
    public boolean isPoweredRail()
    {
        return railType == PoweredRails.POWERED;
    }

    /**
     * Rotates the cart
     *
     * @return
     */
    public boolean isRotationRail()
    {
        return railType == PoweredRails.ROTATION;
    }

    /**
     * Changes the side of a tile the cart is on
     *
     * @return
     */
    public boolean isOrientationRail()
    {
        return railType == PoweredRails.ORIENTATION;
    }

    /**
     * Stops a cart
     *
     * @return
     */
    public boolean isStopRail()
    {
        return railType == PoweredRails.STOP;
    }

    /**
     * Loads cargo from carts from a tile
     *
     * @return
     */
    public boolean isLoaderRail()
    {
        return railType == PoweredRails.LOADER;
    }

    /**
     * Unloads cargo from carts into a tile
     *
     * @return
     */
    public boolean isUnloadRail()
    {
        return railType == PoweredRails.UNLOADER;
    }

    /**
     * Two way splitter rail
     * Sends cart left or right
     *
     * @return
     */
    public boolean isSplitterRail()
    {
        return railType == PoweredRails.SPLITTER;
    }

    /**
     * Extender tracks allow tiles to be connected to
     * loaders and unloads a little distance from the
     * track itself
     *
     * @return
     */
    public boolean isLoaderExtendTrack()
    {
        return railType == PoweredRails.EXTENDER;
    }

    /**
     * Calculates that direction to access tiles
     * for loading and unloading.
     */
    protected void setupLoadingDirection()
    {
        switch (getFacingDirection())
        {
            case UP:
                switch (getAttachedDirection())
                {
                    case NORTH:
                        loadDirection = ForgeDirection.WEST;
                        break;
                    case SOUTH:
                        loadDirection = ForgeDirection.EAST;
                        break;
                    case EAST:
                        loadDirection = ForgeDirection.NORTH;
                        break;
                    case WEST:
                        loadDirection = ForgeDirection.SOUTH;
                        break;
                }
                break;
            case DOWN:
                switch (getAttachedDirection())
                {
                    case NORTH:
                        loadDirection = ForgeDirection.EAST;
                        break;
                    case SOUTH:
                        loadDirection = ForgeDirection.WEST;
                        break;
                    case EAST:
                        loadDirection = ForgeDirection.SOUTH;
                        break;
                    case WEST:
                        loadDirection = ForgeDirection.NORTH;
                        break;
                }
                break;
            case NORTH:
                switch (getAttachedDirection())
                {
                    case EAST:
                        loadDirection = ForgeDirection.DOWN;
                        break;
                    case WEST:
                        loadDirection = ForgeDirection.UP;
                        break;
                    default:
                        loadDirection = ForgeDirection.EAST;
                }
                break;
            case SOUTH:
                switch (getAttachedDirection())
                {
                    case EAST:
                        loadDirection = ForgeDirection.UP;
                        break;
                    case WEST:
                        loadDirection = ForgeDirection.DOWN;
                        break;
                    default:
                        loadDirection = ForgeDirection.WEST;
                }
                break;
            case WEST:
                switch (getAttachedDirection())
                {
                    case NORTH:
                        loadDirection = ForgeDirection.DOWN;
                        break;
                    case SOUTH:
                        loadDirection = ForgeDirection.UP;
                        break;
                    default:
                        loadDirection = ForgeDirection.NORTH;
                }
                break;
            case EAST:
                switch (getAttachedDirection())
                {
                    case NORTH:
                        loadDirection = ForgeDirection.UP;
                        break;
                    case SOUTH:
                        loadDirection = ForgeDirection.DOWN;
                        break;
                    default:
                        loadDirection = ForgeDirection.SOUTH;
                }
                break;
        }
        if (!rotateClockwise)
        {
            loadDirection = loadDirection.getOpposite();
        }
    }

    /**
     * Sets the loading direction for accessing tiles
     *
     * @param direction
     */
    public void setLoadingDirection(ForgeDirection direction)
    {
        this.loadDirection = direction;
    }

    /**
     * Direction to load/unload items from tiles
     *
     * @return direction
     */
    public ForgeDirection getLoadingDirection()
    {
        if (loadDirection == null)
        {
            setupLoadingDirection();
        }
        return loadDirection;
    }

    @Override
    public ForgeDirection getAttachedDirection()
    {
        if (attachedSide == null)
        {
            attachedSide = ForgeDirection.getOrientation(getMetadata());
        }
        return attachedSide;
    }

    public void setFacingDirection(ForgeDirection facingDirection)
    {
        this.facingDirection = facingDirection;
        if (world() != null && isServer())
        {
            sendDescPacket();
        }
    }

    /** Direction we are facing */
    @Override
    public ForgeDirection getFacingDirection()
    {
        return facingDirection;
    }

    @Override
    public float getRailHeight()
    {
        return BlockRail.RAIL_HEIGHT;
    }

    protected void handlePush(ITransportCart cart)
    {
        if (isServer())
        {
            cart.recenterCartOnRail(this, false);
            Pos pos = new Pos(facingDirection).multiply(cart.getDesiredPushVelocity());
            cart.setMotion(pos.x(), pos.y(), pos.z());
        }
    }

    @Override
    protected boolean onPlayerRightClick(EntityPlayer player, int side, Pos hit)
    {
        if (Engine.runningAsDev)
        {
            if (player.getHeldItem() != null && player.getHeldItem().getItem() == Items.stick)
            {
                if (isServer())
                {
                    player.addChatMessage(new ChatComponentText("A: " + getAttachedDirection() + " F:" + getFacingDirection() + " T:" + railType));
                    if (isUnloadRail() || isLoaderRail())
                    {
                        setupLoadingDirection();
                        player.addChatMessage(new ChatComponentText("L: " + loadDirection + " C: " + rotateClockwise));
                    }
                }
                return true;
            }
        }
        return false;
    }

    @Override
    protected boolean onPlayerRightClickWrench(EntityPlayer player, int side, Pos hit)
    {
        if (player.isSneaking())
        {
            if (isRotationRail() || isUnloadRail() || isLoaderRail())
            {
                rotateClockwise = !rotateClockwise;
                setupLoadingDirection();
                if (isServer())
                {
                    sendDescPacket();
                }
                else
                {
                    world().markBlockRangeForRenderUpdate(xi(), yi(), zi(), xi(), yi(), zi());
                }
            }
        }
        else
        {
            if (side == 0 || side == 1)
            {
                boolean high = hit.z() >= 0.7;
                boolean low = hit.z() <= 0.3;
                //Left & right are inverted for South
                boolean left = hit.x() <= 0.3;
                boolean right = hit.x() >= 0.7;

                if (!left && !right)
                {
                    if (high)
                    {
                        setFacingDirection(ForgeDirection.SOUTH);
                    }
                    else if (low)
                    {
                        setFacingDirection(ForgeDirection.NORTH);
                    }
                }
                else
                {
                    if (left)
                    {
                        setFacingDirection(side == 0 ? ForgeDirection.EAST : ForgeDirection.WEST);
                    }
                    else if (right)
                    {
                        setFacingDirection(side == 0 ? ForgeDirection.WEST : ForgeDirection.EAST);
                    }
                }
            }
            //North South
            else if (side == 2 || side == 3)
            {
                boolean high = hit.y() >= 0.7;
                boolean low = hit.y() <= 0.3;
                //Left & right are inverted for South
                boolean left = hit.x() <= 0.3;
                boolean right = hit.x() >= 0.7;

                if (!left && !right)
                {
                    if (high)
                    {
                        setFacingDirection(ForgeDirection.UP);
                    }
                    else if (low)
                    {
                        setFacingDirection(ForgeDirection.DOWN);
                    }
                }
                else
                {
                    if (left)
                    {
                        setFacingDirection(ForgeDirection.WEST);
                    }
                    else if (right)
                    {
                        setFacingDirection(ForgeDirection.EAST);
                    }
                }
            }
            //West East
            else if (side == 4 || side == 5)
            {
                boolean high = hit.y() >= 0.7;
                boolean low = hit.y() <= 0.3;
                //Left & right are inverted for East
                boolean left = hit.z() <= 0.3;
                boolean right = hit.z() >= 0.7;

                if (!left && !right)
                {
                    if (high)
                    {
                        setFacingDirection(ForgeDirection.UP);
                    }
                    else if (low)
                    {
                        setFacingDirection(ForgeDirection.DOWN);
                    }
                }
                else
                {
                    if (left)
                    {
                        setFacingDirection(ForgeDirection.NORTH);
                    }
                    else if (right)
                    {
                        setFacingDirection(ForgeDirection.SOUTH);
                    }
                }
            }
            world().markBlockRangeForRenderUpdate(xi(), yi(), zi(), xi(), yi(), zi());
        }
        return true;
    }

    @Override
    public void writeDescPacket(ByteBuf buf)
    {
        buf.writeInt(railType.ordinal());
        buf.writeInt(getFacingDirection().ordinal());
        if (isRotationRail())
        {
            buf.writeBoolean(rotateToAngle);
            buf.writeBoolean(rotateClockwise);
            buf.writeInt(rotateYaw);
        }
        else if (isStopRail())
        {
            buf.writeBoolean(stopCarts);
        }
        else if (isLoaderRail() || isUnloadRail())
        {
            buf.writeBoolean(rotateClockwise);
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt)
    {
        super.readFromNBT(nbt);
        if (nbt.hasKey("facingDirection"))
        {
            setFacingDirection(ForgeDirection.getOrientation(nbt.getInteger("facingDirection")));
        }
        railType = PoweredRails.get(nbt.getInteger("railType"));
        rotateClockwise = nbt.getBoolean("rotateClockwise");
        if (isRotationRail())
        {
            if (nbt.hasKey("rotateToAngle"))
            {
                rotateToAngle = nbt.getBoolean("rotateToAngle");
            }
            if (nbt.hasKey("rotationYaw"))
            {
                rotateYaw = nbt.getInteger("rotationYaw");
            }
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt)
    {
        super.writeToNBT(nbt);
        nbt.setInteger("facingDirection", getFacingDirection().ordinal());
        nbt.setInteger("railType", railType.ordinal());
        nbt.setBoolean("rotateClockwise", rotateClockwise);
        if (isRotationRail())
        {
            nbt.setBoolean("rotateToAngle", rotateToAngle);
            nbt.setInteger("rotationYaw", rotateYaw);
        }
    }

    @Override
    public Cube getCollisionBounds()
    {
        if (world() != null)
        {
            final int meta = world().getBlockMetadata(xi(), yi(), zi());
            final ForgeDirection dir = ForgeDirection.getOrientation(meta);
            switch (dir)
            {
                case DOWN:
                    return COLLISION_BOX_DOWN;
                case NORTH:
                    return COLLISION_BOX_NORTH;
                case SOUTH:
                    return COLLISION_BOX_SOUTH;
                case EAST:
                    return COLLISION_BOX_EAST;
                case WEST:
                    return COLLISION_BOX_WEST;
            }
        }
        return bounds;
    }

    @Override
    public void getSubBlocks(Item item, CreativeTabs creativeTabs, List list)
    {
        for (PoweredRails rails : PoweredRails.values())
        {
            list.add(new ItemStack(item, 1, rails.ordinal()));
        }
    }
}
