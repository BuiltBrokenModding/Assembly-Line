package hydraulic.network;

import hydraulic.api.ColorCode;
import hydraulic.api.INetworkFluidPart;
import hydraulic.api.INetworkPart;

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

public class FluidNetwork extends TileNetwork
{
	/* MACHINES THAT USE THE FORGE LIQUID API TO RECEIVE LIQUID ** */
	public final List<ITankContainer> fluidTanks = new ArrayList<ITankContainer>();

	/* COMBINED TEMP STORAGE FOR ALL PIPES IN THE NETWORK */
	public LiquidTank combinedStorage = new LiquidTank(LiquidContainerRegistry.BUCKET_VOLUME);

	public ColorCode color = ColorCode.NONE;

	public FluidNetwork(ColorCode color, INetworkPart... parts)
	{
		super(parts);
		this.color = color;
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
			this.combinedStorage.setLiquid(new LiquidStack(itemID, volume, itemMeta));
		}

		if (this.combinedStorage.getLiquid() != null && this.networkMember.size() > 0)
		{
			volume = this.combinedStorage.getLiquid().amount / this.networkMember.size();
			itemID = this.combinedStorage.getLiquid().itemID;
			itemMeta = this.combinedStorage.getLiquid().itemMeta;

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
		this.fluidTanks.remove(ent);
	}

	@Override
	public boolean addEntity(TileEntity ent, boolean member)
	{
		if (!(super.addEntity(ent, member)) && ent instanceof ITankContainer && !fluidTanks.contains(ent))
		{
			fluidTanks.add((ITankContainer) ent);
			return true;
		}
		return false;
	}

	/**
	 * Checks too see if the tileEntity is part of or connected too the network
	 */
	public boolean isConnected(TileEntity tileEntity)
	{
		return this.fluidTanks.contains(tileEntity);
	}
	
	public boolean isPartOfNetwork(TileEntity ent)
	{		
		return super.isPartOfNetwork(ent) || this.fluidTanks.contains(ent);
	}

	public void causingMixing(LiquidStack stack, LiquidStack stack2)
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
	public boolean preMergeProcessing(TileNetwork net)
	{
		if (net instanceof FluidNetwork && ((FluidNetwork)net).color == this.color)
		{
			FluidNetwork network = (FluidNetwork) net;
			if (this.combinedStorage.getLiquid() != null && network.combinedStorage.getLiquid() != null && !this.combinedStorage.getLiquid().isLiquidEqual(network.combinedStorage.getLiquid()))
			{
				this.causingMixing(this.combinedStorage.getLiquid(), network.combinedStorage.getLiquid());
			}
			else
			{
				this.balanceColletiveTank(false);
				network.balanceColletiveTank(false);

				LiquidStack stack = new LiquidStack(0, 0, 0);

				if (this.combinedStorage.getLiquid() != null && network.combinedStorage.getLiquid() != null && this.combinedStorage.getLiquid().isLiquidEqual(network.combinedStorage.getLiquid()))
				{
					stack = this.combinedStorage.getLiquid();
					stack.amount += network.combinedStorage.getLiquid().amount;
				}
				else if (this.combinedStorage.getLiquid() == null && network.combinedStorage.getLiquid() != null)
				{
					stack = network.combinedStorage.getLiquid();
				}
				else if (this.combinedStorage.getLiquid() != null && network.combinedStorage.getLiquid() == null)
				{
					stack = this.combinedStorage.getLiquid();
				}

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

	public void refresh()
	{
		this.cleanUpConductors();
		int capacity = 0;

		try
		{
			Iterator<INetworkPart> it = this.networkMember.iterator();

			while (it.hasNext())
			{
				INetworkPart conductor = it.next();
				conductor.updateNetworkConnections();
				capacity += LiquidContainerRegistry.BUCKET_VOLUME;
			}
			this.combinedStorage.setCapacity(capacity);
		}
		catch (Exception e)
		{
			FMLLog.severe("FluidNetwork>>>Refresh>>>Critical Error.");
			e.printStackTrace();
		}
	}

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
		if (!combinedStorage.containsValidLiquid())
		{
			return "Zero";
		}
		return String.format("%d/%d %S Stored", combinedStorage.getLiquid().amount / LiquidContainerRegistry.BUCKET_VOLUME, combinedStorage.getCapacity() / LiquidContainerRegistry.BUCKET_VOLUME, LiquidDictionary.findLiquidName(this.combinedStorage.getLiquid()));
	}

	public ILiquidTank getNetworkTank()
	{
		if (this.combinedStorage == null)
		{
			this.combinedStorage = new LiquidTank(0);
		}
		return this.combinedStorage;
	}
}
