package com.builtbroken.assemblyline.client;

import com.builtbroken.assemblyline.AssemblyLine;
import com.builtbroken.assemblyline.CommonProxy;
import com.builtbroken.assemblyline.content.belt.TileSimpleBelt;
import com.builtbroken.assemblyline.content.inserter.TileInsertArmClient;
import com.builtbroken.assemblyline.content.rail.carts.EntityCart;
import com.builtbroken.assemblyline.content.rail.carts.RenderCart;
import com.builtbroken.assemblyline.content.rail.powered.TilePowerRailClient;
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
    long lastAnimationUpdate = 0L;

    @Override
    public void preInit()
    {
        super.preInit();
        FMLCommonHandler.instance().bus().register(this);
        AssemblyLine.blockPowerRail = AssemblyLine.INSTANCE.getManager().newBlock("cartPowerRail", TilePowerRailClient.class);
        AssemblyLine.blockInserter = AssemblyLine.INSTANCE.getManager().newBlock("insertArm", TileInsertArmClient.class);
    }

    @Override
    public void init()
    {
        RenderingRegistry.registerEntityRenderingHandler(EntityCart.class, new RenderCart());

        //The belt animation should match the speed, speed is how many meters to move per ticks
        //      it can also be thought of as how far does the belt rotate a tick
        BELT_ANIMATION_UPDATE_TIME = (int) (TileSimpleBelt.beltSpeed * 20 * 50);
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
                TileSimpleBelt.FRAME_SLANTED++;

                int frames = AssemblyLine.simpleBelt.data.getSettingAsInt("flat.belt.frames");
                if (frames == 0)
                {
                    frames = 14;
                }
                if (TileSimpleBelt.FRAME_FLAT >= frames)
                {
                    TileSimpleBelt.FRAME_FLAT = 0;
                }

                frames = AssemblyLine.simpleBelt.data.getSettingAsInt("slanted.belt.frames");
                if (frames == 0)
                {
                    frames = 24;
                }
                if (TileSimpleBelt.FRAME_SLANTED >= frames)
                {
                    TileSimpleBelt.FRAME_SLANTED = 0;
                }
            }
        }
    }
}
