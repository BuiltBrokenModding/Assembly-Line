package dark.farmtech.client;

import net.minecraft.client.renderer.entity.RenderSnowball;
import net.minecraft.item.Item;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import dark.farmtech.CommonProxy;
import dark.farmtech.client.renders.RenderTurkey;
import dark.farmtech.entities.EntityFarmEgg;
import dark.farmtech.entities.EntityTurkey;

@SideOnly(Side.CLIENT)
public class ClientProxy extends CommonProxy
{
    @Override
    public void init()
    {
        RenderingRegistry.registerEntityRenderingHandler(EntityTurkey.class, new RenderTurkey());
        RenderingRegistry.registerEntityRenderingHandler(EntityFarmEgg.class, new RenderSnowball(Item.egg));
    }
}
