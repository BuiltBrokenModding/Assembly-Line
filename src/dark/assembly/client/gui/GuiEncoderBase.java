package dark.assembly.client.gui;

import net.minecraft.entity.player.EntityPlayer;
import dark.assembly.AssemblyLine;
import dark.assembly.CommonProxy;
import dark.core.client.gui.GuiMachineBase;
import dark.core.prefab.machine.TileEntityMachine;

public class GuiEncoderBase extends GuiMachineBase
{

    public GuiEncoderBase(EntityPlayer player, TileEntityMachine tileEntity)
    {
        super(AssemblyLine.MOD_ID, player, tileEntity);
        this.guiID = CommonProxy.GUI_ENCODER;
        this.guiID2 = CommonProxy.GUI_ENCODER_CODE;
        this.guiID3 = CommonProxy.GUI_ENCODER_HELP;
        this.invName = "Main";
        this.invName2 = "Coder";
        this.invName3 = "Help";
    }

}
