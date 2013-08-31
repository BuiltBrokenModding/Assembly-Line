package dark.farmtech.machines.farmer;

import universalelectricity.core.vector.Vector3;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class CropHandlerCactus extends CropHandler
{

    public CropHandlerCactus(ItemStack blockStack)
    {
        super(blockStack);
        // TODO Auto-generated constructor stub
    }

    @Override
    public boolean preHarvest(EntityFarmDrone drone, World world, Vector3 pos)
    {
        // TODO check if the cactus is the lowest then harvest the block above it
        return true;
    }

}
