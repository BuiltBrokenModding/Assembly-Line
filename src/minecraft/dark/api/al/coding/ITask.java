package dark.api.al.coding;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import universalelectricity.core.vector.Vector2;

public interface ITask extends Cloneable
{
    /** Location in the column and row format. */
    public Vector2 getPosition();

    public void setPosition(Vector2 pos);

    /** Method name or rather command name this will be called. Uses both to ID this command, and do
     * basic command structuring. */
    public String getMethodName();

    /** Type of task used mainly for GUI displays */
    public TaskType getType();

    /** Read the command from the armbot save. */
    public ITask load(NBTTagCompound nbt);

    /** Writes the command to the armbot save. Should only be used to save the data used to recreate
     * a new version of this command */
    public NBTTagCompound save(NBTTagCompound nbt);

    /** Saves the current progress of the current command */
    public IProcessTask loadProgress(NBTTagCompound nbt);

    /** Reads the progress of the command if it was saved mid process */
    public NBTTagCompound saveProgress(NBTTagCompound nbt);

    /** Can this task function for this machine */
    public boolean canUseTask(IProgramableMachine device);

    /** Used to create a new task from this task. Make sure to return a fresh copy without anything.
     * This includes no arguments, progress, varables, etc. */
    public ITask clone();

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
}
