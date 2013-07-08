package dark.core.network.fluid;

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.fluids.IFluidHandler;
import universalelectricity.core.vector.Vector3;
import universalelectricity.core.vector.VectorHelper;
import dark.core.api.INetworkPart;
import dark.core.tile.network.NetworkTileEntities;
import dark.fluid.api.IDrain;

public class HydraulicNetworkHelper
{

	/** Invalidates a TileEntity */
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
					network.removeEntity(tileEntity);
					for (IFluidHandler tank : ((NetworkFluidTiles) network).connectedTanks)
					{
						if (tank instanceof IDrain)
						{
							((IDrain) tank).stopRequesting(tileEntity);
						}
					}
				}
			}
		}
	}
}
