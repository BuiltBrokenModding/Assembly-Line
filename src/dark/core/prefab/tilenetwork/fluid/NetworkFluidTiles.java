package dark.core.prefab.tilenetwork.fluid;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;
import dark.api.fluid.INetworkFluidPart;
import dark.api.tilenetwork.INetworkPart;
import dark.api.tilenetwork.ITileNetwork;
import dark.core.prefab.fluids.FluidHelper;
import dark.core.prefab.tilenetwork.NetworkTileEntities;
import dark.core.prefab.tilenetwork.NetworkUpdateHandler;

public class NetworkFluidTiles extends NetworkTileEntities
{
    /** Fluid Tanks that are connected to the network but not part of the network's main body */
    public final Set<IFluidHandler> connectedTanks = new HashSet<IFluidHandler>();
    /** Collective storage tank of all fluid tile that make up this networks main body */
    protected FluidTank sharedTank;
    protected FluidTankInfo sharedTankInfo;

    /** Has the collective tank been loaded yet */
    protected boolean loadedLiquids = false;

    static
    {
        NetworkUpdateHandler.registerNetworkClass("FluidTiles", NetworkFluidTiles.class);
    }

    public NetworkFluidTiles()
    {
        super();
    }

    public NetworkFluidTiles(INetworkPart... parts)
    {
        super(parts);
    }

    /** Gets the collective tank of the network */
    public FluidTank getNetworkTank()
    {
        if (this.sharedTank == null)
        {
            this.sharedTank = new FluidTank(FluidContainerRegistry.BUCKET_VOLUME);
            this.getNetworkTankInfo();
        }
        return this.sharedTank;
    }

    public FluidTankInfo getNetworkTankInfo()
    {
        if (this.sharedTankInfo == null)
        {
            this.sharedTankInfo = this.getNetworkTank().getInfo();
        }
        return this.sharedTankInfo;
    }

    public int fillNetworkTank(World world, FluidStack stack, boolean doFill)
    {
        if (!this.loadedLiquids)
        {
            this.load();
            this.loadedLiquids = true;
        }
        if (!world.isRemote && this.getNetworkTank() != null && stack != null)
        {
            int p = this.getNetworkTank().getFluidAmount();
            int r = this.getNetworkTank().fill(stack, doFill);
            //System.out.println((world.isRemote ? "Client" : "Server") + " Network Fill: B:" + p + "  A:" + this.getNetworkTank().getFluidAmount());
            if (doFill)
            {
                if (p != this.getNetworkTank().getFluidAmount())
                {
                    this.sharedTankInfo = this.getNetworkTank().getInfo();
                    this.save();
                }
            }
            return r;
        }
        return 0;
    }

    public FluidStack drainNetworkTank(World world, int volume, boolean doDrain)
    {

        if (!this.loadedLiquids)
        {
            this.load();
            this.loadedLiquids = true;
        }
        FluidStack before = this.getNetworkTank().getFluid();
        if (!world.isRemote && this.getNetworkTank() != null && before != null)
        {
            FluidStack drain = this.getNetworkTank().drain(volume, doDrain);
            FluidStack after = this.getNetworkTank().getFluid();
            // System.out.println((doDrain ? "" : "Fake") + " Network Drain for " + volume + " B:" + (before != null ? before.amount : 0) + "  A:" + (after != null ? after.amount : 0) + "  D:" + (drain != null ? drain.amount : 0));
            if (doDrain)
            {
                //Has the tank changed any. If yes then update all info and do a client update
                if (!before.isFluidEqual(after) || (before != null && after != null && before.amount != after.amount))
                {
                    this.sharedTankInfo = this.getNetworkTank().getInfo();
                    this.save();
                    /*TODO do a client update from the network rather than each pipe updating itself.
                     *This will save on packet size but will increase the CPU load of the client since the client
                     *will need to do network calculations */
                }
            }
            return drain;
        }
        return null;
    }

    @Override
    public void save()
    {
        this.cleanUpMembers();
        if (this.getNetworkTank().getFluid() != null && this.getMembers().size() > 0)
        {
            FluidStack stack = this.getNetworkTank().getFluid() == null ? null : this.getNetworkTank().getFluid().copy();
            int membersFilled = 0;

            for (INetworkPart par : this.getMembers())
            {
                //UPDATE FILL VOLUME
                int fillVol = stack == null ? 0 : (stack.amount / (this.getMembers().size() - membersFilled));

                if (par instanceof INetworkFluidPart)
                {
                    //EMPTY TANK
                    ((INetworkFluidPart) par).drainTankContent(0, Integer.MAX_VALUE, true);
                    //FILL TANK
                    if (stack != null)
                    {
                        stack.amount -= ((INetworkFluidPart) par).fillTankContent(0, FluidHelper.getStack(stack, fillVol), true);
                        membersFilled++;
                    }
                }
            }
        }

    }

    @Override
    public void load()
    {
        FluidStack stack = null;
        this.cleanUpMembers();
        for (INetworkPart par : this.getMembers())
        {
            if (par instanceof INetworkFluidPart)
            {
                if (((INetworkFluidPart) par).getTankInfo()[0] != null && ((INetworkFluidPart) par).getTankInfo()[0].fluid != null)
                {
                    if (stack == null)
                    {
                        stack = ((INetworkFluidPart) par).getTankInfo()[0].fluid.copy();
                    }
                    else
                    {
                        stack.amount += ((INetworkFluidPart) par).getTankInfo()[0].fluid.amount;
                    }
                }
            }
        }
        if (stack != null && stack.amount > 0)
        {
            this.getNetworkTank().setFluid(stack);
        }
        else
        {
            this.getNetworkTank().setFluid(null);
        }
        this.loadedLiquids = true;
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

    @Override
    public boolean preMergeProcessing(ITileNetwork mergingNetwork, INetworkPart mergePoint)
    {
        if (mergingNetwork instanceof NetworkFluidTiles)
        {
            if (!((NetworkFluidTiles) mergingNetwork).loadedLiquids)
            {
                ((NetworkFluidTiles) mergingNetwork).load();
            }
            if (!this.loadedLiquids)
            {
                this.load();
            }
        }
        return super.preMergeProcessing(mergingNetwork, mergePoint);

    }

    @Override
    protected void mergeDo(ITileNetwork network)
    {
        ITileNetwork newNetwork = NetworkUpdateHandler.createNewNetwork(NetworkUpdateHandler.getID(this.getClass()));
        if (newNetwork != null)
        {
            if (newNetwork instanceof NetworkFluidTiles)
            {
                FluidStack one = this.getNetworkTank().getFluid();
                FluidStack two = ((NetworkFluidTiles) network).getNetworkTank().getFluid();

                this.getNetworkTank().setFluid(null);
                ((NetworkFluidTiles) network).getNetworkTank().setFluid(null);

                ((NetworkFluidTiles) newNetwork).getNetworkTank().setFluid(FluidCraftingHandler.mergeFluidStacks(one, two));
                ((NetworkFluidTiles) newNetwork).sharedTankInfo = ((NetworkFluidTiles) newNetwork).getNetworkTank().getInfo();
            }
            newNetwork.getMembers().addAll(this.getMembers());
            newNetwork.getMembers().addAll(network.getMembers());
            newNetwork.onCreated();
        }
        else
        {
            System.out.println("[NetworkFluidTiles] Failed to merge network due to the new network returned null");
        }
    }

    @Override
    public void cleanUpMembers()
    {
        Iterator<INetworkPart> it = this.getMembers().iterator();
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
                if (part instanceof INetworkFluidPart && ((INetworkFluidPart) part).getTankInfo()[0] != null)
                {
                    capacity += ((INetworkFluidPart) part).getTankInfo()[0].capacity;
                }
            }
        }
        this.getNetworkTank().setCapacity(capacity);
        this.sharedTankInfo = this.getNetworkTank().getInfo();
    }

    @Override
    public boolean isValidMember(INetworkPart part)
    {
        return super.isValidMember(part) && part instanceof INetworkFluidPart;
    }

    @Override
    public String toString()
    {
        return "FluidNetwork[" + this.hashCode() + "|parts:" + this.getMembers().size() + "]";
    }

    public String getNetworkFluid()
    {
        if (this.getNetworkTank() != null && this.getNetworkTank().getFluid() != null && this.getNetworkTank().getFluid().getFluid() != null)
        {
            int cap = this.getNetworkTank().getCapacity() / FluidContainerRegistry.BUCKET_VOLUME;
            int vol = this.getNetworkTank().getFluid() != null ? (this.getNetworkTank().getFluid().amount / FluidContainerRegistry.BUCKET_VOLUME) : 0;
            String name = this.getNetworkTank().getFluid().getFluid().getLocalizedName();
            return String.format("%d/%d %S Stored", vol, cap, name);
        }
        return ("Empty");
    }
}
