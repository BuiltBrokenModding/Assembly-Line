package fluidmech.common.machines;

import java.util.Random;

import fluidmech.common.FluidMech;
import hydraulic.api.ColorCode;
import hydraulic.api.IColorCoded;
import hydraulic.core.liquidNetwork.LiquidHandler;
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
import universalelectricity.prefab.tile.TileEntityAdvanced;

import com.google.common.io.ByteArrayDataInput;

public class TileEntitySink extends TileEntityAdvanced implements IPacketReceiver, ITankContainer, IColorCoded
{
	public TileEntity[] cc = { null, null, null, null, null, null };

	public static final int LMax = 2;
	private int count = 100;
	private Random random = new Random();
	private LiquidTank tank = new LiquidTank(LiquidContainerRegistry.BUCKET_VOLUME * LMax);

	@Override
	public void updateEntity()
	{
		if (!worldObj.isRemote)
		{
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
		return (resource == null || (!this.getColor().getLiquidData().getStack().isLiquidEqual(resource))) ? 0 : this.fill(0, resource, doFill);
	}

	@Override
	public int fill(int tankIndex, LiquidStack resource, boolean doFill)
	{
		if (resource == null || tankIndex != 0)
		{
			return 0;
		}
		if (doFill)
		{
			this.worldObj.markBlockForUpdate(this.xCoord, this.yCoord, this.zCoord);
		}
		return this.tank.fill(resource, doFill);
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
			this.worldObj.markBlockForUpdate(this.xCoord, this.yCoord, this.zCoord);
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
		return tank;
	}

	@Override
	public void setColor(Object obj)
	{
	}

	@Override
	public ColorCode getColor()
	{
		return ColorCode.BLUE;
	}
}
