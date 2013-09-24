package dark.core.common.machines;

import java.util.EnumSet;

import micdoodle8.mods.galacticraft.API.ISolarLevel;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.ForgeDirection;
import universalelectricity.core.electricity.ElectricityPack;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import dark.core.client.renders.RenderBlockSolarPanel;
import dark.core.prefab.machine.TileEntityEnergyMachine;

public class TileEntitySolarPanel extends TileEntityEnergyMachine
{
    float wattOutput = 0;

    public TileEntitySolarPanel()
    {
        this.MAX_WATTS = 1;
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
            System.out.println("Watts: " + this.wattOutput);
            this.produceAllSides();
        }

    }

    @Override
    public EnumSet<ForgeDirection> getOutputDirections()
    {
        return EnumSet.allOf(ForgeDirection.class);
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
    @SideOnly(Side.CLIENT)
    public TileEntitySpecialRenderer getClientTileEntityRenderer()
    {
        return new RenderBlockSolarPanel();
    }
}
