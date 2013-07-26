package dark.core.network.fluid;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fluids.FluidStack;
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
    public void balanceColletiveTank(boolean sumParts)
    {
        int volume = 0;
        int fluid = -1;
        NBTTagCompound tag = new NBTTagCompound();

        if (sumParts)
        {
            for (INetworkPart par : this.networkMember)
            {
                if (par instanceof INetworkFluidPart)
                {
                    INetworkFluidPart part = ((INetworkFluidPart) par);
                    if (part.getTank() != null && part.getTank().getFluid() != null)
                    {
                        FluidStack fluidStack = part.getTank().getFluid();
                        fluid = fluidStack.fluidID;
                        volume += fluidStack.amount;
                        if (fluidStack.tag != null && !fluidStack.tag.hasNoTags() && tag.hasNoTags())
                        {
                            tag = fluidStack.tag;
                        }
                    }
                }
            }
            if (fluid != -1)
            {
                this.combinedStorage().setFluid(new FluidStack(fluid, volume));
            }
            else
            {
                this.combinedStorage().setFluid(null);
            }
            this.loadedLiquids = true;
        }
        if (this.combinedStorage().getFluid() != null && this.getNetworkMemebers().size() > 0)
        {
            this.cleanUpMembers();

            int lowestY = 255;
            int highestY = 0;
            for (INetworkPart part : this.getNetworkMemebers())
            {
                if (part instanceof TileEntity && ((TileEntity) part).yCoord < lowestY)
                {
                    lowestY = ((TileEntity) part).yCoord;
                }
                if (part instanceof TileEntity && ((TileEntity) part).yCoord > highestY)
                {
                    highestY = ((TileEntity) part).yCoord;
                }
            }
            fluid = this.combinedStorage().getFluid().fluidID;
            volume = Math.abs(this.combinedStorage().getFluid().amount);
            tag = this.combinedStorage().getFluid().tag;
            //TODO change this to use hydraulics to not only place fluid at the lowest but as well not move it to another side if there is no path there threw fluid
            for (int y = lowestY; y <= highestY; y++)
            {
                List<INetworkFluidPart> parts = new ArrayList<INetworkFluidPart>();
                for (INetworkPart part : this.getNetworkMemebers())
                {
                    if (part instanceof INetworkFluidPart && ((TileEntity) part).yCoord == y)
                    {
                        parts.add((INetworkFluidPart) part);
                    }
                }
                int fillvolume = Math.abs(volume / parts.size());

                for (INetworkFluidPart part : parts)
                {
                    part.setTankContent(null);
                    int fill = Math.min(fillvolume, part.getTank().getCapacity());
                    part.setTankContent(new FluidStack(fluid, fill, tag));
                    volume -= fill;
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
