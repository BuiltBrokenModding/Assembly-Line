package dark.assembly.client.gui;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import dark.assembly.AssemblyLine;
import dark.assembly.CommonProxy;
import dark.core.client.gui.GuiMachineContainer;
import dark.core.prefab.invgui.ContainerFake;
import dark.core.prefab.machine.TileEntityMachine;

public class GuiEncoderBase extends GuiMachineContainer
{
    //
    public GuiEncoderBase(InventoryPlayer player, TileEntityMachine tileEntity, Container container)
    {
        super(AssemblyLine.MOD_ID, container, player, tileEntity);
        this.guiID = CommonProxy.GUI_ENCODER;
        this.guiID2 = CommonProxy.GUI_ENCODER_CODE;
        this.guiID3 = CommonProxy.GUI_ENCODER_HELP;
        this.invName = "Main";
        this.invName2 = "Coder";
        this.invName3 = "Help";
    }

    public GuiEncoderBase(InventoryPlayer player, TileEntityMachine tileEntity)
    {
        this(player, tileEntity, new ContainerFake(tileEntity));
    }

}
