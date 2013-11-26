package dark.core.common.machines;

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;
import dark.core.prefab.machine.TileEntityMachine;

/** Simple steam gen designed to burn items to create steam to power a steam device directly above it
 * 
 * @author DarkGuardsman */
public class TileEntitySteamGen extends TileEntityMachine implements IFluidHandler
{
    /** The number of ticks that a fresh copy of the currently-burning item would keep the furnace
     * burning for */
    public int itemCookTime = 0;

    protected final int HEAT_TIME = 100, WATER_CONSUME_TIME = 100, WATER_CONSUME_SUM = 10;
    protected int heatTicks = 0, waterTicks = 0;

    protected boolean steamMachineConnected = false, isHeated = false, creatingSteam = false;

    protected FluidTank tank = new FluidTank(2 * FluidContainerRegistry.BUCKET_VOLUME);

    @Override
    public void updateEntity()
    {
        super.updateEntity();

        TileEntity entity = this.worldObj.getBlockTileEntity(xCoord, yCoord + 1, zCoord);
        if (itemCookTime > 0)
        {
            itemCookTime--;
        }
        else
        {
            heatTicks--;
        }
        if (entity instanceof TileEntitySteamPiston)
        {
            steamMachineConnected = true;
            if (itemCookTime < 10)
            {
                //TODO consume an item to keep us running
            }
            if (itemCookTime > 0 && this.heatTicks < HEAT_TIME)
            {
                heatTicks++;
            }
            if (this.isFunctioning())
            {
                if (this.tank != null && this.tank.getFluid() != null && this.tank.getFluidAmount() > WATER_CONSUME_SUM && this.tank.getFluid().getFluid() == FluidRegistry.WATER)
                {
                    waterTicks++;
                    if (waterTicks % WATER_CONSUME_TIME == 0)
                    {
                        this.tank.drain(10, true);
                    }
                }
                else
                {
                    //TODO start heating up machine and blow it up if left without water for too long
                }
            }
        }
        else
        {
            steamMachineConnected = false;
        }
    }

    public boolean isCreatingSteam()
    {
        return creatingSteam;
    }

    @Override
    public boolean canFunction()
    {
        return super.canFunction() && itemCookTime > 0 && steamMachineConnected && isHeated;
    }

    @Override
    public int fill(ForgeDirection from, FluidStack resource, boolean doFill)
    {
        if(resource != null && resource.getFluid().equals(FluidRegistry.WATER))
        {
            this.tank.fill(resource, doFill);
        }
        return 0;
    }

    @Override
    public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain)
    {
        return null;
    }

    @Override
    public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain)
    {
        return null;
    }

    @Override
    public boolean canFill(ForgeDirection from, Fluid fluid)
    {
        return fluid != null && fluid.equals(FluidRegistry.WATER);
    }

    @Override
    public boolean canDrain(ForgeDirection from, Fluid fluid)
    {
        return false;
    }

    @Override
    public FluidTankInfo[] getTankInfo(ForgeDirection from)
    {
        return new FluidTankInfo[] { this.tank.getInfo() };
    }

}