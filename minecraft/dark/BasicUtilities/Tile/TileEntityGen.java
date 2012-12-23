package dark.BasicUtilities.Tile;

import java.util.EnumSet;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.ForgeDirection;
import universalelectricity.core.electricity.ElectricityConnections;
import universalelectricity.core.electricity.ElectricityNetwork;
import universalelectricity.core.implement.IConductor;
import universalelectricity.core.vector.Vector3;
import universalelectricity.prefab.network.IPacketReceiver;
import universalelectricity.prefab.tile.TileEntityElectricityProducer;

import com.google.common.io.ByteArrayDataInput;

import dark.BasicUtilities.BasicUtilitiesMain;
import dark.BasicUtilities.api.IForce;
import dark.BasicUtilities.api.IReadOut;
import dark.BasicUtilities.api.MHelper;

public class TileEntityGen extends TileEntityElectricityProducer implements IPacketReceiver, IForce, IReadOut
{
    ForgeDirection facing = ForgeDirection.DOWN;

    public int force = 0;// current total force
    public int aForce = 0;// force this unit can apply
    public int pos = 0;// current pos of rotation max of 8
    public int disableTicks = 0;// time disabled
    public double genAmmount = 0;// watt output of machine
    public int tCount = 0;

    IConductor[] wires =
        { null, null, null, null, null, null };

    public boolean needUpdate()
    {
        return false;
    }

    public void initiate()
    {
        ElectricityConnections.registerConnector(this, EnumSet.of(ForgeDirection.UP));
        ElectricityConnections.registerConnector(this, EnumSet.of(ForgeDirection.DOWN));
        this.worldObj.notifyBlocksOfNeighborChange(this.xCoord, this.yCoord, this.zCoord, BasicUtilitiesMain.generator.blockID);
    }

    @Override
    public void updateEntity()
    {
        this.genAmmount = force / this.getVoltage();

        int wireCount = 0;
        TileEntity[] ents = MHelper.getSourounding(worldObj, xCoord, yCoord, zCoord);
        this.wires = new IConductor[6];
        for (int i = 0; i < ents.length; i++)
        {
            if (ents[i] instanceof IConductor)
            {
                this.wires[i] = (IConductor) ents[i];
                wireCount++;
            }
        }
        if (!this.worldObj.isRemote)
        {
            for (int i = 0; i < 6; i++)
            {
                //TODO set up for other sides
                if (i == 0 || i == 1)
                {
                    ForgeDirection outputDirection = ForgeDirection.getOrientation(i);
                    TileEntity outputTile = Vector3.getConnectorFromSide(this.worldObj, new Vector3(this.xCoord, this.yCoord, this.zCoord), outputDirection);

                    ElectricityNetwork network = ElectricityNetwork.getNetworkFromTileEntity(outputTile, outputDirection);
                    this.outputEnergy(network, wires[i], outputTile);
                }
            }
        }

        super.updateEntity();
    }

    public void outputEnergy(ElectricityNetwork network, IConductor connectedElectricUnit, TileEntity outputTile)
    {
        if (network != null)
        {
            if (network.getRequest().getWatts() > 0)
            {
                connectedElectricUnit = (IConductor) outputTile;
            }
            else
            {
                connectedElectricUnit = null;
            }
        }
        else
        {
            connectedElectricUnit = null;
        }

        if (connectedElectricUnit != null)
        {
            if (this.genAmmount > 0)
            {
                connectedElectricUnit.getNetwork().startProducing(this, (this.genAmmount / this.getVoltage()) / 20, this.getVoltage());
            }
            else
            {
                connectedElectricUnit.getNetwork().stopProducing(this);
            }
        }

    }

    /**
     * does the basic animation for the model
     */
    public void doAnimation()
    {
        if (worldObj.isRemote)
        {
            this.pos += 1;
            if (pos >= 8 || pos < 0)
            {
                pos = 0;
            }
        }
    }

    // ------------------------------
    // Data handling
    // ------------------------------
    @Override
    public void handlePacketData(INetworkManager network, int packetType, Packet250CustomPayload packet, EntityPlayer player, ByteArrayDataInput data)
    {
        // TODO Auto-generated method stub

    }
    // ------------------------------
    // Mechanics
    // ------------------------------
    @Override
    public int getForceSide(ForgeDirection side)
    {
        if (side == facing.getOpposite()) { return aForce; }
        return 0;
    }

    @Override
    public int getForce()
    {
        return this.force;
    }

    @Override
    public boolean canOutputSide(ForgeDirection side)
    {
        if (side == facing.getOpposite()) { return true; }
        return false;
    }

    @Override
    public boolean canInputSide(ForgeDirection side)
    {
        if (side == facing) { return true; }
        return false;
    }

    @Override
    public int applyForce(int force)
    {
        this.force = force;
        return force;
    }

    @Override
    public int getAnimationPos()
    {
        return pos;
    }

    // ------------------------------
    // Electric
    // ------------------------------
    @Override
    public void onDisable(int duration)
    {
        this.disableTicks = duration;
    }

    @Override
    public boolean isDisabled()
    {
        if (disableTicks-- <= 0) { return false; }
        return true;
    }

    @Override
    public double getVoltage()
    {
        return 120;
    }

    @Override
    public String getMeterReading(EntityPlayer user, ForgeDirection side)
    {
        return this.force + "N Input " + this.genAmmount + "W output";
    }

}
