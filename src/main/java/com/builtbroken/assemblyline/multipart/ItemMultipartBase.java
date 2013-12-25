package com.builtbroken.assemblyline.multipart;

import codechicken.multipart.JItemMultiPart;

import com.builtbroken.assemblyline.AssemblyLine;
import com.builtbroken.minecraft.DarkCore;
import com.builtbroken.minecraft.IndustryTabs;

public abstract class ItemMultipartBase extends JItemMultiPart
{
    public ItemMultipartBase(String name)
    {
        super(AssemblyLine.CONFIGURATION.getItem(name, DarkCore.getNextItemId()).getInt());
        this.setCreativeTab(IndustryTabs.tabIndustrial());
        this.setUnlocalizedName(AssemblyLine.PREFIX + name);
        this.setTextureName(AssemblyLine.PREFIX + name);
    }
}