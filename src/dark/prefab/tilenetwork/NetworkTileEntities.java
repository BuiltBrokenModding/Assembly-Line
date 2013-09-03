package dark.prefab.tilenetwork;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import universalelectricity.core.path.Pathfinder;
import universalelectricity.core.vector.Vector3;
import universalelectricity.core.vector.VectorHelper;
import cpw.mods.fml.common.FMLLog;
import dark.interfaces.INetworkPart;
import dark.prefab.helpers.ConnectionHelper;

public abstract class NetworkTileEntities
{
    /* BLOCK THAT ACT AS FLUID CONVEYORS ** */
    public final List<INetworkPart> networkMember = new ArrayList<INetworkPart>();

    public NetworkTileEntities(INetworkPart... parts)
    {
        this.networkMember.addAll(Arrays.asList(parts));
    }

    /** Should be called after a network is created from a split or merge */
    public void init()
    {
        cleanUpMembers();
    }

    /** Creates a new instance of this network to be used to merge or split networks while still
     * maintaining each class that extends the base network class
     *
     * @return - new network instance using the current networks properties */
    public abstract NetworkTileEntities newInstance();

    /** Adds a TileEntity to the network. extends this to catch non-network parts and add them to
     * other tile lists
     *
     * @param tileEntity - tileEntity instance
     * @param member - add to network member list
     * @return */
    public boolean addTile(TileEntity tileEntity, boolean member)
    {
        if (tileEntity == null || this.isPartOfNetwork(tileEntity))
        {
            return false;
        }
        else if (tileEntity instanceof INetworkPart && member)
        {
            return this.addNetworkPart((INetworkPart) tileEntity);
        }
        return false;
    }

    /** Adds a new part to the network member list */
    public boolean addNetworkPart(INetworkPart part)
    {
        if (!networkMember.contains(part) && this.isValidMember(part))
        {
            networkMember.add(part);
            part.setTileNetwork(this);
            this.cleanUpMembers();
            return true;
        }
        return false;
    }

    public boolean isPartOfNetwork(TileEntity ent)
    {
        return this.networkMember.contains(ent);
    }

    /** removes a tile from all parts of the network */
    public boolean removeTile(TileEntity ent)
    {
        return this.networkMember.remove(ent);
    }

    /** Cleans the list of networkMembers and remove those that no longer belong */
    public void cleanUpMembers()
    {
        Iterator<INetworkPart> it = this.networkMember.iterator();

        while (it.hasNext())
        {
            INetworkPart part = it.next();
            if (!this.isValidMember(part))
            {
                it.remove();
            }
            else
            {
                part.setTileNetwork(this);
            }
        }

    }

    /** Is this part a valid member of the network */
    public boolean isValidMember(INetworkPart part)
    {
        return part != null && part instanceof TileEntity && !((TileEntity) part).isInvalid();
    }

    /** Refreshes the network... mainly the network member list */
    public void refresh()
    {
        this.cleanUpMembers();
        try
        {
            Iterator<INetworkPart> it = this.networkMember.iterator();

            while (it.hasNext())
            {
                INetworkPart conductor = it.next();
                conductor.refresh();
            }
        }
        catch (Exception e)
        {
            FMLLog.severe("TileNetwork>>>Refresh>>>Critical Error.");
            e.printStackTrace();
        }
    }

    /** Gets the list of network members */
    public List<INetworkPart> getNetworkMemebers()
    {
        return this.networkMember;
    }

    /** Override this to write any data to the tile. Called before a merge, split, or major edit of
     * the network */
    public void writeDataToTiles()
    {

    }

    /** Override this to read any data to the tile. Called after a merge, split, or major edit of the
     * network */
    public void readDataFromTiles()
    {

    }

    /** Combines two networks together into one. Calls to preMerge and doMerge instead of doing the
     * merge process itself
     *
     * @param network
     * @param mergePoint */
    public void merge(NetworkTileEntities network, INetworkPart mergePoint)
    {
        if (network != null && network != this && network.getClass().equals(this.getClass()))
        {
            if (this.preMergeProcessing(network, mergePoint))
            {
                this.mergeDo(network);
            }
        }
    }

    /** Processing that needs too be done before the network merges. Use this to do final network
     * merge calculations and to cause network merge failure
     *
     * @param network the network that is to merge with this one
     * @param part the part at which started the network merge. Use this to cause damage if two
     * networks merge with real world style failures
     *
     * @return false if the merge needs to be canceled.
     *
     * Cases in which the network should fail to merge are were the two networks merge with error.
     * Or, in the case of pipes the two networks merge and the merge point was destroyed by
     * combination of liquids.
     *
     * Ex Lava and water */
    public boolean preMergeProcessing(NetworkTileEntities network, INetworkPart part)
    {
        this.writeDataToTiles();
        return true;
    }

    /** Merges the two networks together */
    protected void mergeDo(NetworkTileEntities network)
    {
        NetworkTileEntities newNetwork = this.newInstance();
        newNetwork.getNetworkMemebers().addAll(this.getNetworkMemebers());
        newNetwork.getNetworkMemebers().addAll(network.getNetworkMemebers());
        newNetwork.readDataFromTiles();
        newNetwork.init();
    }

    /** Called when a peace of the network is remove from the network. Will split the network if it
     * can no longer find a valid connection too all parts */
    public void splitNetwork(World world, INetworkPart splitPoint)
    {
        if (splitPoint instanceof TileEntity)
        {
            this.getNetworkMemebers().remove(splitPoint);
            /** Loop through the connected blocks and attempt to see if there are connections between
             * the two points elsewhere. */
            TileEntity[] connectedBlocks = ConnectionHelper.getSurroundingTileEntities((TileEntity) splitPoint);

            for (int i = 0; i < connectedBlocks.length; i++)
            {
                TileEntity connectedBlockA = connectedBlocks[i];

                if (connectedBlockA instanceof INetworkPart)
                {
                    for (int pipeCount = 0; pipeCount < connectedBlocks.length; pipeCount++)
                    {
                        final TileEntity connectedBlockB = connectedBlocks[pipeCount];

                        if (connectedBlockA != connectedBlockB && connectedBlockB instanceof INetworkPart)
                        {
                            Pathfinder finder = new NetworkPathFinder(world, (INetworkPart) connectedBlockB, splitPoint);
                            finder.init(new Vector3(connectedBlockA));

                            if (finder.results.size() > 0)
                            {
                                /* STILL CONNECTED SOMEWHERE ELSE */
                                for (Vector3 node : finder.closedSet)
                                {
                                    TileEntity entity = node.getTileEntity(world);
                                    if (entity instanceof INetworkPart)
                                    {
                                        if (node != splitPoint)
                                        {
                                            ((INetworkPart) entity).setTileNetwork(this);
                                        }
                                    }
                                }
                            }
                            else
                            {
                                /* NO LONGER CONNECTED ELSE WHERE SO SPLIT AND REFRESH */
                                NetworkTileEntities newNetwork = this.newInstance();
                                int parts = 0;
                                for (Vector3 node : finder.closedSet)
                                {
                                    TileEntity entity = node.getTileEntity(world);
                                    if (entity instanceof INetworkPart)
                                    {
                                        if (node != splitPoint)
                                        {
                                            newNetwork.getNetworkMemebers().add((INetworkPart) entity);
                                            parts++;
                                        }
                                    }
                                }
                                newNetwork.init();
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public String toString()
    {
        return "TileNetwork[" + this.hashCode() + "| Parts:" + this.networkMember.size() + "]";
    }

    /** invalidates/remove a tile from the networks that surround and connect to it
     *
     * @param tileEntity - tile */
    public static void invalidate(TileEntity tileEntity)
    {
        for (ForgeDirection direction : ForgeDirection.VALID_DIRECTIONS)
        {
            TileEntity checkTile = VectorHelper.getConnectorFromSide(tileEntity.worldObj, new Vector3(tileEntity), direction);

            if (checkTile instanceof INetworkPart && ((INetworkPart) checkTile).getTileNetwork() != null)
            {
                ((INetworkPart) checkTile).getTileNetwork().removeTile(tileEntity);
            }
        }
    }
}
