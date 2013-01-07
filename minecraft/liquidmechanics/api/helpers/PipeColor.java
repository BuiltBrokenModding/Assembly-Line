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
    WHITE("White"),
    ORANGE("Orange"),
    NONE("");
    
    String name;

    private PipeColor(String name)
    {
        this.name = name;
    }

    public String getName()
    {
        return this.name;
    }

    /** gets a pipeColor from any of the following
     * 
     * @param obj
     *            - Integer,String,LiquidData,PipeColor
     * @return Color NONE if it can't find it */
    public static PipeColor get(Object obj)
    {
        if (obj instanceof Integer && ((Integer) obj) < PipeColor.values().length)
        {
            return PipeColor.values()[((Integer) obj)];
        } else if (obj instanceof LiquidData)
        {
            LiquidData data = (LiquidData) obj;
            if (data == LiquidHandler.lava) { return RED; }
            if (data == LiquidHandler.steam) { return ORANGE; }
            if (data == LiquidHandler.water) { return BLUE; }
        } else if (obj instanceof PipeColor)
        {
            return (PipeColor) obj;
        } else if (obj instanceof String)
        {
            for (int i = 0; i < PipeColor.values().length; i++)
            {
                if (((String) obj).equalsIgnoreCase(PipeColor.get(i).getName())) { return PipeColor.get(i); }
            }
        }
        return NONE;
    }

    /** gets the liquidData linked with this color. in rare cases there could be
     * more than one, but first instance will be returned */
    public LiquidData getLiquidData()
    {
        for (LiquidData data : LiquidHandler.allowedLiquids)
        {
            if (data.getColor() == this) { return data; }
        }
        return LiquidHandler.unkown;
    }
}
