package dark.api;

import universalelectricity.core.vector.Vector3;

/** Applied to objects that can be aimed by yaw and pitch. This is used by things like sentry guns,
 * vehicles, or mining tools.
 * 
 * @author DarkGuardsman */
public interface IAimable
{
    /** Vector which runs from the objects eyes(or gun) to what it is looking at. Should be right
     * outside the objects bounds but no farther than that. Used for ray traces mainly */
    public Vector3 getLook();

    /** X pitch, Y is yaw, z is roll but is not used for basic aiming */
    public Vector3 getRotation();

    /** This does not set the rotation but rather moves the current rotation by the given values */
    public void updateRotation(float pitch, float yaw, float roll);

    /** Forces the rotation to the angles */
    public void setRotation(float pitch, float yaw, float roll);
}
