package com.builtbroken.assemblyline.content.belt.pipe.listener;

import com.builtbroken.assemblyline.AssemblyLine;
import com.builtbroken.assemblyline.content.belt.TilePipeBelt;
import com.builtbroken.mc.api.tile.node.ITileNode;
import com.builtbroken.mc.framework.block.imp.*;
import com.builtbroken.mc.seven.framework.block.listeners.TileListener;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 7/10/2018.
 */
public class TilePipeStackListener extends TileListener implements IBlockListener, IBlockStackListener, IDestroyedListener
{
    @Override
    public ItemStack toStack()
    {
        ITileNode node = getNode();
        if (node instanceof TilePipeBelt)
        {
            ItemStack stack = new ItemStack(AssemblyLine.pipeBelt);
            stack.setItemDamage(((TilePipeBelt) node).type.ordinal());
            return stack;
        }
        return null;
    }

    @Override
    public List<String> getListenerKeys()
    {
        List<String> list = new ArrayList();
        list.add(BlockListenerKeys.BLOCK_STACK);
        list.add(BlockListenerKeys.BREAK);
        return list;
    }

    @Override
    public boolean removedByPlayer(EntityPlayer player, boolean willHarvest)
    {
        //Need block to remain
        return true;
    }

    public static class Builder implements ITileEventListenerBuilder
    {
        @Override
        public ITileEventListener createListener(Block block)
        {
            return new TilePipeStackListener();
        }

        @Override
        public String getListenerKey()
        {
            return "beltPipeStack";
        }
    }
}
