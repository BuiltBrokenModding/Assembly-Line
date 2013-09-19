package dark.fluid.common.pipes;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;

import org.bouncycastle.util.Arrays;

import universalelectricity.core.vector.Vector3;
import universalelectricity.core.vector.VectorHelper;
import universalelectricity.prefab.network.IPacketReceiver;
import universalelectricity.prefab.tile.TileEntityAdvanced;

import com.google.common.io.ByteArrayDataInput;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import dark.api.ColorCode;
import dark.api.ColorCode.IColorCoded;
import dark.api.IToolReadOut;
import dark.api.fluid.INetworkPipe;
import dark.api.parts.ITileConnector;
import dark.core.common.DarkMain;
import dark.core.network.PacketHandler;
import dark.core.prefab.helpers.FluidHelper;
import dark.core.prefab.tilenetwork.NetworkTileEntities;
import dark.core.prefab.tilenetwork.fluid.NetworkPipes;

public class TileEntityPipe extends TileEntityAdvanced implements IFluidHandler, IToolReadOut, IColorCoded, INetworkPipe, IPacketReceiver
{
    /** Pipe temp tank storage, main storage is thought of as the collective network tank */
    protected FluidTank tank = new FluidTank(FluidContainerRegistry.BUCKET_VOLUME);

    /** TileEntities that this pipe has connections too */
    private List<TileEntity> connectedBlocks = new ArrayList<TileEntity>();

    /** Should render connections on side */
    public boolean[] renderConnection = new boolean[6];

    /** Network that links the collective pipes together to work */
    private NetworkPipes pipeNetwork;

    protected ColorCode colorCode = ColorCode.UNKOWN;

    protected boolean flagForColorCodeUpdate = false;

    protected int updateTick = 1;

    public enum PipePacketID
    {
        PIPE_CONNECTIONS,
        EXTENTION_CREATE,
        EXTENTION_UPDATE;
    }

    @Override
    public void updateEntity()
    {
        super.updateEntity();
        if (!worldObj.isRemote)
        {
            if (ticks % this.updateTick == 0)
            {
                this.updateTick = this.worldObj.rand.nextInt(5) * 40 + 20;
                this.refresh();
            }
        }
    }

    @Override
    public void invalidate()
    {
        super.invalidate();
        if (!this.worldObj.isRemote)
        {
            this.getTileNetwork().splitNetwork(this.worldObj, this);
        }

    }

    @Override
    public void handlePacketData(INetworkManager network, int type, Packet250CustomPayload packet, EntityPlayer player, ByteArrayDataInput dataStream)
    {
        try
        {
            if (this.worldObj.isRemote)
            {
                this.colorCode = ColorCode.get(dataStream.readInt());
                this.renderConnection[0] = dataStream.readBoolean();
                this.renderConnection[1] = dataStream.readBoolean();
                this.renderConnection[2] = dataStream.readBoolean();
                this.renderConnection[3] = dataStream.readBoolean();
                this.renderConnection[4] = dataStream.readBoolean();
                this.renderConnection[5] = dataStream.readBoolean();
            }
        }
        catch (Exception e)
        {
            System.out.print("Error with reading packet for TileEntityPipe");
            e.printStackTrace();
        }
    }

    @Override
    public Packet getDescriptionPacket()
    {
        return PacketHandler.instance().getPacket(DarkMain.CHANNEL, this, this.colorCode.ordinal(), this.renderConnection[0], this.renderConnection[1], this.renderConnection[2], this.renderConnection[3], this.renderConnection[4], this.renderConnection[5]);
    }

    /** Reads a tile entity from NBT. */
    @Override
    public void readFromNBT(NBTTagCompound nbt)
    {
        super.readFromNBT(nbt);
        //Load color code from nbt
        if (nbt.hasKey("ColorCode"))
        {
            this.colorCode = ColorCode.get(nbt.getInteger("ColorCode"));
        }
        else
        {
            this.flagForColorCodeUpdate = true;
        }

        //Load fluid tank
        FluidStack liquid = FluidStack.loadFluidStackFromNBT(nbt.getCompoundTag("FluidTank"));
        if (nbt.hasKey("stored"))
        {
            NBTTagCompound tag = nbt.getCompoundTag("stored");
            String name = tag.getString("LiquidName");
            int amount = nbt.getInteger("Amount");
            Fluid fluid = FluidRegistry.getFluid(name);
            if (fluid != null)
            {
                liquid = new FluidStack(fluid, amount);
            }
        }
        if (liquid != null)
        {
            this.tank.setFluid(liquid);
        }
    }

    /** Writes a tile entity to NBT. */
    @Override
    public void writeToNBT(NBTTagCompound nbt)
    {
        super.writeToNBT(nbt);
        nbt.setInteger("ColorCode", this.colorCode.ordinal());
        if (this.tank != null && this.tank.getFluid() != null)
        {
            nbt.setTag("FluidTank", this.tank.getFluid().writeToNBT(new NBTTagCompound()));
        }
    }

    /** gets the current color mark of the pipe */
    @Override
    public ColorCode getColor()
    {
        return this.colorCode;
    }

    /** sets the current color mark of the pipe */
    @Override
    public void setColor(Object cc)
    {
        if (!worldObj.isRemote)
        {
            this.colorCode = ColorCode.get(cc);
        }
    }

    @Override
    public String getMeterReading(EntityPlayer user, ForgeDirection side, EnumTools tool)
    {
        /* DEBUG CODE ACTIVATERS */
        boolean testConnections = false, testNetwork = true;

        /* NORMAL OUTPUT */
        String string = ((NetworkPipes) this.getTileNetwork()).pressureProduced + "p " + ((NetworkPipes) this.getTileNetwork()).getNetworkFluid() + " Extra";

        /* DEBUG CODE */
        if (testConnections)
        {
            for (int i = 0; i < 6; i++)
            {
                string += ":" + (this.renderConnection[i] ? "T" : "F") + (this.renderConnection[i] ? "T" : "F");
            }
        }
        if (testNetwork)
        {
            string += " " + this.getTileNetwork().toString();
        }

        return string;
    }

    @Override
    public int fill(ForgeDirection from, FluidStack resource, boolean doFill)
    {
        if (resource == null || !FluidHelper.isValidLiquid(this.getColor(), resource.getFluid()))
        {
            return 0;
        }
        TileEntity tile = VectorHelper.getTileEntityFromSide(this.worldObj, new Vector3(this), from);
        return ((NetworkPipes) this.getTileNetwork()).addFluidToNetwork(tile, resource, doFill);
    }

    @Override
    public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain)
    {
        return null;
    }

    @Override
    public FluidTankInfo[] getTankInfo(ForgeDirection direction)
    {
        return new FluidTankInfo[] { new FluidTankInfo(this.getTank(0)) };
    }

    /** Checks to make sure the connection is valid to the tileEntity
     *
     * @param tileEntity - the tileEntity being checked
     * @param side - side the connection is too
     * @return */
    public boolean validateConnectionSide(TileEntity tileEntity, ForgeDirection side)
    {
        if (!this.worldObj.isRemote && tileEntity != null)
        {
            if (tileEntity instanceof ITileConnector)
            {
                if (((ITileConnector) tileEntity).canTileConnect(Connection.NETWORK, side.getOpposite()))
                {
                    if (tileEntity instanceof INetworkPipe)
                    {
                        if (((INetworkPipe) tileEntity).getColor() == this.getColor())
                        {
                            this.getTileNetwork().merge(((INetworkPipe) tileEntity).getTileNetwork(), this);
                            return connectedBlocks.add(tileEntity);

                        }
                    }
                    else
                    {
                        return connectedBlocks.add(tileEntity);
                    }
                }
            }
            else if (tileEntity instanceof IColorCoded)
            {
                if (this.getColor() == ColorCode.UNKOWN || this.getColor() == ((IColorCoded) tileEntity).getColor())
                {
                    return connectedBlocks.add(tileEntity);
                }
            }
            else if (tileEntity instanceof IFluidHandler)
            {
                return connectedBlocks.add(tileEntity);
            }
        }
        return false;
    }

    @Override
    public void refresh()
    {

        if (this.worldObj != null && !this.worldObj.isRemote)
        {

            boolean[] previousConnections = this.renderConnection.clone();
            this.connectedBlocks.clear();

            for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS)
            {
                TileEntity ent = new Vector3(this).modifyPositionFromSide(dir).getTileEntity(this.worldObj);
                this.renderConnection[dir.ordinal()] = this.validateConnectionSide(ent, dir);

                if (this.renderConnection[dir.ordinal()] && ent instanceof IFluidHandler && !(ent instanceof INetworkPipe))
                {
                    IFluidHandler tankContainer = (IFluidHandler) ent;
                    this.getTileNetwork().addTile(ent, false);

                    /* LITTLE TRICK TO AUTO DRAIN TANKS ON EACH CONNECTION UPDATE */

                    FluidStack stack = tankContainer.drain(dir, FluidContainerRegistry.BUCKET_VOLUME, false);
                    if (stack != null && stack.amount > 0)
                    {
                        //TODO change this to be turned off or leave it as is for not using valves
                        int fill = ((NetworkPipes) this.getTileNetwork()).addFluidToNetwork((TileEntity) tankContainer, stack, true);
                        tankContainer.drain(dir, fill, true);
                    }
                }
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
        return new Vector3(this).modifyPositionFromSide(dir).getTileEntity(this.worldObj) != null;
    }

    @Override
    public double getMaxPressure(ForgeDirection side)
    {
        return 350;
    }

    @Override
    public NetworkTileEntities getTileNetwork()
    {
        if (this.pipeNetwork == null)
        {
            this.setTileNetwork(new NetworkPipes(this.getColor(), this));
        }
        return this.pipeNetwork;
    }

    @Override
    public void setTileNetwork(NetworkTileEntities network)
    {
        if (network instanceof NetworkPipes)
        {
            this.pipeNetwork = (NetworkPipes) network;
        }
    }

    @Override
    public int getMaxFlowRate(Fluid stack, ForgeDirection side)
    {
        //TODO change this to get info from stack
        return 1000 * 3;
    }

    @Override
    public boolean onOverPressure(Boolean damageAllowed)
    {
        if (damageAllowed)
        {
            worldObj.setBlockMetadataWithNotify(xCoord, yCoord, yCoord, 0, 0);
            return true;
        }
        return false;
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
    public FluidTank getTank(int index)
    {
        if (this.tank == null)
        {
            this.tank = new FluidTank(FluidContainerRegistry.BUCKET_VOLUME);
        }
        return this.tank;
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

    @Override
    public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean canFill(ForgeDirection from, Fluid fluid)
    {
        return this.canTileConnect(Connection.FLUIDS, from);
    }

    @Override
    public boolean canDrain(ForgeDirection from, Fluid fluid)
    {
        return this.canTileConnect(Connection.FLUIDS, from);
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

}
