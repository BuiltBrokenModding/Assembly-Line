package assemblyline.machines.crafter;

import com.google.common.io.ByteArrayDataInput;

import assemblyline.TileEntityBase;
import universalelectricity.implement.IElectricityReceiver;
import universalelectricity.prefab.TileEntityElectricityReceiver;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.INetworkManager;
import net.minecraft.src.Packet250CustomPayload;
import net.minecraft.src.TileEntity;
import net.minecraftforge.common.ForgeDirection;

public class TileEntityCraftingArm extends TileEntityBase implements IElectricityReceiver
{
	public enum armTasks
	{
		NONE, COLLECT, MINE, PLACE, SORT, CRAFT
	}

	/**
	 * Entity robotic arm to be used with this
	 * tileEntity
	 */
	public EntityCraftingArm EntityArm = null;

	public double wattUsed = 20;

	public double jouleReceived = 0;

	public double maxJoules = 100;
	/**
	 * does this arm have a task to do
	 */
	public boolean hasTask = true;
	/**
	 * what kind of task this arm should do
	 */
	public armTasks task = armTasks.NONE;

	public void updateEntity()
	{
		super.updateEntity();
		if (this.ticks % 5 == 0 && !this.isDisabled() && this.hasTask && EntityArm != null)
		{
			this.jouleReceived -= this.wattUsed;
			this.doWork();
		}
	}

	/**
	 * controls the robotic arm into doing a set
	 * task
	 */
	public void doWork()
	{

	}

	/**
	 * UE methods
	 */
	@Override
	public void onReceive(TileEntity sender, double amps, double voltage, ForgeDirection side)
	{
		this.jouleReceived = Math.max(jouleReceived + (amps * voltage), maxJoules);

	}

	@Override
	public double wattRequest()
	{
		return maxJoules - jouleReceived;
	}

	@Override
	public boolean canReceiveFromSide(ForgeDirection side)
	{
		if (side != ForgeDirection.UP) { return true; }
		return false;
	}

	@Override
	public void onDisable(int duration)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isDisabled()
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean canConnect(ForgeDirection side)
	{
		if (side != ForgeDirection.UP) { return true; }
		return false;
	}

	@Override
	public double getVoltage()
	{
		// TODO Auto-generated method stub
		return 120;
	}

	/**
	 * Data
	 */
	@Override
	public void handlePacketData(INetworkManager network, int packetType, Packet250CustomPayload packet, EntityPlayer player, ByteArrayDataInput dataStream)
	{
		// TODO Auto-generated method stub

	}

	/**
	 * inventory
	 */
	@Override
	public int getSizeInventory()
	{
		// TODO Auto-generated method stub
		return 1;
	}

	@Override
	public String getInvName()
	{
		// TODO Auto-generated method stub
		return "RoboticArm";
	}

}
