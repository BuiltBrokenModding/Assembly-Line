package liquidmechanics.api.helpers;

import liquidmechanics.common.handlers.LiquidData;
import liquidmechanics.common.handlers.LiquidHandler;

public enum PipeColor
{    
    BLACK, RED, GREEN, BROWN, BLUE, PURPLE, CYAN, SILVER, GREY, PINK, LIME, YELLOW, LIGHTBLUE, MAGENTA, ORANGE, NONE;   
    
   /**
    * get the liquidData linked with this color
    */
    public LiquidData getLiquidData()
    {
        for(LiquidData data: LiquidHandler.allowedLiquids)
        {
            if(data.getColor() == this)
            {
                return data;
            }
        }
        return LiquidHandler.air;
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
        if(num < PipeColor.values().length)
        {
            return PipeColor.values()[num];
        }
        return NONE;
    }
}
