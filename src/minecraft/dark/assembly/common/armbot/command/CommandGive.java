package dark.assembly.common.armbot.command;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import universalelectricity.core.vector.Vector3;

import com.builtbroken.common.science.units.UnitHelper;

import dark.api.al.coding.IProgramableMachine;
import dark.api.al.coding.args.ArgumentIntData;
import dark.assembly.common.armbot.TaskArmbot;
import dark.assembly.common.armbot.TaskBase;
import dark.assembly.common.machine.InvInteractionHelper;
import dark.core.prefab.helpers.MathHelper;

public class CommandGive extends TaskArmbot
{

    private ItemStack stack;
    private int ammount = -1;

    public CommandGive()
    {
        super("give", TaskType.DEFINEDPROCESS);
        this.defautlArguments.add(new ArgumentIntData("blockID", -1, Block.blocksList.length - 1, -1));
        this.defautlArguments.add(new ArgumentIntData("blockMeta", -1, 15, -1));
        this.defautlArguments.add(new ArgumentIntData("stackSize", -1, 64, -1));
    }

    @Override
    public ProcessReturn onMethodCalled(World world, Vector3 location, IProgramableMachine armbot)
    {
        super.onMethodCalled(world, location, armbot);

        ammount = UnitHelper.tryToParseInt(this.getArg("stackSize"), -1);
        int blockID = UnitHelper.tryToParseInt(this.getArg("blockID"), -1);
        int blockMeta = UnitHelper.tryToParseInt(this.getArg("blockMeta"), 32767);

        if (blockID > 0)
        {
            stack = new ItemStack(blockID, ammount <= 0 ? 1 : ammount, blockMeta == -1 ? 32767 : blockMeta);
        }

        return ProcessReturn.CONTINUE;

    }

    @Override
    public ProcessReturn onUpdate()
    {
        TileEntity targetTile = this.armbot.getHandPos().getTileEntity(this.worldObj);

        if (targetTile != null && this.armbot.getGrabbedObject() instanceof ItemStack)
        {
            ForgeDirection direction = MathHelper.getFacingDirectionFromAngle((float) this.armbot.getRotation().x);
            ItemStack itemStack = (ItemStack) this.armbot.getGrabbedObject();
            List<ItemStack> stacks = new ArrayList<ItemStack>();
            if (this.stack != null)
            {
                stacks.add(stack);
            }
            InvInteractionHelper invEx = new InvInteractionHelper(this.worldObj, this.devicePos, stacks, false);
            ItemStack insertStack = invEx.tryPlaceInPosition(itemStack, new Vector3(targetTile), direction.getOpposite());
            if (this.armbot.clear(itemStack))
            {
                this.armbot.grab(insertStack);
            }
        }
        return ProcessReturn.CONTINUE;
    }

    @Override
    public String toString()
    {
        return super.toString() + " " + (stack != null ? stack.toString() : "1x???@???");
    }

    @Override
    public TaskBase loadProgress(NBTTagCompound taskCompound)
    {
        super.loadProgress(taskCompound);
        this.stack = ItemStack.loadItemStackFromNBT(taskCompound.getCompoundTag("item"));
        return this;
    }

    @Override
    public NBTTagCompound saveProgress(NBTTagCompound taskCompound)
    {
        super.saveProgress(taskCompound);
        if (stack != null)
        {
            NBTTagCompound tag = new NBTTagCompound();
            this.stack.writeToNBT(tag);
            taskCompound.setTag("item", tag);
        }
        return taskCompound;
    }

    @Override
    public TaskBase clone()
    {
        return new CommandGive();
    }

    @Override
    public boolean canUseTask(IProgramableMachine device)
    {
        // TODO Auto-generated method stub
        return false;
    }
}
