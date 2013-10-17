package dark.api.al.coding.args;

/** Used to create argument data for the encoder. Should only be used if the value needs to be
 * clearly limited inside the encoder display.
 *
 * @author DarkGuardsman */
public class ArgumentFloatData extends ArgumentData
{
    protected float max, min;

    public ArgumentFloatData(String name, float value, float max, float min)
    {
        super(name, value);
        this.max = max;
        this.min = min;
    }

    @Override
    public boolean isValid(Object object)
    {
        return super.isValid() && object instanceof Float && ((float) object) >= min && ((float) object) <= max;
    }
}
