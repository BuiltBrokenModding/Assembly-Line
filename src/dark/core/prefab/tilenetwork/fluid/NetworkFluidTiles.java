package dark.core.prefab.tilenetwork.fluid;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;
import dark.api.ColorCode;
import dark.api.fluid.INetworkFluidPart;
import dark.api.parts.INetworkPart;
import dark.core.prefab.helpers.FluidHelper;
import dark.core.prefab.tilenetwork.NetworkTileEntities;

public class NetworkFluidTiles extends NetworkTileEntities
{
    /** Fluid Tanks that are connected to the network but not part of the network's main body */
    public final Set<IFluidHandler> connectedTanks = new HashSet<IFluidHandler>();
    /** Collective storage tank of all fluid tile that make up this networks main body */
    protected FluidTank sharedTank;
    protected FluidTankInfo sharedTankInfo;

    /** Has the collective tank been loaded yet */
    protected boolean loadedLiquids = false;

    public NetworkFluidTiles(INetworkPart... parts)
    {
        super(parts);
    }

    @Override
    public NetworkTileEntities newInstance()
    {
        return new NetworkFluidTiles();
    }

    /** Gets the collective tank of the network */
    public FluidTank getNetworkTank()
    {
        if (this.sharedTank == null)
        {
            this.sharedTank = new FluidTank(FluidContainerRegistry.BUCKET_VOLUME);
            this.readDataFromTiles();
        }
        if (!loadedLiquids)
        {
            this.readDataFromTiles();
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

    public int fillNetworkTank(FluidStack stack, boolean doFill)
    {
        if (this.getNetworkTank() != null)
        {
            int p = this.getNetworkTank().getFluid() != null ? this.getNetworkTank().getFluid().amount : 0;
            int r = this.getNetworkTank().fill(stack, doFill);
            if (doFill)
            {
                if (p != r)
                {
                    this.sharedTankInfo = this.getNetworkTank().getInfo();
                    this.writeDataToTiles();
                }
            }
            return r;
        }
        return 0;
    }

    public FluidStack drainNetworkTank(int volume, boolean doDrain)
    {
        if (this.getNetworkTank() != null)
        {
            FluidStack p = this.getNetworkTank().getFluid();
            FluidStack r = this.getNetworkTank().drain(volume, doDrain);
            if (doDrain)
            {
                //Has the tank changed any. If yes then update all info and do a client update
                if (!p.isFluidEqual(r) && (p == null || r == null || p.amount != r.amount))
                {
                    this.sharedTankInfo = this.getNetworkTank().getInfo();
                    this.writeDataToTiles();
                    //TODO do a client update from the network rather than each pipe updating itself.
                }
            }
            return r;
        }
        return null;
    }

    @Override
    public void writeDataToTiles()
    {
        this.cleanUpMembers();
        if (this.getNetworkTank().getFluid() != null && this.networkMember.size() > 0)
        {
            FluidStack stack = this.getNetworkTank().getFluid() == null ? null : this.getNetworkTank().getFluid().copy();
            int membersFilled = 0;

            for (INetworkPart par : this.networkMember)
            {
                //UPDATE FILL VOLUME
                int fillVol = stack == null ? 0 : (stack.amount / (this.networkMember.size() - membersFilled));

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
    public void readDataFromTiles()
    {
        FluidStack stack = null;
        this.cleanUpMembers();
        for (INetworkPart par : this.networkMember)
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
    public void init()
    {
        super.init();
        this.readDataFromTiles();
    }

    @Override
    public boolean preMergeProcessing(NetworkTileEntities mergingNetwork, INetworkPart mergePoint)
    {
        if (mergingNetwork instanceof NetworkFluidTiles && ((NetworkFluidTiles) mergingNetwork).getClass() == this.getClass())
        {
            if (!((NetworkFluidTiles) mergingNetwork).loadedLiquids)
            {
                ((NetworkFluidTiles) mergingNetwork).readDataFromTiles();
            }
            if (!this.loadedLiquids)
            {
                this.readDataFromTiles();
            }
            return true;
        }
        return false;
    }

    @Override
    protected void mergeDo(NetworkTileEntities network)
    {
        NetworkFluidTiles newNetwork = (NetworkFluidTiles) this.newInstance();
        FluidStack one = this.getNetworkTank().getFluid();
        FluidStack two = ((NetworkFluidTiles) network).getNetworkTank().getFluid();

        this.getNetworkTank().setFluid(null);
        ((NetworkFluidTiles) network).getNetworkTank().setFluid(null);

        newNetwork.getNetworkMemebers().addAll(this.getNetworkMemebers());
        newNetwork.getNetworkMemebers().addAll(network.getNetworkMemebers());

        newNetwork.cleanUpMembers();
        newNetwork.getNetworkTank().setFluid(FluidCraftingHandler.mergeFluidStacks(one, two));
        newNetwork.sharedTankInfo = newNetwork.getNetworkTank().getInfo();
        newNetwork.writeDataToTiles();
    }

    @Override
    public void cleanUpMembers()
    {
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
        return "FluidNetwork[" + this.hashCode() + "|parts:" + this.networkMember.size() + "]";
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
