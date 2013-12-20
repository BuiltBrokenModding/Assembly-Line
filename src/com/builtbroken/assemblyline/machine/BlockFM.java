package com.builtbroken.assemblyline.machine;

import net.minecraft.block.material.Material;

import com.builtbroken.assemblyline.AssemblyLine;
import com.builtbroken.minecraft.IndustryTabs;
import com.builtbroken.minecraft.prefab.BlockMachine;

public abstract class BlockFM extends BlockMachine
{

    public BlockFM(String name, Material material)
    {
        super(AssemblyLine.CONFIGURATION, name, material);
        this.setCreativeTab(IndustryTabs.tabHydraulic());
    }
}
