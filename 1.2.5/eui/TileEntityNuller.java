package net.minecraft.src.eui;

import net.minecraft.src.universalelectricity.electricity.IElectricUnit;


public class TileEntityNuller extends TileEntityMachine implements IElectricUnit {

	public float electricityRequest()
	{
		return 100;
	}
	public boolean canReceiveFromSide(byte side)
	{
		return true;
	}
	public float getVoltage()
	{
		return 1000;
	}
	public int getTickInterval()
	{
		return 1;
	}
	public boolean canConnect(byte side)
	{
		return true;
	}
}
