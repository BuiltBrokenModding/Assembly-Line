package dark.library.math;

import universalelectricity.core.vector.Vector3;

public class OrbitHelper
{
	/**
	 * Get the offset of the center of the circle and object will be if move around the edge of the
	 * circle so far
	 * 
	 * @param radus distance of the circle
	 * @param pos in the circle
	 * @return change from center of the circle
	 */
	public Vector3 getRadianPos(float radus, int pos, float spacing)
	{
		return null;
	}

	/**
	 * Gets the spacing in order to to have x number of the same width object orbit a point
	 * 
	 * @param radus of the circle
	 * @param width of the object including spacing
	 * @param number of objects
	 * @return spacing in radians of the circle
	 */
	public float getObjectSpacing(double radus, double width, double number)
	{
		return 0;
	}

	public Vector3 getCirclePos(int objects, double width, double radius)
	{
		if (radius < (width + (width / 2)) * objects)
		{
			radius = (width + width / 2) * objects;
		}
		float spacing = this.getObjectSpacing(radius, width, objects);
		return null;
	}
}
