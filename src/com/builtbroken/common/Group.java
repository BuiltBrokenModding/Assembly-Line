package com.builtbroken.common;

import java.util.ArrayList;
import java.util.List;

/** Generic class for link objects of the same class type to each other.
 * 
 * @author Robert Seifert */
public class Group<J>
{
    private String groupName;
    protected List<J> memebers = new ArrayList<J>();

    public Group(String name, J... js)
    {
        this.groupName = name;
        if (js != null)
        {
            for (J obj : js)
            {
                this.addMemeber(obj);
            }
        }
    }

    public List<J> getMembers()
    {
        return this.memebers;
    }

    protected boolean isValid(J obj)
    {
        return obj != null && !memebers.contains(obj);
    }

    public boolean addMemeber(J obj)
    {
        if (this.isValid(obj))
        {
            return memebers.add(obj);
        }
        return false;
    }

    public boolean removeMemeber(J obj)
    {
        return memebers.remove(obj);
    }

    public boolean isMemeber(J obj)
    {
        return memebers.contains(obj);
    }

    public String getName()
    {
        return this.groupName;
    }

    public void setName(String name)
    {
        this.groupName = name;
    }
}
