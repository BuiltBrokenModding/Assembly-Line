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

    /** Passed in from both the armbot to the program manager then here after a Computer craft
     * machine calls a this commands method name. {@IPeripheral #callMethod()} */
    public Object[] onCCMethodCalled(World world, Vector3 location, IArmbot armbot, IComputerAccess computer, ILuaContext context, Object[] arguments) throws Exception;

    /** Update the current part of the command */
    public boolean onUpdate();

    /** Called when the task is being run by the armbot
     *
     * @param world - current world
     * @param location - current location
     * @param armbot - armbot instance
     * @param arguments - arguments for command
     * @return should task be continued */
    public boolean onMethodCalled(World world, Vector3 location, IArmbot armbot, Object[] arguments);

    /** Called when the task is finish and then cleared */
    public void terminated();

    /** Read the command from the armbot save. Mainly only the current task is saved. */
    public IArmbotTask readFromNBT(NBTTagCompound nbt);

    /** Writes the command to the armbot save. Mainly only the current task is saved. */
    public NBTTagCompound writeToNBT(NBTTagCompound nbt);
}
