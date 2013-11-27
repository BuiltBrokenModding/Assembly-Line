package dark.core.common.machines;

import java.util.EnumSet;

import net.minecraft.network.packet.Packet;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.ForgeDirection;
import universalelectricity.core.block.IElectrical;
import universalelectricity.core.electricity.ElectricityHelper;
import universalelectricity.core.electricity.ElectricityPack;
import universalelectricity.core.grid.IElectricityNetwork;
import universalelectricity.core.vector.Vector3;
import universalelectricity.core.vector.VectorHelper;
import universalelectricity.prefab.network.PacketManager;
import cpw.mods.fml.common.registry.LanguageRegistry;
import dark.core.helpers.EnergyHelper;
import dark.core.prefab.machine.TileEntityEnergyMachine;

/** Simple in out battery box
 *
 * @author DarkGuardsman */
public class TileEntityBatteryBox extends TileEntityEnergyMachine
{

    public TileEntityBatteryBox()
    {
        this.invSlots = 2;
    }

    @Override
    public void updateEntity()
    {
        super.updateEntity();

        if (!this.isDisabled())
        {
            if (!this.worldObj.isRemote)
            {
                /** Recharges electric item. */
                EnergyHelper.recharge(this.getStackInSlot(0), this);
                /** Decharge electric item. */
                EnergyHelper.discharge(this.getStackInSlot(1), this);

                ForgeDirection outputDirection = ForgeDirection.getOrientation(this.getBlockMetadata());
                TileEntity inputTile = VectorHelper.getConnectorFromSide(this.worldObj, new Vector3(this), outputDirection.getOpposite());
                TileEntity outputTile = VectorHelper.getConnectorFromSide(this.worldObj, new Vector3(this), outputDirection);

                IElectricityNetwork outputNetwork = ElectricityHelper.getNetworkFromTileEntity(outputTile, outputDirection);
                IElectricityNetwork inputNetwork = ElectricityHelper.getNetworkFromTileEntity(inputTile, outputDirection.getOpposite());

                if (outputNetwork != null)
                {
                    if (inputNetwork != outputNetwork)
                    {
                        ElectricityPack powerRequest = outputNetwork.getRequest(this);
                        float outputWatts = Math.min(outputNetwork.getRequest(this).getWatts(), Math.min(this.getEnergyStored(), 10000));
                        if (powerRequest.getWatts() > 0)
                        {
                            ElectricityPack sendPack = ElectricityPack.min(ElectricityPack.getFromWatts(this.getEnergyStored(), this.getVoltage()), ElectricityPack.getFromWatts(outputWatts, this.getVoltage()));
                            float rejectedPower = outputNetwork.produce(sendPack, this);
                            this.provideElectricity(sendPack.getWatts() - rejectedPower, true);
                        }
                    }
                }
                else if (outputTile instanceof IElectrical)
                {
                    float powerRequest = ((IElectrical) outputTile).getRequest(outputDirection.getOpposite());
                    float outputWatts = Math.min(powerRequest, Math.min(this.getEnergyStored(), 10000));
                    if (powerRequest > 0)
                    {
                        ElectricityPack sendPack = ElectricityPack.min(ElectricityPack.getFromWatts(this.getEnergyStored(), this.getVoltage()), ElectricityPack.getFromWatts(outputWatts, this.getVoltage()));
                        float rejectedPower = ((IElectrical) outputTile).receiveElectricity(outputDirection.getOpposite(), sendPack, true);
                        this.provideElectricity(sendPack.getWatts() - rejectedPower, true);
                    }
                }
            }
        }

        /** Gradually lose energy. */
        this.setEnergyStored(this.getEnergyStored() - 0.00005f);
    }

    @Override
    public boolean canConnect(ForgeDirection direction)
    {
        return true;
    }

    @Override
    public EnumSet<ForgeDirection> getOutputDirections()
    {
        return EnumSet.of(ForgeDirection.getOrientation(this.getBlockMetadata()).getOpposite());
    }

    @Override
    public Packet getDescriptionPacket()
    {
        return PacketManager.getPacket(this.getChannel(), this, this.getEnergyStored(), this.disabledTicks);
    }

    @Override
    public String getInvName()
    {
        return LanguageRegistry.instance().getStringLocalization("tile.batterybox.name");
    }

    @Override
    public int getInventoryStackLimit()
    {
        return 1;
    }

    @Override
    public float getMaxEnergyStored()
    {
        return 5000;
    }

    @Override
    public boolean isInvNameLocalized()
    {
        return true;
    }

    @Override
    public int[] getAccessibleSlotsFromSide(int slotID)
    {
        return new int[] { 0, 1 };
    }
}
