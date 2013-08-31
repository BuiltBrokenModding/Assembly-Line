package dark.farmtech.machines.farmer;

import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import universalelectricity.core.vector.Vector3;
import dark.api.farm.ICropHandler;

public class CropHandler implements ICropHandler
{
    ItemStack blockStack;

    public CropHandler(ItemStack blockStack)
    {
        this.blockStack = blockStack;
    }

    @Override
    public void onCareUpdate(EntityFarmDrone drone, World world, Vector3 pos)
    {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean preHarvest(EntityFarmDrone drone, World world, Vector3 pos)
    {
        return true;
    }

    @Override
    public boolean onHarvest(EntityFarmDrone drone, World world, Vector3 pos)
    {
        // TODO Auto-generated method stub
        return true;
    }

    @Override
    public void postHarvest(EntityFarmDrone drone, World world, Vector3 pos)
    {
        // TODO Grab items and place into drop instead of letting fall to the ground

    }

}
