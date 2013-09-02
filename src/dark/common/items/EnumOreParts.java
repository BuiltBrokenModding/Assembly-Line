package dark.common.items;

public enum EnumOreParts
{

    RUBBLE("Rubble", 0),
    DUST("Dust", 20),
    INGOTS("Ingot", 40),
    PLATES("Plate", 60),
    GEARS("Gears", 80),
    TUBE("Tube", 100),
    ROD("Rod", 120),
    SCRAPS("Scraps", 140);

    public int meta;
    public String name;

    private EnumOreParts(String name, int meta)
    {
        this.meta = meta;
        this.name = name;
    }

    public static String getPartName(int meta)
    {
        if (meta < (EnumOreParts.values().length * 20))
        {
            return EnumOreParts.values()[meta / 20].name;
        }
        return "";
    }

    public static String getFullName(int meta)
    {
        if (meta < (EnumOreParts.values().length * 20))
        {
            return EnumMeterials.values()[meta % 20].name + EnumOreParts.values()[meta / 20].name;
        }
        return "";
    }
}
