package fluidmech.common.machines;

import fluidmech.common.FluidMech;
import hydraulic.api.ColorCode;
import hydraulic.api.IColorCoded;
import hydraulic.api.IPipeConnection;
import hydraulic.api.IReadOut;
import hydraulic.core.liquidNetwork.LiquidData;
import hydraulic.core.liquidNetwork.LiquidHandler;
import hydraulic.helpers.connectionHelper;

import java.util.Random;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.liquids.ILiquidTank;
import net.minecraftforge.liquids.ITankContainer;
import net.minecraftforge.liquids.LiquidContainerRegistry;
import net.minecraftforge.liquids.LiquidStack;
import net.minecraftforge.liquids.LiquidTank;
import universalelectricity.core.block.IConductor;
import universalelectricity.core.block.IConnectionProvider;
import universalelectricity.prefab.network.IPacketReceiver;
import universalelectricity.prefab.network.PacketManager;
import universalelectricity.prefab.tile.TileEntityAdvanced;

import com.google.common.io.ByteArrayDataInput;

public class TileEntityTank extends TileEntityAdvanced implements IPacketReceiver, IReadOut, IPipeConnection, ITankContainer, IColorCoded, IConnectionProvider
{
	public TileEntity[] connectedBlocks = { null, null, null, null, null, null };

	public static final int LMax = 4;

	private Random random = new Random();

	private LiquidTank tank = new LiquidTank(LiquidContainerRegistry.BUCKET_VOLUME * LMax);

	@Override
	public void initiate()
	{
		this.updateAdjacentConnections();
	}

	public void updateEntity()
	{
		if (this.ticks % (random.nextInt(10) * 4 + 20) == 0)
		{
			updateAdjacentConnections();
		}
		if (!worldObj.isRemote)
		{
			int originalVolume = 0;

			if (this.tank.getLiquid() != null)
			{
				originalVolume = this.tank.getLiquid().amount;

				if (ticks % (random.nextInt(4) * 5 + 10) >= 0)
				{
					this.fillTanksAround();
					this.fillTankBellow();
				}

				if ((this.tank.getLiquid() == null && originalVolume != 0) || (this.tank.getLiquid() != null && this.tank.getLiquid().amount != originalVolume))
				{
					this.worldObj.markBlockForUpdate(this.xCoord, this.yCoord, this.zCoord);
				}
			}

			if (ticks % (random.nextInt(5) * 10 + 20) == 0)
			{
				this.worldObj.markBlockForUpdate(this.xCoord, this.yCoord, this.zCoord);
			}

		}
	}

	@Override
	public Packet getDescriptionPacket()
	{
		if (this.getStack() != null)
		{
			return PacketManager.getPacket(FluidMech.CHANNEL, this, this.getStack().itemID, this.getStack().amount, this.getStack().itemMeta);
		}
		else
		{
			return PacketManager.getPacket(FluidMech.CHANNEL, this, 0, 0, 0);
		}
	}

	public LiquidStack getStack()
	{
		return tank.getLiquid();
	}

	@Override
	public String getMeterReading(EntityPlayer user, ForgeDirection side)
	{
		if (tank.getLiquid() == null)
		{
			return "Empty";
		}
		return String.format("%d/%d %S Stored", tank.getLiquid().amount / LiquidContainerRegistry.BUCKET_VOLUME, tank.getCapacity() / LiquidContainerRegistry.BUCKET_VOLUME, LiquidHandler.get(tank.getLiquid()).getName());
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt)
	{
		super.readFromNBT(nbt);

		LiquidStack liquid = new LiquidStack(0, 0, 0);
		liquid.readFromNBT(nbt.getCompoundTag("stored"));
		if (!liquid.isLiquidEqual(LiquidHandler.unkown.getStack()))
		{
			tank.setLiquid(liquid);
		}
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt)
	{
		super.writeToNBT(nbt);
		if (tank.getLiquid() != null)
		{
			nbt.setTag("stored", tank.getLiquid().writeToNBT(new NBTTagCompound()));
		}
	}

	@Override
	public void handlePacketData(INetworkManager network, int packetType, Packet250CustomPayload packet, EntityPlayer player, ByteArrayDataInput data)
	{
		try
		{
			this.tank.setLiquid(new LiquidStack(data.readInt(), data.readInt(), data.readInt()));
		}
		catch (Exception e)
		{
			e.printStackTrace();
			System.out.print("Fail reading data for Storage tank \n");
		}

	}

	@Override
	public int fill(ForgeDirection from, LiquidStack resource, boolean doFill)
	{
		if (resource == null || (this.getColor() != ColorCode.NONE && !getColor().getLiquidData().getStack().isLiquidEqual(resource)))
		{
			// TODO add if liquids are not equal but can still be accept cause mixing
			return 0;
		}

		LiquidData data = LiquidHandler.get(resource);

		if ((data.getCanFloat() && from == ForgeDirection.DOWN) || !data.getCanFloat() && from == ForgeDirection.UP)
		{
			return 0;
		}
		return this.fill(0, resource, doFill);
	}

	@Override
	public int fill(int tankIndex, LiquidStack resource, boolean doFill)
	{
		if (resource == null || tankIndex != 0)
		{
			return 0;
		}

		if (this.isFull())
		{
			int change = 1;
			if (LiquidHandler.get(resource).getCanFloat())
			{
				change = -1;
			}
			TileEntity tank = worldObj.getBlockTileEntity(xCoord, yCoord + change, zCoord);
			if (tank instanceof TileEntityTank)
			{
				return ((TileEntityTank) tank).fill(0, resource, doFill);
			}
		}
		return this.tank.fill(resource, doFill);
	}

	/**
	 * is the tank full
	 */
	public boolean isFull()
	{
		if (this.tank.getLiquid() == null)
		{
			return false;
		}
		if (this.tank.getLiquid().amount < this.tank.getCapacity())
		{
			return false;
		}
		return true;
	}

	@Override
	public LiquidStack drain(ForgeDirection from, int maxDrain, boolean doDrain)
	{
		if(from  == ForgeDirection.UP || from  == ForgeDirection.DOWN)
		{
			return null;
		}
		return this.drain(0, maxDrain, doDrain);
	}

	@Override
	public LiquidStack drain(int tankIndex, int maxDrain, boolean doDrain)
	{
		if (tankIndex != 0 || this.tank.getLiquid() == null)
		{
			return null;
		}
		LiquidStack stack = this.tank.getLiquid();
		if (maxDrain < this.tank.getLiquid().amount)
		{
			stack = LiquidHandler.getStack(stack, maxDrain);
		}
		if (doDrain)
		{
			this.tank.drain(maxDrain, doDrain);
		}
		return stack;
	}

	@Override
	public ILiquidTank[] getTanks(ForgeDirection direction)
	{
		return new ILiquidTank[] { tank };
	}

	@Override
	public ILiquidTank getTank(ForgeDirection direction, LiquidStack resource)
	{
		if (getColor().isValidLiquid(resource))
		{
			LiquidData data = LiquidHandler.get(resource);
			if ((data.getCanFloat() && direction == ForgeDirection.DOWN) || !data.getCanFloat() && direction == ForgeDirection.UP)
			{
				return null;
			}
		}
		return tank;

	}

	/** Cause this TE to trade liquid with the Tanks around it to level off */
	public void fillTanksAround()
	{
		if (this.tank.getLiquid() == null || this.tank.getLiquid().amount <= 0)
		{
			return;
		}

		TileEntity[] ents = connectionHelper.getSurroundingTileEntities(worldObj, xCoord, yCoord, zCoord);

		int commonVol = this.tank.getLiquid().amount;
		int tanks = 1;

		for (int i = 2; i < 6; i++)
		{
			if (ents[i] instanceof TileEntityTank && ((TileEntityTank) ents[i]).getColor() == this.getColor())
			{
				tanks++;
				if (((TileEntityTank) ents[i]).tank.getLiquid() != null)
				{
					commonVol += ((TileEntityTank) ents[i]).tank.getLiquid().amount;
				}
			}
		}

		int equalVol = commonVol / tanks;

		for (int i = 2; i < 6; i++)
		{
			if (this.tank.getLiquid() == null || this.tank.getLiquid().amount <= equalVol)
			{
				break;
			}

			if (ents[i] instanceof TileEntityTank && ((TileEntityTank) ents[i]).getColor() == this.getColor() && !((TileEntityTank) ents[i]).isFull())
			{
				LiquidStack target = ((TileEntityTank) ents[i]).tank.getLiquid();
				LiquidStack filling = this.tank.getLiquid();

				if (target == null)
				{
					filling = LiquidHandler.getStack(this.tank.getLiquid(), equalVol);
				}
				else if (target.amount < equalVol)
				{
					filling = LiquidHandler.getStack(this.tank.getLiquid(), equalVol - target.amount);
				}
				else
				{
					filling = null;
				}
				int f = ((TileEntityTank) ents[i]).tank.fill(filling, true);
				this.tank.drain(f, true);
			}

		}
	}

	/** Will fill the ITankContainer bellow with up to one bucket of liquid a request */
	public void fillTankBellow()
	{
		if (this.tank.getLiquid() == null || this.tank.getLiquid().amount <= 0)
		{
			return;
		}
		/* GET DATA FOR THE LIQUID IN THE INTERNAL TANK */
		LiquidData liquidData = LiquidHandler.get(this.tank.getLiquid());

		if (liquidData != null)
		{
			/* GET THE TILE ABOVE OR BELLOW BASE ON LIQUID DATA */
			ForgeDirection fillDirection = liquidData.getCanFloat() ? ForgeDirection.UP : ForgeDirection.DOWN;
			TileEntity tileEntity = worldObj.getBlockTileEntity(xCoord, yCoord + fillDirection.offsetY, zCoord);

			if (tileEntity instanceof ITankContainer)
			{
				/* DO CHECK FOR NON-MATCHING COLOR CODE */
				if (tileEntity instanceof IColorCoded && ((IColorCoded) tileEntity).getColor() != ColorCode.NONE && ((IColorCoded) tileEntity).getColor() != this.getColor())
				{
					return;
				}
				/* CAN ONLY TRADE ONE BUCKET AT A TIME */
				int vol = LiquidContainerRegistry.BUCKET_VOLUME;
				if (this.tank.getLiquid().amount < vol)
				{
					vol = this.tank.getLiquid().amount;
				}
				/* FILL THE ITANKCONTAINER BELLOW THEN DRAIN THE INTERAL TANK IN THIS */
				int fillAmmount = ((ITankContainer) tileEntity).fill(fillDirection, LiquidHandler.getStack(this.tank.getLiquid(), vol), true);
				this.tank.drain(fillAmmount, true);
			}
		}
	}

	@Override
	public void setColor(Object obj)
	{
		ColorCode code = ColorCode.get(obj);
		if (!worldObj.isRemote && code != this.getColor() && (this.tank != null || code.isValidLiquid(this.tank.getLiquid())))
		{
			this.worldObj.setBlockMetadataWithNotify(xCoord, yCoord, zCoord, code.ordinal() & 15, 3);
			this.updateAdjacentConnections();
		}
	}

	@Override
	public ColorCode getColor()
	{
		return ColorCode.get(worldObj.getBlockMetadata(xCoord, yCoord, zCoord));
	}

	@Override
	public boolean canConnect(ForgeDirection direction)
	{
		TileEntity entity = worldObj.getBlockTileEntity(xCoord, yCoord, zCoord);

		return entity != null && entity.getClass() == this.getClass() && ((IColorCoded) entity).getColor() == this.getColor();
	}

	@Override
	public boolean canConnect(TileEntity entity, ForgeDirection dir)
	{
		return entity != null && entity instanceof IColorCoded && (((IColorCoded) entity).getColor() == ColorCode.NONE || ((IColorCoded) entity).getColor() == this.getColor());
	}

	@Override
	public TileEntity[] getAdjacentConnections()
	{
		return this.connectedBlocks;
	}

	@Override
	public void updateAdjacentConnections()
	{
		TileEntity[] originalConnection = this.connectedBlocks;
		this.connectedBlocks = new TileEntity[6];
		for (int side = 0; side < 6; side++)
		{
			ForgeDirection direction = ForgeDirection.getOrientation(side);
			TileEntity entity = worldObj.getBlockTileEntity(xCoord + direction.offsetX, yCoord + direction.offsetY, zCoord + direction.offsetZ);
			if (entity != null && !(entity instanceof IConductor))
			{
				if (!(entity instanceof IColorCoded) || (entity instanceof IColorCoded && (((IColorCoded) entity).getColor() == ColorCode.NONE || ((IColorCoded) entity).getColor() == this.getColor())))
				{
					if (entity instanceof IConnectionProvider && ((IConnectionProvider) entity).canConnect(direction))
					{
						connectedBlocks[side] = entity;
					}
					else if (entity instanceof ITankContainer)
					{
						connectedBlocks[side] = entity;
					}
				}
			}

		}

		if (!originalConnection.equals(this.connectedBlocks))
		{
			this.worldObj.markBlockForUpdate(this.xCoord, this.yCoord, this.zCoord);
		}

	}
}
