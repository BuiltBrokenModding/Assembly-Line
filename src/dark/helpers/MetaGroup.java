package dark.helpers;

/**
 * Used by machines that only rotate to 4 directions
 * 
 * @author DarkGuardsman *
 */
public class MetaGroup
{
	/**
	 * Gets minecraft style facing direction base
	 * 
	 * @param metaData - block metadata based on 4 meta rotation
	 * @return 2,5,3,4
	 */
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

	/**
	 * Gets the block's group
	 */
	public static int getGrouping(int meta)
	{
		return meta % 4;
	}
	/**
	 * Gets the starting meta of a group
	 * @param grouping - 4 meta group base
	 * @return metadata
	 */
	public static int getGroupStartMeta(int grouping)
	{
		return grouping * 4;
	}

}