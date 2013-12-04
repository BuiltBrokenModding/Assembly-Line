package dark.core.prefab.entities;

import net.minecraft.world.World;
import dark.machines.common.CoreRecipeLoader;

public class EntityTestCar extends EntityVehicle
{

    public EntityTestCar(World world)
    {
        super(world);
    }

    public EntityTestCar(World world, float xx, float yy, float zz)
    {
        super(world, xx, yy, zz);
    }

    @Override
    public void updateClients()
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void dropAsItem()
    {
        this.dropItemWithOffset(CoreRecipeLoader.itemVehicleTest.itemID, 1, 0.0F);
    }

}
