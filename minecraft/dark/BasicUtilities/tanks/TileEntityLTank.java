package dark.BasicUtilities.tanks;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
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

import dark.BasicUtilities.BasicUtilitiesMain;
import dark.BasicUtilities.api.IReadOut;
import dark.BasicUtilities.api.ITankOutputer;
import dark.BasicUtilities.api.Liquid;
import dark.BasicUtilities.api.MHelper;

public class TileEntityLTank extends TileEntity implements IPacketReceiver, IReadOut, ITankOutputer
{
    public TileEntity[] cc =
    { null, null, null, null, null, null };
    public Liquid type = Liquid.DEFUALT;
    public int LMax = 4;
    private int count = 0;
    private int count2 = 0;

    private boolean doUpdate = true;
    public LiquidTank tank = new LiquidTank(LiquidContainerRegistry.BUCKET_VOLUME * 4);

    public void updateEntity()
    {
        if (tank.getLiquid() == null)
        {
            tank.setLiquid(Liquid.getStack(this.type, 1));
        }
        LiquidStack liquid = tank.getLiquid();

        if (++count >= 5 && liquid != null)
        {
            count = 0;
            this.cc = MHelper.getSourounding(worldObj, xCoord, yCoord, zCoord);
            if (!worldObj.isRemote)
            {
                this.tradeDown();
                this.tradeArround();
                if (doUpdate || count2 >= 20)
                {
                    this.doUpdate = false;
                    count2 = 0;
                    Packet packet = PacketManager.getPacket(BasicUtilitiesMain.CHANNEL, this, new Object[]
                    { type.ordinal(), liquid.amount });
                    PacketManager.sendPacketToClients(packet, worldObj, Vector3.get(this), 20);
                }
            }
        }
    }

    @Override
    public String getMeterReading(EntityPlayer user, ForgeDirection side)
    {
        String output = "";
        LiquidStack stack = tank.getLiquid();
        if (stack != null) output += (stack.amount / LiquidContainerRegistry.BUCKET_VOLUME) + " " + this.type.displayerName;
        if (stack != null) return output;

        return "0/0 " + this.type.displayerName;
    }

    @Override
    public void readFromNBT(NBTTagCompound par1NBTTagCompound)
    {
        super.readFromNBT(par1NBTTagCompound);
        this.type = Liquid.getLiquid(par1NBTTagCompound.getInteger("type"));
        int vol = par1NBTTagCompound.getInteger("liquid");
        this.tank.setLiquid(Liquid.getStack(type, vol));
    }

    /**
     * Writes a tile entity to NBT.
     */
    @Override
    public void writeToNBT(NBTTagCompound par1NBTTagCompound)
    {
        super.writeToNBT(par1NBTTagCompound);
        int s = 0;
        LiquidStack stack = this.tank.getLiquid();
        if (stack != null) s = stack.amount;
        par1NBTTagCompound.setInteger("liquid", s);
        par1NBTTagCompound.setInteger("type", this.type.ordinal());
    }

    @Override
    public void handlePacketData(INetworkManager network, int packetType,
            Packet250CustomPayload packet, EntityPlayer player,
            ByteArrayDataInput data)
    {
        try
        {
            this.type = Liquid.getLiquid(data.readInt());
            this.tank.setLiquid(Liquid.getStack(this.type, data.readInt()));
        }
        catch (Exception e)
        {
            e.printStackTrace();
            System.out.print("Fail reading data for Storage tank \n");
        }

    }

    // ----------------------------
    // Liquid stuff
    // ----------------------------
    public void setType(Liquid dm)
    {
        this.type = dm;

    }

    public Liquid getType()
    {
        return this.type;
    }

    @Override
    public int fill(ForgeDirection from, LiquidStack resource, boolean doFill)
    {
        if (!Liquid.isStackEqual(resource, type)) return 0;
        return this.fill(0, resource, doFill);
    }

    @Override
    public int fill(int tankIndex, LiquidStack resource, boolean doFill)
    {
        if (resource == null || tankIndex != 0) return 0;
        if (this.isFull())
        {
            int change = 1;
            if (Liquid.getLiquid(resource).doesFlaot) change = -1;
            TileEntity tank = worldObj.getBlockTileEntity(xCoord, yCoord + change, zCoord);
            if (tank instanceof TileEntityLTank) { return ((TileEntityLTank) tank).tank.fill(resource, doFill); }
        }
        return this.tank.fill(resource, doFill);
    }

    public boolean isFull()
    {
        if (this.tank.getLiquid() == null) return false;
        if (this.tank.getLiquid().amount > 0 && this.tank.getLiquid().amount < this.tank.getCapacity()) return false;
        return true;
    }

    public TileEntityLTank getFillAbleTank(boolean top)
    {
        TileEntityLTank tank = this;
        boolean stop = false;
        int y = tank.yCoord;
        while (y > 6 && y < 255)
        {
            if (top)
            {
                y += 1;
            }
            else
            {
                y -= 1;
            }
            TileEntity ent = tank.worldObj.getBlockTileEntity(xCoord, y, zCoord);
            if (ent instanceof TileEntityLTank && ((TileEntityLTank) ent).getType() == this.type && !((TileEntityLTank) ent).isFull())
            {
                tank = (TileEntityLTank) ent;
            }
            else
            {
                break;
            }
        }
        return tank;
    }

    @Override
    public LiquidStack drain(ForgeDirection from, int maxDrain, boolean doDrain)
    {
        return this.drain(0, maxDrain, doDrain);
    }

    @Override
    public LiquidStack drain(int tankIndex, int maxDrain, boolean doDrain)
    {
        if (tankIndex != 0) return null;
        return this.tank.getLiquid();
    }

    @Override
    public ILiquidTank[] getTanks(ForgeDirection direction)
    {
        return new ILiquidTank[] { tank };
    }

    @Override
    public ILiquidTank getTank(ForgeDirection direction, LiquidStack type)
    {
        return null;
    }

    @Override
    public int presureOutput(Liquid type, ForgeDirection dir)
    {
        if (type == this.type)
        {
            if (type.doesFlaot && dir == ForgeDirection.DOWN) return type.defaultPresure;
            if (!type.doesFlaot && dir == ForgeDirection.UP) return type.defaultPresure;
        }
        return 0;
    }

    @Override
    public boolean canPressureToo(Liquid type, ForgeDirection dir)
    {
        if (type == this.type)
        {
            if (type.doesFlaot && dir == ForgeDirection.DOWN) return true;
            if (!type.doesFlaot && dir == ForgeDirection.UP) return true;
        }
        return false;
    }

    public void tradeDown()
    {
        if (this.tank.getLiquid() == null || this.tank.getLiquid().amount <= 0) return;
        TileEntity ent = worldObj.getBlockTileEntity(xCoord, yCoord - 1, zCoord);
        if (ent instanceof TileEntityLTank && ((TileEntityLTank) ent).type == this.type && !((TileEntityLTank) ent).isFull())
        {
            int f = ((TileEntityLTank) ent).tank.fill(this.tank.getLiquid(), true);
            this.tank.drain(f, true);
        }
    }

    public void tradeArround()
    {
        if (this.tank.getLiquid() == null || this.tank.getLiquid().amount <= 0) return;
        TileEntity[] ents = MHelper.getSourounding(worldObj, xCoord, yCoord, zCoord);
        for (int i = 2; i < 6; i++)
        {
            if (ents[i] instanceof TileEntityLTank && ((TileEntityLTank) ents[i]).type == this.type && !((TileEntityLTank) ents[i]).isFull())
            {
                int f = ((TileEntityLTank) ents[i]).tank.fill(this.tank.getLiquid(), true);
                this.tank.drain(f, true);
            }
            if (this.tank.getLiquid() == null || this.tank.getLiquid().amount <= 0) break;
        }
    }
}
