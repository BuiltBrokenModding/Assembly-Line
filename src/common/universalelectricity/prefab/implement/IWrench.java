package universalelectricity.prefab.implement;

import net.minecraft.src.EntityPlayer;

/**
 * Code written by Buildcraft.
 * 
 * @author Buildcraft Team
 */
public interface IWrench
{

	/***
	 * Called to ensure that the wrench can be used. To get the ItemStack that is used, check
	 * player.inventory.getCurrentItem()
	 * 
	 * @param player
	 *            - The player doing the wrenching
	 * @param x
	 *            ,y,z - The coordinates for the block being wrenched
	 * 
	 * @return true if wrenching is allowed, false if not
	 */
	public boolean canWrench(EntityPlayer player, int x, int y, int z);

	/***
	 * Callback after the wrench has been used. This can be used to decrease durability or for other
	 * purposes. To get the ItemStack that was used, check player.inventory.getCurrentItem()
	 * 
	 * @param player
	 *            - The player doing the wrenching
	 * @param x
	 *            ,y,z - The coordinates of the block being wrenched
	 */
	public void wrenchUsed(EntityPlayer player, int x, int y, int z);
}
