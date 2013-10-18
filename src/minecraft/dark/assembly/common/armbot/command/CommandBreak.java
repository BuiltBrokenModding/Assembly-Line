package dark.assembly.common.armbot.command;

import java.util.ArrayList;

import net.minecraft.block.Block;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import universalelectricity.core.vector.Vector3;
import dark.api.al.coding.IProcessTask;
import dark.api.al.coding.IProcessTask.TaskType;
import dark.assembly.common.armbot.TaskBase;
import dark.assembly.common.armbot.TaskArmbot;
import dark.core.prefab.helpers.ItemWorldHelper;

/** Used by arms to break a specific block in a position.
 *
 * @author Calclavia */
public class CommandBreak extends TaskArmbot
{
    protected int breakTicks = 30;
    protected boolean keep = false;

    public CommandBreak()
    {
        super("break", TaskType.DEFINEDPROCESS);
        this.breakTicks = 30;
    }

    public CommandBreak(String name)
    {
        super(name, TaskType.DEFINEDPROCESS);
    }



    @Override
    public ProcessReturn onUpdate()
    {
        super.onUpdate();

        Vector3 serachPosition = this.armbot.getHandPos();

        Block block = Block.blocksList[serachPosition.getBlockID(this.worldObj)];
        this.breakTicks--;
        if (block != null && breakTicks <= 0)
        {
            ArrayList<ItemStack> items = block.getBlockDropped(this.worldObj, serachPosition.intX(), serachPosition.intY(), serachPosition.intZ(), serachPosition.getBlockMetadata(worldObj), 0);

            if (!this.keep || items.size() > 1)
            {
                ItemWorldHelper.dropBlockAsItem(this.worldObj, serachPosition);
            }
            else
            {
                this.armbot.grab(new EntityItem(this.worldObj, serachPosition.intX() + 0.5D, serachPosition.intY() + 0.5D, serachPosition.intZ() + 0.5D, items.get(0)));
            }

            worldObj.setBlock(serachPosition.intX(), serachPosition.intY(), serachPosition.intZ(), 0, 0, 3);
            return ProcessReturn.DONE;
        }

        /** Notes on break command Beds Break Wrong Multi blocks don't work */
        return ProcessReturn.CONTINUE;
    }

    @Override
    public TaskBase clone()
    {
        return new CommandBreak();
    }

    @Override
    public IProcessTask loadProgress(NBTTagCompound nbt)
    {
        this.breakTicks = nbt.getInteger("breakTicks");
        return this;
    }

    @Override
    public NBTTagCompound saveProgress(NBTTagCompound nbt)
    {
        nbt.setInteger("breakTicks", this.breakTicks);
        return nbt;
    }
}
