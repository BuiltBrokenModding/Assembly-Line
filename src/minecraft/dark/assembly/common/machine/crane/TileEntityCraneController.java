package dark.assembly.common.machine.crane;

import java.awt.Color;

import dark.assembly.api.ICraneStructure;
import dark.assembly.common.AssemblyLine;
import dark.assembly.common.machine.TileEntityAssembly;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.ForgeDirection;
import universalelectricity.core.vector.Vector3;

public class TileEntityCraneController extends TileEntityAssembly implements ICraneStructure
{
	int width, depth;
	boolean isCraneValid;
	Vector3 armPos;

	public TileEntityCraneController()
	{
		width = depth = 0;
		isCraneValid = false;
	}

	@Override
	public void initiate()
	{
		this.validateCrane();
		if (armPos == null || armPos.equals(new Vector3()))
		{
			int deltaX = 0;
			int deltaZ = 0;
			switch (this.getFacing())
			{
				case SOUTH:
				case EAST:
					deltaX = (this.width / 2);
					deltaZ = (this.depth / 2);
					break;
				case NORTH:
				case WEST:
					deltaX = -(this.width / 2);
					deltaZ = -(this.depth / 2);
					break;
			}
			armPos = new Vector3(this.xCoord + deltaX, this.yCoord, this.zCoord + deltaZ);
		}
	}

	public ForgeDirection getFacing()
	{
		return ForgeDirection.getOrientation(this.worldObj.getBlockMetadata(this.xCoord, this.yCoord, this.zCoord));
	}



	@Override
	public void updateEntity()
	{
		if (this.worldObj.isRemote && armPos != null)
		{
			AssemblyLine.proxy.renderBeam(this.worldObj, new Vector3(this), armPos, Color.BLUE, 1);
		}
		super.updateEntity();
		if (ticks % 60 == 0)
		{
			this.validateCrane();
		}
	}

	public boolean isCraneValid()
	{
		return isCraneValid;
	}

	private void validateCrane()
	{
		isCraneValid = false;
		width = depth = 0;
		findCraneWidth();
		System.out.println("CraneValidator: Width = " + this.width);
		findCraneDepth();
		System.out.println("CraneValidator: Depth = " + this.depth);
		if (Math.abs(width) > 1 && Math.abs(depth) > 1)
		{
			isCraneValid = isFrameValid();

		}
		System.out.println("CraneValidator: is valid? " + this.isCraneValid);
	}

	private boolean isFrameValid()
	{
		for (int x = Math.min(0, width); x <= Math.max(0, width); x++)
		{
			if (!CraneHelper.isCraneStructureBlock(worldObj, xCoord + x, yCoord, zCoord))
			{
				System.out.println("CraneValidator: Failed width check ");
				return false;
			}
		}
		for (int z = Math.min(0, depth); z <= Math.max(0, depth); z++)
		{
			if (!CraneHelper.isCraneStructureBlock(worldObj, xCoord, yCoord, zCoord + z))
			{
				System.out.println("CraneValidator: Failed Depth Check? ");
				return false;
			}
		}
		for (int x = Math.min(width + 1, 1); x <= Math.max(-1, width - 1); x++)
		{
			for (int z = Math.min(depth + 1, 1); z <= Math.max(-1, depth - 1); z++)
			{
				if (!worldObj.isAirBlock(xCoord + x, yCoord, zCoord + z))
				{
					System.out.println("CraneValidator: Failed Area check");
					return false;
				}
			}
		}
		return true;
	}

	/** Find x size and store in this.width */
	private void findCraneWidth()
	{
		int x = 0;
		ForgeDirection facing = this.getFacing();
		System.out.println("CraneValidator: Width direction = " + facing.ordinal());
		while (true)
		{
			if (Math.abs(x) >= CraneHelper.MAX_SIZE)
			{
				break;
			}
			if (!CraneHelper.isCraneStructureBlock(worldObj, xCoord + x, yCoord, zCoord))
			{
				System.out.println("CraneValidator: Hit non block at x = " + x);
				break;
			}
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
		{
			width++;
		}
		if (width > 0)
		{
			width--;
		}
	}

	/** Find x size and store in this.depth */
	private void findCraneDepth()
	{
		int z = 0;
		ForgeDirection facing = this.getFacing();
		while (true)
		{
			if (Math.abs(z) > CraneHelper.MAX_SIZE)
				break;
			if (!CraneHelper.isCraneStructureBlock(worldObj, xCoord, yCoord, zCoord + z))
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
		nbt.setInteger("depth", depth);
		nbt.setBoolean("isValid", isCraneValid);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt)
	{
		super.readFromNBT(nbt);
		width = nbt.getInteger("width");
		depth = nbt.getInteger("depth");
		isCraneValid = nbt.getBoolean("isValid");
	}

	@Override
	public boolean canConnect(ForgeDirection direction)
	{
		return true;
	}

	@Override
	public void onUpdate()
	{
		// TODO Auto-generated method stub

	}
}
