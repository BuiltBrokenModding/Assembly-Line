package assemblyline.common.machine.crane;

import net.minecraftforge.common.ForgeDirection;

public interface ICraneConnectable
{
	boolean canFrameConnectTo(ForgeDirection side);
}
