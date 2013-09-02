package dark.prefab.helpers;

import java.util.Random;

import universalelectricity.core.vector.Vector3;

public class MathHelper extends net.minecraft.util.MathHelper
{
    /** Generates an array of random numbers
     *
     * @param random - random instance to be used
     * @param maxNumber - max size of the int to use
     * @param arraySize - length of the array
     * @return array of random numbers */
    public static int[] generateRandomIntArray(Random random, int maxNumber, int arraySize)
    {
        return MathHelper.generateRandomIntArray(random, 0, maxNumber, arraySize);
    }

    /** Generates an array of random numbers
     *
     * @param random - random instance to be used
     * @param minNumber - smallest random Integer to use. Warning can lead to longer than normal
     * delay in returns
     * @param maxNumber - max size of the int to use
     * @param arraySize - length of the array
     * @return array of random numbers */
    public static int[] generateRandomIntArray(Random random, int minNumber, int maxNumber, int arraySize)
    {
        int[] array = new int[arraySize];
        for (int i = 0; i < array.length; i++)
        {
            int number = random.nextInt(maxNumber);
            if (minNumber != 0)
            {
                while (number < minNumber)
                {
                    number = random.nextInt(maxNumber);
                }
            }
            array[i] = number;
        }
        return array;
    }

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
