package dark.core.network.fluid;

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.ForgeDirection;
import universalelectricity.core.vector.Vector3;
import universalelectricity.core.vector.VectorHelper;
import dark.api.parts.INetworkPart;
import dark.core.prefab.tilenetwork.NetworkTileEntities;

public class FluidNetworkHelper
{

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

}
