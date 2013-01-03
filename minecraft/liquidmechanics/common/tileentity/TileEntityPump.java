package liquidmechanics.common.tileentity;

import java.util.EnumSet;

import liquidmechanics.api.IReadOut;
import liquidmechanics.api.ITankOutputer;
import liquidmechanics.common.LiquidMechanics;
import liquidmechanics.common.MetaGroupingHelper;
import liquidmechanics.common.handlers.DefautlLiquids;

import net.minecraft.block.Block;
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
import universalelectricity.core.electricity.ElectricityConnections;
import universalelectricity.core.electricity.ElectricityNetwork;
import universalelectricity.core.vector.Vector3;
import universalelectricity.prefab.network.IPacketReceiver;
import universalelectricity.prefab.network.PacketManager;
import universalelectricity.prefab.tile.TileEntityElectricityReceiver;

import com.google.common.io.ByteArrayDataInput;

public class TileEntityPump extends TileEntityElectricityReceiver implements IPacketReceiver, IReadOut, ITankOutputer
{
	public final double WATTS_PER_TICK = 400;
	double percentPumped = 0.0;
	double joulesReceived = 0;
	int wMax = LiquidContainerRegistry.BUCKET_VOLUME * 2;
	int disableTimer = 0;
	int count = 0;

	public DefautlLiquids type = DefautlLiquids.DEFUALT;
	public LiquidTank tank = new LiquidTank(wMax);

	@Override
	public void initiate()
	{
		this.registerConnections();
		this.worldObj.notifyBlocksOfNeighborChange(this.xCoord, this.yCoord, this.zCoord, LiquidMechanics.blockMachine.blockID);
	}

	public void registerConnections()
	{
		int notchMeta = MetaGroupingHelper.getFacingMeta(worldObj.getBlockMetadata(xCoord, yCoord, zCoord));
		ForgeDirection facing = ForgeDirection.getOrientation(notchMeta).getOpposite();
		ForgeDirection[] dirs = new ForgeDirection[] { ForgeDirection.UNKNOWN, ForgeDirection.UNKNOWN, ForgeDirection.UNKNOWN, ForgeDirection.UNKNOWN, ForgeDirection.UNKNOWN, ForgeDirection.UNKNOWN };
		ElectricityConnections.registerConnector(this, EnumSet.of(facing.getOpposite()));
		for (int i = 2; i < 6; i++)
		{
			ForgeDirection dir = ForgeDirection.getOrientation(i);
			if (dir != facing)
			{
				dirs[i] = dir;
			}
		}
		ElectricityConnections.registerConnector(this, EnumSet.of(dirs[0], dirs[1], dirs[2], dirs[3], dirs[4], dirs[5]));
	}

	@Override
	public void onDisable(int duration)
	{
		disableTimer = duration;
	}

	@Override
	public boolean isDisabled()
	{
		if (disableTimer <= 0) { return false; }
		return true;
	}

	@Override
	public void updateEntity()
	{
		super.updateEntity();

		if (!this.worldObj.isRemote)
		{
			if (count-- <= 0)
			{
				int bBlock = worldObj.getBlockId(xCoord, yCoord - 1, zCoord);
				DefautlLiquids bellow = DefautlLiquids.getLiquidTypeByBlock(bBlock);
				if (bellow != null)
				{
					if (this.type != bellow && bellow != DefautlLiquids.DEFUALT)
					{
						this.tank.setLiquid(DefautlLiquids.getStack(bellow, 0));
						this.type = bellow;
					}

				}
				count = 40;
			}
			if (this.tank.getLiquid() == null)
			{
				this.tank.setLiquid(DefautlLiquids.getStack(this.type, 1));
			}
			LiquidStack stack = tank.getLiquid();

			if (stack != null)
			{
				for (int i = 0; i < 6; i++)
				{
					ForgeDirection dir = ForgeDirection.getOrientation(i);
					TileEntity tile = worldObj.getBlockTileEntity(xCoord + dir.offsetX, yCoord + dir.offsetY, zCoord + dir.offsetZ);

					if (tile instanceof ITankContainer)
					{
						int moved = ((ITankContainer) tile).fill(dir.getOpposite(), stack, true);
						tank.drain(moved, true);
						if (stack.amount <= 0)
							break;
					}
				}

			}

			int notchMeta = MetaGroupingHelper.getFacingMeta(worldObj.getBlockMetadata(xCoord, yCoord, zCoord));
			ForgeDirection facing = ForgeDirection.getOrientation(notchMeta).getOpposite();

			for (int i = 2; i < 6; i++)
			{
				ForgeDirection dir = ForgeDirection.getOrientation(i);
				if (dir != facing)
				{
					TileEntity inputTile = Vector3.getTileEntityFromSide(this.worldObj, new Vector3(this), dir);
					ElectricityNetwork network = ElectricityNetwork.getNetworkFromTileEntity(inputTile, dir);
					if (network != null)
					{

						if (this.canPump(xCoord, yCoord - 1, zCoord))
						{
							network.startRequesting(this, WATTS_PER_TICK / this.getVoltage(), this.getVoltage());
							this.joulesReceived = Math.max(Math.min(this.joulesReceived + network.consumeElectricity(this).getWatts(), WATTS_PER_TICK), 0);
						}
						else
						{
							network.stopRequesting(this);
						}
					}
				}
			}
			if (this.joulesReceived >= this.WATTS_PER_TICK - 50 && this.canPump(xCoord, yCoord - 1, zCoord))
			{

				joulesReceived -= this.WATTS_PER_TICK;
				if (percentPumped++ >= 20)
				{
					this.drainBlock(new Vector3(xCoord, yCoord - 1, zCoord));
				}
			}
		}

		if (!this.worldObj.isRemote)
		{
			if (this.ticks % 10 == 0)
			{
				Packet packet = PacketManager.getPacket(LiquidMechanics.CHANNEL, this, this.type.ordinal());
				PacketManager.sendPacketToClients(packet, worldObj, new Vector3(this), 60);
			}
		}
	}

	public boolean canPump(int x, int y, int z)
	{
		// if (this.tank.getLiquid() == null) return false;
		if (this.tank.getLiquid() != null && this.tank.getLiquid().amount >= this.wMax)
			return false;
		if (this.isDisabled())
			return false;
		if (!this.isValidLiquid(Block.blocksList[worldObj.getBlockId(x, y, z)]))
			return false;
		return true;
	}

	/**
	 * drains the block or in other words removes it
	 * 
	 * @param loc
	 * @return true if the block was drained
	 */
	public boolean drainBlock(Vector3 loc)
	{
		int bBlock = worldObj.getBlockId(loc.intX(), loc.intY(), loc.intZ());
		int meta = worldObj.getBlockMetadata(loc.intX(), loc.intY(), loc.intZ());
		DefautlLiquids bellow = DefautlLiquids.getLiquidTypeByBlock(bBlock);
		if (bBlock == Block.waterMoving.blockID || (bBlock == Block.waterStill.blockID && meta != 0))
			return false;
		if (bBlock == Block.lavaMoving.blockID || (bBlock == Block.lavaStill.blockID && meta != 0))
			return false;
		if (bBlock == type.liquid.itemID && this.isValidLiquid(Block.blocksList[bBlock]))
		{
			// FMLLog.info("pumping " + bellow.displayerName + " blockID:" + bBlock + " Meta:" +
			// meta);
			int f = this.tank.fill(DefautlLiquids.getStack(this.type, LiquidContainerRegistry.BUCKET_VOLUME), true);
			if (f > 0)
				worldObj.setBlockWithNotify(loc.intX(), loc.intY(), loc.intZ(), 0);
			percentPumped = 0;
			return true;
		}
		return false;
	}

	@Override
	public void handlePacketData(INetworkManager network, int packetType, Packet250CustomPayload packet, EntityPlayer player, ByteArrayDataInput data)
	{
		try
		{
			this.type = (DefautlLiquids.getLiquid(data.readInt()));
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

	}

	/**
	 * Reads a tile entity from NBT.
	 */
	@Override
	public void readFromNBT(NBTTagCompound par1NBTTagCompound)
	{
		super.readFromNBT(par1NBTTagCompound);
		int stored = par1NBTTagCompound.getInteger("liquid");
		this.type = DefautlLiquids.getLiquid(par1NBTTagCompound.getInteger("type"));
		this.tank.setLiquid(DefautlLiquids.getStack(this.type, stored));
	}

	/**
	 * Writes a tile entity to NBT.
	 */
	@Override
	public void writeToNBT(NBTTagCompound par1NBTTagCompound)
	{
		super.writeToNBT(par1NBTTagCompound);
		int s = 1;
		if (this.tank.getLiquid() != null)
			s = this.tank.getLiquid().amount;
		par1NBTTagCompound.setInteger("liquid", s);
		par1NBTTagCompound.setInteger("type", this.type.ordinal());
	}

	@Override
	public String getMeterReading(EntityPlayer user, ForgeDirection side)
	{
		int liquid = 0;
		if (this.tank.getLiquid() != null)
		{
			liquid = (this.tank.getLiquid().amount / LiquidContainerRegistry.BUCKET_VOLUME);
		}
		else
		{
			liquid = 0;
		}
		return liquid + "" + type.displayerName + " " + this.joulesReceived + "W " + this.percentPumped + "/20";
	}

	@Override
	public int fill(ForgeDirection from, LiquidStack resource, boolean doFill)
	{
		return 0;
	}

	@Override
	public int fill(int tankIndex, LiquidStack resource, boolean doFill)
	{
		return 0;
	}

	@Override
	public LiquidStack drain(ForgeDirection from, int maxDrain, boolean doDrain)
	{
		return drain(0, maxDrain, doDrain);
	}

	@Override
	public LiquidStack drain(int tankIndex, int maxDrain, boolean doDrain)
	{
		if (tankIndex == 0)
			return tank.drain(maxDrain, doDrain);

		return null;
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
	public int presureOutput(DefautlLiquids type, ForgeDirection dir)
	{
		if (type == this.type)
			return type.defaultPresure;
		return 0;
	}

	@Override
	public boolean canPressureToo(DefautlLiquids type, ForgeDirection dir)
	{
		if (type == this.type)
			return true;
		return false;
	}
	/**
	 * Checks to see if the given block type is valid for pumping
	 * @param block
	 * @return
	 */
	private boolean isValidLiquid(Block block)
	{
		return DefautlLiquids.getLiquidFromBlock(block.blockID) != null;
	}

}
