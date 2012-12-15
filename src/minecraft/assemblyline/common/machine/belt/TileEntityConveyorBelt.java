package assemblyline.common.machine.belt;

import java.util.EnumSet;
import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraftforge.common.ForgeDirection;
import universalelectricity.core.electricity.ElectricityConnections;
import universalelectricity.core.implement.IConductor;
import universalelectricity.core.vector.Vector3;
import universalelectricity.prefab.implement.IRotatable;
import universalelectricity.prefab.network.IPacketReceiver;
import universalelectricity.prefab.network.PacketManager;
import universalelectricity.prefab.tile.TileEntityElectricityReceiver;
import assemblyline.api.ConveyorIgnore;
import assemblyline.api.IBelt;
import assemblyline.common.AssemblyLine;

import com.google.common.io.ByteArrayDataInput;

public class TileEntityConveyorBelt extends TileEntityElectricityReceiver implements IPacketReceiver, IBelt, IRotatable
{
	/**
	 * Joules required to run this thing.
	 */
	public static final int WATT_REQUEST = 5;

	/**
	 * The amount of watts received.
	 */
	public double wattsReceived = 0;

	public float speed = 0.02f;

	public float wheelRotation = 0;
	public boolean running = false;
	public boolean textureFlip = false;
	public TileEntityConveyorBelt[] adjBelts = { null, null, null, null };

	public int powerTransferRange = 0;

	public TileEntityConveyorBelt()
	{
		super();
		ElectricityConnections.registerConnector(this, EnumSet.of(ForgeDirection.DOWN));
	}

	/**
	 * Steal power from nearby belts.
	 * 
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

		if (!this.worldObj.isRemote)
		{
			for (int i = 0; i < 6; i++)
			{
				ForgeDirection inputDirection = ForgeDirection.getOrientation(i);

				TileEntity inputTile = Vector3.getTileEntityFromSide(this.worldObj, Vector3.get(this), inputDirection);

				if (inputTile != null)
				{
					if (inputTile instanceof IConductor)
					{
						if (this.wattsReceived >= this.WATT_REQUEST)
						{
							((IConductor) inputTile).getNetwork().stopRequesting(this);
						}
						else
						{
							((IConductor) inputTile).getNetwork().startRequesting(this, this.WATT_REQUEST / this.getVoltage(), this.getVoltage());
							this.wattsReceived += ((IConductor) inputTile).getNetwork().consumeElectricity(this).getWatts();
						}
					}
				}

			}
		}

		if (this.ticks % 10 == 0)
		{
			if (!worldObj.isRemote)
			{
				PacketManager.sendPacketToClients(this.getDescriptionPacket(), this.worldObj, Vector3.get(this), 15);
			}

			if (this.wattsReceived >= WATT_REQUEST)
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
				this.searchNeighborBelts();
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
			this.textureFlip = textureFlip ? false : true;
			this.wheelRotation -= this.speed;
			this.doBeltAction();
		}
	}

	/**
	 * almost unneeded but is change for each different belt type
	 */
	public void doBeltAction()
	{
		this.conveyItemsHorizontal(true, false);
	}

	/**
	 * Causes all items to be moved above the belt
	 * 
	 * @param extendLife - increases the items life
	 * @param preventPickUp - prevent a player from picking the item up
	 */
	public void conveyItemsHorizontal(boolean extendLife, boolean preventPickUp)
	{
		try
		{
			List<Entity> entityOnTop = this.getAffectedEntities();

			for (Entity entity : entityOnTop)
			{
				int direction = worldObj.getBlockMetadata(xCoord, yCoord, zCoord);

				if (!ConveyorIgnore.isIgnore(entity))
				{
					if (!(entity instanceof EntityPlayer && ((EntityPlayer) entity).isSneaking()))
					{
						if (direction == 0)
						{
							entity.motionZ -= 1 * this.speed;
							// entity.posX = this.xCoord + 0.5D;
						}
						if (direction == 1)
						{
							entity.motionX += 1 * this.speed;
							// entity.posZ = this.zCoord + 0.5D;
						}
						if (direction == 2)
						{
							entity.motionZ += 1 * this.speed;
							// entity.posX = this.xCoord + 0.5D;
						}
						if (direction == 3)
						{
							entity.motionX -= 1 * this.speed;
							// entity.posZ = this.zCoord + 0.5D;
						}
					}
				}

				if (entity instanceof EntityItem)
				{
					EntityItem entityItem = (EntityItem) entity;

					if (extendLife && entityItem.age >= 1000)
					{
						entityItem.age = 0;
					}
					if (preventPickUp && entityItem.delayBeforeCanPickup <= 1)
					{
						entityItem.delayBeforeCanPickup += 10;
					}
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	@Override
	public Packet getDescriptionPacket()
	{
		return PacketManager.getPacket(AssemblyLine.CHANNEL, this, this.wattsReceived);
	}

	/**
	 * Is this belt in the middile of two belts? Used for rendering.
	 */
	public boolean getIsMiddleBelt()
	{

		ForgeDirection front = this.getDirection();
		ForgeDirection back = this.getDirection().getOpposite();
		TileEntity fBelt = worldObj.getBlockTileEntity(xCoord + front.offsetX, yCoord + front.offsetY, zCoord + front.offsetZ);
		TileEntity BBelt = worldObj.getBlockTileEntity(xCoord + back.offsetX, yCoord + back.offsetY, zCoord + back.offsetZ);
		if (fBelt instanceof TileEntityConveyorBelt && BBelt instanceof TileEntityConveyorBelt)
		{
			ForgeDirection fD = ((TileEntityConveyorBelt) fBelt).getDirection();
			ForgeDirection BD = ((TileEntityConveyorBelt) BBelt).getDirection();
			ForgeDirection TD = this.getDirection();
			return fD == TD && BD == TD;
		}
		return false;
	}

	/**
	 * Is this belt in the front of a conveyor line? Used for rendering.
	 */
	public boolean getIsFrontCap()
	{

		ForgeDirection front = this.getDirection();
		ForgeDirection back = this.getDirection().getOpposite();
		TileEntity fBelt = worldObj.getBlockTileEntity(xCoord + front.offsetX, yCoord + front.offsetY, zCoord + front.offsetZ);
		TileEntity BBelt = worldObj.getBlockTileEntity(xCoord + back.offsetX, yCoord + back.offsetY, zCoord + back.offsetZ);
		if (fBelt instanceof TileEntityConveyorBelt)
		{
			ForgeDirection fD = ((TileEntityConveyorBelt) fBelt).getDirection();
			ForgeDirection TD = this.getDirection();
			return fD == TD;
		}
		return false;
	}

	/**
	 * Is this belt in the back of a conveyor line? Used for rendering.
	 */
	public boolean getIsBackCap()
	{

		ForgeDirection front = this.getDirection();
		ForgeDirection back = this.getDirection().getOpposite();
		TileEntity fBelt = worldObj.getBlockTileEntity(xCoord + front.offsetX, yCoord + front.offsetY, zCoord + front.offsetZ);
		TileEntity BBelt = worldObj.getBlockTileEntity(xCoord + back.offsetX, yCoord + back.offsetY, zCoord + back.offsetZ);

		if (BBelt instanceof TileEntityConveyorBelt)
		{
			ForgeDirection BD = ((TileEntityConveyorBelt) BBelt).getDirection();
			ForgeDirection TD = this.getDirection();
			return BD == TD;
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

	@Override
	public void setDirection(ForgeDirection facingDirection)
	{
		this.worldObj.setBlockMetadataWithNotify(this.xCoord, this.yCoord, this.zCoord, facingDirection.ordinal());
	}

	@Override
	public ForgeDirection getDirection()
	{
		return ForgeDirection.getOrientation(this.getBlockMetadata());
	}

	@Override
	public List<Entity> getAffectedEntities()
	{
		AxisAlignedBB bounds = AxisAlignedBB.getBoundingBox(this.xCoord, this.yCoord, this.zCoord, this.xCoord + 1, this.yCoord + 1, this.zCoord + 1);
		return worldObj.getEntitiesWithinAABB(Entity.class, bounds);
	}
}
