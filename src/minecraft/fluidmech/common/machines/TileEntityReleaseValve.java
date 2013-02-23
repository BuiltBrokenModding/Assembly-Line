package fluidmech.common.machines;

import hydraulic.core.helpers.connectionHelper;
import hydraulic.core.implement.ColorCode;
import hydraulic.core.implement.IColorCoded;
import hydraulic.core.implement.IPsiCreator;
import hydraulic.core.implement.IReadOut;
import hydraulic.core.liquids.LiquidData;
import hydraulic.core.liquids.LiquidHandler;

import java.util.ArrayList;
import java.util.List;

import fluidmech.common.block.BlockReleaseValve;
import fluidmech.common.machines.pipes.TileEntityPipe;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.liquids.ILiquidTank;
import net.minecraftforge.liquids.ITankContainer;
import net.minecraftforge.liquids.LiquidStack;
import universalelectricity.prefab.implement.IRedstoneReceptor;
import universalelectricity.prefab.tile.TileEntityAdvanced;

public class TileEntityReleaseValve extends TileEntityAdvanced implements IPsiCreator, IReadOut
{
	public boolean[] allowed = new boolean[ColorCode.values().length - 1];
	public TileEntity[] connected = new TileEntity[6];

	private List<TileEntityPipe> output = new ArrayList<TileEntityPipe>();
	private List<ITankContainer> input = new ArrayList<ITankContainer>();

	public boolean isPowered = false;

	private ItemStack[] inventory = new ItemStack[0];

	@Override
	public void updateEntity()
	{
		super.updateEntity();

		this.isPowered = worldObj.isBlockGettingPowered(xCoord, yCoord, zCoord) || worldObj.isBlockIndirectlyGettingPowered(xCoord, yCoord, zCoord);

		connected = connectionHelper.getSurroundingTileEntities(this);

		for (int i = 0; i < 6; i++)
		{
			if (connected[i] instanceof ITankContainer)
			{
				if (connected[i] instanceof IColorCoded && !this.canConnect(((IColorCoded) connected[i]).getColor()))
				{
					connected[i] = null;
				}
			}
			else
			{
				connected[i] = null;
			}
		}

		if (!this.worldObj.isRemote && this.ticks % 20 == 0)
		{
			validateNBuildList();
			// start the draining process
			if (this.input.size() > 0 && this.output.size() > 0)
			{
				for (ITankContainer drainedTank : input)
				{
					LiquidStack stack = drainedTank.drain(ForgeDirection.UNKNOWN, TileEntityPipe.maxVolume, false);
					if (stack != null && stack.amount > 0)
					{
						TileEntityPipe inputPipe = this.findValidPipe(stack);
						if (inputPipe != null)
						{
							ILiquidTank pipeVolume = inputPipe.getTanks(ForgeDirection.UNKNOWN)[0];
							int ammountFilled = inputPipe.fill(ForgeDirection.UNKNOWN, stack, true);
							drainedTank.drain(ForgeDirection.UNKNOWN, ammountFilled, true);
						}
					}
				}
			}

		}
	}

	/** used to find a valid pipe for filling of the liquid type */
	public TileEntityPipe findValidPipe(LiquidStack stack)
	{
		// find normal color selective pipe first
		for (TileEntityPipe pipe : output)
		{
			ILiquidTank tank = pipe.getTanks(ForgeDirection.UNKNOWN)[0];
			if (pipe.getColor().getLiquidData().getStack().isLiquidEqual(stack) && (tank.getLiquid() == null || tank.getLiquid().amount < tank.getCapacity()))
			{
				//
				return pipe;
			}
		}
		// if no color selective pipe is found look for generic pipes
		for (TileEntityPipe pipe : output)
		{
			if (pipe.getColor() == ColorCode.NONE)
			{
				return pipe;
			}
		}

		return null;
	}

	/** sees if it can connect to a pipe of some color */
	public boolean canConnect(ColorCode cc)
	{
		if (this.isRestricted())
		{
			for (int i = 0; i < this.allowed.length; i++)
			{
				if (i == cc.ordinal())
				{
					return allowed[i];
				}
			}
		}
		return true;
	}

	/**
	 * if any of allowed list is true
	 * 
	 * @return true
	 */
	public boolean isRestricted()
	{
		for (int i = 0; i < this.allowed.length; i++)
		{
			if (allowed[i])
			{
				return true;
			}
		}
		return false;
	}

	/**
	 * checks a liquidstack against its color code
	 * 
	 * @param stack
	 * @return
	 */
	public boolean canAcceptLiquid(LiquidStack stack)
	{
		return !this.isRestricted() || canConnect(ColorCode.get(LiquidHandler.get(stack)));
	}

	/**
	 * Collects info about the surrounding 6 tiles and orders them into drain-able(ITankContainer)
	 * and fill-able(TileEntityPipes) instances
	 */
	public void validateNBuildList()
	{
		// cleanup
		this.connected = connectionHelper.getSurroundingTileEntities(worldObj, xCoord, yCoord, zCoord);
		this.input.clear();
		this.output.clear();
		// read surroundings
		for (int i = 0; i < 6; i++)
		{
			ForgeDirection dir = ForgeDirection.getOrientation(i);
			TileEntity ent = connected[i];
			if (ent instanceof TileEntityPipe)
			{
				TileEntityPipe pipe = (TileEntityPipe) ent;
				ILiquidTank tank = pipe.getTanks(ForgeDirection.UNKNOWN)[0];
				if (this.isRestricted() && this.canConnect(pipe.getColor()))
				{
					connected[i] = null;
				}
				else if (tank.getLiquid() != null && tank.getLiquid().amount >= tank.getCapacity())
				{
					connected[i] = null;
				}
				else
				{
					this.output.add(pipe);
				}
			}
			else if (ent instanceof ITankContainer)
			{
				ILiquidTank[] tanks = ((ITankContainer) connected[i]).getTanks(dir);
				for (int t = 0; t < tanks.length; t++)
				{
					LiquidStack ll = tanks[t].getLiquid();
					if (ll != null && ll.amount > 0 && ll.amount > 0)
					{
						if (this.canAcceptLiquid(ll))
						{
							this.input.add((ITankContainer) ent);
							break;
						}
					}
				}
			}
			else
			{
				connected[i] = null;
			}
		}
	}

	@Override
	public int getPressureOut(LiquidData type, ForgeDirection dir)
	{		
		return (type != null && this.canConnect(type.getColor()) ? type.getPressure() : 0);
	}

	@Override
	public boolean getCanPressureTo(LiquidData type, ForgeDirection dir)
	{
		return type != null && this.canConnect(type.getColor());
	}

	@Override
	public String getMeterReading(EntityPlayer user, ForgeDirection side)
	{
		// TODO maybe debug on # of connected units of input/output
		String output = "";
		if (this.isRestricted())
		{
			output += "Output: Restricted and";
		}
		else
		{
			output += " Output: UnRestricted and";
		}
		if (!this.isPowered)
		{
			output += " Open ";
		}
		else
		{
			output += " Closed ";
		}
		return output;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt)
	{
		super.readFromNBT(nbt);
		for (int i = 0; i < this.allowed.length; i++)
		{
			allowed[i] = nbt.getBoolean("allowed" + i);
		}
	}

	/** Writes a tile entity to NBT. */
	@Override
	public void writeToNBT(NBTTagCompound nbt)
	{
		super.writeToNBT(nbt);
		for (int i = 0; i < this.allowed.length; i++)
		{
			nbt.setBoolean("allowed" + i, allowed[i]);
		}
	}
}
