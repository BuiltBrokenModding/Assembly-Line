package dark.core.prefab.tilenetwork.fluid;

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;
import dark.api.fluid.INetworkPipe;
import dark.api.parts.INetworkPart;
import dark.core.helpers.ConnectionHelper;
import dark.core.prefab.fluids.FluidHelper;
import dark.core.prefab.tilenetwork.NetworkUpdateHandler;

/** Extension on the fluid container network to provide a more advanced reaction to fluid passing
 * threw each pipe. As well this doubled as a pressure network for those machines that support the
 * use of pressure.
 *
 * @author Rseifert */
public class NetworkPipes extends NetworkFluidTiles
{
    private boolean processingRequest;
    public float pressureProduced;

    static
    {
        NetworkUpdateHandler.registerNetworkClass("FluidPipes", NetworkPipes.class);
    }

    public NetworkPipes()
    {
        super();
    }

    public NetworkPipes(INetworkPart... parts)
    {
        super(parts);
    }

    /** Adds FLuid to this network from one of the connected Pipes
     *
     * @param source - Were this liquid came from
     * @param stack - LiquidStack to be sent
     * @param doFill - actually fill the tank or just check numbers
     * @return the amount of liquid consumed from the init stack */
    public int addFluidToNetwork(TileEntity source, FluidStack stack, boolean doFill)
    {
        return this.addFluidToNetwork(source, stack, doFill, false);
    }

    /** Adds FLuid to this network from one of the connected Pipes
     *
     * @param source - Were this liquid came from
     * @param stack - LiquidStack to be sent
     * @param doFill - actually fill the tank or just check numbers
     * @param allowStore - allows the network to store this liquid in the pipes
     * @return the amount of liquid consumed from the init stack */
    public int addFluidToNetwork(TileEntity source, FluidStack sta, boolean doFill, boolean allowStore)
    {
        int used = 0;
        FluidStack prevCombined = this.getNetworkTank().getFluid();
        FluidStack stack = sta.copy();

        if (!this.processingRequest && stack != null)
        {
            this.processingRequest = true;

            if (this.getNetworkTank().getFluid() != null && !stack.isFluidEqual(this.getNetworkTank().getFluid()))
            {
                //this.causingMixing(null, this.combinedStorage().getFluid(), stack);
            }
            if (stack.amount > this.getMaxFlow(stack))
            {
                stack = FluidHelper.getStack(stack, this.getMaxFlow(stack));
            }

            /* Main fill target to try to fill with the stack */
            IFluidHandler primaryFill = null;
            int volume = Integer.MAX_VALUE;
            ForgeDirection fillDir = ForgeDirection.UNKNOWN;

            /* Secondary fill target if the main target is not found */
            IFluidHandler secondayFill = null;
            int mostFill = 0;
            ForgeDirection otherFillDir = ForgeDirection.UNKNOWN;

            boolean found = false;

            /* FIND THE FILL TARGET FROM THE LIST OF FLUID RECIEVERS */
            for (IFluidHandler tankContainer : connectedTanks)
            {
                if (tankContainer instanceof TileEntity && tankContainer != source && !(tankContainer instanceof INetworkPipe))
                {
                    TileEntity[] connectedTiles = ConnectionHelper.getSurroundingTileEntities((TileEntity) tankContainer);

                    for (int i = 0; i < 6; i++)
                    {
                        if (connectedTiles[i] instanceof INetworkPipe && ((INetworkPipe) connectedTiles[i]).getTileNetwork() == this)
                        {
                            ForgeDirection dir = ForgeDirection.getOrientation(i).getOpposite();
                            FluidTankInfo[] targetTank = tankContainer.getTankInfo(dir);
                            int fill = tankContainer.fill(dir, stack, false);

                            /* USE GET TANK FROM SIDE METHOD FIRST */
                            if (targetTank != null)
                            {
                                for (int t = 0; t < targetTank.length; t++)
                                {
                                    FluidStack stackStored = targetTank[t].fluid;
                                    int tankCap = targetTank[t].capacity;
                                    if (stackStored == null)
                                    {
                                        primaryFill = tankContainer;
                                        found = true;
                                        fillDir = dir;
                                        break;
                                    }
                                    else if (stackStored.isFluidEqual(sta) && stackStored.amount < tankCap && stackStored.amount < volume)
                                    {
                                        primaryFill = tankContainer;
                                        volume = stackStored.amount;
                                    }
                                }
                            }/* USE FILL METHOD IF GET TANK == NULL */
                            else if (fill > 0 && fill > mostFill)
                            {
                                secondayFill = tankContainer;
                                mostFill = fill;
                                otherFillDir = dir;
                            }
                        }
                    }
                }
                if (found)
                {
                    break;
                }
            }// End of tank finder

            boolean filledMain = false;
            if (primaryFill != null)
            {
                used = primaryFill.fill(fillDir, stack, doFill);
                // System.out.println("Primary Target " + used + doFill);
            }
            else if (secondayFill != null)
            {
                used = secondayFill.fill(fillDir, stack, doFill);
                // System.out.println("Seconday Target " + used + doFill);
            }
            else if (allowStore)
            {
                used = this.fillNetworkTank(source.worldObj, stack, doFill);
                // System.out.println("Network Target filled for " + used + doFill);
                filledMain = true;
            }

            /* IF THE COMBINED STORAGE OF THE PIPES HAS LIQUID MOVE IT FIRST */
            if (!filledMain && used > 0 && this.getNetworkTank().getFluid() != null && this.getNetworkTank().getFluid().amount > 0)
            {

                FluidStack drainStack = new FluidStack(0, 0);
                if (this.getNetworkTank().getFluid().amount >= used)
                {
                    drainStack = this.drainNetworkTank(source.worldObj, used, doFill);
                    used = 0;
                }
                else
                {
                    int pUsed = used;
                    used = Math.min(used, Math.max(used - this.getNetworkTank().getFluid().amount, 0));
                    drainStack = this.drainNetworkTank(source.worldObj, pUsed - used, doFill);
                }
                // System.out.println("Pulling " + (drainStack != null ? drainStack.amount : 0) +
                // " from combined leaving " + (this.combinedStorage.getLiquid() != null ?
                // this.combinedStorage.getLiquid().amount : 0));

            }
        }
        this.processingRequest = false;
        return used;
    }

    /** Gets the flow rate of the system using the lowest flow rate */
    public int getMaxFlow(FluidStack stack)
    {
        return 1000;
    }

    /** Updates after the pressure has changed a good bit */
    public void onPresureChange()
    {
        this.cleanUpMembers();
    }

}
