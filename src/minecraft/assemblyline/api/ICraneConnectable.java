package assemblyline.api;

import net.minecraftforge.common.ForgeDirection;

public interface ICraneConnectable
{
	boolean canFrameConnectTo(ForgeDirection side);
}
