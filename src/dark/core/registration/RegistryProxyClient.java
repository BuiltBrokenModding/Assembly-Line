package dark.core.registration;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import dark.core.prefab.IExtraInfo.IExtraTileEntityInfo;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemBlock;
import net.minecraft.tileentity.TileEntity;

@SideOnly(Side.CLIENT)
public class RegistryProxyClient extends RegistryProxy
{
    @Override
    public void registerBlock(Block block, Class<? extends ItemBlock> itemClass, String name, String modID)
    {
        super.registerBlock(block, itemClass, name, modID);
    }

    @Override
    public void regiserTileEntity(String name, Class<? extends TileEntity> clazz)
    {
        super.regiserTileEntity(name, clazz);
        try
        {
            TileEntity entity = clazz.newInstance();
            if (entity instanceof IExtraTileEntityInfo)
            {
                TileEntitySpecialRenderer render = ((IExtraTileEntityInfo) entity).getClientTileEntityRenderer();
                if (render != null)
                {
                    ClientRegistry.bindTileEntitySpecialRenderer(clazz, render);
                }
            }
        }
        catch (InstantiationException e)
        {
        }
        catch (IllegalAccessException e)
        {
            e.printStackTrace();
        }
    }
}
