package assemblyline.common.imprinter.prefab;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import universalelectricity.prefab.implement.IRotatable;
import universalelectricity.prefab.network.IPacketReceiver;
import universalelectricity.prefab.network.PacketManager;
import assemblyline.api.IFilterable;
import assemblyline.common.AssemblyLine;
import assemblyline.common.imprinter.ItemImprinter;
import assemblyline.common.machine.TileEntityAssembly;

import com.google.common.io.ByteArrayDataInput;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;

public abstract class TileEntityFilterable extends TileEntityAssembly implements IRotatable, IFilterable, IPacketReceiver
{
	private ItemStack filterItem;
	private boolean inverted;

	/**
	 * Looks through the things in the filter and finds out which item is being filtered.
	 * 
	 * @return Is this filterable block filtering this specific ItemStack?
	 */
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

		if (FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER)
		{
			PacketManager.sendPacketToClients(this.getDescriptionPacket());
		}
	}

	@Override
	public ItemStack getFilter()
	{
		return this.filterItem;
	}

	public void setInverted(boolean inverted)
	{
		this.inverted = inverted;
		if (FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER)
		{
			PacketManager.sendPacketToClients(this.getDescriptionPacket());
		}
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
	public ForgeDirection getDirection(IBlockAccess world, int x, int y, int z)
	{
		return ForgeDirection.getOrientation(this.getBlockMetadata());
	}

	@Override
	public void setDirection(World world, int x, int y, int z, ForgeDirection facingDirection)
	{
		this.worldObj.setBlockMetadataWithNotify(this.xCoord, this.yCoord, this.zCoord, facingDirection.ordinal(), 3);
	}

	public void setDirection(ForgeDirection facingDirection)
	{
		this.setDirection(worldObj, xCoord, yCoord, zCoord, facingDirection);
	}

	public ForgeDirection getDirection()
	{
		return this.getDirection(worldObj, xCoord, yCoord, zCoord);
	}

	/**
	 * Don't override this! Override getPackData() instead!
	 */
	@Override
	public Packet getDescriptionPacket()
	{
		return PacketManager.getPacket(AssemblyLine.CHANNEL, this, this.getPacketData().toArray());
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
	public void handlePacketData(INetworkManager network, int packetType, Packet250CustomPayload packet, EntityPlayer player, ByteArrayDataInput dataStream)
	{
		if (worldObj.isRemote)
		{
			ByteArrayInputStream bis = new ByteArrayInputStream(packet.data);
			DataInputStream dis = new DataInputStream(bis);
			int id, x, y, z;
			try
			{
				id = dis.readInt();
				x = dis.readInt();
				y = dis.readInt();
				z = dis.readInt();
				this.worldObj.markBlockForRenderUpdate(x, y, z);
				NBTTagCompound tag = Packet.readNBTTagCompound(dis);
				readFromNBT(tag);
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
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
