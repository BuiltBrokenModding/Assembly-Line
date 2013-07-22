package dark.assembly.common.machine.detector;

import java.util.ArrayList;

import dark.assembly.common.AssemblyLine;
import dark.assembly.common.imprinter.prefab.TileEntityFilterable;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraftforge.common.ForgeDirection;
import universalelectricity.prefab.network.PacketManager;

public class TileEntityDetector extends TileEntityFilterable
{

	private boolean powering = false;

	@Override
	public void updateEntity()
	{
		super.updateEntity();

		if (!this.worldObj.isRemote && this.ticks % 10 == 0)
		{
			int metadata = this.worldObj.getBlockMetadata(this.xCoord, this.yCoord, this.zCoord);
			AxisAlignedBB testArea = AxisAlignedBB.getBoundingBox(this.xCoord, this.yCoord, this.zCoord, this.xCoord + 1, this.yCoord + 1, this.zCoord + 1);
			ForgeDirection dir = ForgeDirection.getOrientation(metadata);
			testArea.offset(dir.offsetX, dir.offsetY, dir.offsetZ);

			ArrayList<Entity> entities = (ArrayList<Entity>) this.worldObj.getEntitiesWithinAABB(EntityItem.class, testArea);
			boolean powerCheck = false;

			if (entities.size() > 0)
			{
				if (getFilter() != null)
				{
					for (int i = 0; i < entities.size(); i++)
					{
						EntityItem e = (EntityItem) entities.get(i);
						ItemStack itemStack = e.getEntityItem();

						powerCheck = this.isFiltering(itemStack);
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

	@Override
	public void readFromNBT(NBTTagCompound tag)
	{
		super.readFromNBT(tag);

		this.powering = tag.getBoolean("powering");
	}

	@Override
	public void writeToNBT(NBTTagCompound tag)
	{
		super.writeToNBT(tag);

		tag.setBoolean("powering", this.powering);
	}

	public int isPoweringTo(ForgeDirection side)
	{
		return this.powering && this.getDirection() != side.getOpposite() ? 15 : 0;
	}

	public boolean isIndirectlyPoweringTo(ForgeDirection side)
	{
		return this.isPoweringTo(side) > 0;
	}

	@Override
	public boolean canConnect(ForgeDirection direction)
	{
		return direction != this.getDirection();
	}

	@Override
	public void onUpdate()
	{
		// TODO Auto-generated method stub

	}
}
