package net.minecraft.src.eui;

import net.minecraft.src.universalelectricity.UEIConsumer;

public class TileEntityNuller extends TileEntityMachine implements UEIConsumer {

	@Override
	public int onReceiveElectricity(int watts, int voltage, byte side) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean canReceiveElectricity(byte side) {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public int getStoredElectricity() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getElectricityCapacity() {
		// TODO Auto-generated method stub
		return 100000;
	}

	@Override
	public int getVolts() {
		// TODO Auto-generated method stub
		return 100000000;
	}

	@Override
	public void onDisable(int duration) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isDisabled() {
		// TODO Auto-generated method stub
		return false;
	}

}
