package dark.fluid.common.pump;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;
import universalelectricity.core.vector.Vector3;
import universalelectricity.core.vector.VectorHelper;
import dark.api.fluid.IDrain;
import dark.api.fluid.INetworkPipe;
import dark.core.helpers.FluidHelper;
import dark.core.helpers.Pair;
import dark.fluid.common.prefab.TileEntityFluidDevice;

public class TileEntityDrain extends TileEntityFluidDevice implements IFluidHandler, IDrain
{
    /* MAX BLOCKS DRAINED PER 1/2 SECOND */
    public static int MAX_WORLD_EDITS_PER_PROCESS = 30;
    private int currentWorldEdits = 0;

    /* LIST OF PUMPS AND THERE REQUESTS FOR THIS DRAIN */
    private HashMap<TileEntity, Pair<FluidStack, Integer>> requestMap = new HashMap<TileEntity, Pair<FluidStack, Integer>>();

    private List<Vector3> updateQue = new ArrayList<Vector3>();
    private LiquidPathFinder pathLiquid;

    public LiquidPathFinder getLiquidFinder()
    {
        if (pathLiquid == null)
        {
            pathLiquid = new LiquidPathFinder(this.worldObj, 1000, 100);
        }
        return pathLiquid;
    }

    @Override
    public String getMeterReading(EntityPlayer user, ForgeDirection side, EnumTools tool)
    {
        return "Set to " + (canDrainSources() ? "input liquids" : "output liquids");
    }

    @Override
    public void invalidate()
    {
        super.invalidate();
    }

    public boolean canDrainSources()
    {
        int meta = 0;
        if (worldObj != null)
        {
            meta = worldObj.getBlockMetadata(xCoord, yCoord, zCoord);
        }
        return meta < 6;
    }

    public ForgeDirection getFacing()
    {
        int meta = 0;
        if (worldObj != null)
        {
            meta = worldObj.getBlockMetadata(xCoord, yCoord, zCoord) % 6;
        }
        return ForgeDirection.getOrientation(meta);
    }

    @Override
    public void updateEntity()
    {
        super.updateEntity();
        /* MAIN LOGIC PATH FOR DRAINING BODIES OF LIQUID */
        if (!this.worldObj.isRemote && this.ticks % 20 == 0)
        {
            this.currentWorldEdits = 0;
            this.doCleanup();

            if (this.canDrainSources() && this.requestMap.size() > 0)
            {
                /* ONLY FIND NEW SOURCES IF OUR CURRENT LIST RUNS DRY */
                if (this.getLiquidFinder().results.size() < TileEntityDrain.MAX_WORLD_EDITS_PER_PROCESS + 10)
                {
                    this.getLiquidFinder().start(new Vector3(this).modifyPositionFromSide(this.getFacing()), false);
                }
                for (Entry<TileEntity, Pair<FluidStack, Integer>> request : requestMap.entrySet())
                {
                    if (this.currentWorldEdits >= MAX_WORLD_EDITS_PER_PROCESS)
                    {
                        break;
                    }

                    if (request.getKey() instanceof IFluidHandler)
                    {
                        IFluidHandler tank = (IFluidHandler) request.getKey();
                        if (getLiquidFinder().results.size() > 1)
                        {
                            this.sortBlockList(new Vector3(this).modifyPositionFromSide(this.getFacing()), getLiquidFinder().results, false, true);
                        }
                        Vector3[] sortedList = this.getLiquidFinder().results.toArray(new Vector3[MAX_WORLD_EDITS_PER_PROCESS]);

                        for (int i = 0; sortedList != null && i < sortedList.length; i++)
                        {
                            Vector3 loc = sortedList[i];

                            if (this.currentWorldEdits >= MAX_WORLD_EDITS_PER_PROCESS)
                            {
                                break;
                            }
                            FluidStack stack = FluidHelper.drainBlock(this.worldObj, loc, false);
                            if (stack != null)
                            {
                                /* GET STACKS */

                                Pair<FluidStack, Integer> requestStack = request.getValue();
                                boolean flag = false;
                                if (requestStack != null && requestStack.getValue() > 0)
                                {
                                    if (requestStack.getKey() == null || requestStack.getKey() != null && stack.isFluidEqual(requestStack.getKey().copy()))
                                    {
                                        if (tank.fill(ForgeDirection.UNKNOWN, stack, false) > FluidContainerRegistry.BUCKET_VOLUME)
                                        {

                                            /* EDIT REQUEST IN MAP */
                                            int requestAmmount = requestStack.getValue() - tank.fill(ForgeDirection.UNKNOWN, stack, true);
                                            if (requestAmmount <= 0)
                                            {
                                                this.requestMap.remove(request);
                                            }
                                            else
                                            {
                                                this.requestMap.put(request.getKey(), new Pair<FluidStack, Integer>(requestStack.getKey(), requestAmmount));
                                            }

                                            /* ADD TO UPDATE QUE */
                                            if (!this.updateQue.contains(loc))
                                            {
                                                this.updateQue.add(loc);
                                            }

                                            /* REMOVE BLOCK */
                                            FluidHelper.drainBlock(this.worldObj, loc, true);
                                            this.currentWorldEdits++;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    /** Cleans up all the data lists to remove unneeded references
     *
     * UpdateQue run every 5 seconds
     *
     * RequestQue is cleaned every time this method is called */
    public void doCleanup()
    {
        /* Call refresh on path finder to clear out invalid nodes/results */
        this.getLiquidFinder().refresh();
        /* CALL UPDATE ON EDITED BLOCKS */
        if (this.ticks % 100 == 0 && updateQue.size() > 0)
        {
            Iterator<Vector3> pp = this.updateQue.iterator();
            int up = 0;
            while (pp.hasNext() && up < TileEntityDrain.MAX_WORLD_EDITS_PER_PROCESS)
            {
                Vector3 vec = pp.next();
                worldObj.notifyBlockChange(vec.intX(), vec.intY(), vec.intZ(), vec.getBlockID(this.worldObj));
                worldObj.notifyBlockOfNeighborChange(vec.intX(), vec.intY(), vec.intZ(), vec.getBlockID(this.worldObj));
                pp.remove();
                up++;
            }
        }
        /* CLEANUP REQUEST MAP AND REMOVE INVALID TILES */
        Iterator<Entry<TileEntity, Pair<FluidStack, Integer>>> requests = this.requestMap.entrySet().iterator();
        TileEntity pipe = VectorHelper.getTileEntityFromSide(worldObj, new Vector3(this), this.getFacing().getOpposite());

        while (requests.hasNext())
        {
            Entry<TileEntity, Pair<FluidStack, Integer>> entry = (Entry<TileEntity, Pair<FluidStack, Integer>>) requests.next();
            TileEntity entity = entry.getKey();
            if (entity == null)
            {
                requests.remove();
            }
            else if (entity.isInvalid())
            {
                requests.remove();
            }
            else if (pipe instanceof INetworkPipe && !((INetworkPipe) pipe).getTileNetwork().isPartOfNetwork(entry.getKey()))
            {
                requests.remove();
            }
        }

    }

    @Override
    public int fillArea(FluidStack resource, boolean doFill)
    {
        int fillVolume = 0;

        if (!this.canDrainSources() && this.currentWorldEdits < MAX_WORLD_EDITS_PER_PROCESS)
        {
            /* ID LIQUID BLOCK AND SET VARS FOR BLOCK PLACEMENT */
            if (resource == null || resource.amount < FluidContainerRegistry.BUCKET_VOLUME)
            {
                return 0;
            }

            fillVolume = resource.amount;

            /* FIND ALL VALID BLOCKS ON LEVEL OR BELLOW */
            final Vector3 faceVec = new Vector3(this).modifyPositionFromSide(this.getFacing());
            this.getLiquidFinder().start(faceVec, true);
            //System.out.println("Drain>>FillArea>>Targets>> " + getLiquidFinder().results.size());

            /* SORT RESULTS TO PUT THE LOWEST AND CLOSEST AT THE TOP */
            if (getLiquidFinder().results.size() > 1)
            {
                this.sortBlockList(faceVec, getLiquidFinder().results, true, false);
            }
            List<Vector3> fluids = new ArrayList<Vector3>();
            List<Vector3> blocks = new ArrayList<Vector3>();
            List<Vector3> drained = new ArrayList<Vector3>();
            /* Sort results out into two groups and clear the rest out of the result list */
            Iterator<Vector3> it = this.getLiquidFinder().results.iterator();
            while (it.hasNext())
            {
                Vector3 vec = it.next();
                if (FluidHelper.isFillableFluid(worldObj, vec) && !fluids.contains(vec) && !blocks.contains(vec))
                {
                    fluids.add(vec);
                }
                else if (FluidHelper.isFillableBlock(worldObj, vec) && !blocks.contains(vec) && !fluids.contains(vec))
                {
                    blocks.add(vec);
                }
                else
                {
                    it.remove();
                }
            }
            /* Fill non-full fluids first */
            for (Vector3 loc : fluids)
            {
                if (fillVolume <= 0)
                {
                    break;
                }
                if (FluidHelper.isFillableFluid(worldObj, loc))
                {

                    fillVolume -= FluidHelper.fillBlock(worldObj, loc, FluidHelper.getStack(resource, fillVolume), doFill);
                    //System.out.println("Drain>>FillArea>>Filling>>" + (doFill ? "" : "Sim>>") + ">>Fluid>" + loc.toString());

                    if (doFill)
                    {
                        drained.add(loc);
                        this.currentWorldEdits++;
                        if (!this.updateQue.contains(loc))
                        {
                            this.updateQue.add(loc);
                        }
                    }

                }

            }
            /* Fill air or replaceable blocks after non-full fluids */
            for (Vector3 loc : blocks)
            {
                if (fillVolume <= 0)
                {
                    break;
                }
                if (FluidHelper.isFillableBlock(worldObj, loc))
                {
                    fillVolume -= FluidHelper.fillBlock(worldObj, loc, FluidHelper.getStack(resource, fillVolume), doFill);
                    //System.out.println("Drain>>FillArea>>Filling>>" + (doFill ? "" : "Sim>>") + ">>Block>" + loc.toString());

                    if (doFill)
                    {
                        drained.add(loc);
                        this.currentWorldEdits++;
                        if (!this.updateQue.contains(loc))
                        {
                            this.updateQue.add(loc);
                        }
                    }

                }
            }
            this.getLiquidFinder().results.removeAll(drained);
            //System.out.println("Drain>>FillArea>>Filling>>Filled>>" + (doFill ? "" : "Sim>>") + (resource.amount - fillVolume) + "mb");
            return Math.max(resource.amount - fillVolume, 0);
        }
        return 0;
    }

    /** Used to sort a list of vector3 locations using the vector3's distance from one point and
     * elevation in the y axis
     *
     * @param start - start location to measure distance from
     * @param locations - list of vectors to sort
     * @param closest - sort closest distance to the top
     * @param highest - sort highest y value to the top.
     *
     * Note: highest takes priority over closest */
    public void sortBlockList(final Vector3 start, final List<Vector3> locations, final boolean closest, final boolean highest)
    {
        try
        {
            Collections.sort(locations, new Comparator<Vector3>()
            {
                @Override
                public int compare(Vector3 vecA, Vector3 vecB)
                {
                    //Though unlikely always return zero for equal vectors
                    if (vecA.equals(vecB))
                    {
                        return 0;
                    }
                    //Check y value fist as this is the primary search area
                    if (Integer.compare(vecA.intY(), vecB.intY()) != 0)
                    {
                        if (highest)
                        {
                            return vecA.intY() > vecB.intY() ? -1 : 1;
                        }
                        else
                        {
                            return vecA.intY() > vecB.intY() ? 1 : -1;
                        }
                    }
                    //Check distance after that
                    double distanceA = Vector3.distance(vecA, start);
                    double distanceB = Vector3.distance(vecB, start);
                    if (Double.compare(distanceA, distanceB) != 0)
                    {
                        if (closest)
                        {
                            return distanceA > distanceB ? 1 : -1;
                        }
                        else
                        {
                            return distanceA > distanceB ? -1 : 1;
                        }
                    }
                    return Double.compare(distanceA, distanceB);
                }
            });
        }
        catch (Exception e)
        {
            System.out.println("FluidMech>>>BlockDrain>>FillArea>>Error>>CollectionSorter");
            e.printStackTrace();
        }
    }

    @Override
    public boolean canTileConnect(TileEntity entity, ForgeDirection dir)
    {
        return dir == this.getFacing();
    }

    @Override
    public void requestLiquid(TileEntity pump, FluidStack fluid, int amount)
    {
        this.requestMap.put(pump, new Pair<FluidStack, Integer>(fluid, amount));
    }

    @Override
    public void stopRequesting(TileEntity pump)
    {
        if (this.requestMap.containsKey(pump))
        {
            this.requestMap.remove(pump);
        }
    }

    @Override
    public boolean canFill(ForgeDirection from, Fluid fluid)
    {
        return this.getFacing() == from;
    }

    @Override
    public int fill(ForgeDirection from, FluidStack resource, boolean doFill)
    {
        if (this.canDrainSources() || resource == null)
        {
            return 0;
        }
        return this.fillArea(resource, doFill);
    }

    @Override
    public boolean canDrain(ForgeDirection from, Fluid fluid)
    {
        return false;
    }

    @Override
    public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain)
    {
        return null;
    }

    @Override
    public FluidTankInfo[] getTankInfo(ForgeDirection from)
    {
        return null;
    }
}
