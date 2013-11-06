package dark.api;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;

public interface IHelpFrame<E>
{
    public String getTitle(EntityPlayer player, E item);

    public void getDisplayList(EntityPlayer player, E item, List<String> list);
}
