package universalelectricity.core.electricity;

public class ElectricityPack
{
	public double amperes;
	public double voltage;

	public ElectricityPack(double amperes, double voltage)
	{
		this.amperes = amperes;
		this.voltage = voltage;
	}

	public double getWatts()
	{
		return ElectricInfo.getWatts(amperes, voltage);
	}

	@Override
	public String toString()
	{
		return "ElectricityPack [Amps:" + this.amperes + " Volts:" + this.voltage + "]";
	}
}
