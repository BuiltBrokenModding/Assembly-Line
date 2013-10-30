package dark.core.prefab.tilenetwork.fluid;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;
import dark.api.ColorCode;
import dark.api.fluid.INetworkPipe;
import dark.api.parts.INetworkPart;
import dark.core.prefab.helpers.ConnectionHelper;
import dark.core.prefab.helpers.FluidHelper;
import dark.core.prefab.tilenetwork.NetworkTileEntities;

/** Side note: the network should act like this when done {@link http
 * ://www.e4training.com/hydraulic_calculators/B1.htm} as well as stay compatible with the forge
 * Liquids
 *
 * @author Rseifert */
public class NetworkPipes extends NetworkFluidTiles
{
    private boolean processingRequest;
    public float pressureProduced;

    public NetworkPipes(INetworkPart... parts)
    {
        super(parts);
    }

    @Override
    public NetworkTileEntities newInstance()
    {
        return new NetworkPipes();
    }

    @Override
    public boolean removeTile(TileEntity ent)
    {
        return super.removeTile(ent);
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
        FluidStack prevCombined = this.combinedStorage().getFluid();
        FluidStack stack = sta.copy();

        if (!this.processingRequest && stack != null && FluidHelper.isValidLiquid(color, stack.getFluid()))
        {
            this.processingRequest = true;

            if (this.combinedStorage().getFluid() != null && !stack.isFluidEqual(this.combinedStorage().getFluid()))
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
                used = this.storeFluidInSystem(stack, doFill);
                // System.out.println("Network Target filled for " + used + doFill);
                filledMain = true;
            }

            /* IF THE COMBINED STORAGE OF THE PIPES HAS LIQUID MOVE IT FIRST */
            if (!filledMain && used > 0 && this.combinedStorage().getFluid() != null && this.combinedStorage().getFluid().amount > 0)
            {

                FluidStack drainStack = new FluidStack(0, 0);
                if (this.combinedStorage().getFluid().amount >= used)
                {
                    drainStack = this.combinedStorage().drain(used, doFill);
                    used = 0;
                }
                else
                {
                    int pUsed = used;
                    used = Math.min(used, Math.max(used - this.combinedStorage().getFluid().amount, 0));
                    drainStack = this.combinedStorage().drain(pUsed - used, doFill);
                }
                // System.out.println("Pulling " + (drainStack != null ? drainStack.amount : 0) +
                // " from combined leaving " + (this.combinedStorage.getLiquid() != null ?
                // this.combinedStorage.getLiquid().amount : 0));

            }
            if (prevCombined != null && this.combinedStorage().getFluid() != null && prevCombined.amount != this.combinedStorage().getFluid().amount)
            {
                this.writeDataToTiles();
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
