package com.builtbroken.assemblyline.content.belt.pipe.listener;

import com.builtbroken.assemblyline.client.FakeItemRender;
import com.builtbroken.assemblyline.content.belt.TilePipeBelt;
import com.builtbroken.assemblyline.content.belt.pipe.BeltType;
import com.builtbroken.mc.api.tile.node.ITileNode;
import com.builtbroken.mc.api.tile.node.ITileNodeHost;
import com.builtbroken.mc.framework.block.imp.IBlockListener;
import com.builtbroken.mc.framework.block.imp.ITileEventListener;
import com.builtbroken.mc.framework.block.imp.ITileEventListenerBuilder;
import com.builtbroken.mc.seven.framework.block.listeners.TileListener;
import com.builtbroken.mc.seven.framework.block.listeners.client.ITileRenderListener;
import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;

/**
 * Handles rendering items on the belt
 *
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 11/16/2017.
 */
public class TilePipeRenderListener extends TileListener implements IBlockListener, ITileRenderListener
{


    @Override
    public void renderDynamic(TileEntity tile, double xx, double yy, double zz, float f)
    {
        final double spaceFromEdge = 5.4 / 32f;
        final double n = spaceFromEdge;
        final double n2 = 1 - spaceFromEdge;
        //Update fake item for rendering
        FakeItemRender.setWorldPosition(tile.getWorldObj(), tile.xCoord + 0.5, tile.yCoord + 0.5, tile.zCoord + 0.5);

        //Get node
        ITileNode node = tile instanceof ITileNodeHost ? ((ITileNodeHost) tile).getTileNode() : null;

        //Only render if pipe
        if (node instanceof TilePipeBelt)
        {
            TilePipeBelt belt = (TilePipeBelt) node;
            if (belt.renderInventory != null)
            {
                GL11.glPushMatrix();
                GL11.glTranslated(xx, yy, zz);

                //Always render center as slot 2 unless center is a buffer chest
                FakeItemRender.renderItemAtPosition(0.5, 0.5, 0.5, belt.renderInventory.getStackInSlot(2));

                //TODO optimize to use an object set to detail slots to render positions
                if (belt.type == BeltType.NORMAL)
                {
                    if (belt.getDirection() == ForgeDirection.WEST)
                    {
                        FakeItemRender.renderItemAtPosition(n, 0.5, 0.5, belt.renderInventory.getStackInSlot(0));
                        FakeItemRender.renderItemAtPosition(n2, 0.5, 0.5, belt.renderInventory.getStackInSlot(1));
                    }
                    else if (belt.getDirection() == ForgeDirection.EAST)
                    {
                        FakeItemRender.renderItemAtPosition(n, 0.5, 0.5, belt.renderInventory.getStackInSlot(1));
                        FakeItemRender.renderItemAtPosition(n2, 0.5, 0.5, belt.renderInventory.getStackInSlot(0));
                    }
                    else if (belt.getDirection() == ForgeDirection.NORTH)
                    {
                        FakeItemRender.renderItemAtPosition(0.5, 0.5, n, belt.renderInventory.getStackInSlot(0));
                        FakeItemRender.renderItemAtPosition(0.5, 0.5, n2, belt.renderInventory.getStackInSlot(1));
                    }
                    else if (belt.getDirection() == ForgeDirection.SOUTH)
                    {
                        FakeItemRender.renderItemAtPosition(0.5, 0.5, n, belt.renderInventory.getStackInSlot(1));
                        FakeItemRender.renderItemAtPosition(0.5, 0.5, n2, belt.renderInventory.getStackInSlot(0));
                    }
                }
                else if (belt.type == BeltType.LEFT_ELBOW)
                {
                    if (belt.getDirection() == ForgeDirection.WEST)
                    {
                        FakeItemRender.renderItemAtPosition(0.5, 0.5, n, belt.renderInventory.getStackInSlot(1));
                        FakeItemRender.renderItemAtPosition(n2, 0.5, 0.5, belt.renderInventory.getStackInSlot(0));
                    }
                    else if (belt.getDirection() == ForgeDirection.EAST)
                    {
                        FakeItemRender.renderItemAtPosition(n, 0.5, 0.5, belt.renderInventory.getStackInSlot(0));
                        FakeItemRender.renderItemAtPosition(0.5, 0.5, n2, belt.renderInventory.getStackInSlot(1));
                    }
                    else if (belt.getDirection() == ForgeDirection.NORTH)
                    {
                        FakeItemRender.renderItemAtPosition(n2, 0.5, 0.5, belt.renderInventory.getStackInSlot(1));
                        FakeItemRender.renderItemAtPosition(0.5, 0.5, n2, belt.renderInventory.getStackInSlot(0));
                    }
                    else if (belt.getDirection() == ForgeDirection.SOUTH)
                    {
                        FakeItemRender.renderItemAtPosition(0.5, 0.5, n, belt.renderInventory.getStackInSlot(0));
                        FakeItemRender.renderItemAtPosition(n, 0.5, 0.5, belt.renderInventory.getStackInSlot(1));
                    }
                }
                else if (belt.type == BeltType.RIGHT_ELBOW)
                {
                    if (belt.getDirection() == ForgeDirection.WEST)
                    {
                        FakeItemRender.renderItemAtPosition(0.5, 0.5, n2, belt.renderInventory.getStackInSlot(1));
                        FakeItemRender.renderItemAtPosition(n2, 0.5, 0.5, belt.renderInventory.getStackInSlot(0));
                    }
                    else if (belt.getDirection() == ForgeDirection.EAST)
                    {
                        FakeItemRender.renderItemAtPosition(n, 0.5, 0.5, belt.renderInventory.getStackInSlot(0));
                        FakeItemRender.renderItemAtPosition(0.5, 0.5, n, belt.renderInventory.getStackInSlot(1));
                    }
                    else if (belt.getDirection() == ForgeDirection.NORTH)
                    {
                        FakeItemRender.renderItemAtPosition(n, 0.5, 0.5, belt.renderInventory.getStackInSlot(1));
                        FakeItemRender.renderItemAtPosition(0.5, 0.5, n2, belt.renderInventory.getStackInSlot(0));
                    }
                    else if (belt.getDirection() == ForgeDirection.SOUTH)
                    {
                        FakeItemRender.renderItemAtPosition(0.5, 0.5, n, belt.renderInventory.getStackInSlot(0));
                        FakeItemRender.renderItemAtPosition(n2, 0.5, 0.5, belt.renderInventory.getStackInSlot(1));
                    }
                }
                else if (belt.type == BeltType.JUNCTION || belt.type == BeltType.INTERSECTION)
                {
                    if (belt.getDirection() == ForgeDirection.WEST)
                    {
                        FakeItemRender.renderItemAtPosition(0.5, 0.5, n2, belt.renderInventory.getStackInSlot(1)); //left
                        FakeItemRender.renderItemAtPosition(n2, 0.5, 0.5, belt.renderInventory.getStackInSlot(0)); //output
                        FakeItemRender.renderItemAtPosition(0.5, 0.5, n, belt.renderInventory.getStackInSlot(3)); //right
                        if (belt.type == BeltType.INTERSECTION)
                        {
                            FakeItemRender.renderItemAtPosition(n, 0.5, 0.5, belt.renderInventory.getStackInSlot(4)); //input
                        }
                    }
                    else if (belt.getDirection() == ForgeDirection.EAST)
                    {
                        FakeItemRender.renderItemAtPosition(0.5, 0.5, n2, belt.renderInventory.getStackInSlot(3)); //right
                        FakeItemRender.renderItemAtPosition(n, 0.5, 0.5, belt.renderInventory.getStackInSlot(0)); //output
                        FakeItemRender.renderItemAtPosition(0.5, 0.5, n, belt.renderInventory.getStackInSlot(1)); //left
                        if (belt.type == BeltType.INTERSECTION)
                        {
                            FakeItemRender.renderItemAtPosition(n2, 0.5, 0.5, belt.renderInventory.getStackInSlot(4)); //input
                        }
                    }
                    else if (belt.getDirection() == ForgeDirection.NORTH)
                    {
                        FakeItemRender.renderItemAtPosition(n, 0.5, 0.5, belt.renderInventory.getStackInSlot(1)); //left
                        FakeItemRender.renderItemAtPosition(0.5, 0.5, n2, belt.renderInventory.getStackInSlot(0)); //output
                        FakeItemRender.renderItemAtPosition(n2, 0.5, 0.5, belt.renderInventory.getStackInSlot(3)); //right
                        if (belt.type == BeltType.INTERSECTION)
                        {
                            FakeItemRender.renderItemAtPosition(0.5, 0.5, n, belt.renderInventory.getStackInSlot(4)); //input
                        }
                    }
                    else if (belt.getDirection() == ForgeDirection.SOUTH)
                    {
                        FakeItemRender.renderItemAtPosition(n, 0.5, 0.5, belt.renderInventory.getStackInSlot(3)); //right
                        FakeItemRender.renderItemAtPosition(0.5, 0.5, n, belt.renderInventory.getStackInSlot(0)); //output
                        FakeItemRender.renderItemAtPosition(n2, 0.5, 0.5, belt.renderInventory.getStackInSlot(1)); //left
                        if (belt.type == BeltType.INTERSECTION)
                        {
                            FakeItemRender.renderItemAtPosition(0.5, 0.5, n2, belt.renderInventory.getStackInSlot(4)); //input
                        }
                    }
                }

                GL11.glPopMatrix();
            }
        }
    }

    @Override
    public List<String> getListenerKeys()
    {
        List<String> list = new ArrayList();
        list.add("tilerender");
        return list;
    }

    public static class Builder implements ITileEventListenerBuilder
    {
        @Override
        public ITileEventListener createListener(Block block)
        {
            return new TilePipeRenderListener();
        }

        @Override
        public String getListenerKey()
        {
            return "beltPipeRender";
        }
    }
}
