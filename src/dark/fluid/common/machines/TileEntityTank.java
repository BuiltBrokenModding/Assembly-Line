package dark.fluid.common.machines;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityComparator;
import net.minecraft.util.AxisAlignedBB;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;
import net.minecraftforge.fluids.IFluidTank;

import org.bouncycastle.util.Arrays;

import universalelectricity.core.vector.Vector3;
import universalelectricity.prefab.network.IPacketReceiver;

import com.google.common.io.ByteArrayDataInput;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import dark.api.ColorCode;
import dark.api.IToolReadOut;
import dark.api.ColorCode.IColorCoded;
import dark.api.fluid.INetworkFluidPart;
import dark.api.fluid.INetworkPipe;
import dark.core.common.DarkMain;
import dark.core.network.PacketHandler;
import dark.core.prefab.helpers.FluidHelper;
import dark.core.prefab.tilenetwork.NetworkTileEntities;
import dark.core.prefab.tilenetwork.fluid.NetworkFluidContainers;
import dark.core.prefab.tilenetwork.fluid.NetworkFluidTiles;
import dark.fluid.common.prefab.TileEntityFluidStorage;

public class TileEntityTank extends TileEntityFluidStorage implements IFluidHandler, IToolReadOut, IColorCoded, INetworkFluidPart, IPacketReceiver
{
    /* CURRENTLY CONNECTED TILE ENTITIES TO THIS */
    private List<TileEntity> connectedBlocks = new ArrayList<TileEntity>();
    public int[] renderConnection = new int[6];

    /* NETWORK INSTANCE THAT THIS PIPE USES */
    private NetworkFluidContainers fluidNetwork;
    private int refreshRate = 1;

    @Override
    public void updateEntity()
    {
        super.updateEntity();
        if (!worldObj.isRemote)
        {
            if (ticks % refreshRate == 0)
            {
                this.refreshRate = (random.nextInt(5) * 40) + 20;
                this.refresh();
            }
        }
    }

    @Override
    public void initiate()
    {
        this.refresh();
    }

    @Override
    public void invalidate()
    {
        if (!this.worldObj.isRemote)
        {
            this.getTileNetwork().splitNetwork(this.worldObj, this);
        }

        super.invalidate();
    }

    @Override
    public void handlePacketData(INetworkManager network, int type, Packet250CustomPayload packet, EntityPlayer player, ByteArrayDataInput dataStream)
    {
        try
        {
            if (this.worldObj.isRemote)
            {
                int id = dataStream.readInt();
                if (id == 0 || id == 1)
                {
                    if (id == 0)
                    {
                        this.getTank().setFluid(FluidStack.loadFluidStackFromNBT(PacketHandler.instance().readNBTTagCompound(dataStream)));
                        //System.out.println("Received Fluid Packet Fluid = " + this.getTank().getFluid().getFluid().getName() + "@" + this.getTank().getFluid().amount);
                    }
                    else
                    {
                        this.getTank().setFluid(null);
                        dataStream.readInt();
                    }
                    this.renderConnection[0] = dataStream.readInt();
                    this.renderConnection[1] = dataStream.readInt();
                    this.renderConnection[2] = dataStream.readInt();
                    this.renderConnection[3] = dataStream.readInt();
                    this.renderConnection[4] = dataStream.readInt();
                    this.renderConnection[5] = dataStream.readInt();
                }
            }
        }
        catch (IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    public Packet getDescriptionPacket()
    {
        FluidStack stack = null;
        if (this.getTank().getFluid() != null && this.getTank().getFluid().getFluid() != null)
        {
            stack = this.getTank().getFluid();
        }
        return PacketHandler.instance().getPacket(DarkMain.CHANNEL, this, stack != null ? 0 : 1, stack != null ? stack.writeToNBT(new NBTTagCompound()) : 1, this.renderConnection[0], this.renderConnection[1], this.renderConnection[2], this.renderConnection[3], this.renderConnection[4], this.renderConnection[5]);
    }

    /** gets the current color mark of the pipe */
    @Override
    public ColorCode getColor()
    {
        if (this.worldObj == null)
        {
            return ColorCode.UNKOWN;
        }
        return ColorCode.get(worldObj.getBlockMetadata(xCoord, yCoord, zCoord));
    }

    /** sets the current color mark of the pipe */
    @Override
    public void setColor(Object cc)
    {
    }

    @Override
    public String getMeterReading(EntityPlayer user, ForgeDirection side, EnumTools tool)
    {
        /* DEBUG CODE ACTIVATERS */
        boolean testNetwork = true;

        /* NORMAL OUTPUT */
        String string = "Fluid>" + (this.getTileNetwork() instanceof NetworkFluidTiles ? ((NetworkFluidTiles) this.getTileNetwork()).getNetworkFluid() : "Error");

        if (testNetwork)
        {
            string += "\nNetID>" + this.getTileNetwork().toString();
        }

        return string;
    }

    @Override
    public int fill(ForgeDirection from, FluidStack resource, boolean doFill)
    {
        if (resource == null || !FluidHelper.isValidLiquid(this.getColor(), resource.getFluid()) || this.worldObj.isRemote)
        {
            return 0;
        }
        return ((NetworkFluidContainers) this.getTileNetwork()).storeFluidInSystem(resource, doFill);
    }

    @Override
    public FluidStack drain(ForgeDirection from, FluidStack stack, boolean doDrain)
    {
        if (this.worldObj.isRemote)
        {
            return null;
        }
        return ((NetworkFluidContainers) this.getTileNetwork()).drainFluidFromSystem(stack, doDrain);
    }

    @Override
    public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain)
    {
        if (this.worldObj.isRemote)
        {
            return null;
        }
        return ((NetworkFluidContainers) this.getTileNetwork()).drainFluidFromSystem(maxDrain, doDrain);
    }

    @Override
    public FluidTankInfo[] getTankInfo(ForgeDirection direction)
    {
        return new FluidTankInfo[] { new FluidTankInfo(((NetworkFluidContainers) this.getTileNetwork()).combinedStorage()) };
    }

    /** Checks to make sure the connection is valid to the tileEntity
     *
     * @param tileEntity - the tileEntity being checked
     * @param side - side the connection is too */
    public void validateConnectionSide(TileEntity tileEntity, ForgeDirection side)
    {
        if (!this.worldObj.isRemote)
        {
            this.renderConnection[side.ordinal()] = tileEntity instanceof TileEntityTank && ((TileEntityTank) tileEntity).getColor() == this.getColor() ? 2 : (tileEntity instanceof INetworkPipe ? 1 : tileEntity != null ? 3 : 0);

            if (tileEntity != null)
            {
                if (tileEntity instanceof TileEntityTank)
                {
                    if (((TileEntityTank) tileEntity).getColor() == this.getColor())
                    {
                        this.getTileNetwork().merge(((TileEntityTank) tileEntity).getTileNetwork(), this);
                        connectedBlocks.add(tileEntity);
                    }
                }
                if (tileEntity instanceof TileEntityComparator)
                {
                    this.worldObj.markBlockForUpdate(tileEntity.xCoord, tileEntity.yCoord, tileEntity.zCoord);
                }
            }
        }
    }

    @Override
    public void refresh()
    {

        if (this.worldObj != null && !this.worldObj.isRemote)
        {
            int[] previousConnections = this.renderConnection.clone();
            this.connectedBlocks.clear();

            for (int i = 0; i < 6; i++)
            {
                ForgeDirection dir = ForgeDirection.getOrientation(i);
                this.validateConnectionSide(this.worldObj.getBlockTileEntity(xCoord + dir.offsetX, yCoord + dir.offsetY, zCoord + dir.offsetZ), dir);

            }
            /** Only send packet updates if visuallyConnected changed. */
            if (!Arrays.areEqual(previousConnections, this.renderConnection))
            {
                this.worldObj.markBlockForUpdate(this.xCoord, this.yCoord, this.zCoord);
            }
        }
    }

    @Override
    public boolean canTileConnect(Connection type, ForgeDirection dir)
    {
        TileEntity entity = new Vector3(this).modifyPositionFromSide(dir).getTileEntity(this.worldObj);
        return entity != null && entity instanceof IFluidHandler;
    }

    @Override
    public NetworkTileEntities getTileNetwork()
    {
        if (this.fluidNetwork == null)
        {
            this.setTileNetwork(new NetworkFluidContainers(this.getColor(), this));
        }
        return this.fluidNetwork;
    }

    @Override
    public void setTileNetwork(NetworkTileEntities network)
    {
        if (network instanceof NetworkFluidContainers)
        {
            this.fluidNetwork = ((NetworkFluidContainers) network);
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getRenderBoundingBox()
    {
        return AxisAlignedBB.getAABBPool().getAABB(this.xCoord, this.yCoord, this.zCoord, this.xCoord + 1, this.yCoord + 1, this.zCoord + 1);
    }

    @Override
    public List<TileEntity> getNetworkConnections()
    {
        return this.connectedBlocks;
    }

    @Override
    public int getTankSize()
    {
        return FluidContainerRegistry.BUCKET_VOLUME * (BlockTank.tankVolume > 0 ? BlockTank.tankVolume : 1);
    }

    @Override
    public int fillTankContent(int index, FluidStack stack, boolean doFill)
    {
        if (this.getTank(index) != null)
        {
            return this.getTank(index).fill(stack, doFill);
        }
        return 0;
    }

    @Override
    public FluidStack drainTankContent(int index, int volume, boolean doDrain)
    {
        if (this.getTank(index) != null)
        {
            return this.getTank(index).drain(volume, doDrain);
        }
        return null;
    }

    public int getRedstoneLevel()
    {
        if (this.getTileNetwork() != null && this.getTileNetwork() instanceof NetworkFluidTiles)
        {
            IFluidTank tank = ((NetworkFluidTiles) this.getTileNetwork()).combinedStorage();
            if (tank != null && tank.getFluid() != null)
            {
                int max = tank.getCapacity();
                int current = tank.getFluid().amount;
                double percent = current / max;
                return ((int) (15 * percent));
            }
        }
        return 0;
    }

    @Override
    public boolean canFill(ForgeDirection from, Fluid fluid)
    {
        return fluid != null;
    }

    @Override
    public boolean canDrain(ForgeDirection from, Fluid fluid)
    {
        return fluid != null;
    }

    @Override
    public boolean mergeDamage(String result)
    {
        return false;
    }

    @Override
    public int getNumberOfTanks()
    {
        return 1;
    }

    @Override
    public FluidTank getTank(int index)
    {
        return this.getTank();
    }
}
