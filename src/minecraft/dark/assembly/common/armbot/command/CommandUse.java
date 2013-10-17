package dark.assembly.common.armbot.command;

import com.builtbroken.common.science.units.UnitHelper;

import universalelectricity.core.vector.Vector3;
import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import dark.api.al.coding.IArmbotUseable;
import dark.api.al.coding.IDeviceTask;
import dark.api.al.coding.ILogicDevice;
import dark.api.al.coding.IDeviceTask.ProcessReturn;
import dark.api.al.coding.IDeviceTask.TaskType;
import dark.api.al.coding.args.ArgumentData;
import dark.api.al.coding.args.ArgumentIntData;
import dark.assembly.common.armbot.TaskBase;
import dark.assembly.common.armbot.TaskArmbot;

public class CommandUse extends TaskArmbot
{

    protected int times, curTimes;

    public CommandUse()
    {
        super("use", TaskType.DEFINEDPROCESS);
        this.defautlArguments.add(new ArgumentIntData("repeat", 1, Integer.MAX_VALUE, 1));
    }

    @Override
    public ProcessReturn onMethodCalled(World world, Vector3 location, ILogicDevice armbot)
    {
        super.onMethodCalled(world, location, armbot);
        this.curTimes = 0;
        this.times = UnitHelper.tryToParseInt(this.getArg("repeat"));
        return ProcessReturn.CONTINUE;
    }

    @Override
    public ProcessReturn onUpdate()
    {
        Block block = Block.blocksList[this.armbot.getHandPos().getBlockID(this.worldObj)];
        TileEntity targetTile = this.armbot.getHandPos().getTileEntity(this.worldObj);

        if (targetTile != null)
        {
            if (targetTile instanceof IArmbotUseable)
            {
                ((IArmbotUseable) targetTile).onUse(this.armbot, this.getArgs());
            }

        }
        else if (block != null)
        {
            try
            {
                boolean f = block.onBlockActivated(this.worldObj, this.armbot.getHandPos().intX(), this.armbot.getHandPos().intY(), this.armbot.getHandPos().intZ(), null, 0, 0, 0, 0);
            }
            catch (Exception e)
            {

                e.printStackTrace();
            }

        }

        this.curTimes++;

        if (this.curTimes >= this.times)
        {
            return ProcessReturn.DONE;
        }

        return ProcessReturn.CONTINUE;
    }

    @Override
    public String toString()
    {
        return "USE " + Integer.toString(this.times);
    }

    @Override
    public CommandUse load(NBTTagCompound taskCompound)
    {
        super.loadProgress(taskCompound);
        this.times = taskCompound.getInteger("useTimes");

        return this;
    }

    @Override
    public NBTTagCompound save(NBTTagCompound taskCompound)
    {
        super.saveProgress(taskCompound);
        taskCompound.setInteger("useTimes", this.times);

        return taskCompound;
    }

    @Override
    public IDeviceTask loadProgress(NBTTagCompound nbt)
    {
        this.curTimes = nbt.getInteger("useCurTimes");
        return this;
    }

    @Override
    public NBTTagCompound saveProgress(NBTTagCompound nbt)
    {
        nbt.setInteger("useCurTimes", this.curTimes);
        return nbt;
    }

    @Override
    public TaskBase clone()
    {
        return new CommandUse();
    }
}
