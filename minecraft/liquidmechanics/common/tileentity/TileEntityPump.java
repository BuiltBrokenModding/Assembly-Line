package liquidmechanics.common.tileentity;

import java.util.EnumSet;

import liquidmechanics.api.IReadOut;
import liquidmechanics.api.IPressure;
import liquidmechanics.api.helpers.ColorCode;
import liquidmechanics.api.helpers.LiquidData;
import liquidmechanics.api.helpers.LiquidHandler;
import liquidmechanics.common.LiquidMechanics;
import liquidmechanics.common.MetaGroup;
import liquidmechanics.common.handlers.UpdateConverter;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.liquids.ILiquidTank;
import net.minecraftforge.liquids.ITankContainer;
import net.minecraftforge.liquids.LiquidContainerData;
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

public class TileEntityPump extends TileEntityElectricityReceiver implements IPacketReceiver, IReadOut, IPressure, ITankContainer
{
    public final double WATTS_PER_TICK = 400;
    double percentPumped = 0.0;
    double joulesReceived = 0;

    int wMax = LiquidContainerRegistry.BUCKET_VOLUME * 2;
    int disableTimer = 0;
    int count = 0;

    private boolean converted = false;
    public ColorCode color = ColorCode.NONE;
    public LiquidTank tank = new LiquidTank(wMax);

    @Override
    public void initiate()
    {
        this.registerWireConnections();
        this.worldObj.notifyBlocksOfNeighborChange(this.xCoord, this.yCoord, this.zCoord, LiquidMechanics.blockMachine.blockID);
    }

    public void registerWireConnections()
    {
        int notchMeta = MetaGroup.getFacingMeta(worldObj.getBlockMetadata(xCoord, yCoord, zCoord));
        ForgeDirection facing = ForgeDirection.getOrientation(notchMeta).getOpposite();
        ForgeDirection[] dirs = new ForgeDirection[] { ForgeDirection.UNKNOWN, ForgeDirection.UNKNOWN, ForgeDirection.UNKNOWN, ForgeDirection.UNKNOWN, ForgeDirection.UNKNOWN, ForgeDirection.UNKNOWN };
        ElectricityConnections.registerConnector(this, EnumSet.of(facing.getOpposite()));
        for (int i = 2; i < 6; i++)
        {
            ForgeDirection dir = ForgeDirection.getOrientation(i);
            if (dir != facing)
            {
                dirs[i] = dir;
            }
        }
        ElectricityConnections.registerConnector(this, EnumSet.of(dirs[0], dirs[1], dirs[2], dirs[3], dirs[4], dirs[5]));
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
                count = 40;
                int bBlock = worldObj.getBlockId(xCoord, yCoord - 1, zCoord);
                LiquidData bellow = LiquidHandler.getFromBlockID(bBlock);
                if (bellow != null)
                {
                    if (this.color.getLiquidData() != bellow && bellow != LiquidHandler.unkown)
                    {
                        this.tank.setLiquid(LiquidHandler.getStack(bellow, 1));
                        this.color = bellow.getColor();
                    }

                }

            }

            if (this.tank.getLiquid() == null)
            {
                this.tank.setLiquid(LiquidHandler.getStack(this.color.getLiquidData(), 1));
            }

            // consume/give away stored units
            this.fillSurroundings();
            this.chargeUp();

            if (this.joulesReceived >= this.WATTS_PER_TICK - 50 && this.canPump(xCoord, yCoord - 1, zCoord))
            {

                joulesReceived -= this.WATTS_PER_TICK;
                if (percentPumped++ >= 20)
                {
                    this.drainBlock(new Vector3(xCoord, yCoord - 1, zCoord));
                }
            }
        }

        if (!this.worldObj.isRemote)
        {
            if (this.ticks % 10 == 0)
            {
                Packet packet = PacketManager.getPacket(LiquidMechanics.CHANNEL, this, color.ordinal());
                PacketManager.sendPacketToClients(packet, worldObj, new Vector3(this), 60);
            }
        }
    }

    /**
     * gets the search range the pump used to find valid block to pump
     */
    public int getPumpRange()
    {
        int meta = worldObj.getBlockMetadata(xCoord, yCoord, zCoord);
        switch (MetaGroup.getGrouping(meta))
        {
            case 0:
            case 1:
                return 1;
            case 2:
                return 20;
            case 3:
                return 50;
        }
        return 1;
    }

    /**
     * Cause this to empty its internal tank to surrounding tanks
     */
    public void fillSurroundings()
    {
        LiquidStack stack = tank.getLiquid();

        if (stack != null && stack.amount > 1)
        {
            for (int i = 0; i < 6; i++)
            {
                ForgeDirection dir = ForgeDirection.getOrientation(i);
                TileEntity tile = worldObj.getBlockTileEntity(xCoord + dir.offsetX, yCoord + dir.offsetY, zCoord + dir.offsetZ);

                if (tile instanceof ITankContainer)
                {
                    int moved = ((ITankContainer) tile).fill(dir.getOpposite(), stack, true);
                    tank.drain(moved, true);
                    if (stack.amount <= 0)
                    {
                        break;
                    }
                }
            }

        }
    }

    /**
     * causes this to request/drain energy from connected networks
     */
    public void chargeUp()
    {
        // this.joulesReceived += this.WATTS_PER_TICK; //TODO remove after
        // testing
        int notchMeta = MetaGroup.getFacingMeta(worldObj.getBlockMetadata(xCoord, yCoord, zCoord));
        ForgeDirection facing = ForgeDirection.getOrientation(notchMeta).getOpposite();

        for (int i = 2; i < 6; i++)
        {
            ForgeDirection dir = ForgeDirection.getOrientation(i);
            if (dir != facing)
            {
                TileEntity inputTile = Vector3.getTileEntityFromSide(this.worldObj, new Vector3(this), dir);
                ElectricityNetwork network = ElectricityNetwork.getNetworkFromTileEntity(inputTile, dir);
                if (network != null)
                {

                    if (this.canPump(xCoord, yCoord - 1, zCoord))
                    {
                        network.startRequesting(this, WATTS_PER_TICK / this.getVoltage(), this.getVoltage());
                        this.joulesReceived = Math.max(Math.min(this.joulesReceived + network.consumeElectricity(this).getWatts(), WATTS_PER_TICK), 0);
                    }
                    else
                    {
                        network.stopRequesting(this);
                    }
                }
            }
        }
    }

    public boolean canPump(int x, int y, int z)
    {
        // if (this.tank.getLiquid() == null) return false;
        if (this.tank.getLiquid() != null && this.tank.getLiquid().amount >= this.wMax)
            return false;
        if (this.isDisabled())
            return false;
        if ((LiquidHandler.getFromBlockID(worldObj.getBlockId(x, y, z)) == null || LiquidHandler.getFromBlockID(worldObj.getBlockId(x, y, z)) == LiquidHandler.unkown))
            return false;
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
        int blockID = worldObj.getBlockId(loc.intX(), loc.intY(), loc.intZ());
        int meta = worldObj.getBlockMetadata(loc.intX(), loc.intY(), loc.intZ());
        LiquidData resource = LiquidHandler.getFromBlockID(blockID);
        if (resource == color.getLiquidData())
        {
            worldObj.setBlockWithNotify(xCoord, yCoord - 1, zCoord, 0);
            LiquidStack stack = resource.getStack();
            stack.amount = LiquidContainerRegistry.BUCKET_VOLUME;
            this.tank.fill(stack, true);
            return true;
        }

        return false;
    }

    @Override
    public void handlePacketData(INetworkManager network, int packetType, Packet250CustomPayload packet, EntityPlayer player, ByteArrayDataInput data)
    {
        try
        {
            this.color = (ColorCode.get(data.readInt()));
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

        LiquidStack liquid = new LiquidStack(0, 0, 0);
        liquid.readFromNBT(nbt.getCompoundTag("stored"));
        tank.setLiquid(liquid);
    }

    /**
     * Writes a tile entity to NBT.
     */
    @Override
    public void writeToNBT(NBTTagCompound nbt)
    {
        super.writeToNBT(nbt);
        if (tank.getLiquid() != null)
        {
            nbt.setTag("stored", tank.getLiquid().writeToNBT(new NBTTagCompound()));
        }
    }

    @Override
    public String getMeterReading(EntityPlayer user, ForgeDirection side)
    {
        LiquidStack stack = this.tank.getLiquid();
        if (stack != null) { return (stack.amount / LiquidContainerRegistry.BUCKET_VOLUME) + "/" + (this.tank.getCapacity() / LiquidContainerRegistry.BUCKET_VOLUME) + " " + LiquidHandler.get(stack).getName() + " " + this.joulesReceived + "/" + this.WATTS_PER_TICK + " " + this.percentPumped; }

        return "Empty";
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
        return null;
    }

    @Override
    public LiquidStack drain(int tankIndex, int maxDrain, boolean doDrain)
    {
        return null;
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
    public int presureOutput(LiquidData type, ForgeDirection dir)
    {
        if (type == this.color.getLiquidData() || type == LiquidHandler.unkown)
            return this.color.getLiquidData().getPressure();
        return 0;
    }

    @Override
    public boolean canPressureToo(LiquidData type, ForgeDirection dir)
    {
        if (type == this.color.getLiquidData() || type == LiquidHandler.unkown) { return true; }
        return false;
    }

}
