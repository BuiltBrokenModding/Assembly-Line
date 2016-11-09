package com.builtbroken.assemblyline;

import com.builtbroken.assemblyline.content.armbot.TileArmbot;
import com.builtbroken.assemblyline.content.belt.TileBelt;
import com.builtbroken.assemblyline.content.ejector.TileEjector;
import com.builtbroken.assemblyline.content.manipulator.TileManipulator;
import com.builtbroken.assemblyline.content.rail.BlockRail;
import com.builtbroken.assemblyline.content.rail.ItemBlockRail;
import com.builtbroken.mc.core.Engine;
import com.builtbroken.mc.lib.mod.AbstractMod;
import com.builtbroken.mc.lib.mod.ModCreativeTab;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import net.minecraft.block.Block;

/**
 * Created by DarkGuardsman on 8/31/2015.
 */
@Mod(modid = AssemblyLine.DOMAIN, name = AssemblyLine.NAME, version = AssemblyLine.VERSION, dependencies = "required-after:VoltzEngine")
public class AssemblyLine extends AbstractMod
{
    //Meta
    public static final String NAME = "Assembly Line";
    public static final String DOMAIN = "assemblyline";
    public static final String PREFIX = DOMAIN + ":";

    // Version numbers
    public static final String MAJOR_VERSION = "@MAJOR@";
    public static final String MINOR_VERSION = "@MINOR@";
    public static final String REVISION_VERSION = "@REVIS@";
    public static final String BUILD_VERSION = "@BUILD@";
    public static final String VERSION = MAJOR_VERSION + "." + MINOR_VERSION + "." + REVISION_VERSION + "." + BUILD_VERSION;

    @Mod.Instance(DOMAIN)
    public static AssemblyLine INSTANCE;

    @SidedProxy(clientSide = "com.builtbroken.assemblyline.client.ClientProxy", serverSide = "com.builtbroken.assemblyline.server.ServerProxy")
    public static CommonProxy proxy;

    public final ModCreativeTab CREATIVE_TAB;

    public static Block blockArmbot;
    public static Block blockBelt;
    public static Block blockEjector;
    public static Block blockManipulator;

    /** Simple robotic arm that only handles items, NOT A COPY FROM FACTORIO */
    public static Block blockInserter;

    public static Block blockRail;
    public static Block blockPowerRail;

    public AssemblyLine()
    {
        super(DOMAIN);
        CREATIVE_TAB = new ModCreativeTab("ICBM");
        manager.setTab(CREATIVE_TAB);
    }

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        super.preInit(event);
        if (Engine.runningAsDev)
        {
            blockArmbot = getManager().newBlock("alArmbot", TileArmbot.class);
            blockBelt = getManager().newBlock("alBelt", TileBelt.class);
            blockEjector = getManager().newBlock("alEjector", TileEjector.class);
            blockManipulator = getManager().newBlock("alManipulator", TileManipulator.class);
        }
        blockRail = manager.newBlock("cartTransportRail", BlockRail.class, ItemBlockRail.class);
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event)
    {
        super.init(event);
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event)
    {
        super.postInit(event);
    }

    @Override
    public CommonProxy getProxy()
    {
        return proxy;
    }
}
