package com.builtbroken.assemblyline.content.belt;

import com.builtbroken.assemblyline.AssemblyLine;
import com.builtbroken.assemblyline.content.belt.pipe.BeltType;
import com.builtbroken.assemblyline.content.belt.pipe.PipeInventory;
import com.builtbroken.assemblyline.content.belt.pipe.data.BeltSlotState;
import com.builtbroken.assemblyline.content.belt.pipe.data.BeltState;
import com.builtbroken.jlib.type.Pair;
import com.builtbroken.mc.api.data.IPacket;
import com.builtbroken.mc.api.tile.access.IRotation;
import com.builtbroken.mc.api.tile.provider.IInventoryProvider;
import com.builtbroken.mc.codegen.annotations.TileWrapped;
import com.builtbroken.mc.core.network.packet.PacketType;
import com.builtbroken.mc.framework.logic.TileNode;
import com.builtbroken.mc.lib.helper.BlockUtility;
import com.builtbroken.mc.prefab.inventory.BasicInventory;
import com.builtbroken.mc.prefab.inventory.InventoryUtility;
import cpw.mods.fml.common.network.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 11/14/2017.
 */
@TileWrapped(className = ".gen.TileEntityWrappedPipeBelt", wrappers = "ExternalInventory")
public class TilePipeBelt extends TileNode implements IRotation, IInventoryProvider<PipeInventory>
{
    public static final int PACKET_INVENTORY = 1;
    //TODO fixed sided slots for inventory
    /** Cached state map of direction to input sides & slots */
    public static BeltSlotState[][][] inputStates;
    /** Cached state map of direction to output sides & slots */
    public static BeltSlotState[][][] outputStates;

    public static int[] centerSlots = new int[]{2};

    //Main inventory
    PipeInventory inventory;

    //Cached facing direction
    private ForgeDirection _direction;

    //Type of belt
    public BeltType type = BeltType.NORMAL;

    //Belt states, used for filters and inverting belt directions
    private BeltState[] beltStates = new BeltState[4];

    private boolean sendInvToClient = true;
    private boolean pullItems = false;

    public BasicInventory renderInventory;

    public TilePipeBelt()
    {
        super("pipeBelt", AssemblyLine.DOMAIN);
    }

    @Override
    public void update(long ticks)
    {
        super.update(ticks);
        if (isServer())
        {
            if (ticks % 20 == 0)
            {
                //Inventory movement
                //1. Push items from output to tiles
                //2. Move center slots to outputs (sorting)
                //3. Move inputs to center (round-robin)
                //4. Pull items into inputs from tiles


                //Push outputs to next belt or machine
                BeltSlotState[] states = getOutputs();
                if (states != null)
                {
                    for (BeltSlotState slotState : states)
                    {
                        if (slotState != null)
                        {
                            ItemStack stack = getInventory().getStackInSlot(slotState.slotID);
                            if (stack != null)
                            {
                                stack = InventoryUtility.insertStack(toLocation().add(slotState.side), stack, slotState.side.ordinal(), false);
                                getInventory().setInventorySlotContents(slotState.slotID, stack);
                            }
                        }
                    }
                }

                int[] centerSlots = getCenterSlots();

                //Center to output
                if (states != null)
                {
                    for (BeltSlotState slotState : states)
                    {
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
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }

                //TODO round robin to ensure each belt has a chance to move to center
                //TODO store inputs as list object with current index
                //TODO only move index forward if we moved an item
                //Index will be used to note starting point for loop
                //Example: 0 1 2 (moved item) next tick > 1 2 0 (moved item) > 2 0 1

                //Input to center
                states = getInputs();
                if (states != null)
                {
                    for (BeltSlotState slotState : states)
                    {
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
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }

                //Pull items from machines
                if (states != null && pullItems)
                {
                    for (BeltSlotState slotState : states)
                    {
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
                                if (tileInv != null)
                                {
                                    BeltState beltState = getStateForSide(slotState.side);
                                    Pair<ItemStack, Integer> slotData = InventoryUtility.findFirstItemInInventory(tileInv,
                                            slotState.side.getOpposite().ordinal(), getItemsToPullPerCycle(), beltState != null ? beltState.filter : null);
                                    if (slotData != null)
                                    {
                                        ItemStack inputStack = tileInv.decrStackSize(slotData.right(), slotData.left().stackSize);
                                        getInventory().setInventorySlotContents(slotState.slotID, inputStack);
                                    }
                                }
                            }
                        }
                    }
                }
            }

            if (sendInvToClient) //TODO add settings to control packet update times
            {
                sendInvToClient = false;
                sendInventoryPacket();
            }
        }
    }

    public int getItemsToPullPerCycle()
    {
        return 1;
    }

    public BeltState getStateForSide(ForgeDirection direction)
    {
        return beltStates[BlockUtility.directionToRotation(direction)];
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
        BeltSlotState[] states = getInputs();
        if (states != null)
        {
            for (BeltSlotState state : states)
            {
                if (state != null && slot == state.slotID)
                {
                    return state.side.getOpposite() == side;
                }
            }
        }
        return false;
    }

    @Override
    public boolean canRemove(ItemStack stack, int slot, ForgeDirection side)
    {
        BeltSlotState[] states = getOutputs();
        if (states != null)
        {
            for (BeltSlotState state : states)
            {
                if (state != null && slot == state.slotID)
                {
                    return state.side.getOpposite() == side;
                }
            }
        }
        return false;
    }

    @Override
    public void onInventoryChanged(int slot, ItemStack prev, ItemStack item)
    {
        if (isServer())
        {
            sendInvToClient = true;
        }
    }

    public BeltSlotState[] getInputs()
    {
        if (type != BeltType.T_SECTION && type != BeltType.INTERSECTION)
        {
            return inputStates[type.ordinal()][getDirection().ordinal()];
        }
        return null;
    }

    public BeltSlotState[] getOutputs()
    {
        if (type != BeltType.T_SECTION && type != BeltType.INTERSECTION)
        {
            return outputStates[type.ordinal()][getDirection().ordinal()];
        }
        return null;
    }

    public int[] getCenterSlots()
    {
        return centerSlots;
    }

    @Override
    public void readDescPacket(ByteBuf buf)
    {
        super.readDescPacket(buf);
        type = BeltType.values()[buf.readInt()];
        readInvPacket(buf);
    }


    @Override
    public void writeDescPacket(ByteBuf buf)
    {
        super.writeDescPacket(buf);
        buf.writeInt(type.ordinal());
        writeInvPacket(buf);
    }

    public void readInvPacket(ByteBuf buf)
    {
        int size = buf.readInt();
        if(renderInventory == null || renderInventory.getSizeInventory() != size)
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

    @Override
    public boolean read(ByteBuf buf, int id, EntityPlayer player, PacketType type)
    {
        if (!super.read(buf, id, player, type))
        {
            if (id == PACKET_INVENTORY)
            {
                readInvPacket(buf);
                return true;
            }
            return false;
        }
        return true;
    }

    static
    {
        inputStates = new BeltSlotState[3][6][];
        outputStates = new BeltSlotState[3][6][];
        ForgeDirection[] rotations = new ForgeDirection[]{ForgeDirection.NORTH, ForgeDirection.SOUTH, ForgeDirection.EAST, ForgeDirection.WEST};
        for (ForgeDirection direction : rotations)
        {
            inputStates[BeltType.NORMAL.ordinal()][direction.ordinal()] = new BeltSlotState[]{new BeltSlotState(0, direction)};
            outputStates[BeltType.NORMAL.ordinal()][direction.ordinal()] = new BeltSlotState[]{new BeltSlotState(1, direction.getOpposite())};

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

            inputStates[BeltType.LEFT_ELBOW.ordinal()][direction.ordinal()] = new BeltSlotState[]{new BeltSlotState(0, direction)};
            outputStates[BeltType.LEFT_ELBOW.ordinal()][direction.ordinal()] = new BeltSlotState[]{new BeltSlotState(1, turn.getOpposite())};

            inputStates[BeltType.RIGHT_ELBOW.ordinal()][direction.ordinal()] = new BeltSlotState[]{new BeltSlotState(0, direction)};
            outputStates[BeltType.RIGHT_ELBOW.ordinal()][direction.ordinal()] = new BeltSlotState[]{new BeltSlotState(1, turn)};
        }
    }
}
