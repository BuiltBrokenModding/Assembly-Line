package dark.api.fluid;

import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidHandler;
import net.minecraftforge.fluids.IFluidTank;
import dark.api.ColorCode.IColorCoded;
import dark.api.parts.INetworkPart;

public interface INetworkFluidPart extends IColorCoded, IFluidHandler, INetworkPart
{
    /** Gets an array of the fluid the tank can take */
    public int getNumberOfTanks();

    /** Gets the part's main tank for shared storage */
    public IFluidTank getTank(int index);

    /** Sets the content of the part's main tank */
    public int fillTankContent(int index, FluidStack stack, boolean doFill);

    public FluidStack drainTankContent(int index, int volume, boolean doDrain);
}
