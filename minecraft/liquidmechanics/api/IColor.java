package liquidmechanics.api;

import liquidmechanics.api.helpers.PipeColor;

public interface IColor
{
    /**
     * gets the pipeColor being used by this object
     */
    public PipeColor getColor();
    /**
     * sets the pipeColor to be used by this object     * 
     * @param obj-can be anything must be sorted
     */
    public void setColor(Object obj);
}
