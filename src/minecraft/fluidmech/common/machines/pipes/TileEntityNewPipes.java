package fluidmech.common.machines.pipes;

import com.google.common.io.ByteArrayDataInput;

import hydraulic.core.prefab.TileEntityFluidHandler;
import fluidmech.common.FluidMech;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraftforge.liquids.LiquidStack;
import universalelectricity.prefab.tile.TileEntityConductor;
import basiccomponents.common.BCLoader;

public class TileEntityNewPipes extends TileEntityFluidHandler
{
	public static double RESISTANCE = 0.05;
	public static double MAX_AMPS = 200;

	public TileEntityNewPipes()
	{
		this.channel = FluidMech.CHANNEL;
	}

	@Override
	public double getResistance(LiquidStack stack)
	{
		return this.RESISTANCE;
	}

	@Override
	public double getMaxPressure()
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
		// TODO Auto-generated method stub

	}
}
