package assemblyline.common.imprinter.prefab;

import java.util.ArrayList;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.packet.Packet;
import net.minecraftforge.common.ForgeDirection;
import universalelectricity.prefab.network.IPacketReceiver;
import universalelectricity.prefab.network.PacketManager;
import universalelectricity.prefab.tile.IRotatable;
import assemblyline.api.IFilterable;
import assemblyline.common.AssemblyLine;
import assemblyline.common.imprinter.ItemImprinter;
import assemblyline.common.machine.TileEntityAssembly;

public abstract class TileEntityFilterable extends TileEntityAssembly implements IRotatable, IFilterable, IPacketReceiver
{
	public TileEntityFilterable(int tickEnergy)
	{
		super(tickEnergy);
		// TODO Auto-generated constructor stub
	}

	private ItemStack filterItem;
	private boolean inverted;

	/** Looks through the things in the filter and finds out which item is being filtered.
	 * 
	 * @return Is this filterable block filtering this specific ItemStack? */
	public boolean isFiltering(ItemStack itemStack)
	{
		if (this.getFilter() != null && itemStack != null)
		{
			ArrayList<ItemStack> checkStacks = ItemImprinter.getFilters(getFilter());

			if (checkStacks != null)
			{
				for (int i = 0; i < checkStacks.size(); i++)
				{
					if (checkStacks.get(i) != null)
					{
						if (checkStacks.get(i).isItemEqual(itemStack))
						{
							return !inverted;
						}
					}
				}
			}
		}

		return inverted;
	}

	@Override
	public void setFilter(ItemStack filter)
	{
		this.filterItem = filter;
		this.worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
	}

	@Override
	public ItemStack getFilter()
	{
		return this.filterItem;
	}

	public void setInverted(boolean inverted)
	{
		this.inverted = inverted;
		this.worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
	}

	public boolean isInverted()
	{
		return this.inverted;
	}

	public void toggleInversion()
	{
		setInverted(!isInverted());
	}

	@Override
	public ForgeDirection getDirection()
	{
		return ForgeDirection.getOrientation(this.getBlockMetadata());
	}

	@Override
	public void setDirection(ForgeDirection facingDirection)
	{
		this.worldObj.setBlockMetadataWithNotify(this.xCoord, this.yCoord, this.zCoord, facingDirection.ordinal(), 3);
	}
	/** Don't override this! Override getPackData() instead! */
	@Override
	public Packet getDescriptionPacket()
	{
		return PacketManager.getPacket(AssemblyLine.CHANNEL, this, AssemblyTilePacket.NBT.ordinal(), this.getPacketData().toArray());
	}

	public ArrayList getPacketData()
	{
		ArrayList array = new ArrayList();
		NBTTagCompound tag = new NBTTagCompound();
		writeToNBT(tag);
		array.add(tag);
		return array;
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt)
	{
		super.writeToNBT(nbt);

		NBTTagCompound filter = new NBTTagCompound();
		if (getFilter() != null)
			getFilter().writeToNBT(filter);
		nbt.setTag("filter", filter);
		nbt.setBoolean("inverted", inverted);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt)
	{
		super.readFromNBT(nbt);

		inverted = nbt.getBoolean("inverted");
		NBTTagCompound filter = nbt.getCompoundTag("filter");
		this.filterItem = ItemStack.loadItemStackFromNBT(filter);
	}

}
