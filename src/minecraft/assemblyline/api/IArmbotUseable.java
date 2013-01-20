package assemblyline.api;

import assemblyline.common.machine.armbot.TileEntityArmbot;
import net.minecraft.entity.Entity;

/**
 * The IUseable inteface is used by the ArmBot so that it may interact with Tile Entities.
 * onUse will be called on the block an ArmBot is touching whenver the USE command is run
 * on it.
 * @author Briman0094
 *
 */
public interface IArmbotUseable
{
	
	/**
	 * Called when the ArmBot command "USE" is run. This is called on any IUseable the
	 * ArmBot is touching.
	 * @param tileEntity the TileEntityArmbot that is using this IUseable
	 * @param heldEntity the Entity being held by the ArmBot, or null if there is none
	 * @return whether or not the "use" did anything
	 */
	public boolean onUse(TileEntityArmbot tileEntity, Entity heldEntity);
	
}
