package dark.assembly.common.machine.crane;

import universalelectricity.prefab.tile.TileEntityAdvanced;
import net.minecraftforge.common.ForgeDirection;
import dark.assembly.api.ICraneStructure;
import dark.assembly.common.machine.TileEntityAssembly;

public class TileEntityCraneRail extends TileEntityAdvanced implements ICraneStructure
{

    @Override
    public boolean canFrameConnectTo(ForgeDirection side)
    {
        return true;
    }

}
