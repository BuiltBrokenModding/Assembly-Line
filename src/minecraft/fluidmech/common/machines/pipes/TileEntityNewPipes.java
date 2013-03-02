package fluidmech.common.machines.pipes;

import fluidmech.common.FluidMech;
import hydraulic.core.implement.ColorCode;
import hydraulic.prefab.TileEntityFluidConveyor;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.liquids.LiquidContainerRegistry;
import net.minecraftforge.liquids.LiquidStack;

import com.google.common.io.ByteArrayDataInput;

public class TileEntityNewPipes extends TileEntityFluidConveyor
{
	public static int MAX_FLOW = LiquidContainerRegistry.BUCKET_VOLUME;
	public static double MAX_AMPS = 200;

	public ColorCode color = ColorCode.NONE;

	public TileEntityNewPipes()
	{
		this.channel = FluidMech.CHANNEL;
	}

	@Override
	public int getMaxFlowRate(LiquidStack stack)
	{
		return this.MAX_FLOW;
	}

	@Override
	public double getMaxPressure(ForgeDirection side)
	{
		return this.MAX_AMPS;
	}

	@Override
	public void onOverPressure()
	{
		if (!this.worldObj.isRemote)
		{
			this.worldObj.setBlockWithNotify(this.xCoord, this.yCoord, this.zCoord, Block.fire.blockID);
			// TODO make place a broken pipe, damage stuff, go boom, or something instead of just
			// destroying it
		}

	}

	@Override
	public void handlePacketData(INetworkManager network, int packetType, Packet250CustomPayload packet, EntityPlayer player, ByteArrayDataInput dataStream)
	{
	}

	@Override
	public ColorCode getColor()
	{
		return color;
	}

	@Override
	public void setColor(Object obj)
	{
		this.color = ColorCode.get(obj);
	}
}
