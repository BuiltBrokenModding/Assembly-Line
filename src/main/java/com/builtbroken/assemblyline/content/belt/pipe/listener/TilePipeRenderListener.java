package com.builtbroken.assemblyline.content.belt.pipe.listener;

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
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
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
    private final RenderItem renderItem = new RenderItem()
    {
        @Override
        public boolean shouldBob()
        {
            return false;
        }
    };
    private final EntityItem entityItem = new EntityItem(null);

    @Override
    public void renderDynamic(TileEntity tile, double xx, double yy, double zz, float f)
    {
        //Update fake item for rendering
        entityItem.worldObj = tile.getWorldObj();
        entityItem.posX = tile.xCoord + 0.5;
        entityItem.posY = tile.yCoord + 0.5;
        entityItem.posZ = tile.zCoord + 0.5;

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
                renderItemAtPosition(0.5, 0.5, 0.5, belt.renderInventory.getStackInSlot(2));

                //TODO optimize to use an object set to detail slots to render positions
                if (belt.type == BeltType.NORMAL)
                {
                    if (belt.getDirection() == ForgeDirection.WEST)
                    {
                        renderItemAtPosition(0.2, 0.5, 0.5, belt.renderInventory.getStackInSlot(0));
                        renderItemAtPosition(0.8, 0.5, 0.5, belt.renderInventory.getStackInSlot(1));
                    }
                    else if (belt.getDirection() == ForgeDirection.EAST)
                    {
                        renderItemAtPosition(0.2, 0.5, 0.5, belt.renderInventory.getStackInSlot(1));
                        renderItemAtPosition(0.8, 0.5, 0.5, belt.renderInventory.getStackInSlot(0));
                    }
                    else if (belt.getDirection() == ForgeDirection.NORTH)
                    {
                        renderItemAtPosition(0.5, 0.5, 0.2, belt.renderInventory.getStackInSlot(0));
                        renderItemAtPosition(0.5, 0.5, 0.8, belt.renderInventory.getStackInSlot(1));
                    }
                    else if (belt.getDirection() == ForgeDirection.SOUTH)
                    {
                        renderItemAtPosition(0.5, 0.5, 0.2, belt.renderInventory.getStackInSlot(1));
                        renderItemAtPosition(0.5, 0.5, 0.8, belt.renderInventory.getStackInSlot(0));
                    }
                }
                else if (belt.type == BeltType.LEFT_ELBOW)
                {
                    if (belt.getDirection() == ForgeDirection.WEST)
                    {
                        renderItemAtPosition(0.5, 0.5, 0.2, belt.renderInventory.getStackInSlot(1));
                        renderItemAtPosition(0.8, 0.5, 0.5, belt.renderInventory.getStackInSlot(0));
                    }
                    else if (belt.getDirection() == ForgeDirection.EAST)
                    {
                        renderItemAtPosition(0.2, 0.5, 0.5, belt.renderInventory.getStackInSlot(0));
                        renderItemAtPosition(0.5, 0.5, 0.8, belt.renderInventory.getStackInSlot(1));
                    }
                    else if (belt.getDirection() == ForgeDirection.NORTH)
                    {
                        renderItemAtPosition(0.8, 0.5, 0.5, belt.renderInventory.getStackInSlot(1));
                        renderItemAtPosition(0.5, 0.5, 0.8, belt.renderInventory.getStackInSlot(0));
                    }
                    else if (belt.getDirection() == ForgeDirection.SOUTH)
                    {
                        renderItemAtPosition(0.5, 0.5, 0.2, belt.renderInventory.getStackInSlot(0));
                        renderItemAtPosition(0.2, 0.5, 0.5, belt.renderInventory.getStackInSlot(1));
                    }
                }
                else if (belt.type == BeltType.RIGHT_ELBOW)
                {
                    if (belt.getDirection() == ForgeDirection.WEST)
                    {
                        renderItemAtPosition(0.5, 0.5, 0.8, belt.renderInventory.getStackInSlot(1));
                        renderItemAtPosition(0.8, 0.5, 0.5, belt.renderInventory.getStackInSlot(0));
                    }
                    else if (belt.getDirection() == ForgeDirection.EAST)
                    {
                        renderItemAtPosition(0.2, 0.5, 0.5, belt.renderInventory.getStackInSlot(0));
                        renderItemAtPosition(0.5, 0.5, 0.2, belt.renderInventory.getStackInSlot(1));
                    }
                    else if (belt.getDirection() == ForgeDirection.NORTH)
                    {
                        renderItemAtPosition(0.2, 0.5, 0.5, belt.renderInventory.getStackInSlot(1));
                        renderItemAtPosition(0.5, 0.5, 0.8, belt.renderInventory.getStackInSlot(0));
                    }
                    else if (belt.getDirection() == ForgeDirection.SOUTH)
                    {
                        renderItemAtPosition(0.5, 0.5, 0.2, belt.renderInventory.getStackInSlot(0));
                        renderItemAtPosition(0.8, 0.5, 0.5, belt.renderInventory.getStackInSlot(1));
                    }
                }

                GL11.glPopMatrix();
            }
        }
    }

    protected void renderItemAtPosition(double xx, double yy, double zz, ItemStack item)
    {
        if (item != null)
        {
            //Start
            GL11.glPushMatrix();

            //Translate
            GL11.glTranslated(xx, yy, zz);
            GL11.glScalef(0.8f, 0.8f, 0.8f);

            try
            {
                //Fix missing render manager
                renderItem.setRenderManager(RenderManager.instance);

                entityItem.setEntityItemStack(item);
                entityItem.hoverStart = 0.0F;

                //Adjustment data per item
                double xAdjust = 0;
                double zAdjust = 0;

                //Render item using RenderItem class to save time
                renderItem.doRender(entityItem, xAdjust, 0, zAdjust, 0, 0);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }

            //End
            GL11.glPopMatrix();
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
