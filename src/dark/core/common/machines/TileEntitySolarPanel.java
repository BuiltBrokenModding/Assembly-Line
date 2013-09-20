package dark.core.common.machines;

import java.util.EnumSet;

import universalelectricity.core.electricity.ElectricityPack;

import net.minecraft.item.ItemStack;
import net.minecraftforge.common.ForgeDirection;
import dark.core.prefab.machine.TileEntityEnergyMachine;

public class TileEntitySolarPanel extends TileEntityEnergyMachine
{
    public TileEntitySolarPanel()
    {
        this.MAX_WATTS = 1;
    }

    public void updateEntity()
    {
        super.updateEntity();
        if (this.ticks % BlockSolarPanel.tickRate == 0)
        {
            this.produceAllSides();
        }

    }

    public EnumSet<ForgeDirection> getOutputDirections()
    {
        return EnumSet.of(ForgeDirection.DOWN);
    }

    @Override
    public void discharge(ItemStack itemStack)
    {

    }

    @Override
    public float getRequest(ForgeDirection direction)
    {
        return 0;
    }

    @Override
    public float receiveElectricity(ElectricityPack receive, boolean doReceive)
    {
        return 0;
    }
}
