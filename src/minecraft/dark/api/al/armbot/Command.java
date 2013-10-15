package dark.api.al.armbot;

import com.builtbroken.common.science.units.UnitHelper;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import universalelectricity.core.vector.Vector2;
import universalelectricity.core.vector.Vector3;
import dan200.computer.api.IComputerAccess;
import dan200.computer.api.ILuaContext;
import dark.api.al.armbot.IArmbotTask.TaskType;

/** Basic command prefab used by machines like an armbot. You are not required to use this in order
 * to make armbot commands but it does help. Delete this if you don't plan to use it. */
public abstract class Command implements IArmbotTask, Cloneable
{
    /** Program this is part of. Can be null while stores as a prefab waiting to be copied */
    protected IProgram program;
    private String methodName;
    /** The amount of ticks this command has been running for. */
    protected int ticks = 0;

    protected TaskType taskType;

    /** World current working in */
    protected World worldObj;
    /** Armbot instance */
    protected IArmbot armbot;
    /** Armbot location */
    protected Vector3 armbotPos;
    /** Position in the coder is also used during loading to place everything together */
    protected Vector2 pos;

    /** The parameters this command */
    private Object[] parameters;

    public Command(String name, TaskType tasktype)
    {
        this.methodName = name;
        this.taskType = tasktype;
    }

    @Override
    public boolean onUpdate()
    {
        this.ticks++;
        return false;
    }

    @Override
    public boolean onMethodCalled(World world, Vector3 location, IArmbot armbot)
    {
        this.worldObj = world;
        this.armbot = armbot;
        this.armbotPos = location;

        return true;
    }

    @Override
    public Object[] onCCMethodCalled(World world, Vector3 location, IArmbot armbot, IComputerAccess computer, ILuaContext context) throws Exception
    {
        this.worldObj = world;
        this.armbot = armbot;
        this.armbotPos = location;

        return null;
    }

    @Override
    public void terminated()
    {
    }

    public void setParameters(String[] strings)
    {
        this.parameters = strings;
    }

    public Object[] getArgs()
    {
        return this.parameters;
    }

    /** Some functions to help get parameter arguments. */
    protected Object getArg(int i)
    {
        if (i >= 0 && i < this.parameters.length)
        {
            return this.parameters[i];
        }

        return null;
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

    public ItemStack getItem(String string, int ammount)
    {
        int id = 0;
        int meta = 32767;
        if (string.contains(":"))
        {
            String[] blockID = string.split(":");
            id = Integer.parseInt(blockID[0]);
            meta = Integer.parseInt(blockID[1]);
        }
        else
        {
            id = UnitHelper.tryToParseInt(string);
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
    public Command load(NBTTagCompound nbt)
    {
        NBTTagCompound parmTag = nbt.getCompoundTag("parms");
        int parms = parmTag.getInteger("parms");
        if (parms > 0)
        {
            Object[] args = new Object[parms];
            for (int i = 0; i < parms; i++)
            {
                args[i] = nbt.getString("parm" + i);
            }
        }
        this.pos = new Vector2(nbt.getDouble("xx"), nbt.getDouble("yy"));
        return this;
    }

    @Override
    public NBTTagCompound save(NBTTagCompound nbt)
    {
        if (this.parameters != null)
        {
            NBTTagCompound parmTag = new NBTTagCompound();
            parmTag.setInteger("parms", this.parameters.length);

            for (int i = 0; i < this.parameters.length; i++)
            {
                parmTag.setString("parm" + i, "" + this.parameters[i]);
            }
            nbt.setCompoundTag("parms", parmTag);
        }
        if (this.pos != null)
        {
            nbt.setDouble("xx", pos.x);
            nbt.setDouble("yy", pos.y);
        }
        return nbt;
    }

    @Override
    public IArmbotTask loadProgress(NBTTagCompound nbt)
    {
        this.ticks = nbt.getInteger("ticks");
        return this;
    }

    @Override
    public NBTTagCompound saveProgress(NBTTagCompound nbt)
    {
        nbt.setInteger("ticks", this.ticks);
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
    public Object[] getCurrentParms()
    {
        return this.parameters;
    }

    @Override
    public void setParms(Object... arguments)
    {
        this.parameters = arguments;
    }



    @Override
    public TaskType getType()
    {
        return this.taskType;
    }

    @Override
    public abstract Command clone();
}
