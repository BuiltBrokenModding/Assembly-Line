package dark.assembly.client.gui;

import net.minecraft.entity.player.EntityPlayer;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import dark.assembly.AssemblyLine;
import dark.core.client.gui.GuiMachineBase;
import dark.core.prefab.machine.TileEntityMachine;

@SideOnly(Side.CLIENT)
public class GuiManipulator extends GuiMachineBase
{
    public GuiManipulator(EntityPlayer player, TileEntityMachine tileEntity)
    {
        super(AssemblyLine.instance, player, tileEntity);
    }
}
