package dark.core.prefab.machine;

import java.util.EnumSet;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.oredict.OreDictionary;
import universalelectricity.core.block.IElectrical;
import universalelectricity.core.block.IElectricalStorage;
import universalelectricity.core.electricity.ElectricityHelper;
import universalelectricity.core.electricity.ElectricityPack;
import universalelectricity.core.grid.IElectricityNetwork;
import universalelectricity.core.vector.Vector3;
import universalelectricity.core.vector.VectorHelper;
import buildcraft.api.power.PowerHandler;
import buildcraft.api.power.PowerHandler.Type;
import dark.api.energy.IPowerLess;
import dark.core.common.ExternalModHandler;

/** Basic energy tile that can consume power
 *
 * Based off both UE universal electrical tile, and electrical tile prefabs
 *
 * @author DarkGuardsman */
public abstract class TileEntityEnergyMachine extends TileEntityMachine implements IElectrical, IElectricalStorage, IPowerLess
{
    /** Forge Ore Directory name of the item to toggle infinite power mode */
    public static String powerToggleItemID = "battery";

    public float WATTS_PER_TICK, MAX_WATTS, maxInputEnergy = 100, energyStored = 0;
    protected boolean isAddedToEnergyNet, consumeEnergy = true;
    public PowerHandler bcPowerHandler;
    public Type bcBlockType = Type.MACHINE;

    public TileEntityEnergyMachine()
    {

    }

    public TileEntityEnergyMachine(float wattsPerTick)
    {
        this.WATTS_PER_TICK = wattsPerTick;
        this.MAX_WATTS = wattsPerTick * 20;
    }

    public TileEntityEnergyMachine(float wattsPerTick, float maxEnergy)
    {
        this.WATTS_PER_TICK = wattsPerTick;
        this.MAX_WATTS = maxEnergy;
    }

    /** Called to consume power from the internal storage */
    public boolean consumePower(float watts, boolean doDrain)
    {
        if (!this.runPowerLess() && this.getEnergyStored() >= watts)
        {
            if (doDrain)
            {
                this.setEnergyStored(this.getEnergyStored() - watts);
            }
            return true;
        }
        return this.runPowerLess();
    }

    /** Does this tile have power to run and do work */
    @Override
    public boolean canFunction()
    {
        return !this.isDisabled() && (this.runPowerLess() || this.consumePower(this.WATTS_PER_TICK, false));
    }

    @Override
    public boolean runPowerLess()
    {
        return !consumeEnergy || ExternalModHandler.runPowerLess();
    }

    @Override
    public void setPowerLess(boolean bool)
    {
        consumeEnergy = !bool;
    }

    public void togglePowerMode()
    {
        this.setPowerLess(!this.runPowerLess());
    }

    /** Called when a player activates the tile's block */
    public boolean onPlayerActivated(EntityPlayer player)
    {
        if (player != null && player.capabilities.isCreativeMode)
        {
            ItemStack itemStack = player.getHeldItem();
            if (itemStack != null)
            {
                for (ItemStack stack : OreDictionary.getOres(powerToggleItemID))
                {
                    if (stack.isItemEqual(itemStack))
                    {
                        this.togglePowerMode();
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @Override
    public void updateEntity()
    {
        super.updateEntity();
        if (!this.worldObj.isRemote)
        {
            if (this.isFunctioning())
            {
                this.consumePower(this.WATTS_PER_TICK, true);
            }
        }
    }

    /** Produces energy on all sides */
    public void produceAllSides()
    {
        if (!this.worldObj.isRemote)
        {
            for (ForgeDirection outputDirection : this.getOutputDirections())
            {
                this.produceDirection(outputDirection);
            }
        }
    }

    /** Produces energy only on the given side */
    public void produceDirection(ForgeDirection outputDirection)
    {
        //TODO detect machines and power them if they are directly next to this machine
        if (!this.worldObj.isRemote && outputDirection != null && outputDirection != ForgeDirection.UNKNOWN)
        {
            float provide = this.getProvide(outputDirection);

            if (provide > 0)
            {
                TileEntity outputTile = VectorHelper.getConnectorFromSide(this.worldObj, new Vector3(this), outputDirection);
                IElectricityNetwork outputNetwork = ElectricityHelper.getNetworkFromTileEntity(outputTile, outputDirection);

                if (outputNetwork != null)
                {
                    ElectricityPack powerRequest = outputNetwork.getRequest(this);

                    if (powerRequest.getWatts() > 0)
                    {
                        ElectricityPack sendPack = ElectricityPack.min(ElectricityPack.getFromWatts(this.getEnergyStored(), this.getVoltage()), ElectricityPack.getFromWatts(provide, this.getVoltage()));
                        float rejectedPower = outputNetwork.produce(sendPack, this);
                        this.provideElectricity(sendPack.getWatts() - rejectedPower, true);
                    }
                }
            }
        }
    }

    /** The electrical input direction.
     *
     * @return The direction that electricity is entered into the tile. Return null for no input. By
     * default you can accept power from all sides. */
    public EnumSet<ForgeDirection> getInputDirections()
    {
        return EnumSet.allOf(ForgeDirection.class);
    }

    /** The electrical output direction.
     *
     * @return The direction that electricity is output from the tile. Return null for no output. By
     * default it will return an empty EnumSet. */
    public EnumSet<ForgeDirection> getOutputDirections()
    {
        return EnumSet.noneOf(ForgeDirection.class);
    }

    @Override
    public float receiveElectricity(ForgeDirection from, ElectricityPack receive, boolean doReceive)
    {
        if (!this.runPowerLess() && receive != null && this.canConnect(from))
        {
            if (receive != null && receive.voltage > (Math.sqrt(2) * this.getVoltage()) && this.worldObj.rand.nextBoolean())
            {
                if (doReceive)
                {
                    this.onDisable(20 + this.worldObj.rand.nextInt(100));
                }
                return 0;
            }
            return this.receiveElectricity(receive.getWatts(), doReceive);

        }
        return 0;
    }

    @Override
    public ElectricityPack provideElectricity(ForgeDirection from, ElectricityPack request, boolean doProvide)
    {
        if (this.getOutputDirections().contains(from))
        {
            return this.provideElectricity(request, doProvide);
        }

        return new ElectricityPack();
    }

    /** A non-side specific version of receiveElectricity for you to optionally use it internally. */
    public float receiveElectricity(ElectricityPack receive, boolean doReceive)
    {

        if (receive != null)
        {
            float prevEnergyStored = this.getEnergyStored();
            float newStoredEnergy = Math.min(this.getEnergyStored() + receive.getWatts(), this.getMaxEnergyStored());

            if (doReceive)
            {
                this.setEnergyStored(newStoredEnergy);
            }

            return Math.max(newStoredEnergy - prevEnergyStored, 0);
        }

        return 0;
    }

    public float receiveElectricity(float energy, boolean doReceive)
    {
        return this.receiveElectricity(ElectricityPack.getFromWatts(energy, this.getVoltage()), doReceive);
    }

    /** A non-side specific version of provideElectricity for you to optionally use it internally. */
    public ElectricityPack provideElectricity(ElectricityPack request, boolean doProvide)
    {
        if (request != null)
        {
            float requestedEnergy = Math.min(request.getWatts(), this.energyStored);

            if (doProvide)
            {
                this.setEnergyStored(this.energyStored - requestedEnergy);
            }

            return ElectricityPack.getFromWatts(requestedEnergy, this.getVoltage());
        }

        return new ElectricityPack();
    }

    public ElectricityPack provideElectricity(float energy, boolean doProvide)
    {
        return this.provideElectricity(ElectricityPack.getFromWatts(energy, this.getVoltage()), doProvide);
    }

    @Override
    public void setEnergyStored(float energy)
    {
        this.energyStored = Math.max(Math.min(energy, this.getMaxEnergyStored()), 0);
    }

    @Override
    public float getEnergyStored()
    {
        return this.energyStored;
    }

    @Override
    public boolean canConnect(ForgeDirection direction)
    {
        if (direction == null || direction.equals(ForgeDirection.UNKNOWN))
        {
            return false;
        }

        return this.getInputDirections().contains(direction) || this.getOutputDirections().contains(direction);
    }

    @Override
    public float getVoltage()
    {
        return 0.120F;
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt)
    {
        super.readFromNBT(nbt);
        this.energyStored = nbt.getFloat("energyStored");
        consumeEnergy = !nbt.getBoolean("shouldPower");
        this.functioning = nbt.getBoolean("isRunning");

        if (nbt.hasKey("wattsReceived"))
        {
            this.energyStored = (float) nbt.getDouble("wattsReceived");
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt)
    {
        super.writeToNBT(nbt);
        nbt.setBoolean("shouldPower", !consumeEnergy);
        nbt.setFloat("energyStored", this.energyStored);
        nbt.setBoolean("isRunning", this.functioning);
    }

    @Override
    public float getMaxEnergyStored()
    {
        return this.MAX_WATTS;
    }

    @Override
    public float getRequest(ForgeDirection direction)
    {
        return Math.max(this.getMaxEnergyStored() - this.getEnergyStored(), 0);
    }

    @Override
    public float getProvide(ForgeDirection direction)
    {
        return 0;
    }
}
