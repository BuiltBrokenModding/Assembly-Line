package hydraulic.network;

import hydraulic.api.INetworkPart;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import universalelectricity.core.path.Pathfinder;
import universalelectricity.core.vector.Vector3;
import cpw.mods.fml.common.FMLLog;

public class TileNetwork
{
	/* BLOCK THAT ACT AS FLUID CONVEYORS ** */
	public final List<INetworkPart> networkMember = new ArrayList<INetworkPart>();

	public TileNetwork(INetworkPart... parts)
	{
		this.networkMember.addAll(Arrays.asList(parts));
	}

	/**
	 * Adds a TileEntity to the network
	 * 
	 * @param ent - tileEntity instance
	 * @param member - add to network member list
	 * @return
	 */
	public boolean addEntity(TileEntity ent, boolean member)
	{
		if (ent == null || this.isPartOfNetwork(ent))
		{
			return false;
		}
		else if (ent instanceof INetworkPart && member)
		{
			return this.addNetworkPart((INetworkPart) ent);
		}
		return false;
	}

	public boolean isPartOfNetwork(TileEntity ent)
	{
		return this.networkMember.contains(ent);
	}

	/**
	 * Adds a new part to the network member list
	 */
	public boolean addNetworkPart(INetworkPart part)
	{
		if (!networkMember.contains(part) && this.isValidMember(part))
		{
			networkMember.add(part);
			part.setTileNetwork(this);
			this.cleanUpConductors();
			return true;
		}
		return false;
	}

	/**
	 * Removes a tileEntity from any of the valid lists
	 */
	public void removeEntity(TileEntity ent)
	{
		this.networkMember.remove(ent);
	}

	/**
	 * Cleans the list of networkMembers and remove those that no longer belong
	 */
	public void cleanUpConductors()
	{
		Iterator<INetworkPart> it = this.networkMember.iterator();

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

			}
		}

	}

	/**
	 * Is this part a valid member of the network
	 */
	public boolean isValidMember(INetworkPart part)
	{
		return part != null && part instanceof TileEntity && !((TileEntity) part).isInvalid();
	}

	/**
	 * Refreshes the network... mainly the network member list
	 */
	public void refresh()
	{
		this.cleanUpConductors();
		try
		{
			Iterator<INetworkPart> it = this.networkMember.iterator();

			while (it.hasNext())
			{
				INetworkPart conductor = it.next();
				conductor.updateNetworkConnections();
			}
		}
		catch (Exception e)
		{
			FMLLog.severe("TileNetwork>>>Refresh>>>Critical Error.");
			e.printStackTrace();
		}
	}

	/**
	 * Gets the list of network members
	 */
	public List<INetworkPart> getNetworkMemebers()
	{
		return this.networkMember;
	}

	/**
	 * Combines two networks together into one
	 * 
	 * @param network
	 * @param part
	 */
	public void merge(TileNetwork network, INetworkPart part)
	{
		if (network != null && network != this && network.getClass().equals(this.getClass()))
		{
			if (this.preMergeProcessing(network, part))
			{
				this.postMergeProcessing(network);
			}
		}
	}

	/**
	 * Processing that needs too be done before the network merges
	 * 
	 * @return false if the merge needs to be canceled
	 */
	public boolean preMergeProcessing(TileNetwork network, INetworkPart part)
	{
		return true;
	}

	/**
	 * Finalizing the merge of two networks by creating the new network and importing all network
	 * parts
	 */
	public void postMergeProcessing(TileNetwork network)
	{
		TileNetwork newNetwork = new TileNetwork();
		newNetwork.getNetworkMemebers().addAll(this.getNetworkMemebers());
		newNetwork.getNetworkMemebers().addAll(network.getNetworkMemebers());

		newNetwork.cleanUpConductors();
		// newNetwork.balanceColletiveTank(true);
	}

	/**
	 * Called when a peace of the network is remove from the network. Will split the network if it
	 * can no longer find a valid connection too all parts
	 */
	public void splitNetwork(World world, INetworkPart splitPoint)
	{
		if (splitPoint instanceof TileEntity)
		{
			this.getNetworkMemebers().remove(splitPoint);
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
								TileNetwork newNetwork = new TileNetwork();
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
							}
						}
					}
				}
			}
		}
	}

	@Override
	public String toString()
	{
		return "TileNetwork[" + this.hashCode() + "|parts:" + this.networkMember.size() + "]";
	}
}
