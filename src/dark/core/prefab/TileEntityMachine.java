package dark.core.prefab;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
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

import com.google.common.io.ByteArrayDataInput;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import dark.api.IDisableable;
import dark.api.energy.IPowerLess;
import dark.core.common.DarkMain;
import dark.core.common.ExternalModHandler;
import dark.core.interfaces.IExternalInv;
import dark.core.interfaces.IInvBox;
import dark.core.network.PacketHandler;
import dark.core.prefab.invgui.InvChest;

/** Prefab for most machines in the CoreMachine set. Provides basic power updates, packet updates,
 * inventory handling, and other handy methods.
 * 
 * @author DarkGuardsman */
public abstract class TileEntityMachine extends TileEntityUniversalElectrical implements ISidedInventory, IExternalInv, IDisableable, IPacketReceiver, IPowerLess
{
    //TODO add support for attaching multi-meter to side of machine

    /** Forge Ore Directory name of the item to toggle infinite power mode */
    public static String powerToggleItemID = "battery";

    /** ticks to act dead, disabled, or not function at all */
    protected int ticksDisabled = 0;

    protected float WATTS_PER_TICK, MAX_WATTS;

    protected boolean unpowered = false, running = false, prevRunning = false;
    /** Inventory manager used by this machine */
    protected IInvBox inventory;

    /** Default generic packet types used by all machines */
    public static enum TilePacketTypes
    {
        /** Normal packet data of any kind */
        GENERIC("generic"),
        /** Power updates */
        POWER("isRunning"),
        /** GUI display data update */
        GUI("guiGeneral"),
        /** Full tile read/write data from tile NBT */
        NBT("nbtAll");

        public String name;

        private TilePacketTypes(String name)
        {
            this.name = name;
        }
    }

    public TileEntityMachine()
    {

    }

    public TileEntityMachine(float wattsPerTick)
    {
        this.WATTS_PER_TICK = wattsPerTick;
        this.MAX_WATTS = wattsPerTick * 20;
    }

    public TileEntityMachine(float wattsPerTick, float maxEnergy)
    {
        this.WATTS_PER_TICK = wattsPerTick;
        this.MAX_WATTS = maxEnergy;
    }

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
                this.sendPowerUpdate();
            }
        }

        if (this.ticksDisabled > 0)
        {
            this.ticksDisabled--;
            this.whileDisable();
        }
    }

    public void doPowerDebug()
    {
        System.out.println("\n  CanRun: " + this.canRun());
        System.out.println("  RedPower: " + this.worldObj.isBlockIndirectlyGettingPowered(xCoord, yCoord, zCoord));
        System.out.println(" IsDisabled: " + this.isDisabled());//TODO i'm going to kick myself if this is it, yep disabled
        System.out.println("  HasPower: " + this.consumePower(WATTS_PER_TICK, false));
        System.out.println("  IsRunning: " + this.running);
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
        return !this.isDisabled() && (this.runPowerLess() || this.consumePower(this.WATTS_PER_TICK, false));
    }

    @Override
    public boolean runPowerLess()
    {
        return this.unpowered || ExternalModHandler.runPowerLess();
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
            if (receive != null && receive.voltage > (Math.sqrt(2) * this.getVoltage()) && this.worldObj.rand.nextBoolean())
            {
                if (doReceive)
                {
                    this.onDisable(20 + this.worldObj.rand.nextInt(100));
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
    public void handlePacketData(INetworkManager network, int packetType, Packet250CustomPayload packet, EntityPlayer player, ByteArrayDataInput dataStream)
    {
        boolean packetSize = true;
        try
        {
            ByteArrayInputStream bis = new ByteArrayInputStream(packet.data);
            DataInputStream dis = new DataInputStream(bis);

            int id = dis.readInt();
            int x = dis.readInt();
            int y = dis.readInt();
            int z = dis.readInt();
            String pId = dis.readUTF();

            this.simplePacket(pId, dis, player);

            /** DEBUG PACKET SIZE AND INFO */
            if (packetSize)
            {
                System.out.println("Tile>" + this.toString() + ">>>Debug>>Packet" + pId + ">>Size>>bytes>>" + packet.data.length);
            }
        }
        catch (Exception e)
        {
            System.out.println("Error Reading Packet for a TileEntityAssembly");
            e.printStackTrace();
        }

    }

    /** Handles reduced data from the main packet method
     * 
     * @param id - packet ID
     * @param dis - data
     * @param player - player
     * @return true if the packet was used */
    public boolean simplePacket(String id, DataInputStream dis, EntityPlayer player)
    {
        try
        {
            if (this.worldObj.isRemote)
            {
                if (id.equalsIgnoreCase(TilePacketTypes.POWER.name))
                {
                    this.running = dis.readBoolean();
                    return true;
                }
                if (id.equalsIgnoreCase(TilePacketTypes.NBT.name))
                {
                    this.readFromNBT(Packet.readNBTTagCompound(dis));
                    return true;
                }
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return false;
    }

    /** NetworkMod channel name */
    public String getChannel()
    {
        return DarkMain.CHANNEL;
    }

    /** Sends a simple true/false am running power update */
    public void sendPowerUpdate()
    {
        if (!this.worldObj.isRemote)
        {
            PacketHandler.instance().sendPacketToClients(PacketHandler.instance().getPacket(this.getChannel(), this, TilePacketTypes.POWER.name, this.running), worldObj, new Vector3(this), 64);
        }
    }

    /** Sends the tileEntity save data to the client */
    public void sendNBTPacket()
    {
        if (!this.worldObj.isRemote)
        {
            NBTTagCompound tag = new NBTTagCompound();
            this.writeToNBT(tag);
            PacketHandler.instance().sendPacketToClients(PacketHandler.instance().getPacket(this.getChannel(), this, TilePacketTypes.NBT.name, tag), worldObj, new Vector3(this), 64);
        }
    }

    @Override
    public Packet getDescriptionPacket()
    {
        NBTTagCompound tag = new NBTTagCompound();
        this.writeToNBT(tag);
        return PacketHandler.instance().getPacket(this.getChannel(), this, TilePacketTypes.NBT.name, tag);
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt)
    {
        super.readFromNBT(nbt);
        this.ticksDisabled = nbt.getInteger("disabledTicks");
        this.unpowered = nbt.getBoolean("shouldPower");
        this.running = nbt.getBoolean("isRunning");
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
        nbt.setBoolean("isRunning", this.running);
    }

    /*--------------------------------------------------------------
     * IInventory stuff
     * ------------------------------------------------------------- */

    @Override
    public IInvBox getInventory()
    {
        if (inventory == null)
        {
            inventory = new InvChest(this, 1);
        }
        return inventory;
    }

    @Override
    public int getSizeInventory()
    {
        return this.getInventory().getSizeInventory();
    }

    @Override
    public ItemStack getStackInSlot(int i)
    {
        return this.getInventory().getStackInSlot(i);
    }

    @Override
    public ItemStack decrStackSize(int i, int j)
    {
        return this.getInventory().decrStackSize(i, j);
    }

    @Override
    public ItemStack getStackInSlotOnClosing(int i)
    {
        return this.getInventory().getStackInSlotOnClosing(i);
    }

    @Override
    public void setInventorySlotContents(int i, ItemStack itemstack)
    {
        this.getInventory().setInventorySlotContents(i, itemstack);

    }

    @Override
    public String getInvName()
    {
        return this.getInventory().getInvName();
    }

    @Override
    public boolean isInvNameLocalized()
    {
        return this.getInventory().isInvNameLocalized();
    }

    @Override
    public int getInventoryStackLimit()
    {
        return this.getInventory().getInventoryStackLimit();
    }

    @Override
    public boolean isUseableByPlayer(EntityPlayer entityplayer)
    {
        return this.getInventory().isUseableByPlayer(entityplayer);
    }

    @Override
    public void openChest()
    {
        this.getInventory().openChest();

    }

    @Override
    public void closeChest()
    {
        this.getInventory().closeChest();

    }

    @Override
    public boolean isItemValidForSlot(int i, ItemStack itemstack)
    {
        return this.getInventory().isItemValidForSlot(i, itemstack);
    }

    @Override
    public int[] getAccessibleSlotsFromSide(int var1)
    {
        return this.getInventory().getAccessibleSlotsFromSide(var1);
    }

    @Override
    public boolean canInsertItem(int i, ItemStack itemstack, int j)
    {
        return this.getInventory().canInsertItem(i, itemstack, j);
    }

    @Override
    public boolean canExtractItem(int i, ItemStack itemstack, int j)
    {
        return this.getInventory().canExtractItem(i, itemstack, j);
    }

    @Override
    public boolean canStore(ItemStack stack, int slot, ForgeDirection side)
    {
        return false;
    }

    @Override
    public boolean canRemove(ItemStack stack, int slot, ForgeDirection side)
    {
        return false;
    }

}
