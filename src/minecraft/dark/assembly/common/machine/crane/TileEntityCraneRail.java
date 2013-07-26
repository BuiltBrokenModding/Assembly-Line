package dark.assembly.common.machine.crane;

import net.minecraftforge.common.ForgeDirection;
import dark.assembly.api.ICraneStructure;
import dark.assembly.common.machine.TileEntityAssembly;

public class TileEntityCraneRail extends TileEntityAssembly implements ICraneStructure
{

    @Override
    public boolean canFrameConnectTo(ForgeDirection side)
    {
        return true;
    }

    @Override
    public boolean canConnect(ForgeDirection direction)
    {
        return false;
    }

    @Override
    public void onUpdate()
    {
        // TODO Auto-generated method stub

    }

}
