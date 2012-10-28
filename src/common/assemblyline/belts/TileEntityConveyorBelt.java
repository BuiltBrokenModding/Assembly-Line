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
import universalelectricity.implement.IConductor;
import universalelectricity.prefab.TileEntityElectricityReceiver;
import universalelectricity.prefab.network.IPacketReceiver;
import universalelectricity.prefab.network.PacketManager;
import assemblyline.AssembleLine;

import com.google.common.io.ByteArrayDataInput;

public class TileEntityConveyorBelt extends TileEntityElectricityReceiver implements IPacketReceiver
{
	public double electricityStored = 0;
	public final double electricityRequired = 0.1f;
	public final double energyMax = 10;
	private float speed = -0.05F;
	public float wheelRotation = 0;
	public boolean running = false;
	public boolean flip = false;
	public TileEntityConveyorBelt[] adjBelts =
	{ null, null, null, null };

	public int clearCount = 0;
	public int range = 0;
	public boolean connected = false;
	public List<Entity> entityIgnoreList = new ArrayList<Entity>();

	// Checks Adjacent belt to see if there
	// powered. Reduces need for wire per belt
	public boolean powerNeighbor()
	{
		for (int n = 2; n < 6; n++)
		{
			ForgeDirection d = ForgeDirection.getOrientation(n);
			TileEntity ent = worldObj.getBlockTileEntity(xCoord - d.offsetX, yCoord, zCoord - d.offsetZ);
			if (ent instanceof TileEntityConveyorBelt)
			{
				adjBelts[n - 2] = (TileEntityConveyorBelt) ent;
			}
			else
			{
				adjBelts[n - 2] = null;
			}
		}
		int rr = 0;
		for (int b = 0; b < 4; b++)
		{
			if (adjBelts[b] instanceof TileEntityConveyorBelt)
			{
				TileEntityConveyorBelt belt = (TileEntityConveyorBelt) adjBelts[b];
				if (belt.range > rr)
				{
					rr = belt.getRange();
				}
			}
		}
		this.range = rr - 1;
		return false;
	}

	public int getRange()
	{
		return this.range;
	}

	@Override
	public void updateEntity()
	{
		super.updateEntity();
		
		if (this.ticks % 10 == 0)
		{
			if (worldObj.getBlockTileEntity(xCoord, yCoord - 1, zCoord) instanceof IConductor)
			{
				this.connected = true;
			}
			else
			{
				this.connected = false;
			}
			
			if (this.electricityStored >= this.electricityRequired)
			{
				this.electricityStored = Math.max(this.electricityStored - this.electricityRequired, 0);
				this.range = 20;
			}
			else
			{
				this.range = 0;
			}
			if (!this.connected)
			{
				powerNeighbor();
			}
			if (this.range > 0)
			{
				this.running = true;
			}
			else
			{
				this.running = false;
			}

			if (!worldObj.isRemote)
			{
				Packet packet = PacketManager.getPacket("asmLine", this, new Object[]
				{ running, range });
				PacketManager.sendPacketToClients(packet, worldObj, new Vector3(xCoord, yCoord, zCoord), 40);

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

			if (AssembleLine.animationOn)
			{
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
	}

	@Override
	public double wattRequest()
	{
		return energyMax - electricityStored;
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
				this.running = dataStream.readBoolean();
				this.range = dataStream.readInt();

			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}

	}

	@Override
	public void onReceive(TileEntity sender, double amps, double voltage, ForgeDirection side)
	{
		this.electricityStored += (amps * voltage);

	}

	/**
	 * Used to tell the belt not to apply velocity
	 * to some Entity in case they are being
	 * handled by another block. For example
	 * ejector
	 * 
	 * @param entity
	 */
	public void ignore(Entity entity)
	{
		if (!this.entityIgnoreList.contains(entity))
		{
			this.entityIgnoreList.add(entity);
		}

	}

	@Override
	public void readFromNBT(NBTTagCompound nbt)
	{
		super.readFromNBT(nbt);
		this.electricityStored = nbt.getDouble("energy");
	}

	/**
	 * Writes a tile entity to NBT.
	 */
	@Override
	public void writeToNBT(NBTTagCompound nbt)
	{
		super.writeToNBT(nbt);
		nbt.setDouble("energy", this.electricityStored);
	}
}
