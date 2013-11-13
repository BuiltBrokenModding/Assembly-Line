package dark.assembly.armbot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import net.minecraft.nbt.NBTTagCompound;
import universalelectricity.core.vector.Vector2;
import dark.api.al.coding.IMemorySlot;
import dark.api.al.coding.IProgram;
import dark.api.al.coding.IProgrammableMachine;
import dark.api.al.coding.ITask;
import dark.api.al.coding.args.ArgumentData;
import dark.core.save.NBTFileHelper;

/** @author DarkGuardsman */
public abstract class TaskBase implements ITask, IMemorySlot
{
    /** Program this is part of. Can be null while stores as a prefab waiting to be copied */
    protected IProgram program;
    protected String methodName;
    /** The amount of ticks this command has been running for. */
    protected long ticks;

    protected TaskType taskType;

    protected Vector2 pos;

    /** The parameters this command */
    protected HashMap<String, Object> aruguments = new HashMap<String, Object>();
    protected List<ArgumentData> defautlArguments = new ArrayList<ArgumentData>();
    protected HashMap<String, Object> activeMemory = new HashMap<String, Object>();

    public TaskBase(String name, TaskType tasktype)
    {
        this.methodName = name;
        this.taskType = tasktype;
    }

    @Override
    public void refresh()
    {

    }

    @Override
    public void reset()
    {
        this.activeMemory.clear();
    }

    @Override
    public Object getMemory(String name)
    {
        return this.activeMemory.get(name);
    }

    @Override
    public HashMap<String, Object> getMemory()
    {
        return this.activeMemory;
    }

    @Override
    public HashMap<String, Object> getSavedData()
    {
        return null;
    }

    @Override
    public void setProgram(IProgram program)
    {
        this.program = program;
    }

    @Override
    public Vector2 getPosition()
    {
        return pos;
    }

    @Override
    public void setPosition(Vector2 pos)
    {
        this.pos = pos;
    }

    @Override
    public String getMethodName()
    {
        return this.methodName;
    }

    @Override
    public TaskType getType()
    {
        return this.taskType;
    }

    @Override
    public Object getArg(String name)
    {
        if (this.aruguments.containsKey(name))
        {
            return this.aruguments.get(name);
        }
        return null;
    }

    @Override
    public void setArgs(HashMap<String, Object> args)
    {
        this.aruguments = args;
        if (this.aruguments == null)
        {
            this.aruguments = new HashMap();
        }
    }

    @Override
    public HashMap<String, Object> getArgs()
    {
        return this.aruguments;
    }

    @Override
    public List<ArgumentData> getEncoderParms()
    {
        return this.defautlArguments;
    }

    @Override
    public abstract TaskBase clone();

    @Override
    public TaskBase load(NBTTagCompound nbt)
    {
        this.pos = new Vector2(nbt.getDouble("xx"), nbt.getDouble("yy"));
        if (this.getEncoderParms() != null)
        {
            this.aruguments = new HashMap();
            NBTTagCompound parms = nbt.getCompoundTag("args");
            for (ArgumentData arg : this.getEncoderParms())
            {
                Object obj = NBTFileHelper.loadObject(parms, arg.getName());
                if (arg.isValid(obj))
                {
                    this.aruguments.put(arg.getName(), obj);
                }
            }
        }
        return this;
    }

    @Override
    public NBTTagCompound save(NBTTagCompound nbt)
    {
        if (this.pos != null)
        {
            nbt.setDouble("xx", pos.x);
            nbt.setDouble("yy", pos.y);
        }
        NBTTagCompound parms = new NBTTagCompound();
        for (Entry<String, Object> entry : this.aruguments.entrySet())
        {
            NBTFileHelper.saveObject(parms, entry.getKey(), entry.getValue());
        }
        nbt.setCompoundTag("args", parms);
        return nbt;
    }

    @Override
    public ITask loadProgress(NBTTagCompound nbt)
    {
        return this;
    }

    @Override
    public NBTTagCompound saveProgress(NBTTagCompound nbt)
    {
        return nbt;
    }

    @Override
    public boolean canUseTask(IProgrammableMachine device)
    {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public String toString()
    {
        return "COMMAND[" + super.toString() + "]:" + this.methodName;
    }

}
