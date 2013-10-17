package dark.api.al.coding;

import java.util.HashMap;

import net.minecraft.nbt.NBTTagCompound;
import universalelectricity.core.vector.Vector2;

/** Flow chart style program. Each command in the program needs to have a stored location so it can
 * be saved and loaded with its correct connections. Though the location only need to be a simple
 * Column and row based system.
 *
 * @author DarkGuardsman */
public interface IProgram
{
    /** Called when the program is added to an encoder, machine, or devices. */
    public void init();

    /** Variables this program has to operate. Is still limited by the actual machine. String is the
     * name, Object is the starting value and data type */
    public HashMap<String, Object> getDeclairedVarables();

    /** Next task in the set. Its up to the program to increment down the list */
    public IDeviceTask getNextTask();

    /** Gets a task at the given x y location in the program */
    public IDeviceTask getTaskAt(Vector2 vector2);

    public void setTaskAt(Vector2 vector2, IDeviceTask task);

    /** Return this program to its starting conditions
     *
     * @full - means full reset including memory clean */
    public void reset(boolean full);

    /** Sets the declared variable */
    public void setVar(String name, Object object);

    /** Gets a declared variable */
    public Object getVar(String name);

    public NBTTagCompound save(NBTTagCompound nbt);

    public void load(NBTTagCompound nbt);
}
