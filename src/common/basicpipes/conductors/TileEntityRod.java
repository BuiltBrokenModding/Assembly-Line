package basicpipes.conductors;

import com.google.common.io.ByteArrayDataInput;

import net.minecraft.src.EntityPlayer;
import net.minecraft.src.NetworkManager;
import net.minecraft.src.Packet;
import net.minecraft.src.Packet250CustomPayload;
import net.minecraft.src.TileEntity;
import net.minecraft.src.World;
import net.minecraftforge.common.ForgeDirection;
import universalelectricity.network.IPacketReceiver;
import universalelectricity.network.PacketManager;
import universalelectricity.prefab.Vector3;
import basicpipes.BasicPipesMain;
import basicpipes.pipes.api.ILiquidConsumer;
import basicpipes.pipes.api.IMechenical;
import basicpipes.pipes.api.Liquid;

public class TileEntityRod extends TileEntity implements IPacketReceiver,IMechenical {

	public int pos = 0;

	@Override
	public double getForce(ForgeDirection side) {
		return 0;
	}

	@Override
	public boolean canOutputSide(ForgeDirection side) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean canInputSide(ForgeDirection side) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public double applyForce(int force) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void handlePacketData(NetworkManager network,
			Packet250CustomPayload packet, EntityPlayer player,
			ByteArrayDataInput dataStream) {
		// TODO Auto-generated method stub
		
	}
}
