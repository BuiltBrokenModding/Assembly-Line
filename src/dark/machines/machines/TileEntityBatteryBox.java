package dark.machines.machines;

import java.util.EnumSet;

import net.minecraft.inventory.Container;
import net.minecraft.network.packet.Packet;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.ForgeDirection;
import universalelectricity.core.block.IElectrical;
import universalelectricity.core.electricity.ElectricityHelper;
import universalelectricity.core.electricity.ElectricityPack;
import universalelectricity.core.grid.IElectricityNetwork;
import universalelectricity.core.vector.Vector3;
import universalelectricity.core.vector.VectorHelper;

import com.builtbroken.minecraft.network.PacketHandler;
import com.builtbroken.minecraft.prefab.EnergyHelper;
import com.builtbroken.minecraft.prefab.TileEntityEnergyMachine;
import com.google.common.io.ByteArrayDataInput;

import cpw.mods.fml.common.network.Player;
import cpw.mods.fml.common.registry.LanguageRegistry;

/** Simple in out battery box
 * 
 * @author DarkGuardsman */
public class TileEntityBatteryBox extends TileEntityEnergyMachine
{
    public TileEntityBatteryBox()
    {
        super(0, 5000);
        this.invSlots = 2;
        this.hasGUI = true;
    }

    @Override
    public void updateEntity()
    {
        super.updateEntity();

        if (!this.worldObj.isRemote && this.enabled && !this.isDisabled())
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
                    float outputWatts = Math.min(outputNetwork.getRequest(this).getWatts(), Math.min(this.getEnergyStored(), 100));
                    if (powerRequest.getWatts() > 0)
                    {
                        ElectricityPack sendPack = ElectricityPack.min(ElectricityPack.getFromWatts(this.getEnergyStored(), this.getVoltage()), ElectricityPack.getFromWatts(outputWatts, this.getVoltage()));

                        this.setEnergyStored(this.getEnergyStored() - (sendPack.getWatts() - outputNetwork.produce(sendPack, this)));
                    }
                }
            }
            else if (outputTile instanceof IElectrical)
            {
                float powerRequest = ((IElectrical) outputTile).getRequest(outputDirection.getOpposite());
                float outputWatts = Math.min(powerRequest, Math.min(this.getEnergyStored(), 100));
                if (powerRequest > 0)
                {
                    ElectricityPack sendPack = ElectricityPack.min(ElectricityPack.getFromWatts(this.getEnergyStored(), this.getVoltage()), ElectricityPack.getFromWatts(outputWatts, this.getVoltage()));
                    this.setEnergyStored(this.getEnergyStored() - ((IElectrical) outputTile).receiveElectricity(outputDirection.getOpposite(), sendPack, true));
                }
            }
        }

        /** Gradually lose energy. */
        this.consumePower(0.000005f, true);
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
    public boolean simplePacket(String id, ByteArrayDataInput dis, Player player)
    {
        boolean r = super.simplePacket(id, dis, player);
        try
        {

            if (this.worldObj.isRemote && !r)
            {
                if (id.equalsIgnoreCase("desc"))
                {
                    this.setEnergyStored(dis.readFloat());
                    this.setMaxEnergyStored(dis.readFloat());
                    return true;
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return r;
    }

    @Override
    public Packet getDescriptionPacket()
    {
        return PacketHandler.instance().getTilePacket(this.getChannel(), this, "desc", this.getEnergyStored(), this.getMaxEnergyStored());
    }

    @Override
    public Packet getGUIPacket()
    {
        return this.getDescriptionPacket();
    }

    @Override
    public Class<? extends Container> getContainer()
    {
        return ContainerBatteryBox.class;
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
