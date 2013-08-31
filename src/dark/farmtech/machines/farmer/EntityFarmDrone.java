package dark.farmtech.machines.farmer;

import net.minecraft.entity.EntityLiving;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import universalelectricity.core.block.IElectricalStorage;

public class EntityFarmDrone extends EntityLiving implements IElectricalStorage
{
    /** Battery energy level */
    private float energy = 0;
    /** Running cost of drone */
    private float wattPerTick = 0.1f;
    /** current inv slots of the drone */
    private int slots = 1;
    /** Drone inv */
    public ItemStack[] inv = new ItemStack[3];

    TileEntityFarmBox home;

    public EntityFarmDrone(World par1World)
    {
        super(par1World);
        this.energy = 10;
    }

    public EntityFarmDrone(World world, TileEntityFarmBox tileController)
    {
        this(world);
        this.home = tileController;
    }

    /** Get the amount of time it takes the drone to get home. Used by AI logic to stop work and
     * return home for recharge */
    public int getTimeToHome()
    {
        return 1;
    }

    public static class DroneData
    {
        public float health = 0;
        public float energy = 0;
        public ItemStack[] inv = new ItemStack[3];

        /** saves drone data to nbt */
        public NBTTagCompound saveData(NBTTagCompound tag)
        {
            return tag;
        }

        public DroneData getDroneData(EntityFarmDrone drone)
        {
            this.health = drone.func_110143_aJ();
            return this;
        }

        /** Loads drone data from nbt */
        public DroneData loadData(NBTTagCompound tag)
        {
            return this;
        }

        /** Creates a new DroneData instances and load data from nbt into it */
        public static DroneData load(NBTTagCompound tag)
        {
            return new DroneData().loadData(tag);
        }
    }

    @Override
    public void setEnergyStored(float energy)
    {
        this.energy = energy;

    }

    @Override
    public float getEnergyStored()
    {
        return this.energy;
    }

    @Override
    public float getMaxEnergyStored()
    {
        return 50;
    }
}
