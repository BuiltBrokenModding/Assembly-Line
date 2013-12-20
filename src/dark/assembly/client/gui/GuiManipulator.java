package dark.assembly.client.gui;

import net.minecraft.entity.player.EntityPlayer;

import com.dark.prefab.TileEntityMachine;
import com.dark.prefab.invgui.GuiMachineBase;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import dark.assembly.AssemblyLine;

@SideOnly(Side.CLIENT)
public class GuiManipulator extends GuiMachineBase
{
    public GuiManipulator(EntityPlayer player, TileEntityMachine tileEntity)
    {
        super(AssemblyLine.instance, player, tileEntity);
    }
}
