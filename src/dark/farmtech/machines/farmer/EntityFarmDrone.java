package dark.farmtech.machines.farmer;

import net.minecraft.entity.EntityLiving;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import universalelectricity.core.block.IElectricalStorage;
import universalelectricity.core.vector.Vector3;

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

    @Override
    public void onEntityUpdate()
    {
        super.onEntityUpdate();
        //TODO update AI logic
        float homeCost = getTimeToHome() * this.wattPerTick;
        if (this.getEnergyStored() < (this.getMaxEnergyStored() / 4) || this.getEnergyStored() <= homeCost + 1)
        {
            //TODO stop work and return home for recharge
        }
    }

    @Override
    public void onLivingUpdate()
    {
        super.onLivingUpdate();
        //TODO update energy info
    }

    /** Get the amount of time it takes the drone to get home. Used by AI logic to stop work and
     * return home for recharge */
    public int getTimeToHome()
    {
        if (this.home != null)
        {
            //TODO calculate jump time, and rotation time
            double distance = new Vector3(this).difference(new Vector3(this.home)).getMagnitudeSquared();
            double speed = this.getMoveHelper().getSpeed();
            return (int) Math.ceil(distance / speed) + 1;
        }
        return 1;
    }

    @Override
    protected boolean canDespawn()
    {
        //TODO do calculations based on player distance to not have the drone do work or be spawned if the player is not near at all
        return false;
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

    /** Used to store data on a drone. Mainly for saving the drone when stored in a tileEntity
     *
     * @author DarkGuardsman */
    public static class DroneData
    {
        public float health = 0;
        public float energy = 0;
        public ItemStack[] inv = new ItemStack[3];

        /** saves drone data to nbt */
        public NBTTagCompound saveData(NBTTagCompound tag)
        {
            tag.setFloat("Health", health);
            tag.setFloat("Energy", energy);
            return tag;
        }

        public DroneData getDroneData(EntityFarmDrone drone)
        {
            this.health = drone.func_110143_aJ();
            this.energy = drone.energy;
            this.inv = drone.inv;
            return this;
        }

        /** Loads drone data from nbt */
        public DroneData loadData(NBTTagCompound tag)
        {
            this.health = tag.getFloat("Health");
            this.energy = tag.getFloat("Energy");
            return this;
        }

        /** Creates a new DroneData instances and load data from nbt into it */
        public static DroneData createDroneDataNBT(NBTTagCompound tag)
        {
            return new DroneData().loadData(tag);
        }

        public static DroneData createDroneData(EntityFarmDrone drone)
        {
            return new DroneData().getDroneData(drone);
        }
    }

}
