package liquidmechanics.api;

import liquidmechanics.api.helpers.ColorCode;

public interface IColorCoded
{
    /**
     * Returns the ColorCode of the object
     */
    public ColorCode getColor();
    /**
     * Sets the ColorCode of the Object
     */
    public void setColor(Object obj);
}
