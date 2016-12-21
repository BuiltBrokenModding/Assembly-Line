package com.builtbroken.assemblyline.client;

import com.builtbroken.assemblyline.AssemblyLine;
import com.builtbroken.assemblyline.CommonProxy;
import com.builtbroken.assemblyline.content.inserter.TileInsertArmClient;
import com.builtbroken.assemblyline.content.rail.carts.EntityCart;
import com.builtbroken.assemblyline.content.rail.carts.RenderCart;
import com.builtbroken.assemblyline.content.rail.powered.TilePowerRailClient;
import cpw.mods.fml.client.registry.RenderingRegistry;

/**
 * Created by DarkGuardsman on 8/31/2015.
 */
public class ClientProxy extends CommonProxy
{
    @Override
    public void preInit()
    {
        super.preInit();
        AssemblyLine.blockPowerRail = AssemblyLine.INSTANCE.getManager().newBlock("cartPowerRail", TilePowerRailClient.class);
        AssemblyLine.blockInserter = AssemblyLine.INSTANCE.getManager().newBlock("insertArm", TileInsertArmClient.class);
    }

    @Override
    public void init()
    {
        RenderingRegistry.registerEntityRenderingHandler(EntityCart.class, new RenderCart());
    }
}
