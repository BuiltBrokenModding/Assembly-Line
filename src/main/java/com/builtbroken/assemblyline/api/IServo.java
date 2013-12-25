package com.builtbroken.assemblyline.api;

import universalelectricity.api.vector.Vector2;

/** Class used in the creation of servo based object
 * 
 * @author Rseifert */
public interface IServo
{
    /** Gets the rotation as a Vector2 (X - Yaw, Y - pitch) */
    public Vector2 getRotation();

    /** Forces the rotation to the two angles */
    public void setRotation(float yaw, float pitch);
}
