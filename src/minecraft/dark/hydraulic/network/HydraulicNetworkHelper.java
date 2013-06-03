package dark.hydraulic.network;

import dark.hydraulic.api.IDrain;
import dark.hydraulic.api.INetworkPart;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.liquids.ITankContainer;
import universalelectricity.core.vector.Vector3;
import universalelectricity.core.vector.VectorHelper;

public class HydraulicNetworkHelper
{

	/**
	 * Invalidates a TileEntity
	 */
	public static void invalidate(TileEntity tileEntity)
	{
		for (int i = 0; i < 6; i++)
		{
			ForgeDirection direction = ForgeDirection.getOrientation(i);
			TileEntity checkTile = VectorHelper.getConnectorFromSide(tileEntity.worldObj, new Vector3(tileEntity), direction);

			if (checkTile instanceof INetworkPart)
			{
				TileNetwork network = ((INetworkPart) checkTile).getTileNetwork();

				if (network != null && network instanceof FluidNetwork)
				{
					network.removeEntity(tileEntity);
					for (ITankContainer tank : ((FluidNetwork) network).connectedTanks)
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
