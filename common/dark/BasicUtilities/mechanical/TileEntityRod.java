package dark.BasicUtilities.mechanical;

import net.minecraft.src.EntityPlayer;
import net.minecraft.src.INetworkManager;
import net.minecraft.src.Packet;
import net.minecraft.src.Packet250CustomPayload;
import net.minecraft.src.TileEntity;
import net.minecraftforge.common.ForgeDirection;
import universalelectricity.core.vector.Vector3;
import universalelectricity.prefab.network.IPacketReceiver;
import universalelectricity.prefab.network.PacketManager;

import com.google.common.io.ByteArrayDataInput;

import dark.BasicUtilities.BasicUtilitiesMain;
import dark.BasicUtilities.api.IForce;
import dark.BasicUtilities.api.IReadOut;

public class TileEntityRod extends TileEntity implements IPacketReceiver, IForce, IReadOut
{

    public int pos = 0;
    private int force = 0;
    private int pForce = 0;
    public int aForce = 0;
    public int forceMax = 1000;
    private int tickCount = 0;
    private int posCount = 0;

    private ForgeDirection frontDir;
    private ForgeDirection backDir;

    private TileEntity bb;
    private TileEntity ff;

    @Override
    public void updateEntity()
    {
        super.updateEntity();
        if (tickCount++ >= 10)
        {
            tickCount = 0;
            int meta = worldObj.getBlockMetadata(xCoord, yCoord, zCoord);
            frontDir = ForgeDirection.getOrientation(meta);
            backDir = ForgeDirection.getOrientation(meta).getOpposite();
            bb = worldObj.getBlockTileEntity(xCoord + backDir.offsetX, yCoord, zCoord + backDir.offsetZ);
            ff = worldObj.getBlockTileEntity(xCoord + frontDir.offsetX, yCoord, zCoord + frontDir.offsetZ);
            if (force > 0)
            {
                int posCountA = (forceMax / force) & 10;
                if (posCount++ >= posCountA)
                {
                    pos++;
                    if (pos > 7)
                    {
                        pos = 0;
                    }
                    ;
                }
            }
            if (bb instanceof TileEntityRod)
            {
                this.pos = ((IForce) bb).getAnimationPos();
            }
            if (!worldObj.isRemote)
            {

                if (ff instanceof IForce)
                {
                    if (((IForce) ff).canInputSide(backDir))
                    {
                        ((IForce) ff).applyForce(aForce);
                    }
                }
                if (bb instanceof IForce)
                {
                    if (((IForce) bb).canOutputSide(frontDir))
                    {
                        this.force = ((IForce) bb).getForce();
                    }
                }
                else
                {
                    this.force -= Math.max(force / 10, 0);
                }
                aForce = Math.max(force - 10, 0);
                if (this.force != this.pForce)
                {
                    Packet packet = PacketManager.getPacket(BasicUtilitiesMain.CHANNEL, this, new Object[]
                        { force });
                    PacketManager.sendPacketToClients(packet, worldObj, Vector3.get(this), 40);
                }
                this.pForce = this.force;
            }
        }
    }

    @Override
    public int getForceSide(ForgeDirection side)
    {
        return aForce;
    }

    @Override
    public boolean canOutputSide(ForgeDirection side)
    {
        if (side == frontDir) { return true; }
        return false;
    }

    @Override
    public boolean canInputSide(ForgeDirection side)
    {
        if (side == backDir) { return true; }
        return false;
    }

    @Override
    public int applyForce(int force)
    {
        this.force = force;
        return force;
    }

    @Override
    public void handlePacketData(INetworkManager network, int packetType,
            Packet250CustomPayload packet, EntityPlayer player,
            ByteArrayDataInput data)
    {
        try
        {
            this.force = data.readInt();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            System.out.print("MechRodDataFailure \n");
        }

    }

    @Override
    public int getAnimationPos()
    {
        return this.pos;
    }

    @Override
    public int getForce()
    {
        // TODO Auto-generated method stub
        return this.force;
    }

    @Override
    public String getMeterReading(EntityPlayer user, ForgeDirection side)
    {
        return this.aForce + "N Out " + this.force + "N In";
    }
}
