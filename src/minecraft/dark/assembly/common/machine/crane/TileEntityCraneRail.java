package dark.assembly.common.machine.crane;

import net.minecraftforge.common.ForgeDirection;
import universalelectricity.prefab.tile.TileEntityAdvanced;
import dark.assembly.api.ICraneStructure;

public class TileEntityCraneRail extends TileEntityAdvanced implements ICraneStructure
{

    @Override
    public boolean canFrameConnectTo(ForgeDirection side)
    {
        return true;
    }

}
