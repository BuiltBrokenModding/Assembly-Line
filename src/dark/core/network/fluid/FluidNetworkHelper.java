package dark.core.network.fluid;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import universalelectricity.core.vector.Vector3;
import universalelectricity.core.vector.VectorHelper;
import dark.api.parts.INetworkPart;
import dark.core.prefab.helpers.Pair;
import dark.core.prefab.tilenetwork.NetworkTileEntities;

public class FluidNetworkHelper
{
    /** Map of results of two different liquids merging */
    public static HashMap<Pair<Fluid, Fluid>, Object> fluidMergeResults = new HashMap<Pair<Fluid, Fluid>, Object>();

    static
    {
        fluidMergeResults.put(new Pair<Fluid, Fluid>(FluidRegistry.WATER, FluidRegistry.LAVA), Block.obsidian);
        fluidMergeResults.put(new Pair<Fluid, Fluid>(FluidRegistry.LAVA, FluidRegistry.WATER), Block.cobblestone);
    }

    /** Invalidates a TileEntity that is part of a fluid network */
    public static void invalidate(TileEntity tileEntity)
    {
        for (int i = 0; i < 6; i++)
        {
            ForgeDirection direction = ForgeDirection.getOrientation(i);
            TileEntity checkTile = VectorHelper.getConnectorFromSide(tileEntity.worldObj, new Vector3(tileEntity), direction);

            if (checkTile instanceof INetworkPart)
            {
                NetworkTileEntities network = ((INetworkPart) checkTile).getTileNetwork();

                if (network != null && network instanceof NetworkFluidTiles)
                {
                    network.removeTile(tileEntity);
                }
            }
        }
    }

    /** Merges two fluids together that don't result in damage to the network */
    public static FluidStack mergeFluidStacks(FluidStack stackOne, FluidStack stackTwo)
    {
        FluidStack resultStack = null;

        if (stackTwo != null && stackOne != null && stackOne.isFluidEqual(stackTwo))
        {
            resultStack = stackOne.copy();
            resultStack.amount += stackTwo.amount;
        }
        else if (stackOne == null && stackTwo != null)
        {
            resultStack = stackTwo.copy();
        }
        else if (stackOne != null && stackTwo == null)
        {
            resultStack = stackOne.copy();
        }
        else if (stackTwo != null && stackOne != null && !stackOne.isFluidEqual(stackTwo))
        {
            /* Try to merge fluids by mod defined rules first */
            if (fluidMergeResults.containsKey(new Pair<Fluid, Fluid>(stackOne.getFluid(), stackTwo.getFluid())))
            {
                Object result = fluidMergeResults.get(new Pair<Fluid, Fluid>(stackOne.getFluid(), stackTwo.getFluid()));
                if (result instanceof Fluid)
                {
                    resultStack = new FluidStack(((Fluid) result).getID(), stackOne.amount + stackTwo.amount);
                }
                else if (result instanceof FluidStack)
                {
                    resultStack = ((FluidStack) result).copy();
                    resultStack.amount = stackOne.amount + stackTwo.amount;
                }
                else if (result instanceof String && ((String) result).startsWith("Liquid:"))
                {
                    resultStack = new FluidStack(FluidRegistry.getFluid(((String) result).replace("Liquid:", "")), stackOne.amount + stackTwo.amount);
                }
            }
            if (resultStack == null)
            {
                Fluid waste = FluidRegistry.getFluid("waste");
                if (waste == null)
                {
                    System.out.println("[FluidNetworkHelper] Attempted to merge two fluids into a waste fluid stack but Forge fluid registry return null for waste. Possible that waste fluid was disabled or not registered correctly.");
                    return null;
                }
                /* If both liquids are waste then copy fluidStack lists then merge */
                if (stackTwo.fluidID == waste.getID() && stackOne.fluidID == waste.getID())
                {
                    List<FluidStack> stacks = new ArrayList<FluidStack>();
                    stacks.addAll(getStacksFromWaste(stackOne.copy()));
                    stacks.addAll(getStacksFromWaste(stackTwo.copy()));
                    resultStack = createNewWasteStack(stacks.toArray(new FluidStack[stacks.size()]));
                }
                else
                {
                    resultStack = createNewWasteStack(stackOne.copy(), stackTwo.copy());
                }
            }
        }
        return resultStack;
    }

    /** Gets the fluidStacks that make up a waste FluidStack */
    public static List<FluidStack> getStacksFromWaste(FluidStack wasteStack)
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

    /** Creates a new waste stack from the listed fluidStacks */
    public static FluidStack createNewWasteStack(FluidStack... liquids)
    {
        FluidStack stack = new FluidStack(FluidRegistry.getFluid("waste"), 0);
        stack.tag = new NBTTagCompound();
        if (liquids != null)
        {
            int count = 0;
            for (int i = 0; i < liquids.length; i++)
            {
                if (liquids[i] != null)
                {
                    if (!liquids[i].getFluid().equals(stack.getFluid()))
                    {
                        count++;
                        stack.tag.setCompoundTag("Liquids" + count, liquids[i].writeToNBT(new NBTTagCompound()));
                        stack.amount += liquids[i].amount;
                    }
                    else
                    {
                        for (FluidStack loadStack : getStacksFromWaste(liquids[i]))
                        {
                            count++;
                            stack.tag.setCompoundTag("Liquids" + count, loadStack.writeToNBT(new NBTTagCompound()));
                            stack.amount += loadStack.amount;
                        }
                    }
                }
            }
            stack.tag.setInteger("liquids", count);
        }
        return stack;
    }

    /** Gets the result of the merge of the two fluids, order of merge does matter and will produce
     * diffrent results.
     *
     * @param stackOne - Receiving fluid, eg the one that is not moving
     * @param stackTwo - Flowing fluid, eg the one moving into the first fluid
     * @return Object result of the merge, can be anything from string, ItemStack, Item, Block, or
     * enum action */
    public static Object getMergeResult(FluidStack stackOne, FluidStack stackTwo)
    {
        if (stackOne != null && stackTwo != null && !stackOne.equals(stackTwo))
        {
            if (fluidMergeResults.containsKey(new Pair<Fluid, Fluid>(stackOne.getFluid(), stackTwo.getFluid())))
            {
                //TODO add volume calculation too see if merge can happen resulting in one liquid just vanishing
                //Case 100mb of fuel 10000mb of lava will result in fuel being consumed with no major results
                return fluidMergeResults.get(new Pair<Fluid, Fluid>(stackOne.getFluid(), stackTwo.getFluid()));
            }
        }
        return null;
    }

}
