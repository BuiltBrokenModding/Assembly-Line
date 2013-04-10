package fluidmech.common.tiles;

import fluidmech.common.machines.pipes.TileEntityPipe;
import hydraulic.api.ColorCode;
import hydraulic.api.IColorCoded;
import hydraulic.api.IPipeConnection;
import hydraulic.api.IReadOut;
import hydraulic.fluidnetwork.IFluidNetworkPart;
import hydraulic.helpers.connectionHelper;
import hydraulic.prefab.tile.TileEntityFluidDevice;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.liquids.ILiquidTank;
import net.minecraftforge.liquids.ITankContainer;
import net.minecraftforge.liquids.LiquidContainerRegistry;
import net.minecraftforge.liquids.LiquidStack;

public class TileEntityReleaseValve extends TileEntityFluidDevice implements IPipeConnection, IReadOut
{
	public boolean[] allowed = new boolean[ColorCode.values().length - 1];
	public TileEntity[] connected = new TileEntity[6];

	private List<IFluidNetworkPart> output = new ArrayList<IFluidNetworkPart>();
	private ITankContainer[] input = new ITankContainer[6];

	public boolean isPowered = false;

	@Override
	public void updateEntity()
	{
		super.updateEntity();

		this.isPowered = worldObj.isBlockIndirectlyGettingPowered(xCoord, yCoord, zCoord);

		connected = connectionHelper.getSurroundingTileEntities(this);

		for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS)
		{
			if (connected[dir.ordinal()] instanceof ITankContainer)
			{
				if (connected[dir.ordinal()] instanceof IColorCoded && !this.canConnect(((IColorCoded) connected[dir.ordinal()]).getColor()))
				{
					connected[dir.ordinal()] = null;
				}
			}
			else
			{
				connected[dir.ordinal()] = null;
			}
		}

		if (!this.worldObj.isRemote && !isPowered && this.ticks % 20 == 0)
		{
			this.validateNBuildList();
			// start the draining process
			for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS)
			{
				ITankContainer drainedTank = input[dir.ordinal()];
				if (drainedTank != null)
				{
					LiquidStack stack = drainedTank.drain(dir.getOpposite(), LiquidContainerRegistry.BUCKET_VOLUME, false);
					if (stack != null && stack.amount > 0)
					{
						IFluidNetworkPart inputPipe = this.findValidPipe(stack);
						if (inputPipe != null)
						{
							int ammountFilled = inputPipe.getNetwork().addFluidToNetwork((TileEntity) drainedTank, stack, true);
							drainedTank.drain(ForgeDirection.UNKNOWN, ammountFilled, true);
						}
					}
				}
			}

		}
	}

	/** used to find a valid pipe for filling of the liquid type */
	public IFluidNetworkPart findValidPipe(LiquidStack stack)
	{
		// find normal color selective pipe first
		for (IFluidNetworkPart pipe : output)
		{
			if (pipe.fill(ForgeDirection.UNKNOWN, stack, false) > 0)
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
	 * Collects info about the surrounding 6 tiles and orders them into drain-able(ITankContainer)
	 * and fill-able(TileEntityPipes) instances
	 */
	public void validateNBuildList()
	{
		// cleanup
		this.connected = connectionHelper.getSurroundingTileEntities(worldObj, xCoord, yCoord, zCoord);
		this.input = new ITankContainer[6];
		this.output.clear();
		// read surroundings
		for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS)
		{
			TileEntity tileEntity = connected[dir.ordinal()];
			if (tileEntity instanceof IFluidNetworkPart)
			{
				IFluidNetworkPart pipe = (IFluidNetworkPart) tileEntity;
				if (this.canConnect(pipe.getColor()))
				{
					this.output.add(pipe);
				}
				else
				{
					this.connected[dir.ordinal()] = null;
				}
			}
			else if (tileEntity instanceof ITankContainer)
			{
				ITankContainer tank = (ITankContainer) tileEntity;
				if (tank != null && tank.drain(dir.getOpposite(), LiquidContainerRegistry.BUCKET_VOLUME, false) != null)
				{
					this.input[dir.ordinal()] = (ITankContainer) tileEntity;
				}
			}
			else
			{
				connected[dir.ordinal()] = null;
			}
		}
	}

	@Override
	public boolean canPipeConnect(TileEntity entity, ForgeDirection dir)
	{
		return entity != null && entity instanceof IColorCoded && this.canConnect(((IColorCoded) entity).getColor());
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
