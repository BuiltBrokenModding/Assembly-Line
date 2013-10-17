package dark.assembly.common.armbot.command;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import universalelectricity.core.vector.Vector3;

import com.builtbroken.common.science.units.UnitHelper;

import dark.api.al.coding.IArmbot;
import dark.api.al.coding.ILogicDevice;
import dark.assembly.common.armbot.TaskBase;
import dark.assembly.common.armbot.TaskArmbot;
import dark.assembly.common.machine.InvInteractionHelper;
import dark.core.prefab.helpers.MathHelper;

public class CommandGive extends TaskArmbot
{

    private ItemStack stack;
    private int ammount = -1;

    public CommandGive()
    {
        super("give", TaskType.DEFINEDPROCESS);
    }

    @Override
    public ProcessReturn onMethodCalled(World world, Vector3 location, ILogicDevice armbot)
    {
        super.onMethodCalled(world, location, armbot);

        if (this.getArgs().length > 1)
        {
            ammount = UnitHelper.tryToParseInt("" + this.getArg(1));
        }

        if (this.getArgs().length > 0)
        {
            stack = this.getItem("" + this.getArg(0), ammount == -1 ? 1 : ammount);
        }

        return ProcessReturn.CONTINUE;

    }

    @Override
    public ProcessReturn onUpdate()
    {
        TileEntity targetTile = this.armbot.getHandPos().getTileEntity(this.worldObj);

        if (targetTile != null && this.armbot.getGrabbedObjects().size() > 0)
        {
            ForgeDirection direction = MathHelper.getFacingDirectionFromAngle((float) this.armbot.getRotation().x);
            List<ItemStack> stacks = new ArrayList<ItemStack>();
            if (this.stack != null)
            {
                stacks.add(stack);
            }
            InvInteractionHelper invEx = new InvInteractionHelper(this.worldObj, this.armbotPos, stacks, false);

            Iterator<Object> targetIt = this.armbot.getGrabbedObjects().iterator();
            boolean itemsLeft = false;
            while (targetIt.hasNext())
            {
                Object object = targetIt.next();
                if (object instanceof ItemStack)
                {
                    ItemStack insertStack = (ItemStack) object;
                    insertStack = invEx.tryPlaceInPosition(insertStack, new Vector3(targetTile), direction.getOpposite());
                    itemsLeft = insertStack != null;
                    if (insertStack == null || insertStack.stackSize <= 0)
                    {
                        targetIt.remove();
                        break;
                    }
                }
            }
            return itemsLeft ? ProcessReturn.CONTINUE : ProcessReturn.DONE;
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
    public boolean canUseTask(ILogicDevice device)
    {
        // TODO Auto-generated method stub
        return false;
    }
}
