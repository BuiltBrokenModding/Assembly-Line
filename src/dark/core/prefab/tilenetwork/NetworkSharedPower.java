package dark.core.prefab.tilenetwork;

import net.minecraft.tileentity.TileEntity;
import universalelectricity.core.block.IElectricalStorage;
import dark.api.energy.IPowerLess;
import dark.api.parts.INetworkEnergyPart;
import dark.api.parts.INetworkPart;

/** Used for tile networks that only need to share power or act like a group battery that doesn't
 * store power on world save
 * 
 * @author DarkGuardsman */
public class NetworkSharedPower extends NetworkTileEntities implements IElectricalStorage, IPowerLess
{
    private float energy, energyMax;
    private boolean runPowerLess;

    public NetworkSharedPower(INetworkPart... parts)
    {
        super(parts);
    }

    @Override
    public boolean isValidMember(INetworkPart part)
    {
        return super.isValidMember(part) && part instanceof INetworkEnergyPart;
    }

    public float dumpPower(TileEntity source, float power, boolean doFill)
    {
        float room = (this.getMaxEnergyStored() - this.getEnergyStored());
        if (!this.runPowerLess && this.networkMembers.contains(source) && Math.ceil(room) > 0)
        {
            if (doFill)
            {
                this.setEnergyStored(Math.max(this.getEnergyStored() + power, this.getMaxEnergyStored()));
            }
            return Math.max(Math.min(Math.abs(room - power), power), 0);
        }
        return 0;
    }

    public boolean drainPower(TileEntity source, float power, boolean doDrain)
    {
        if (this.networkMembers.contains(source) && (this.getEnergyStored() >= power || this.runPowerLess))
        {
            if (doDrain && !this.runPowerLess)
            {
                this.setEnergyStored(this.getEnergyStored() - power);
            }
            return true;
        }
        return false;
    }

    @Override
    public void cleanUpMembers()
    {
        super.cleanUpMembers();
        boolean set = false;
        this.energyMax = 0;
        for (INetworkPart part : this.networkMembers)
        {
            if (!set && part instanceof IPowerLess && ((IPowerLess) part).runPowerLess())
            {
                this.setPowerLess(((IPowerLess) part).runPowerLess());
                set = true;
            }
            if (part instanceof INetworkEnergyPart)
            {
                this.energyMax += ((INetworkEnergyPart) part).getPartMaxEnergy();
            }
        }

    }

    @Override
    public boolean runPowerLess()
    {
        return this.runPowerLess;
    }

    @Override
    public void setPowerLess(boolean bool)
    {
        this.runPowerLess = bool;
        for (INetworkPart part : this.networkMembers)
        {
            if (part instanceof IPowerLess)
            {
                ((IPowerLess) part).setPowerLess(bool);
            }

        }
    }

    @Override
    public void setEnergyStored(float energy)
    {
        this.energy = energy;
    }

    @Override
    public float getEnergyStored()
    {
        if (this.energy < 0)
        {
            this.energy = 0;
        }
        return this.energy;
    }

    @Override
    public float getMaxEnergyStored()
    {
        if (this.energyMax < 0)
        {
            this.energyMax = Math.abs(this.energyMax);
        }
        return this.energyMax;
    }

    /** Space left to store more energy */
    public float getEnergySpace()
    {
        return Math.max(this.getMaxEnergyStored() - this.getEnergyStored(), 0);
    }

    @Override
    public void save()
    {
        this.cleanUpMembers();
        float energyRemaining = this.getEnergyStored();
        for (INetworkPart part : this.getMembers())
        {
            float watts = energyRemaining / this.getMembers().size();
            if (part instanceof INetworkEnergyPart)
            {
                ((INetworkEnergyPart) part).setEnergyStored(Math.min(watts, ((INetworkEnergyPart) part).getMaxEnergyStored()));
                energyRemaining -= Math.min(watts, ((INetworkEnergyPart) part).getMaxEnergyStored());
            }
        }
    }

    @Override
    public void load()
    {
        this.setEnergyStored(0);
        this.cleanUpMembers();
        for (INetworkPart part : this.getMembers())
        {
            if (part instanceof INetworkEnergyPart)
            {
                this.setEnergyStored(this.getEnergyStored() + ((INetworkEnergyPart) part).getPartEnergy());
            }
        }
    }

}
