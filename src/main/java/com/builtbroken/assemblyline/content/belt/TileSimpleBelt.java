package com.builtbroken.assemblyline.content.belt;

import com.builtbroken.assemblyline.AssemblyLine;
import com.builtbroken.mc.api.tile.access.IRotation;
import com.builtbroken.mc.client.json.IJsonRenderStateProvider;
import com.builtbroken.mc.codegen.annotations.TileWrapped;
import com.builtbroken.mc.framework.block.imp.IBoundListener;
import com.builtbroken.mc.framework.logic.TileNode;
import com.builtbroken.mc.imp.transform.vector.Pos;
import com.builtbroken.mc.seven.framework.block.BlockBase;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Very simple entity driven belt
 * Created by DarkGuardsman on 9/6/2017.
 */
@TileWrapped(className = ".gen.TileEntityWrappedSimpleBelt")
public class TileSimpleBelt extends TileNode implements IRotation, IBoundListener, IJsonRenderStateProvider
{
    public static final byte FLAT = 0;
    public static final byte INCLINE = 1;
    public static final byte DECLINE = 2;

    public static float beltSpeed = 0.05f;

    public static int FRAME_FLAT = 0;
    public static int FRAME_SLANTED = 0;

    private ForgeDirection _cachedDirection;

    protected byte state = 0;

    protected List<Entity> ignoreList = new ArrayList();

    protected Pos _forceToApply;

    protected AxisAlignedBB searchBounds;
    protected AxisAlignedBB collisionBounds;

    public TileSimpleBelt()
    {
        super("belt.simple", AssemblyLine.DOMAIN);
    }

    @Override
    public void update(long ticks)
    {
        super.update(ticks);
        if (tickRate() == 1 || ticks % tickRate() == 0)
        {
            //Create bounds if null
            if (searchBounds == null)
            {
                searchBounds = AxisAlignedBB.getBoundingBox(xi(), yi(), zi(), xi() + 1, yi() + 1, zi() + 1);
            }

            //Move entities
            List<Entity> list = world().unwrap().getEntitiesWithinAABB(Entity.class, searchBounds);
            if (list != null && !list.isEmpty())
            {
                for (Entity entity : list)
                {
                    if (!shouldIgnore(entity))
                    {
                        moveEntity(entity);
                    }
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

    protected void moveEntity(Entity entity)
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
            entity.moveEntity(force.x(), force.y(), force.z());

            //Move entity towards center of belt
            ForgeDirection direction = getDirection();
            if (direction == ForgeDirection.NORTH || direction == ForgeDirection.SOUTH)
            {
                if (flatDeltaX > 0.1)
                {
                    entity.moveEntity(delta_x > 0 ? beltSpeed : -beltSpeed, 0, 0);
                }
            }
            else
            {
                if (flatDeltaZ > 0.1)
                {
                    entity.moveEntity(0, 0, delta_z > 0 ? beltSpeed : -beltSpeed);
                }
            }
        }
    }

    public Pos forceVector()
    {
        if (_forceToApply == null)
        {
            if (state == INCLINE)
            {
                _forceToApply = new Pos(getDirection()).multiply(beltSpeed).add(0, beltSpeed, 0);
            }
            else if (state == DECLINE)
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
     *
     * @param b
     */
    public void setState(byte b)
    {
        state = b;
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
        sendDescPacket(); //TODO move to update method to reduce packet spam
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
        return getCollisionBounds();
    }

    @Override
    public AxisAlignedBB getCollisionBounds()
    {
        if (collisionBounds == null)
        {
            //TODO change based on state
            collisionBounds = AxisAlignedBB.getBoundingBox(xi(), yi(), zi(), xi() + 1, yi() + 0.3, zi() + 1);
        }
        return collisionBounds;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public String getRenderStateKey(String key)
    {
        if (state == INCLINE)
        {
            return "incline." + getDirection().name().toLowerCase() + "." + FRAME_FLAT;
        }
        else if (state == DECLINE)
        {
            return "decline." + getDirection().name().toLowerCase() + "." + FRAME_FLAT;
        }
        return "flat." + getDirection().name().toLowerCase() + "." + FRAME_FLAT;
    }

    @Override
    public void readDescPacket(ByteBuf buf)
    {
        buf.writeByte(state);
    }

    @Override
    public void writeDescPacket(ByteBuf buf)
    {
        state = buf.readByte();
    }
}
