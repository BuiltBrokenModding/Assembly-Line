package dark.fluid.common.pipes;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;

import org.bouncycastle.util.Arrays;

import universalelectricity.core.vector.Vector3;
import universalelectricity.core.vector.VectorHelper;
import universalelectricity.prefab.network.IPacketReceiver;
import universalelectricity.prefab.network.PacketManager;
import universalelectricity.prefab.tile.TileEntityAdvanced;

import com.google.common.io.ByteArrayDataInput;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import dark.api.ColorCode;
import dark.api.IColorCoded;
import dark.api.ITileConnector;
import dark.api.IToolReadOut;
import dark.api.fluid.INetworkPipe;
import dark.core.helpers.FluidRestrictionHandler;
import dark.core.network.fluid.NetworkPipes;
import dark.core.tile.network.NetworkTileEntities;
import dark.fluid.common.FluidMech;
import dark.fluid.common.pipes.addon.IPipeExtention;

public class TileEntityPipe extends TileEntityAdvanced implements IFluidHandler, IToolReadOut, IColorCoded, INetworkPipe, IPacketReceiver
{

	/* TANK TO FAKE OTHER TILES INTO BELIVING THIS HAS AN INTERNAL STORAGE */
	protected FluidTank fakeTank = new FluidTank(FluidContainerRegistry.BUCKET_VOLUME);
	/* CURRENTLY CONNECTED TILE ENTITIES TO THIS */
	private List<TileEntity> connectedBlocks = new ArrayList<TileEntity>();
	public boolean[] renderConnection = new boolean[6];
	public IPipeExtention[] subEntities = new IPipeExtention[6];
	/* RANDOM INSTANCE USED BY THE UPDATE TICK */
	private Random random = new Random();
	/* NETWORK INSTANCE THAT THIS PIPE USES */
	private NetworkPipes pipeNetwork;

	private boolean shouldAutoDrain = false;

	public enum PacketID
	{
		PIPE_CONNECTIONS,
		EXTENTION_CREATE,
		EXTENTION_UPDATE;
	}

	@Override
	public void initiate()
	{
		this.refresh();
		if (this.subEntities[0] == null)
		{
			// this.addNewExtention(0, TileEntityPipeWindow.class);
		}
		if (!worldObj.isRemote)
		{

			for (int i = 0; i < 6; i++)
			{
				TileEntity entity = (TileEntity) this.subEntities[i];
				if (entity != null)
				{
					this.initSubTile(i);
				}
			}
		}
	}

	@Override
	public void updateEntity()
	{
		super.updateEntity();
		if (ticks > 1)
		{
			this.updateSubEntities();
		}
		if (!worldObj.isRemote)
		{
			if (ticks % ((int) random.nextInt(5) * 40 + 20) == 0)
			{
				this.refresh();
			}
			if (ticks % ((int) random.nextInt(5) * 60 + 20) == 0)
			{
				for (int i = 0; i < 6; i++)
				{
					if (this.subEntities[i] != null)
					{
						this.initSubTile(i);
					}
				}
			}
		}
	}

	/** Builds and sends data to client for all PipeExtentions */
	private void updateSubEntities()
	{

		for (int i = 0; i < 6; i++)
		{
			if (subEntities[i] instanceof IPipeExtention && subEntities[i] instanceof TileEntity)
			{
				IPipeExtention extention = subEntities[i];
				if (this.ticks % extention.updateTick() == 0)
				{
					((TileEntity) extention).updateEntity();
					if (extention.shouldSendPacket(!this.worldObj.isRemote) && extention.getExtentionPacketData(!this.worldObj.isRemote) != null)
					{
						Packet packet = PacketManager.getPacket(FluidMech.CHANNEL, this, PacketID.EXTENTION_UPDATE.ordinal(), ForgeDirection.getOrientation(i), extention.getExtentionPacketData(!this.worldObj.isRemote));
						PacketManager.sendPacketToClients(packet, worldObj, new Vector3(this), 50);
					}
				}
			}
		}
	}

	@Override
	public void invalidate()
	{
		if (!this.worldObj.isRemote)
		{
			this.getTileNetwork().splitNetwork(this.worldObj, this);
		}

		super.invalidate();
	}

	@Override
	public void handlePacketData(INetworkManager network, int type, Packet250CustomPayload packet, EntityPlayer player, ByteArrayDataInput dataStream)
	{
		try
		{
			PacketID id = PacketID.values()[dataStream.readInt()];
			if (this.worldObj.isRemote)
			{
				if (id == PacketID.PIPE_CONNECTIONS)
				{
					this.renderConnection[0] = dataStream.readBoolean();
					this.renderConnection[1] = dataStream.readBoolean();
					this.renderConnection[2] = dataStream.readBoolean();
					this.renderConnection[3] = dataStream.readBoolean();
					this.renderConnection[4] = dataStream.readBoolean();
					this.renderConnection[5] = dataStream.readBoolean();
				}
				else if (id == PacketID.EXTENTION_CREATE)
				{
					System.out.println("Handling Packet for Pipe addon");
					int side = dataStream.readInt();
					NBTTagCompound tag = PacketManager.readNBTTagCompound(dataStream);
					this.loadOrCreateSubTile(side, tag);

				}
				else if (id == PacketID.EXTENTION_UPDATE)
				{
					int side = dataStream.readInt();
					if (this.subEntities[side] instanceof IPipeExtention)
					{
						this.subEntities[side].handlePacketData(network, type, packet, player, dataStream);
					}
				}
			}
		}
		catch (IOException e)
		{
			System.out.print("Error with reading packet for TileEntityPipe");
			e.printStackTrace();
		}
	}

	@Override
	public Packet getDescriptionPacket()
	{
		return PacketManager.getPacket(FluidMech.CHANNEL, this, PacketID.PIPE_CONNECTIONS.ordinal(), this.renderConnection[0], this.renderConnection[1], this.renderConnection[2], this.renderConnection[3], this.renderConnection[4], this.renderConnection[5]);
	}

	/** Reads a tile entity from NBT. */
	@Override
	public void readFromNBT(NBTTagCompound nbt)
	{
		super.readFromNBT(nbt);
		FluidStack liquid = FluidStack.loadFluidStackFromNBT(nbt.getCompoundTag("FluidTank"));
		if (nbt.hasKey("stored"))
		{
			NBTTagCompound tag = nbt.getCompoundTag("stored");
			String name = tag.getString("LiquidName");
			int amount = nbt.getInteger("Amount");
			Fluid fluid = FluidRegistry.getFluid(name);
			if (fluid != null)
			{
				liquid = new FluidStack(fluid, amount);
			}
		}
		if (liquid != null)
		{
			this.fakeTank.setFluid(liquid);
		}
		for (int i = 0; i < 6; i++)
		{
			if (nbt.hasKey("Addon" + i))
			{
				this.loadOrCreateSubTile(i, nbt.getCompoundTag("Addon" + i));
			}
		}
	}

	/** Writes a tile entity to NBT. */
	@Override
	public void writeToNBT(NBTTagCompound nbt)
	{
		super.writeToNBT(nbt);
		if (this.fakeTank != null && this.fakeTank.getFluid() != null)
		{
			nbt.setTag("FluidTank", this.fakeTank.getFluid().writeToNBT(new NBTTagCompound()));
		}
		for (int i = 0; i < 6; i++)
		{
			if (this.subEntities[i] != null)
			{
				NBTTagCompound tag = new NBTTagCompound();
				((TileEntity) this.subEntities[i]).writeToNBT(tag);
				nbt.setTag("Addon" + i, tag);
			}
		}
	}

	public boolean addNewExtention(int side, Class<? extends TileEntity> partClass)
	{
		if (partClass == null)
		{
			return false;
		}
		try
		{
			TileEntity tile = partClass.newInstance();
			if (tile instanceof IPipeExtention)
			{
				this.subEntities[side] = (IPipeExtention) tile;
				this.initSubTile(side);
			}
		}
		catch (Exception e)
		{
			System.out.print("Failed to add a Pipe Extention using Class " + partClass.toString());
			e.printStackTrace();
		}
		return false;
	}

	public void loadOrCreateSubTile(int side, NBTTagCompound tag)
	{
		if (tag != null && tag.hasKey("id"))
		{
			TileEntity tile = TileEntity.createAndLoadEntity(tag);
			if (tile instanceof IPipeExtention)
			{
				this.subEntities[side] = (IPipeExtention) tile;
				this.initSubTile(side);
				if (worldObj != null)
				{
					System.out.println("Creating addon " + (worldObj.isRemote ? "Client" : "Server"));
				}
				else
				{
					System.out.println("Creating addon Unkown side");
				}
			}
		}
	}

	public void initSubTile(int side)
	{
		if (this.subEntities[side] instanceof TileEntity)
		{
			TileEntity tile = (TileEntity) subEntities[side];
			((IPipeExtention) tile).setPipe(this);
			((IPipeExtention) tile).setDirection(ForgeDirection.getOrientation(side));
			tile.worldObj = this.worldObj;
			tile.xCoord = this.xCoord;
			tile.yCoord = this.yCoord;
			tile.zCoord = this.zCoord;

			this.sendExtentionToClient(side);
		}
	}

	/** Sends the save data for the tileEntity too the client */
	public void sendExtentionToClient(int side)
	{
		if (worldObj != null && !worldObj.isRemote && this.subEntities[side] instanceof TileEntity)
		{
			NBTTagCompound tag = new NBTTagCompound();
			((TileEntity) this.subEntities[side]).writeToNBT(tag);
			if (tag != null && tag.hasKey("id"))
			{
				System.out.println("Sending TileEntity to Client");
				Packet packet = PacketManager.getPacket(FluidMech.CHANNEL, this, PacketID.EXTENTION_CREATE.ordinal(), ForgeDirection.getOrientation(side), tag);
				PacketManager.sendPacketToClients(packet, this.worldObj, new Vector3(this), 50);
			}
		}
	}

	public TileEntity getEntitySide(ForgeDirection side)
	{
		return (TileEntity) this.subEntities[side.ordinal() & 5];

	}

	/** gets the current color mark of the pipe */
	@Override
	public ColorCode getColor()
	{
		if (this.worldObj == null)
		{
			return ColorCode.NONE;
		}
		return ColorCode.get(worldObj.getBlockMetadata(xCoord, yCoord, zCoord));
	}

	/** sets the current color mark of the pipe */
	@Override
	public void setColor(Object cc)
	{
		ColorCode code = ColorCode.get(cc);
		if (!worldObj.isRemote && code != this.getColor())
		{
			this.worldObj.setBlockMetadataWithNotify(xCoord, yCoord, zCoord, code.ordinal(), 3);
		}
	}

	@Override
	public String getMeterReading(EntityPlayer user, ForgeDirection side, EnumTools tool)
	{
		/* DEBUG CODE ACTIVATERS */
		boolean testConnections = false;
		boolean testNetwork = true;
		boolean testSubs = false;

		/* NORMAL OUTPUT */
		String string = ((NetworkPipes) this.getTileNetwork()).pressureProduced + "p " + ((NetworkPipes) this.getTileNetwork()).getNetworkFluid() + " Extra";

		/* DEBUG CODE */
		if (testConnections)
		{
			for (int i = 0; i < 6; i++)
			{
				string += ":" + (this.renderConnection[i] ? "T" : "F") + (this.renderConnection[i] ? "T" : "F");
			}
		}
		if (testNetwork)
		{
			string += " " + this.getTileNetwork().toString();
		}
		if (testSubs)
		{
			string += " ";
			for (int i = 0; i < 6; i++)
			{
				if (this.subEntities[i] == null)
				{
					string += ":" + "Null";
				}
				else
				{
					string += ":" + this.subEntities[i].toString();
				}
			}
			string += " ";
		}

		return string;
	}

	@Override
	public int fill(ForgeDirection from, FluidStack resource, boolean doFill)
	{
		if (resource == null || !FluidRestrictionHandler.isValidLiquid(this.getColor(), resource.getFluid()))
		{
			return 0;
		}
		TileEntity tile = VectorHelper.getTileEntityFromSide(this.worldObj, new Vector3(this), from);
		return ((NetworkPipes) this.getTileNetwork()).addFluidToNetwork(tile, resource, doFill);
	}

	@Override
	public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain)
	{
		return null;
	}

	@Override
	public FluidTankInfo[] getTankInfo(ForgeDirection direction)
	{
		return new FluidTankInfo[] { new FluidTankInfo(this.getTank()) };
	}

	/** Checks to make sure the connection is valid to the tileEntity
	 * 
	 * @param tileEntity - the tileEntity being checked
	 * @param side - side the connection is too
	 * @return */
	public boolean validateConnectionSide(TileEntity tileEntity, ForgeDirection side)
	{
		if (!this.worldObj.isRemote && tileEntity != null)
		{
			if (this.subEntities[side.ordinal()] != null)
			{
				return false;
			}
			if (tileEntity instanceof ITileConnector)
			{
				if (((ITileConnector) tileEntity).canTileConnect(this, side))
				{
					if (tileEntity instanceof INetworkPipe)
					{
						if (((INetworkPipe) tileEntity).getColor() == this.getColor())
						{
							this.getTileNetwork().merge(((INetworkPipe) tileEntity).getTileNetwork(), this);
							return connectedBlocks.add(tileEntity);

						}
					}
					else
					{
						return connectedBlocks.add(tileEntity);
					}
				}
			}
			else if (tileEntity instanceof IColorCoded)
			{
				if (this.getColor() == ColorCode.NONE || this.getColor() == ((IColorCoded) tileEntity).getColor())
				{
					return connectedBlocks.add(tileEntity);
				}
			}
			else if (tileEntity instanceof IFluidHandler)
			{
				return connectedBlocks.add(tileEntity);
			}
		}
		return false;
	}

	@Override
	public void refresh()
	{

		if (this.worldObj != null && !this.worldObj.isRemote)
		{

			boolean[] previousConnections = this.renderConnection.clone();
			this.connectedBlocks.clear();

			for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS)
			{
				TileEntity ent = new Vector3(this).modifyPositionFromSide(dir).getTileEntity(this.worldObj);
				this.renderConnection[dir.ordinal()] = this.validateConnectionSide(ent, dir);

				if (this.renderConnection[dir.ordinal()] && ent instanceof IFluidHandler && !(ent instanceof INetworkPipe))
				{
					IFluidHandler tankContainer = (IFluidHandler) ent;
					this.getTileNetwork().addTile(ent, false);

					/* LITTLE TRICK TO AUTO DRAIN TANKS ON EACH CONNECTION UPDATE */

					FluidStack stack = tankContainer.drain(dir, FluidContainerRegistry.BUCKET_VOLUME, false);
					if (stack != null && stack.amount > 0)
					{
						int fill = ((NetworkPipes) this.getTileNetwork()).addFluidToNetwork((TileEntity) tankContainer, stack, true);
						tankContainer.drain(dir, fill, true);
					}
				}
			}

			/** Only send packet updates if visuallyConnected changed. */
			if (!Arrays.areEqual(previousConnections, this.renderConnection))
			{
				this.worldObj.markBlockForUpdate(this.xCoord, this.yCoord, this.zCoord);
			}
		}
	}

	@Override
	public boolean canTileConnect(TileEntity entity, ForgeDirection dir)
	{
		return entity != null && this.subEntities[dir.ordinal()] == null;
	}

	@Override
	public double getMaxPressure(ForgeDirection side)
	{
		return 350;
	}

	@Override
	public NetworkTileEntities getTileNetwork()
	{
		if (this.pipeNetwork == null)
		{
			this.setTileNetwork(new NetworkPipes(this.getColor(), this));
		}
		return this.pipeNetwork;
	}

	@Override
	public void setTileNetwork(NetworkTileEntities network)
	{
		if (network instanceof NetworkPipes)
		{
			this.pipeNetwork = (NetworkPipes) network;
		}
	}

	@Override
	public int getMaxFlowRate(Fluid stack, ForgeDirection side)
	{
		//TODO change this to get info from stack
		return 1000 * 3;
	}

	@Override
	public boolean onOverPressure(Boolean damageAllowed)
	{
		if (damageAllowed)
		{
			worldObj.setBlockMetadataWithNotify(xCoord, yCoord, yCoord, 0, 0);
			return true;
		}
		return false;
	}

	@SideOnly(Side.CLIENT)
	public AxisAlignedBB getRenderBoundingBox()
	{
		return AxisAlignedBB.getAABBPool().getAABB(this.xCoord, this.yCoord, this.zCoord, this.xCoord + 1, this.yCoord + 1, this.zCoord + 1);
	}

	@Override
	public List<TileEntity> getNetworkConnections()
	{
		return this.connectedBlocks;
	}

	@Override
	public FluidTank getTank()
	{
		if (this.fakeTank == null)
		{
			this.fakeTank = new FluidTank(FluidContainerRegistry.BUCKET_VOLUME);
		}
		return this.fakeTank;
	}

	@Override
	public void setTankContent(FluidStack stack)
	{		
		this.getTank().setFluid(stack);
	}

	@Override
	public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean canFill(ForgeDirection from, Fluid fluid)
	{
		return this.subEntities[from.ordinal()] == null;
	}

	@Override
	public boolean canDrain(ForgeDirection from, Fluid fluid)
	{
		// TODO Auto-generated method stub
		return false;
	}

}
