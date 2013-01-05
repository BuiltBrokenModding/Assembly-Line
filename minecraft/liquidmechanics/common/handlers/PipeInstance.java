package liquidmechanics.common.handlers;

import liquidmechanics.common.tileentity.TileEntityPipe;
/**
 * used to keep track of a pipe, its meta, and if Universal
 */
public class PipeInstance
{
    public int color;
    public TileEntityPipe pipe;
    public boolean any;

    public PipeInstance(int color, TileEntityPipe pipe, boolean any)
    {
        this.color = color;
        this.pipe = pipe;
        this.any = any;
    }
}
