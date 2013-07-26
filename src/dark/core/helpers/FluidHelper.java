package dark.core.helpers;

import net.minecraft.block.Block;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidBlock;
import universalelectricity.core.vector.Vector3;

public class FluidHelper
{

    /** Gets the block's fluid if it has one
     * 
     * @param world - world we are working in
     * @param vector - 3D location in world
     * @return @Fluid that the block is */
    public static Fluid getLiquidFromBlock(World world, Vector3 vector)
    {
        return FluidHelper.getFluidFromBlockID(vector.getBlockID(world));
    }

    /** Gets a fluid from blockID */
    public static Fluid getFluidFromBlockID(int id)
    {
        if (Block.blocksList[id] instanceof IFluidBlock)
        {
            return ((IFluidBlock) Block.blocksList[id]).getFluid();
        }
        else if (id == Block.waterStill.blockID || id == Block.waterMoving.blockID)
        {
            return FluidRegistry.getFluid("water");
        }
        else if (id == Block.lavaStill.blockID || id == Block.lavaMoving.blockID)
        {
            return FluidRegistry.getFluid("lava");
        }
        return null;
    }

    public static FluidStack getStack(FluidStack stack, int amount)
    {
        if (stack != null)
        {
            return new FluidStack(stack.getFluid(), amount);
        }
        return stack;
    }

    public static FluidStack drainBlock(World world, Vector3 vector, boolean doDrain)
    {
        Block block = Block.blocksList[vector.getBlockID(world)];
        if (block != null)
        {
            if (block instanceof IFluidBlock && ((IFluidBlock) block).canDrain(world, vector.intX(), vector.intY(), vector.intZ()))
            {
                return ((IFluidBlock) block).drain(world, vector.intX(), vector.intY(), vector.intZ(), doDrain);
            }
            else if (block.blockID == Block.waterStill.blockID && vector.getBlockMetadata(world) == 0)
            {
                if (doDrain)
                {
                    vector.setBlock(world, 0);
                }
                return new FluidStack(FluidRegistry.getFluid("water"), FluidContainerRegistry.BUCKET_VOLUME);
            }
            else if (block.blockID == Block.lavaStill.blockID && vector.getBlockMetadata(world) == 0)
            {
                if (doDrain)
                {
                    vector.setBlock(world, 0);
                }
                return new FluidStack(FluidRegistry.getFluid("lava"), FluidContainerRegistry.BUCKET_VOLUME);
            }
        }
        return null;
    }

    public static boolean isFillable(World world, Vector3 node)
    {
        Block block = Block.blocksList[node.getBlockID(world)];
        if ((block.blockID == Block.waterStill.blockID || block.blockID == Block.waterMoving.blockID) && node.getBlockMetadata(world) != 0)
        {
            return true;
        }
        else if ((block.blockID == Block.lavaStill.blockID || block.blockID == Block.lavaMoving.blockID) && node.getBlockMetadata(world) != 0)
        {
            return true;
        }
        else if (block instanceof IFluidBlock)
        {

        }
        return false;
    }
}
