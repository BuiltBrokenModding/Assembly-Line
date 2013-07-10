package dark.fluid.common.prefab;

import java.util.Random;

import universalelectricity.prefab.tile.TileEntityAdvanced;
import dark.core.api.ITileConnector;
import dark.core.api.IToolReadOut;
import dark.core.network.fluid.HydraulicNetworkHelper;

public abstract class TileEntityFluidDevice extends TileEntityAdvanced implements IToolReadOut, ITileConnector
{
	public Random random = new Random();

	@Override
	public void invalidate()
	{
		super.invalidate();
		HydraulicNetworkHelper.invalidate(this);
	}
}
