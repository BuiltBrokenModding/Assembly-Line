package com.builtbroken.assemblyline.content.belt.pipe.data;

import com.builtbroken.assemblyline.content.belt.TilePipeBelt;

import java.util.Iterator;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 11/22/2017.
 */
public class BeltSideStateIterator implements Iterator<BeltSideState>, Iterable<BeltSideState>
{
    private int index = -1;
    private int nextIndex = -1;
    private boolean output;

    private TilePipeBelt belt;

    public BeltSideStateIterator(TilePipeBelt belt, boolean output)
    {
        this.belt = belt;
        this.output = output;
    }

    @Override
    public boolean hasNext()
    {
        while ((peek() == null || peek().output != output) && nextIndex < belt.getBeltStates().size())
        {
            nextIndex = nextIndex + 1;
        }
        BeltSideState state = peek();
        return state != null && state.output == output;
    }

    @Override
    public BeltSideState next()
    {
        //Get current listener
        BeltSideState re = peek();

        //set next index
        index = nextIndex;
        nextIndex++;
        return re;
    }

    /**
     * Looks at what the next entry in the list will be
     *
     * @return
     */
    protected BeltSideState peek()
    {
        return get(nextIndex);
    }

    protected BeltSideState get()
    {
        return get(index);
    }

    /**
     * Gets the item at the index
     *
     * @return
     */
    protected BeltSideState get(int index)
    {
        if (index >= 0 && index < belt.getBeltStates().size())
        {
            return belt.getBeltStates().get(index);
        }
        return null;
    }

    public BeltSideStateIterator reset()
    {
        index = -1;
        nextIndex = -1;
        return this;
    }

    @Override
    public Iterator<BeltSideState> iterator()
    {
        return this;
    }
}
