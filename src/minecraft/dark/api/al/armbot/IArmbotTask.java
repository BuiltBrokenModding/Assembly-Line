package dark.api.al.armbot;

import universalelectricity.core.vector.Vector2;
import universalelectricity.core.vector.Vector3;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import dan200.computer.api.IComputerAccess;
import dan200.computer.api.ILuaContext;

/** Basic armbot command use to tell the armbot how to function
 *
 * @author DarkGuardsman */
public interface IArmbotTask
{
    /** Gets the position inside the coding display. Set by the user but is general grid aligned by
     * int values */
    public Vector2 getPosition();

    public void setPosition(Vector2 pos);

    /** Method name or rather command name this will be called. Use both to ID this command, and do
     * basic command structuring. */
    public String getMethodName();

    /** Should be the same as getMethodName() but can be different */
    public String getCCMethod();

    public Object[] getCurrentParms();

    public void setParms(Object... arguments);

    /** Passed in from both the armbot to the program manager then here after a Computer craft
     * machine calls a this commands method name. {@IPeripheral #callMethod()} */
    public Object[] onCCMethodCalled(World world, Vector3 location, IArmbot armbot, IComputerAccess computer, ILuaContext context) throws Exception;

    /** Update the current segment of the task */
    public boolean onUpdate();

    /** Called when the task is being run by the armbot. Used mainly to setup the task before
     * actually doing the task.
     *
     * @param world - current world
     * @param location - current location
     * @param armbot - armbot instance
     * @param arguments - arguments for command
     * @return should task be continued */
    public boolean onMethodCalled(World world, Vector3 location, IArmbot armbot);

    /** Called when the task is finish and then cleared */
    public void terminated();

    /** Read the command from the armbot save. */
    public IArmbotTask load(NBTTagCompound nbt);

    /** Writes the command to the armbot save. Should only be used to save the data used to recreate
     * a new version of this command */
    public NBTTagCompound save(NBTTagCompound nbt);

    /** Saves the current progress of the current command */
    public IArmbotTask loadProgress(NBTTagCompound nbt);

    /** Reads the progress of the command if it was saved mid process */
    public NBTTagCompound saveProgress(NBTTagCompound nbt);

    public TaskType getType();

    /** Used mainly for display purposes in the encoder */
    public static enum TaskType
    {
        DATA(),
        DEFINEDPROCESS(),
        DECISION()
    }
}
