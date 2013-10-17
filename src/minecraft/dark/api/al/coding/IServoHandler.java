package dark.api.al.coding;

import java.util.HashMap;

/** Container like class to handle several servos in an object.
 *
 * @author DarkGuardsman */
public interface IServoHandler
{
    /** Gets a map of the handler's server with a string to ID them by. Mainly will only be used by
     * advanced encoders to change the handlers servos in code */
    public HashMap<String, IServo> getServos();

    /** Ask the handler to rotation the servo.
     *
     * @return true if the handler will rotate the servo */
    public boolean updateRotation(String servo, float rotation);

    /** Forces the rotation angle of a servo. */
    public void setRotation(String servo, float rotation);
}
