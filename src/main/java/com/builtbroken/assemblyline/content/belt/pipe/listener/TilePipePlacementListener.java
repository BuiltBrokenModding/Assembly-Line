package com.builtbroken.assemblyline.content.belt.pipe.listener;

import com.builtbroken.assemblyline.content.belt.TilePipeBelt;
import com.builtbroken.assemblyline.content.belt.pipe.BeltType;
import com.builtbroken.mc.api.tile.node.ITileNode;
import com.builtbroken.mc.framework.block.imp.IBlockListener;
import com.builtbroken.mc.framework.block.imp.IPlacementListener;
import com.builtbroken.mc.framework.block.imp.ITileEventListener;
import com.builtbroken.mc.framework.block.imp.ITileEventListenerBuilder;
import com.builtbroken.mc.seven.framework.block.listeners.TileListener;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

/**
 * Handles placement
 *
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 11/18/2017.
 */
public class TilePipePlacementListener extends TileListener implements IBlockListener, IPlacementListener
{
    @Override
    public void onPlacedBy(EntityLivingBase entityLivingBase, ItemStack stack)
    {
        ITileNode node = getNode();
        if (node instanceof TilePipeBelt)
        {
            ((TilePipeBelt) node).type = BeltType.get(stack.getItemDamage());
        }
    }

    @Override
    public List<String> getListenerKeys()
    {
        List<String> list = new ArrayList();
        list.add("placement");
        return list;
    }

    public static class Builder implements ITileEventListenerBuilder
    {
        @Override
        public ITileEventListener createListener(Block block)
        {
            return new TilePipePlacementListener();
        }

        @Override
        public String getListenerKey()
        {
            return "beltPipePlacement";
        }
    }
}
