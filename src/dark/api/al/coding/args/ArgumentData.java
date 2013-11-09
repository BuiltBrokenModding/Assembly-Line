package dark.api.al.coding.args;

/** Used to store arguments in a way that can be easier to read, limit, and understand
 * 
 * @author DarkGuardsman */
public class ArgumentData
{
    protected String name;
    protected Object storedValue;

    public ArgumentData(String name, Object object)
    {
        this.name = name;
        this.storedValue = object;
    }

    /** Sets the value
     * 
     * @return true if the value was accepted */
    public boolean setData(Object object)
    {
        if (this.isValid(object))
        {
            this.storedValue = object;
            return true;
        }
        return false;
    }

    /** Gets the value of the stored data */
    public Object getData()
    {
        return this.storedValue;
    }

    public String getName()
    {
        return this.name;
    }

    public boolean isValid(Object object)
    {
        return object != null;
    }

    /** Is this argument valid. */
    public boolean isValid()
    {
        //Null is invalide since the object is used to understand data types. Without data the encoder can't use the value and will remove it
        return storedValue != null;
    }
}
