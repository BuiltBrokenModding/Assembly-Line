package com.builtbroken.common.science;

/** Helper class that does the work of basic formulas.
 * 
 * @Note: Unless stated other wise the formulas don't care what the units are as long as the match
 * up. Eg pressure can be in any unit as long as all pressures for the method are the same unit
 * 
 * @Note: Some of these methods are very simple but they remove the confusion involved in rewriting
 * the code for each use
 * 
 * @author Robert Seifert */
public class FormulaHelper
{
    public static final float ACCELERATION_EARTH = 9.80665f;
    public static final float ACCELERATION_MOON = 1.622f;

    /** Gay-Lussac's Law (P1/T1 = P2/T2)
     * 
     * @param pressure - original pressure
     * @param temp - original tempature
     * @param newTemp - new tempature
     * @return pressure */
    public static float getPressure(float pressure, float temp, float newTemp)
    {
        return getPressure(pressure, delta(temp, newTemp));
    }

    public static float getPressure(float pressure, float deltaTemp)
    {
        return pressure * deltaTemp;
    }

    public static float calcWeight(float mass, float gravity)
    {
        return mass * gravity;
    }

    public static float calcForce(float mass, float acceleration)
    {
        return mass * acceleration;
    }

    /** Gets the number of moles of the object from the mass of the object, and the materials
     * molarMass
     * 
     * @param molarMass - mass of one mole of the material
     * @param objectMass - mass of the object made of the material
     * @return number of moles of the material */
    public static float moles(float molarMass, float objectMass)
    {
        return objectMass / molarMass;
    }

    /** Calculates change from original value to new value
     * 
     * @param a - original value
     * @param b - new value
     * @return change in value from original to new */
    public static float delta(float a, float b)
    {
        //Yes i know its simple but it removes confusion
        return b - a;
    }
}
