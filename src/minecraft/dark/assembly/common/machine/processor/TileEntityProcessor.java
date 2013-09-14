package dark.assembly.common.machine.processor;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.ForgeDirection;
import dark.assembly.common.machine.processor.ProcessorRecipes.ProcessorType;
import dark.core.interfaces.IInvBox;
import dark.core.prefab.TileEntityMachine;
import dark.core.prefab.invgui.InvChest;

/** Basic A -> B recipe processor machine designed mainly to handle ore blocks
 *
 * @author DarkGuardsman */
public class TileEntityProcessor extends TileEntityMachine
{
    private int slotInput = 0, slotOutput = 1, slotBatteryCharge = 2, slotBatteryDrain = 3;

    private int processingTicks = 0;
    private int processingTime = 100;
    public int renderStage = 1;

    public ProcessorType type;

    public TileEntityProcessor(ProcessorType type)
    {
        this.type = type;
    }

    @Override
    public void updateEntity()
    {
        super.updateEntity();
        if (this.running)
        {
            if (this.ticks % 5 == 0)
            {
                renderStage = renderStage++;
                if (renderStage >= 8)
                {
                    renderStage = 1;
                }
            }
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

    @Override
    public boolean canRun()
    {
        return super.canRun() && this.canProcess();
    }

    /** Can the machine process the itemStack */
    public boolean canProcess()
    {
        ItemStack processResult = ProcessorRecipes.getOuput(this.type, this.getInventory().getStackInSlot(this.slotInput));
        ItemStack outputStack = this.getInventory().getStackInSlot(this.slotOutput);
        if (processResult != null)
        {
            if (outputStack == null)
            {
                return true;
            }
            else if (outputStack.equals(processResult))
            {
                if (Math.min(outputStack.getMaxStackSize() - outputStack.stackSize, this.getInventoryStackLimit()) >= processResult.stackSize)
                {
                    return true;
                }
            }
        }
        return false;
    }

    /** Processes the itemStack */
    public void process()
    {
        ItemStack output = ProcessorRecipes.getOuput(this.type, this.getInventory().getStackInSlot(this.slotInput));
        ItemStack outputSlot = this.getInventory().getStackInSlot(this.slotOutput);
        if (output != null && outputSlot != null && output.equals(outputSlot))
        {
            ItemStack outputStack = outputSlot.copy();
            outputStack.stackSize += outputSlot.stackSize;
            this.getInventory().decrStackSize(this.slotInput, 1);
            this.getInventory().setInventorySlotContents(this.slotOutput, outputStack);
        }
    }

    @Override
    public IInvBox getInventory()
    {
        if (inventory == null)
        {
            inventory = new InvChest(this, 4);
        }
        return inventory;
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
        return new int[] { slotBatteryDrain };
    }

    public ForgeDirection getDirection()
    {
        int meta = this.worldObj.getBlockMetadata(xCoord, yCoord, zCoord);
        if (meta == 0)
        {
            return ForgeDirection.NORTH;
        }
        return ForgeDirection.EAST;
    }

    /** NBT Data */
    @Override
    public void readFromNBT(NBTTagCompound nbt)
    {
        super.readFromNBT(nbt);
        this.processingTicks = nbt.getInteger("processingTicks");
        this.renderStage = nbt.getInteger("renderStage");

    }

    @Override
    public void writeToNBT(NBTTagCompound nbt)
    {
        super.writeToNBT(nbt);
        nbt.setInteger("processingTicks", this.processingTicks);
        nbt.setInteger("renderStage", this.renderStage);

    }

}
