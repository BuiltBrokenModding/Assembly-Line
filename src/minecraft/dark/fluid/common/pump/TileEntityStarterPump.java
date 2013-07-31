package dark.fluid.common.pump;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidHandler;
import universalelectricity.core.vector.Vector3;
import dark.api.ITileConnector;
import dark.api.IToolReadOut;
import dark.api.fluid.IDrain;
import dark.core.blocks.TileEntityMachine;
import dark.core.helpers.FluidHelper;
import dark.core.helpers.Pair;

public class TileEntityStarterPump extends TileEntityMachine implements IToolReadOut, ITileConnector
{
    private int currentWorldEdits, MAX_WORLD_EDITS_PER_PROCESS;

    public float ENERGY_PER_DRAIN = 5;

    private LiquidPathFinder pathLiquid;
    private Vector3 lastDrainOrigin;

    public int rotation = 0;

    public TileEntityStarterPump()
    {
        this(1, 5, 5);
    }

    public TileEntityStarterPump(float wattTick, float wattDrain, int maxEdits)
    {
        super(wattTick, (maxEdits * wattDrain) + (wattTick * 20));
        this.MAX_WORLD_EDITS_PER_PROCESS = maxEdits;
        this.ENERGY_PER_DRAIN = wattDrain;
    }

    public LiquidPathFinder getLiquidFinder()
    {
        if (pathLiquid == null)
        {
            pathLiquid = new LiquidPathFinder(this.worldObj, 100, 20);
        }
        return pathLiquid;
    }

    @Override
    public void updateEntity()
    {
        super.updateEntity();

        if (this.ticks % 10 == 0)
        {
            this.currentWorldEdits = 0;

            if (this.running)
            {
                this.rotation = Math.max(Math.min(this.rotation + 1, 7), 0);

                if (!this.worldObj.isRemote)
                {
                    Pair<World, Vector3> pair = this.getDrainOrigin();
                    if (pair != null && pair.getKey() != null && pair.getValue() != null)
                    {
                        this.drainAroundArea(pair.getKey(), pair.getValue(), 3);
                    }
                }
            }

        }

    }

    /** Gets the origin the path finder starts on */
    public Pair<World, Vector3> getDrainOrigin()
    {
        //TODO change this to lower by the amount of air between the pump and bottom
        return new Pair<World, Vector3>(this.worldObj, new Vector3(this).modifyPositionFromSide(ForgeDirection.DOWN));
    }

    /** Drains an area starting at the given location
     *
     * @param world - world to drain in, most cases will be the TileEntities world
     * @param loc - origin to start the path finder with. If this is an instance of IDrain this
     * method will act different */
    public void drainAroundArea(World world, Vector3 vec, int update)
    {
        Vector3 origin = vec.clone();
        if (origin == null)
        {
            return;
        }

        /* Update last drain origin to prevent failed path finding */
        if (this.lastDrainOrigin == null || !this.lastDrainOrigin.equals(origin))
        {
            this.lastDrainOrigin = origin.clone();
            this.getLiquidFinder().reset();
        }

        TileEntity drain = vec.clone().getTileEntity(world);
        TileEntity entity = null;

        Set<Vector3> drainList = null;

        if (drain instanceof IDrain)
        {
            if (!((IDrain) drain).canDrain(((IDrain) drain).getDirection()))
            {
                return;
            }
            origin = vec.modifyPositionFromSide(((IDrain) drain).getDirection());
            entity = origin.getTileEntity(world);
            if (entity instanceof IFluidHandler)
            {
                FluidStack draStack = ((IFluidHandler) entity).drain(ForgeDirection.UP, MAX_WORLD_EDITS_PER_PROCESS * FluidContainerRegistry.BUCKET_VOLUME, false);

                if (draStack != null && FluidHelper.fillTanksAllSides(worldObj, new Vector3(this), draStack, false, ForgeDirection.DOWN) > 0)
                {
                    ((IFluidHandler) entity).drain(ForgeDirection.UP, FluidHelper.fillTanksAllSides(worldObj, new Vector3(this), draStack, true, ForgeDirection.DOWN), true);

                }
                return;
            }
            else
            {
                drainList = ((IDrain) drain).getFluidList();
            }
        }

        if (drainList == null)
        {
            if (this.getLiquidFinder().results.size() < TileEntityDrain.MAX_WORLD_EDITS_PER_PROCESS + 10)
            {
                this.getLiquidFinder().setWorld(world).refresh().start(origin, false);
            }
            drainList = this.getLiquidFinder().refresh().results;
        }

        if (entity == null && drainList != null && drainList.size() > 0)
        {
            //System.out.println("StartPump>>DrainArea>>Targets>" + this.getLiquidFinder().results.size());

            Iterator<Vector3> fluidList = drainList.iterator();

            while (fluidList.hasNext() && this.consumePower(ENERGY_PER_DRAIN, false))
            {
                if (this.currentWorldEdits >= MAX_WORLD_EDITS_PER_PROCESS)
                {
                    break;
                }

                Vector3 drainLocation = fluidList.next();
                FluidStack drainStack = FluidHelper.drainBlock(world, drainLocation, false, 3);
                // System.out.println("StartPump>>DrainArea>>Draining>>NextFluidBlock>" + (drainStack == null ? "Null" : drainStack.amount + "mb of " + drainStack.getFluid().getName()));

                //int fillV = FluidHelper.fillTanksAllSides(worldObj, new Vector3(this), drainStack, false, ForgeDirection.DOWN);
                //System.out.println("StartPump>>DrainArea>>Draining>>NextFluidBlock>Filled>" + fillV + "mb");

                if (drainStack != null && this.fill(drainStack, false) >= drainStack.amount && this.consumePower(ENERGY_PER_DRAIN, true))
                {
                    //System.out.println("StartPump>>DrainArea>>Draining>>Fluid>" + drainLocation.toString());
                    /* REMOVE BLOCK */
                    FluidHelper.drainBlock(this.worldObj, drainLocation, true, update);
                    this.fill(drainStack, true);
                    this.currentWorldEdits++;
                    fluidList.remove();

                    if (drain instanceof IDrain)
                    {
                        ((IDrain) drain).onUse(drainLocation);
                    }
                }
            }
        }
    }

    public int fill(FluidStack stack, boolean doFill)
    {
        return FluidHelper.fillTanksAllSides(worldObj, new Vector3(this), stack, doFill, ForgeDirection.DOWN);
    }

    @Override
    public boolean canRun()
    {
        return super.canRun() && worldObj.isBlockIndirectlyGettingPowered(xCoord, yCoord, zCoord);
    }

    @Override
    public float getRequest(ForgeDirection side)
    {
        return WATTS_PER_TICK;
    }

    @Override
    public String getMeterReading(EntityPlayer user, ForgeDirection side, EnumTools tool)
    {
        return String.format("%.2f/%.2fWatts  %d SourceBlocks", this.getEnergyStored(), this.getMaxEnergyStored(), this.getLiquidFinder().results.size());
    }

    @Override
    public boolean canConnect(ForgeDirection direction)
    {
        return direction != ForgeDirection.DOWN;
    }

    @Override
    public boolean canTileConnect(TileEntity entity, ForgeDirection direction)
    {
        return direction != ForgeDirection.DOWN;
    }

}
