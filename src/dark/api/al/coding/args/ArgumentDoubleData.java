package dark.api.al.coding.args;

import universalelectricity.core.electricity.ElectricityDisplay;

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
        return super.isValid() && object instanceof Double && ((Double) object) >= min && ((Double) object) <= max;
    }

    @Override
    public String warning()
    {
        return "" + ElectricityDisplay.roundDecimals(min, 2) + " - " + ElectricityDisplay.roundDecimals(max, 2);
    }
}
