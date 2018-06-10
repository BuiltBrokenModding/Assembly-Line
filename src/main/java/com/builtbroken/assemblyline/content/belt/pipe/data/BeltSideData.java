package com.builtbroken.assemblyline.content.belt.pipe.data;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 6/10/2018.
 */
public class BeltSideData
{
    public boolean output;
    public boolean enabled = true;
    public BeltInventoryFilter filter;

    public BeltSideData(boolean output)
    {
        this.output = output;
    }

}
