package fluidmech.common.machines;

import fluidmech.common.FluidMech;
import fluidmech.common.machines.pipes.TileEntityPipe;
import hydraulic.api.ColorCode;
import hydraulic.api.IColorCoded;
import hydraulic.api.IPsiCreator;
import hydraulic.api.IReadOut;
import hydraulic.core.liquidNetwork.LiquidData;
import hydraulic.core.liquidNetwork.LiquidHandler;
import hydraulic.helpers.connectionHelper;
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
import universalelectricity.core.vector.Vector3;
import universalelectricity.prefab.network.IPacketReceiver;
import universalelectricity.prefab.network.PacketManager;

import com.google.common.io.ByteArrayDataInput;

public class TileEntityTank extends TileEntity implements IPacketReceiver, IReadOut, IPsiCreator, ITankContainer, IColorCoded
{
	public TileEntity[] cc = { null, null, null, null, null, null };

	private ColorCode color = ColorCode.NONE;

	public static final int LMax = 4;
	private int count, count2 = 0;

	public int pVolume = 0;

	private LiquidTank tank = new LiquidTank(LiquidContainerRegistry.BUCKET_VOLUME * LMax);

	public void updateEntity()
	{

		this.color = ColorCode.get(worldObj.getBlockMetadata(xCoord, yCoord, zCoord));
		if (++count >= 40)
		{
			count = 0;
			this.cc = connectionHelper.getSurroundingTileEntities(worldObj, xCoord, yCoord, zCoord);
			if (!worldObj.isRemote)
			{
				this.tradeDown();
				this.tradeArround();
				this.fillPipe();

				LiquidStack stack = new LiquidStack(0, 0, 0);
				if (this.tank.getLiquid() != null)
				{
					stack = this.tank.getLiquid();
				}
				Packet packet = PacketManager.getPacket(FluidMech.CHANNEL, this, new Object[] { stack.itemID, stack.amount, stack.itemMeta });
				PacketManager.sendPacketToClients(packet, worldObj, new Vector3(this), 20);

			}
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
		return (tank.getLiquid().amount / LiquidContainerRegistry.BUCKET_VOLUME) + "/" + (tank.getCapacity() / LiquidContainerRegistry.BUCKET_VOLUME) + " " + LiquidHandler.get(tank.getLiquid()).getName();
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt)
	{
		super.readFromNBT(nbt);

		LiquidStack liquid = new LiquidStack(0, 0, 0);
		liquid.readFromNBT(nbt.getCompoundTag("stored"));
		tank.setLiquid(liquid);
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
		if (resource == null || (!color.getLiquidData().getStack().isLiquidEqual(resource) && this.color != ColorCode.NONE))
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
	 * find out if this tank is actual full or not
	 * 
	 * @return
	 */
	public boolean isFull()
	{
		if (this.tank.getLiquid() == null)
		{
			return false;
		}
		if (this.tank.getLiquid().amount > 0 && this.tank.getLiquid().amount < this.tank.getCapacity())
		{
			return false;
		}
		return true;
	}

	@Override
	public LiquidStack drain(ForgeDirection from, int maxDrain, boolean doDrain)
	{
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
	public ILiquidTank getTank(ForgeDirection direction, LiquidStack type)
	{
		return null;
	}

	@Override
	public int getPressureOut(LiquidStack type, ForgeDirection dir)
	{
		if (color.isValidLiquid(type) || type.isLiquidEqual(LiquidHandler.unkown.getStack()))
		{
			LiquidData data = LiquidHandler.get(type);
			if (data.getCanFloat() && dir == ForgeDirection.DOWN)
				return data.getPressure();
			if (!data.getCanFloat() && dir == ForgeDirection.UP)
				return data.getPressure();
		}
		return 0;
	}

	@Override
	public boolean canConnect(ForgeDirection dir, LiquidStack... stacks)
	{
		for (int i = 0; i < stacks.length; i++)
		{
			LiquidData data = LiquidHandler.get(stacks[i]);
			if (color.isValidLiquid(stacks[i]) && ((data.getCanFloat() && dir == ForgeDirection.DOWN) || (!data.getCanFloat() && dir == ForgeDirection.UP)))
			{
				return true;
			}
		}
		return false;
	}

	/**
	 * cause this TE to trade liquid down if the liquid is in liquid state or up if in gas state.
	 */
	public void tradeDown()
	{
		if (this.tank.getLiquid() == null || this.tank.getLiquid().amount <= 0)
			return;
		TileEntity ent = worldObj.getBlockTileEntity(xCoord, yCoord - 1, zCoord);
		if (ent instanceof TileEntityTank && ((TileEntityTank) ent).getColor() == this.color && !((TileEntityTank) ent).isFull())
		{
			int f = ((TileEntityTank) ent).tank.fill(this.tank.getLiquid(), true);
			this.tank.drain(f, true);
		}
	}

	/** Cause this TE to trade liquid with the Tanks around it to level off */
	public void tradeArround()
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
			if (ents[i] instanceof TileEntityTank && ((TileEntityTank) ents[i]).color == this.color)
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

			if (ents[i] instanceof TileEntityTank && ((TileEntityTank) ents[i]).color == this.color && !((TileEntityTank) ents[i]).isFull())
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

	/** Causes this to fill a pipe either above or bellow based on liquid data */
	public void fillPipe()
	{
		if (this.tank.getLiquid() == null || this.tank.getLiquid().amount <= 0)
		{
			return;
		}
		LiquidData data = LiquidHandler.get(this.tank.getLiquid());
		if (data != null)
		{

			int change = -1;
			if (data.getCanFloat())
			{
				change = 1;
			}
			TileEntity ent = worldObj.getBlockTileEntity(xCoord, yCoord + change, zCoord);
			if (ent instanceof TileEntityPipe)
			{
				ColorCode c = ((TileEntityPipe) ent).getColor();
				if (c == ColorCode.NONE || c == this.color)
				{
					int vol = LiquidContainerRegistry.BUCKET_VOLUME;
					if (this.tank.getLiquid().amount < vol)
					{
						vol = this.tank.getLiquid().amount;
					}
					int f = ((TileEntityPipe) ent).fill(0, LiquidHandler.getStack(this.tank.getLiquid(), vol), true);
					this.tank.drain(f, true);
				}
			}
		}
	}

	@Override
	public void setColor(Object obj)
	{
		this.color = ColorCode.get(cc);

	}

	@Override
	public ColorCode getColor()
	{
		return color;
	}
}
