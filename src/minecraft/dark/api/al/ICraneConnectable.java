package dark.api.al;

import net.minecraftforge.common.ForgeDirection;

public interface ICraneConnectable
{
    boolean canFrameConnectTo(ForgeDirection side);
}
