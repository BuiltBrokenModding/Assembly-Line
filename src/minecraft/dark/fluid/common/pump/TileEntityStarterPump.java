package dark.fluid.common.pump;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidHandler;
import universalelectricity.core.vector.Vector3;
import dark.api.ITileConnector;
import dark.api.IToolReadOut;
import dark.core.blocks.TileEntityMachine;
import dark.core.helpers.FluidHelper;

public class TileEntityStarterPump extends TileEntityMachine implements IToolReadOut, ITileConnector
{
    private int currentWorldEdits = 0;
    private static final int MAX_WORLD_EDITS_PER_PROCESS = 5;

    public static float ENERGY_PER_DRAIN = 5;

    private List<Vector3> updateQue = new ArrayList<Vector3>();
    private LiquidPathFinder pathLiquid;

    public int pos = 0;

    public TileEntityStarterPump()
    {
        super(1, (MAX_WORLD_EDITS_PER_PROCESS * ENERGY_PER_DRAIN) + 20);
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

        if (!this.worldObj.isRemote && !this.isDisabled() && this.ticks % 20 == 0 && this.canRun())
        {
            this.currentWorldEdits = 0;

            TileEntity entity = new Vector3(this).modifyPositionFromSide(ForgeDirection.DOWN).getTileEntity(worldObj);
            if (entity instanceof IFluidHandler)
            {
                FluidStack draStack = ((IFluidHandler) entity).drain(ForgeDirection.UP, MAX_WORLD_EDITS_PER_PROCESS * FluidContainerRegistry.BUCKET_VOLUME, false);
                if (draStack != null && FluidHelper.fillTanksAllSides(worldObj, new Vector3(this), draStack, false, ForgeDirection.DOWN) > 0)
                {
                    ((IFluidHandler) entity).drain(ForgeDirection.UP, FluidHelper.fillTanksAllSides(worldObj, new Vector3(this), draStack, true, ForgeDirection.DOWN), true);
                }
            }

            if (this.getLiquidFinder().results.size() < TileEntityDrain.MAX_WORLD_EDITS_PER_PROCESS + 10)
            {
                this.getLiquidFinder().refresh().start(new Vector3(this).modifyPositionFromSide(ForgeDirection.DOWN), false);
            }

            if (entity == null && this.getLiquidFinder().results.size() > 0)
            {
                System.out.println("StartPump>>DrainArea>>Targets>" + this.getLiquidFinder().results.size());

                Iterator<Vector3> fluidList = this.getLiquidFinder().results.iterator();

                while (fluidList.hasNext() && this.consumePower(ENERGY_PER_DRAIN, false))
                {
                    if (this.currentWorldEdits >= MAX_WORLD_EDITS_PER_PROCESS)
                    {
                        break;
                    }

                    Vector3 drainLocation = fluidList.next();
                    FluidStack drainStack = FluidHelper.drainBlock(this.worldObj, drainLocation, false, 3);
                    System.out.println("StartPump>>DrainArea>>Draining>>NextFluidBlock>" + (drainStack == null ? "Null" : drainStack.amount + "mb of " + drainStack.getFluid().getName()));

                    int fillV = FluidHelper.fillTanksAllSides(worldObj, new Vector3(this), drainStack, false, ForgeDirection.DOWN);
                    System.out.println("StartPump>>DrainArea>>Draining>>NextFluidBlock>Filled>" + fillV + "mb");

                    if (drainStack != null && fillV >= drainStack.amount && this.consumePower(ENERGY_PER_DRAIN, true))
                    {
                        System.out.println("StartPump>>DrainArea>>Draining>>Fluid>" + drainLocation.toString());
                        /* REMOVE BLOCK */
                        FluidHelper.drainBlock(this.worldObj, drainLocation, true, 3);
                        FluidHelper.fillTanksAllSides(worldObj, new Vector3(this), drainStack, true, ForgeDirection.DOWN);
                        this.currentWorldEdits++;
                        fluidList.remove();
                    }
                }
            }
        }

    }

    @Override
    public boolean canRun()
    {
        return super.canRun() && !worldObj.isBlockIndirectlyGettingPowered(xCoord, yCoord, zCoord);
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
