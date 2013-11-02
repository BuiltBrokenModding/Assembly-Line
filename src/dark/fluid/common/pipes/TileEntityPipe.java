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
import dark.api.fluid.FluidMasterList;
import dark.api.fluid.INetworkPipe;
import dark.api.parts.ITileConnector;
import dark.core.common.DarkMain;
import dark.core.network.PacketHandler;
import dark.core.prefab.helpers.FluidHelper;
import dark.core.prefab.tilenetwork.NetworkTileEntities;
import dark.core.prefab.tilenetwork.fluid.NetworkPipes;
import dark.fluid.common.FluidPartsMaterial;
import dark.fluid.common.prefab.TileEntityFluidNetworkTile;

public class TileEntityPipe extends TileEntityFluidNetworkTile implements IColorCoded, INetworkPipe
{
    /** gets the current color mark of the pipe */
    @Override
    public ColorCode getColor()
    {
        return EnumPipeType.getColorCode(this.subID);
    }

    /** sets the current color mark of the pipe */
    @Override
    public boolean setColor(Object cc)
    {
        if (!worldObj.isRemote)
        {
            int p = this.subID;
            this.subID = EnumPipeType.getUpdatedID(subID, ColorCode.get(cc));
            return p != this.subID;
        }
        return false;
    }

    @Override
    public void validateConnectionSide(TileEntity tileEntity, ForgeDirection side)
    {
        super.validateConnectionSide(tileEntity, side);

        if (tileEntity instanceof TileEntityPipe)
        {
            int meta = new Vector3(this).getBlockMetadata(this.worldObj);
            int metaOther = new Vector3(tileEntity).getBlockMetadata(this.worldObj);
            if (meta < FluidPartsMaterial.values().length && metaOther < FluidPartsMaterial.values().length)
            {
                FluidPartsMaterial pipeMat = FluidPartsMaterial.values()[meta];
                FluidPartsMaterial pipeMatOther = FluidPartsMaterial.values()[metaOther];
                //Same pipe types can connect
                if (pipeMat == pipeMatOther)
                {
                    this.getTileNetwork().merge(((INetworkPipe) tileEntity).getTileNetwork(), this);
                    connectedBlocks.add(tileEntity);
                    this.renderConnection[side.ordinal()] = true;
                }//Wood and stone pipes can connect to each other but not other pipe types since they are more like a trough than a pipe
                else if ((pipeMat == FluidPartsMaterial.WOOD || pipeMat == FluidPartsMaterial.STONE) && (pipeMatOther == FluidPartsMaterial.WOOD || pipeMatOther == FluidPartsMaterial.STONE))
                {
                    this.getTileNetwork().merge(((INetworkPipe) tileEntity).getTileNetwork(), this);
                    connectedBlocks.add(tileEntity);
                    this.renderConnection[side.ordinal()] = true;
                }//Any other pipe can connect to each other as long as the color matches except for glass which only works with itself at the moment
                else if (pipeMat != FluidPartsMaterial.WOOD && pipeMat != FluidPartsMaterial.STONE && pipeMatOther != FluidPartsMaterial.WOOD && pipeMatOther != FluidPartsMaterial.STONE && pipeMat != FluidPartsMaterial.GLASS && pipeMatOther != FluidPartsMaterial.GLASS)
                {
                    this.getTileNetwork().merge(((INetworkPipe) tileEntity).getTileNetwork(), this);
                    connectedBlocks.add(tileEntity);
                    this.renderConnection[side.ordinal()] = true;
                }
            }
        }
        else if (tileEntity instanceof IFluidHandler)
        {
            connectedBlocks.add(tileEntity);
            this.renderConnection[side.ordinal()] = true;
        }

    }

    @Override
    public boolean onPassThrew(FluidStack fluid, ForgeDirection from, ForgeDirection to)
    {
        //TODO do checks for molten pipe so that fluids like water turn into steam, oils and fuels burn
        return super.onPassThrew(fluid, from, to);
    }

    @Override
    public double getMaxPressure(ForgeDirection side)
    {
        int meta = this.worldObj.getBlockMetadata(xCoord, yCoord, zCoord);
        if (meta < FluidPartsMaterial.values().length)
        {
            return FluidPartsMaterial.values()[meta].maxPressure;
        }
        return 350;
    }

    @Override
    public NetworkPipes getTileNetwork()
    {
        if (!(this.network instanceof NetworkPipes))
        {
            this.setTileNetwork(new NetworkPipes(this));
        }
        return (NetworkPipes) this.network;
    }

    @Override
    public void setTileNetwork(NetworkTileEntities network)
    {
        if (network instanceof NetworkPipes)
        {
            this.network = (NetworkPipes) network;
        }
    }

    @Override
    public int getMaxFlowRate(FluidStack stack, ForgeDirection side)
    {
        if (stack != null)
        {
            return this.calculateFlowRate(stack, 40, 20);
        }
        return BlockPipe.waterFlowRate;
    }

    /** Calculates flow rate based on viscosity & temp of the fluid as all other factors are know
     *
     * @param fluid - fluidStack
     * @param temp = tempature of the fluid
     * @param pressure - pressure difference of were the fluid is flowing too.
     * @return flow rate in mili-Buckets */
    public int calculateFlowRate(FluidStack fluid, float pressure, float temp)
    {
        //TODO recalculate this based on pipe material for friction
        if (fluid != null & fluid.getFluid() != null)
        {
            float f = .012772f * pressure;
            f = f / (8 * (fluid.getFluid().getViscosity() / 1000));
            return (int) (f * 1000);
        }
        return BlockPipe.waterFlowRate;
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
    public void sendTankUpdate(int index)
    {
        //TODO only send tank update for pipes that need to visually render the fluid, eg glass, stone, wood
    }
}
