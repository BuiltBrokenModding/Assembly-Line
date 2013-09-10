package dark.fluid.common.machines;

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;
import dark.core.interfaces.ColorCode;
import dark.core.interfaces.ColorCode.IColorCoded;
import dark.fluid.common.prefab.TileEntityFluidDevice;

public class TileEntityBoiler extends TileEntityFluidDevice implements IFluidHandler
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
        if(from == ForgeDirection.DOWN)
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

}
