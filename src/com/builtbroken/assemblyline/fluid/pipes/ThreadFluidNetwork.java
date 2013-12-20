package com.builtbroken.assemblyline.fluid.pipes;

import java.util.ArrayList;
import java.util.List;

import com.builtbroken.assemblyline.fluid.network.NetworkFluidTiles;
import com.builtbroken.common.Pair;

public class ThreadFluidNetwork extends Thread
{
    protected static List<Pair<NetworkFluidTiles, Boolean>> storageCalcList = new ArrayList();

    //TODO create a thread to off load tile network calculation to reduce strain on the main thread. Things to include are fluid container updates, path finding, and fill target finding.
    /** Makes a request that this thread calculate the liquid storage locations in the network for
     * the entire network's volume
     * 
     * @even - each tile will get the same fluid level. If false physics will be used to calculate
     * were the fluid is */
    public static void calculateStorage(NetworkFluidTiles network, boolean even)
    {

    }

    public static void sendUpdateToClient(NetworkFluidTiles network)
    {

    }

    @Override
    public void run()
    {

    }

}
