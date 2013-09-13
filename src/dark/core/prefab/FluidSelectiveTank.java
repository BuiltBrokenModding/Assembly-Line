package dark.core.prefab;

import java.util.ArrayList;
import java.util.List;

import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;

/** Selective fluid version of the FluidTank
 * 
 * @author DarkGuardsman */
public class FluidSelectiveTank extends FluidTank
{
    protected List<Fluid> fluidList = new ArrayList<Fluid>();

    public FluidSelectiveTank(int capacity)
    {
        super(capacity);
    }

    public FluidSelectiveTank(FluidStack stack, int capacity)
    {
        super(stack, capacity);
    }

    public FluidSelectiveTank(Fluid fluid, int amount, int capacity)
    {
        super(fluid, amount, capacity);
    }

    public FluidSelectiveTank setFluidList(List<Fluid> fluid)
    {
        this.fluidList = fluid;
        return this;
    }

    public FluidSelectiveTank addFluidToList(Fluid fluid)
    {
        if (fluid != null)
        {
            this.getList().add(fluid);
        }
        return this;
    }

    public List<Fluid> getList()
    {
        if (this.fluidList == null)
        {
            this.fluidList = new ArrayList<Fluid>();
        }
        return this.fluidList;
    }

    public boolean canAcceptFluid(Fluid fluid)
    {
        return fluid != null && this.getList().contains(fluid);
    }
}
