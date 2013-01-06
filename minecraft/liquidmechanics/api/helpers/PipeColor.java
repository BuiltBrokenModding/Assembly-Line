package liquidmechanics.api.helpers;

import liquidmechanics.common.handlers.LiquidData;
import liquidmechanics.common.handlers.LiquidHandler;

public enum PipeColor
{
    BLACK("Black"), 
    RED("Red"), 
    GREEN("Green"), 
    BROWN("Brown"),
    BLUE("Blue"), 
    PURPLE("Purple"), 
    CYAN("Cyan"), 
    SILVER("Silver"),
    GREY("Grey"), 
    PINK("Pink"), 
    LIME("Lime"), 
    YELLOW("Yellow"),
    LIGHTBLUE("LightBlue"), 
    MAGENTA("Magenta"), 
    ORANGE("Orange"), 
    NONE("");
    String name;

    PipeColor(String name)
    {
        this.name = name;
    }

    public String getName()
    {
        return this.name;
    }

    /**
     * get the liquidData linked with this color
     */
    public LiquidData getLiquidData()
    {
        for (LiquidData data : LiquidHandler.allowedLiquids)
        {
            if (data.getColor() == this) { return data; }
        }
        return LiquidHandler.unkown;
    }

    /**
     * gets a color based on liquid Data
     */
    public static PipeColor get(LiquidData data)
    {
        if (data == LiquidHandler.lava) { return RED; }
        if (data == LiquidHandler.steam) { return ORANGE; }
        if (data == LiquidHandler.water) { return BLUE; }
        return NONE;
    }

    /**
     * gets a color based on number(0-15)
     */
    public static PipeColor get(int num)
    {
        if (num < PipeColor.values().length) { return PipeColor.values()[num]; }
        return NONE;
    }
}
