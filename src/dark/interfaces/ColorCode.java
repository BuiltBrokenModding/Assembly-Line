package dark.interfaces;

public enum ColorCode
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

    private ColorCode(String name)
    {
        this.name = name;
    }

    public String getName()
    {
        return this.name;
    }

    /** gets a ColorCode from any of the following
     *
     * @param obj - Integer,String,LiquidData,ColorCode
     * @return Color NONE if it can't find it */
    public static ColorCode get(Object obj)
    {
        if (obj instanceof Integer && ((Integer) obj) < ColorCode.values().length)
        {
            return ColorCode.values()[((Integer) obj)];
        }
        else if (obj instanceof ColorCode)
        {
            return (ColorCode) obj;
        }
        else if (obj instanceof String)
        {
            for (int i = 0; i < ColorCode.values().length; i++)
            {
                if (((String) obj).equalsIgnoreCase(ColorCode.get(i).getName()))
                {
                    return ColorCode.get(i);
                }
            }
        }
        return NONE;
    }

    public static interface IColorCoded
    {
        /** Returns the ColorCode of the object */
        public ColorCode getColor();

        /** Sets the ColorCode of the Object */
        public void setColor(Object obj);
    }

}
