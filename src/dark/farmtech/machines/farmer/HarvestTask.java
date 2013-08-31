package dark.farmtech.machines.farmer;

import java.util.ArrayList;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import universalelectricity.core.vector.Vector3;

public class HarvestTask extends DroneTask
{
    int harvestTick = 0;
    int harvestTime = 50;

    public HarvestTask(EntityFarmDrone drone, Vector3 location)
    {
        super(drone, location);
    }

    @Override
    public boolean canDoTask()
    {
        if (this.drone.location.difference(this.location).getMagnitudeSquared() <= 0)
        {
            if (this.harvestTick++ >= this.harvestTime)
            {
                return true;
            }
        }
        else
        {
            //TODO tell drone to move to target
            this.harvestTick = 0;
        }
        return false;
    }

    @Override
    public void doTask()
    {
        if (this.drone.location.difference(this.location).getMagnitudeSquared() <= 0)
        {
            int blockID = location.getBlockID(drone.worldObj);
            int metaData = location.getBlockMetadata(drone.worldObj);
            Block block = Block.blocksList[blockID];
            if (block != null)
            {
                ArrayList<ItemStack> items = block.getBlockDropped(drone.worldObj, location.intX(), location.intY(), location.intZ(), metaData, 1);
                for (ItemStack stack : items)
                {
                    drone.pickUpItem(stack);
                }
            }
            //TODO do a few checks and method calls to simulate player like block harvesting as much as possible
            location.setBlock(drone.worldObj, 0);
        }
    }
}
