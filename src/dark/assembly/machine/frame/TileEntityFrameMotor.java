package dark.assembly.machine.frame;

import dark.core.prefab.machine.TileEntityEnergyMachine;

public class TileEntityFrameMotor extends TileEntityEnergyMachine
{
    /** Number of times this motor revolves a second of run time */
    private float revolutionsASecond = 10;
    /** Max limit of blocks this motor can move */
    private float getStrenght = 20;
}
