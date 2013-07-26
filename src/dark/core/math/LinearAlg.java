package dark.core.math;

import universalelectricity.core.vector.Vector3;

public class LinearAlg
{
    /** @param vec - vector3 that is on the sphere
     * @return new Vector3(radius, inclination, azimuth) */
    public static Vector3 vecToSphereAngles(Vector3 vec)
    {
        double radius = Math.sqrt((vec.x * vec.x) + (vec.y * vec.y) + (vec.z * vec.z));
        double inclination = Math.acos(vec.z / radius);
        double azimuth = Math.atan(vec.y / vec.z);
        return new Vector3(radius, inclination, azimuth);
    }

    /** Turns radius and sphere cords into a vector3
     * 
     * @param radius - sphere radius
     * @param inclination -
     * @param azimuth
     * @return Vector3(x,y,z) */
    public static Vector3 sphereAnglesToVec(Double radius, Double inclination, Double azimuth)
    {
        double x = radius * Math.sin(inclination) * Math.cos(azimuth);
        double y = radius * Math.sin(inclination) * Math.sin(azimuth);
        double z = radius * Math.cos(inclination);

        return new Vector3(x, y, z);
    }
}
