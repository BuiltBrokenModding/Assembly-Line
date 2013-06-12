package dark.mech.common.machines;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.ForgeDirection;
import universalelectricity.core.vector.Vector3;
import universalelectricity.prefab.network.IPacketReceiver;
import universalelectricity.prefab.network.PacketManager;

import com.google.common.io.ByteArrayDataInput;

import dark.core.api.tools.IReadOut;
import dark.fluid.api.mech.IForce;
import dark.fluid.common.FluidMech;


public class TileEntityRod extends TileEntity implements IPacketReceiver, IForce, IReadOut
{

	public int pos = 0;
	private int currentForce = 0;// current force given to rod
	private int pasteForce = 0;// last update force count
	public int appliedForce = 0;// force this rod can apply to other things
	private int tickCount = 0;
	private int posCount = 0;// animation position 0-8

	private ForgeDirection facing = ForgeDirection.UNKNOWN;

	@Override
	public void updateEntity()
	{
		super.updateEntity();
		if (tickCount++ >= 10)
		{
			tickCount = 0;
			int meta = worldObj.getBlockMetadata(xCoord, yCoord, zCoord);
			facing = ForgeDirection.getOrientation(meta);
			if (this.currentForce > 0)
			{
				this.pos++;
				if (pos >= 8)
					pos = 0;
			}
			if (!worldObj.isRemote)
			{
				TileEntity ent = worldObj.getBlockTileEntity(xCoord + facing.offsetX, yCoord + facing.offsetY, zCoord + facing.offsetZ);
				appliedForce = Math.max(currentForce - 20, 0);
				if (ent instanceof IForce && (((IForce) ent).canInputSide(facing)))
				{
					((IForce) ent).applyForce(appliedForce);
				}

				if (this.currentForce != this.pasteForce)
				{
					Packet packet = PacketManager.getPacket(FluidMech.CHANNEL, this, new Object[] { currentForce });
					PacketManager.sendPacketToClients(packet, worldObj, new Vector3(this), 40);
				}
				this.pasteForce = this.currentForce;
			}
		}
	}

	@Override
	public int getForceSide(ForgeDirection side)
	{
		return appliedForce;
	}

	@Override
	public boolean canOutputSide(ForgeDirection side)
	{
		if (side == facing || side == facing.getOpposite())
		{
			return true;
		}
		return false;
	}

	@Override
	public boolean canInputSide(ForgeDirection side)
	{
		if (side == facing || side == facing.getOpposite())
		{
			return true;
		}
		return false;
	}

	@Override
	public int applyForce(int force)
	{
		this.currentForce = force;
		return force;
	}

	@Override
	public void handlePacketData(INetworkManager network, int packetType, Packet250CustomPayload packet, EntityPlayer player, ByteArrayDataInput data)
	{
		try
		{
			this.currentForce = data.readInt();
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
	public String getMeterReading(EntityPlayer user, ForgeDirection side)
	{
		return this.appliedForce + "N Out " + this.currentForce + "N In";
	}
}
