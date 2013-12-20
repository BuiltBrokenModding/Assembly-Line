package dark.assembly.client.gui;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;

import com.dark.prefab.TileEntityMachine;
import com.dark.prefab.invgui.ContainerFake;
import com.dark.prefab.invgui.GuiMachineContainer;

import dark.assembly.AssemblyLine;
import dark.assembly.CommonProxy;

public class GuiEncoderBase extends GuiMachineContainer
{
    //
    public GuiEncoderBase(InventoryPlayer player, TileEntityMachine tileEntity, Container container)
    {
        super(AssemblyLine.instance, container, player, tileEntity);
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
