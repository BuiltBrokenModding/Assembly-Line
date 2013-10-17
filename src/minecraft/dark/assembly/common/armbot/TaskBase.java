package dark.assembly.common.armbot;

import java.util.HashMap;
import java.util.Map.Entry;

import com.builtbroken.common.science.units.UnitHelper;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;
import universalelectricity.core.vector.Vector2;
import universalelectricity.core.vector.Vector3;
import dan200.computer.api.IComputerAccess;
import dan200.computer.api.ILuaContext;
import dark.api.al.armbot.IArmbot;
import dark.api.al.armbot.ILogicDevice;
import dark.api.al.armbot.IDeviceTask;
import dark.api.al.armbot.IMemoryTask;
import dark.api.al.armbot.IProgram;
import dark.api.al.armbot.IDeviceTask.TaskType;
import dark.core.prefab.helpers.NBTFileLoader;

/** Basic command prefab used by machines like an armbot. You are not required to use this in order
 * to make armbot commands but it does help. Delete this if you don't plan to use it. */
public abstract class TaskBase implements IDeviceTask, Cloneable, IMemoryTask
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
    protected Vector3 armbotPos;
    /** Position in the coder is also used during loading to place everything together */
    protected Vector2 pos;

    /** The parameters this command */
    private HashMap<String, Object> parameters;

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
    public ProcessReturn onMethodCalled(World world, Vector3 location, ILogicDevice armbot)
    {
        if (location != null && armbotPos != null)
        {
            this.worldObj = world;
            this.armbotPos = location;
            return ProcessReturn.CONTINUE;
        }

        return ProcessReturn.GENERAL_ERROR;
    }

    @Override
    public Object[] onCCMethodCalled(World world, Vector3 location, ILogicDevice armbot, IComputerAccess computer, ILuaContext context) throws Exception
    {
        this.worldObj = world;
        this.armbotPos = location;

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
            this.parameters = new HashMap();
            NBTTagCompound parms = nbt.getCompoundTag("args");
            for (Entry<String, Object> entry : this.getEncoderParms().entrySet())
            {
                this.parameters.put(entry.getKey(), NBTFileLoader.loadObject(parms, entry.getKey()));
            }
        }
        this.pos = new Vector2(nbt.getDouble("xx"), nbt.getDouble("yy"));
        return this;
    }

    @Override
    public NBTTagCompound save(NBTTagCompound nbt)
    {
        NBTTagCompound parms = new NBTTagCompound();
        for (Entry<String, Object> entry : this.parameters.entrySet())
        {
            NBTFileLoader.saveObject(parms, entry.getKey(), entry.getValue());
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
    public IDeviceTask loadProgress(NBTTagCompound nbt)
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
    public String getCCMethod()
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
    public HashMap<String, Object> getSavedData()
    {
        return null;
    }

    @Override
    public HashMap<String, Object> getEncoderParms()
    {
        // TODO Auto-generated method stub
        return null;
    }
}
