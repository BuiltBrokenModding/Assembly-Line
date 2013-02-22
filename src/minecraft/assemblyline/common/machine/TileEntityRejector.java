package assemblyline.common.machine;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraftforge.common.ForgeDirection;
import universalelectricity.core.vector.Vector3;
import universalelectricity.prefab.network.PacketManager;
import assemblyline.common.machine.imprinter.TileEntityFilterable;

/**
 * 
 * @author Darkguardsman
 * 
 */
public class TileEntityRejector extends TileEntityFilterable
{
	/**
	 * should the piston fire, or be extended
	 */
	public boolean firePiston = false;

	public TileEntityRejector()
	{
		super();
	}

	@Override
	protected int getMaxTransferRange()
	{
		return 20;
	}

	@Override
	public void onUpdate()
	{
		super.onUpdate();
		/**
		 * Has to update a bit faster than a conveyer belt
		 */
		if (this.ticks % 5 == 0 && !this.isDisabled())
		{
			int metadata = this.getBlockMetadata();
			this.firePiston = false;

			// area to search for items
			Vector3 searchPosition = new Vector3(this);
			searchPosition.modifyPositionFromSide(this.getDirection());
			TileEntity tileEntity = searchPosition.getTileEntity(this.worldObj);

			try
			{
				boolean flag = false;

				if (this.isRunning())
				{
					/**
					 * Find all entities in the position in which this block is facing and attempt
					 * to push it out of the way.
					 */
					AxisAlignedBB bounds = AxisAlignedBB.getBoundingBox(searchPosition.x, searchPosition.y, searchPosition.z, searchPosition.x + 1, searchPosition.y + 1, searchPosition.z + 1);
					List<Entity> entitiesInFront = this.worldObj.getEntitiesWithinAABB(Entity.class, bounds);

					for (Entity entity : entitiesInFront)
					{
						if (this.canEntityBeThrow(entity))
						{
							this.throwItem(this.getDirection(), entity);
							flag = true;
						}
					}
				}
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}

	/**
	 * Used to move after it has been rejected
	 * 
	 * @param side - used to do the offset
	 * @param entity - Entity being thrown
	 */
	public void throwItem(ForgeDirection side, Entity entity)
	{
		this.firePiston = true;

		entity.motionX = (double) side.offsetX * 0.1;
		entity.motionY += 0.10000000298023224D;
		entity.motionZ = (double) side.offsetZ * 0.1;

		PacketManager.sendPacketToClients(getDescriptionPacket());
	}

	public boolean canEntityBeThrow(Entity entity)
	{
		// TODO Add other things than items
		if (entity instanceof EntityItem)
		{
			EntityItem entityItem = (EntityItem) entity;
			ItemStack itemStack = entityItem.getEntityItem();

			return this.isFiltering(itemStack);
		}

		return false;
	}

	/**
	 * NBT Data
	 */
	@Override
	public void readFromNBT(NBTTagCompound nbt)
	{
		super.readFromNBT(nbt);
		this.firePiston = nbt.getBoolean("piston");
	}

	/**
	 * Writes a tile entity to NBT.
	 */
	@Override
	public void writeToNBT(NBTTagCompound nbt)
	{
		super.writeToNBT(nbt);
		nbt.setBoolean("piston", this.firePiston);
	}
}
