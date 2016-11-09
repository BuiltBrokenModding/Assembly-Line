package com.builtbroken.assemblyline.content.inserter;

import com.builtbroken.mc.prefab.tile.Tile;
import cpw.mods.fml.common.network.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import net.minecraft.item.ItemStack;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 11/9/2016.
 */
public class TileInsertArmClient extends TileInsertArm
{
    private ItemStack renderStack;

    @Override
    public Tile newTile()
    {
        return new TileInsertArmClient();
    }

    @Override
    public void readDescPacket(ByteBuf buf)
    {
        super.readDescPacket(buf);
        rotation.readByteBuf(buf);
        if (buf.readBoolean())
        {
            renderStack = ByteBufUtils.readItemStack(buf);
        }
        else
        {
            renderStack = null;
        }
    }
}
