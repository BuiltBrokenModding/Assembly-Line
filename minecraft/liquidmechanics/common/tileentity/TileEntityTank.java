package liquidmechanics.common.tileentity;

import liquidmechanics.api.IReadOut;
import liquidmechanics.api.ITankOutputer;
import liquidmechanics.api.helpers.Liquid;
import liquidmechanics.api.helpers.MHelper;
import liquidmechanics.common.LiquidMechanics;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.liquids.ILiquidTank;
import net.minecraftforge.liquids.LiquidContainerRegistry;
import net.minecraftforge.liquids.LiquidStack;
import net.minecraftforge.liquids.LiquidTank;
import universalelectricity.core.vector.Vector3;
import universalelectricity.prefab.network.IPacketReceiver;
import universalelectricity.prefab.network.PacketManager;

import com.google.common.io.ByteArrayDataInput;


public class TileEntityTank extends TileEntity implements IPacketReceiver, IReadOut, ITankOutputer
{
	public TileEntity[] cc = { null, null, null, null, null, null };
	public Liquid type = Liquid.DEFUALT;
	public static final int LMax = 4;
	private int count = 0;
	private int count2 = 0;

	private boolean doUpdate = true;
	public LiquidTank tank = new LiquidTank(LiquidContainerRegistry.BUCKET_VOLUME * LMax);

	public void updateEntity()
	{
		if (tank.getLiquid() == null)
		{
			tank.setLiquid(Liquid.getStack(this.type, 1));
		}
		LiquidStack liquid = tank.getLiquid();

		if (++count >= 20 && liquid != null)
		{
			count = 0;
			this.cc = MHelper.getSourounding(worldObj, xCoord, yCoord, zCoord);
			if (!worldObj.isRemote)
			{
				this.tradeDown();
				this.tradeArround();

				Packet packet = PacketManager.getPacket(LiquidMechanics.CHANNEL, this, new Object[] { type.ordinal(), liquid.amount });
				PacketManager.sendPacketToClients(packet, worldObj, new Vector3(this), 20);

			}
		}
	}

	@Override
	public String getMeterReading(EntityPlayer user, ForgeDirection side)
	{
		String output = "";
		LiquidStack stack = tank.getLiquid();
		if (stack != null)
			output += (stack.amount / LiquidContainerRegistry.BUCKET_VOLUME) + " " + this.type.displayerName;
		if (stack != null)
			return output;

		return "0/4 " + this.type.displayerName;
	}

	@Override
	public void readFromNBT(NBTTagCompound par1NBTTagCompound)
	{
		super.readFromNBT(par1NBTTagCompound);
		this.type = Liquid.getLiquid(par1NBTTagCompound.getInteger("type"));
		int vol = par1NBTTagCompound.getInteger("liquid");
		this.tank.setLiquid(Liquid.getStack(type, vol));
	}
	@Override
	public void writeToNBT(NBTTagCompound par1NBTTagCompound)
	{
		super.writeToNBT(par1NBTTagCompound);
		int s = 0;
		LiquidStack stack = this.tank.getLiquid();
		if (stack != null)
			s = stack.amount;
		par1NBTTagCompound.setInteger("liquid", s);
		par1NBTTagCompound.setInteger("type", this.type.ordinal());
	}

	@Override
	public void handlePacketData(INetworkManager network, int packetType, Packet250CustomPayload packet, EntityPlayer player, ByteArrayDataInput data)
	{
		try
		{
			this.type = Liquid.getLiquid(data.readInt());
			this.tank.setLiquid(Liquid.getStack(this.type, data.readInt()));
		}
		catch (Exception e)
		{
			e.printStackTrace();
			System.out.print("Fail reading data for Storage tank \n");
		}

	}

	// ----------------------------
	// Liquid stuff
	// ----------------------------
	public void setType(Liquid dm)
	{
		this.type = dm;

	}

	public Liquid getType()
	{
		return this.type;
	}

	@Override
	public int fill(ForgeDirection from, LiquidStack resource, boolean doFill)
	{
		if (!Liquid.isStackEqual(resource, type))
			return 0;
		return this.fill(0, resource, doFill);
	}

	@Override
	public int fill(int tankIndex, LiquidStack resource, boolean doFill)
	{
		if (resource == null || tankIndex != 0)
			return 0;
		if (this.isFull())
		{
			int change = 1;
			if (Liquid.getLiquid(resource).doesFlaot)
				change = -1;
			TileEntity tank = worldObj.getBlockTileEntity(xCoord, yCoord + change, zCoord);
			if (tank instanceof TileEntityTank) { return ((TileEntityTank) tank).tank.fill(resource, doFill); }
		}
		this.doUpdate = true;
		return this.tank.fill(resource, doFill);
	}
	/**
	 * find out if this tank is actual full or not
	 * @return
	 */
	public boolean isFull()
	{
		if (this.tank.getLiquid() == null)
			return false;
		if (this.tank.getLiquid().amount > 0 && this.tank.getLiquid().amount < this.tank.getCapacity())
			return false;
		return true;
	}
	/**
	 * finds the first fillable tank in either direction
	 * @param top - search up 
	 * @return
	 */
	public TileEntityTank getFillAbleTank(boolean top)
	{
		TileEntityTank tank = this;
		boolean stop = false;
		int y = tank.yCoord;
		while (y > 6 && y < 255)
		{
			if (top)
			{
				y += 1;
			}
			else
			{
				y -= 1;
			}
			TileEntity ent = tank.worldObj.getBlockTileEntity(xCoord, y, zCoord);
			if (ent instanceof TileEntityTank && ((TileEntityTank) ent).getType() == this.type && !((TileEntityTank) ent).isFull())
			{
				tank = (TileEntityTank) ent;
			}
			else
			{
				break;
			}
		}
		return tank;
	}

	@Override
	public LiquidStack drain(ForgeDirection from, int maxDrain, boolean doDrain)
	{
		return this.drain(0, maxDrain, doDrain);
	}

	@Override
	public LiquidStack drain(int tankIndex, int maxDrain, boolean doDrain)
	{
		if (tankIndex != 0) {return null;}
		LiquidStack stack = this.tank.getLiquid();
		if(maxDrain <= this.tank.getLiquid().amount)
		{
		    stack =  Liquid.getStack(type, maxDrain);
		}
		if(doDrain)
		{
		    this.tank.drain(maxDrain, doDrain);
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
		return null;
	}

	@Override
	public int presureOutput(Liquid type, ForgeDirection dir)
	{
		if (type == this.type)
		{
			if (type.doesFlaot && dir == ForgeDirection.DOWN)
				return type.defaultPresure;
			if (!type.doesFlaot && dir == ForgeDirection.UP)
				return type.defaultPresure;
		}
		return 0;
	}

	@Override
	public boolean canPressureToo(Liquid type, ForgeDirection dir)
	{
		if (type == this.type)
		{
			if (type.doesFlaot && dir == ForgeDirection.DOWN)
				return true;
			if (!type.doesFlaot && dir == ForgeDirection.UP)
				return true;
		}
		return false;
	}
	/**
	 * cause this TE to trade liquid down if
	 * the liquid is in liquid state or up
	 * if in gas state. 
	 */
	public void tradeDown()
	{
		if (this.tank.getLiquid() == null || this.tank.getLiquid().amount <= 0)
			return;
		TileEntity ent = worldObj.getBlockTileEntity(xCoord, yCoord - 1, zCoord);
		if (ent instanceof TileEntityTank && ((TileEntityTank) ent).type == this.type && !((TileEntityTank) ent).isFull())
		{
			int f = ((TileEntityTank) ent).tank.fill(this.tank.getLiquid(), true);
			this.tank.drain(f, true);
		}
	}
	/**
	 * Cause this TE to trade liquid with
	 * the Tanks around it to level off
	 */
	public void tradeArround()
	{
		if (this.tank.getLiquid() == null || this.tank.getLiquid().amount <= 0)
			return;
		TileEntity[] ents = MHelper.getSourounding(worldObj, xCoord, yCoord, zCoord);
		int commonVol = this.tank.getLiquid().amount;
		int tanks = 1;
		for (int i = 2; i < 6; i++)
		{
			if (ents[i] instanceof TileEntityTank && ((TileEntityTank) ents[i]).type == this.type)
			{
				tanks++;
				if (((TileEntityTank) ents[i]).tank.getLiquid() != null)
				{
					commonVol += ((TileEntityTank) ents[i]).tank.getLiquid().amount;
				}
			}
		}
		int equalVol = commonVol / tanks;
		for (int i = 2; i < 6; i++)
		{
			if (this.tank.getLiquid() == null || this.tank.getLiquid().amount <= equalVol)
				break;

			if (ents[i] instanceof TileEntityTank && ((TileEntityTank) ents[i]).type == this.type && !((TileEntityTank) ents[i]).isFull())
			{
				LiquidStack stack = ((TileEntityTank) ents[i]).tank.getLiquid();
				LiquidStack filling = this.tank.getLiquid();
				if (stack == null)
				{
					filling = Liquid.getStack(this.type, equalVol);
				}
				else if (stack.amount < equalVol)
				{
					filling = Liquid.getStack(this.type, equalVol - stack.amount);
				}
				else
				{
					filling = null;
				}
				int f = ((TileEntityTank) ents[i]).tank.fill(filling, true);
				this.tank.drain(f, true);
			}

		}
	}
}
