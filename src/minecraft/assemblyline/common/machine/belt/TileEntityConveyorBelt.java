package assemblyline.common.machine.belt;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import universalelectricity.prefab.implement.IRotatable;
import universalelectricity.prefab.network.IPacketReceiver;
import universalelectricity.prefab.network.PacketManager;
import assemblyline.api.IBelt;
import assemblyline.common.AssemblyLine;
import assemblyline.common.machine.TileEntityAssemblyNetwork;

import com.google.common.io.ByteArrayDataInput;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;

public class TileEntityConveyorBelt extends TileEntityAssemblyNetwork implements IPacketReceiver, IBelt, IRotatable
{
	public enum SlantType
	{
		NONE, UP, DOWN, TOP
	}

	public static final int MAX_FRAME = 13;
	public static final int MAX_SLANT_FRAME = 23;

	/**
	 * Joules required to run this thing.
	 */
	public final float acceleration = 0.01f;
	public final float maxSpeed = 0.1f;

	public float wheelRotation = 0;
	private int animFrame = 0; // this is from 0 to 15
	private SlantType slantType = SlantType.NONE;

	public List<Entity> IgnoreList = new ArrayList<Entity>();// Entities that need to be ignored to
																// prevent movement

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

			for (int i = -1; i <= 1; i++)
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
		if (FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER && this.ticks % 10 == 0)
		{
			PacketManager.sendPacketToClients(this.getDescriptionPacket());
		}

		/* PROCESSES IGNORE LIST AND REMOVES UNNEED ENTRIES */
		List<Entity> newList = new ArrayList<Entity>();
		for (Entity ent : IgnoreList)
		{
			if (this.getAffectedEntities().contains(ent))
			{
				newList.add(ent);
			}
		}
		this.IgnoreList = newList;

		if (this.isRunning() && !this.worldObj.isBlockIndirectlyGettingPowered(this.xCoord, this.yCoord, this.zCoord))
		{
			if (this.ticks % 10 == 0 && this.worldObj.isRemote && this.worldObj.getBlockId(this.xCoord - 1, this.yCoord, this.zCoord) != AssemblyLine.blockConveyorBelt.blockID && this.worldObj.getBlockId(xCoord, yCoord, zCoord - 1) != AssemblyLine.blockConveyorBelt.blockID)
			{
				this.worldObj.playSound(this.xCoord, this.yCoord, this.zCoord, "mods.assemblyline.conveyor", 0.5f, 0.7f, true);
			}

			this.wheelRotation += 40;

			if (this.wheelRotation > 360)
				this.wheelRotation = 0;

			float wheelRotPct = wheelRotation / 360f;

			// Sync the animation. Slant belts are slower.
			if (this.getSlant() == SlantType.NONE || this.getSlant() == SlantType.TOP)
			{
				this.animFrame = (int) (wheelRotPct * MAX_FRAME);
				if (this.animFrame < 0)
					this.animFrame = 0;
				if (this.animFrame > MAX_FRAME)
					this.animFrame = MAX_FRAME;
			}
			else
			{
				this.animFrame = (int) (wheelRotPct * MAX_SLANT_FRAME);
				if (this.animFrame < 0)
					this.animFrame = 0;
				if (this.animFrame > MAX_SLANT_FRAME)
					this.animFrame = MAX_SLANT_FRAME;
			}
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
		if (this.worldObj.isRemote)
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
	public void setDirection(World world, int x, int y, int z, ForgeDirection facingDirection)
	{
		this.worldObj.setBlockMetadataWithNotify(this.xCoord, this.yCoord, this.zCoord, facingDirection.ordinal(), 3);
	}

	@Override
	public ForgeDirection getDirection(IBlockAccess world, int x, int y, int z)
	{
		return ForgeDirection.getOrientation(this.getBlockMetadata());
	}

	public ForgeDirection getDirection()
	{
		return this.getDirection(worldObj, xCoord, yCoord, zCoord);
	}

	public void setDirection(ForgeDirection facingDirection)
	{
		this.setDirection(worldObj, xCoord, yCoord, zCoord, facingDirection);
	}

	@Override
	public List<Entity> getAffectedEntities()
	{
		AxisAlignedBB bounds = AxisAlignedBB.getBoundingBox(this.xCoord, this.yCoord, this.zCoord, this.xCoord + 1, this.yCoord + 1, this.zCoord + 1);
		return worldObj.getEntitiesWithinAABB(Entity.class, bounds);
	}

	public int getAnimationFrame()
	{
		if (!this.worldObj.isBlockIndirectlyGettingPowered(this.xCoord, this.yCoord, this.zCoord))
		{
			TileEntity te = null;
			te = this.worldObj.getBlockTileEntity(this.xCoord - 1, this.yCoord, this.zCoord);

			if (te != null)
			{
				if (te instanceof TileEntityConveyorBelt)
				{
					if (((TileEntityConveyorBelt) te).getSlant() == this.slantType)
						return ((TileEntityConveyorBelt) te).getAnimationFrame();
				}

			}

			te = this.worldObj.getBlockTileEntity(this.xCoord, this.yCoord, this.zCoord - 1);

			if (te != null)
			{
				if (te instanceof TileEntityConveyorBelt)
				{
					if (((TileEntityConveyorBelt) te).getSlant() == this.slantType)
						return ((TileEntityConveyorBelt) te).getAnimationFrame();
				}

			}
		}

		return this.animFrame;
	}

	/**
	 * NBT Data
	 */
	@Override
	public void readFromNBT(NBTTagCompound nbt)
	{
		super.readFromNBT(nbt);
		this.slantType = SlantType.values()[nbt.getByte("slant")];

		if (worldObj != null)
		{
			worldObj.setBlockMetadataWithNotify(this.xCoord, this.yCoord, this.zCoord, nbt.getInteger("rotation"), 3);
		}
	}

	/**
	 * Writes a tile entity to NBT.
	 */
	@Override
	public void writeToNBT(NBTTagCompound nbt)
	{
		super.writeToNBT(nbt);
		nbt.setByte("slant", (byte) this.slantType.ordinal());

		if (worldObj != null)
		{
			nbt.setInteger("rotation", worldObj.getBlockMetadata(this.xCoord, this.yCoord, this.zCoord));
		}
	}

	@Override
	public void IgnoreEntity(Entity entity)
	{
		if (!this.IgnoreList.contains(entity))
		{
			this.IgnoreList.add(entity);
		}

	}

	@Override
	public boolean canConnect(ForgeDirection direction)
	{
		return direction == ForgeDirection.DOWN;
	}
}
