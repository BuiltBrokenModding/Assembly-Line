package liquidmechanics.common.tileentity;

import liquidmechanics.api.IReadOut;
import liquidmechanics.api.IPressure;
import liquidmechanics.api.helpers.TankHelper;
import liquidmechanics.common.LiquidMechanics;
import liquidmechanics.common.handlers.LiquidData;
import liquidmechanics.common.handlers.LiquidHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.liquids.ILiquidTank;
import net.minecraftforge.liquids.ITankContainer;
import net.minecraftforge.liquids.LiquidContainerRegistry;
import net.minecraftforge.liquids.LiquidStack;
import net.minecraftforge.liquids.LiquidTank;
import universalelectricity.core.vector.Vector3;
import universalelectricity.prefab.network.IPacketReceiver;
import universalelectricity.prefab.network.PacketManager;

import com.google.common.io.ByteArrayDataInput;

public class TileEntityPipe extends TileEntity implements ITankContainer, IPacketReceiver, IReadOut
{
    public LiquidData type = LiquidHandler.air;
    
    private int count = 20;
    private int count2, presure = 0;
    
    public boolean converted = false;
    protected boolean firstUpdate = true;
    public boolean isUniversal = false;

    public TileEntity[] connectedBlocks = { null, null, null, null, null, null };
    
    public LiquidTank stored = new LiquidTank(LiquidContainerRegistry.BUCKET_VOLUME * 3);

    @Override
    public void updateEntity()
    {
        if (++count >= 40)
        {
            count = 0;
            this.connectedBlocks = TankHelper.getSurroundings(worldObj, xCoord, yCoord, zCoord);
            for (int e = 0; e < 6; e++)
            {
                if (connectedBlocks[e] instanceof ITankContainer || connectedBlocks[e] instanceof IPressure)
                {
                    if (connectedBlocks[e] instanceof TileEntityPipe && ((TileEntityPipe) connectedBlocks[e]).type != this.type)
                    {
                        connectedBlocks[e] = null;
                    }
                }
                else
                {
                    connectedBlocks[e] = null;
                }

            }

            if (!worldObj.isRemote)
            {
                this.updatePressure();
                if (count2-- <= 0)
                {
                    count2 = 5;
                    firstUpdate = false;
                    Packet packet = PacketManager.getPacket(LiquidMechanics.CHANNEL, this, LiquidData.getName(type));
                    PacketManager.sendPacketToClients(packet, worldObj, new Vector3(this), 60);
                }

                LiquidStack stack = stored.getLiquid();
                if (stack != null && stack.amount >= 0)
                {

                    for (int i = 0; i < 6; i++)
                    {
                        ForgeDirection dir = ForgeDirection.getOrientation(i);
                        int moved = 0;
                        if (connectedBlocks[i] instanceof ITankContainer)
                        {
                            if (connectedBlocks[i] instanceof TileEntityPipe)
                            {
                                if (((TileEntityPipe) connectedBlocks[i]).presure < this.presure)
                                {
                                    moved = ((TileEntityPipe) connectedBlocks[i]).stored.fill(stack, true);
                                }

                            }
                            else
                            {
                                moved = ((ITankContainer) connectedBlocks[i]).fill(dir.getOpposite(), stack, true);
                            }
                        }
                        stored.drain(moved, true);
                        // FMLLog.warning("Moved "+moved+ " "+ i);
                        if (stack.amount <= 0)
                        {
                            break;
                        }
                    }
                }
            }
        }

    }

    // returns liquid type
    public LiquidData getType()
    {
        return this.type;
    }

    // used by the item to set the liquid type on spawn
    public void setType(LiquidData rType)
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
            this.setType(LiquidHandler.get(data.readUTF()));
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
    public void readFromNBT(NBTTagCompound nbt)
    {
        super.readFromNBT(nbt);

        this.converted = nbt.getBoolean("converted");
        if (!converted)
        {
            int t = nbt.getInteger("type");
            this.type = LiquidHandler.getFromMeta(t);
           this.converted = true;
        }
        else
        {
            this.type = LiquidHandler.get(nbt.getString("name"));
        }
        if (this.type == null) type = LiquidHandler.air;
        
        int vol = nbt.getInteger("liquid");
        this.stored.setLiquid(LiquidHandler.getStack(type, vol));
    }

    /**
     * Writes a tile entity to NBT.
     */
    @Override
    public void writeToNBT(NBTTagCompound nbt)
    {
        super.writeToNBT(nbt);
        nbt.setBoolean("converted", this.converted);
        int s = 0;
        if (stored.getLiquid() != null) s = stored.getLiquid().amount;
        nbt.setInteger("liquid", s);

        nbt.setString("name", LiquidData.getName(type));
    }

    @Override
    public String getMeterReading(EntityPlayer user, ForgeDirection side)
    {
        if (type == null) return "Error: No Type";
        String output = "";
        LiquidStack stack = stored.getLiquid();
        if (stack != null)
            output += (stack.amount / LiquidContainerRegistry.BUCKET_VOLUME) + " " + LiquidData.getName(type);
        output += " @" + this.presure + "psi";

        if (stack != null)
            return output;

        return "Error";
    }

    @Override
    public int fill(ForgeDirection from, LiquidStack resource, boolean doFill)
    {
        LiquidStack stack = stored.getLiquid();
        if (stack == null) stored.setLiquid(LiquidHandler.getStack(this.type, 1));
        if (stack != null && LiquidHandler.isEqual(resource, this.type)) return fill(0, resource, doFill);

        return 0;
    }

    @Override
    public int fill(int tankIndex, LiquidStack resource, boolean doFill)
    {
        if (tankIndex != 0 || resource == null)
            return 0;

        return stored.fill(resource, doFill);
    }

    @Override
    public LiquidStack drain(ForgeDirection from, int maxDrain, boolean doDrain)
    {
        return drain(0, maxDrain, doDrain);
    }

    @Override
    public LiquidStack drain(int tankIndex, int maxDrain, boolean doDrain)
    {
        return stored.drain(maxDrain, doDrain);
    }

    @Override
    public ILiquidTank[] getTanks(ForgeDirection direction)
    {
        return new ILiquidTank[] { this.stored };
    }

    @Override
    public ILiquidTank getTank(ForgeDirection direction, LiquidStack type)
    {
        return null;
    }

    /**
     * Used to determan pipe connection rules
     */
    public boolean canConnect(TileEntity entity)
    {
        if (entity instanceof TileEntityPipe)
        {
            if (((TileEntityPipe) entity).type == this.type) { return true; }
        }
        return false;
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
        this.presure = 0;

        for (int i = 0; i < 6; i++)
        {
            ForgeDirection dir = ForgeDirection.getOrientation(i);

            if (connectedBlocks[i] instanceof TileEntityPipe && ((TileEntityPipe) connectedBlocks[i]).canConnect(this))
            {
                if (((TileEntityPipe) connectedBlocks[i]).getPressure() > highestPressure)
                {
                    highestPressure = ((TileEntityPipe) connectedBlocks[i]).getPressure();
                }
            }
            if (connectedBlocks[i] instanceof IPressure && ((IPressure) connectedBlocks[i]).canPressureToo(this.type, dir))
            {

                int p = ((IPressure) connectedBlocks[i]).presureOutput(this.type, dir);
                if (p > highestPressure)
                    highestPressure = p;
            }
        }
        this.presure = highestPressure - 1;
    }

    public int getPressure()
    {
        return this.presure;
    }
}
