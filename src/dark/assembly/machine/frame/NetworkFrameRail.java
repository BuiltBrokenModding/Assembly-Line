package dark.assembly.machine.frame;

import com.dark.prefab.tile.network.NetworkTileEntities;
import com.dark.tile.network.INetworkPart;


/** This is a sub network for the frames that handles only one rail of frames
 * 
 * @author DarkGuardsman */
public class NetworkFrameRail extends NetworkTileEntities
{
    /** Animation rotation of the frame wheels */
    public float rotation = 0;

    public NetworkFrameRail(INetworkPart... frames)
    {
        super(frames);
    }
}
