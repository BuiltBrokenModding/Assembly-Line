package dark.api.al.armbot;

import java.util.List;

import universalelectricity.core.vector.Vector2;

import com.builtbroken.common.Vector3;

import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.ForgeDirection;

/** Should be used to interact with the armbot and not to create a new armbot */
public interface IArmbot extends Cloneable
{
    /** Location of the hand, or working location of the object */
    public Vector3 getHandPos();

    /** Gets the rotation as a Vector2 (X - Yaw, Y - pitch) */
    public Vector2 getRotation();

    /** Forces the rotation to the two angles */
    public void setRotation(float yaw, float pitch);

    /** Asks the armbot to move its arm to the rotation */
    public void moveTo(float yaw, float pitch);

    /** Asks the armbot to move to the facing direction */
    public void moveTo(ForgeDirection direction);

    /** Adds an entity to the Armbot's grab list. Entity or ItemStack */
    public void grab(Object entity);

    /**Drops an object. Does except strings with "All" resulting in dropping everything */
    public void drop(Object object);

    /** List of object held by the armbot */
    public List<Object> getGrabbedObjects();
}
