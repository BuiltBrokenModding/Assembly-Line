package dark.core.network.fluid;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import universalelectricity.core.vector.Vector2;
import universalelectricity.core.vector.Vector3;

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidHandler;
import dark.api.fluid.INetworkFluidPart;
import dark.api.parts.INetworkPart;
import dark.core.interfaces.ColorCode;
import dark.core.prefab.helpers.FluidHelper;
import dark.core.prefab.tilenetwork.NetworkTileEntities;

/** Designed to be used by fluid containers with only one internal tank */
public class NetworkFluidContainers extends NetworkFluidTiles
{

    public NetworkFluidContainers(ColorCode color, INetworkPart... parts)
    {
        super(color, parts);
    }

    @Override
    public NetworkTileEntities newInstance()
    {
        return new NetworkFluidContainers(this.color);
    }

    @Override
    public void writeDataToTiles()
    {
        if (this.combinedStorage() == null || this.combinedStorage().getFluid() == null)
        {
            return;
        }
        FluidStack tankStack = this.combinedStorage().getFluid();
        int volume = tankStack.amount;

        int lowestY = 255;
        int highestY = 0;

        this.cleanUpMembers();

        if (this.combinedStorage().getFluid() != null && this.getNetworkMemebers().size() > 0)
        {
            for (INetworkPart part : this.getNetworkMemebers())
            {
                if (part instanceof IFluidHandler)
                {
                    ((INetworkFluidPart) part).drainTankContent(0, Integer.MAX_VALUE, true);
                }
                if (part instanceof TileEntity && ((TileEntity) part).yCoord < lowestY)
                {
                    lowestY = ((TileEntity) part).yCoord;
                }
                if (part instanceof TileEntity && ((TileEntity) part).yCoord > highestY)
                {
                    highestY = ((TileEntity) part).yCoord;
                }
            }

            //TODO change this to use hydraulics to not only place fluid at the lowest but as well not move it to another side if there is no path there threw fluid
            for (int y = lowestY; y <= highestY; y++)
            {
                /** List of parts for this Y level */
                List<INetworkFluidPart> parts = new ArrayList<INetworkFluidPart>();

                /* Grab all parts that share this Y level*/
                for (INetworkPart part : this.getNetworkMemebers())
                {
                    if (part instanceof INetworkFluidPart && ((TileEntity) part).yCoord == y)
                    {
                        parts.add((INetworkFluidPart) part);
                    }
                }
                if (!parts.isEmpty())
                {
                    /* Div out the volume for this level. TODO change this to use a percent system for even filling */
                    int fillVolume = Math.abs(volume / parts.size());

                    /* Fill all tanks on this level */
                    for (INetworkFluidPart part : parts)
                    {
                        int fill = Math.min(fillVolume, part.getTank(0).getCapacity());
                        volume -= part.fillTankContent(0, FluidHelper.getStack(tankStack, fillVolume), true);
                    }
                }

                if (volume <= 0)
                {
                    break;
                }
            }
        }

    }

    @Override
    public int storeFluidInSystem(FluidStack stack, boolean doFill)
    {
        int vol = this.combinedStorage().getFluid() != null ? this.combinedStorage().getFluid().amount : 0;
        int filled = super.storeFluidInSystem(stack, doFill);
        if (vol != filled)
        {
            for (INetworkPart part : this.getNetworkMemebers())
            {
                if (part instanceof TileEntity)
                {
                    TileEntity ent = ((TileEntity) part);
                    ent.worldObj.markBlockForUpdate(ent.xCoord, ent.yCoord, ent.zCoord);
                }
            }
        }
        return filled;
    }

    @Override
    public FluidStack drainFluidFromSystem(int maxDrain, boolean doDrain)
    {
        FluidStack vol = this.combinedStorage().getFluid();
        FluidStack stack = super.drainFluidFromSystem(maxDrain, doDrain);
        boolean flag = false;
        if (vol != null)
        {
            if (stack == null)
            {
                flag = true;
            }
            else if (stack.amount != vol.amount)
            {
                flag = true;
            }
        }
        if (flag)
        {
            for (INetworkPart part : this.getNetworkMemebers())
            {
                if (part instanceof TileEntity)
                {
                    TileEntity ent = ((TileEntity) part);
                    ent.worldObj.markBlockForUpdate(ent.xCoord, ent.yCoord, ent.zCoord);
                }
            }
        }
        return stack;
    }

    public void sortBlockList(final Set<INetworkPart> set)
    {
        try
        {
            List<INetworkPart> list = new ArrayList<INetworkPart>();
            Iterator<INetworkPart> it = list.iterator();
            while (it.hasNext())
            {
                if (!(it.next() instanceof TileEntity))
                {
                    it.remove();
                }
            }
            Collections.sort(list, new Comparator<INetworkPart>()
            {
                @Override
                public int compare(INetworkPart partA, INetworkPart partB)
                {
                    Vector3 vecA = new Vector3((TileEntity) partA);
                    Vector3 vecB = new Vector3((TileEntity) partB);
                    //Though unlikely always return zero for equal vectors
                    if (vecA.equals(vecB) || vecA.intY() == vecB.intY())
                    {
                        return 0;
                    }

                    return Integer.compare(vecA.intY(), vecB.intY());
                }
            });
            set.clear();
            set.addAll(list);
        }
        catch (Exception e)
        {
            System.out.println("FluidMech>>>BlockDrain>>FillArea>>Error>>CollectionSorter");
            e.printStackTrace();
        }
    }

}
