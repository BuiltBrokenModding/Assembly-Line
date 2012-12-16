package assemblyline.common.machine.belt;

import java.util.EnumSet;
import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
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
import assemblyline.api.IBelt;
import assemblyline.common.AssemblyLine;

import com.google.common.io.ByteArrayDataInput;

public class TileEntityConveyorBelt extends TileEntityElectricityReceiver implements IPacketReceiver, IBelt, IRotatable
{
	public enum SlantType
	{
		NONE, UP, DOWN
	}

	/**
	 * Joules required to run this thing.
	 */
	public static final int WATT_REQUEST = 5;

	/**
	 * The amount of watts received.
	 */
	public double wattsReceived = 0;

	public float acceleration = 0.01f;
	public float maxSpeed = 0.3f;

	public float wheelRotation = 0;
	public boolean running = false;
	public boolean textureFlip = false;
	public TileEntityConveyorBelt[] adjBelts = { null, null, null, null };
	public int powerTransferRange = 0;
	private SlantType slantType = SlantType.NONE;

	public TileEntityConveyorBelt()
	{
		super();
		ElectricityConnections.registerConnector(this, EnumSet.of(ForgeDirection.DOWN));
	}

	public SlantType getSlant()
	{
		return slantType;
	}

	public void setSlant(SlantType slantType)
	{
		if (slantType == null)
		{
			slantType = SlantType.NONE;
		}

		this.slantType = slantType;

		PacketManager.sendPacketToClients(this.getDescriptionPacket(), this.worldObj);
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
				PacketManager.sendPacketToClients(this.getDescriptionPacket(), this.worldObj, new Vector3(this), 15);
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
			this.wheelRotation -= this.maxSpeed;
		}
	}

	@Override
	public Packet getDescriptionPacket()
	{
		return PacketManager.getPacket(AssemblyLine.CHANNEL, this, this.wattsReceived, this.slantType.ordinal());
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
				this.slantType = SlantType.values()[dataStream.readInt()];
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

	/**
	 * NBT Data
	 */
	@Override
	public void readFromNBT(NBTTagCompound nbt)
	{
		super.readFromNBT(nbt);
		this.slantType = SlantType.values()[nbt.getByte("slant")];
	}

	/**
	 * Writes a tile entity to NBT.
	 */
	@Override
	public void writeToNBT(NBTTagCompound nbt)
	{
		super.writeToNBT(nbt);

		nbt.setByte("slant", (byte) this.slantType.ordinal());
	}
}
