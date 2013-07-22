package dark.core.helpers;

import universalelectricity.core.vector.Vector3;

public enum Direction
{
	/** -Y */
	DOWN(0, -1, 0),

	/** +Y */
	UP(0, 1, 0),

	/** -Z */
	NORTH(0, 0, -1),

	/** +Z */
	SOUTH(0, 0, 1),

	/** -X */
	WEST(-1, 0, 0),

	/** +X */
	EAST(1, 0, 0),

	/** -X +Y */
	NORTH_UP(0, 1, -1),

	/** +X +Y */
	SOUTH_UP(0, 1, 1),

	/** -X +Y */
	WEST_UP(-1, 1, 0),

	/** +Z +Y */
	EAST_UP(1, 1, 0),

	/** -X -Y */
	NORTH_DOWN(0, -1, -1),

	/** +X -Y */
	SOUTH_DOWN(0, -1, 1),

	/** -X -Y */
	WEST_DOWN(-1, -1, 0),

	/** +Z -Y */
	EAST_DOWN(1, -1, 0),

	/** Used only by getOrientation, for invalid inputs */
	UNKNOWN(0, 0, 0);
	public static final Direction[] VALID_DIRECTIONS = { DOWN, UP, NORTH, SOUTH, WEST, EAST, NORTH_UP, SOUTH_UP, WEST_UP, EAST_UP, NORTH_DOWN, SOUTH_DOWN, WEST_DOWN, EAST_DOWN };

	public final int offsetX;
	public final int offsetY;
	public final int offsetZ;

	public static final int[] OPPOSITES = { 1, 0, 3, 2, 5, 4, 6 };

	private Direction(int x, int y, int z)
	{
		offsetX = x;
		offsetY = y;
		offsetZ = z;
	}

	public static Direction getOrientation(int id)
	{
		if (id >= 0 && id < VALID_DIRECTIONS.length)
		{
			return VALID_DIRECTIONS[id];
		}
		return UNKNOWN;
	}

	public static Vector3 modifyPositionFromSide(Vector3 vec, Direction side, double amount)
	{
		double x = amount * side.offsetX;
		double y = amount * side.offsetY;
		double z = amount * side.offsetZ;

		return new Vector3(vec.x + x, vec.y + y, vec.z + z);

	}
}
