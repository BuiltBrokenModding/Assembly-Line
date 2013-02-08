package dark.minecraft.helpers;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class DebugToPlayer
{
	/**
	 * Send a debug message to the player using world xyz
	 * 
	 * @param r - range
	 * @param msg - display message under 200 chars
	 */
	public static void SendToClosest(World world, int x, int y, int z, int r, String msg)
	{
		EntityPlayer player = world.getClosestPlayer(x, y, z, r);
		if (player != null)
		{
			msg = trimForDisplay(msg);
			player.sendChatToPlayer("Debug: " + msg);
		}
	}

	/**
	 * Sends a debug message to the player using the tileEntity as the center
	 * 
	 * @param r - range
	 * @param msg - display message under 200 chars
	 */
	public static void SendToClosest(TileEntity ent, int r, String msg)
	{
		if (ent != null)
		{
			DebugToPlayer.SendToClosest(ent.worldObj, ent.xCoord, ent.yCoord, ent.zCoord, r, msg);
		}
	}

	/**
	 * cleans up the display text and adds the [Debug] prefix too the text
	 * 
	 * @param msg - display string under 200 chars
	 * @return
	 */
	public static String trimForDisplay(String msg)
	{
		// TODO trim the length to under 255 to prevent crashing
		msg = msg.trim();
		msg = "[Debug] " + msg;
		return msg;
	}
}
