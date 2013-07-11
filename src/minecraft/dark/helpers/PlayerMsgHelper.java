package dark.helpers;

import java.util.Iterator;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import universalelectricity.core.vector.Vector3;

public class PlayerMsgHelper
{
	public static void msgAllPlayersInRange(World world, Vector3 loc, double range, String sender, String... msgs)
	{
		if (world != null && loc != null && msgs != null)
		{
			String sendMsg = sender + ">>>";
			for (int i = 0; i < msgs.length; i++)
			{
				sendMsg += msgs[i] + ">>>";
			}
			sendMsg = sendMsg.substring(0, sendMsg.length() > 255 ? 255 : sendMsg.length());
			List<EntityPlayer> list = getPlayersInRange(world, loc, range, false);

			for (EntityPlayer player : list)
			{
				//player..sendChatToPlayer(sendMsg);
			}
		}
	}

	public static List<EntityPlayer> getPlayersInRange(World world, Vector3 loc, double range, boolean canSee)
	{
		AxisAlignedBB bound = AxisAlignedBB.getBoundingBox(loc.x - range, loc.y - range, loc.z - range, loc.x + range, loc.y + range, loc.z + range);
		List<EntityPlayer> list = world.getEntitiesWithinAABB(EntityPlayer.class, bound);
		Iterator<EntityPlayer> it = list.iterator();
		while (it.hasNext() && canSee)
		{
			EntityPlayer player = it.next();
			if (world.rayTraceBlocks_do_do(loc.toVec3(), new Vector3(player).add(new Vector3(0, player.getEyeHeight(), 0)).toVec3(), canSee, canSee) != null)
			{
				it.remove();
			}
		}
		return list;
	}
}
