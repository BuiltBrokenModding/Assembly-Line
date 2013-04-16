package fluidmech.common.tiles;

import fluidmech.common.FluidMech;
import hydraulic.api.ColorCode;
import hydraulic.api.FluidRestrictionHandler;
import hydraulic.api.IColorCoded;
import hydraulic.helpers.FluidHelper;
import hydraulic.prefab.tile.TileEntityFluidStorage;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.liquids.ITankContainer;
import net.minecraftforge.liquids.LiquidContainerRegistry;
import net.minecraftforge.liquids.LiquidStack;
import universalelectricity.core.block.IConductor;
import universalelectricity.core.block.IConnectionProvider;
import universalelectricity.prefab.network.IPacketReceiver;
import universalelectricity.prefab.network.PacketManager;

import com.google.common.io.ByteArrayDataInput;

public class TileEntityTank extends TileEntityFluidStorage implements IPacketReceiver, ITankContainer, IColorCoded, IConnectionProvider
{
	public TileEntity[] connectedBlocks = { null, null, null, null, null, null };

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
					this.tank.drain(this.fillSide(this.getStoredLiquid(), ForgeDirection.DOWN, true), true);
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
		if (this.getStoredLiquid() != null)
		{
			return PacketManager.getPacket(FluidMech.CHANNEL, this, this.getStoredLiquid().itemID, this.getStoredLiquid().amount, this.getStoredLiquid().itemMeta);
		}
		else
		{
			return PacketManager.getPacket(FluidMech.CHANNEL, this, 0, 0, 0);
		}
	}

	public LiquidStack getStoredLiquid()
	{
		return tank.getLiquid();
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
	public int fill(int tankIndex, LiquidStack resource, boolean doFill)
	{
		if (!this.getColor().isValidLiquid(resource))
		{
			return 0;
		}
		if (this.isFull())
		{
			TileEntity tank = worldObj.getBlockTileEntity(xCoord, yCoord + 1, zCoord);
			if (tank instanceof TileEntityTank)
			{
				return ((TileEntityTank) tank).fill(tankIndex, resource, doFill);
			}
		}
		return super.fill(tankIndex, resource, doFill);
	}

	@Override
	public LiquidStack drain(ForgeDirection from, int maxDrain, boolean doDrain)
	{
		if (from != ForgeDirection.DOWN)
		{
			return super.drain(from, maxDrain, doDrain);
		}
		return null;
	}

	/** Cause this TE to trade liquid with the Tanks around it to level off */
	public void fillTanksAround()
	{
		if (this.tank.getLiquid() == null || this.tank.getLiquid().amount <= 0)
		{
			return;
		}

		TileEntity[] ents = this.getAdjacentConnections();

		/* SUM VOLUME UP FOR ALL CONNECTED TANKS */
		int commonVol = this.tank.getLiquid().amount;
		int equalVol = commonVol;
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
		equalVol = commonVol / tanks;

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
					filling = FluidHelper.getStack(this.tank.getLiquid(), equalVol);
				}
				else if (target.amount < equalVol)
				{
					filling = FluidHelper.getStack(this.tank.getLiquid(), equalVol - target.amount);
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
	public boolean canPipeConnect(TileEntity entity, ForgeDirection dir)
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

	@Override
	public int getTankSize()
	{
		return (LiquidContainerRegistry.BUCKET_VOLUME * 4);
	}
}
