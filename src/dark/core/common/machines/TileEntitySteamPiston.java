package dark.core.common.machines;

import java.util.EnumSet;

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.ForgeDirection;
import dark.core.prefab.machine.TileEntityEnergyMachine;

public class TileEntitySteamPiston extends TileEntityEnergyMachine
{
    protected float wattPerSteam = 32.0f;
    protected float maxWattOutput = 500f;
    protected float maxSteamInput = 50f;
    protected float wattsOut = 0;
    protected int heatUpTime = 100;
    protected int heatTicks = 0;

    public TileEntitySteamPiston()
    {
        super(0, 0);
    }

    @Override
    public void updateEntity()
    {
        super.updateEntity();

        if (this.isFunctioning())
        {
            if (heatTicks < heatUpTime)
            {
                heatTicks++;
            }
            this.wattsOut = this.MAX_JOULES_STORED * (heatTicks / heatUpTime);
            this.produceAllSides();
        }
        else
        {
            if (heatTicks > 0)
            {
                heatTicks--;
            }
        }

    }

    @Override
    public float getRequest(ForgeDirection direction)
    {
        return 0;
    }

    @Override
    public float getProvide(ForgeDirection direction)
    {
        return this.wattsOut;
    }

    public boolean consumePower(float watts, boolean doDrain)
    {
        return true;
    }

    @Override
    public boolean canFunction()
    {
        TileEntity ent = this.worldObj.getBlockTileEntity(xCoord, yCoord - 1, zCoord);
        return super.canFunction() && ent instanceof TileEntitySteamGen && ((TileEntitySteamGen) ent).isCreatingSteam();
    }

    protected void updateAnimation()
    {

    }

    public EnumSet<ForgeDirection> getInputDirections()
    {
        return EnumSet.noneOf(ForgeDirection.class);
    }

    public EnumSet<ForgeDirection> getOutputDirections()
    {
        return EnumSet.allOf(ForgeDirection.class);
    }

}
