package dark.machines.common.machines;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.ForgeDirection;
import dark.core.prefab.machine.TileEntityEnergyMachine;

public class TileEntityInfLoad extends TileEntityEnergyMachine implements IDebugTile
{

    @Override
    public void updateEntity()
    {
        super.updateEntity();

        if (!this.worldObj.isRemote)
        {
            if (this.ticks % 1000 == 0)
            {
                this.setEnergyStored(0);
            }
        }
    }

    @Override
    public boolean canUpdate()
    {
        return true;
    }

    @Override
    public boolean canConnect(ForgeDirection direction)
    {
        //TODO add wrench settings to close sides for testing
        return true;
    }

    @Override
    public float getRequest(ForgeDirection direction)
    {
        //TODO add config options to change this for testing
        return 10000;
    }

    @Override
    public float getProvide(ForgeDirection direction)
    {
        return 0;
    }

    @Override
    public float getMaxEnergyStored()
    {
        return Integer.MAX_VALUE;
    }

    @Override
    public boolean onActivated(EntityPlayer entityPlayer)
    {
        // TODO Auto-generated method stub
        return false;
    }

}
