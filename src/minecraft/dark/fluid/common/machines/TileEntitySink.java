package dark.fluid.common.machines;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraftforge.liquids.ITankContainer;
import net.minecraftforge.liquids.LiquidContainerRegistry;
import net.minecraftforge.liquids.LiquidStack;
import universalelectricity.prefab.network.IPacketReceiver;
import universalelectricity.prefab.network.PacketManager;

import com.google.common.io.ByteArrayDataInput;

import dark.core.api.ColorCode;
import dark.core.api.IColorCoded;
import dark.fluid.common.FluidMech;
import dark.fluid.common.prefab.TileEntityFluidStorage;

public class TileEntitySink extends TileEntityFluidStorage implements IPacketReceiver, ITankContainer, IColorCoded
{
	@Override
	public int getTankSize()
	{
		return LiquidContainerRegistry.BUCKET_VOLUME * 2;
	}

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
		LiquidStack stack = this.tank.getLiquid();
		if (stack != null)
		{
			return PacketManager.getPacket(FluidMech.CHANNEL, this, stack.itemID, stack.amount, stack.itemMeta);
		}
		else
		{
			return PacketManager.getPacket(FluidMech.CHANNEL, this, 0, 0, 0);
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
	public int fill(int tankIndex, LiquidStack resource, boolean doFill)
	{
		if (doFill)
		{
			this.worldObj.markBlockForUpdate(this.xCoord, this.yCoord, this.zCoord);
		}
		return super.fill(tankIndex, resource, doFill);
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
