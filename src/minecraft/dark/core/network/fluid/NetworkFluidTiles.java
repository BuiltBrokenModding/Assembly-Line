package dark.core.network.fluid;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidRegistry;
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
		int fluid = -1;
		NBTTagCompound tag = new NBTTagCompound();
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
						if (fluid == -1)
						{
							fluid = part.getTank().getFluid().fluidID;
							tag = part.getTank().getFluid().tag;
						}
						volume += part.getTank().getFluid().amount;
					}
				}
			}
			if (fluid != -1)
			{
				this.combinedStorage().setFluid(new FluidStack(fluid, volume, tag));
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
			fluid = this.combinedStorage().getFluid().fluidID;
			tag = this.combinedStorage().getFluid().tag;

			for (INetworkPart par : this.networkMember)
			{
				if (par instanceof INetworkFluidPart)
				{
					INetworkFluidPart part = ((INetworkFluidPart) par);
					part.setTankContent(null);
					part.setTankContent(new FluidStack(fluid, volume, tag));
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
		else if (stackTwo != null && stackOne != null && !stackOne.isFluidEqual(stackTwo))
		{
			Fluid waste = FluidRegistry.getFluid("waste");
			/* If the two liquids are not waste merge and set tag too liquids */
			if (stackTwo.fluidID != waste.getID() && stackOne.fluidID != waste.getID())
			{
				stack = new FluidStack(waste, stackTwo.amount + stackOne.amount);
				stack.tag = new NBTTagCompound();
				stack.tag.setCompoundTag("Liquid1", stackTwo.writeToNBT(new NBTTagCompound()));
				stack.tag.setCompoundTag("Liquid2", stackOne.writeToNBT(new NBTTagCompound()));
				stack.tag.setInteger("liquids", 2);
			}
			else if (stackTwo.fluidID == waste.getID() && stackOne.fluidID == waste.getID())
			{
				List<FluidStack> stacks = new ArrayList<FluidStack>();
				stacks.addAll(this.getStacksFromWaste(stackOne.copy()));
				stacks.addAll(this.getStacksFromWaste(stackTwo.copy()));
				stack = new FluidStack(waste, stackOne.amount + stackTwo.amount);
				stack.tag = new NBTTagCompound();
				stack.tag.setInteger("liquids", stacks.size());
				for (int i = 0; i < stacks.size(); i++)
				{
					stack.tag.setCompoundTag("Liquids" + 1 + i, stacks.get(i).writeToNBT(new NBTTagCompound()));
				}
			}
			else
			{
				FluidStack wasteStack = null, codeStack = null;
				if (stackOne.fluidID != waste.getID() && stackTwo.fluidID == waste.getID())
				{
					wasteStack = stackTwo.copy();
					codeStack = stackOne.copy();
				}
				else if (stackOne.fluidID == waste.getID() && stackTwo.fluidID != waste.getID())
				{
					wasteStack = stackOne.copy();
					codeStack = stackTwo.copy();
				}
				if (wasteStack != null)
				{
					wasteStack.tag.setCompoundTag("Liquid" + wasteStack.tag.getInteger("liquids") + 1, codeStack.writeToNBT(new NBTTagCompound()));
				}
			}
			//TODO do mixing of liquids and create a new waste liquid stack that is encoded with the volume of the two liquids before it
			//TODO check merge result first to allow for some liquids to merge in X way
		}
		return stack;
	}

	public List<FluidStack> getStacksFromWaste(FluidStack wasteStack)
	{
		List<FluidStack> stacks = new ArrayList<FluidStack>();
		if (wasteStack.fluidID == FluidRegistry.getFluidID("waste"))
		{
			for (int i = 1; i <= wasteStack.tag.getInteger("liquids"); i++)
			{
				FluidStack readStack = FluidStack.loadFluidStackFromNBT(wasteStack.tag.getCompoundTag("Liquid" + i));
				if (readStack != null)
				{
					stacks.add(readStack);
				}
			}
		}
		return stacks;
	}

	/** Checks if the liquid can be merged without damage */
	public String canMergeFluids(FluidStack stackOne, FluidStack stackTwo)
	{
		if (stackOne != null && stackTwo != null && !stackOne.equals(stackTwo))
		{
			if (this.mergeResult.containsKey(new Pair<Fluid, Fluid>(stackOne.getFluid(), stackTwo.getFluid())))
			{
				//TODO add volume calculation too see if merge can happen resulting in one liquid just vanishing
				//Case 100mb of fuel 10000mb of lava will result in fuel being comsumed with no explosion
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
