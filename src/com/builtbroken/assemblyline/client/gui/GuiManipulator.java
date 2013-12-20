package com.builtbroken.assemblyline.client.gui;

import net.minecraft.entity.player.EntityPlayer;

import com.builtbroken.assemblyline.AssemblyLine;
import com.builtbroken.minecraft.prefab.TileEntityMachine;
import com.builtbroken.minecraft.prefab.invgui.GuiMachineBase;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiManipulator extends GuiMachineBase
{
    public GuiManipulator(EntityPlayer player, TileEntityMachine tileEntity)
    {
        super(AssemblyLine.instance, player, tileEntity);
    }
}
