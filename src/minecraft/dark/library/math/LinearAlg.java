package dark.library.math;

import universalelectricity.core.vector.Vector3;
import dark.library.helpers.Pair;

public class LinearAlg
{
	public Pair<Double, Double> vecToSphereAngles(Vector3 vec)
	{
		double radius = Math.sqrt((vec.x * vec.x) + (vec.y * vec.y) + (vec.z * vec.z));
		double inclination = Math.acos(vec.z / radius);
		double azimuth = Math.atan(vec.y / vec.z);
		return new Pair<Double, Double>(inclination, azimuth);
	}

	public Vector3 sphereAnglesToVec(Double radius, Double inclination, Double azimuth)
	{
		double x = radius * Math.sin(inclination) * Math.cos(azimuth);
		double y = radius * Math.sin(inclination) * Math.sin(azimuth);
		double z = radius * Math.cos(inclination);

		return new Vector3(x, y, z);
	}
}
