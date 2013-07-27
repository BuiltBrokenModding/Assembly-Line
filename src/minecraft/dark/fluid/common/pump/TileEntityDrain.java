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
import universalelectricity.core.vector.Vector2;
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

    private List<Vector3> targetSources = new ArrayList<Vector3>();
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
        if (!this.worldObj.isRemote && this.ticks % 30 == 0)
        {
            this.currentWorldEdits = 0;
            this.doCleanup();

            if (this.canDrainSources() && this.requestMap.size() > 0)
            {
                /* ONLY FIND NEW SOURCES IF OUR CURRENT LIST RUNS DRY */
                if (this.targetSources.size() < TileEntityDrain.MAX_WORLD_EDITS_PER_PROCESS + 10)
                {
                    this.getNextFluidBlock();
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

    /** Finds more liquid blocks using a path finder to be drained */
    public void getNextFluidBlock()
    {

        getLiquidFinder().reset();
        getLiquidFinder().init(new Vector3(this.xCoord + this.getFacing().offsetX, this.yCoord + this.getFacing().offsetY, this.zCoord + this.getFacing().offsetZ), false);
        // System.out.println("Nodes:" + pathFinder.nodes.size() + "Results:" +
        // pathFinder.results.size());
        for (Vector3 vec : getLiquidFinder().nodes)
        {
            this.addVectorToQue(vec);
        }
    }

    @SuppressWarnings("unchecked")
    public void doCleanup()
    {
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
        Iterator requests = this.requestMap.entrySet().iterator();
        TileEntity pipe = VectorHelper.getTileEntityFromSide(worldObj, new Vector3(this), this.getFacing().getOpposite());

        while (requests.hasNext())
        {
            Entry<TileEntityConstructionPump, FluidStack> entry = (Entry<TileEntityConstructionPump, FluidStack>) requests.next();
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
        int drained = 0;

        if (!this.canDrainSources() && this.currentWorldEdits < MAX_WORLD_EDITS_PER_PROCESS)
        {
            /* ID LIQUID BLOCK AND SET VARS FOR BLOCK PLACEMENT */
            if (resource == null || resource.amount < FluidContainerRegistry.BUCKET_VOLUME)
            {
                return 0;
            }

            int blockID = resource.getFluid().getBlockID();
            int blocks = (resource.amount / FluidContainerRegistry.BUCKET_VOLUME);

            /* FIND ALL VALID BLOCKS ON LEVEL OR BELLOW */
            final Vector3 faceVec = new Vector3(this.xCoord + this.getFacing().offsetX, this.yCoord + this.getFacing().offsetY, this.zCoord + this.getFacing().offsetZ);
            getLiquidFinder().init(faceVec, true);
            System.out.println("Drain:FillArea: Targets -> " + getLiquidFinder().results.size());

            /* SORT RESULTS TO PUT THE LOWEST AND CLOSEST AT THE TOP */

            if (getLiquidFinder().results.size() > 1)
            {
                this.sortBlockList(faceVec, getLiquidFinder().results, true, false);
            }

            for (Vector3 loc : getLiquidFinder().results)
            {
                if (blocks <= 0)
                {
                    break;
                }
                Fluid stack = FluidHelper.getFluidFromBlockID(loc.getBlockID(worldObj));
                if (stack != null && stack.getBlockID() == blockID && loc.getBlockMetadata(worldObj) != 0)
                {
                    drained += FluidContainerRegistry.BUCKET_VOLUME;
                    blocks--;
                    if (doFill)
                    {
                        loc.setBlock(worldObj, blockID, 0);
                        this.currentWorldEdits++;
                        if (!this.updateQue.contains(loc))
                        {
                            this.updateQue.add(loc);
                        }
                    }
                }

            }

            for (Vector3 loc : getLiquidFinder().results)
            {
                if (blocks <= 0)
                {
                    break;
                }
                if (loc.getBlockID(worldObj) == 0)
                {
                    drained += FluidContainerRegistry.BUCKET_VOLUME;
                    blocks--;
                    if (doFill)
                    {
                        loc.setBlock(worldObj, blockID, 0);
                        this.currentWorldEdits++;
                        if (!this.updateQue.contains(loc))
                        {
                            this.updateQue.add(loc);
                        }
                    }
                }
            }
        }
        return drained;
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

    public void addVectorToQue(Vector3 vector)
    {
        if (!this.targetSources.contains(vector))
        {
            this.targetSources.add(vector);
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
