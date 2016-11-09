package com.builtbroken.assemblyline.client;

import com.builtbroken.assemblyline.AssemblyLine;
import com.builtbroken.assemblyline.CommonProxy;
import com.builtbroken.assemblyline.content.rail.powered.TilePowerRailClient;

/**
 * Created by DarkGuardsman on 8/31/2015.
 */
public class ClientProxy extends CommonProxy
{
    @Override
    public void preInit()
    {
        super.preInit();
        AssemblyLine.blockPowerRail =  AssemblyLine.INSTANCE.getManager().newBlock("cartPowerRail", TilePowerRailClient.class);
    }
}
