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
import universalelectricity.core.vector.Vector3;
import universalelectricity.prefab.implement.IRotatable;
import universalelectricity.prefab.network.IPacketReceiver;
import universalelectricity.prefab.network.PacketManager;
import assemblyline.api.IBelt;
import assemblyline.common.AssemblyLine;
import assemblyline.common.machine.TileEntityAssemblyNetwork;

import com.google.common.io.ByteArrayDataInput;

public class TileEntityConveyorBelt extends TileEntityAssemblyNetwork implements IPacketReceiver, IBelt, IRotatable
{
	public enum SlantType
	{
		NONE, UP, DOWN
	}

	/**
	 * Joules required to run this thing.
	 */
	public float acceleration = 0.01f;
	public float maxSpeed = 0.3f;

	public float wheelRotation = 0;
	private SlantType slantType = SlantType.NONE;

	public TileEntityConveyorBelt()
	{
		super();
		ElectricityConnections.registerConnector(this, EnumSet.of(ForgeDirection.DOWN));
	}

	/**
	 * This function is overriden to allow conveyor belts to power belts that are diagonally going
	 * up.
	 */
	@Override
	public void updatePowerTransferRange()
	{
		int maximumTransferRange = 0;

		for (int i = 0; i < 6; i++)
		{
			ForgeDirection direction = ForgeDirection.getOrientation(i);
			TileEntity tileEntity = worldObj.getBlockTileEntity(this.xCoord + direction.offsetX, this.yCoord + direction.offsetY, this.zCoord + direction.offsetZ);

			if (tileEntity != null)
			{
				if (tileEntity instanceof TileEntityAssemblyNetwork)
				{
					TileEntityAssemblyNetwork assemblyNetwork = (TileEntityAssemblyNetwork) tileEntity;

					if (assemblyNetwork.powerTransferRange > maximumTransferRange)
					{
						maximumTransferRange = assemblyNetwork.powerTransferRange;
					}
				}
			}
		}
		
		for (int d = 0; d <= 1; d++)
		{
			ForgeDirection direction = this.getDirection();

			if (d == 1)
			{
				direction = direction.getOpposite();
			}

			for (int i = -1; i < 1; i++)
			{
				TileEntity tileEntity = worldObj.getBlockTileEntity(this.xCoord + direction.offsetX, this.yCoord + i, this.zCoord + direction.offsetZ);
				if (tileEntity != null)
				{
					if (tileEntity instanceof TileEntityAssemblyNetwork)
					{
						TileEntityAssemblyNetwork assemblyNetwork = (TileEntityAssemblyNetwork) tileEntity;

						if (assemblyNetwork.powerTransferRange > maximumTransferRange)
						{
							maximumTransferRange = assemblyNetwork.powerTransferRange;
						}
					}
				}
			}
		}

		this.powerTransferRange = Math.max(maximumTransferRange - 1, 0);
	}

	@Override
	public void onUpdate()
	{
		if (!worldObj.isRemote && this.ticks % 20 == 0)
		{
			PacketManager.sendPacketToClients(this.getDescriptionPacket(), this.worldObj, new Vector3(this), 20);
		}

		if (this.isRunning())
		{
			this.wheelRotation += 2;

			if (this.wheelRotation > 360)
				this.wheelRotation = 0;
		}

	}

	@Override
	protected int getMaxTransferRange()
	{
		return 20;
	}

	@Override
	public Packet getDescriptionPacket()
	{
		return PacketManager.getPacket(AssemblyLine.CHANNEL, this, this.wattsReceived, this.slantType.ordinal());
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
	 * Is this belt in the front of a conveyor line? Used for rendering.
	 */
	public boolean getIsFirstBelt()
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
	 * Is this belt in the back of a conveyor line? Used for rendering.
	 */
	public boolean getIsLastBelt()
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
