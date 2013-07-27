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

public class TileEntityStarterPump extends TileEntityMachine implements IPacketReceiver, IToolReadOut, ITileConnector
{
    public final static float WATTS_PER_TICK = 20;

    private int currentWorldEdits = 0;
    private static final int MAX_WORLD_EDITS_PER_PROCESS = 30;

    private List<Vector3> updateQue = new ArrayList<Vector3>();
    private LiquidPathFinder pathLiquid;

    public int pos = 0;
    public boolean running = false;

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

        if (!this.worldObj.isRemote && !this.isDisabled() && this.ticks % 20 == 0 && !worldObj.isBlockIndirectlyGettingPowered(xCoord, yCoord, yCoord))
        {
            if (this.getLiquidFinder().results.size() < TileEntityDrain.MAX_WORLD_EDITS_PER_PROCESS + 10)
            {
                this.getLiquidFinder().start(new Vector3(this).modifyPositionFromSide(ForgeDirection.DOWN), false);
            }
            boolean prevRun = this.running;
            if (this.canRun())
            {
                this.running = true;
                if (this.getLiquidFinder().results.size() > 0)
                {
                    System.out.println("StartPump>>DrainArea>>Targets>" + this.getLiquidFinder().results.size());

                    Iterator<Vector3> fluidList = this.getLiquidFinder().results.iterator();

                    while (fluidList.hasNext())
                    {
                        System.out.println("StartPump>>DrainArea>>Draining>>NextFluidBlock");
                        Vector3 drainLocation = fluidList.next();
                        FluidStack drainStack = FluidHelper.drainBlock(this.worldObj, drainLocation, false);

                        if (this.currentWorldEdits >= MAX_WORLD_EDITS_PER_PROCESS)
                        {
                            break;
                        }
                        if (drainStack != null && FluidHelper.fillTanksAllSides(worldObj, new Vector3(this), drainStack, false, ForgeDirection.DOWN) >= drainStack.amount)
                        {
                            System.out.println("StartPump>>DrainArea>>Draining>>Fluid>" + drainLocation.toString());
                            /* REMOVE BLOCK */
                            FluidHelper.drainBlock(this.worldObj, drainLocation, true);
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
            else
            {
                this.running = false;
            }
            if (running != prevRun)
            {
                worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
            }
        }

    }

    @Override
    public Packet getDescriptionPacket()
    {
        return PacketManager.getPacket(FluidMech.CHANNEL, this, this.running);
    }

    @Override
    public void handlePacketData(INetworkManager network, int packetType, Packet250CustomPayload packet, EntityPlayer player, ByteArrayDataInput data)
    {
        try
        {
            if (worldObj.isRemote)
            {
                this.running = data.readBoolean();
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }

    @Override
    public float getRequest(ForgeDirection side)
    {
        return WATTS_PER_TICK;
    }

    @Override
    public String getMeterReading(EntityPlayer user, ForgeDirection side, EnumTools tool)
    {
        return String.format("%.2f/%.2fWatts  %f SourceBlocks", this.getEnergyStored(), this.getMaxEnergyStored(), this.getLiquidFinder().results.size());
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
