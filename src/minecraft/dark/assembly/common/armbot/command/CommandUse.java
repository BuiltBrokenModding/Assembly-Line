package dark.assembly.common.armbot.command;

import universalelectricity.core.vector.Vector3;
import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import dark.api.al.armbot.IArmbotUseable;
import dark.api.al.armbot.ILogicDevice;
import dark.api.al.armbot.IDeviceTask.ProcessReturn;
import dark.api.al.armbot.IDeviceTask.TaskType;
import dark.assembly.common.armbot.TaskBase;
import dark.assembly.common.armbot.TaskArmbot;

public class CommandUse extends TaskArmbot
{

    private int times;
    private int curTimes;

    public CommandUse()
    {
        super("use", TaskType.DEFINEDPROCESS);
    }

    @Override
    public ProcessReturn onMethodCalled(World world, Vector3 location, ILogicDevice armbot)
    {
        this.times = 0;
        this.curTimes = 0;

        if (this.getArgs().length > 0)
        {
            this.times = this.getIntArg(0);
        }

        if (this.times <= 0)
            this.times = 1;
    }

    @Override
    public boolean onUpdate()
    {
        Block block = Block.blocksList[this.worldObj.getBlockId(tileEntity.getHandPosition().intX(), tileEntity.getHandPosition().intY(), tileEntity.getHandPosition().intZ())];
        TileEntity targetTile = this.tileEntity.getHandPosition().getTileEntity(this.worldObj);

        if (targetTile != null)
        {
            if (targetTile instanceof IArmbotUseable)
            {
                ((IArmbotUseable) targetTile).onUse(this.tileEntity, this.getArgs());
            }

        }
        else if (block != null)
        {
            try
            {
                boolean f = block.onBlockActivated(this.worldObj, tileEntity.getHandPosition().intX(), tileEntity.getHandPosition().intY(), tileEntity.getHandPosition().intZ(), null, 0, 0, 0, 0);
            }
            catch (Exception e)
            {

                e.printStackTrace();
            }

        }

        this.curTimes++;

        if (this.curTimes >= this.times)
        {
            return false;
        }

        return true;
    }

    @Override
    public String toString()
    {
        return "USE " + Integer.toString(this.times);
    }

    @Override
    public void loadProgress(NBTTagCompound taskCompound)
    {
        super.loadProgress(taskCompound);
        this.times = taskCompound.getInteger("useTimes");
        this.curTimes = taskCompound.getInteger("useCurTimes");
    }

    @Override
    public void saveProgress(NBTTagCompound taskCompound)
    {
        super.saveProgress(taskCompound);
        taskCompound.setInteger("useTimes", this.times);
        taskCompound.setInteger("useCurTimes", this.curTimes);
    }
}
