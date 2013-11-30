package dark.assembly.armbot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import universalelectricity.core.vector.Vector2;
import dark.api.al.coding.IMemorySlot;
import dark.api.al.coding.IProgram;
import dark.api.al.coding.IProgrammableMachine;
import dark.api.al.coding.ITask;
import dark.api.al.coding.args.ArgumentData;
import dark.api.save.NBTFileHelper;

/** @author DarkGuardsman */
public abstract class TaskBase implements ITask, IMemorySlot
{
    /** Program this is part of. Can be null while stores as a prefab waiting to be copied */
    protected IProgram program;
    protected String methodName;
    /** The amount of ticks this command has been running for. */
    protected long ticks;

    protected TaskType taskType;

    protected int col;
    protected int row;

    protected Vector2 UV;

    /** The parameters this command */
    protected HashMap<String, Object> aruguments = new HashMap<String, Object>();
    protected List<ArgumentData> defautlArguments = new ArrayList<ArgumentData>();
    protected HashMap<String, Object> activeMemory = new HashMap<String, Object>();

    public TaskBase(String name, TaskType tasktype)
    {
        this.methodName = name;
        this.taskType = tasktype;
        this.UV = this.taskType.UV;
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
    public int getCol()
    {
        return this.col;
    }

    @Override
    public int getRow()
    {
        return this.row;
    }

    @Override
    public void setPosition(int col, int row)
    {
        this.col = col;
        this.row = row;
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
        if (this.getArgs().containsKey(name))
        {
            return this.getArgs().get(name);
        }
        return null;
    }

    public void setArg(String argName, Object obj)
    {
        if (this.getArgs() != null && this.getArgs().containsKey(argName))
        {
            this.getArgs().put(argName, obj);
        }
    }

    @Override
    public void setArgs(HashMap<String, Object> args)
    {
        if (this.aruguments == null)
        {
            this.aruguments = new HashMap();
        }
        this.aruguments = args;
    }

    @Override
    public HashMap<String, Object> getArgs()
    {
        if (this.aruguments == null)
        {
            this.aruguments = new HashMap();
            if (this.defautlArguments != null && !this.defautlArguments.isEmpty())
            {
                for (ArgumentData obj : this.defautlArguments)
                {
                    this.aruguments.put(obj.getName(), obj.getData());
                }
            }
        }
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
        this.col = nbt.getInteger("col");
        this.row = nbt.getInteger("row");
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

        nbt.setInteger("col", this.col);
        nbt.setInteger("row", this.row);
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

    @Override
    public ResourceLocation getTextureSheet()
    {
        return ITask.TaskType.TEXTURE;
    }

    @Override
    public Vector2 getTextureUV()
    {
        return this.UV;
    }

}
