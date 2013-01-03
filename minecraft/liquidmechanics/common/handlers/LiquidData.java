package liquidmechanics.common.handlers;

import net.minecraftforge.liquids.LiquidStack;

public class LiquidData
{
    private boolean isAGas;
    private int defaultPresure;
    private LiquidStack sampleStack;
    private String name;

    public LiquidData(String name, LiquidStack stack, boolean gas, int dPressure)
    {
        this.sampleStack = stack;
        this.isAGas = gas;
        this.defaultPresure = dPressure;
        this.name = name;
    }

    public static String getName(LiquidData type)
    {
        if (type != null) { return type.name; }
        return "unknown";
    }
    public static int getPressure(LiquidData type)
    {
        if (type != null) { return type.defaultPresure; }
        return 0;
    }
    public static LiquidStack getStack(LiquidData type)
    {
        if (type != null) { return type.sampleStack; }
        return new LiquidStack(0,1);
    }
    public static boolean getCanFloat(LiquidData type)
    {
        if (type != null) { return type.isAGas; }
        return false;
    }
}
