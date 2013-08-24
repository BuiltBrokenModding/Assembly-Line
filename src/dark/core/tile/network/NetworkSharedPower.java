package dark.core.tile.network;

import net.minecraft.tileentity.TileEntity;
import universalelectricity.core.block.IElectricalStorage;
import dark.api.INetworkEnergyPart;
import dark.api.INetworkPart;
import dark.api.IPowerLess;

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
    public NetworkTileEntities newInstance()
    {
        return new NetworkSharedPower();
    }

    @Override
    public boolean isValidMember(INetworkPart part)
    {
        return super.isValidMember(part) && part instanceof INetworkEnergyPart;
    }

    public float dumpPower(TileEntity source, float power, boolean doFill)
    {
        float room = (this.getMaxEnergyStored() - this.getEnergyStored());
        if (!this.runPowerLess && this.networkMember.contains(source) && Math.ceil(room) > 0)
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
        if (this.networkMember.contains(source) && (this.getEnergyStored() >= power || this.runPowerLess))
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
        for (INetworkPart part : this.networkMember)
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
        for (INetworkPart part : this.networkMember)
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
    public void writeDataToTiles()
    {
        this.cleanUpMembers();
        float energyRemaining = this.getEnergyStored();
        for (INetworkPart part : this.getNetworkMemebers())
        {
            float watts = energyRemaining / this.getNetworkMemebers().size();
            if (part instanceof INetworkEnergyPart)
            {
                ((INetworkEnergyPart) part).setEnergyStored(Math.min(watts, ((INetworkEnergyPart) part).getMaxEnergyStored()));
                energyRemaining -= Math.min(watts, ((INetworkEnergyPart) part).getMaxEnergyStored());
            }
        }
    }

    @Override
    public void readDataFromTiles()
    {
        this.setEnergyStored(0);
        this.cleanUpMembers();
        for (INetworkPart part : this.getNetworkMemebers())
        {
            if (part instanceof INetworkEnergyPart)
            {
                this.setEnergyStored(this.getEnergyStored() + ((INetworkEnergyPart) part).getPartEnergy());
            }
        }
    }

}
