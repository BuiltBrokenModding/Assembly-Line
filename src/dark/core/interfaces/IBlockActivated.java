package dark.core.interfaces;

import net.minecraft.entity.player.EntityPlayer;

/** Useful for several diffrent things. Main use is to be called when a block or entity to trigger an
 * event inside the tileEntity */
public interface IBlockActivated
{
    /** Called when activated. Use angle from tile to get side. and getHeldItem to get the item the
     * player is using */
    public boolean onActivated(EntityPlayer entityPlayer);
}