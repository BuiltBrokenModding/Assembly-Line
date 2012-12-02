package dark.BasicUtilities.machines;

import net.minecraft.src.EntityPlayer;
import net.minecraft.src.INetworkManager;
import net.minecraft.src.NBTTagCompound;
import net.minecraft.src.Packet;
import net.minecraft.src.Packet250CustomPayload;
import net.minecraft.src.TileEntity;
import net.minecraftforge.common.ForgeDirection;
import universalelectricity.core.implement.IElectricityReceiver;
import universalelectricity.core.vector.Vector3;
import universalelectricity.prefab.network.IPacketReceiver;
import universalelectricity.prefab.network.PacketManager;
import universalelectricity.prefab.tile.TileEntityElectricityReceiver;

import com.google.common.io.ByteArrayDataInput;

import dark.BasicUtilities.BasicUtilitiesMain;
import dark.BasicUtilities.api.IProducer;
import dark.BasicUtilities.api.Liquid;
import dark.BasicUtilities.api.MHelper;

public class TileEntityPump extends TileEntityElectricityReceiver implements IProducer, IElectricityReceiver, IPacketReceiver
{
    int dCount = 0;
    float eStored = 0;
    float eMax = 2000;
    int lStored = 0;
    int wMax = 10;
    public Liquid type = Liquid.DEFUALT;
    public TileEntity[] connectedBlocks =
        { null, null, null, null, null, null };

    private int count = 0;
    private int count2 = 0;

    protected boolean firstUpdate = true;

    @Override
    public void onDisable(int duration)
    {
        dCount = duration;
    }

    @Override
    public boolean isDisabled()
    {
        if (dCount <= 0) { return false; }
        return true;
    }

    @Override
    public void updateEntity()
    {
        super.updateEntity();
        if (count++ >= 20)
        {
            count = 0;
            connectedBlocks = MHelper.getSourounding(worldObj, xCoord, yCoord, zCoord);
           
            eStored += 200; // TODO remove after testing
            if (!worldObj.isRemote)
            {
                if (firstUpdate || count2++ >= 5)
                {
                    int bBlock = worldObj.getBlockId(xCoord, yCoord - 1, zCoord);
                    Liquid bellow = Liquid.getLiquidByBlock(bBlock);

                    if (bellow != null && this.lStored <= 0) // TODO correct for full
                                                             // pump
                    {
                        this.type = bellow;
                    }
                    count2 = 0;
                    firstUpdate = false;
                    Packet packet = PacketManager.getPacket(BasicUtilitiesMain.CHANNEL, this, this.type.ordinal());
                    PacketManager.sendPacketToClients(packet, worldObj, Vector3.get(this), 60);
                }

                this.drainBlock(new Vector3(xCoord, yCoord - 1, zCoord));
            }
        }
    }

    /**
     * drains the block or in other words removes it
     * 
     * @param loc
     * @return true if the block was drained
     */
    public boolean drainBlock(Vector3 loc)
    {
        int bBlock = worldObj.getBlockId(loc.intX(), loc.intY(), loc.intZ());
        Liquid bellow = Liquid.getLiquidByBlock(bBlock);
        if (bBlock == type.Still && this.eStored >= 200 && this.lStored < this.wMax)
        {
            eStored -= 200;
            lStored += 1;
            worldObj.setBlockAndMetadataWithNotify(xCoord, yCoord - 1, zCoord, 0, 0);
            return true;
        }
        return false;
    }

    /**
     * Used to find the farthest source from the center location
     * 
     * @param loc
     *            - center of search bounds
     * @param maxRange
     *            - max search range
     * @param liquid
     *            - liquid being searched for, if has no block registered to it
     *            returns null
     */
    public Vector3 findDistanceSource(Vector3 loc, int maxRange, Liquid liquid)
    {
        // TODO create a way to scan the outer bounds
        // looking for a source of x liquid
        if (liquid.Still != 0 && liquid.Still != -1)
        {

        }
        return null;
    }

    @Override
    public boolean canReceiveFromSide(ForgeDirection side)
    {
        if (side != ForgeDirection.DOWN) { return true; }
        return false;
    }

    @Override
    public int onProduceLiquid(Liquid type, int maxVol, ForgeDirection side)
    {
        if (type == this.type && lStored > 0 && maxVol > 0)
        {
            lStored -= 1;
            return 1;
        }
        return 0;
    }

    @Override
    public boolean canProduceLiquid(Liquid type, ForgeDirection side)
    {
        int meta = worldObj.getBlockMetadata(xCoord, yCoord, zCoord);
        int facing = 0;
        switch (meta)
        {
            case 0:
                facing = 2;
                break;
            case 1:
                facing = 5;
                break;
            case 2:
                facing = 3;
                break;
            case 3:
                facing = 4;
                break;
        }

        if (type == this.type && side != ForgeDirection.DOWN && side != ForgeDirection.UP && side != ForgeDirection.getOrientation(facing).getOpposite()) { return true; }
        return false;
    }

    @Override
    public int presureOutput(Liquid type, ForgeDirection side)
    {
        if (type == this.type) { return type.defaultPresure; }
        return 0;
    }

    @Override
    public boolean canProducePresure(Liquid type, ForgeDirection side)
    {
        if (type == this.type) { return true; }
        return false;
    }

    @Override
    public void onReceive(Object sender, double amps, double voltage, ForgeDirection side)
    {
        if (wattRequest() > 0 && canConnect(side))
        {
            double watts =(amps * voltage);
            float rejectedElectricity = (float) Math.max((this.eStored + watts) - this.eMax, 0.0);
            this.eStored = (float) Math.max(this.eStored + watts - rejectedElectricity, 0.0);
        }

    }

    @Override
    public double wattRequest()
    {
        return Math.max(eMax - eStored, 0);
    }

    @Override
    public void handlePacketData(INetworkManager network, int packetType, Packet250CustomPayload packet, EntityPlayer player, ByteArrayDataInput data)
    {
        try
        {
            this.type = (Liquid.getLiquid(data.readInt()));
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
        this.lStored = par1NBTTagCompound.getInteger("liquid");
        this.type = Liquid.getLiquid(par1NBTTagCompound.getInteger("type"));
    }

    /**
     * Writes a tile entity to NBT.
     */
    @Override
    public void writeToNBT(NBTTagCompound par1NBTTagCompound)
    {
        super.writeToNBT(par1NBTTagCompound);
        par1NBTTagCompound.setInteger("liquid", this.lStored);
        par1NBTTagCompound.setInteger("type", this.type.ordinal());
    }
}
