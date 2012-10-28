package assemblyline.belts;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.src.AxisAlignedBB;
import net.minecraft.src.Entity;
import net.minecraft.src.EntityItem;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.INetworkManager;
import net.minecraft.src.NBTTagCompound;
import net.minecraft.src.Packet;
import net.minecraft.src.Packet250CustomPayload;
import net.minecraft.src.TileEntity;
import net.minecraftforge.common.ForgeDirection;
import universalelectricity.core.Vector3;
import universalelectricity.electricity.ElectricInfo;
import universalelectricity.implement.IConductor;
import universalelectricity.prefab.TileEntityElectricityReceiver;
import universalelectricity.prefab.network.IPacketReceiver;
import universalelectricity.prefab.network.PacketManager;
import assemblyline.AssemblyLine;

import com.google.common.io.ByteArrayDataInput;

public class TileEntityConveyorBelt extends TileEntityElectricityReceiver implements IPacketReceiver
{
	/**
	 * Joules required to run this thing.
	 */
	public static final int JOULES_REQUIRED = 5;

	/**
	 * The amount of watts received.
	 */
	public double wattsReceived = 0;

	private float speed = -0.045F;
	public float wheelRotation = 0;
	public boolean running = false;
	public boolean flip = false;
	public TileEntityConveyorBelt[] adjBelts =
	{ null, null, null, null };

	public int clearCount = 0;
	public int powerTransferRange = 0;
	public List<Entity> entityIgnoreList = new ArrayList<Entity>();

	/**
	 * Steal power from nearby belts.
	 * @return
	 */
	public boolean searchNeighborBelts()
	{
		for (int i = 2; i < 6; i++)
		{
			ForgeDirection direction = ForgeDirection.getOrientation(i);
			TileEntity tileEntity = worldObj.getBlockTileEntity(xCoord - direction.offsetX, yCoord, zCoord - direction.offsetZ);

			if (tileEntity instanceof TileEntityConveyorBelt)
			{
				adjBelts[i - 2] = (TileEntityConveyorBelt) tileEntity;
			}
			else
			{
				adjBelts[i - 2] = null;
			}
		}

		int rr = 0;
		for (int b = 0; b < 4; b++)
		{
			if (adjBelts[b] instanceof TileEntityConveyorBelt)
			{
				TileEntityConveyorBelt belt = (TileEntityConveyorBelt) adjBelts[b];
				if (belt.powerTransferRange > rr)
				{
					rr = belt.powerTransferRange;
				}
			}
		}
		
		this.powerTransferRange = rr - 1;
		return false;
	}

	@Override
	public void updateEntity()
	{
		super.updateEntity();

		if (this.ticks % 20 == 0)
		{
			if (!worldObj.isRemote)
			{
				PacketManager.sendPacketToClients(this.getDescriptionPacket(), this.worldObj, Vector3.get(this), 15);
			}

			if (this.wattsReceived >= JOULES_REQUIRED)
			{
				this.wattsReceived = 0;
				this.powerTransferRange = 20;
			}
			else
			{
				this.powerTransferRange = 0;
			}

			if (!(worldObj.getBlockTileEntity(xCoord, yCoord - 1, zCoord) instanceof IConductor))
			{
				searchNeighborBelts();
			}

			if (this.powerTransferRange > 0)
			{
				this.running = true;
			}
			else
			{
				this.running = false;
			}

		}
		
		if (this.running)
		{
			AxisAlignedBB bounds = AxisAlignedBB.getBoundingBox(this.xCoord, this.yCoord, this.zCoord, this.xCoord + 1, this.yCoord + 1, this.zCoord + 1);
			try
			{
				List<Entity> entityOnTop = worldObj.getEntitiesWithinAABB(Entity.class, bounds);

				for (Entity entity : entityOnTop)
				{
					int direction = worldObj.getBlockMetadata(xCoord, yCoord, zCoord);
					if (!this.entityIgnoreList.contains(entity))
					{
						if (direction == 0)
						{
							entity.motionZ -= 1 * this.speed;
						}
						if (direction == 1)
						{
							entity.motionX += 1 * this.speed;
						}
						if (direction == 2)
						{
							entity.motionZ += 1 * this.speed;
						}
						if (direction == 3)
						{
							entity.motionX -= 1 * this.speed;
						}
					}
					if (this.clearCount++ >= 4)
					{
						// clear the temp ignore
						// list every 2 second
						this.entityIgnoreList.clear();
					}
					if (entity instanceof EntityItem)
					{
						EntityItem entityItem = (EntityItem) entity;
						// Make sure the item
						// doesn't decay/disappear
						if (entityItem.age >= 1000)
						{
							entityItem.age = 0;
						}
					}
				}
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}

			if (flip == true)
			{
				flip = false;
			}
			else
			{
				flip = true;
			}

			this.wheelRotation -= this.speed;
		}
	}

	@Override
	public Packet getDescriptionPacket()
    {
		return PacketManager.getPacket(AssemblyLine.CHANNEL, this, this.wattsReceived);
    }
	
	@Override
	public double wattRequest()
	{
		return JOULES_REQUIRED;
	}

	@Override
	public void onReceive(TileEntity sender, double amps, double voltage, ForgeDirection side)
	{
		this.wattsReceived += ElectricInfo.getWatts(amps, voltage);
	}

	@Override
	public boolean canReceiveFromSide(ForgeDirection side)
	{
		return side == ForgeDirection.DOWN;
	}

	public int getBeltDirection()
	{
		int meta = worldObj.getBlockMetadata(this.xCoord, this.yCoord, this.zCoord);
		if (meta >= 0 && meta < 4)
		{
			switch (meta)
			{
				case 0:
					return 2;
				case 1:
					return 5;
				case 2:
					return 3;
				case 3:
					return 4;
			}
		}
		return 0;
	}

	public boolean middleBelt()
	{

		ForgeDirection front = ForgeDirection.getOrientation(getBeltDirection());
		ForgeDirection back = ForgeDirection.getOrientation(getBeltDirection()).getOpposite();
		TileEntity fBelt = worldObj.getBlockTileEntity(xCoord + front.offsetX, yCoord + front.offsetY, zCoord + front.offsetZ);
		TileEntity BBelt = worldObj.getBlockTileEntity(xCoord + back.offsetX, yCoord + back.offsetY, zCoord + back.offsetZ);
		if (fBelt instanceof TileEntityConveyorBelt && BBelt instanceof TileEntityConveyorBelt)
		{
			int fD = ((TileEntityConveyorBelt) fBelt).getBeltDirection();
			int BD = ((TileEntityConveyorBelt) BBelt).getBeltDirection();
			int TD = this.getBeltDirection();
			if (fD == TD && BD == TD) { return true; }
		}
		return false;
	}

	public boolean FrontCap()
	{

		ForgeDirection front = ForgeDirection.getOrientation(getBeltDirection());
		ForgeDirection back = ForgeDirection.getOrientation(getBeltDirection()).getOpposite();
		TileEntity fBelt = worldObj.getBlockTileEntity(xCoord + front.offsetX, yCoord + front.offsetY, zCoord + front.offsetZ);
		TileEntity BBelt = worldObj.getBlockTileEntity(xCoord + back.offsetX, yCoord + back.offsetY, zCoord + back.offsetZ);
		if (fBelt instanceof TileEntityConveyorBelt)
		{
			int fD = ((TileEntityConveyorBelt) fBelt).getBeltDirection();
			int TD = this.getBeltDirection();
			if (fD == TD) { return true; }
		}
		return false;
	}

	public boolean BackCap()
	{

		ForgeDirection front = ForgeDirection.getOrientation(getBeltDirection());
		ForgeDirection back = ForgeDirection.getOrientation(getBeltDirection()).getOpposite();
		TileEntity fBelt = worldObj.getBlockTileEntity(xCoord + front.offsetX, yCoord + front.offsetY, zCoord + front.offsetZ);
		TileEntity BBelt = worldObj.getBlockTileEntity(xCoord + back.offsetX, yCoord + back.offsetY, zCoord + back.offsetZ);
		if (BBelt instanceof TileEntityConveyorBelt)
		{
			int BD = ((TileEntityConveyorBelt) BBelt).getBeltDirection();
			int TD = this.getBeltDirection();
			if (BD == TD) { return true; }
		}
		return false;
	}

	@Override
	public void handlePacketData(INetworkManager network, int packetType, Packet250CustomPayload packet, EntityPlayer player, ByteArrayDataInput dataStream)
	{
		if (worldObj.isRemote)
		{
			try
			{
				this.wattsReceived = dataStream.readDouble();
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}

	}

	/**
	 * Used to tell the belt not to apply velocity
	 * to some Entity in case they are being
	 * handled by another block. For example
	 * ejector
	 * 
	 * @param entity
	 */
	public void ignoreEntity(Entity entity)
	{
		if (!this.entityIgnoreList.contains(entity))
		{
			this.entityIgnoreList.add(entity);
		}

	}
}
