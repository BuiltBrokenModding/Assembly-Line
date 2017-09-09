package com.builtbroken.assemblyline.content.belt;

import com.builtbroken.assemblyline.AssemblyLine;
import com.builtbroken.mc.api.tile.IRotatable;
import com.builtbroken.mc.api.tile.access.IRotation;
import com.builtbroken.mc.api.tile.node.ITileNodeHost;
import com.builtbroken.mc.client.json.IJsonRenderStateProvider;
import com.builtbroken.mc.codegen.annotations.TileWrapped;
import com.builtbroken.mc.framework.block.imp.IBoundListener;
import com.builtbroken.mc.framework.block.imp.IChangeListener;
import com.builtbroken.mc.framework.block.imp.IWrenchListener;
import com.builtbroken.mc.framework.logic.TileNode;
import com.builtbroken.mc.imp.transform.vector.Pos;
import com.builtbroken.mc.seven.framework.block.BlockBase;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChatComponentText;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Very simple entity driven belt
 * Created by DarkGuardsman on 9/6/2017.
 */
@TileWrapped(className = ".gen.TileEntityWrappedSimpleBelt")
public class TileSimpleBelt extends TileNode implements IRotation, IRotatable, IBoundListener, IJsonRenderStateProvider, IChangeListener, IWrenchListener
{
    //Settings
    public static float beltSpeed = 0.05f;

    //Animation data
    public static int FRAME_FLAT;
    public static int FRAME_SLANTED;

    //Cached state
    private ForgeDirection _cachedDirection;
    protected BeltState beltState = BeltState.FLAT;
    protected ConnectionState connectionState = ConnectionState.BASE;

    /** Entity ignore list */
    public List<Entity> ignoreList = new ArrayList();
    /** Items detected last tick, can be used to get what is on the belt */
    public List<EntityItem> items = new ArrayList();

    //Cached data based on state
    protected Pos _forceToApply;
    protected AxisAlignedBB searchBounds;
    protected AxisAlignedBB collisionBounds;

    //Triggers
    private boolean updateConnections = false;
    private boolean syncToClient = false;

    public TileSimpleBelt()
    {
        super("belt.simple", AssemblyLine.DOMAIN);
    }

    @Override
    public void firstTick()
    {
        updateConnections();
    }

    @Override
    public void onBlockChanged()
    {
        updateConnections = true;
    }

    /**
     * Updates connection state of belts
     */
    protected void updateConnections()
    {
        final ForgeDirection rotation = getDirection();
        final Pos pos = new Pos(this);
        boolean front = false;
        boolean back = false;

        if (beltState == BeltState.FLAT)
        {
            front = canBeltConnect(pos.add(rotation).getTileEntity(world().unwrap()));
            back = canBeltConnect(pos.add(rotation.getOpposite()).getTileEntity(world().unwrap()));
        }
        else if (beltState == BeltState.INCLINE)
        {
            front = canBeltConnect(pos.add(rotation).add(0, 1, 0).getTileEntity(world().unwrap()));
            back = canBeltConnect(pos.add(rotation.getOpposite()).getTileEntity(world().unwrap()));
        }
        else if (beltState == BeltState.DECLINE)
        {
            front = canBeltConnect(pos.add(rotation).getTileEntity(world().unwrap()));
            back = canBeltConnect(pos.add(rotation.getOpposite()).add(0, 1, 0).getTileEntity(world().unwrap()));
        }

        //Belt on both sides
        if (front && back)
        {
            connectionState = ConnectionState.MIDDLE;
        }
        //Belt in front but not back
        else if (front)
        {
            connectionState = ConnectionState.BACK;
        }
        //Belt behind but not in front
        else if (back)
        {
            connectionState = ConnectionState.FRONT;
        }
        else
        {
            connectionState = ConnectionState.BASE;
        }
        syncToClient = true;
    }

    /**
     * Checks if a belt can connection visually to the tile
     *
     * @param tile
     * @return
     */
    protected boolean canBeltConnect(TileEntity tile)
    {
        if (tile != null)
        {
            if (tile instanceof ITileNodeHost && ((ITileNodeHost) tile).getTileNode() instanceof TileSimpleBelt)
            {
                TileSimpleBelt node = (TileSimpleBelt) ((ITileNodeHost) tile).getTileNode();
                return node.getDirection() == getDirection() || node.getDirection() == getDirection().getOpposite();
            }
        }
        return false;
    }

    @Override
    public void update(long ticks)
    {
        super.update(ticks);

        //Server only logic
        if (isServer())
        {
            //Update connections if triggered
            if (updateConnections)
            {
                updateConnections = false;
                updateConnections();
            }

            //Fire update client packet
            if (syncToClient)
            {
                syncToClient = false;
                sendDescPacket();
            }
        }

        //Runs both sides to improve animation
        if (tickRate() == 1 || ticks % tickRate() == 0)
        {
            //Create bounds if null
            if (searchBounds == null)
            {
                if (beltState == BeltState.FLAT)
                {
                    searchBounds = AxisAlignedBB.getBoundingBox(xi(), yi(), zi(), xi() + 1, yi() + 1, zi() + 1);
                }
                else
                {
                    searchBounds = AxisAlignedBB.getBoundingBox(xi(), yi(), zi(), xi() + 1, yi() + 1.4, zi() + 1);
                }
            }

            final List<Entity> list = world().unwrap().getEntitiesWithinAABB(Entity.class, searchBounds);
            if (list != null && !list.isEmpty())
            {
                boolean playerSneakingNear = false;

                //Refresh cache data
                items.clear();

                //Loop entities
                for (Entity entity : list)
                {
                    //Move entities
                    if (!shouldIgnore(entity))
                    {
                        applyMovement(entity);
                    }

                    //Look for sneaking players
                    if (entity instanceof EntityPlayer)
                    {
                        if (entity.isSneaking())
                        {
                            playerSneakingNear = true;
                        }
                    }

                    //Collect items
                    if (entity instanceof EntityItem)
                    {
                        items.add((EntityItem) entity);
                    }
                }

                //Update items
                for (EntityItem entity : items)
                {
                    if (playerSneakingNear)
                    {
                        entity.delayBeforeCanPickup = 0;
                    }
                    else
                    {
                        entity.delayBeforeCanPickup = 20;
                    }
                    entity.lifespan += tickRate();
                }

                //Clean up list
                Iterator<Entity> it = ignoreList.iterator();
                while (it.hasNext())
                {
                    if (!list.contains(it.next()))
                    {
                        it.remove();
                    }
                }
            }
            else if (ticks % 3 == 0)
            {
                ignoreList.clear();
            }
        }
    }

    @Override
    public boolean onPlayerRightClickWrench(EntityPlayer player, int side, float hitX, float hitY, float hitZ)
    {
        if (isServer())
        {
            ForgeDirection rotation = getDirection();

            boolean left = hitX > 0.75;
            boolean right = hitX < 0.25;
            boolean back = hitZ > 0.75;
            boolean front = hitZ < 0.25;

            player.addChatComponentMessage(new ChatComponentText("D: " + rotation + "  L: " + left + " R: " + right + " F: " + front + " B: " + back));

            //Change state
            if (player.isSneaking())
            {
                boolean b;

                if (rotation == ForgeDirection.SOUTH)
                {
                    b = front;
                    front = back;
                    back = b;
                }

                else if (rotation == ForgeDirection.EAST)
                {
                    front = left;
                    back = right;
                }
                else if (rotation == ForgeDirection.WEST)
                {
                    front = right;
                    back = left;
                }

                if (back)
                {
                    beltState = BeltState.INCLINE;
                    updateState();
                    player.addChatComponentMessage(new ChatComponentText("Setting belt to incline, dir: " + rotation));
                }
                else if (front)
                {
                    beltState = BeltState.DECLINE;
                    updateState();
                    player.addChatComponentMessage(new ChatComponentText("Setting belt to decline, dir: " + rotation));
                }
                else
                {
                    beltState = BeltState.FLAT;
                    updateState();
                    player.addChatComponentMessage(new ChatComponentText("Setting belt to flat, dir: " + rotation));
                }
            }
            //Rotate tile
            else
            {
                if (side == 0 || side == 1)
                {
                    if (!left && !right)
                    {
                        if (front)
                        {
                            setDirection(ForgeDirection.NORTH);
                        }
                        else if (back)
                        {
                            setDirection(ForgeDirection.SOUTH);
                        }
                    }
                    else if (left)
                    {
                        setDirection(ForgeDirection.EAST);
                    }
                    else if (right)
                    {
                        setDirection(ForgeDirection.WEST);
                    }
                }
                else
                {
                    setDirection(ForgeDirection.getOrientation(side));
                }
            }
        }
        return true;
    }

    protected int tickRate()
    {
        if (getHost().getHostBlock() instanceof BlockBase)
        {
            int rate = ((BlockBase) getHost().getHostBlock()).data.getSettingAsInt("tick.rate"); //TODO cache local?
            if (rate > 1)
            {
                return rate;
            }
        }
        return 1;
    }

    protected void applyMovement(Entity entity)
    {
        if (entity != null)
        {
            double delta_x = x() - entity.posX;
            double delta_z = z() - entity.posZ;
            double delta_y = z() - entity.posZ;

            double flatDeltaX = Math.abs(delta_x);
            double flatDeltaZ = Math.abs(delta_z);

            //To far away from belt, most likely only part of the entity is over the belt
            if (flatDeltaX > 0.5 || flatDeltaZ > 0.5)
            {
                return; //TODO spin entity for lolz
            }

            //Move entity forward
            Pos force = forceVector();
            pushEntity(entity, force.x(), force.y(), force.z());

            //Move entity towards center of belt
            ForgeDirection direction = getDirection();
            if (direction == ForgeDirection.NORTH || direction == ForgeDirection.SOUTH)
            {
                if (flatDeltaX > 0.1)
                {
                    pushEntity(entity, delta_x > 0 ? beltSpeed : -beltSpeed, 0, 0);
                }
            }
            else
            {
                if (flatDeltaZ > 0.1)
                {
                    pushEntity(entity, 0, 0, delta_z > 0 ? beltSpeed : -beltSpeed);
                }
            }
        }
    }

    protected void pushEntity(Entity entity, double x, double y, double z)
    {
        if (entity instanceof EntityPlayer)
        {
            if (!entity.isSneaking())
            {
                entity.addVelocity(x, y, z);
            }
        }
        else
        {
            entity.addVelocity(x, y, z);
        }
    }

    public Pos forceVector()
    {
        if (_forceToApply == null)
        {
            if (beltState == BeltState.INCLINE)
            {
                _forceToApply = new Pos(getDirection()).multiply(beltSpeed).add(0, beltSpeed, 0);
            }
            else if (beltState == BeltState.DECLINE)
            {
                _forceToApply = new Pos(getDirection()).multiply(beltSpeed);
            }
            else
            {
                _forceToApply = new Pos(getDirection()).multiply(beltSpeed);
            }
        }
        return _forceToApply;
    }

    public void ignore(Entity entity)
    {
        if (!ignoreList.contains(entity))
        {
            ignoreList.add(entity);
        }
    }

    public boolean shouldIgnore(Entity entity)
    {
        return ignoreList.contains(entity) || !entity.onGround || entity.isDead;
    }

    /**
     * Called to set the state of the belt
     * <p>
     * Used in packet handling
     *
     * @param belt       -enum index
     * @param connection - enum index
     */
    public void setState(int belt, int connection)
    {
        if (belt >= 0 && belt < BeltState.values().length)
        {
            beltState = BeltState.values()[belt];
        }
        if (connection >= 0 && connection < ConnectionState.values().length)
        {
            connectionState = ConnectionState.values()[connection];
        }
        updateState();
    }

    /**
     * Called to update the state of the belt.
     * Resets data about current state.
     */
    public void updateState()
    {
        _forceToApply = null;
        searchBounds = null;
        syncToClient = true;
        updateConnections = true;
    }

    @Override
    public ForgeDirection getDirection()
    {
        if (_cachedDirection == null)
        {
            _cachedDirection = ForgeDirection.getOrientation(getHost().getHostMeta());
        }
        return _cachedDirection;
    }

    @Override
    public AxisAlignedBB getSelectedBounds()
    {
        if (beltState != BeltState.FLAT)
        {
            return AxisAlignedBB.getBoundingBox(xi(), yi(), zi(), xi() + 1, yi() + 1, zi() + 1); //TODO cache
        }
        return getCollisionBounds();
    }

    @Override
    public AxisAlignedBB getCollisionBounds()
    {
        if (collisionBounds == null)
        {
            if (beltState != BeltState.FLAT)
            {
                collisionBounds = AxisAlignedBB.getBoundingBox(xi(), yi(), zi(), xi() + 1, yi() + .5, zi() + 1);
            }
            else
            {
                collisionBounds = AxisAlignedBB.getBoundingBox(xi(), yi(), zi(), xi() + 1, yi() + 0.32, zi() + 1);
            }
        }
        return collisionBounds;
    }

    @Override
    public void addCollisionBoxesToList(AxisAlignedBB aabb, List list, Entity entity)
    {
        if (beltState != BeltState.FLAT)
        {
            ForgeDirection direction = getDirection();
            if (beltState != BeltState.INCLINE)
            {
                direction = direction.getOpposite();
            }
            switch (direction)
            {
                case SOUTH:
                    list.add(AxisAlignedBB.getBoundingBox(xi(), yi() + 0.5, zi() + 0.5,
                            xi() + 1, yi() + 1, zi() + 1));
                    if (entity instanceof EntityItem)
                    {
                        list.add(AxisAlignedBB.getBoundingBox(xi(), yi() + 0.5, zi() + 0.25,
                                xi() + 1, yi() + 0.75, zi() + 0.5));
                    }
                    break;
                case NORTH:
                    list.add(AxisAlignedBB.getBoundingBox(xi(), yi() + 0.5, zi(),
                            xi() + 1, yi() + 1, zi() + 0.5));
                    if (entity instanceof EntityItem)
                    {
                        list.add(AxisAlignedBB.getBoundingBox(xi(), yi() + 0.5, zi() + 0.5,
                                xi() + 1, yi() + 0.75, zi() + 0.75));
                    }
                    break;
                case EAST:
                    list.add(AxisAlignedBB.getBoundingBox(xi() + 0.5, yi() + 0.5, zi(),
                            xi() + 1, yi() + 1, zi() + 1));
                    if (entity instanceof EntityItem)
                    {
                        list.add(AxisAlignedBB.getBoundingBox(xi() + 0.25, yi() + 0.5, zi(),
                                xi() + 0.5, yi() + 0.75, zi() + 1));
                    }
                    break;
                case WEST:
                    list.add(AxisAlignedBB.getBoundingBox(xi(), yi() + 0.5, zi(),
                            xi() + 0.5, yi() + 1, zi() + 1));
                    if (entity instanceof EntityItem)
                    {
                        list.add(AxisAlignedBB.getBoundingBox(xi() + 0.5, yi() + 0.5, zi(),
                                xi() + +0.75, yi() + 0.75, zi() + 1));
                    }
                    break;
            }
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public String getRenderStateKey(String key)
    {
        if (beltState == BeltState.INCLINE)
        {
            return "incline." + getDirection().name().toLowerCase() + "." + connectionState.name().toLowerCase() + "." + FRAME_SLANTED;
        }
        else if (beltState == BeltState.DECLINE)
        {
            return "decline." + getDirection().name().toLowerCase() + "." + connectionState.name().toLowerCase() + "." + FRAME_SLANTED;
        }
        return "flat." + getDirection().name().toLowerCase() + "." + connectionState.name().toLowerCase() + "." + FRAME_FLAT;
    }

    @Override
    public void readDescPacket(ByteBuf buf)
    {
        int b = buf.readInt();
        int c = buf.readInt();
        setState(b, c);
    }

    @Override
    public void writeDescPacket(ByteBuf buf)
    {
        buf.writeInt(beltState.ordinal());
        buf.writeInt(connectionState.ordinal());
    }

    @Override
    public void load(NBTTagCompound nbt)
    {
        super.load(nbt);
        if (nbt.hasKey("beltType"))
        {
            setState(nbt.getByte("beltType"), -1);
        }
    }

    @Override
    public NBTTagCompound save(NBTTagCompound nbt)
    {
        super.save(nbt);
        nbt.setByte("beltType", (byte) beltState.ordinal());
        return nbt;
    }

    @Override
    public void setDirection(ForgeDirection direction)
    {
        getHost().setMetaValue(direction.ordinal());
        updateState();
    }

    public enum BeltState
    {
        FLAT,
        INCLINE,
        DECLINE
    }

    public enum ConnectionState
    {
        BASE,
        FRONT,
        MIDDLE,
        BACK
    }
}
