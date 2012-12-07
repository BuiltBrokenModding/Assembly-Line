package dark.BasicUtilities.pipes;

import net.minecraft.src.EntityPlayer;
import net.minecraft.src.INetworkManager;
import net.minecraft.src.NBTTagCompound;
import net.minecraft.src.Packet;
import net.minecraft.src.Packet250CustomPayload;
import net.minecraft.src.TileEntity;
import net.minecraftforge.common.ForgeDirection;
import universalelectricity.core.vector.Vector3;
import universalelectricity.prefab.network.IPacketReceiver;
import universalelectricity.prefab.network.PacketManager;

import com.google.common.io.ByteArrayDataInput;

import dark.BasicUtilities.BasicUtilitiesMain;
import dark.BasicUtilities.api.IConsumer;
import dark.BasicUtilities.api.IProducer;
import dark.BasicUtilities.api.IReadOut;
import dark.BasicUtilities.api.Liquid;
import dark.BasicUtilities.api.MHelper;

public class TileEntityPipe extends TileEntity implements IConsumer, IPacketReceiver,IReadOut
{
    protected Liquid type = Liquid.DEFUALT;

    public int capacity = 2;
    public int presure = 0;
    public int connectedUnits = 0;
    public int liquidStored = 0;
    private int count = 0;
    private int count2 = 0;

    protected boolean firstUpdate = true;

    public TileEntity[] connectedBlocks =
        { null, null, null, null, null, null };

    public int getPressure()
    {
        return this.presure;
    }

    @Override
    public void updateEntity()
    {
        if (++count >= 5)
        {
            count = 0;
            this.connectedBlocks = MHelper.getSourounding(worldObj, xCoord, yCoord, zCoord);
            this.updatePressure();

            if (!worldObj.isRemote)
            {
                if (firstUpdate || count2++ >= 5)
                {
                    count2 = 0;
                    firstUpdate = false;
                    Packet packet = PacketManager.getPacket(BasicUtilitiesMain.CHANNEL, this, this.type.ordinal());
                    PacketManager.sendPacketToClients(packet, worldObj, Vector3.get(this), 60);
                }

                for (int i = 0; i < 6; i++)
                {

                    ForgeDirection dir = ForgeDirection.getOrientation(i);

                    if (connectedBlocks[i] instanceof IProducer)
                    {
                        int vol = ((IProducer) connectedBlocks[i]).onProduceLiquid(this.type, this.capacity - this.liquidStored, dir);
                        this.liquidStored = Math.min(this.liquidStored + vol,
                                this.capacity);
                    }
                    if (connectedBlocks[i] instanceof IConsumer && this.liquidStored > 0 && this.presure > 0)
                    {
                        if (connectedBlocks[i] instanceof TileEntityPipe)
                        {
                            if (((TileEntityPipe) connectedBlocks[i]).presure < this.presure)
                            {
                                this.liquidStored--;
                                int vol = ((IConsumer) connectedBlocks[i]).onReceiveLiquid(this.type, Math.max(this.liquidStored, 1), dir);
                                this.liquidStored += vol;
                            }
                        }
                        else
                        {
                            this.liquidStored = ((IConsumer) connectedBlocks[i]).onReceiveLiquid(this.type, this.liquidStored, dir);
                        }
                    }
                }
            }
        }
    }

    /**
     * used to cause the pipes pressure to update depending on what is connected
     * to it
     * 
     * @return
     */
    public void updatePressure()
    {
        int highestPressure = 0;
        this.connectedUnits = 0;
        this.presure = 0;

        for (int i = 0; i < 6; i++)
        {
            ForgeDirection dir = ForgeDirection.getOrientation(i);

            if (connectedBlocks[i] instanceof IConsumer && ((IConsumer) connectedBlocks[i]).canRecieveLiquid(this.type, dir))
            {
                this.connectedUnits++;
                if (connectedBlocks[i] instanceof TileEntityPipe)
                {
                    if (((TileEntityPipe) connectedBlocks[i]).getPressure() > highestPressure)
                    {
                        highestPressure = ((TileEntityPipe) connectedBlocks[i]).getPressure();
                    }
                }
            }
            else if (connectedBlocks[i] instanceof IProducer && ((IProducer) connectedBlocks[i]).canProduceLiquid(this.type, dir))
            {
                this.connectedUnits++;
                if (((IProducer) connectedBlocks[i]).canProducePresure(this.type, dir) && ((IProducer) connectedBlocks[i]).presureOutput(this.type, dir) > highestPressure)
                {
                    highestPressure = ((IProducer) connectedBlocks[i]).presureOutput(this.type, dir);
                }
            }
            else
            {
                connectedBlocks[i] = null;
            }
        }
        this.presure = highestPressure - 1;
    }

    // ---------------
    // liquid stuff
    // ---------------
    @Override
    public int onReceiveLiquid(Liquid type, int vol, ForgeDirection side)
    {
        if (type == this.type)
        {
            int rejectedVolume = Math.max((this.getStoredLiquid(type) + vol) - this.capacity, 0);
            this.liquidStored = Math.min(Math.max((liquidStored + vol - rejectedVolume), 0), this.capacity);
            return Math.abs(rejectedVolume);
        }
        return vol;
    }

    /**
     * @return Return the stored volume in this pipe.
     */
    @Override
    public int getStoredLiquid(Liquid type)
    {
        if (type == this.type) { return this.liquidStored; }
        return 0;
    }

    @Override
    public int getLiquidCapacity(Liquid type)
    {
        if (type == this.type) { return this.capacity; }
        return 0;
    }

    // find wether or not this side of X block can recieve X liquid type. Also
    // use to determine connection of a pipe
    @Override
    public boolean canRecieveLiquid(Liquid type, ForgeDirection side)
    {
        if (type == this.type) { return true; }
        return false;
    }

    // returns liquid type
    public Liquid getType()
    {
        return this.type;
    }

    // used by the item to set the liquid type on spawn
    public void setType(Liquid rType)
    {
        this.type = rType;
    }

    // ---------------------
    // data
    // --------------------
    @Override
    public void handlePacketData(INetworkManager network, int packetType, Packet250CustomPayload packet, EntityPlayer player, ByteArrayDataInput data)
    {
        try
        {
            this.setType(Liquid.getLiquid(data.readInt()));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }

    /**
     * Reads a tile entity from NBT.
     */
    @Override
    public void readFromNBT(NBTTagCompound par1NBTTagCompound)
    {
        super.readFromNBT(par1NBTTagCompound);
        this.liquidStored = par1NBTTagCompound.getInteger("liquid");
        this.type = Liquid.getLiquid(par1NBTTagCompound.getInteger("type"));
    }

    /**
     * Writes a tile entity to NBT.
     */
    @Override
    public void writeToNBT(NBTTagCompound par1NBTTagCompound)
    {
        super.writeToNBT(par1NBTTagCompound);
        par1NBTTagCompound.setInteger("liquid", this.liquidStored);
        par1NBTTagCompound.setInteger("type", this.type.ordinal());
    }

    @Override
    public String getMeterReading(EntityPlayer user, ForgeDirection side)
    {
        return this.liquidStored+" "+this.type.name()+" @ "+this.presure+"PSI";
    }
}
