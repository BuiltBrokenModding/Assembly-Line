package com.builtbroken.assemblyline.content.belt.pipe;

import com.builtbroken.assemblyline.content.belt.TilePipeBelt;
import com.builtbroken.mc.api.tile.node.ITileNodeHost;
import com.builtbroken.mc.client.json.ClientDataHandler;
import com.builtbroken.mc.data.Direction;
import com.builtbroken.mc.lib.render.RenderUtility;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.tileentity.TileEntity;
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
    public final static double pixel = 1.0 / 16.0;

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
        boolean rendered = false;

        renderer.setRenderAllFaces(true);

        //Get data from tile
        Direction direction = world != null ? Direction.getOrientation(world.getBlockMetadata(x, y, z)) : Direction.EAST;
        boolean renderTop = true;
        BeltType type = BeltType.NORMAL;

        if (world != null)
        {
            TileEntity tile = world.getTileEntity(x, y, z);
            if (tile instanceof ITileNodeHost && ((ITileNodeHost) tile).getTileNode() instanceof TilePipeBelt)
            {
                TilePipeBelt belt = (TilePipeBelt) ((ITileNodeHost) tile).getTileNode();
                renderTop = belt.renderTop;
                type = belt.type;
            }
        }

        //Rotate textures
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

        if (type == BeltType.NORMAL)
        {
            rendered = renderNormalBelt(x, y, z, block, renderer, direction, renderTop);
        }
        else if (type == BeltType.LEFT_ELBOW)
        {
            rendered = renderLeftElbow(x, y, z, block, renderer, direction, renderTop);
        }
        else if (type == BeltType.RIGHT_ELBOW)
        {
            rendered = renderRightElbow(x, y, z, block, renderer, direction, renderTop);
        }

        renderer.uvRotateTop = prevUVTop;
        renderer.setRenderAllFaces(false);
        return rendered;
    }

    //TODO convert to JSON, as this is all repeat with data
    protected boolean renderLeftElbow(int x, int y, int z, Block block, RenderBlocks renderer, Direction direction, boolean renderTop)
    {
        if (direction == Direction.WEST)
        {
            if (pass == 0)
            {
                //Base facing
                bounds(renderer,
                        pixel * 5, 0, pixel * 5,
                        pixel * 11, pixel * 5, pixel * 6);
                renderBlock(renderer, block, x, y, z, null);

                //Base input
                renderer.uvRotateTop = 3;
                bounds(renderer,
                        pixel * 5, 0, 0,
                        pixel * 6, pixel * 5, pixel * 5);
                renderBlock(renderer, block, x, y, z, null);

                return true;
            }
        }
        else if (direction == Direction.EAST)
        {
            if (pass == 0)
            {
                //Base facing
                bounds(renderer,
                        0, 0, pixel * 5,
                        pixel * 11, pixel * 5, pixel * 6);
                renderBlock(renderer, block, x, y, z, null);

                //Base input
                renderer.uvRotateTop = 0;
                bounds(renderer,
                        pixel * 5, 0, pixel * 11,
                        pixel * 6, pixel * 5, pixel * 5);
                renderBlock(renderer, block, x, y, z, null);

                return true;
            }
        }
        else if (direction == Direction.NORTH)
        {
            if (pass == 0)
            {
                //Base
                bounds(renderer,
                        pixel * 5, 0, pixel * 5,
                        pixel * 6, pixel * 5, pixel * 11);
                renderBlock(renderer, block, x, y, z, null);

                renderer.uvRotateTop = 2;
                bounds(renderer,
                        pixel * 11, 0, pixel * 5,
                        pixel * 5, pixel * 5, pixel * 6);
                renderBlock(renderer, block, x, y, z, null);
                return true;
            }
        }
        else if (direction == Direction.SOUTH)
        {
            if (pass == 0)
            {
                //Base
                bounds(renderer,
                        pixel * 5, 0, 0,
                        pixel * 6, pixel * 5, pixel * 11);
                renderBlock(renderer, block, x, y, z, null);

                //Base input


                //Base input
                renderer.uvRotateTop = 1;
                bounds(renderer,
                        0, 0, pixel * 5,
                        pixel * 5, pixel * 5, pixel * 6);
                renderBlock(renderer, block, x, y, z, null);
                return true;
            }
        }
        return false;
    }

    //TODO convert to JSON, as this is all repeat with data
    protected boolean renderRightElbow(int x, int y, int z, Block block, RenderBlocks renderer, Direction direction, boolean renderTop)
    {
        if (direction == Direction.WEST)
        {
            if (pass == 0)
            {
                //Base facing
                bounds(renderer,
                        pixel * 5, 0, pixel * 5,
                        pixel * 11, pixel * 5, pixel * 6);
                renderBlock(renderer, block, x, y, z, null);

                //Base input
                renderer.uvRotateTop = 0;
                bounds(renderer,
                        pixel * 5, 0, pixel * 11,
                        pixel * 6, pixel * 5, pixel * 5);
                renderBlock(renderer, block, x, y, z, null);
                return true;
            }
        }
        else if (direction == Direction.EAST)
        {
            if (pass == 0)
            {
                //Base facing
                bounds(renderer,
                        0, 0, pixel * 5,
                        pixel * 11, pixel * 5, pixel * 6);
                renderBlock(renderer, block, x, y, z, null);


                //Base input
                renderer.uvRotateTop = 3;
                bounds(renderer,
                        pixel * 5, 0, 0,
                        pixel * 6, pixel * 5, pixel * 5);
                renderBlock(renderer, block, x, y, z, null);
                return true;
            }
        }
        else if (direction == Direction.NORTH)
        {
            if (pass == 0)
            {
                //Base
                bounds(renderer,
                        pixel * 5, 0, pixel * 5,
                        pixel * 6, pixel * 5, pixel * 11);
                renderBlock(renderer, block, x, y, z, null);

                //Base input
                renderer.uvRotateTop = 1;
                bounds(renderer,
                        0, 0, pixel * 5,
                        pixel * 5, pixel * 5, pixel * 6);
                renderBlock(renderer, block, x, y, z, null);
                return true;
            }
        }
        else if (direction == Direction.SOUTH)
        {
            if (pass == 0)
            {
                //Base
                bounds(renderer,
                        pixel * 5, 0, 0,
                        pixel * 6, pixel * 5, pixel * 11);
                renderBlock(renderer, block, x, y, z, null);

                //Base input
                renderer.uvRotateTop = 2;
                bounds(renderer,
                        pixel * 11, 0, pixel * 5,
                        pixel * 5, pixel * 5, pixel * 6);
                renderBlock(renderer, block, x, y, z, null);
                return true;
            }
        }
        return false;
    }

    //TODO convert to JSON, as this is all repeat with data
    protected boolean renderNormalBelt(int x, int y, int z, Block block, RenderBlocks renderer, Direction direction, boolean renderTop)
    {
        if (direction == Direction.WEST || direction == Direction.EAST)
        {
            if (pass == 0)
            {
                //Base
                bounds(renderer,
                        0, 0, pixel * 5,
                        1, pixel * 5, pixel * 6);
                renderBlock(renderer, block, x, y, z, null);

                //Lower bars
                bounds(renderer,
                        0, pixel * 4, pixel * 4,
                        1, pixel * 2, pixel);
                renderBlock(renderer, block, x, y, z, block.getIcon(0, 1));

                bounds(renderer,
                        0, pixel * 4, 1 - pixel * 5,
                        1, pixel * 2, pixel);
                renderBlock(renderer, block, x, y, z, block.getIcon(0, 1));

                if (renderTop)
                {
                    //Upper bars
                    bounds(renderer,
                            0, pixel * 10, pixel * 4,
                            1, pixel * 2, pixel);
                    renderBlock(renderer, block, x, y, z, block.getIcon(0, 1));

                    bounds(renderer,
                            0, pixel * 10, 1 - pixel * 5,
                            1, pixel * 2, pixel);
                    renderBlock(renderer, block, x, y, z, block.getIcon(0, 1));

                    //Top bars
                    bounds(renderer,
                            0, pixel * 11, pixel * 5,
                            1, pixel * 2, pixel);
                    renderBlock(renderer, block, x, y, z, block.getIcon(0, 1));

                    bounds(renderer,
                            0, pixel * 11, 1 - pixel * 6,
                            1, pixel * 2, pixel);
                    renderBlock(renderer, block, x, y, z, block.getIcon(0, 1));

                    //End caps top
                    bounds(renderer,
                            0, pixel * 12, pixel * 6,
                            pixel, pixel, pixel * 4);
                    renderBlock(renderer, block, x, y, z, block.getIcon(0, 1));

                    bounds(renderer,
                            pixel * 15, pixel * 12, pixel * 6,
                            pixel, pixel, pixel * 4);
                    renderBlock(renderer, block, x, y, z, block.getIcon(0, 1));

                    //End caps sides
                    bounds(renderer,
                            0, pixel * 6, pixel * 4,
                            pixel, pixel * 4, pixel);
                    renderBlock(renderer, block, x, y, z, block.getIcon(0, 1));

                    bounds(renderer,
                            0, pixel * 6, 1 - pixel * 5,
                            pixel, pixel * 4, pixel);
                    renderBlock(renderer, block, x, y, z, block.getIcon(0, 1));

                    bounds(renderer,
                            1 - pixel, pixel * 6, pixel * 4,
                            pixel, pixel * 4, pixel);
                    renderBlock(renderer, block, x, y, z, block.getIcon(0, 1));

                    bounds(renderer,
                            1 - pixel, pixel * 6, 1 - pixel * 5,
                            pixel, pixel * 4, pixel);
                    renderBlock(renderer, block, x, y, z, block.getIcon(0, 1));
                }

                return true;
            }
            else if (renderTop)
            {
                IIcon icon = block.getIcon(0, 15);
                //Glass Top
                bounds(renderer,
                        pixel, pixel * 12, pixel * 6,
                        1 - pixel * 2, pixel, pixel * 4);
                renderBlock(renderer, block, x, y, z, icon);

                //Glass walls
                bounds(renderer,
                        pixel, pixel * 6, pixel * 4,
                        pixel * 14, pixel * 4, pixel);
                renderBlock(renderer, block, x, y, z, icon);

                bounds(renderer,
                        pixel, pixel * 6, 1 - pixel * 5,
                        pixel * 14, pixel * 4, pixel);
                renderBlock(renderer, block, x, y, z, icon);

                return true;
            }
        }
        else
        {
            if (pass == 0)
            {
                //Base
                bounds(renderer,
                        pixel * 5, 0, 0,
                        pixel * 6, pixel * 5, 1);
                renderBlock(renderer, block, x, y, z, null);

                //Lower bars
                bounds(renderer,
                        pixel * 4, pixel * 4, 0,
                        pixel, pixel * 2, 1);
                renderBlock(renderer, block, x, y, z, block.getIcon(0, 1));

                bounds(renderer,
                        1 - pixel * 5, pixel * 4, 0,
                        pixel, pixel * 2, 1);
                renderBlock(renderer, block, x, y, z, block.getIcon(0, 1));

                if (renderTop)
                {
                    //Upper bars
                    bounds(renderer,
                            pixel * 4, pixel * 10, 0,
                            pixel, pixel * 2, 1);
                    renderBlock(renderer, block, x, y, z, block.getIcon(0, 1));

                    bounds(renderer,
                            1 - pixel * 5, pixel * 10, 0,
                            pixel, pixel * 2, 1);
                    renderBlock(renderer, block, x, y, z, block.getIcon(0, 1));

                    //Top bars
                    bounds(renderer,
                            pixel * 5, pixel * 11, 0,
                            pixel, pixel * 2, 1);
                    renderBlock(renderer, block, x, y, z, block.getIcon(0, 1));

                    bounds(renderer,
                            1 - pixel * 6, pixel * 11, 0,
                            pixel, pixel * 2, 1);
                    renderBlock(renderer, block, x, y, z, block.getIcon(0, 1));

                    //End caps top
                    bounds(renderer,
                            pixel * 6, pixel * 12, 0,
                            pixel * 4, pixel, pixel);
                    renderBlock(renderer, block, x, y, z, block.getIcon(0, 1));

                    bounds(renderer,
                            pixel * 6, pixel * 12, pixel * 15,
                            pixel * 4, pixel, pixel);
                    renderBlock(renderer, block, x, y, z, block.getIcon(0, 1));

                    //End caps sides
                    bounds(renderer,
                            pixel * 4, pixel * 6, 0,
                            pixel, pixel * 4, pixel);
                    renderBlock(renderer, block, x, y, z, block.getIcon(0, 1));

                    bounds(renderer,
                            1 - pixel * 5, pixel * 6, 0,
                            pixel, pixel * 4, pixel);
                    renderBlock(renderer, block, x, y, z, block.getIcon(0, 1));

                    bounds(renderer,
                            pixel * 4, pixel * 6, 1 - pixel,
                            pixel, pixel * 4, pixel);
                    renderBlock(renderer, block, x, y, z, block.getIcon(0, 1));

                    bounds(renderer,
                            1 - pixel * 5, pixel * 6, 1 - pixel,
                            pixel, pixel * 4, pixel);
                    renderBlock(renderer, block, x, y, z, block.getIcon(0, 1));
                }

                return true;
            }
            else if (renderTop)
            {
                IIcon icon = block.getIcon(0, 15);
                //Glass Top
                bounds(renderer,
                        pixel * 6, pixel * 12, pixel,
                        pixel * 4, pixel, 1 - pixel * 2);
                renderBlock(renderer, block, x, y, z, icon);

                //Glass walls
                bounds(renderer,
                        pixel * 4, pixel * 6, pixel,
                        pixel, pixel * 4, pixel * 14);
                renderBlock(renderer, block, x, y, z, icon);

                bounds(renderer,
                        1 - pixel * 5, pixel * 6, pixel,
                        pixel, pixel * 4, pixel * 14);
                renderBlock(renderer, block, x, y, z, icon);

                return true;
            }
        }
        return false;
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
