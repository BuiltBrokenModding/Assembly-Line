package assemblyline.common.machine.crane;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.ForgeDirection;
import assemblyline.api.ICraneConnectable;
import assemblyline.common.machine.TileEntityAssemblyNetwork;

public class TileEntityCraneController extends TileEntityAssemblyNetwork implements ICraneConnectable
{
	int width, height, depth;
	boolean isCraneValid;
	long ticks;

	public TileEntityCraneController()
	{
		super();
		width = height = depth = 0;
		isCraneValid = false;
		ticks = 0;
	}

	@Override
	public void updateEntity()
	{
		ticks++;
		if (ticks % 20 == 0)
		{
			validateCrane();
		}
	}

	public boolean isCraneValid()
	{
		return isCraneValid;
	}

	private void validateCrane()
	{
		isCraneValid = false;
		width = height = depth = 0;
		findCraneHeight();
		findCraneWidth();
		findCraneDepth();
		if (Math.abs(height) > 1 && Math.abs(width) > 1 && Math.abs(depth) > 1)
		{
			isCraneValid = isFrameValid();
		}
	}

	private boolean isFrameValid()
	{
		for (int x = Math.min(0, width); x <= Math.max(0, width); x++)
		{
			if (!CraneHelper.isCraneStructureBlock(worldObj, xCoord + x, yCoord + height, zCoord))
				return false;
		}
		for (int x = Math.min(0, width); x <= Math.max(0, width); x++)
		{
			if (!CraneHelper.isCraneStructureBlock(worldObj, xCoord + x, yCoord + height, zCoord + depth))
				return false;
		}
		for (int z = Math.min(0, depth); z <= Math.max(0, depth); z++)
		{
			if (!CraneHelper.isCraneStructureBlock(worldObj, xCoord, yCoord + height, zCoord + z))
				return false;
		}
		for (int z = Math.min(0, depth); z <= Math.max(0, depth); z++)
		{
			if (!CraneHelper.isCraneStructureBlock(worldObj, xCoord + width, yCoord + height, zCoord + z))
				return false;
		}
		for (int x = Math.min(width + 1, 1); x <= Math.max(-1, width - 1); x++)
		{
			for (int z = Math.min(depth + 1, 1); z <= Math.max(-1, depth - 1); z++)
			{
				if (!worldObj.isAirBlock(xCoord + x, yCoord + height, zCoord + z))
					return false;
			}
		}
		return true;
	}

	/**
	 * Find x size and store in this.width
	 */
	private void findCraneWidth()
	{
		if (height == 0)
		{
			width = 0;
			return;
		}
		int x = 0;
		ForgeDirection facing = ForgeDirection.getOrientation(worldObj.getBlockMetadata(xCoord, yCoord, zCoord));
		while (true)
		{
			if (Math.abs(x) > CraneHelper.MAX_SIZE)
				break;
			if (!CraneHelper.isCraneStructureBlock(worldObj, xCoord + x, yCoord + height, zCoord))
				break;
			if (facing == ForgeDirection.NORTH || facing == ForgeDirection.EAST)
			{
				x++;
			}
			else
			{
				x--;
			}
		}
		width = x; // can be negative
		if (width < 0)
			width++;
		if (width > 0)
			width--;
	}

	/**
	 * Find y size and store in this.height
	 */
	private void findCraneHeight()
	{
		int y = 1;
		while (true)
		{
			if (yCoord + y >= 256)
				break;
			if (y > CraneHelper.MAX_SIZE)
				break;
			if (!CraneHelper.isCraneStructureBlock(worldObj, xCoord, yCoord + y, zCoord))
				break;
			y++;
		}
		height = y - 1;
	}

	/**
	 * Find x size and store in this.depth
	 */
	private void findCraneDepth()
	{
		if (height == 0)
		{
			width = 0;
			return;
		}
		int z = 0;
		ForgeDirection facing = ForgeDirection.getOrientation(worldObj.getBlockMetadata(xCoord, yCoord, zCoord));
		while (true)
		{
			if (Math.abs(z) > CraneHelper.MAX_SIZE)
				break;
			if (!CraneHelper.isCraneStructureBlock(worldObj, xCoord, yCoord + height, zCoord + z))
				break;
			if (facing == ForgeDirection.SOUTH || facing == ForgeDirection.EAST)
			{
				z++;
			}
			else
			{
				z--;
			}
		}
		depth = z; // can be negative
		if (depth < 0)
			depth++;
		if (depth > 0)
			depth--;
	}

	@Override
	public boolean canFrameConnectTo(ForgeDirection side)
	{
		ForgeDirection facing = ForgeDirection.getOrientation(worldObj.getBlockMetadata(xCoord, yCoord, zCoord));
		if (side == facing)
			return true;
		if (side == CraneHelper.rotateClockwise(facing))
			return true;
		if (side == ForgeDirection.UP)
			return true;
		return false;
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt)
	{
		super.writeToNBT(nbt);
		nbt.setInteger("width", width);
		nbt.setInteger("height", height);
		nbt.setInteger("depth", depth);
		nbt.setBoolean("isValid", isCraneValid);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt)
	{
		super.readFromNBT(nbt);
		width = nbt.getInteger("width");
		height = nbt.getInteger("height");
		depth = nbt.getInteger("depth");
		isCraneValid = nbt.getBoolean("isValid");
	}

	@Override
	public boolean canConnect(ForgeDirection direction)
	{
		return true;
	}
}
