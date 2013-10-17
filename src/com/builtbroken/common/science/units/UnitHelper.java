package com.builtbroken.common.science.units;

import java.util.ArrayList;
import java.util.List;

import com.builtbroken.common.Pair;

public class UnitHelper
{
    public static final float FEET_TO_METERS = 0.3048f;
    public static final float METERS_TO_FEET = 3.28084f;

    public static List<Character> numbers = new ArrayList<Character>();

    static
    {
        numbers.add('0');
        numbers.add('1');
        numbers.add('2');
        numbers.add('3');
        numbers.add('4');
        numbers.add('5');
        numbers.add('6');
        numbers.add('7');
        numbers.add('8');
        numbers.add('9');
    }

    public float convert(ImperialUnits a, MetricUnit b, float value)
    {
        return b.convert(MetricUnit.BASE, a.convert(ImperialUnits.foot, value) * FEET_TO_METERS);

    }

    public float convert(MetricUnit a, ImperialUnits b, float value)
    {
        return b.convert(ImperialUnits.foot, a.convert(MetricUnit.BASE, value) * METERS_TO_FEET);
    }

    public Pair<Object, Float> parseString(String input)
    {
        Pair<Object, Float> def = null;
        if (input != null && !input.isEmpty())
        {
            String editedString = input;
            char[] chars = input.toCharArray();
            Object unitEnumValue = null;

            String numberAsString = "";
            float number = 0;
            String units = "";
            int toPowerOf = 1;
            int timeTenToPowerOF = 1;

            //Get number first
            for (int i = 0; i < chars.length; i++)
            {
                char c = chars[i];
                if (numbers.contains(c))
                {
                    numberAsString += c;
                }
                else
                {
                    break;
                }
            }
            try
            {
                number = Float.parseFloat(numberAsString);
            }
            catch (Exception e)
            {

            }
            editedString.replaceAll("[0-9]", "");
            chars = editedString.toCharArray();
            //Check if number is being times by 10 to the power of something
            if (chars != null)
            {
                if (chars.length >= 5 && chars[0] == 'x' && chars[1] == '1' && chars[2] == '0' && chars[3] == '^' && numbers.contains(chars[4]))
                {
                    timeTenToPowerOF = Integer.parseInt("" + chars[4], 1);
                    editedString = editedString.substring(5);
                }
                else if (chars.length >= 2 && chars[0] == '^' && numbers.contains(chars[1]))
                {
                    toPowerOf = Integer.parseInt("" + chars[1], 1);
                    editedString = editedString.substring(2);
                }
            }

            //TODO detect units
            return new Pair<Object, Float>(unitEnumValue, number);
        }

        return def;
    }

    /** Tries to parse a value that may be anything.
     *
     * @param var - String, Integer, Float, Double
     * @param suggestValue - Used by string parsing in case it fails and you want to return a
     * default value */
    public static int tryToParseInt(Object var, int suggestValue)
    {
        if (var instanceof String)
        {
            try
            {
                return Integer.parseInt((String) var);
            }
            catch (Exception e)
            {

            }
        }
        if (var instanceof Integer || var instanceof Float || var instanceof Double)
        {
            return (int) var;
        }

        return suggestValue;
    }

    /** Tries to parse a value that may be anything.
     *
     * @param var - String, Integer, Float, Double */
    public static int tryToParseInt(Object var)
    {
        return tryToParseInt(var, 0);
    }

    /** Tries to parse a value that may be anything.
     *
     * @param var - String, Integer, Float, Double
     * @param suggestValue - Used by string parsing in case it fails and you want to return a
     * default value */
    public static Double tryToParseDouble(Object var, double suggestValue)
    {
        if (var instanceof String)
        {
            try
            {
                return Double.parseDouble((String) var);
            }
            catch (Exception e)
            {

            }
        }
        if (var instanceof Integer || var instanceof Float || var instanceof Double)
        {
            return (Double) var;
        }

        return suggestValue;
    }

    /** Tries to parse a value that may be anything.
     *
     * @param var - String, Integer, Float, Double */
    public static Double tryToParseDouble(Object var)
    {
        return tryToParseDouble(var, 0);
    }

    /** Tries to parse a value that may be anything.
     *
     * @param var - String, Integer, Float, Double
     * @param suggestValue - Used by string parsing in case it fails and you want to return a
     * default value */
    public static Float tryToParseFloat(Object var, float suggestValue)
    {
        if (var instanceof String)
        {
            try
            {
                return Float.parseFloat((String) var);
            }
            catch (Exception e)
            {

            }
        }
        if (var instanceof Integer || var instanceof Float || var instanceof Double)
        {
            return (Float) var;
        }

        return suggestValue;
    }

    /** Tries to parse a value that may be anything.
     *
     * @param var - String, Integer, Float, Double
     * @return Zero if it fails to parse the value */
    public static Float tryToParseFloat(Object var)
    {
        return tryToParseFloat(var, 0);
    }
}
