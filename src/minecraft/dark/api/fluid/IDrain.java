package dark.api.fluid;

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidHandler;

/** Interface to make or use the TileEntityDrain. In real life a drain would do nothing but act as an
 * input or output for a pipe system. In code in order to interact with the pump the drain actual
 * has to do the filling/draining for the pump. The pump only need to find the drain and tell it to
 * fill or drain an area.
 *
 * The use of ITankContainer is optional but is need for the drain to be added to a Fluid Network */
public interface IDrain extends IFluidHandler
{
	/** In the drain you can use the ITankContainer.fill methods or use this to get the drain to
	 * place a liquid into the world
	 *
	 * @param stack - valid LiquidStack that has a Liquid Block for it
	 * @param doFill - actual do the action of filling or check if it can
	 * @return amount of liquid used */
	public int fillArea(FluidStack stack, boolean doFill);

	/** Requests that this drain give the pump this liquid. The pump will have to decide if it can
	 * accept, request, and maintain this demand
	 *
	 * @param pump - requesting pump
	 * @param stack - liquid this pump wants for this request */
	public void requestLiquid(TileEntity pump, FluidStack fluid, int amount);

	/** Request that this drain no longer supply the pump with a volume. By default a request will be
	 * removed from the request map after being filled. However, this can be used too stop a request
	 * short if the pump becomes full before the request is filled
	 *
	 * @param tileEntity - requesting pump */
	public void stopRequesting(TileEntity tileEntity);
}
