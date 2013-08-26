package dark.assembly.common.machine;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.ForgeDirection;
import universalelectricity.core.block.IElectrical;
import universalelectricity.core.vector.Vector3;
import dark.api.INetworkEnergyPart;
import dark.api.INetworkPart;
import dark.core.tile.network.NetworkSharedPower;
import dark.core.tile.network.NetworkTileEntities;

public class NetworkAssembly extends NetworkSharedPower
{
    public Set<TileEntity> powerSources = new HashSet<TileEntity>();
    public Set<TileEntity> powerLoads = new HashSet<TileEntity>();

    public NetworkAssembly(INetworkPart... parts)
    {
        super(parts);
    }

    @Override
    public NetworkTileEntities newInstance()
    {
        return new NetworkAssembly();
    }

    @Override
    public void cleanUpMembers()
    {
        Iterator<TileEntity> it = powerSources.iterator();
        for (int t = 0; t < 2; t++)
        {
            while (it.hasNext())
            {
                TileEntity te = it.next();
                if (te == null)
                {
                    it.remove();
                }
                if (te.isInvalid())
                {
                    it.remove();
                }
                if (!(te instanceof IElectrical))
                {
                    it.remove();
                }
                else
                {
                    Vector3 vec = new Vector3(te);
                    int b = 0;

                    for (int i = 0; i < 6; i++)
                    {
                        ForgeDirection dir = ForgeDirection.getOrientation(i);
                        TileEntity ent = vec.clone().modifyPositionFromSide(dir).getTileEntity(te.worldObj);
                        if (ent instanceof INetworkEnergyPart && ((INetworkEnergyPart) ent).getTileNetwork() != null)
                        {
                            if (((INetworkEnergyPart) ent).getTileNetwork() != this)
                            {
                                it.remove();
                            }
                            if (!((IElectrical) te).canConnect(dir.getOpposite()))
                            {
                                b++;
                            }
                            else if (t == 0 && ((IElectrical) te).getProvide(dir.getOpposite()) <= 0)
                            {
                                b++;
                            }
                            else if (t == 1 && ((IElectrical) te).getRequest(dir.getOpposite()) <= 0)
                            {
                                b++;
                            }
                        }
                    }
                    if (b >= 6)
                    {
                        it.remove();
                    }
                }

            }
            it = powerLoads.iterator();
        }

    }

    @Override
    public boolean isValidMember(INetworkPart part)
    {
        return super.isValidMember(part) && part instanceof TileEntityAssembly;
    }

    @Override
    public String toString()
    {
        return "AssemblyNetwork[" + this.hashCode() + "][parts:" + this.networkMember.size() + "]";
    }

}
