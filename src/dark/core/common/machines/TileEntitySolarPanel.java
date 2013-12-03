package dark.core.common.machines;

import java.util.EnumSet;

import micdoodle8.mods.galacticraft.api.world.ISolarLevel;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.ForgeDirection;
import universalelectricity.core.electricity.ElectricityPack;
import dark.core.prefab.machine.TileEntityEnergyMachine;

public class TileEntitySolarPanel extends TileEntityEnergyMachine
{
    protected float wattOutput = 0;

    public TileEntitySolarPanel()
    {
        super(0, 1);
    }

    @Override
    public void updateEntity()
    {
        super.updateEntity();
        if (!this.worldObj.isRemote && this.ticks % BlockSolarPanel.tickRate == 0)
        {

            if (this.worldObj.canBlockSeeTheSky(xCoord, yCoord + 1, zCoord) && !this.worldObj.provider.hasNoSky)
            {
                if (this.worldObj.isDaytime())
                {
                    this.wattOutput = BlockSolarPanel.wattDay;
                    if (this.worldObj.isThundering() || this.worldObj.isRaining())
                    {
                        this.wattOutput = BlockSolarPanel.wattStorm;
                    }
                }
                else
                {
                    this.wattOutput = BlockSolarPanel.wattNight;
                    if (this.worldObj.isThundering() || this.worldObj.isRaining())
                    {
                        this.wattOutput = 0;
                    }
                }
                this.wattOutput += this.wattOutput * (this.worldObj.provider instanceof ISolarLevel ? (int) ((ISolarLevel) this.worldObj.provider).getSolarEnergyMultiplier() : 0);
            }
            else
            {
                wattOutput = 0;
            }
            this.produceAllSides();
        }

    }

    @Override
    public EnumSet<ForgeDirection> getOutputDirections()
    {
        return EnumSet.allOf(ForgeDirection.class);
    }

    @Override
    public float getRequest(ForgeDirection direction)
    {
        return 0;
    }

    @Override
    public float getProvide(ForgeDirection direction)
    {
        return this.wattOutput;
    }

    @Override
    public float receiveElectricity(ElectricityPack receive, boolean doReceive)
    {
        return 0;
    }

    @Override
    public float getVoltage()
    {
        return 0.060F;
    }
}
