package dark.core.common;

import java.awt.Color;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import universalelectricity.core.vector.Vector3;
import cpw.mods.fml.common.network.IGuiHandler;
import dark.core.common.machines.ContainerCoalGenerator;
import dark.core.common.machines.TileEntitySteamGen;
import dark.core.network.PacketManagerEffects;

public class CommonProxy implements IGuiHandler
{
    public static final int GUI_COAL_GEN = 0, GUI_FUEL_GEN = 1, GUI_FURNACE_ELEC = 2, GUI_BATTERY_BOX = 3;

    public void preInit()
    {
        // TODO Auto-generated method stub

    }

    public void init()
    {
        // TODO Auto-generated method stub

    }

    public void postInit()
    {
        // TODO Auto-generated method stub

    }

    /** Renders a laser beam from one power to another by a set color for a set time
     * 
     * @param world - world this laser is to be rendered in
     * @param position - start vector3
     * @param target - end vector3
     * @param color - color of the beam
     * @param age - life of the beam in 1/20 secs */
    public void renderBeam(World world, Vector3 position, Vector3 target, Color color, int age)
    {
        PacketManagerEffects.sendClientLaserEffect(world, position, target, color, age);
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
    {

        return null;
    }

    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
    {
        TileEntity tileEntity = world.getBlockTileEntity(x, y, z);

        if (tileEntity != null)
        {
            if (tileEntity instanceof TileEntitySteamGen)
            {
                return new ContainerCoalGenerator(player.inventory, ((TileEntitySteamGen) tileEntity));
            }
        }

        return null;
    }

}
