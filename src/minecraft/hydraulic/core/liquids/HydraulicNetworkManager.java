package hydraulic.core.liquids;

import hydraulic.core.implement.IFluidPipe;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.ForgeDirection;
import universalelectricity.core.vector.Vector3;
import cpw.mods.fml.common.FMLLog;

/**
 * based on Calclavia's UE Electric Network stuff
 * 
 */
public class HydraulicNetworkManager
{
    public static HydraulicNetworkManager instance = new HydraulicNetworkManager();

    private List<HydraulicNetwork> hydraulicNetworks = new ArrayList<HydraulicNetwork>();

    /**
     * Registers a conductor into the UE electricity net.
     */
    public void registerConductor(IFluidPipe newConductor)
    {
        this.cleanUpNetworks();
        HydraulicNetwork newNetwork = new HydraulicNetwork(newConductor);
        this.hydraulicNetworks.add(newNetwork);
    }

    public void unregister(TileEntity tileEntity)
    {
        for (HydraulicNetwork network : this.hydraulicNetworks)
        {
            network.removeEntity(tileEntity);
        }
    }

    /**
     * Merges two connection lines together into one.
     * 
     * @param networkA
     *            - The network to be merged into. This network will be kept.
     * @param networkB
     *            - The network to be merged. This network will be deleted.
     */
    public void mergeConnection(HydraulicNetwork networkA, HydraulicNetwork networkB)
    {
        if (networkA != networkB)
        {
            if (networkA != null && networkB != null)
            {
                networkA.conductors.addAll(networkB.conductors);
                networkA.setNetwork();
                this.hydraulicNetworks.remove(networkB);
                networkB = null;

                networkA.cleanConductors();
            }
            else
            {
                System.err.println("Failed to merge pipe connections!");
            }
        }
    }

    /**
     * Separate one connection line into two different ones between two
     * conductors. This function does this by resetting all wires in the
     * connection line and making them each reconnect.
     * 
     * @param conductorA
     *            - existing conductor
     * @param conductorB
     *            - broken/invalid conductor
     */
    public void splitConnection(IFluidPipe conductorA, IFluidPipe conductorB)
    {
        try
        {
            HydraulicNetwork network = conductorA.getNetwork();

            if (network != null)
            {
                network.cleanConductors();
                network.resetConductors();

                Iterator it = network.conductors.iterator();

                while (it.hasNext())
                {
                    IFluidPipe conductor = (IFluidPipe) it.next();

                    for (byte i = 0; i < 6; i++)
                    {
                        conductor.updateConnectionWithoutSplit(Vector3.getConnectorFromSide(((TileEntity) conductor).worldObj, new Vector3((TileEntity) conductor), ForgeDirection.getOrientation(i)), ForgeDirection.getOrientation(i));
                    }
                }
            }
            else
            {
                FMLLog.severe("Conductor invalid network while splitting connection!");
            }
        }
        catch (Exception e)
        {
            FMLLog.severe("Failed to split wire connection!");
            e.printStackTrace();
        }
    }

    /**
     * Clean up and remove all useless and invalid connections.
     */
    public void cleanUpNetworks()
    {
        try
        {
            Iterator it = hydraulicNetworks.iterator();

            while (it.hasNext())
            {
                HydraulicNetwork network = (HydraulicNetwork) it.next();
                network.cleanConductors();

                if (network.conductors.size() == 0)
                {
                    it.remove();
                }
            }
        }
        catch (Exception e)
        {
            FMLLog.severe("Failed to clean up wire connections!");
            e.printStackTrace();
        }
    }

    public void resetConductors()
    {
        Iterator it = hydraulicNetworks.iterator();

        while (it.hasNext())
        {
            HydraulicNetwork network = ((HydraulicNetwork) it.next());
            network.resetConductors();
        }
    }
}
