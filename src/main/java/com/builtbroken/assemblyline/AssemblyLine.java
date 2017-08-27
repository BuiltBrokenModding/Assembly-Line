package com.builtbroken.assemblyline;

import com.builtbroken.assemblyline.client.ALCreativeTab;
import com.builtbroken.assemblyline.content.armbot.TileArmbot;
import com.builtbroken.assemblyline.content.belt.TileBelt;
import com.builtbroken.assemblyline.content.ejector.TileEjector;
import com.builtbroken.assemblyline.content.manipulator.TileManipulator;
import com.builtbroken.assemblyline.content.parts.ItemCraftingParts;
import com.builtbroken.assemblyline.content.rail.BlockRail;
import com.builtbroken.assemblyline.content.rail.ItemBlockRail;
import com.builtbroken.assemblyline.content.rail.carts.EntityCart;
import com.builtbroken.assemblyline.content.rail.carts.ItemCart;
import com.builtbroken.mc.core.Engine;
import com.builtbroken.mc.framework.mod.AbstractMod;
import com.builtbroken.mc.framework.mod.ModCreativeTab;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.EntityRegistry;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

/**
 * Created by DarkGuardsman on 8/31/2015.
 */
@Mod(modid = AssemblyLine.DOMAIN, name = AssemblyLine.NAME, version = AssemblyLine.VERSION, dependencies = "required-after:voltzengine")
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
    public static Block blockInserter;
    public static Block blockRail;
    public static Block blockPowerRail;

    public static Item itemParts;
    public static Item itemCart;

    public static int ENTITY_ID_PREFIX = 70;

    public AssemblyLine()
    {
        super(DOMAIN);
        CREATIVE_TAB = new ALCreativeTab();
        manager.setTab(CREATIVE_TAB);
    }

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        super.preInit(event);
        Engine.requestOres();
        Engine.requestResources();
        Engine.requestSheetMetalContent();
        Engine.requestMultiBlock();
        Engine.requestSimpleTools();
        Engine.requestCircuits();
        Engine.requestCraftingParts();

        itemParts = getManager().newItem("alCraftingParts", new ItemCraftingParts());
        itemCart = getManager().newItem("alCarts", new ItemCart());

        if (Engine.runningAsDev)
        {
            blockArmbot = getManager().newBlock("alArmbot", TileArmbot.class);
            blockBelt = getManager().newBlock("alBelt", TileBelt.class);
            blockEjector = getManager().newBlock("alEjector", TileEjector.class);
            blockManipulator = getManager().newBlock("alManipulator", TileManipulator.class);
        }

        //Some content is registered in the proxy
        blockRail = manager.newBlock("cartTransportRail", BlockRail.class, ItemBlockRail.class);
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event)
    {
        super.init(event);
        CREATIVE_TAB.itemStack = new ItemStack(blockInserter);

        EntityRegistry.registerGlobalEntityID(EntityCart.class, "ALTransportCart", EntityRegistry.findGlobalUniqueEntityId());
        EntityRegistry.registerModEntity(EntityCart.class, "ALTransportCart", ENTITY_ID_PREFIX, this, 500, 1, true);
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
