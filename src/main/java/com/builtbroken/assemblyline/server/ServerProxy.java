package com.builtbroken.assemblyline.server;

import com.builtbroken.assemblyline.AssemblyLine;
import com.builtbroken.assemblyline.CommonProxy;
import com.builtbroken.assemblyline.content.inserter.TileInsertArm;
import com.builtbroken.assemblyline.content.rail.powered.TilePowerRail;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 11/9/2016.
 */
public class ServerProxy extends CommonProxy
{
    @Override
    public void preInit()
    {
        super.preInit();
        AssemblyLine.blockPowerRail = AssemblyLine.INSTANCE.getManager().newBlock("cartPowerRail", TilePowerRail.class);
        AssemblyLine.blockInserter = AssemblyLine.INSTANCE.getManager().newBlock("insertArm", TileInsertArm.class);
    }
}
