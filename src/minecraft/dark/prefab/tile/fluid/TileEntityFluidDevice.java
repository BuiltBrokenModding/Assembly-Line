package dark.prefab.tile.fluid;

import java.util.Random;

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.liquids.ITankContainer;
import net.minecraftforge.liquids.LiquidStack;
import universalelectricity.prefab.tile.TileEntityAdvanced;
import dark.core.api.network.ITileConnector;
import dark.core.api.tools.IReadOut;
import dark.core.hydraulic.network.fluid.HydraulicNetworkHelper;

public abstract class TileEntityFluidDevice extends TileEntityAdvanced implements IReadOut, ITileConnector
{
	public Random random = new Random();

	@Override
	public void invalidate()
	{
		super.invalidate();
		HydraulicNetworkHelper.invalidate(this);
	}

	/**
	 * Fills an ITankContainer in the direction
	 * 
	 * @param stack - LiquidStack that will be inputed in the tile
	 * @param side - direction to fill in
	 * @return the ammount filled
	 */
	public int fillSide(LiquidStack stack, ForgeDirection side, boolean doFill)
	{
		TileEntity tileEntity = worldObj.getBlockTileEntity(xCoord + side.offsetX, yCoord + side.offsetY, zCoord + side.offsetZ);

		if (stack != null && stack.amount > 0 && tileEntity instanceof ITankContainer)
		{
			return ((ITankContainer) tileEntity).fill(side.getOpposite(), stack, doFill);
		}
		return 0;
	}
}
