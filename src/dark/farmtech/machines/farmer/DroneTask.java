package dark.farmtech.machines.farmer;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import universalelectricity.core.vector.Vector3;

public class DroneTask
{
    Vector3 location;
    EntityFarmDrone drone;

    public DroneTask(final EntityFarmDrone drone, final Vector3 location)
    {
        this.drone = drone;
        this.location = location;
    }

    /** Can the task be performed */
    public boolean canDoTask()
    {
        return false;
    }

    /** Does the task */
    public void doTask()
    {

    }
}
