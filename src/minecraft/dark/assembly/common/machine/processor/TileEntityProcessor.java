package dark.assembly.common.machine.processor;

import java.io.DataInputStream;
import java.io.IOException;

import universalelectricity.core.vector.Vector3;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.packet.Packet;
import net.minecraftforge.common.ForgeDirection;
import dark.api.ProcessorRecipes;
import dark.api.ProcessorRecipes.ProcessorType;
import dark.core.network.PacketHandler;
import dark.core.prefab.TileEntityMachine;
import dark.core.prefab.TileEntityMachine.TilePacketTypes;
import dark.core.prefab.invgui.InvChest;

/** Basic A -> B recipe processor machine designed mainly to handle ore blocks
 *
 * @author DarkGuardsman */
public class TileEntityProcessor extends TileEntityMachine
{
    public int slotInput = 0, slotOutput = 1, slotBatteryCharge = 2, slotBatteryDrain = 3;

    public int processingTicks = 0;
    public int processingTime = 100;
    public int renderStage = 1;

    public ProcessorType type;

    public boolean invertPiston = false;

    @Override
    public void updateEntity()
    {
        if (this.type == null)
        {
            if (this.getBlockMetadata() == 0 || this.getBlockMetadata() == 1)
            {
                this.type = ProcessorType.CRUSHER;
                this.WATTS_PER_TICK = BlockProcessor.crusherWattPerTick;
            }
            else if (this.getBlockMetadata() == 4 || this.getBlockMetadata() == 5)
            {
                this.type = ProcessorType.GRINDER;
                this.WATTS_PER_TICK = BlockProcessor.grinderWattPerTick;
            }
            else if (this.getBlockMetadata() == 8 || this.getBlockMetadata() == 9)
            {
                this.type = ProcessorType.PRESS;
                this.WATTS_PER_TICK = BlockProcessor.pressWattPerTick;
            }

            this.MAX_WATTS = this.WATTS_PER_TICK * 20;
        }
        super.updateEntity();
        if (this.running)
        {
            this.doAnimation();

            if (!this.worldObj.isRemote)
            {

                if (this.processingTicks++ >= this.processingTime)
                {
                    this.processingTicks = 0;
                    this.process();
                }
            }
        }
    }

    public void doAnimation()
    {
        if (this.type == ProcessorType.CRUSHER || this.type == ProcessorType.PRESS)
        {
            if (invertPiston)
            {
                if (renderStage-- <= 0)
                {
                    invertPiston = false;
                }
            }
            else
            {
                if (renderStage++ >= 8)
                {
                    invertPiston = true;
                }
            }
        }
        else
        {
            if (renderStage++ >= 8)
            {
                renderStage = 1;
            }
        }
    }

    @Override
    public boolean canRun()
    {
        return super.canRun() && this.canProcess();
    }

    /** Can the machine process the itemStack */
    public boolean canProcess()
    {
        ItemStack inputStack = this.getInventory().getStackInSlot(this.slotInput);
        ItemStack outputStack = this.getInventory().getStackInSlot(this.slotOutput);
        if (inputStack != null)
        {
            inputStack = inputStack.copy();
            inputStack.stackSize = 1;
            ItemStack outputResult = ProcessorRecipes.getOuput(this.type, inputStack);
            if (outputResult != null)
            {
                if (outputStack == null)
                {
                    return true;
                }
                else if (outputResult.isItemEqual(outputStack))
                {
                    if (Math.min(outputStack.getMaxStackSize() - outputStack.stackSize, this.getInventoryStackLimit()) >= outputResult.stackSize)
                    {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /** Processes the itemStack */
    public void process()
    {
        ItemStack inputSlotStack = this.getInventory().getStackInSlot(this.slotInput);
        ItemStack outputSlotStack = this.getInventory().getStackInSlot(this.slotOutput);
        if (inputSlotStack != null)
        {

            inputSlotStack = inputSlotStack.copy();
            inputSlotStack.stackSize = 1;
            ItemStack receipeResult = ProcessorRecipes.getOuput(this.type, inputSlotStack);
            if (receipeResult != null && (outputSlotStack == null || outputSlotStack.isItemEqual(receipeResult)))
            {

                ItemStack outputStack = outputSlotStack == null ? receipeResult : outputSlotStack.copy();
                if (outputSlotStack != null)
                {
                    outputStack.stackSize += receipeResult.stackSize;
                }
                this.getInventory().decrStackSize(this.slotInput, 1);
                this.getInventory().setInventorySlotContents(this.slotOutput, outputStack);
            }
        }
    }

    @Override
    public InvChest getInventory()
    {
        if (inventory == null)
        {
            inventory = new InvChest(this, 4);
        }
        return (InvChest) inventory;
    }

    @Override
    public boolean canStore(ItemStack stack, int slot, ForgeDirection side)
    {
        if (slotInput == slot && ProcessorRecipes.getOuput(this.type, stack) != null)
        {
            return true;
        }
        if (slotBatteryDrain == slot && this.isBattery(stack))
        {
            return true;
        }
        return false;
    }

    @Override
    public boolean canRemove(ItemStack stack, int slot, ForgeDirection side)
    {
        return slot != slotInput;
    }

    @Override
    public int[] getAccessibleSlotsFromSide(int side)
    {
        if (side == ForgeDirection.DOWN.ordinal())
        {
            return new int[] { slotOutput };
        }
        if (side == ForgeDirection.UP.ordinal())
        {
            return new int[] { slotInput };
        }
        return new int[] { slotBatteryDrain, slotInput, slotOutput };
    }

    public ForgeDirection getDirection()
    {
        if (this.worldObj.getBlockMetadata(xCoord, yCoord, zCoord) == 0)
        {
            return ForgeDirection.NORTH;
        }
        return ForgeDirection.EAST;
    }

    @Override
    public String getInvName()
    {
        if (this.type != null)
        {
            return type.unlocalizedContainerName;
        }
        return "gui.processor.name";
    }

    @Override
    public void sendGUIPacket(EntityPlayer entity)
    {
        if (!this.worldObj.isRemote && entity instanceof EntityPlayerMP)
        {
            ((EntityPlayerMP) entity).playerNetServerHandler.sendPacketToPlayer(PacketHandler.instance().getPacket(this.getChannel(), this, TilePacketTypes.GUI.name, this.processingTicks, this.processingTime, this.energyStored));
        }
    }

    public boolean simplePacket(String id, DataInputStream dis, EntityPlayer player)
    {
        if (!super.simplePacket(id, dis, player))
        {
            try
            {
                if (this.worldObj.isRemote)
                {
                    if (id.equalsIgnoreCase(TilePacketTypes.GUI.name))
                    {
                        this.processingTicks = dis.readInt();
                        this.processingTime = dis.readInt();
                        this.setEnergyStored(dis.readFloat());
                        return true;
                    }
                }
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
        return false;
    }

    /** NBT Data */
    @Override
    public void readFromNBT(NBTTagCompound nbt)
    {
        super.readFromNBT(nbt);
        this.processingTicks = nbt.getInteger("processingTicks");
        this.renderStage = nbt.getInteger("renderStage");
        this.getInventory().loadInv(nbt);

    }

    @Override
    public void writeToNBT(NBTTagCompound nbt)
    {
        super.writeToNBT(nbt);
        nbt.setInteger("processingTicks", this.processingTicks);
        nbt.setInteger("renderStage", this.renderStage);
        this.getInventory().saveInv(nbt);

    }

}
