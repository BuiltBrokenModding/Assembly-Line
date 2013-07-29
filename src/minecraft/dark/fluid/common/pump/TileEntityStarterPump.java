package dark.fluid.common.pump;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidHandler;
import universalelectricity.core.vector.Vector3;
import universalelectricity.core.vector.VectorHelper;
import universalelectricity.prefab.network.IPacketReceiver;
import universalelectricity.prefab.network.PacketManager;

import com.google.common.io.ByteArrayDataInput;

import dark.api.ColorCode;
import dark.api.IColorCoded;
import dark.api.ITileConnector;
import dark.api.IToolReadOut;
import dark.core.blocks.TileEntityMachine;
import dark.core.helpers.FluidHelper;
import dark.core.helpers.FluidRestrictionHandler;
import dark.core.helpers.MetaGroup;
import dark.core.helpers.Pair;
import dark.fluid.common.FluidMech;

public class TileEntityStarterPump extends TileEntityMachine implements IToolReadOut, ITileConnector
{

    private int currentWorldEdits = 0;
    private static final int MAX_WORLD_EDITS_PER_PROCESS = 5;

    private List<Vector3> updateQue = new ArrayList<Vector3>();
    private LiquidPathFinder pathLiquid;

    public int pos = 0;

    @Override
    public void initiate()
    {
        super.initiate();
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

        if (!this.worldObj.isRemote && !this.isDisabled() && this.ticks % 20 == 0)
        {
            this.currentWorldEdits = 0;

            if (this.canRun())
            {
                if (this.getLiquidFinder().results.size() < TileEntityDrain.MAX_WORLD_EDITS_PER_PROCESS + 10)
                {
                    this.getLiquidFinder().start(new Vector3(this).modifyPositionFromSide(ForgeDirection.DOWN), false);
                }

                if (this.getLiquidFinder().results.size() > 0)
                {
                    System.out.println("StartPump>>DrainArea>>Targets>" + this.getLiquidFinder().results.size());

                    Iterator<Vector3> fluidList = this.getLiquidFinder().results.iterator();

                    while (fluidList.hasNext())
                    {
                        Vector3 drainLocation = fluidList.next();
                        FluidStack drainStack = FluidHelper.drainBlock(this.worldObj, drainLocation, false);
                        System.out.println("StartPump>>DrainArea>>Draining>>NextFluidBlock>" + (drainStack == null ? "Null" : drainStack.amount + "mb of " + drainStack.getFluid().getName()));

                        if (this.currentWorldEdits >= MAX_WORLD_EDITS_PER_PROCESS)
                        {
                            break;
                        }
                        int fillV = FluidHelper.fillTanksAllSides(worldObj, new Vector3(this), drainStack, false, ForgeDirection.DOWN);
                        System.out.println("StartPump>>DrainArea>>Draining>>NextFluidBlock>Filled>" + fillV + "mb");
                        if (drainStack != null && fillV >= drainStack.amount)
                        {
                            System.out.println("StartPump>>DrainArea>>Draining>>Fluid>" + drainLocation.toString());
                            /* REMOVE BLOCK */
                            FluidHelper.drainBlock(this.worldObj, drainLocation, true);
                            FluidHelper.fillTanksAllSides(worldObj, new Vector3(this), drainStack, true, ForgeDirection.DOWN);
                            this.currentWorldEdits++;
                            fluidList.remove();
                            /* ADD TO UPDATE QUE */
                            if (!this.updateQue.contains(drainLocation))
                            {
                                this.updateQue.add(drainLocation);
                            }
                        }
                    }
                }
            }

            if (this.updateQue.size() > 0)
            {
                for (Vector3 vec : this.updateQue)
                {
                    this.worldObj.markBlockForUpdate(vec.intX(), vec.intY(), vec.intZ());
                    for (ForgeDirection direction : ForgeDirection.VALID_DIRECTIONS)
                    {
                        Vector3 veb = vec.clone().modifyPositionFromSide(direction);
                        if (!updateQue.contains(veb))
                        {
                            updateQue.add(veb);
                            this.worldObj.markBlockForUpdate(veb.intX(), veb.intY(), veb.intZ());
                        }
                    }
                }
                this.updateQue.clear();
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
