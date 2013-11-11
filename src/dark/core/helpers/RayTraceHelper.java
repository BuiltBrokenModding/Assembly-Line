package dark.core.helpers;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class RayTraceHelper
{
    public static MovingObjectPosition raytraceEntities(World world, Entity entity, Vec3 error, double reachDistance, boolean collisionFlag)
    {

        MovingObjectPosition pickedEntity = null;
        Vec3 playerPosition = Vec3.createVectorHelper(entity.posX, entity.posY + entity.getEyeHeight(), entity.posZ);
        Vec3 playerLook = entity.getLookVec();

        Vec3 playerViewOffset = Vec3.createVectorHelper(playerPosition.xCoord + playerLook.xCoord * reachDistance + error.xCoord, playerPosition.yCoord + playerLook.yCoord * reachDistance + error.yCoord, playerPosition.zCoord + playerLook.zCoord * reachDistance + error.zCoord);

        double playerBorder = 1.1 * reachDistance;
        AxisAlignedBB boxToScan = entity.boundingBox.expand(playerBorder, playerBorder, playerBorder);

        List<Entity> entitiesHit = world.getEntitiesWithinAABBExcludingEntity(entity, boxToScan);
        double closestEntity = reachDistance;

        if (entitiesHit == null || entitiesHit.isEmpty())
        {
            return null;
        }
        for (Entity entityHit : entitiesHit)
        {
            if (entityHit != null && entityHit.canBeCollidedWith() && entityHit.boundingBox != null)
            {
                float border = entityHit.getCollisionBorderSize();
                AxisAlignedBB aabb = entityHit.boundingBox.expand(border, border, border);
                MovingObjectPosition hitMOP = aabb.calculateIntercept(playerPosition, playerViewOffset);

                if (hitMOP != null)
                {
                    if (aabb.isVecInside(playerPosition))
                    {
                        if (0.0D < closestEntity || closestEntity == 0.0D)
                        {
                            pickedEntity = new MovingObjectPosition(entityHit);
                            if (pickedEntity != null)
                            {
                                pickedEntity.hitVec = hitMOP.hitVec;
                                closestEntity = 0.0D;
                            }
                        }
                    }
                    else
                    {
                        double distance = playerPosition.distanceTo(hitMOP.hitVec);

                        if (distance < closestEntity || closestEntity == 0.0D)
                        {
                            pickedEntity = new MovingObjectPosition(entityHit);
                            pickedEntity.hitVec = hitMOP.hitVec;
                            closestEntity = distance;
                        }
                    }
                }
            }
        }
        return pickedEntity;
    }

    public static MovingObjectPosition raytraceEntities(World world, Vec3 start, Vec3 end, boolean collisionFlag, Entity exclude)
    {

        AxisAlignedBB boxToScan = AxisAlignedBB.getBoundingBox(start.xCoord, start.zCoord, start.yCoord, end.xCoord, end.yCoord, end.zCoord);
        MovingObjectPosition pickedEntity = null;
        List<Entity> entities;
        if (exclude == null)
        {
            entities = world.getEntitiesWithinAABB(Entity.class, boxToScan);
        }
        else
        {
            entities = world.getEntitiesWithinAABBExcludingEntity(exclude, boxToScan);
        }
        double closestEntity = start.distanceTo(end);

        if (entities == null || entities.isEmpty())
        {
            return null;
        }
        for (Entity entityHit : entities)
        {
            if (entityHit != null && entityHit.canBeCollidedWith() && entityHit.boundingBox != null)
            {
                float border = entityHit.getCollisionBorderSize();
                AxisAlignedBB aabb = entityHit.boundingBox.expand(border, border, border);
                MovingObjectPosition hitMOP = aabb.calculateIntercept(start, end);

                if (hitMOP != null)
                {
                    if (aabb.isVecInside(start))
                    {
                        if (0.0D < closestEntity || closestEntity == 0.0D)
                        {
                            pickedEntity = new MovingObjectPosition(entityHit);
                            if (pickedEntity != null)
                            {
                                pickedEntity.hitVec = hitMOP.hitVec;
                                closestEntity = 0.0D;
                            }
                        }
                    }
                    else
                    {
                        double distance = start.distanceTo(hitMOP.hitVec);

                        if (distance < closestEntity || closestEntity == 0.0D)
                        {
                            pickedEntity = new MovingObjectPosition(entityHit);
                            pickedEntity.hitVec = hitMOP.hitVec;
                            closestEntity = distance;
                        }
                    }
                }
            }
        }
        return pickedEntity;
    }

    public static MovingObjectPosition raytraceBlocks(World world, Entity entity, Vec3 error, double reachDistance, boolean collisionFlag)
    {
        Vec3 playerPosition = Vec3.createVectorHelper(entity.posX, entity.posY + entity.getEyeHeight(), entity.posZ);
        Vec3 playerLook = entity.getLookVec();

        Vec3 playerViewOffset = Vec3.createVectorHelper(playerPosition.xCoord + playerLook.xCoord * reachDistance + error.xCoord, playerPosition.yCoord + playerLook.yCoord * reachDistance + error.yCoord, playerPosition.zCoord + playerLook.zCoord * reachDistance + error.zCoord);
        return raytraceBlocks(world, playerPosition, playerViewOffset, collisionFlag);
    }

    public static MovingObjectPosition raytraceBlocks(World world, Entity entity, double reachDistance, boolean collisionFlag)
    {
        Vec3 playerPosition = Vec3.createVectorHelper(entity.posX, entity.posY + entity.getEyeHeight(), entity.posZ);
        Vec3 playerLook = entity.getLookVec();

        Vec3 playerViewOffset = Vec3.createVectorHelper(playerPosition.xCoord + playerLook.xCoord * reachDistance, playerPosition.yCoord + playerLook.yCoord * reachDistance, playerPosition.zCoord + playerLook.zCoord * reachDistance);
        return raytraceBlocks(world, playerPosition, playerViewOffset, collisionFlag);
    }

    public static MovingObjectPosition raytraceBlocks(World world, Vec3 start, Vec3 end, boolean collisionFlag)
    {
        return world.rayTraceBlocks_do_do(start, end, collisionFlag, !collisionFlag);
    }

    public static MovingObjectPosition ray_trace_do(World world, Entity entity, Vec3 e, double reachDistance, boolean collisionFlag)
    {

        MovingObjectPosition hitBlock = raytraceBlocks(world, entity, e, reachDistance, collisionFlag);
        MovingObjectPosition hitEntity = raytraceEntities(world, entity, e, reachDistance, collisionFlag);
        if (hitEntity == null)
        {
            return hitBlock;
        }
        else if (hitBlock == null)
        {
            return hitEntity;
        }
        else
        {
            Vec3 playerPosition = Vec3.createVectorHelper(entity.posX, entity.posY + entity.getEyeHeight(), entity.posZ);
            if (hitEntity.hitVec.distanceTo(playerPosition) < hitBlock.hitVec.distanceTo(playerPosition))
            {
                return hitEntity;
            }
            else
            {
                return hitBlock;
            }
        }
    }

    public static Vec3 getLook(Entity entity, float par1)
    {
        float f1;
        float f2;
        float f3;
        float f4;

        if (par1 == 1.0F)
        {
            f1 = MathHelper.cos(-entity.rotationYaw * 0.017453292F - (float) Math.PI);
            f2 = MathHelper.sin(-entity.rotationYaw * 0.017453292F - (float) Math.PI);
            f3 = -MathHelper.cos(-entity.rotationPitch * 0.017453292F);
            f4 = MathHelper.sin(-entity.rotationPitch * 0.017453292F);
            return entity.worldObj.getWorldVec3Pool().getVecFromPool((f2 * f3), f4, (f1 * f3));
        }
        else
        {
            f1 = entity.prevRotationPitch + (entity.rotationPitch - entity.prevRotationPitch) * par1;
            f2 = entity.prevRotationYaw + (entity.rotationYaw - entity.prevRotationYaw) * par1;
            f3 = MathHelper.cos(-f2 * 0.017453292F - (float) Math.PI);
            f4 = MathHelper.sin(-f2 * 0.017453292F - (float) Math.PI);
            float f5 = -MathHelper.cos(-f1 * 0.017453292F);
            float f6 = MathHelper.sin(-f1 * 0.017453292F);
            return entity.worldObj.getWorldVec3Pool().getVecFromPool((f4 * f5), f6, (f3 * f5));
        }
    }
}
