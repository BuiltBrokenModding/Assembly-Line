package dark.mech.steam;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
import dark.mech.steam.boiler.TileEntityBoiler;
import dark.mech.steam.renders.RenderBoiler;
import dark.mech.steam.renders.RenderGearPiston;
import dark.mech.steam.steamengine.TileEntitySteamPiston;

public class SteamClientProxy extends SteamProxy
{

    public void preInit()
    {
        RenderingRegistry.registerBlockHandler(new ItemRenderHelperS());
    }

    @Override
    public void init()
    {
        ClientRegistry.registerTileEntity(TileEntityBoiler.class, "boiler", new RenderBoiler(0f));
        // ClientRegistry.registerTileEntity(TileEntityFireBox.class, "fireBox", new RenderFurnace());
        ClientRegistry.registerTileEntity(TileEntitySteamPiston.class, "generator", new RenderGearPiston());
    }

    public void postInit()
    {

    }

}
