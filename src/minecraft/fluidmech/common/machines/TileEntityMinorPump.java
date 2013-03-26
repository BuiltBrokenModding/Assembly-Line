package fluidmech.common.machines;

import fluidmech.common.FluidMech;
import hydraulic.api.ColorCode;
import hydraulic.api.IPsiCreator;
import hydraulic.api.IReadOut;
import hydraulic.core.liquidNetwork.LiquidData;
import hydraulic.core.liquidNetwork.LiquidHandler;
import hydraulic.helpers.MetaGroup;
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
import universalelectricity.core.electricity.ElectricityPack;
import universalelectricity.core.vector.Vector3;
import universalelectricity.core.vector.VectorHelper;
import universalelectricity.prefab.network.IPacketReceiver;
import universalelectricity.prefab.network.PacketManager;
import universalelectricity.prefab.tile.TileEntityElectricityRunnable;

import com.google.common.io.ByteArrayDataInput;

public class TileEntityMinorPump extends TileEntityElectricityRunnable implements IPacketReceiver, IReadOut, IPsiCreator
{
	public final double WATTS_PER_TICK = (400 / 20);
	double percentPumped = 0.0;

	int disableTimer = 0;
	public int pos = 0;

	public ColorCode color = ColorCode.BLUE;

	ForgeDirection wireConnection = ForgeDirection.EAST;
	ForgeDirection pipeConnection = ForgeDirection.EAST;

	/**
	 * gets the side connection for the wire and pipe
	 */
	public void getConnections()
	{
		int notchMeta = MetaGroup.getFacingMeta(worldObj.getBlockMetadata(xCoord, yCoord, zCoord));

		wireConnection = ForgeDirection.getOrientation(notchMeta);
		pipeConnection = VectorHelper.getOrientationFromSide(wireConnection, ForgeDirection.WEST);

		if (notchMeta == 2 || notchMeta == 3)
		{
			pipeConnection = pipeConnection.getOpposite();
		}
	}

	@Override
	public void updateEntity()
	{
		super.updateEntity();

		this.getConnections();

		if (!this.worldObj.isRemote && !this.isDisabled())
		{
			if (this.canPump(xCoord, yCoord - 1, zCoord) && this.wattsReceived >= this.WATTS_PER_TICK)
			{
				wattsReceived -= this.WATTS_PER_TICK;

				if (percentPumped++ >= 10)
				{
					percentPumped = 0;
					this.drainBlock(new Vector3(xCoord, yCoord - 1, zCoord));
				}
				// // Do animation to simulate life //
				this.pos++;
				if (pos >= 8)
				{
					pos = 0;
				}
			}
			if (this.ticks % 10 == 0)
			{
				// TODO fix this to tell the client its running
				Packet packet = PacketManager.getPacket(FluidMech.CHANNEL, this, color.ordinal(), this.wattsReceived);
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
			this.wattsReceived = data.readDouble();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

	}

	/**
	 * gets the fluidConductor or storageTank to ouput its pumped liquids too if there is not one it
	 * will not function
	 */
	public ITankContainer getFillTarget()
	{
		TileEntity ent = worldObj.getBlockTileEntity(xCoord + pipeConnection.offsetX, yCoord + pipeConnection.offsetY, zCoord + pipeConnection.offsetZ);

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
	/**
	 * checks to see if this pump can pump the selected target block
	 * @param x y z - location of the block, use the tileEntities world
	 * @return true if it can pump
	 */
	boolean canPump(int x, int y, int z)
	{
		int blockID = worldObj.getBlockId(x, y, z);
		int meta = worldObj.getBlockMetadata(x, y, z);

		LiquidData resource = LiquidHandler.getFromBlockID(blockID);

		ITankContainer fillTarget = getFillTarget();

		if (fillTarget == null || fillTarget.fill(pipeConnection, this.color.getLiquidData().getStack(), false) == 0)
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
	 * drains the block(removes) at the location given
	 * 
	 * @param loc - vector 3 location
	 * @return true if the block was drained
	 */
	boolean drainBlock(Vector3 loc)
	{
		int blockID = worldObj.getBlockId(loc.intX(), loc.intY(), loc.intZ());
		int meta = worldObj.getBlockMetadata(loc.intX(), loc.intY(), loc.intZ());

		LiquidData resource = LiquidHandler.getFromBlockID(blockID);

		if (color.isValidLiquid(resource.getStack()) && meta == 0 && getFillTarget().fill(pipeConnection, resource.getStack(), false) != 0)
		{

			LiquidStack stack = resource.getStack();
			stack.amount = LiquidContainerRegistry.BUCKET_VOLUME;
			int fillAmmount = getFillTarget().fill(pipeConnection, this.color.getLiquidData().getStack(), true);
			
			if (fillAmmount > 0)
			{
				worldObj.setBlockAndMetadataWithNotify(xCoord, yCoord - 1, zCoord, 0, 0, 3);
				return true;
			}
		}

		return false;
	}

	@Override
	public String getMeterReading(EntityPlayer user, ForgeDirection side)
	{
		return this.wattsReceived + "/" + this.WATTS_PER_TICK + "W " + this.percentPumped + "% DONE";
	}

	@Override
	public int getPressureOut(LiquidStack type, ForgeDirection dir)
	{
		if (type != null && this.color.isValidLiquid(type))
		{
			return LiquidHandler.get(type).getPressure();
		}
		return 0;
	}

	@Override
	public boolean canConnect(ForgeDirection direction)
	{
		return direction == wireConnection;
	}

	@Override
	public boolean canConnect(ForgeDirection dir, LiquidStack... stacks)
	{
		if (dir == this.pipeConnection.getOpposite())
		{
			if (stacks == null || stacks.length == 0)
			{
				return true;
			}
			for (int i = 0; i < stacks.length; i++)
			{
				if (this.color.isValidLiquid(stacks[i]))
				{
					return true;
				}
			}
		}
		return false;
	}

}
