package fluidmech.common.machines;

import hydraulic.core.helpers.MetaGroup;
import hydraulic.core.implement.ColorCode;
import hydraulic.core.implement.IPsiCreator;
import hydraulic.core.implement.IReadOut;
import hydraulic.core.liquids.LiquidData;
import hydraulic.core.liquids.LiquidHandler;

import java.util.EnumSet;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.liquids.ITankContainer;
import net.minecraftforge.liquids.LiquidContainerRegistry;
import net.minecraftforge.liquids.LiquidStack;
import universalelectricity.core.electricity.ElectricityConnections;
import universalelectricity.core.electricity.ElectricityNetwork;
import universalelectricity.core.vector.Vector3;
import universalelectricity.prefab.network.IPacketReceiver;
import universalelectricity.prefab.network.PacketManager;
import universalelectricity.prefab.tile.TileEntityElectricityReceiver;

import com.google.common.io.ByteArrayDataInput;

import fluidmech.common.FluidMech;

public class TileEntityPump extends TileEntityElectricityReceiver implements IPacketReceiver, IReadOut, IPsiCreator
{
    public final double WATTS_PER_TICK = (400/20);
    double percentPumped = 0.0;
    double joulesReceived = 0;

    int disableTimer = 0;
    int count = 0;
    public int pos = 0;

    private boolean converted = false;
    public ColorCode color = ColorCode.BLUE;

    ForgeDirection back = ForgeDirection.EAST;
    ForgeDirection side = ForgeDirection.EAST;

    ITankContainer fillTarget = null;

    @Override
    public void initiate()
    {
        this.getConnections();
        ElectricityConnections.registerConnector(this, EnumSet.of(back, side));
        this.worldObj.notifyBlocksOfNeighborChange(this.xCoord, this.yCoord, this.zCoord, FluidMech.blockMachine.blockID);
    }

    public void getConnections()
    {
        int notchMeta = MetaGroup.getFacingMeta(worldObj.getBlockMetadata(xCoord, yCoord, zCoord));
        back = ForgeDirection.getOrientation(notchMeta);
        side = Vector3.getOrientationFromSide(back, ForgeDirection.WEST);
        if (notchMeta == 2 || notchMeta == 3)
        {
            side = side.getOpposite();
        }
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
            // consume/give away stored units
            this.chargeUp();
            TileEntity ent = worldObj.getBlockTileEntity(xCoord+side.offsetX,yCoord+side.offsetY,zCoord+side.offsetZ);
            if(ent instanceof ITankContainer)
            {
                this.fillTarget = (ITankContainer) ent;
            }else
            {
                ent = null;
            }
            if (this.canPump(xCoord, yCoord - 1, zCoord) && this.joulesReceived >= this.WATTS_PER_TICK)
            {

                joulesReceived -= this.WATTS_PER_TICK;
                this.pos++;
                if (pos >= 8)
                {
                    pos = 0;
                }
                if (percentPumped++ >= 10)
                {
                    percentPumped = 0;
                    this.drainBlock(new Vector3(xCoord, yCoord - 1, zCoord));
                }
            }
        }

        if (!this.worldObj.isRemote)
        {
            if (this.ticks % 10 == 0)
            {
                //TODO fix this to tell the client its running
                Packet packet = PacketManager.getPacket(FluidMech.CHANNEL, this, color.ordinal(),this.joulesReceived);
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
            if (dir == this.back || dir == this.side)
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
        int blockID = worldObj.getBlockId(x, y, z);
        int meta = worldObj.getBlockMetadata(x, y, z);
        LiquidData resource = LiquidHandler.getFromBlockID(blockID);

        if (this.fillTarget == null || this.fillTarget.fill(side, this.color.getLiquidData().getStack(), false) == 0) { return false; }

        if (this.isDisabled()) { return false; }

        if ((LiquidHandler.getFromBlockID(worldObj.getBlockId(x, y, z)) == null || LiquidHandler.getFromBlockID(worldObj.getBlockId(x, y, z)) == LiquidHandler.unkown)) { return false; }

        if (blockID == Block.waterMoving.blockID || blockID == Block.lavaStill.blockID) { return false; }
        if (blockID == Block.waterStill.blockID || blockID == Block.waterStill.blockID)
        {

        }
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
        if (resource == color.getLiquidData() && meta == 0 && this.fillTarget.fill(back, resource.getStack(), false) != 0)
        {

            LiquidStack stack = resource.getStack();
            stack.amount = LiquidContainerRegistry.BUCKET_VOLUME;
            int f = this.fillTarget.fill(back, this.color.getLiquidData().getStack(), true);
            if (f > 0)
            {
                worldObj.setBlockWithNotify(xCoord, yCoord - 1, zCoord, 0);
                return true;
            }
        }

        return false;
    }

    @Override
    public void handlePacketData(INetworkManager network, int packetType, Packet250CustomPayload packet, EntityPlayer player, ByteArrayDataInput data)
    {
        try
        {
            this.color = ColorCode.get(data.readInt());
            this.joulesReceived = data.readDouble();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }
    @Override
    public String getMeterReading(EntityPlayer user, ForgeDirection side)
    {
        return this.joulesReceived + "/" + this.WATTS_PER_TICK + " " + this.percentPumped;
    }

    @Override
    public int getPressureOut(LiquidData type, ForgeDirection dir)
    {
        if (type == this.color.getLiquidData() || type == LiquidHandler.unkown)
            return this.color.getLiquidData().getPressure();
        return 0;
    }

    @Override
    public boolean getCanPressureTo(LiquidData type, ForgeDirection dir)
    {
        if (dir == this.side.getOpposite() && (type == this.color.getLiquidData() || type == LiquidHandler.unkown)) { return true; }
        return false;
    }

}
