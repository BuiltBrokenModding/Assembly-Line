package dark.core.common.debug;

import java.util.EnumSet;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.ForgeDirection;
import dark.core.prefab.machine.TileEntityEnergyMachine;

public class TileEntityInfSupply extends TileEntityEnergyMachine implements IDebugTile
{

    @Override
    public void updateEntity()
    {
        super.updateEntity();

        if (!this.worldObj.isRemote)
        {
            //System.out.println("Inf power supply cycle " + this.ticks);
            this.produceAllSides();
            if (this.ticks % 10 == 0)
            {
                this.setEnergyStored(this.getEnergyStored() + (this.getProvide(ForgeDirection.UNKNOWN) * 10));
            }
        }
    }

    @Override
    public EnumSet<ForgeDirection> getOutputDirections()
    {
        return EnumSet.allOf(ForgeDirection.class);
    }

    @Override
    public boolean canUpdate()
    {
        return true;
    }

    @Override
    public boolean canConnect(ForgeDirection direction)
    {
        return true;
    }

    @Override
    public float getProvide(ForgeDirection direction)
    {
        // TODO Auto-generated method stub
        return 1000;
    }

    @Override
    public float getMaxEnergyStored()
    {
        return Integer.MAX_VALUE;
    }

    @Override
    public float getRequest(ForgeDirection direction)
    {
        return 0;
    }

    @Override
    public boolean onActivated(EntityPlayer entityPlayer)
    {
        // TODO Auto-generated method stub
        return false;
    }

}
