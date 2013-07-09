package dark.core.hydraulic.helpers;

import universalelectricity.core.vector.Vector3;
import net.minecraft.block.Block;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidBlock;

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
		if (id == Block.waterStill.blockID || id == Block.waterMoving.blockID)
		{
			return FluidRegistry.getFluid("water");
		}
		else if (id == Block.lavaStill.blockID || id == Block.lavaMoving.blockID)
		{
			return FluidRegistry.getFluid("lava");
		}
		else if (Block.blocksList[id] instanceof IFluidBlock)
		{
			return ((IFluidBlock) Block.blocksList[id]).getFluid();
		}
		return null;
	}

	public static FluidStack getStack(FluidStack stack, int amount)
	{
		if(stack != null)
		{
			return new FluidStack(stack.getFluid(), amount);
		}
		return stack;
	}
}
