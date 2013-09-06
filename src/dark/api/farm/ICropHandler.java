package dark.api.farm;

import dark.farmtech.machines.farmer.EntityFarmDrone;
import universalelectricity.core.vector.Vector3;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

/** Special case handling for crops so the farm automatons know to do a few extra steps to care for
 * and harvest a crop block
 * 
 * @author DarkGuardsman */
public interface ICropHandler
{
    /** Called per block each update of the farming box. Used to manage anything from calling the
     * drone to harvest, water, or to fertilise the crop */
    public void onCareUpdate(EntityFarmDrone drone, World world, Vector3 pos);

    /** Called before the drone harvests the crop
     * 
     * @return true to keep harvesting */
    public boolean preHarvest(EntityFarmDrone drone, World world, Vector3 pos);

    /** Called as the crop is being harvest but right before its actually removed from the world
     * 
     * @return true to finish harvesting */
    public boolean onHarvest(EntityFarmDrone drone, World world, Vector3 pos);

    /** Called after the crop has been removed */
    public void postHarvest(EntityFarmDrone drone, World world, Vector3 pos);
}
