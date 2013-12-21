package com.builtbroken.assemblyline.machine;

import java.util.EnumSet;

import net.minecraft.inventory.Container;
import net.minecraft.network.packet.Packet;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.ForgeDirection;
import universalelectricity.api.energy.IEnergyInterface;
import universalelectricity.api.item.ElectricItemHelper;
import universalelectricity.api.vector.Vector3;
import universalelectricity.api.vector.VectorHelper;

import com.builtbroken.minecraft.network.PacketHandler;
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
            ElectricItemHelper.chargeItemFromMachine(this, ForgeDirection.UNKNOWN, this.getStackInSlot(0));
            /** Decharge electric item. */
            ElectricItemHelper.dischargeItemToMachine(this, ForgeDirection.UNKNOWN, this.getStackInSlot(1));

            ForgeDirection outputDirection = ForgeDirection.getOrientation(this.getBlockMetadata());
            TileEntity outputTile = VectorHelper.getConnectorFromSide(this.worldObj, new Vector3(this), outputDirection);

            if (outputTile instanceof IEnergyInterface)
            {
                long outputWatts = Math.min(this.getEnergyStored(), 10000);
                if (outputWatts > 0 && ((IEnergyInterface) outputTile).onReceiveEnergy(outputDirection.getOpposite(), outputWatts, false) > 0)
                {
                    this.setEnergy(outputDirection, this.getEnergy(outputDirection) - ((IEnergyInterface) outputTile).onReceiveEnergy(outputDirection, outputWatts, true));
                }
            }
            for (ForgeDirection direction : ForgeDirection.VALID_DIRECTIONS)
            {
                if (direction != outputDirection)
                {
                    TileEntity inputTile = VectorHelper.getConnectorFromSide(this.worldObj, new Vector3(this), direction);
                    long inputLimit = Math.min(this.getEnergyCapacity(direction.getOpposite()) - this.getEnergy(direction.getOpposite()), 10000);
                    if (inputLimit > 0 && ((IEnergyInterface) inputTile).onExtractEnergy(direction, inputLimit, false) > 0)
                    {
                        this.setEnergy(outputDirection, this.getEnergy(outputDirection) + ((IEnergyInterface) inputTile).onExtractEnergy(direction, inputLimit, true));
                    }
                }
            }
        }

        /** Gradually lose energy. */
        if (this.ticks % 2000 == 0)
        {
            this.consumePower(1, true);
        }
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
                    this.setEnergy(ForgeDirection.UNKNOWN, dis.readLong());
                    this.setMaxEnergyStored(dis.readLong());
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
        return PacketHandler.instance().getTilePacket(this.getChannel(), "desc", this, this.getEnergyStored(), this.getMaxEnergyStored());
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
