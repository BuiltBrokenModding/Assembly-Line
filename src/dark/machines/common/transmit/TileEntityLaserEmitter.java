package dark.machines.common.transmit;

import java.awt.Color;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumMovingObjectType;
import net.minecraft.util.MovingObjectPosition;
import net.minecraftforge.common.ForgeDirection;
import universalelectricity.core.vector.Vector3;
import dark.core.prefab.machine.TileEntityEnergyMachine;
import dark.machines.common.DarkMain;

public class TileEntityLaserEmitter extends TileEntityEnergyMachine
{
    /** Is tile set up to receive power */
    private boolean receiver = false;
    /** Demand of connected network */
    private float powerDemand = 0.0f;
    /** Supply from other laser emitter */
    private float powerSupply = 0.0f;
    private float yaw, pitch, prevYaw, prevPitch, deltaYaw, deltaPitch;
    /** Color of renderer laser */
    private Color color = Color.red;
    /** Linked emitter */
    TileEntityLaserEmitter linkedEmitter = null;
    Vector3 laserTarget = null;

    public TileEntityLaserEmitter()
    {
        super(.001f/* 1W/t*/, 1f/* 1000W battery*/);
    }

    /** Facing direction of the tile and not the laser */
    public ForgeDirection getFacingDirection()
    {
        int meta = 0;
        if (this.worldObj != null)
        {
            meta = (worldObj.getBlockMetadata(xCoord, yCoord, zCoord) % 6);
        }
        return ForgeDirection.getOrientation(meta);
    }

    @Override
    public void updateEntity()
    {
        super.updateEntity();
        if (this.isFunctioning())
        {
            this.updateRotation();
            if (this.linkedEmitter != null && ticks % 20 == 0)
            {
                this.createLaser(new Vector3(linkedEmitter));
            }
            else if (laserTarget != null)
            {
                TileEntity entity = laserTarget.getTileEntity(this.worldObj);
                if (entity instanceof TileEntityLaserEmitter && ((TileEntityLaserEmitter) entity).receiver != this.receiver)
                {
                    linkedEmitter = (TileEntityLaserEmitter) entity;
                }
            }
        }
    }

    public void updateRotation()
    {

    }

    protected void createLaser(Vector3 target)
    {
        Vector3 start = new Vector3(this);
        double distance = start.distance(target);
        MovingObjectPosition hit = start.rayTrace(this.worldObj, yaw, pitch, true, distance);
        if (hit != null)
        {
            if (hit.typeOfHit == EnumMovingObjectType.ENTITY)
            {
                //TODO damage entity if power is over 1000W
            }
            else
            {

            }
        }
        else
        {
            DarkMain.proxy.renderBeam(this.worldObj, start, target, color, 20);
        }
    }

    @Override
    public boolean canConnect(ForgeDirection direction)
    {
        return direction == this.getFacingDirection().getOpposite();
    }

    @Override
    public float getRequest(ForgeDirection side)
    {
        if (!receiver && side == getFacingDirection().getOpposite())
        {
            return powerDemand;
        }
        return 0;
    }

    @Override
    public float getProvide(ForgeDirection direction)
    {
        if (receiver && direction == getFacingDirection().getOpposite())
        {
            return powerSupply;
        }
        return 0;
    }

    @Override
    public String getChannel()
    {
        return DarkMain.CHANNEL;
    }

}
