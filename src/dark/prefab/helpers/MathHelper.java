package dark.prefab.helpers;

import java.util.Random;

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
}
