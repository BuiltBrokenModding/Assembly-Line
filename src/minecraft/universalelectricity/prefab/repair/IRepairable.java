package universalelectricity.prefab.repair;

import net.minecraft.entity.player.EntityPlayer;

/**
 * Applied to TileEntities/Machines that can be repaired.
 * 
 * @author Calclavia
 * 
 */
public interface IRepairable
{
	/**
	 * Called when the machine is being repaired.
	 * 
	 * @param itemStack - The repairing tool that the player is holding
	 * @param player - The player who is repairing this machine.
	 */
	public void onRepair(IToolRepair itemStack, EntityPlayer player);

	/**
	 * @return The maximum possible damage of this machine.
	 */
	public int getMaxDamage();

	/**
	 * @return How damaged is this machine?
	 */
	public int getDamage();

}
