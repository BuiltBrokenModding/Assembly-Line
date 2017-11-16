package com.builtbroken.assemblyline.client;

import com.builtbroken.assemblyline.AssemblyLine;
import com.builtbroken.assemblyline.CommonProxy;
import com.builtbroken.assemblyline.content.belt.TileSimpleBelt;
import com.builtbroken.assemblyline.content.belt.pipe.ISBR_Belt;
import com.builtbroken.assemblyline.content.belt.pipe.TilePipeRenderListener;
import com.builtbroken.assemblyline.content.inserter.TileInsertArmClient;
import com.builtbroken.assemblyline.content.rail.carts.EntityCart;
import com.builtbroken.assemblyline.content.rail.carts.RenderCart;
import com.builtbroken.assemblyline.content.rail.powered.TilePowerRailClient;
import com.builtbroken.mc.seven.framework.block.json.JsonBlockListenerProcessor;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import net.minecraft.client.Minecraft;

/**
 * Created by DarkGuardsman on 8/31/2015.
 */
public class ClientProxy extends CommonProxy
{
    /** Amount of time to delay between switching animation frames (in milliseconds) */
    public static int BELT_ANIMATION_UPDATE_TIME = 100;
    public static int BELT_ANIMATION_UPDATE_TIME_2 = 100;

    public static int FLAT_BELT_FRAMES = 14;
    public static int SLANTED_BELT_FRAMES = 24;

    long lastAnimationUpdate = 0L;
    long lastAnimationUpdate_2 = 0L;

    @Override
    public void preInit()
    {
        super.preInit();
        FMLCommonHandler.instance().bus().register(this);
        AssemblyLine.blockPowerRail = AssemblyLine.INSTANCE.getManager().newBlock("cartPowerRail", TilePowerRailClient.class);
        AssemblyLine.blockInserter = AssemblyLine.INSTANCE.getManager().newBlock("insertArm", TileInsertArmClient.class);
    }

    @Override
    public void loadJsonContentHandlers()
    {
        RenderingRegistry.registerBlockHandler(new ISBR_Belt());
        JsonBlockListenerProcessor.addBuilder(new TilePipeRenderListener.Builder());
    }

    @Override
    public void init()
    {
        super.init();
        RenderingRegistry.registerEntityRenderingHandler(EntityCart.class, new RenderCart());
    }

    @Override
    public void postInit()
    {
        super.postInit();
        loadSettings();
    }

    public void loadSettings()
    {
        int frames = AssemblyLine.simpleBelt.data.getSettingAsInt("flat.belt.frames");
        if (frames > 0)
        {
            FLAT_BELT_FRAMES = frames;
        }

        frames = AssemblyLine.simpleBelt.data.getSettingAsInt("slanted.belt.frames");
        if (frames > 0)
        {
            SLANTED_BELT_FRAMES = 24;
        }

        //The belt animation should match the speed, speed is how many meters to move per ticks
        //      it can also be thought of as how far does the belt rotate a tick
        BELT_ANIMATION_UPDATE_TIME = (int) (TileSimpleBelt.beltSpeed * 20 * 50);
        BELT_ANIMATION_UPDATE_TIME_2 = (int) Math.floor(BELT_ANIMATION_UPDATE_TIME * ((float) FLAT_BELT_FRAMES / (float) SLANTED_BELT_FRAMES));
    }

    @SubscribeEvent
    public void clientUpdate(TickEvent.ClientTickEvent event)
    {
        if (AssemblyLine.simpleBelt != null && Minecraft.getMinecraft() != null && Minecraft.getMinecraft().theWorld != null && !Minecraft.getMinecraft().isGamePaused())
        {
            if (System.currentTimeMillis() - lastAnimationUpdate >= BELT_ANIMATION_UPDATE_TIME)
            {
                lastAnimationUpdate = System.currentTimeMillis();
                TileSimpleBelt.FRAME_FLAT++;

                if (TileSimpleBelt.FRAME_FLAT >= FLAT_BELT_FRAMES)
                {
                    TileSimpleBelt.FRAME_FLAT = 0;
                }
            }

            if (System.currentTimeMillis() - lastAnimationUpdate_2 >= BELT_ANIMATION_UPDATE_TIME_2)
            {
                lastAnimationUpdate_2 = System.currentTimeMillis();
                TileSimpleBelt.FRAME_SLANTED++;
                if (TileSimpleBelt.FRAME_SLANTED >= SLANTED_BELT_FRAMES)
                {
                    TileSimpleBelt.FRAME_SLANTED = 0;
                }
            }
        }
    }
}
