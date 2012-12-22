package dark.BasicUtilities.machines;

import java.util.EnumSet;

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
import universalelectricity.core.electricity.ElectricityConnections;
import universalelectricity.core.electricity.ElectricityNetwork;
import universalelectricity.core.vector.Vector3;
import universalelectricity.prefab.network.IPacketReceiver;
import universalelectricity.prefab.network.PacketManager;
import universalelectricity.prefab.tile.TileEntityElectricityReceiver;

import com.google.common.io.ByteArrayDataInput;

import dark.BasicUtilities.BasicUtilitiesMain;
import dark.BasicUtilities.api.IReadOut;
import dark.BasicUtilities.api.ITankOutputer;
import dark.BasicUtilities.api.Liquid;

public class TileEntityPump extends TileEntityElectricityReceiver implements IPacketReceiver, IReadOut, ITankOutputer
{

    double percentPumped = 0.0;
    double WATTS_PER_TICK = 400;
    double joulesReceived = 0;
    int wMax = LiquidContainerRegistry.BUCKET_VOLUME * 2;
    int disableTimer = 0;
    int count = 0;

    public Liquid type = Liquid.DEFUALT;
    public LiquidTank tank = new LiquidTank(wMax);

    @Override
    public void initiate()
    {
        ElectricityConnections.registerConnector(this, EnumSet.of(ForgeDirection.getOrientation(this.getBlockMetadata() + 2)));
        this.worldObj.notifyBlocksOfNeighborChange(this.xCoord, this.yCoord, this.zCoord, BasicUtilitiesMain.machine.blockID);
    }

    @Override
    public void onDisable(int duration)
    {
        disableTimer = duration;
    }

    @Override
    public boolean isDisabled()
    {
        if (disableTimer <= 0) { return false; }
        return true;
    }

    @Override
    public void updateEntity()
    {
        super.updateEntity();

        if (!this.worldObj.isRemote)
        {
            if (count-- <= 0)
            {
                int bBlock = worldObj.getBlockId(xCoord, yCoord - 1, zCoord);
                Liquid bellow = Liquid.getLiquidByBlock(bBlock);
                if (bellow != null)
                {
                    if (this.type != bellow && bellow != Liquid.DEFUALT)
                    {
                        this.tank.setLiquid(Liquid.getStack(bellow, 0));
                    }
                    this.type = bellow;
                }
                count = 40;
            }

            LiquidStack stack = tank.getLiquid();

            if (stack != null)
            {
                
                if (stack.amount >= 0)
                {
                    for (int i = 0; i < 6; i++)
                    {
                        ForgeDirection dir = ForgeDirection.getOrientation(i);
                        TileEntity tile = worldObj.getBlockTileEntity(xCoord + dir.offsetX, yCoord + dir.offsetY, zCoord + dir.offsetZ);

                        if (tile instanceof ITankContainer)
                        {
                            int moved = ((ITankContainer) tile).fill(dir.getOpposite(), stack, true);
                            tank.drain(moved, true);
                            if (stack.amount <= 0) break;
                        }
                    }
                }

            }

            ForgeDirection inputDirection = ForgeDirection.getOrientation(this.getBlockMetadata() + 2);
            TileEntity inputTile = Vector3.getTileEntityFromSide(this.worldObj, new Vector3(this), inputDirection);

            ElectricityNetwork network = ElectricityNetwork.getNetworkFromTileEntity(inputTile, inputDirection);

            if (network != null)
            {
                if (this.canPump())
                {
                    network.startRequesting(this, WATTS_PER_TICK / this.getVoltage(), this.getVoltage());
                    this.joulesReceived = Math.max(Math.min(this.joulesReceived + network.consumeElectricity(this).getWatts(), WATTS_PER_TICK), 0);
                }
                else
                {
                    network.stopRequesting(this);
                }
            }
            if (this.joulesReceived >= this.WATTS_PER_TICK - 50 && this.canPump())
            {

                joulesReceived -= this.WATTS_PER_TICK;
                if (percentPumped++ == 20)
                {
                    this.drainBlock(new Vector3(xCoord, yCoord - 1, zCoord));
                    percentPumped = 0;
                }
            }
        }

        if (!this.worldObj.isRemote)
        {
            if (this.ticks % 10 == 0)
            {
                Packet packet = PacketManager.getPacket(BasicUtilitiesMain.CHANNEL, this, this.type.ordinal());
                PacketManager.sendPacketToClients(packet, worldObj, new Vector3(this), 60);
            }
        }
    }

    public boolean canPump()
    {
        //if (this.tank.getLiquid() == null) return false;
        if (this.tank.getLiquid() != null && this.tank.getLiquid().amount >= this.wMax) return false;
        if (this.isDisabled()) return false;
        return true;
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
        if (bBlock == type.liquid.itemID)
        {
            int f = this.tank.fill(Liquid.getStack(this.type, LiquidContainerRegistry.BUCKET_VOLUME), true);
            if (f > 0) worldObj.setBlockWithNotify(loc.intX(), loc.intY(), loc.intZ(), 0);
            return true;
        }
        return false;
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
        int stored = par1NBTTagCompound.getInteger("liquid");
        this.type = Liquid.getLiquid(par1NBTTagCompound.getInteger("type"));
        this.tank.setLiquid(Liquid.getStack(this.type, stored));
    }

    /**
     * Writes a tile entity to NBT.
     */
    @Override
    public void writeToNBT(NBTTagCompound par1NBTTagCompound)
    {
        super.writeToNBT(par1NBTTagCompound);
        int s = 1;
        if (this.tank.getLiquid() != null) s = this.tank.getLiquid().amount;
        par1NBTTagCompound.setInteger("liquid", s);
        par1NBTTagCompound.setInteger("type", this.type.ordinal());
    }

    @Override
    public String getMeterReading(EntityPlayer user, ForgeDirection side)
    {
        int liquid = 0;
        if (this.tank.getLiquid() != null)
        {
            liquid = (this.tank.getLiquid().amount / LiquidContainerRegistry.BUCKET_VOLUME);
        }
        else
        {
            liquid = -10;
        }
        return liquid + "" + type.displayerName + " " + this.joulesReceived + "W " + this.percentPumped + "/20";
    }

    @Override
    public int fill(ForgeDirection from, LiquidStack resource, boolean doFill)
    {
        return 0;
    }

    @Override
    public int fill(int tankIndex, LiquidStack resource, boolean doFill)
    {
        return 0;
    }

    @Override
    public LiquidStack drain(ForgeDirection from, int maxDrain, boolean doDrain)
    {
        return drain(0, maxDrain, doDrain);
    }

    @Override
    public LiquidStack drain(int tankIndex, int maxDrain, boolean doDrain)
    {
        if (tankIndex == 0)
            return tank.drain(maxDrain, doDrain);

        return null;
    }

    @Override
    public ILiquidTank[] getTanks(ForgeDirection direction)
    {
        return new ILiquidTank[]
            { tank };
    }

    @Override
    public ILiquidTank getTank(ForgeDirection direction, LiquidStack type)
    {
        return null;
    }

    @Override
    public int presureOutput(Liquid type, ForgeDirection dir)
    {
        if (type == this.type) return type.defaultPresure;
        return 0;
    }

    @Override
    public boolean canPressureToo(Liquid type, ForgeDirection dir)
    {
        if (type == this.type) return true;
        return false;
    }
}
