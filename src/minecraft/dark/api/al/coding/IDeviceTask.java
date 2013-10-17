package dark.api.al.coding;

import java.util.HashMap;
import java.util.List;

import universalelectricity.core.vector.Vector2;
import universalelectricity.core.vector.Vector3;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import dan200.computer.api.IComputerAccess;
import dan200.computer.api.ILuaContext;
import dark.api.al.coding.args.ArgumentData;

/** Use to construct a basic task that can be used in any device that supports this interface.
 *
 * @Note - there are several methods that look a like. GetArgs is used to get the programs arguments
 * that were set by the encoder. GetEncoderParms should be a constant set of arguments that the
 * device can support. GetMemory is a list of variables that the program needs to store outside of
 * the task. That way it can save values after the task has been refreshed or even deleted.
 *
 * @author DarkGuardsman */
public interface IDeviceTask
{
    /** Location in the column and row format. */
    public Vector2 getPosition();

    public void setPosition(Vector2 pos);

    /** Method name or rather command name this will be called. Uses both to ID this command, and do
     * basic command structuring. */
    public String getMethodName();

    /** Should be the same as getMethodName() but can be different */
    public String getCCMethod();

    /** Passed in from the device to the program manager then here after a Computer craft machine
     * calls a this commands method name. {@IPeripheral #callMethod()} */
    public Object[] onCCMethodCalled(World world, Vector3 location, ILogicDevice device, IComputerAccess computer, ILuaContext context) throws Exception;

    /** Called when the task is being run by the devices program manager. Used mainly to setup the
     * task before actually doing the task.
     *
     * @param world - current world
     * @param location - current location
     * @param armbot - armbot instance
     * @param arguments - arguments for command
     * @return false to stop the task here. */
    public ProcessReturn onMethodCalled(World world, Vector3 location, ILogicDevice device);

    /** Update the current segment of the task */
    public ProcessReturn onUpdate();

    /** Called when the task is finish and then cleared */
    public void terminated();

    /** Read the command from the armbot save. */
    public IDeviceTask load(NBTTagCompound nbt);

    /** Writes the command to the armbot save. Should only be used to save the data used to recreate
     * a new version of this command */
    public NBTTagCompound save(NBTTagCompound nbt);

    /** Saves the current progress of the current command */
    public IDeviceTask loadProgress(NBTTagCompound nbt);

    /** Reads the progress of the command if it was saved mid process */
    public NBTTagCompound saveProgress(NBTTagCompound nbt);

    public TaskType getType();

    /** Can this task function for this machine */
    public boolean canUseTask(ILogicDevice device);

    /** ArgumentData used to both restrict and set values into the argument hashmap */
    public List<ArgumentData> getEncoderParms();

    /** Get an argument by a given name */
    public Object getArg(String name);

    /** Get all given arguments */
    public HashMap<String, Object> getArgs();

    /** Used mainly for display purposes in the encoder */
    public static enum TaskType
    {
        DATA("Data"),
        DEFINEDPROCESS("Defined Process"),
        PROCESS("Process"),
        DECISION("Decision");
        public ResourceLocation blockTexure;
        public String name;

        private TaskType(String name)
        {
            this.name = name;
        }
    }

    public static enum ProcessReturn
    {
        CONTINUE("Continue", "Running"),
        DONE("Done", "Done"),
        GENERAL_ERROR("General Error", "Error program failure"),
        SYNTAX_ERROR("Syntax Error", "Error incorrect syntax"),
        ARGUMENT_ERROR("Arument error", "Error incorrect arguments");
        public String name, userOutput;

        private ProcessReturn(String name, String userOutput)
        {
            this.name = name;
            this.userOutput = userOutput;
        }
    }
}
