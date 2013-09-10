package dark.fluid.common.machines;

import java.util.List;

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;
import net.minecraftforge.fluids.IFluidTank;
import dark.api.fluid.INetworkFluidPart;
import dark.core.interfaces.ColorCode;
import dark.core.prefab.tilenetwork.NetworkTileEntities;
import dark.fluid.common.prefab.TileEntityFluidDevice;

public class TileEntityBoiler extends TileEntityFluidDevice implements IFluidHandler, INetworkFluidPart
{

    public TileEntity[] connectedBlocks = new TileEntity[6];
    public int tankCount;

    FluidTank outputTank = new FluidTank(4);
    FluidTank inputTank = new FluidTank(4);

    @Override
    public boolean canFill(ForgeDirection from, Fluid fluid)
    {
        if (fluid != null)
        {
            if (inputTank.getFluid() == null || inputTank.getFluid().getFluid().equals(fluid))
            {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean canDrain(ForgeDirection from, Fluid fluid)
    {
        if (fluid != null)
        {
            if (from == ForgeDirection.DOWN)
            {
                //Bottom side is reserved for draining boiler of liquid when moving it
                if (inputTank.getFluid() != null && inputTank.getFluid().getFluid().equals(fluid))
                {
                    return true;
                }
            }
            else if (outputTank.getFluid() != null && fluid.equals(outputTank.getFluid().getFluid()))
            {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean canTileConnect(Connection type, ForgeDirection dir)
    {
        return true;
    }

    @Override
    public int fill(ForgeDirection from, FluidStack resource, boolean doFill)
    {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain)
    {
        if (from == ForgeDirection.DOWN)
        {

        }
        else
        {

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
    public FluidTankInfo[] getTankInfo(ForgeDirection from)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ColorCode getColor()
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setColor(Object obj)
    {
        // TODO Auto-generated method stub

    }

    @Override
    public List<TileEntity> getNetworkConnections()
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void refresh()
    {
        // TODO Auto-generated method stub

    }

    @Override
    public NetworkTileEntities getTileNetwork()
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setTileNetwork(NetworkTileEntities fluidNetwok)
    {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean mergeDamage(String result)
    {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public IFluidTank getTank(int index)
    {
        // TODO Auto-generated method stub
        return null;
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
    public int getNumberOfTanks()
    {
        return 2;
    }

}
