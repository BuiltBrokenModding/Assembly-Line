package liquidmechanics.common.handlers;

import liquidmechanics.api.helpers.PipeColor;
import liquidmechanics.common.tileentity.TileEntityPipe;
/**
 * used to keep track of a pipe, its meta, and if Universal
 */
public class PipeInstance
{
    public PipeColor color;
    public TileEntityPipe pipe;
    //colors
    public PipeInstance(PipeColor cc, TileEntityPipe pipe)
    {
        this.color = cc;
        this.pipe = pipe;
    }
}
