package com.builtbroken.assemblyline.content.belt.pipe;

import com.builtbroken.mc.client.json.ClientDataHandler;
import com.builtbroken.mc.data.Direction;
import com.builtbroken.mc.lib.render.RenderUtility;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.client.event.RenderWorldEvent;
import net.minecraftforge.common.MinecraftForge;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 11/14/2017.
 */
public class ISBR_Belt implements ISimpleBlockRenderingHandler
{
    public final static int ID = RenderingRegistry.getNextAvailableRenderId();


    private int pass = 0;

    public ISBR_Belt()
    {
        ClientDataHandler.INSTANCE.addBlockRenderer("pipeBeltRender", this);
        MinecraftForge.EVENT_BUS.register(this);
    }

    @Override
    public void renderInventoryBlock(Block block, int metadata, int modelId, RenderBlocks renderer)
    {

    }

    @Override
    public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer)
    {
        final double p = 1.0 / 16.0;
        renderer.setRenderAllFaces(true);
        Direction direction = world != null ? Direction.getOrientation(world.getBlockMetadata(x, y, z)) : Direction.EAST;

        int prevUVTop = renderer.uvRotateTop;
        switch (direction)
        {
            case NORTH:
                renderer.uvRotateTop = 3;
                break;
            case SOUTH:
                renderer.uvRotateTop = 0;
                break;
            case EAST:
                renderer.uvRotateTop = 2;
                break;
            case WEST:
                renderer.uvRotateTop = 1;
                break;
        }

        if (direction == Direction.WEST || direction == Direction.EAST)
        {
            if(pass == 0)
            {
                //Lower bars
                bounds(renderer,
                        0, p * 4, p * 4,
                        1, p * 2, p);
                renderBlock(renderer, block, x, y, z, block.getIcon(0, 1));

                bounds(renderer,
                        0, p * 4, 1 - p * 5,
                        1, p * 2, p);
                renderBlock(renderer, block, x, y, z, block.getIcon(0, 1));

                //Upper bars
                bounds(renderer,
                        0, p * 10, p * 4,
                        1, p * 2, p);
                renderBlock(renderer, block, x, y, z, block.getIcon(0, 1));

                bounds(renderer,
                        0, p * 10, 1 - p * 5,
                        1, p * 2, p);
                renderBlock(renderer, block, x, y, z, block.getIcon(0, 1));

                //Top bars
                bounds(renderer,
                        0, p * 11, p * 5,
                        1, p * 2, p);
                renderBlock(renderer, block, x, y, z, block.getIcon(0, 1));

                bounds(renderer,
                        0, p * 11, 1 - p * 6,
                        1, p * 2, p);
                renderBlock(renderer, block, x, y, z, block.getIcon(0, 1));

                //Base
                bounds(renderer,
                        0, 0, p * 5,
                        1, p * 5, p * 6);
                renderBlock(renderer, block, x, y, z, null);


                //End caps top
                bounds(renderer,
                        0, p * 12, p * 6,
                        p, p, p * 4);
                renderBlock(renderer, block, x, y, z, block.getIcon(0, 1));

                bounds(renderer,
                        p * 15, p * 12, p * 6,
                        p, p, p * 4);
                renderBlock(renderer, block, x, y, z, block.getIcon(0, 1));

                //End caps sides
                bounds(renderer,
                        0, p * 6, p * 4,
                        p, p * 4, p);
                renderBlock(renderer, block, x, y, z, block.getIcon(0, 1));

                bounds(renderer,
                        0, p * 6, 1 - p * 5,
                        p, p * 4, p);
                renderBlock(renderer, block, x, y, z, block.getIcon(0, 1));

                bounds(renderer,
                        1 - p, p * 6, p * 4,
                        p, p * 4, p);
                renderBlock(renderer, block, x, y, z, block.getIcon(0, 1));

                bounds(renderer,
                        1 - p, p * 6, 1 - p * 5,
                        p, p * 4, p);
                renderBlock(renderer, block, x, y, z, block.getIcon(0, 1));
            }
            else
            {
                //Glass Top
                bounds(renderer,
                        p, p * 12, p * 6,
                        1 - p * 2, p, p * 4);
                renderBlock(renderer, block, x, y, z, block.getIcon(3, 1));

                //Glass walls
                bounds(renderer,
                        p, p * 6, p * 4,
                        p * 14, p * 4, p);
                renderBlock(renderer, block, x, y, z, block.getIcon(3, 1));

                bounds(renderer,
                        p, p * 6, 1 - p * 5,
                        p * 14, p * 4, p);
                renderBlock(renderer, block, x, y, z, block.getIcon(3, 1));
            }
        }
        else
        {
            if(pass == 0)
            {
                //Lower bars
                bounds(renderer,
                        p * 4, p * 4, 0,
                        p, p * 2, 1);
                renderBlock(renderer, block, x, y, z, block.getIcon(0, 1));

                bounds(renderer,
                        1 - p * 5, p * 4, 0,
                        p, p * 2, 1);
                renderBlock(renderer, block, x, y, z, block.getIcon(0, 1));

                //Upper bars
                bounds(renderer,
                        p * 4, p * 10, 0,
                        p, p * 2, 1);
                renderBlock(renderer, block, x, y, z, block.getIcon(0, 1));

                bounds(renderer,
                        1 - p * 5, p * 10, 0,
                        p, p * 2, 1);
                renderBlock(renderer, block, x, y, z, block.getIcon(0, 1));

                //Top bars
                bounds(renderer,
                        p * 5, p * 11, 0,
                        p, p * 2, 1);
                renderBlock(renderer, block, x, y, z, block.getIcon(0, 1));

                bounds(renderer,
                        1 - p * 6, p * 11, 0,
                        p, p * 2, 1);
                renderBlock(renderer, block, x, y, z, block.getIcon(0, 1));

                //Base
                bounds(renderer,
                        p * 5, 0, 0,
                        p * 6, p * 5, 1);
                renderBlock(renderer, block, x, y, z, null);

                //End caps
                bounds(renderer,
                        p * 6, p * 12, 0,
                        p * 4, p, p);
                renderBlock(renderer, block, x, y, z, block.getIcon(0, 1));

                bounds(renderer,
                        p * 6, p * 12, p * 15,
                        p * 4, p, p);
                renderBlock(renderer, block, x, y, z, block.getIcon(0, 1));

                //End caps sides
                bounds(renderer,
                        p * 4, p * 6, 0,
                        p, p * 4, p);
                renderBlock(renderer, block, x, y, z, block.getIcon(0, 1));

                bounds(renderer,
                        1 - p * 5, p * 6, 0,
                        p, p * 4, p);
                renderBlock(renderer, block, x, y, z, block.getIcon(0, 1));

                bounds(renderer,
                        p * 4, p * 6, 1 - p,
                        p, p * 4, p);
                renderBlock(renderer, block, x, y, z, block.getIcon(0, 1));

                bounds(renderer,
                        1 - p * 5, p * 6, 1 - p,
                        p, p * 4, p);
                renderBlock(renderer, block, x, y, z, block.getIcon(0, 1));
            }
            else
            {
                //Glass Top
                bounds(renderer,
                        p * 6, p * 12, p,
                        p * 4, p, 1 - p * 2);
                renderBlock(renderer, block, x, y, z, block.getIcon(3, 1));

                //Glass walls
                bounds(renderer,
                        p * 4, p * 6, p,
                        p, p * 4, p * 14);
                renderBlock(renderer, block, x, y, z, block.getIcon(3, 1));

                bounds(renderer,
                        1 - p * 5, p * 6, p,
                        p, p * 4, p * 14);
                renderBlock(renderer, block, x, y, z, block.getIcon(3, 1));
            }
        }

        renderer.uvRotateTop = prevUVTop;
        renderer.setRenderAllFaces(false);
        return true;
    }

    protected void bounds(RenderBlocks renderer, double x, double y, double z, double xx, double yy, double zz)
    {
        renderer.setRenderBounds(x, y, z, x + xx, y + yy, z + zz);
    }

    public void renderBlock(RenderBlocks renderer, Block block, int x, int y, int z, IIcon icon)
    {
        if (y == -1)
        {
            RenderUtility.renderCube(renderer.renderMinX, renderer.renderMinY, renderer.renderMinZ, renderer.renderMaxX, renderer.renderMaxY, renderer.renderMaxZ, block, icon, 0);
        }
        else
        {
            renderer.setOverrideBlockTexture(icon);
            renderer.renderStandardBlock(block, x, y, z);
            renderer.setOverrideBlockTexture(null);
        }
    }

    @Override
    public boolean shouldRender3DInInventory(int modelId)
    {
        return false;
    }

    @Override
    public int getRenderId()
    {
        return ID;
    }

    @SubscribeEvent
    public void postWorldRender(RenderWorldEvent.Post event)
    {
        pass = 0;
    }

    @SubscribeEvent
    public void preWorldRender(RenderWorldEvent.Pre event)
    {
        pass = event.pass;
    }

}
