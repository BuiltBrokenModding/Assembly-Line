package dark.core.prefab.tilenetwork.fluid;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.IFluidHandler;
import universalelectricity.core.vector.Vector3;
import dark.api.fluid.AdvancedFluidEvent;
import dark.api.fluid.AdvancedFluidEvent.FluidMergeEvent;
import dark.api.fluid.INetworkFluidPart;
import dark.api.parts.INetworkPart;
import dark.core.interfaces.ColorCode;
import dark.core.prefab.helpers.FluidHelper;
import dark.core.prefab.helpers.Pair;
import dark.core.prefab.tilenetwork.NetworkTileEntities;
import dark.fluid.common.machines.TileEntityTank;
import dark.fluid.common.pipes.TileEntityPipe;

public class NetworkFluidTiles extends NetworkTileEntities
{
    /** Fluid Tanks that are connected to the network but not part of it ** */
    public final List<IFluidHandler> connectedTanks = new ArrayList<IFluidHandler>();
    /** Collective storage of all fluid tiles */
    public FluidTank sharedTank = new FluidTank(FluidContainerRegistry.BUCKET_VOLUME);

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
            this.readDataFromTiles();
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
            this.readDataFromTiles();
        }
        if (this.combinedStorage().getFluid() == null || this.combinedStorage().getFluid().amount < this.combinedStorage().getCapacity())
        {
            int filled = this.combinedStorage().fill(stack, doFill);
            if (doFill)
            {
                this.writeDataToTiles();
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
            this.readDataFromTiles();
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
                this.writeDataToTiles();
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

    @Override
    public void writeDataToTiles()
    {
        super.writeDataToTiles();
        if (this.combinedStorage().getFluid() != null && this.networkMember.size() > 0)
        {
            FluidStack stack = this.combinedStorage().getFluid().copy();
            int membersFilled = 0;

            for (INetworkPart par : this.networkMember)
            {
                //UPDATE FILL VOLUME
                int fillVol = stack.amount / (this.networkMember.size() - membersFilled);

                if (par instanceof INetworkFluidPart)
                {
                    //EMPTY TANK
                    ((INetworkFluidPart) par).drainTankContent(0, fillVol, true);

                    //FILL TANK
                    stack.amount -= ((INetworkFluidPart) par).fillTankContent(0, FluidHelper.getStack(stack, fillVol), true);
                    membersFilled++;
                }
            }
        }
    }

    @Override
    public void readDataFromTiles()
    {
        super.readDataFromTiles();

        FluidStack stack = null;

        for (INetworkPart par : this.networkMember)
        {
            if (par instanceof INetworkFluidPart)
            {
                if (((INetworkFluidPart) par).getTank(0) != null && ((INetworkFluidPart) par).getTank(0).getFluid() != null)
                {
                    if (stack == null)
                    {
                        stack = ((INetworkFluidPart) par).getTank(0).getFluid();
                    }
                    else
                    {
                        stack.amount += ((INetworkFluidPart) par).getTank(0).getFluid().amount;
                    }
                }
            }
        }
        if (stack != null && stack.amount > 0)
        {
            this.combinedStorage().setFluid(stack);
        }
        else
        {
            this.combinedStorage().setFluid(null);
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
        if (mergingNetwork instanceof NetworkFluidTiles && ((NetworkFluidTiles) mergingNetwork).color == this.color)
        {
            NetworkFluidTiles network = (NetworkFluidTiles) mergingNetwork;

            this.readDataFromTiles();
            network.readDataFromTiles();
            Object result = FluidCraftingHandler.getMergeResult(this.combinedStorage().getFluid(), network.combinedStorage().getFluid());
            if (mergePoint instanceof TileEntity)
            {
                World world = ((TileEntity) mergePoint).worldObj;
                int x = ((TileEntity) mergePoint).xCoord;
                int y = ((TileEntity) mergePoint).xCoord;
                int z = ((TileEntity) mergePoint).xCoord;
                try
                {
                    if (result != null)
                    {
                        if (result instanceof Block)
                        {
                            if (mergePoint instanceof TileEntityPipe)
                            {
                                //TODO in-case pipe in the block
                            }
                            else if (mergePoint instanceof TileEntityTank)
                            {
                                //TODO in-case tank in the block
                                //for tank set the render Entity to the block at full size
                            }
                            else
                            {
                                world.setBlock(x, y, z, 0);
                                world.setBlock(x, y, z, ((Block) result).blockID);
                            }
                        }
                        else if (result instanceof ItemStack)
                        {
                            world.setBlock(x, y, z, 0);

                            if (((ItemStack) result).itemID >= Block.blocksList.length)
                            {
                                EntityItem item = new EntityItem(world, x, y, z, (ItemStack) result);
                                //TODO add some effect to this
                                world.spawnEntityInWorld(item);
                            }
                            else
                            {
                                world.setBlock(x, y, z, ((ItemStack) result).itemID, ((ItemStack) result).getItemDamage(), 3);
                            }
                        }
                        else if (result instanceof String)
                        {

                            String string = (String) result;
                            if (string.startsWith("explosion:"))
                            {
                                int size = Integer.parseInt(string.replace("explosion:", ""));
                                world.setBlock(x, y, z, 0);
                                world.createExplosion(null, x, y, z, size, false);

                            }

                        }
                        AdvancedFluidEvent.fireEvent(new FluidMergeEvent(this.combinedStorage().getFluid(), network.combinedStorage().getFluid(), world, new Vector3(x, y, z)));
                        return false;
                    }
                }
                catch (Exception e)
                {

                }

            }
            return true;
        }
        return false;
    }

    @Override
    protected void mergeDo(NetworkTileEntities network)
    {
        NetworkFluidTiles newNetwork = (NetworkFluidTiles) this.newInstance();
        FluidStack one = this.combinedStorage().getFluid();
        FluidStack two = ((NetworkFluidTiles) network).combinedStorage().getFluid();

        this.combinedStorage().setFluid(null);
        ((NetworkFluidTiles) network).combinedStorage().setFluid(null);

        newNetwork.getNetworkMemebers().addAll(this.getNetworkMemebers());
        newNetwork.getNetworkMemebers().addAll(network.getNetworkMemebers());

        newNetwork.cleanUpMembers();
        newNetwork.combinedStorage().setFluid(FluidCraftingHandler.mergeFluidStacks(one, two));
        newNetwork.writeDataToTiles();
    }

    @Override
    public void cleanUpMembers()
    {
        if (!loadedLiquids)
        {
            this.readDataFromTiles();
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
                    capacity += ((INetworkFluidPart) part).getTank(0).getCapacity();
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
