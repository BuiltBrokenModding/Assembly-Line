package dark.library.helpers;


public class MetaGroup
{    
    public static int getFacingMeta(int metaData)
    {
        int meta = metaData % 4;
        int newMeta = 0;
        switch (meta)
        {
            case 0:
                newMeta = 2;
                break;
            case 1:
                newMeta = 5;
                break;
            case 2:
                newMeta = 3;
                break;
            case 3:
                newMeta = 4;
        }

        return newMeta;
    }

    public static int getGrouping(int meta)
    {
        if ((meta >= 0) && (meta <= 3))
            return 0;
        if ((meta >= 4) && (meta <= 7))
            return 1;
        if ((meta >= 8) && (meta <= 11))
            return 2;
        if ((meta >= 12) && (meta <= 15))
            return 3;
        return 0;
    }

    public static int getGroupStartMeta(int grouping)
    {
        return grouping * 4;
    }
   
}