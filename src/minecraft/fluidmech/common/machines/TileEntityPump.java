package fluidmech.common.machines;

import hydraulic.core.helpers.MetaGroup;
import hydraulic.core.implement.ColorCode;
import hydraulic.core.implement.IPsiCreator;
import hydraulic.core.implement.IReadOut;
import hydraulic.core.liquids.LiquidData;
import hydraulic.core.liquids.LiquidHandler;

import java.util.EnumSet;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.liquids.ITankContainer;
import net.minecraftforge.liquids.LiquidContainerRegistry;
import net.minecraftforge.liquids.LiquidStack;
import universalelectricity.core.electricity.ElectricityConnections;
import universalelectricity.core.electricity.ElectricityNetwork;
import universalelectricity.core.electricity.ElectricityPack;
import universalelectricity.core.vector.Vector3;
import universalelectricity.prefab.network.IPacketReceiver;
import universalelectricity.prefab.network.PacketManager;
import universalelectricity.prefab.tile.TileEntityElectricityReceiver;
import universalelectricity.prefab.tile.TileEntityElectricityRunnable;

import com.google.common.io.ByteArrayDataInput;

import fluidmech.common.FluidMech;

public class TileEntityPump extends TileEntityElectricityRunnable implements IPacketReceiver, IReadOut, IPsiCreator
{
	public final double WATTS_PER_TICK = (400 / 20);
	double percentPumped = 0.0;
	double joulesReceived = 0;

	int disableTimer = 0;
	int count = 0;
	public int pos = 0;

	private boolean converted = false;
	public ColorCode color = ColorCode.BLUE;

	ForgeDirection back = ForgeDirection.EAST;
	ForgeDirection side = ForgeDirection.EAST;

	@Override
	public void initiate()
	{
		this.getConnections();
		ElectricityConnections.registerConnector(this, EnumSet.of(back, side));
		this.worldObj.notifyBlocksOfNeighborChange(this.xCoord, this.yCoord, this.zCoord, FluidMech.blockMachine.blockID);
	}

	/**
	 * gets the side connection for the wire and pipe
	 */
	public void getConnections()
	{
		int notchMeta = MetaGroup.getFacingMeta(worldObj.getBlockMetadata(xCoord, yCoord, zCoord));

		back = ForgeDirection.getOrientation(notchMeta);
		side = Vector3.getOrientationFromSide(back, ForgeDirection.WEST);

		if (notchMeta == 2 || notchMeta == 3)
		{
			side = side.getOpposite();
		}
	}

	@Override
	public void updateEntity()
	{
		super.updateEntity();

		if (!this.worldObj.isRemote && !this.isDisabled())
		{
			if (this.canPump(xCoord, yCoord - 1, zCoord) && this.joulesReceived >= this.WATTS_PER_TICK)
			{
				joulesReceived -= this.WATTS_PER_TICK;
				this.pos++;
				if (pos >= 8)
				{
					pos = 0;
				}
				if (percentPumped++ >= 10)
				{
					percentPumped = 0;
					this.drainBlock(new Vector3(xCoord, yCoord - 1, zCoord));
				}
			}
			if (this.ticks % 10 == 0)
			{
				// TODO fix this to tell the client its running
				Packet packet = PacketManager.getPacket(FluidMech.CHANNEL, this, color.ordinal(), this.joulesReceived);
				PacketManager.sendPacketToClients(packet, worldObj, new Vector3(this), 60);
			}
		}

	}

	@Override
	public void handlePacketData(INetworkManager network, int packetType, Packet250CustomPayload packet, EntityPlayer player, ByteArrayDataInput data)
	{
		try
		{
			this.color = ColorCode.get(data.readInt());
			this.joulesReceived = data.readDouble();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

	}

	public ITankContainer getFillTarget()
	{
		TileEntity ent = worldObj.getBlockTileEntity(xCoord + side.offsetX, yCoord + side.offsetY, zCoord + side.offsetZ);

		if (ent instanceof ITankContainer)
		{
			return (ITankContainer) ent;
		}
		return null;
	}

	/**
	 * gets the search range the pump used to find valid block to pump
	 */
	public int getPumpRange()
	{
		int meta = worldObj.getBlockMetadata(xCoord, yCoord, zCoord);
		switch (MetaGroup.getGrouping(meta))
		{
			case 0:
			case 1:
				return 1;
			case 2:
				return 20;
			case 3:
				return 50;
		}
		return 1;
	}

	@Override
	public ElectricityPack getRequest()
	{
		double amps = (this.WATTS_PER_TICK / this.getVoltage());
		return new ElectricityPack(amps, this.getVoltage());
	}

	boolean canPump(int x, int y, int z)
	{
		int blockID = worldObj.getBlockId(x, y, z);
		int meta = worldObj.getBlockMetadata(x, y, z);

		LiquidData resource = LiquidHandler.getFromBlockID(blockID);

		ITankContainer fillTarget = getFillTarget();

		if (fillTarget == null || fillTarget.fill(side, this.color.getLiquidData().getStack(), false) == 0)
		{
			return false;
		}
		else if ((LiquidHandler.getFromBlockID(worldObj.getBlockId(x, y, z)) == null || LiquidHandler.getFromBlockID(worldObj.getBlockId(x, y, z)) == LiquidHandler.unkown))
		{
			return false;
		}
		else if (blockID == Block.waterMoving.blockID || blockID == Block.lavaMoving.blockID)
		{
			return false;
		}
		else if (blockID == Block.waterStill.blockID || blockID == Block.waterStill.blockID)
		{

		}
		return true;
	}

	/**
	 * drains the block or in other words removes it
	 * 
	 * @param loc
	 * @return true if the block was drained
	 */
	boolean drainBlock(Vector3 loc)
	{
		int blockID = worldObj.getBlockId(loc.intX(), loc.intY(), loc.intZ());
		int meta = worldObj.getBlockMetadata(loc.intX(), loc.intY(), loc.intZ());
		
		LiquidData resource = LiquidHandler.getFromBlockID(blockID);

		if (color.isValidLiquid(resource) && meta == 0 && getFillTarget().fill(back, resource.getStack(), false) != 0)
		{

			LiquidStack stack = resource.getStack();
			stack.amount = LiquidContainerRegistry.BUCKET_VOLUME;
			int f = getFillTarget().fill(back, this.color.getLiquidData().getStack(), true);
			if (f > 0)
			{
				worldObj.setBlockWithNotify(xCoord, yCoord - 1, zCoord, 0);
				return true;
			}
		}

		return false;
	}

	@Override
	public String getMeterReading(EntityPlayer user, ForgeDirection side)
	{
		return this.joulesReceived + "/" + this.WATTS_PER_TICK + " " + this.percentPumped;
	}

	@Override
	public int getPressureOut(LiquidData type, ForgeDirection dir)
	{
		if (type != null && this.color.isValidLiquid(type.getStack()))
		{
			return type.getPressure();
		}
		return 0;
	}

	@Override
	public boolean getCanPressureTo(LiquidData type, ForgeDirection dir)
	{
		return dir == this.side.getOpposite() && this.color.isValidLiquid(type.getStack());
	}

}
