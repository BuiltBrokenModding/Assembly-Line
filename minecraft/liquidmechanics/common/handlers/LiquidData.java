package liquidmechanics.common.handlers;

import net.minecraftforge.liquids.LiquidStack;

public class LiquidData
{
    public final boolean isAGas;
    public final int defaultPresure;
    public final LiquidStack sampleStack;

    public LiquidData(LiquidStack stack, boolean gas, int dPressure)
    {
        this.sampleStack = stack;
        this.isAGas = gas;
        this.defaultPresure = dPressure;
    }
}
