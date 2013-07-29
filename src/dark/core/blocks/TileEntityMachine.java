package dark.core.blocks;

import java.util.Random;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.oredict.OreDictionary;
import universalelectricity.compatibility.TileEntityUniversalElectrical;
import universalelectricity.core.electricity.ElectricityPack;
import universalelectricity.core.vector.Vector3;
import universalelectricity.prefab.network.IPacketReceiver;
import universalelectricity.prefab.network.PacketManager;

import com.google.common.io.ByteArrayDataInput;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import dark.api.IDisableable;
import dark.api.IPowerLess;
import dark.api.PowerSystems;
import dark.core.DarkMain;

public abstract class TileEntityMachine extends TileEntityUniversalElectrical implements IDisableable, IPacketReceiver, IPowerLess
{

    /** Forge Ore Directory name of the item to toggle power */
    public static String powerToggleItemID = "battery";

    protected Random random = new Random();

    protected int ticksDisabled = 0;

    protected float WATTS_PER_TICK, MAX_WATTS;

    protected boolean unpowered, running;

    @Override
    public void updateEntity()
    {
        super.updateEntity();
        if (!this.worldObj.isRemote)
        {
            boolean prevRun = this.running;
            this.running = this.canRun() && this.consumePower(this.WATTS_PER_TICK, true);
            if (prevRun != this.running)
            {
                PacketManager.sendPacketToClients(this.getDescriptionPacket(), worldObj, new Vector3(this), 64);
            }
        }

        if (this.ticksDisabled > 0)
        {
            this.ticksDisabled--;
            this.whileDisable();
        }
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
    public boolean canRun()
    {
        return !this.isDisabled() && (this.runPowerLess() || this.getEnergyStored() >= this.WATTS_PER_TICK);
    }

    @Override
    public boolean runPowerLess()
    {
        return this.unpowered || PowerSystems.runPowerLess(PowerSystems.UE_SUPPORTED_SYSTEMS);
    }

    @Override
    public void setPowerLess(boolean bool)
    {
        this.unpowered = bool;
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
                for (ItemStack stack : OreDictionary.getOres(TileEntityMachine.powerToggleItemID))
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
    public float receiveElectricity(ForgeDirection from, ElectricityPack receive, boolean doReceive)
    {
        if (!this.runPowerLess() && receive != null && this.canConnect(from))
        {
            // Only do voltage disable if the voltage is higher than the peek voltage and if random chance
            //TODO replace random with timed damage to only disable after so many ticks
            if (receive != null && receive.voltage > (Math.sqrt(2) * this.getVoltage()) && this.random.nextBoolean())
            {
                if (doReceive)
                {
                    this.onDisable(20 + this.random.nextInt(100));
                }
                return 0;
            }
            return super.receiveElectricity(from, receive, doReceive);

        }
        return 0;
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

    @Override
    public float getMaxEnergyStored()
    {
        return this.MAX_WATTS;
    }

    /** Called every tick while this tile entity is disabled. */
    protected void whileDisable()
    {
        if (worldObj.isRemote)
        {
            this.renderSparks();
        }
    }

    @SideOnly(Side.CLIENT)
    private void renderSparks()
    {
        //TODO render sparks or call to a client-proxy method to render sparks around the block correctly
    }

    @Override
    public void onDisable(int duration)
    {
        this.ticksDisabled = duration;
    }

    @Override
    public boolean isDisabled()
    {
        return this.ticksDisabled > 0;
    }

    @Override
    public Packet getDescriptionPacket()
    {
        return PacketManager.getPacket(DarkMain.CHANNEL, this, this.running);
    }

    @Override
    public void handlePacketData(INetworkManager network, int packetType, Packet250CustomPayload packet, EntityPlayer player, ByteArrayDataInput data)
    {
        try
        {
            if (worldObj.isRemote)
            {
                this.running = data.readBoolean();
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }

    @Override
    public void readFromNBT(NBTTagCompound nbt)
    {
        super.readFromNBT(nbt);
        this.ticksDisabled = nbt.getInteger("disabledTicks");
        this.unpowered = nbt.getBoolean("shouldPower");
        if (nbt.hasKey("wattsReceived"))
        {
            this.energyStored = (float) nbt.getDouble("wattsReceived");
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt)
    {
        super.writeToNBT(nbt);
        nbt.setInteger("disabledTicks", this.ticksDisabled);
        nbt.setBoolean("shouldPower", this.unpowered);
    }
}
