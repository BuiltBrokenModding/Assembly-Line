package dark.core.common.items;

public enum EnumOrePart
{

    RUBBLE("Rubble"),
    DUST("Dust"),
    INGOTS("Ingot"),
    PLATES("Plate"),
    GEARS("Gears"),
    TUBE("Tube"),
    ROD("Rod"),
    SCRAPS("Scraps");

    public String simpleName;

    private EnumOrePart(String name)
    {
        this.simpleName = name;
    }

    public static String getPartName(int meta)
    {
        int partID = meta % EnumMaterial.itemCountPerMaterial;
        if (partID < EnumOrePart.values().length)
        {
            return EnumOrePart.values()[partID].simpleName;
        }
        return "Part[" + partID + "]";
    }

    public static String getFullName(int meta)
    {
        int matID = meta / EnumMaterial.itemCountPerMaterial;
        int partID = meta % EnumMaterial.itemCountPerMaterial;
        if (matID < EnumMaterial.values().length && partID < EnumOrePart.values().length)
        {
            return EnumMaterial.values()[matID].simpleName + EnumOrePart.values()[partID].simpleName;
        }
        return "OrePart[" + matID + "][" + partID + "]";
    }
}
