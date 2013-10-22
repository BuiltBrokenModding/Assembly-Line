package dark.api.al.coding.args;

/** Used to create argument data for the encoder. Should only be used if the value needs to be
 * clearly limited inside the encoder display.
 * 
 * @author DarkGuardsman */
public class ArgumentIntData extends ArgumentData
{
    protected int max, min;

    public ArgumentIntData(String name, int value, int max, int min)
    {
        super(name, value);
        this.max = max;
        this.min = min;
    }

    @Override
    public boolean isValid(Object object)
    {
        return super.isValid() && object instanceof Integer && ((int) object) >= min && ((int) object) <= max;
    }
}
