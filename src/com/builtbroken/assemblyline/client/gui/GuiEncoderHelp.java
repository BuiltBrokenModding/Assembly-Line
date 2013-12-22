package com.builtbroken.assemblyline.client.gui;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;

import com.builtbroken.assemblyline.AssemblyLine;
import com.builtbroken.assemblyline.machine.encoder.TileEntityEncoder;
import com.builtbroken.minecraft.DarkCore;

public class GuiEncoderHelp extends GuiEncoderBase
{
    public static final ResourceLocation TEXTURE_CODE_BACK = new ResourceLocation(AssemblyLine.DOMAIN, DarkCore.GUI_DIRECTORY + "gui_encoder_coder.png");

    public GuiEncoderHelp(InventoryPlayer player, TileEntityEncoder tileEntity)
    {
        super(player, tileEntity);
    }
}
