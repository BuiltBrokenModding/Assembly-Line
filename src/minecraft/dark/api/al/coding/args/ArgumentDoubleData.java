package dark.api.al.coding.args;

/** Used to create argument data for the encoder. Should only be used if the value needs to be
 * clearly limited inside the encoder display.
 *
 * @author DarkGuardsman */
public class ArgumentDoubleData extends ArgumentData
{
    protected double max, min;

    public ArgumentDoubleData(String name, double value, double max, double min)
    {
        super(name, value);
        this.max = max;
        this.min = min;
    }

    @Override
    public boolean isValid(Object object)
    {
        return super.isValid() && object instanceof Double && ((double) object) >= min && ((double) object) <= max;
    }
}
