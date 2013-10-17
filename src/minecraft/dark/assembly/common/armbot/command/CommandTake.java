package dark.assembly.common.armbot.command;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import universalelectricity.core.vector.Vector3;

import com.builtbroken.common.science.units.UnitHelper;

import dark.api.al.armbot.ILogicDevice;
import dark.assembly.common.armbot.TaskArmbot;
import dark.assembly.common.armbot.TaskBase;
import dark.assembly.common.machine.InvInteractionHelper;
import dark.core.prefab.helpers.MathHelper;

public class CommandTake extends TaskArmbot
{

    protected ItemStack stack;
    protected int ammount = -1;

    public CommandTake()
    {
        super("Take", TaskType.DEFINEDPROCESS);
    }

    @Override
    public ProcessReturn onMethodCalled(World world, Vector3 location, ILogicDevice armbot)
    {
        super.onMethodCalled(world, location, armbot);

        ammount = UnitHelper.tryToParseInt(this.getArg(1), -1);

        stack = this.getItem(this.getArg(0), ammount == -1 ? 1 : ammount);

        return ProcessReturn.CONTINUE;
    }

    @Override
    public ProcessReturn onUpdate()
    {
        TileEntity targetTile = this.armbot.getHandPos().getTileEntity(this.worldObj);

        if (targetTile != null && this.armbot.getGrabbedObjects().size() <= 0)
        {
            ForgeDirection direction = MathHelper.getFacingDirectionFromAngle(this.armbot.getRotation().x);
            List<ItemStack> stacks = new ArrayList<ItemStack>();
            if (this.stack != null)
            {
                stacks.add(stack);
            }
            InvInteractionHelper invEx = new InvInteractionHelper(this.worldObj, this.armbotPos, stacks, false);
            this.armbot.grab(invEx.tryGrabFromPosition(new Vector3(targetTile), direction, this.stack != null ? stack.stackSize : 1));
            return this.armbot.getGrabbedObjects().size() > 0 ? ProcessReturn.DONE : ProcessReturn.CONTINUE;

        }
        return ProcessReturn.CONTINUE;
    }

    @Override
    public String toString()
    {
        return super.toString() + " " + (stack != null ? stack.toString() : "1x???@???  ");
    }

    @Override
    public CommandTake load(NBTTagCompound taskCompound)
    {
        super.loadProgress(taskCompound);
        this.stack = ItemStack.loadItemStackFromNBT(taskCompound.getCompoundTag("item"));
        return this;
    }

    @Override
    public NBTTagCompound save(NBTTagCompound taskCompound)
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
        return new CommandTake();
    }
}
