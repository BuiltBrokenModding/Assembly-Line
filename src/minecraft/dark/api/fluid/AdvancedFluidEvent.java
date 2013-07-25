package dark.api.fluid;

import universalelectricity.core.vector.Vector3;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.Event;
import net.minecraftforge.fluids.FluidEvent;
import net.minecraftforge.fluids.FluidStack;

public class AdvancedFluidEvent extends FluidEvent
{

	public AdvancedFluidEvent(FluidStack fluid, World world, Vector3 vec)
	{
		super(fluid, world, vec.intX(), vec.intY(), vec.intZ());
	}

	/** Mods should fire this event two different fluids try to merge */
	public static class FluidMergeEvent extends AdvancedFluidEvent
	{
		FluidStack mergeFluid;

		public FluidMergeEvent(FluidStack fluid, FluidStack merge, World world, Vector3 vec)
		{
			super(fluid, world, vec);
			this.mergeFluid = merge;
		}
	}

	public static final void fireEvent(AdvancedFluidEvent event)
	{
		MinecraftForge.EVENT_BUS.post(event);
	}

}
