package dark.core.network.fluid;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidHandler;
import dark.api.ColorCode;
import dark.api.INetworkPart;
import dark.api.fluid.INetworkFluidPart;
import dark.core.tile.network.NetworkTileEntities;

/** Side note: the network should act like this when done {@link http
 * ://www.e4training.com/hydraulic_calculators/B1.htm} as well as stay compatible with the forge
 * Liquids
 *
 * @author Rseifert */
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
        if(this.combinedStorage() == null || this.combinedStorage().getFluid() == null)
        {
            return;
        }
        int fluid = this.combinedStorage().getFluid().fluidID;
        int volume = Math.abs(this.combinedStorage().getFluid().amount);
        NBTTagCompound tag = this.combinedStorage().getFluid().tag;

        int lowestY = 255;
        int highestY = 0;

        this.cleanUpMembers();

        if (this.combinedStorage().getFluid() != null && this.getNetworkMemebers().size() > 0)
        {
            for (INetworkPart part : this.getNetworkMemebers())
            {
                if (part instanceof IFluidHandler)
                {
                    ((INetworkFluidPart) part).setTankContent(null);
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
                    int fillvolume = Math.abs(volume / parts.size());

                    /* Fill all tanks on this level */
                    for (INetworkFluidPart part : parts)
                    {
                        int fill = Math.min(fillvolume, part.getTank().getCapacity());
                        part.setTankContent(new FluidStack(fluid, fill, tag));
                        volume -= fill;
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

}
