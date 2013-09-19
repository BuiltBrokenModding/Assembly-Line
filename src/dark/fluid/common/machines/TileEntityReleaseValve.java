package dark.fluid.common.machines;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidHandler;
import universalelectricity.core.vector.Vector3;
import dark.api.ColorCode;
import dark.api.IToolReadOut;
import dark.api.ColorCode.IColorCoded;
import dark.api.fluid.INetworkPipe;
import dark.api.parts.ITileConnector;
import dark.core.prefab.helpers.ConnectionHelper;
import dark.core.prefab.tilenetwork.fluid.NetworkPipes;
import dark.fluid.common.prefab.TileEntityFluidDevice;

public class TileEntityReleaseValve extends TileEntityFluidDevice implements ITileConnector, IToolReadOut
{
    public boolean[] allowed = new boolean[ColorCode.values().length - 1];
    public TileEntity[] connected = new TileEntity[6];

    private List<INetworkPipe> output = new ArrayList<INetworkPipe>();
    private IFluidHandler[] input = new IFluidHandler[6];

    @Override
    public void updateEntity()
    {
        super.updateEntity();

        connected = ConnectionHelper.getSurroundingTileEntities(this);

        for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS)
        {
            if (connected[dir.ordinal()] instanceof IFluidHandler)
            {
                if (connected[dir.ordinal()] instanceof IColorCoded && !this.canConnect(((IColorCoded) connected[dir.ordinal()]).getColor()))
                {
                    connected[dir.ordinal()] = null;
                }
            }
            else
            {
                connected[dir.ordinal()] = null;
            }
        }

        if (!this.worldObj.isRemote && this.ticks % 10 == 0 && !worldObj.isBlockIndirectlyGettingPowered(xCoord, yCoord, zCoord))
        {
            this.validateNBuildList();
            // start the draining process
            for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS)
            {
                IFluidHandler drainedTank = input[dir.ordinal()];
                if (drainedTank != null)
                {
                    FluidStack stack = drainedTank.drain(dir.getOpposite(), FluidContainerRegistry.BUCKET_VOLUME, false);
                    if (stack != null && stack.amount > 0)
                    {
                        INetworkPipe inputPipe = this.findValidPipe(stack);
                        if (inputPipe != null)
                        {
                            int ammountFilled = ((NetworkPipes) inputPipe.getTileNetwork()).addFluidToNetwork((TileEntity) drainedTank, stack, true);
                            drainedTank.drain(ForgeDirection.UNKNOWN, ammountFilled, true);
                        }
                    }
                }
            }
        }
    }

    /** used to find a valid pipe for filling of the liquid type */
    public INetworkPipe findValidPipe(FluidStack stack)
    {
        // find normal color selective pipe first
        for (INetworkPipe pipe : output)
        {
            if (pipe.fill(ForgeDirection.UNKNOWN, stack, false) > 0)
            {
                return pipe;
            }
        }

        return null;
    }

    /** sees if it can connect to a pipe of some color */
    public boolean canConnect(ColorCode cc)
    {
        if (this.isRestricted())
        {
            for (int i = 0; i < this.allowed.length; i++)
            {
                if (i == cc.ordinal())
                {
                    return allowed[i];
                }
            }
        }
        return true;
    }

    /** if any of allowed list is true
     * 
     * @return true */
    public boolean isRestricted()
    {
        for (int i = 0; i < this.allowed.length; i++)
        {
            if (allowed[i])
            {
                return true;
            }
        }
        return false;
    }

    /** Collects info about the surrounding 6 tiles and orders them into drain-able(ITankContainer)
     * and fill-able(TileEntityPipes) instances */
    public void validateNBuildList()
    {
        // cleanup
        this.connected = ConnectionHelper.getSurroundingTileEntities(worldObj, xCoord, yCoord, zCoord);
        this.input = new IFluidHandler[6];
        this.output.clear();
        // read surroundings
        for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS)
        {
            TileEntity tileEntity = connected[dir.ordinal()];
            if (tileEntity instanceof INetworkPipe)
            {
                INetworkPipe pipe = (INetworkPipe) tileEntity;
                if (this.canConnect(pipe.getColor()))
                {
                    this.output.add(pipe);
                }
                else
                {
                    this.connected[dir.ordinal()] = null;
                }
            }
            else if (tileEntity instanceof IFluidHandler)
            {
                IFluidHandler tank = (IFluidHandler) tileEntity;
                if (tank != null && tank.drain(dir.getOpposite(), FluidContainerRegistry.BUCKET_VOLUME, false) != null)
                {
                    this.input[dir.ordinal()] = (IFluidHandler) tileEntity;
                }
            }
            else
            {
                connected[dir.ordinal()] = null;
            }
        }
    }

    @Override
    public boolean canTileConnect(Connection type, ForgeDirection dir)
    {
        TileEntity entity = new Vector3(this).modifyPositionFromSide(dir).getTileEntity(this.worldObj);
        return entity != null && entity instanceof IFluidHandler && entity instanceof IColorCoded && this.canConnect(((IColorCoded) entity).getColor());
    }

    @Override
    public String getMeterReading(EntityPlayer user, ForgeDirection side, EnumTools tool)
    {
        // TODO maybe debug on # of connected units of input/output
        String output = "";
        if (this.isRestricted())
        {
            output += "Output: Restricted and";
        }
        else
        {
            output += " Output: UnRestricted and";
        }
        if (!worldObj.isBlockIndirectlyGettingPowered(xCoord, yCoord, zCoord))
        {
            output += " Open ";
        }
        else
        {
            output += " Closed ";
        }
        return output;
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt)
    {
        super.readFromNBT(nbt);
        for (int i = 0; i < this.allowed.length; i++)
        {
            allowed[i] = nbt.getBoolean("allowed" + i);
        }
    }

    /** Writes a tile entity to NBT. */
    @Override
    public void writeToNBT(NBTTagCompound nbt)
    {
        super.writeToNBT(nbt);
        for (int i = 0; i < this.allowed.length; i++)
        {
            nbt.setBoolean("allowed" + i, allowed[i]);
        }
    }
}
