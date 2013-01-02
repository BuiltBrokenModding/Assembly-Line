package liquidmechanics.common;

public class MetaGroupingHelper

{
	public static int getFacingMeta(int metaData)

	{
		/* 20 */int meta = metaData % 4;
		/* 21 */int newMeta = 0;
		/* 22 */switch (meta)
		{
			case 0:
				/* 25 */newMeta = 2;
				/* 26 */break;
			case 1:
				/* 28 */newMeta = 5;
				/* 29 */break;
			case 2:
				/* 31 */newMeta = 3;
				/* 32 */break;
			case 3:
				/* 34 */newMeta = 4;
		}

		/* 38 */return newMeta;
	}

	public static int getGrouping(int meta)

	{
		/* 46 */if ((meta >= 0) && (meta <= 3))
			return 0;
		/* 47 */if ((meta >= 4) && (meta <= 7))
			return 1;
		/* 48 */if ((meta >= 8) && (meta <= 11))
			return 2;
		/* 49 */if ((meta >= 12) && (meta <= 15))
			return 3;
		/* 50 */return 0;
	}

	public static int getGroupStartMeta(int grouping)

	{
		/* 58 */return grouping * 4;
	}

}