package dark.core.network.fluid;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.IFluidHandler;
import universalelectricity.core.path.Pathfinder;
import universalelectricity.core.vector.Vector3;
import dark.core.api.ColorCode;
import dark.core.api.INetworkPart;
import dark.core.hydraulic.helpers.FluidHelper;
import dark.core.tile.network.NetworkPathFinder;
import dark.core.tile.network.NetworkTileEntities;
import dark.fluid.api.INetworkFluidPart;
import dark.helpers.ConnectionHelper;

public class NetworkFluidTiles extends NetworkTileEntities
{
	/* MACHINES THAT ARE FLUID BASED AND CONNECTED BUT NOT PART OF THE NETWORK ** */
	public final List<IFluidHandler> connectedTanks = new ArrayList<IFluidHandler>();

	/* COMBINED TEMP STORAGE FOR ALL PIPES IN THE NETWORK */
	public FluidTank sharedTank = new FluidTank(FluidContainerRegistry.BUCKET_VOLUME);

	public ColorCode color = ColorCode.NONE;
	protected boolean loadedLiquids = false;

	public NetworkFluidTiles(ColorCode color, INetworkPart... parts)
	{
		super(parts);
		this.color = color;
	}

	@Override
	public NetworkTileEntities newInstance()
	{
		return new NetworkFluidTiles(this.color);
	}

	public FluidTank combinedStorage()
	{
		if (this.sharedTank == null)
		{
			this.sharedTank = new FluidTank(FluidContainerRegistry.BUCKET_VOLUME);
			this.balanceColletiveTank(true);
		}
		return this.sharedTank;
	}

	/** Stores Fluid in this network's collective tank */
	public int storeFluidInSystem(FluidStack stack, boolean doFill)
	{
		if (stack == null || this.combinedStorage() != null && (this.combinedStorage().getFluid() != null && !this.combinedStorage().getFluid().isFluidEqual(stack)))
		{
			return 0;
		}
		if (!loadedLiquids)
		{
			this.balanceColletiveTank(true);
		}
		if (this.combinedStorage().getFluid() == null || this.combinedStorage().getFluid().amount < this.combinedStorage().getCapacity())
		{
			int filled = this.combinedStorage().fill(stack, doFill);
			if (doFill)
			{
				this.balanceColletiveTank(false);
			}
			return filled;
		}
		return 0;
	}

	/** Drains the network's collective tank */
	public FluidStack drainFluidFromSystem(int maxDrain, boolean doDrain)
	{
		if (!loadedLiquids)
		{
			this.balanceColletiveTank(true);
		}
		FluidStack stack = this.combinedStorage().getFluid();
		if (stack != null)
		{
			stack = this.combinedStorage().getFluid().copy();
			if (maxDrain < stack.amount)
			{
				stack = FluidHelper.getStack(stack, maxDrain);
			}
			stack = this.combinedStorage().drain(maxDrain, doDrain);
			if (doDrain)
			{
				this.balanceColletiveTank(false);
			}
		}
		return stack;
	}

	public FluidStack drainFluidFromSystem(FluidStack stack, boolean doDrain)
	{
		if (stack != null && this.combinedStorage().getFluid() != null && stack.isFluidEqual(this.combinedStorage().getFluid()))
		{
			return this.drainFluidFromSystem(stack.amount, doDrain);
		}
		return null;
	}

	/** Moves the volume stored in the network to the parts or sums up the volume from the parts and
	 * loads it to the network. Assumes that all liquidStacks stored are equal
	 * 
	 * @param sumParts - loads the volume from the parts before leveling out the volumes */
	public void balanceColletiveTank(boolean sumParts)
	{
		Fluid fluid = null;
		int volume = 0;

		if (sumParts)
		{
			for (INetworkPart par : this.networkMember)
			{
				if (par instanceof INetworkFluidPart)
				{
					INetworkFluidPart part = ((INetworkFluidPart) par);
					if (part.getTank() != null && part.getTank().getFluid() != null)
					{
						if (fluid == null)
						{
							fluid = part.getTank().getFluid().getFluid();
						}
						volume += part.getTank().getFluid().amount;
					}
				}
			}
			this.combinedStorage().setFluid(new FluidStack(fluid, volume));
			this.loadedLiquids = true;
		}

		if (this.combinedStorage().getFluid() != null && this.networkMember.size() > 0)
		{
			volume = this.combinedStorage().getFluid().amount / this.networkMember.size();
			fluid = this.combinedStorage().getFluid().getFluid();

			for (INetworkPart par : this.networkMember)
			{
				if (par instanceof INetworkFluidPart)
				{
					INetworkFluidPart part = ((INetworkFluidPart) par);
					part.setTankContent(null);
					part.setTankContent(new FluidStack(fluid, volume));
				}
			}
		}
	}

	@Override
	public boolean removeTile(TileEntity ent)
	{
		return super.removeTile(ent) || this.connectedTanks.remove(ent);
	}

	@Override
	public boolean addTile(TileEntity ent, boolean member)
	{
		if (!(super.addTile(ent, member)) && ent instanceof IFluidHandler && !connectedTanks.contains(ent))
		{
			connectedTanks.add((IFluidHandler) ent);
			return true;
		}
		return false;
	}

	/** Checks too see if the tileEntity is part of or connected too the network */
	public boolean isConnected(TileEntity tileEntity)
	{
		return this.connectedTanks.contains(tileEntity);
	}

	public boolean isPartOfNetwork(TileEntity ent)
	{
		return super.isPartOfNetwork(ent) || this.connectedTanks.contains(ent);
	}

	public void causingMixing(INetworkPart Fluid, FluidStack stack, FluidStack stack2)
	{
		// TODO cause mixing of liquids based on types and volume. Also apply damage to pipes/parts
		// as needed
	}

	public void splitNetwork(World world, INetworkPart splitPoint)
	{
		if (splitPoint instanceof TileEntity)
		{
			this.getNetworkMemebers().remove(splitPoint);
			this.balanceColletiveTank(false);
			/** Loop through the connected blocks and attempt to see if there are connections between
			 * the two points elsewhere. */
			TileEntity[] connectedBlocks = ConnectionHelper.getSurroundingTileEntities((TileEntity) splitPoint);

			for (int i = 0; i < connectedBlocks.length; i++)
			{
				TileEntity connectedBlockA = connectedBlocks[i];

				if (connectedBlockA instanceof INetworkPart)
				{
					for (int pipeCount = 0; pipeCount < connectedBlocks.length; pipeCount++)
					{
						final TileEntity connectedBlockB = connectedBlocks[pipeCount];

						if (connectedBlockA != connectedBlockB && connectedBlockB instanceof INetworkPart)
						{
							Pathfinder finder = new NetworkPathFinder(world, (INetworkPart) connectedBlockB, splitPoint);
							finder.init(new Vector3(connectedBlockA));

							if (finder.results.size() > 0)
							{
								/* STILL CONNECTED SOMEWHERE ELSE */
								for (Vector3 node : finder.closedSet)
								{
									TileEntity entity = node.getTileEntity(world);
									if (entity instanceof INetworkPart)
									{
										if (node != splitPoint)
										{
											((INetworkPart) entity).setTileNetwork(this);
										}
									}
								}
							}
							else
							{
								/* NO LONGER CONNECTED ELSE WHERE SO SPLIT AND REFRESH */
								NetworkPipes newNetwork = new NetworkPipes(this.color);
								int parts = 0;
								for (Vector3 node : finder.closedSet)
								{
									TileEntity entity = node.getTileEntity(world);
									if (entity instanceof INetworkPart)
									{
										if (node != splitPoint)
										{
											newNetwork.getNetworkMemebers().add((INetworkPart) entity);
											parts++;
										}
									}
								}

								newNetwork.cleanUpMembers();
								newNetwork.balanceColletiveTank(true);
							}
						}
					}
				}
			}
		}
	}

	@Override
	public boolean preMergeProcessing(NetworkTileEntities net, INetworkPart part)
	{
		if (net instanceof NetworkFluidTiles && ((NetworkFluidTiles) net).color == this.color)
		{
			NetworkFluidTiles network = (NetworkFluidTiles) net;

			this.balanceColletiveTank(true);
			network.balanceColletiveTank(true);

			FluidStack stack = new FluidStack(0, 0);

			if (this.combinedStorage().getFluid() != null && network.combinedStorage().getFluid() != null && this.combinedStorage().getFluid().isFluidEqual(network.combinedStorage().getFluid()))
			{
				stack = this.combinedStorage().getFluid();
				stack.amount += network.combinedStorage().getFluid().amount;
			}
			else if (this.combinedStorage().getFluid() == null && network.combinedStorage().getFluid() != null)
			{
				stack = network.combinedStorage().getFluid();
			}
			else if (this.combinedStorage().getFluid() != null && network.combinedStorage().getFluid() == null)
			{
				stack = this.combinedStorage().getFluid();
			}
			return true;
		}
		return false;
	}

	@Override
	public void mergeDo(NetworkTileEntities network)
	{
		NetworkFluidTiles newNetwork = new NetworkFluidTiles(this.color);
		newNetwork.getNetworkMemebers().addAll(this.getNetworkMemebers());
		newNetwork.getNetworkMemebers().addAll(network.getNetworkMemebers());

		newNetwork.cleanUpMembers();
		newNetwork.balanceColletiveTank(true);
	}

	@Override
	public void cleanUpMembers()
	{
		if (!loadedLiquids)
		{
			this.balanceColletiveTank(true);
		}
		Iterator<INetworkPart> it = this.networkMember.iterator();
		int capacity = 0;
		while (it.hasNext())
		{
			INetworkPart part = it.next();
			if (!this.isValidMember(part))
			{
				it.remove();
			}
			else
			{
				part.setTileNetwork(this);
				if (part instanceof INetworkFluidPart)
				{
					capacity += ((INetworkFluidPart) part).getTank().getCapacity();
				}
			}
		}
		this.combinedStorage().setCapacity(capacity);
	}

	@Override
	public boolean isValidMember(INetworkPart part)
	{
		return super.isValidMember(part) && part instanceof INetworkFluidPart && ((INetworkFluidPart) part).getColor() == this.color;
	}

	@Override
	public String toString()
	{
		return "FluidNetwork[" + this.hashCode() + "|parts:" + this.networkMember.size() + "]";
	}

	public String getNetworkFluid()
	{
		if (combinedStorage() != null && combinedStorage().getFluid() != null && combinedStorage().getFluid().getFluid() != null)
		{
			int cap = combinedStorage().getCapacity() / FluidContainerRegistry.BUCKET_VOLUME;
			int vol = combinedStorage().getFluid() != null ? (combinedStorage().getFluid().amount / FluidContainerRegistry.BUCKET_VOLUME) : 0;
			String name = combinedStorage().getFluid().getFluid().getLocalizedName();
			return String.format("%d/%d %S Stored", vol, cap, name);
		}
		return ("As far as you can tell it is empty");
	}
}
