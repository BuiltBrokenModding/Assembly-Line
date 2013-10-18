package dark.assembly.common.armbot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import universalelectricity.core.vector.Vector2;
import universalelectricity.core.vector.Vector3;

import com.builtbroken.common.science.units.UnitHelper;

import dan200.computer.api.IComputerAccess;
import dan200.computer.api.ILuaContext;
import dark.api.al.coding.IArmbot;
import dark.api.al.coding.IProcessTask;
import dark.api.al.coding.IMemorySlot;
import dark.api.al.coding.IProgram;
import dark.api.al.coding.IProgrammableMachine;
import dark.api.al.coding.IProcessTask.ProcessReturn;
import dark.api.al.coding.args.ArgumentData;
import dark.core.prefab.helpers.NBTFileHelper;

/** Basic command prefab used by machines like an armbot. You are not required to use this in order
 * to make armbot commands but it does help. Delete this if you don't plan to use it. */
public abstract class TaskBaseProcess extends TaskBase implements IProcessTask
{

    public TaskBaseProcess(String name)
    {
        super(name, TaskType.PROCESS);
        this.ticks = 0;
    }

    @Override
    public ProcessReturn onMethodCalled()
    {
        if (this.program != null && this.program.getMachine() != null)
        {
            return ProcessReturn.CONTINUE;
        }

        return ProcessReturn.GENERAL_ERROR;
    }

    @Override
    public ProcessReturn onUpdate()
    {
        if (ticks++ >= Long.MAX_VALUE - 1)
        {
            this.ticks = 0;
        }
        if (this.program != null && this.program.getMachine() != null)
        {
            return ProcessReturn.CONTINUE;
        }
        return ProcessReturn.GENERAL_ERROR;
    }

    @Override
    public Object[] onCCMethodCalled(IComputerAccess computer, ILuaContext context) throws Exception
    {
        return null;
    }

    @Override
    public void terminated()
    {
    }

    public ItemStack getItem(Object object, int ammount)
    {
        int id = 0;
        int meta = 32767;

        if (object instanceof String && ((String) object).contains(":"))
        {
            String[] blockID = ((String) object).split(":");
            id = Integer.parseInt(blockID[0]);
            meta = Integer.parseInt(blockID[1]);
        }
        else
        {
            id = UnitHelper.tryToParseInt(object);
        }

        if (id == 0)
        {
            return null;
        }
        else
        {
            return new ItemStack(id, ammount, meta);
        }
    }
}
