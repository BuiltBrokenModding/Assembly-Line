package assemblyline.common.machine.detector;

import java.util.ArrayList;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraftforge.common.ForgeDirection;
import universalelectricity.prefab.network.PacketManager;
import assemblyline.common.AssemblyLine;
import assemblyline.common.machine.filter.ItemFilter;
import assemblyline.common.machine.filter.TileEntityFilterable;

public class TileEntityDetector extends TileEntityFilterable
{
	private boolean powering = false;
	private boolean isInverted = false;

	@Override
	public void updateEntity()
	{
		super.updateEntity();

		if (!this.worldObj.isRemote && this.ticks % 10 == 0)
		{
			int metadata = this.worldObj.getBlockMetadata(this.xCoord, this.yCoord, this.zCoord);
			AxisAlignedBB testArea = AxisAlignedBB.getBoundingBox(this.xCoord, this.yCoord - 1, this.zCoord, this.xCoord + 1, this.yCoord, this.zCoord + 1);

			ArrayList<Entity> entities = (ArrayList<Entity>) this.worldObj.getEntitiesWithinAABB(EntityItem.class, testArea);
			boolean powerCheck = false;

			if (entities.size() > 0)
			{
				if (getFilter() != null)
				{
					for (int i = 0; i < entities.size(); i++)
					{
						EntityItem e = (EntityItem) entities.get(i);
						ItemStack itemStack = e.func_92014_d();

						boolean found = this.isFiltering(itemStack);

						if (this.isInverted)
						{
							if (!found)
							{
								powerCheck = true;
							}
							else
							{
								powerCheck = false;
							}
						}
						else if (found)
						{
							powerCheck = true;
							break;
						}
					}
				}
				else
				{
					powerCheck = true;
				}
			}
			else
			{
				powerCheck = false;
			}

			if (powerCheck != this.powering)
			{
				this.powering = powerCheck;
				this.worldObj.notifyBlocksOfNeighborChange(this.xCoord, this.yCoord, this.zCoord, AssemblyLine.blockDetector.blockID);
				this.worldObj.notifyBlocksOfNeighborChange(this.xCoord, this.yCoord + 1, this.zCoord, AssemblyLine.blockDetector.blockID);
				for (int x = this.xCoord - 1; x <= this.xCoord + 1; x++)
				{
					for (int z = this.zCoord - 1; z <= this.zCoord + 1; z++)
					{
						this.worldObj.notifyBlocksOfNeighborChange(x, this.yCoord + 1, z, AssemblyLine.blockDetector.blockID);
					}
				}

				PacketManager.sendPacketToClients(getDescriptionPacket());
			}
		}
	}

	@Override
	public void invalidate()
	{
		this.worldObj.notifyBlocksOfNeighborChange(this.xCoord, this.yCoord, this.zCoord, AssemblyLine.blockDetector.blockID);
		this.worldObj.notifyBlocksOfNeighborChange(this.xCoord, this.yCoord + 1, this.zCoord, AssemblyLine.blockDetector.blockID);
		super.invalidate();
	}

	public boolean isInverted()
	{
		return this.isInverted;
	}

	public void setInversion(boolean inverted)
	{
		this.isInverted = inverted;

		if (this.worldObj.isRemote)
		{
			PacketManager.sendPacketToClients(this.getDescriptionPacket());
		}
	}

	public void toggleInversion()
	{
		this.setInversion(!this.isInverted);
	}

	@Override
	public void readFromNBT(NBTTagCompound tag)
	{
		super.readFromNBT(tag);

		this.isInverted = tag.getBoolean("isInverted");
		this.powering = tag.getBoolean("powering");
	}

	@Override
	public void writeToNBT(NBTTagCompound tag)
	{
		super.writeToNBT(tag);

		tag.setBoolean("isInverted", this.isInverted);
		tag.setBoolean("powering", this.powering);
	}

	public boolean isPoweringTo(ForgeDirection side)
	{
		return this.powering;
	}

	public boolean isIndirectlyPoweringTo(ForgeDirection side)
	{
		return this.isPoweringTo(side);
	}

	@Override
	public String getInvName()
	{
		return "Detector";
	}
}
