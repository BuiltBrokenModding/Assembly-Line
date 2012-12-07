package dark.SteamPower.boiler;

import net.minecraft.src.Block;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.INetworkManager;
import net.minecraft.src.NBTTagCompound;
import net.minecraft.src.Packet250CustomPayload;
import net.minecraft.src.TileEntity;
import net.minecraftforge.common.ForgeDirection;
import universalelectricity.prefab.network.IPacketReceiver;

import com.google.common.io.ByteArrayDataInput;

import dark.BasicUtilities.api.IProducer;
import dark.BasicUtilities.api.IReadOut;
import dark.BasicUtilities.api.IStorageTank;
import dark.BasicUtilities.api.Liquid;
import dark.BasicUtilities.api.MHelper;
import dark.Library.prefab.TileEntityMachine;
import dark.SteamPower.SteamPowerMain;

public class TileEntityBoiler extends TileEntityMachine implements IPacketReceiver, IProducer, IStorageTank, IReadOut
{
    public int steam = 0;
    public int water = 0;
    public int heat = 0;
    public int hullHeat = 0;
    public int heatMax = 4500;
    public TileEntity[] connectedBlocks =
        { null, null, null, null, null, null };
    public int tankCount = 0;
    public int tickCount = 0;

    // -----------------------------
    // Update stuff
    // -----------------------------
    @Override
    public void updateEntity()
    {
        // update connection list used for rendering
        this.connectedBlocks = MHelper.getSourounding(worldObj, xCoord, yCoord, zCoord);
        this.tankCount = 0;
        for (int i = 0; i < connectedBlocks.length; i++)
        {
            if (connectedBlocks[i] != null)
            {
                tankCount++;
            }
        }// end connection update
        if (tickCount++ >= 10 && !worldObj.isRemote)
        {
            tickCount = 0;

            TileEntity ent = worldObj.getBlockTileEntity(xCoord, yCoord - 1, zCoord);
            if (ent instanceof IProducer && ((IProducer) ent).canProduceLiquid(Liquid.HEAT, ForgeDirection.UP))
            {
                this.heat = (int) Math.min(((IProducer) ent).onProduceLiquid(Liquid.HEAT, 250, ForgeDirection.UP) + this.heat, 4500);
            }
            else if (worldObj.getBlockId(xCoord, yCoord - 1, zCoord) == Block.lavaStill.blockID)
            {
                this.heat = Math.min(90 + heat, 2000);
            }
            if (hullHeat < 10000)
            {
                int mHeat = 10000 - hullHeat;
                int hHeat = mHeat - Math.max((mHeat - this.heat), 0);
                hullHeat = Math.min(hullHeat + hHeat, 10000);
                this.heat -= hHeat;
            }
            else
            {
                if (heat >= 2000 && this.water >= 1 && this.steam < this.getLiquidCapacity(Liquid.STEAM))
                {
                    this.water--;
                    this.steam = Math.min(this.steam + 20, this.getLiquidCapacity(Liquid.STEAM));
                    this.heat -= 2000;
                }
                this.hullHeat -= 5;
            }
            this.water = MHelper.shareLiquid(worldObj, xCoord, yCoord, zCoord, this.water, this.getLiquidCapacity(Liquid.WATER), Liquid.WATER);
            this.steam = MHelper.shareLiquid(worldObj, xCoord, yCoord, zCoord, this.steam, this.getLiquidCapacity(Liquid.STEAM), Liquid.STEAM);
        }
        super.updateEntity();
    }

    // -----------------------------
    // Liquid stuff
    // -----------------------------
    @Override
    public int onReceiveLiquid(Liquid type, int vol, ForgeDirection side)
    {
        if (type == Liquid.WATER)
        {
            if (this.water < this.getLiquidCapacity(Liquid.WATER))
            {
                int rej = Math.max((this.water + vol) - this.getLiquidCapacity(Liquid.WATER), 0);
                this.water += vol - rej;
                return rej;
            }
            else
            {
                TileEntity te = worldObj.getBlockTileEntity(xCoord, yCoord + 1, zCoord);
                if (te instanceof IStorageTank) { return ((IStorageTank) te).onReceiveLiquid(type, vol, ForgeDirection.UNKNOWN); }
            }
        }
        else if (type == Liquid.STEAM)
        {
            if (this.steam < this.getLiquidCapacity(Liquid.STEAM))
            {
                int rej = Math.max((this.steam + vol) - this.getLiquidCapacity(Liquid.STEAM), 0);
                this.steam += vol - rej;
                return rej;
            }
            else
            {
                TileEntity te = worldObj.getBlockTileEntity(xCoord, yCoord - 1, zCoord);
                if (te instanceof IStorageTank) { return ((IStorageTank) te).onReceiveLiquid(type, vol, ForgeDirection.UNKNOWN); }
            }
        }
        return vol;
    }

    @Override
    public boolean canRecieveLiquid(Liquid type, ForgeDirection s)
    {
        if (type == Liquid.WATER)
        {
            return true;
        }
        else if (type == Liquid.STEAM && s == ForgeDirection.UNKNOWN) { return true; }
        return false;
    }

    @Override
    public int getStoredLiquid(Liquid type)
    {
        if (type == Liquid.WATER)
        {
            return this.water;
        }
        else if (type == Liquid.STEAM) { return this.steam; }
        return 0;
    }

    @Override
    public int getLiquidCapacity(Liquid type)
    {
        if (type == Liquid.WATER)
        {
            return 14;
        }
        else if (type == Liquid.STEAM) { return 140; }
        return 0;
    }

    @Override
    public int onProduceLiquid(Liquid type, int vol, ForgeDirection side)
    {
        if (type == Liquid.STEAM)
        {
            // TODO setup the actual math for this
            if (vol < this.steam)
            {
                this.steam -= vol;
                return vol;
            }
            else if (this.steam >= 1)
            {
                this.steam -= 1;
                return 1;
            }
        }
        return 0;
    }

    @Override
    public boolean canProduceLiquid(Liquid type, ForgeDirection side)
    {
        if (type == Liquid.STEAM) { return true; }
        return false;
    }

    @Override
    public boolean canProducePresure(Liquid type, ForgeDirection side)
    {
        if (type == Liquid.STEAM) { return true; }
        return false;
    }

    @Override
    public int presureOutput(Liquid type, ForgeDirection side)
    {
        if (type == Liquid.STEAM) { return 100; }
        return 0;
    }

    // -----------------------------
    // Data
    // -----------------------------
    public Object[] getSendData()
    {
        return new Object[]
            { this.water, this.steam, this.heat, this.hullHeat };
    }

    @Override
    public void handlePacketData(INetworkManager network, int packetType,
            Packet250CustomPayload packet, EntityPlayer player,
            ByteArrayDataInput dataStream)
    {
        try
        {
            this.water = dataStream.readInt();
            this.steam = dataStream.readInt();
            this.heat = dataStream.readInt();
            this.hullHeat = dataStream.readInt();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }

    @Override
    public void writeToNBT(NBTTagCompound par1NBTTagCompound)
    {
        super.writeToNBT(par1NBTTagCompound);
        par1NBTTagCompound.setInteger("water", this.water);
        par1NBTTagCompound.setInteger("steam", this.steam);
        par1NBTTagCompound.setInteger("heat", this.heat);
        par1NBTTagCompound.setInteger("hullHeat", this.hullHeat);
    }

    @Override
    public void readFromNBT(NBTTagCompound par1NBTTagCompound)
    {
        super.readFromNBT(par1NBTTagCompound);
        this.water = par1NBTTagCompound.getInteger("water");
        this.steam = par1NBTTagCompound.getInteger("steam");
        this.heat = par1NBTTagCompound.getInteger("heat");
        this.hullHeat = par1NBTTagCompound.getInteger("hullHeat");
    }

    @Override
    public boolean needUpdate()
    {
        return false;
    }

    @Override
    public int getSizeInventory()
    {
        return 0;
    }

    @Override
    public String getChannel()
    {
        return SteamPowerMain.channel;
    }

    @Override
    public String getMeterReading(EntityPlayer user, ForgeDirection side)
    {
        return this.water + "B WATER " + this.steam + "B STEAM " + this.heat + "/" + this.heatMax + " Heat";
    }

}
