package dark.farmtech.machines.farmer;

import universalelectricity.core.vector.Vector3;
import dark.core.prefab.machine.TileEntityEnergyMachine;
import dark.farmtech.machines.farmer.EntityFarmDrone.DroneData;

public class TileEntityFarmBox extends TileEntityEnergyMachine
{
    /** Current amount of drone slots this box has */
    private int droneSlots = 1;
    /** Stores drone data while the drone is stored in the block */
    private DroneData[] droneData = new DroneData[4];
    /** Stores drone instances as drones are active outside the block */
    private EntityFarmDrone[] drones = new EntityFarmDrone[4];

    public TileEntityFarmBox()
    {
        this.MAX_WATTS = 100;
        this.WATTS_PER_TICK = 5;
    }

    @Override
    public void updateEntity()
    {
        super.updateEntity();

        //TODO generate field map
        //Calculate crop status
        //Set workers into motion
    }

    public void spawnDrone(int droneID)
    {
        if (droneID < droneData.length && droneID < drones.length)
        {
            if (drones[droneID] == null && droneData[droneID] != null)
            {
                EntityFarmDrone drone = new EntityFarmDrone(this.worldObj, this);

            }
        }
    }

    public Vector3 getClearSpot()
    {
        Vector3 loc = new Vector3(this);
        return loc;
    }

    public void setTask(int droneID, DroneTask task)
    {

    }
}
