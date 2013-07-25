package dark.core.network.fluid;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.IFluidHandler;
import universalelectricity.core.vector.Vector3;
import dark.api.ColorCode;
import dark.api.INetworkPart;
import dark.api.fluid.AdvancedFluidEvent;
import dark.api.fluid.AdvancedFluidEvent.FluidMergeEvent;
import dark.api.fluid.INetworkFluidPart;
import dark.core.helpers.FluidHelper;
import dark.core.helpers.Pair;
import dark.core.tile.network.NetworkTileEntities;

public class NetworkFluidTiles extends NetworkTileEntities
{
	/** Fluid Tanks that are connected to the network but not part of it ** */
	public final List<IFluidHandler> connectedTanks = new ArrayList<IFluidHandler>();
	/** Collective storage of all fluid tiles */
	public FluidTank sharedTank = new FluidTank(FluidContainerRegistry.BUCKET_VOLUME);
	/** Map of results of two different liquids merging */
	public HashMap<Pair<Fluid, Fluid>, String> mergeResult = new HashMap<Pair<Fluid, Fluid>, String>();
	/** Color code of the network, mainly used for connection rules */
	public ColorCode color = ColorCode.NONE;
	/** Has the collective tank been loaded yet */
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

	/** Gets the collective tank of the network */
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
			if (fluid != null)
			{
				this.combinedStorage().setFluid(new FluidStack(fluid, volume));
			}
			else
			{
				this.combinedStorage().setFluid(null);
			}
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

	/** Merges two fluids together that don't result in damage to the network */
	public FluidStack mergeFluids(FluidStack stackOne, FluidStack stackTwo)
	{
		FluidStack stack = null;

		if (stackTwo != null && stackOne != null && stackOne.isFluidEqual(stackTwo))
		{
			stack = stackOne.copy();
			stack.amount += stackTwo.amount;
		}
		else if (stackOne == null && stackTwo != null)
		{
			stack = stackTwo.copy();
		}
		else if (stackOne != null && stackTwo == null)
		{
			stack = stackOne.copy();
		}
		else
		{
			//TODO do mixing of liquids and create a new waste liquid stack that is encoded with the volume of the two liquids before it
			//TODO check merge result first to allow for some liquids to merge in X way
		}
		return stack;
	}

	/** Checks if the liquid can be merged without damage */
	public String canMergeFluids(FluidStack stackOne, FluidStack stackTwo)
	{
		if (stackOne != null && stackTwo != null && !stackOne.equals(stackTwo))
		{
			if (this.mergeResult.containsKey(new Pair<Fluid, Fluid>(stackOne.getFluid(), stackTwo.getFluid())))
			{
				return this.mergeResult.get(new Pair<Fluid, Fluid>(stackOne.getFluid(), stackTwo.getFluid()));
			}
		}
		return null;
	}

	@Override
	public void init()
	{
		super.init();
		this.balanceColletiveTank(true);
	}

	@Override
	public boolean preMergeProcessing(NetworkTileEntities mergingNetwork, INetworkPart mergePoint)
	{
		if (mergingNetwork instanceof NetworkFluidTiles && ((NetworkFluidTiles) mergingNetwork).color == this.color)
		{
			NetworkFluidTiles network = (NetworkFluidTiles) mergingNetwork;

			this.balanceColletiveTank(true);
			network.balanceColletiveTank(true);
			String result = this.canMergeFluids(this.combinedStorage().getFluid(), network.combinedStorage().getFluid());
			if (result != null)
			{
				if (!mergePoint.mergeDamage(result) && mergePoint instanceof TileEntity)
				{
					AdvancedFluidEvent.fireEvent(new FluidMergeEvent(this.combinedStorage().getFluid(), network.combinedStorage().getFluid(), ((TileEntity) mergePoint).worldObj, new Vector3(((TileEntity) mergePoint))));
				}
				return false;
			}
			return true;
		}
		return false;
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
