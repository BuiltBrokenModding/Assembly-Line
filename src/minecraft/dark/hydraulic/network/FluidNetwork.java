package dark.hydraulic.network;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.liquids.ILiquidTank;
import net.minecraftforge.liquids.ITankContainer;
import net.minecraftforge.liquids.LiquidContainerRegistry;
import net.minecraftforge.liquids.LiquidDictionary;
import net.minecraftforge.liquids.LiquidStack;
import net.minecraftforge.liquids.LiquidTank;
import universalelectricity.core.path.Pathfinder;
import universalelectricity.core.vector.Vector3;
import cpw.mods.fml.common.FMLLog;
import dark.hydraulic.api.ColorCode;
import dark.hydraulic.api.INetworkFluidPart;
import dark.hydraulic.api.INetworkPart;
import dark.hydraulic.helpers.FluidHelper;

public class FluidNetwork extends TileNetwork
{
	/* MACHINES THAT ARE FLUID BASED AND CONNECTED BUT NOT PART OF THE NETWORK ** */
	public final List<ITankContainer> connectedTanks = new ArrayList<ITankContainer>();

	/* COMBINED TEMP STORAGE FOR ALL PIPES IN THE NETWORK */
	public LiquidTank sharedTank = new LiquidTank(LiquidContainerRegistry.BUCKET_VOLUME);

	public ColorCode color = ColorCode.NONE;
	protected boolean loadedLiquids = false;

	public FluidNetwork(ColorCode color, INetworkPart... parts)
	{
		super(parts);
		this.color = color;
	}

	public LiquidTank combinedStorage()
	{
		if (this.sharedTank == null)
		{
			this.sharedTank = new LiquidTank(LiquidContainerRegistry.BUCKET_VOLUME);
			this.balanceColletiveTank(true);
		}
		return this.sharedTank;
	}

	/**
	 * Stores Fluid in this network's collective tank
	 */
	public int storeFluidInSystem(LiquidStack stack, boolean doFill)
	{
		if (stack == null || this.combinedStorage().containsValidLiquid() && (this.combinedStorage().getLiquid() != null && !this.combinedStorage().getLiquid().isLiquidEqual(stack)))
		{
			return 0;
		}
		if (!loadedLiquids)
		{
			this.balanceColletiveTank(true);
		}
		if (this.combinedStorage().getLiquid() == null || this.combinedStorage().getLiquid().amount < this.combinedStorage().getCapacity())
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

	/**
	 * Drains the network's collective tank
	 */
	public LiquidStack drainFluidFromSystem(int maxDrain, boolean doDrain)
	{
		if (!loadedLiquids)
		{
			this.balanceColletiveTank(true);
		}
		LiquidStack stack = this.combinedStorage().getLiquid();
		if (stack != null)
		{
			stack = this.combinedStorage().getLiquid().copy();
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

	/**
	 * Moves the volume stored in the network to the parts or sums up the volume from the parts and
	 * loads it to the network. Assumes that all liquidStacks stored are equal
	 * 
	 * @param sumParts - loads the volume from the parts before leveling out the volumes
	 */
	public void balanceColletiveTank(boolean sumParts)
	{
		int volume = 0, itemID = 0, itemMeta = 0;

		if (sumParts)
		{
			for (INetworkPart par : this.networkMember)
			{
				if (par instanceof INetworkFluidPart)
				{
					INetworkFluidPart part = ((INetworkFluidPart) par);
					if (part.getTank() != null && part.getTank().getLiquid() != null)
					{
						if (itemID == 0)
						{
							itemID = part.getTank().getLiquid().itemID;
							itemMeta = part.getTank().getLiquid().itemMeta;
						}
						volume += part.getTank().getLiquid().amount;
					}
				}
			}
			this.combinedStorage().setLiquid(new LiquidStack(itemID, volume, itemMeta));
			this.loadedLiquids = true;
		}

		if (this.combinedStorage().getLiquid() != null && this.networkMember.size() > 0)
		{
			volume = this.combinedStorage().getLiquid().amount / this.networkMember.size();
			itemID = this.combinedStorage().getLiquid().itemID;
			itemMeta = this.combinedStorage().getLiquid().itemMeta;

			for (INetworkPart par : this.networkMember)
			{
				if (par instanceof INetworkFluidPart)
				{
					INetworkFluidPart part = ((INetworkFluidPart) par);
					part.setTankContent(null);
					part.setTankContent(new LiquidStack(itemID, volume, itemMeta));
				}
			}
		}
	}

	@Override
	public void removeEntity(TileEntity ent)
	{
		super.removeEntity(ent);
		this.connectedTanks.remove(ent);
	}

	@Override
	public boolean addEntity(TileEntity ent, boolean member)
	{
		if (!(super.addEntity(ent, member)) && ent instanceof ITankContainer && !connectedTanks.contains(ent))
		{
			connectedTanks.add((ITankContainer) ent);
			return true;
		}
		return false;
	}

	/**
	 * Checks too see if the tileEntity is part of or connected too the network
	 */
	public boolean isConnected(TileEntity tileEntity)
	{
		return this.connectedTanks.contains(tileEntity);
	}

	public boolean isPartOfNetwork(TileEntity ent)
	{
		return super.isPartOfNetwork(ent) || this.connectedTanks.contains(ent);
	}

	public void causingMixing(INetworkPart Fluid, LiquidStack stack, LiquidStack stack2)
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
			/**
			 * Loop through the connected blocks and attempt to see if there are connections between
			 * the two points elsewhere.
			 */
			TileEntity[] connectedBlocks = splitPoint.getNetworkConnections();

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
							Pathfinder finder = new PathfinderCheckerPipes(world, (INetworkPart) connectedBlockB, splitPoint);
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
								PipeNetwork newNetwork = new PipeNetwork(this.color);
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

								newNetwork.cleanUpConductors();
								newNetwork.balanceColletiveTank(true);
							}
						}
					}
				}
			}
		}
	}

	@Override
	public boolean preMergeProcessing(TileNetwork net, INetworkPart part)
	{
		if (net instanceof FluidNetwork && ((FluidNetwork) net).color == this.color)
		{
			FluidNetwork network = (FluidNetwork) net;

			this.balanceColletiveTank(true);
			network.balanceColletiveTank(true);

			LiquidStack stack = new LiquidStack(0, 0, 0);

			if (this.combinedStorage().getLiquid() != null && network.combinedStorage().getLiquid() != null && this.combinedStorage().getLiquid().isLiquidEqual(network.combinedStorage().getLiquid()))
			{
				stack = this.combinedStorage().getLiquid();
				stack.amount += network.combinedStorage().getLiquid().amount;
			}
			else if (this.combinedStorage().getLiquid() == null && network.combinedStorage().getLiquid() != null)
			{
				stack = network.combinedStorage().getLiquid();
			}
			else if (this.combinedStorage().getLiquid() != null && network.combinedStorage().getLiquid() == null)
			{
				stack = this.combinedStorage().getLiquid();
			}
			return true;
		}
		return false;
	}

	@Override
	public void postMergeProcessing(TileNetwork network)
	{
		FluidNetwork newNetwork = new FluidNetwork(this.color);
		newNetwork.getNetworkMemebers().addAll(this.getNetworkMemebers());
		newNetwork.getNetworkMemebers().addAll(network.getNetworkMemebers());

		newNetwork.cleanUpConductors();
		newNetwork.balanceColletiveTank(true);
	}

	@Override
	public void cleanUpConductors()
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
		int cap = combinedStorage().getCapacity() / LiquidContainerRegistry.BUCKET_VOLUME;
		int vol = combinedStorage().getLiquid() != null ? (combinedStorage().getLiquid().amount / LiquidContainerRegistry.BUCKET_VOLUME) : 0;
		String name = LiquidDictionary.findLiquidName(this.combinedStorage().getLiquid()) != null ? LiquidDictionary.findLiquidName(this.combinedStorage().getLiquid()) : "Unkown";
		return String.format("%d/%d %S Stored", vol, cap, name);
	}
}
