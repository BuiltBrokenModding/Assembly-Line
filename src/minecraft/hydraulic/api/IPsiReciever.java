package hydraulic.api;

public interface IPsiReciever extends IPsiMachine
{
	/**
	 * Called when this machine receives pressure/Fluid
	 * Too get force take pressure x Surface area of you machine
	 * @param pressure - input pressure. make sure to output a pressure later on, plus remaining liquid
	 */
	public void onReceivePressure(double pressure);
}
