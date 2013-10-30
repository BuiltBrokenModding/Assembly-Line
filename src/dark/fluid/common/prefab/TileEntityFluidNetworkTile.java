package dark.fluid.common.prefab;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
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

import com.google.common.io.ByteArrayDataInput;

import cpw.mods.fml.common.network.Player;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import universalelectricity.core.vector.Vector3;
import dark.api.ColorCode;
import dark.api.ColorCode.IColorCoded;
import dark.api.fluid.FluidMasterList;
import dark.api.fluid.INetworkFluidPart;
import dark.api.parts.INetworkPart;
import dark.api.parts.ITileConnector;
import dark.core.common.DarkMain;
import dark.core.network.ISimplePacketReceiver;
import dark.core.network.PacketHandler;
import dark.core.prefab.tilenetwork.NetworkTileEntities;
import dark.core.prefab.tilenetwork.fluid.NetworkFluidTiles;
import dark.core.prefab.tilenetwork.fluid.NetworkPipes;
import dark.fluid.common.PipeMaterial;
import dark.fluid.common.machines.TileEntityTank;

public class TileEntityFluidNetworkTile extends TileEntityFluidDevice implements INetworkFluidPart, ISimplePacketReceiver
{
    private int updateTick = 1;
    protected static final byte NO_CONENCTION = 0, PIPE_CONENCTION = 1, NETWORK_CONNECTION = 2, TILE_ENTITY_CONENCTION = 3;
    protected FluidTank[] internalTanks = new FluidTank[] { new FluidTank(FluidContainerRegistry.BUCKET_VOLUME) };
    protected FluidTankInfo[] internalTanksInfo = new FluidTankInfo[] { new FluidTankInfo(null, FluidContainerRegistry.BUCKET_VOLUME) };
    protected List<TileEntity> connectedBlocks = new ArrayList<TileEntity>();
    public byte[] renderConnection = new byte[6];
    public boolean[] canConnectSide = new boolean[] { true, true, true, true, true, true };
    protected int heat = 0;
    protected int maxHeat = 20000;
    protected int damage = 0;
    protected int maxDamage = 1000;
    protected int subID = 0;

    protected NetworkFluidTiles network;

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
    public int fill(ForgeDirection from, FluidStack resource, boolean doFill)
    {
        if (this.getTileNetwork() != null && this.canConnectSide[from.ordinal()] && resource != null)
        {
            return this.getTileNetwork().fillNetworkTank(resource, doFill);
        }
        return 0;
    }

    @Override
    public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain)
    {
        if (this.getTileNetwork() != null && this.canConnectSide[from.ordinal()] && resource != null)
        {
            if (this.getTileNetwork().getNetworkTank() != null && this.getTileNetwork().getNetworkTank().getFluid() != null && this.getTileNetwork().getNetworkTank().getFluid().isFluidEqual(resource))
            {
                this.getTileNetwork().drainNetworkTank(resource.amount, doDrain);
            }

        }
        return null;
    }

    @Override
    public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain)
    {
        if (this.getTileNetwork() != null && this.canConnectSide[from.ordinal()])
        {
            this.getTileNetwork().drainNetworkTank(maxDrain, doDrain);
        }
        return null;
    }

    @Override
    public boolean canFill(ForgeDirection from, Fluid fluid)
    {
        return this.canConnectSide[from.ordinal()] && this.damage < this.maxDamage;
    }

    @Override
    public boolean canDrain(ForgeDirection from, Fluid fluid)
    {
        return this.canConnectSide[from.ordinal()] && this.damage < this.maxDamage;
    }

    @Override
    public FluidTankInfo[] getTankInfo(ForgeDirection from)
    {
        return new FluidTankInfo[] { this.getTileNetwork().getNetworkTankInfo() };
    }

    @Override
    public List<TileEntity> getNetworkConnections()
    {
        return this.connectedBlocks;
    }

    @Override
    public void refresh()
    {
        if (this.worldObj != null && !this.worldObj.isRemote)
        {
            byte[] previousConnections = this.renderConnection.clone();
            this.connectedBlocks.clear();

            for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS)
            {
                this.validateConnectionSide(new Vector3(this).modifyPositionFromSide(dir).getTileEntity(this.worldObj), dir);

            }
            /** Only send packet updates if visuallyConnected changed. */
            if (!Arrays.areEqual(previousConnections, this.renderConnection))
            {
                this.sendRenderUpdate();
            }
        }

    }

    /** Checks to make sure the connection is valid to the tileEntity
     *
     * @param tileEntity - the tileEntity being checked
     * @param side - side the connection is too */
    public void validateConnectionSide(TileEntity tileEntity, ForgeDirection side)
    {
        if (!this.worldObj.isRemote)
        {

            this.renderConnection[side.ordinal()] = TileEntityFluidNetworkTile.NO_CONENCTION;

            if (tileEntity != null)
            {
                this.renderConnection[side.ordinal()] = TileEntityFluidNetworkTile.TILE_ENTITY_CONENCTION;
                if (tileEntity instanceof ITileConnector && !((ITileConnector) tileEntity).canTileConnect(Connection.FLUIDS, side.getOpposite()))
                {
                    this.renderConnection[side.ordinal()] = TileEntityFluidNetworkTile.NO_CONENCTION;
                }
                else if (tileEntity instanceof INetworkFluidPart)
                {
                    if (this.canTileConnect(Connection.NETWORK, side.getOpposite()))
                    {
                        this.getTileNetwork().merge(((INetworkFluidPart) tileEntity).getTileNetwork(), (INetworkPart) tileEntity);
                        this.renderConnection[side.ordinal()] = TileEntityFluidNetworkTile.NETWORK_CONNECTION;
                        connectedBlocks.add(tileEntity);
                    }
                }
                else if (tileEntity instanceof IFluidHandler)
                {
                    this.getTileNetwork().merge(((TileEntityTank) tileEntity).getTileNetwork(), this);
                    connectedBlocks.add(tileEntity);
                    if (this.getTileNetwork() instanceof NetworkPipes)
                    {
                        ((NetworkPipes) this.getTileNetwork()).addTile(tileEntity, false);
                    }
                }
                else
                {
                    this.renderConnection[side.ordinal()] = TileEntityFluidNetworkTile.TILE_ENTITY_CONENCTION;
                }
            }
        }
    }

    @Override
    public NetworkFluidTiles getTileNetwork()
    {
        if (this.network == null)
        {
            this.network = new NetworkFluidTiles(this);
        }
        return this.network;
    }

    @Override
    public void setTileNetwork(NetworkTileEntities fluidNetwork)
    {
        if (fluidNetwork instanceof NetworkFluidTiles)
        {
            this.network = (NetworkFluidTiles) fluidNetwork;
        }

    }

    @Override
    public boolean mergeDamage(String result)
    {
        return false;
    }

    @Override
    public FluidTankInfo[] getTankInfo()
    {
        if (this.internalTanksInfo == null)
        {
            this.internalTanksInfo = new FluidTankInfo[this.internalTanks.length];
            for (int i = 0; i < this.internalTanks.length; i++)
            {
                this.internalTanksInfo[i] = this.internalTanks[i].getInfo();
            }
        }
        return this.internalTanksInfo;
    }

    @Override
    public int fillTankContent(int index, FluidStack stack, boolean doFill)
    {
        if (index < this.internalTanks.length)
        {
            if (this.internalTanks[index] == null)
            {
                this.internalTanks[index] = new FluidTank(FluidContainerRegistry.BUCKET_VOLUME);
                this.internalTanksInfo[index] = this.internalTanks[index].getInfo();
            }
            int p = this.internalTanks[index].getFluid() != null ? this.internalTanks[index].getFluid().amount : 0;
            int fill = this.internalTanks[index].fill(stack, doFill);
            if (p != fill)
            {
                //TODO add a catch to this so we don't send a dozen packets for one updates
                this.sendTankUpdate(index);
            }
            return fill;
        }
        return 0;
    }

    @Override
    public FluidStack drainTankContent(int index, int volume, boolean doDrain)
    {
        if (index < this.internalTanks.length)
        {
            if (this.internalTanks[index] == null)
            {
                this.internalTanks[index] = new FluidTank(FluidContainerRegistry.BUCKET_VOLUME);
                this.internalTanksInfo[index] = this.internalTanks[index].getInfo();
            }
            FluidStack prev = this.internalTanks[index].getFluid();
            FluidStack stack = this.internalTanks[index].drain(volume, doDrain);
            if (prev != null && (stack == null || prev.amount != stack.amount))
            {
                //TODO add a catch to this so we don't send a dozen packets for one updates
                this.sendTankUpdate(index);
            }
            return stack;
        }
        return null;
    }

    @Override
    public boolean canTileConnect(Connection type, ForgeDirection dir)
    {
        if (this.damage >= this.maxDamage)
        {
            return false;
        }
        Vector3 connection = new Vector3(this).modifyPositionFromSide(dir.getOpposite());
        TileEntity entity = connection.getTileEntity(this.worldObj);
        //Unknown color codes can connect to any color, however two different colors can connect to support better pipe layouts
        if (entity instanceof IColorCoded && this instanceof IColorCoded && ((IColorCoded) entity).getColor() != ((IColorCoded) this).getColor() && ((IColorCoded) this).getColor() != ColorCode.UNKOWN && ((IColorCoded) entity).getColor() != ColorCode.UNKOWN)
        {
            return false;
        }//All Fluid connections are supported
        else if (type == Connection.FLUIDS)
        {
            return true;
        }
        return false;
    }

    @Override
    public boolean canPassThrew(FluidStack fluid, ForgeDirection from, ForgeDirection to)
    {
        return this.connectedBlocks.get(from.ordinal()) != null && this.connectedBlocks.get(to.ordinal()) != null && this.damage < this.maxDamage;
    }

    @Override
    public boolean onPassThrew(FluidStack fluid, ForgeDirection from, ForgeDirection to)
    {
        PipeMaterial mat = PipeMaterial.get(this.getBlockMetadata());
        if (fluid != null && fluid.getFluid() != null && mat != null)
        {
            if (fluid.getFluid().isGaseous(fluid) && !mat.canSupportGas)
            {
                //TODO lose 25% of the gas, and render the lost
            }
            else if (FluidMasterList.isMolten(fluid.getFluid()) && !mat.canSupportMoltenFluids)
            {
                //TODO start to heat up the pipe to melting point. When it hits melting point turn the pipe to its molten metal equal
                //TODO also once it reaches a set heat level start burning up blocks around the pipe. Eg wood
                this.heat += FluidMasterList.getHeatPerPass(fluid.getFluid());
                if (heat >= this.maxHeat)
                {
                    this.worldObj.setBlock(xCoord, yCoord, zCoord, Block.lavaStill.blockID);
                    return true;
                }
            }
            else if (!fluid.getFluid().isGaseous(fluid) && !mat.canSupportFluids)
            {
                this.damage += 1;
                if (this.damage >= this.maxDamage)
                {
                    //TODO test this and make sure its right, as well black fluid block in some cases
                    this.getBlockType().dropBlockAsItem(worldObj, xCoord, yCoord, zCoord, 0, 0);
                    this.worldObj.setBlock(xCoord, yCoord, zCoord, 0);
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt)
    {
        super.readFromNBT(nbt);
        this.damage = nbt.getInteger("damage");
        this.heat = nbt.getInteger("heat");
        this.subID = nbt.getInteger("subID");
        if (nbt.hasKey("stored"))
        {
            NBTTagCompound tag = nbt.getCompoundTag("stored");
            String name = tag.getString("LiquidName");
            int amount = nbt.getInteger("Amount");
            Fluid fluid = FluidRegistry.getFluid(name);
            if (fluid != null)
            {
                FluidStack liquid = new FluidStack(fluid, amount);
                internalTanks[0].setFluid(liquid);
                internalTanksInfo[0] = internalTanks[0].getInfo();
            }
        }
        else if (nbt.hasKey("FluidTank"))
        {
            internalTanks[0].readFromNBT(nbt.getCompoundTag("FluidTank"));
            internalTanksInfo[0] = internalTanks[0].getInfo();
        }
        else
        {
            int tankCount = nbt.getByte("InternalTanks");

            if (tankCount > 0)
            {
                this.internalTanks = new FluidTank[tankCount];
                for (int i = 0; i < this.internalTanks.length; i++)
                {
                    FluidTank tank = new FluidTank(FluidContainerRegistry.BUCKET_VOLUME);
                    if (this.internalTanksInfo != null && i < this.internalTanksInfo.length && this.internalTanksInfo[i] != null)
                    {
                        tank = new FluidTank(this.internalTanksInfo[i].capacity);
                    }
                    tank.readFromNBT(nbt.getCompoundTag("FluidTank" + i));
                }
                this.internalTanksInfo = new FluidTankInfo[tankCount];
                for (int i = 0; i < this.internalTanksInfo.length; i++)
                {
                    this.internalTanksInfo[i] = this.internalTanks[i].getInfo();
                }
            }
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt)
    {
        super.writeToNBT(nbt);
        nbt.setInteger("damage", this.damage);
        nbt.setInteger("heat", this.heat);
        nbt.setInteger("subID", this.subID);
        if (this.internalTanks != null)
        {
            nbt.setByte("InternalTanks", (byte) this.internalTanks.length);
            for (byte i = 0; i < this.internalTanks.length; i++)
            {
                if (this.internalTanks[i] != null)
                {
                    nbt.setCompoundTag("FluidTank" + i, this.internalTanks[i].writeToNBT(new NBTTagCompound()));
                }
            }
        }
    }

    @Override
    public boolean simplePacket(String id, ByteArrayDataInput data, Player player)
    {
        try
        {
            if (this.worldObj.isRemote)
            {
                if (id == "DescriptionPacket")
                {
                    this.subID = data.readInt();
                    this.renderConnection[0] = data.readByte();
                    this.renderConnection[1] = data.readByte();
                    this.renderConnection[2] = data.readByte();
                    this.renderConnection[3] = data.readByte();
                    this.renderConnection[4] = data.readByte();
                    this.renderConnection[5] = data.readByte();
                    int tanks = data.readInt();
                    this.internalTanks = new FluidTank[tanks];
                    for (int i = 0; i < tanks; i++)
                    {
                        this.internalTanks[i] = new FluidTank(data.readInt());
                        this.internalTanks[i].readFromNBT(PacketHandler.instance().readNBTTagCompound(data));
                    }
                    return true;
                }
                else if (id == "TankPacket")
                {
                    this.subID = data.readInt();
                    this.renderConnection[0] = data.readByte();
                    this.renderConnection[1] = data.readByte();
                    this.renderConnection[2] = data.readByte();
                    this.renderConnection[3] = data.readByte();
                    this.renderConnection[4] = data.readByte();
                    this.renderConnection[5] = data.readByte();
                    return true;
                }
                else if (id == "RenderPacket")
                {
                    int tanks = data.readInt();
                    this.internalTanks = new FluidTank[tanks];
                    for (int i = 0; i < tanks; i++)
                    {
                        this.internalTanks[i] = new FluidTank(data.readInt());
                        this.internalTanks[i].readFromNBT(PacketHandler.instance().readNBTTagCompound(data));
                    }
                    return true;
                }
                else if (id == "SingleTank")
                {
                    int index = data.readInt();
                    this.internalTanks[index] = new FluidTank(data.readInt());
                    this.internalTanks[index].readFromNBT(PacketHandler.instance().readNBTTagCompound(data));
                    return true;
                }
            }
        }
        catch (IOException e)
        {
            System.out.println("// Fluid Mechanics Tank packet read error");
            e.printStackTrace();
            return true;
        }
        return false;
    }

    @Override
    public Packet getDescriptionPacket()
    {
        Object[] data = new Object[(this.internalTanks != null ? (this.internalTanks.length * 2) : 2) + 7];
        data[0] = "DescriptionPacket";
        data[1] = this.subID;
        data[2] = this.renderConnection[0];
        data[3] = this.renderConnection[1];
        data[4] = this.renderConnection[2];
        data[5] = this.renderConnection[3];
        data[6] = this.renderConnection[4];
        data[7] = this.renderConnection[5];
        data[8] = (this.internalTanks != null ? (this.internalTanks.length) : 1);
        int place = 9;
        for (int i = 0; i < this.internalTanks.length; i++)
        {
            if (this.internalTanks[i] == null)
            {
                this.internalTanks[i] = new FluidTank(FluidContainerRegistry.BUCKET_VOLUME);
            }
            data[place] = this.internalTanks[i].getCapacity();
            data[place + 1] = this.internalTanks[i].writeToNBT(new NBTTagCompound());
            place += 2;
        }
        return PacketHandler.instance().getPacket(DarkMain.CHANNEL, this, data);
    }

    public void sendRenderUpdate()
    {
        Object[] data = new Object[7];
        data[0] = "renderPacket";
        data[1] = this.subID;
        data[2] = this.renderConnection[0];
        data[3] = this.renderConnection[1];
        data[4] = this.renderConnection[2];
        data[5] = this.renderConnection[3];
        data[6] = this.renderConnection[4];
        data[7] = this.renderConnection[5];
        PacketHandler.instance().sendPacketToClients(PacketHandler.instance().getPacket(DarkMain.CHANNEL, this, data));
    }

    public void sendTankUpdate()
    {
        if (this.internalTanks != null)
        {
            Object[] data = new Object[(this.internalTanks != null ? (this.internalTanks.length * 2) : 2) + 2];
            data[0] = "TankPacket";
            data[1] = this.internalTanks.length;
            int place = 2;
            for (int i = 0; i < this.internalTanks.length; i++)
            {
                if (this.internalTanks[i] == null)
                {
                    this.internalTanks[i] = new FluidTank(FluidContainerRegistry.BUCKET_VOLUME);
                }
                data[place] = this.internalTanks[i].getCapacity();
                data[place + 1] = this.internalTanks[i].writeToNBT(new NBTTagCompound());
                place += 2;
            }
            PacketHandler.instance().sendPacketToClients(PacketHandler.instance().getPacket(DarkMain.CHANNEL, this, data));
        }
    }

    public void sendTankUpdate(int index)
    {
        if (this.internalTanks != null && index < this.internalTanks.length)
        {
            if (this.internalTanks[index] == null)
            {
                this.internalTanks[index] = new FluidTank(FluidContainerRegistry.BUCKET_VOLUME);
            }
            PacketHandler.instance().sendPacketToClients(PacketHandler.instance().getPacket(DarkMain.CHANNEL, this, "SingleTank", index, this.internalTanks[index].getCapacity(), this.internalTanks[index].writeToNBT(new NBTTagCompound())));
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getRenderBoundingBox()
    {
        return AxisAlignedBB.getAABBPool().getAABB(this.xCoord, this.yCoord, this.zCoord, this.xCoord + 1, this.yCoord + 1, this.zCoord + 1);
    }

    public int getSubID()
    {
        return this.subID;
    }

}
