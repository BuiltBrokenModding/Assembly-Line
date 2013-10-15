package dark.assembly.common.armbot;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import universalelectricity.core.vector.Vector2;
import universalelectricity.core.vector.Vector3;
import dan200.computer.api.IComputerAccess;
import dan200.computer.api.ILuaContext;
import dark.api.al.armbot.IArmbot;
import dark.api.al.armbot.IArmbotTask;

/** An AI Commands that is used by TileEntities with AI.
 *
 * @author Calclavia */
public abstract class Command implements IArmbotTask, Cloneable
{
    private String methodName;
    /** The amount of ticks this command has been running for. */
    protected int ticks = 0;

    protected World world;
    protected IArmbot armbot;
    protected Vector2 pos;

    /** The parameters this command has, or the properties. Entered by the player in the disk.
     * Parameters are entered like a Java function. idle(20) = Idles for 20 seconds. */
    private String[] parameters;

    public Command(String name)
    {
        this.methodName = name;
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
    public boolean onUpdate()
    {
        this.ticks++;
        return false;
    }

    @Override
    public boolean onMethodCalled(World world, Vector3 location, IArmbot armbot, Object[] arguments)
    {
        this.world = world;

        return false;
    }

    @Override
    public Object[] onCCMethodCalled(IComputerAccess computer, ILuaContext context, Object[] arguments) throws Exception
    {
        // TODO Auto-generated method stub
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

    public String[] getArgs()
    {
        return this.parameters;
    }

    /** Some functions to help get parameter arguments. */
    protected String getArg(int i)
    {
        if (i >= 0 && i < this.parameters.length)
        {
            return this.parameters[i];
        }

        return null;
    }

    @Override
    public Command readFromNBT(NBTTagCompound nbt)
    {
        this.ticks = nbt.getInteger("ticks");
        this.pos = new Vector2(nbt.getDouble("xx"),nbt.getDouble("yy"));
        return this;
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt)
    {
        nbt.setInteger("ticks", this.ticks);
        if (this.pos != null)
        {
            nbt.setDouble("xx", pos.x);
            nbt.setDouble("yy", pos.y);
        }
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
    public abstract Command clone();
}
