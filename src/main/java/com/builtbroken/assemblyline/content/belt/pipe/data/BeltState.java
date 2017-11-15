package com.builtbroken.assemblyline.content.belt.pipe.data;

import com.builtbroken.mc.api.IInventoryFilter;

public class BeltState
{
    //Direction of the belt
    public boolean output;
    //Inventory filter, only works on inputs
    public IInventoryFilter filter;
}