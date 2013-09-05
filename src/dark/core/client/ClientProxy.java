package dark.core.client;

import java.awt.Color;

import net.minecraft.world.World;
import universalelectricity.core.vector.Vector3;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import dark.core.client.renders.RenderCopperWire;
import dark.core.common.CommonProxy;
import dark.core.common.CoreRecipeLoader;
import dark.core.common.DarkMain;
import dark.core.common.transmit.TileEntityWire;

public class ClientProxy extends CommonProxy
{

    /** Renders a laser beam from one power to another by a set color for a set time
     * 
     * @param world - world this laser is to be rendered in
     * @param position - start vector3
     * @param target - end vector3
     * @param color - color of the beam
     * @param age - life of the beam in 1/20 secs */
    @Override
    public void renderBeam(World world, Vector3 position, Vector3 target, Color color, int age)
    {
        if (world.isRemote || FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT)
        {
            FMLClientHandler.instance().getClient().effectRenderer.addEffect(new FXBeam(world, position, target, color, DarkMain.TEXTURE_DIRECTORY + "", age));
        }
    }

    @Override
    public void init()
    {
        if (CoreRecipeLoader.blockWire != null)
        {
            ClientRegistry.bindTileEntitySpecialRenderer(TileEntityWire.class, new RenderCopperWire());
        }
    }
}
