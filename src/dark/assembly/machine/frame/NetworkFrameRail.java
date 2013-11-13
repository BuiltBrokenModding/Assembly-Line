package dark.assembly.machine.frame;

import dark.api.parts.INetworkPart;
import dark.core.prefab.tilenetwork.NetworkTileEntities;

/** This is a sub network for the frames that handles only one rail of frames
 *
 * @author DarkGuardsman */
public class NetworkFrameRail extends NetworkTileEntities
{
    /** Animation rotation of the frame wheels */
    private float getRotation = 0;

    public NetworkFrameRail(INetworkPart... frames)
    {
        super(frames);
    }
}
