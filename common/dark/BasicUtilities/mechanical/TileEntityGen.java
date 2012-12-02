package dark.BasicUtilities.mechanical;

import net.minecraft.src.EntityPlayer;
import net.minecraft.src.INetworkManager;
import net.minecraft.src.Packet250CustomPayload;
import net.minecraft.src.TileEntity;
import net.minecraftforge.common.ForgeDirection;
import universalelectricity.core.electricity.ElectricityManager;
import universalelectricity.core.implement.IConductor;
import universalelectricity.core.implement.IElectricityProducer;
import universalelectricity.prefab.network.IPacketReceiver;

import com.google.common.io.ByteArrayDataInput;

import dark.BasicUtilities.BasicUtilitiesMain;
import dark.BasicUtilities.api.IForce;
import dark.Library.Util.MetaData;
import dark.Library.prefab.TileEntityMachine;

public class TileEntityGen extends TileEntityMachine implements IPacketReceiver, IForce, IElectricityProducer
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

    @Override
    public void updateEntity()
    {
        this.genAmmount = force / this.getVoltage();
        int wireCount = 0;

        facing = ForgeDirection.getOrientation(MetaData.getMeta(worldObj.getBlockMetadata(xCoord, yCoord, zCoord))).getOpposite();

        if (!this.isDisabled())
        {
            this.doAnimation();
            if (worldObj.isRemote)
            {
                for (int i = 0; i < 6; i++)
                {
                    ForgeDirection side = ForgeDirection.UNKNOWN;
                    switch (i)
                    {
                        case 0:
                            side = ForgeDirection.UP;
                            break;
                        // case 1: side = ForgeDirection.DOWN;break;
                        case 2:
                            side = ForgeDirection.NORTH;
                            break;
                        case 3:
                            side = ForgeDirection.EAST;
                            break;
                        case 4:
                            side = ForgeDirection.SOUTH;
                            break;
                        case 5:
                            side = ForgeDirection.WEST;
                            break;
                    }
                    // update number of connected wires to limit watt output per wire
                    if (side != facing && side != facing.getOpposite())
                    {
                        TileEntity tileEntity = worldObj.getBlockTileEntity(xCoord + side.offsetX, yCoord + side.offsetY, zCoord + side.offsetZ);

                        if (tileEntity instanceof IConductor)
                        {
                            if (ElectricityManager.instance.getElectricityRequired(((IConductor) tileEntity).getNetwork()) > 0)
                            {
                                this.wires[i] = (IConductor) tileEntity;
                                wireCount++;
                            }
                            else
                            {
                                this.wires[i] = null;
                            }
                        }
                        else
                        {
                            this.wires[i] = null;
                        }
                    }

                }
                // apply watts as requested to all wires connected
                for (int side = 0; side < 6; side++)
                {
                    if (wires[side] instanceof IConductor)
                    {
                        double max = wires[side].getMaxAmps();
                        ElectricityManager.instance.produceElectricity(this, wires[side], Math.min(genAmmount / wireCount, max), this.getVoltage());
                    }
                }
            }
        }
        super.updateEntity();
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

    @Override
    public Object[] getSendData()
    {
        return null;
    }

    @Override
    public String getChannel()
    {
        return BasicUtilitiesMain.CHANNEL;
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
    public boolean canConnect(ForgeDirection side)
    {
        if (side != ForgeDirection.DOWN && side != facing && side != facing.getOpposite()) { return true; }
        return false;
    }

    @Override
    public int getSizeInventory()
    {
        return 0;
    }

}
