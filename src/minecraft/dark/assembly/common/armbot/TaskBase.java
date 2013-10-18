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
import dark.api.al.coding.IProcessTask;
import dark.api.al.coding.IMemoryTask;
import dark.api.al.coding.IProgram;
import dark.api.al.coding.IProgramableMachine;
import dark.api.al.coding.args.ArgumentData;
import dark.core.prefab.helpers.NBTFileHelper;

/** Basic command prefab used by machines like an armbot. You are not required to use this in order
 * to make armbot commands but it does help. Delete this if you don't plan to use it. */
public abstract class TaskBase implements IProcessTask, IMemoryTask
{
    /** Program this is part of. Can be null while stores as a prefab waiting to be copied */
    protected IProgram program;
    private String methodName;
    /** The amount of ticks this command has been running for. */
    protected long ticks;

    protected TaskType taskType;

    /** World current working in */
    protected World worldObj;

    /** Armbot location */
    protected Vector3 devicePos;
    /** Position in the coder is also used during loading to place everything together */
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
    public ProcessReturn onUpdate()
    {
        if (ticks++ >= Long.MAX_VALUE - 1)
        {
            this.ticks = 0;
        }
        return ProcessReturn.CONTINUE;
    }

    @Override
    public ProcessReturn onMethodCalled(World world, Vector3 location, IProgramableMachine armbot)
    {
        if (location != null && devicePos != null)
        {
            this.worldObj = world;
            this.devicePos = location;
            return ProcessReturn.CONTINUE;
        }

        return ProcessReturn.GENERAL_ERROR;
    }

    @Override
    public Object[] onCCMethodCalled(World world, Vector3 location, IProgramableMachine armbot, IComputerAccess computer, ILuaContext context) throws Exception
    {
        this.worldObj = world;
        this.devicePos = location;

        return null;
    }

    @Override
    public void terminated()
    {
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

    @Override
    public TaskBase load(NBTTagCompound nbt)
    {
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
        this.pos = new Vector2(nbt.getDouble("xx"), nbt.getDouble("yy"));
        return this;
    }

    @Override
    public NBTTagCompound save(NBTTagCompound nbt)
    {
        NBTTagCompound parms = new NBTTagCompound();
        for (Entry<String, Object> entry : this.aruguments.entrySet())
        {
            NBTFileHelper.saveObject(parms, entry.getKey(), entry.getValue());
        }
        nbt.setCompoundTag("args", parms);
        if (this.pos != null)
        {
            nbt.setDouble("xx", pos.x);
            nbt.setDouble("yy", pos.y);
        }
        return nbt;
    }

    @Override
    public IProcessTask loadProgress(NBTTagCompound nbt)
    {
        return this;
    }

    @Override
    public NBTTagCompound saveProgress(NBTTagCompound nbt)
    {
        return nbt;
    }

    @Override
    public String toString()
    {
        return "COMMAND[" + super.toString() + "]:" + this.methodName;
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
    public abstract TaskBase clone();

    @Override
    public int getMemoryVars()
    {
        return 0;
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
    public Object getArg(String name)
    {
        return this.aruguments.get(name);
    }

    @Override
    public HashMap<String, Object> getArgs()
    {
        return this.aruguments;
    }

    @Override
    public HashMap<String, Object> getSavedData()
    {
        return null;
    }

    @Override
    public List<ArgumentData> getEncoderParms()
    {
        return this.defautlArguments;
    }
}
