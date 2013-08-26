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
    public boolean addTile(TileEntity tileEntity, boolean member)
    {
        boolean higher = super.addTile(tileEntity, member);
        if (!higher && !member && tileEntity instanceof IElectrical)
        {
            Vector3 vec = new Vector3(tileEntity);
            for (int side = 0; side < 6; side++)
            {
                ForgeDirection dir = ForgeDirection.getOrientation(side);
                TileEntity ent = vec.clone().modifyPositionFromSide(dir).getTileEntity(tileEntity.worldObj);
                if (ent instanceof INetworkEnergyPart && ((INetworkEnergyPart) ent).getTileNetwork().equals(this))
                {
                    if (((IElectrical) tileEntity).canConnect(dir.getOpposite()))
                    {
                        if (!this.powerSources.contains(tileEntity) && ((IElectrical) tileEntity).getProvide(dir.getOpposite()) <= 0)
                        {
                            this.powerSources.add(tileEntity);
                            higher = true;
                        }
                        if (!this.powerLoads.contains(tileEntity) && ((IElectrical) tileEntity).getRequest(dir.getOpposite()) <= 0)
                        {
                            this.powerLoads.add(tileEntity);
                            higher = true;
                        }
                    }
                }
            }
        }
        return higher;
    }

    @Override
    public boolean isPartOfNetwork(TileEntity ent)
    {
        //TODO check how this is used since it might only want network parts and not connections
        return this.networkMember.contains(ent);
    }

    @Override
    public boolean removeTile(TileEntity ent)
    {
        return this.networkMember.remove(ent) || this.powerLoads.remove(ent) || this.powerSources.remove(ent);
    }

    @Override
    public void cleanUpMembers()
    {
        Iterator<TileEntity> it = powerSources.iterator();
        for (int set = 0; set < 2; set++)
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
                    int failedConnections = 0;

                    for (int side = 0; side < 6; side++)
                    {
                        ForgeDirection dir = ForgeDirection.getOrientation(side);
                        TileEntity ent = vec.clone().modifyPositionFromSide(dir).getTileEntity(te.worldObj);
                        if (ent instanceof INetworkEnergyPart && ((INetworkEnergyPart) ent).getTileNetwork() != null)
                        {
                            if (((INetworkEnergyPart) ent).getTileNetwork() != this)
                            {
                                it.remove();
                            }
                            if (!((IElectrical) te).canConnect(dir.getOpposite()))
                            {
                                failedConnections++;
                            }
                            else if (set == 0 && ((IElectrical) te).getProvide(dir.getOpposite()) <= 0)
                            {
                                failedConnections++;
                            }
                            else if (set == 1 && ((IElectrical) te).getRequest(dir.getOpposite()) <= 0)
                            {
                                failedConnections++;
                            }
                        }
                    }
                    if (failedConnections >= 6)
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
