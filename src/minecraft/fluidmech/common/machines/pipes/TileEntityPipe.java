package fluidmech.common.machines.pipes;

import fluidmech.common.machines.TileEntityTank;
import hydraulic.core.helpers.connectionHelper;
import hydraulic.core.implement.ColorCode;
import hydraulic.core.implement.IColorCoded;
import hydraulic.core.implement.IPsiCreator;
import hydraulic.core.implement.IReadOut;
import hydraulic.core.liquids.LiquidHandler;

import java.util.Random;

import universalelectricity.prefab.tile.TileEntityAdvanced;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.liquids.ILiquidTank;
import net.minecraftforge.liquids.ITankContainer;
import net.minecraftforge.liquids.LiquidContainerRegistry;
import net.minecraftforge.liquids.LiquidStack;
import net.minecraftforge.liquids.LiquidTank;

public class TileEntityPipe extends TileEntityAdvanced implements ITankContainer, IReadOut, IColorCoded
{
	private ColorCode color = ColorCode.NONE;

	private int presure = 0;

	public boolean converted = false;
	public boolean isUniversal = false;

	public TileEntity[] connectedBlocks = new TileEntity[6];

	public static final int maxVolume = LiquidContainerRegistry.BUCKET_VOLUME * 2;

	private LiquidTank tank = new LiquidTank(maxVolume);

	@Override
	public void updateEntity()
	{

		this.validataConnections();
		this.color = ColorCode.get(worldObj.getBlockMetadata(xCoord, yCoord, zCoord));
		if (ticks % 20 == 0)
		{
			this.updatePressure();

			LiquidStack stack = tank.getLiquid();
			if (!worldObj.isRemote && stack != null && stack.amount >= 0)
			{

				for (int i = 0; i < 6; i++)
				{
					ForgeDirection dir = ForgeDirection.getOrientation(i);

					if (connectedBlocks[i] instanceof ITankContainer)
					{
						if (connectedBlocks[i] instanceof TileEntityPipe)
						{
							if (((TileEntityPipe) connectedBlocks[i]).presure < this.presure)
							{
								tank.drain(((TileEntityPipe) connectedBlocks[i]).fill(dir, stack, true), true);
							}

						}
						else if (connectedBlocks[i] instanceof TileEntityTank && ((TileEntityTank) connectedBlocks[i]).getColor() == this.color)
						{
							if (dir == ForgeDirection.UP && !color.getLiquidData().getCanFloat())
							{
								/* do nothing */
							}
							else if (dir == ForgeDirection.DOWN && color.getLiquidData().getCanFloat())
							{
								/* do nothing */
							}
							else
							{
								tank.drain(((ITankContainer) connectedBlocks[i]).fill(dir.getOpposite(), stack, true), true);
							}
						}
						else
						{
							tank.drain(((ITankContainer) connectedBlocks[i]).fill(dir.getOpposite(), stack, true), true);
						}
					}

					if (stack == null || stack.amount <= 0)
					{
						break;
					}
				}
			}
		}

	}

	public void randomDisplayTick()
	{
		Random random = new Random();
		LiquidStack stack = tank.getLiquid();
		if (stack != null && random.nextInt(10) == 0)
		{
			// TODO align this with the pipe model so not to drip where there is
			// no pipe
			double xx = (double) ((float) xCoord + random.nextDouble());
			double zz = (double) yCoord + .3D;
			double yy = (double) ((float) zCoord + random.nextDouble());

			if (ColorCode.get(stack) != ColorCode.RED)
			{
				worldObj.spawnParticle("dripWater", xx, zz, yy, 0.0D, 0.0D, 0.0D);
			}
			else
			{
				worldObj.spawnParticle("dripLava", xx, zz, yy, 0.0D, 0.0D, 0.0D);
			}
		}
	}

	/**
	 * gets the current color mark of the pipe
	 */
	@Override
	public ColorCode getColor()
	{
		return this.color;
	}

	/**
	 * sets the current color mark of the pipe
	 */
	@Override
	public void setColor(Object cc)
	{
		this.color = ColorCode.get(cc);
	}

	/**
	 * sets the current color mark of the pipe
	 */
	public void setColor(int i)
	{
		if (i < ColorCode.values().length)
		{
			this.color = ColorCode.values()[i];
		}
	}

	/**
	 * Reads a tile entity from NBT.
	 */
	@Override
	public void readFromNBT(NBTTagCompound nbt)
	{
		super.readFromNBT(nbt);

		LiquidStack liquid = new LiquidStack(0, 0, 0);
		liquid.readFromNBT(nbt.getCompoundTag("stored"));
		if (Item.itemsList[liquid.itemID] != null && liquid.amount > 0)
		{
			this.tank.setLiquid(liquid);
		}
	}

	/**
	 * Writes a tile entity to NBT.
	 */
	@Override
	public void writeToNBT(NBTTagCompound nbt)
	{
		super.writeToNBT(nbt);
		if (tank.getLiquid() != null)
		{
			nbt.setTag("stored", tank.getLiquid().writeToNBT(new NBTTagCompound()));
		}
	}

	@Override
	public String getMeterReading(EntityPlayer user, ForgeDirection side)
	{
		LiquidStack stack = this.tank.getLiquid();
		if (stack != null)
		{
			return (stack.amount / LiquidContainerRegistry.BUCKET_VOLUME) + "/" + (this.tank.getCapacity() / LiquidContainerRegistry.BUCKET_VOLUME) + " " + LiquidHandler.get(stack).getName() + " @ " + this.presure + "p";
		}

		return "Empty" + " @ " + this.presure + "p";
	}

	@Override
	public int fill(ForgeDirection from, LiquidStack resource, boolean doFill)
	{
		if (resource == null)
		{
			return 0;
		}
		LiquidStack stack = tank.getLiquid();
		if (this.color.isValidLiquid(resource))
		{
			if (stack == null || (stack != null && stack.isLiquidEqual(resource)))
			{
				return this.fill(0, resource, doFill);
			}
			else
			{
				// return this.causeMix(stack, resource);
			}

		}
		return 0;
	}

	@Override
	public int fill(int tankIndex, LiquidStack resource, boolean doFill)
	{
		if (tankIndex != 0 || resource == null)
		{
			return 0;
		}
		return tank.fill(resource, doFill);
	}

	@Override
	public LiquidStack drain(ForgeDirection from, int maxDrain, boolean doDrain)
	{
		return null;
	}

	@Override
	public LiquidStack drain(int tankIndex, int maxDrain, boolean doDrain)
	{
		return null;
	}

	@Override
	public ILiquidTank[] getTanks(ForgeDirection direction)
	{
		return new ILiquidTank[] { this.tank };
	}

	@Override
	public ILiquidTank getTank(ForgeDirection direction, LiquidStack type)
	{
		if (this.color.isValidLiquid(type))
		{
			return this.tank;
		}
		return null;
	}

	/**
	 * collects and sorts the surrounding TE for valid connections
	 */
	public void validataConnections()
	{
		this.connectedBlocks = connectionHelper.getSurroundingTileEntities(worldObj, xCoord, yCoord, zCoord);

		for (int side = 0; side < 6; side++)
		{
			ForgeDirection direction = ForgeDirection.getOrientation(side);
			TileEntity tileEntity = connectedBlocks[side];

			if (tileEntity instanceof ITankContainer)
			{
				if (tileEntity instanceof TileEntityPipe && this.color != ((TileEntityPipe) tileEntity).getColor())
				{
					connectedBlocks[side] = null;
				}
				// TODO switch side catch for IPressure
				if (this.color != ColorCode.NONE && tileEntity instanceof TileEntityTank && ((TileEntityTank) tileEntity).getColor() != ColorCode.NONE && color != ((TileEntityTank) tileEntity).getColor())
				{
					connectedBlocks[side] = null;
				}
			}
			else if (tileEntity instanceof IPsiCreator)
			{
				if (!((IPsiCreator) tileEntity).getCanPressureTo(color.getLiquidData().getStack(), direction))
				{
					connectedBlocks[side] = null;
				}
			}
			else
			{
				connectedBlocks[side] = null;
			}
		}
	}

	/**
	 * updates this units pressure level using the pipe/machines around it
	 */
	public void updatePressure()
	{
		int highestPressure = 0;
		this.presure = 0;

		for (int i = 0; i < 6; i++)
		{
			ForgeDirection dir = ForgeDirection.getOrientation(i);

			if (connectedBlocks[i] instanceof TileEntityPipe)
			{
				if (((TileEntityPipe) connectedBlocks[i]).getPressure() > highestPressure)
				{
					highestPressure = ((TileEntityPipe) connectedBlocks[i]).getPressure();
				}
			}
			if (connectedBlocks[i] instanceof IPsiCreator && ((IPsiCreator) connectedBlocks[i]).getCanPressureTo(color.getLiquidData().getStack(), dir))
			{

				int p = ((IPsiCreator) connectedBlocks[i]).getPressureOut(color.getLiquidData().getStack(), dir);
				if (p > highestPressure)
					highestPressure = p;
			}
		}
		this.presure = highestPressure - 1;
	}

	public int getPressure()
	{
		return this.presure;
	}
}
