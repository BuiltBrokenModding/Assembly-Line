package fluidmech.common.machines;

import fluidmech.common.FluidMech;
import hydraulic.api.ColorCode;
import hydraulic.api.IColorCoded;
import hydraulic.api.IPipeConnection;
import hydraulic.api.IReadOut;
import hydraulic.helpers.FluidHelper;
import hydraulic.helpers.MetaGroup;
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

public class TileEntityMinorPump extends TileEntityElectricityRunnable implements IPacketReceiver, IReadOut, IPipeConnection
{
	public final double WATTS_PER_TICK = (400 / 20);
	private double percentPumped = 0.0;

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
	public void initiate()
	{
		this.getConnections();
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
				if (percentPumped < 10)
				{
					percentPumped++;
				}
				else if (percentPumped >= 10 && this.drainBlock(new Vector3(xCoord, yCoord - 1, zCoord)))
				{
					percentPumped = 0;
				}

				/* DO ANIMATION CHANGE */
				this.pos++;
				if (pos >= 8)
				{
					pos = 0;
				}
			}
			if (this.ticks % 10 == 0)
			{
				// Packet packet = PacketManager.getPacket(FluidMech.CHANNEL, this, color.ordinal(),
				// this.wattsReceived);
				// PacketManager.sendPacketToClients(packet, worldObj, new Vector3(this), 60);
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
	 * 
	 * @param x y z - location of the block, use the tileEntities world
	 * @return true if it can pump
	 */
	boolean canPump(int x, int y, int z)
	{
		return getFillTarget() != null && FluidHelper.getLiquidId(worldObj.getBlockId(x, y, z)) != -1;
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

		LiquidStack stack = FluidHelper.getLiquidFromBlockId(blockID);
		if (color.isValidLiquid(stack) && getFillTarget() != null)
		{
			stack.amount = LiquidContainerRegistry.BUCKET_VOLUME;
			int fillAmmount = getFillTarget().fill(pipeConnection.getOpposite(), stack, true);

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
		return String.format("%.2f/%.2f  %f Done", this.wattsReceived,this.WATTS_PER_TICK,this.percentPumped);
	}

	@Override
	public boolean canConnect(ForgeDirection direction)
	{
		return direction == wireConnection;
	}

	@Override
	public boolean canConnect(TileEntity entity, ForgeDirection dir)
	{
		if (dir == this.pipeConnection.getOpposite())
		{
			return entity != null && entity instanceof IColorCoded && (((IColorCoded) entity).getColor() == ColorCode.NONE || ((IColorCoded) entity).getColor() == this.color);
		}
		return false;
	}

}
