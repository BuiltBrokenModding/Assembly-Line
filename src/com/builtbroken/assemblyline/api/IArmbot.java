package com.builtbroken.assemblyline.api;

import net.minecraftforge.common.ForgeDirection;
import universalelectricity.core.vector.Vector2;

import com.builtbroken.assemblyline.api.coding.IProgrammableMachine;

/** Should be used to interact with the armbot and not to create a new armbot */
public interface IArmbot extends Cloneable, IProgrammableMachine
{
    /** Location of the hand, or working location of the object */
    public universalelectricity.core.vector.Vector3 getHandPos();

    /** Gets the rotation as a Vector2 (X - Yaw, Y - pitch) */
    public Vector2 getRotation();

    /** Forces the rotation to the two angles */
    public void setRotation(float yaw, float pitch);

    /** Ask the armbot to rotate to face the given direction. Some bots may not support all angles
     * 
     * @return true if the bot will comply. May return false if it can't */
    public boolean moveArmTo(float yaw, float pitch);

    /** Ask the armbot to rotate to face the given direction. Some bots may not support up and down
     * 
     * @param direction - direction
     * @return true if the bot will comply. May return false if it can't */
    public boolean moveTo(ForgeDirection direction);

    /** Adds an entity to the Armbot's grab list. Entity or ItemStack
     * 
     * @entity - object to grab, can be anything though is suggest to be an entity or itemstack
     * @return - true if the bot has grabbed the object */
    public boolean grab(Object entity);

    /** Drops an object. Does except strings with "All" resulting in dropping everything.
     * 
     * @entity - can be anything though entity and itemstack are the main supported types
     * @return - true if the bot dropped the item */
    public boolean drop(Object object);

    /** Same as deleting the object */
    public boolean clear(Object object);

    /** Object currently held. In some cases this can be a list or array but is suggest to only be
     * one object */
    public Object getGrabbedObject();
}
