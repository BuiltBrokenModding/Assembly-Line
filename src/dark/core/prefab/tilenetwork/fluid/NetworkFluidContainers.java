package dark.core.prefab.tilenetwork.fluid;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidHandler;
import dark.api.ColorCode;
import dark.api.fluid.INetworkFluidPart;
import dark.api.parts.INetworkPart;
import dark.core.prefab.helpers.FluidHelper;
import dark.core.prefab.tilenetwork.NetworkTileEntities;

/** Side note: the network should act like this when done {@link http
 * ://www.e4training.com/hydraulic_calculators/B1.htm} as well as stay compatible with the forge
 * Liquids
 *
 * @author Rseifert */
public class NetworkFluidContainers extends NetworkFluidTiles
{

    public NetworkFluidContainers(INetworkPart... parts)
    {
        super(parts);
    }

    @Override
    public NetworkTileEntities newInstance()
    {
        return new NetworkFluidContainers();
    }

    @Override
    public void writeDataToTiles()
    {
        this.cleanUpMembers();

        if (this.combinedStorage() == null || this.combinedStorage().getFluid() == null)
        {
            super.writeDataToTiles();
            return;
        }
        FluidStack fillStack = this.combinedStorage().getFluid().copy();

        int lowestY = 255, highestY = 0;

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
                    int fillvolume = Math.abs(fillStack.amount / parts.size());

                    /* Fill all tanks on this level */
                    for (INetworkFluidPart part : parts)
                    {
                        part.drainTankContent(0, Integer.MAX_VALUE, true);
                        fillStack.amount -= part.fillTankContent(0, FluidHelper.getStack(fillStack, fillvolume), true);
                    }
                }

                if (fillStack == null || fillStack.amount <= 0)
                {
                    break;
                }
            }

            for (INetworkPart part : this.getNetworkMemebers())
            {
                if (part instanceof TileEntity)
                {
                    ((TileEntity) part).worldObj.markBlockForUpdate(((TileEntity) part).xCoord, ((TileEntity) part).yCoord, ((TileEntity) part).zCoord);
                }
            }
        }

    }
}
