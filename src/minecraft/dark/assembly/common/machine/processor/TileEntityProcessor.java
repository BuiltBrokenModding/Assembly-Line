package dark.assembly.common.machine.processor;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.ForgeDirection;
import dark.assembly.common.machine.processor.ProcessorRecipes.ProcessorType;
import dark.core.prefab.TileEntityMachine;
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

    @Override
    public void updateEntity()
    {
        if (this.type == null)
        {
            if (this.getBlockMetadata() == 0 || this.getBlockMetadata() == 1)
            {
                this.type = ProcessorType.CRUSHER;
            }
        }
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
                else if (ItemStack.areItemStacksEqual(outputResult, outputStack))
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
        System.out.println("Processing stack");
        ItemStack inputSlotStack = this.getInventory().getStackInSlot(this.slotInput);
        ItemStack outputSlotStack = this.getInventory().getStackInSlot(this.slotOutput);
        if (inputSlotStack != null)
        {

            inputSlotStack = inputSlotStack.copy();
            inputSlotStack.stackSize = 1;
            ItemStack receipeResult = ProcessorRecipes.getOuput(this.type, inputSlotStack);
            System.out.println("Input = " + inputSlotStack.toString());
            System.out.println("Output = " + (outputSlotStack == null ? "Null" : outputSlotStack.toString()));
            System.out.println("Result = " + (receipeResult == null ? "Null" : receipeResult.toString()));
            if (receipeResult != null && (outputSlotStack == null || ItemStack.areItemStacksEqual(outputSlotStack, receipeResult)))
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

    @Override
    public String getInvName()
    {
        if (this.type == ProcessorType.CRUSHER)
        {
            return "gui.crushor.name";
        }
        if (this.type == ProcessorType.GRINDER)
        {
            return "gui.grinder.name";
        }
        if (this.type == ProcessorType.PRESS)
        {
            return "gui.press.name";
        }
        return "gui.processor.name";
    }

    @Override
    public boolean isInvNameLocalized()
    {
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
