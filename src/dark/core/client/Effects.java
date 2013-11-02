package dark.core.client;

import net.minecraft.entity.Entity;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import universalelectricity.core.vector.Vector3;

public class Effects
{
    public static void drawParticleStreamTo(World world, Vector3 start, Vector3 end)
    {

    }

    public static void drawParticleStreamTo(Entity shooter, World world, double x, double y, double z)
    {
        Vec3 direction = shooter.getLookVec().normalize();
        double scale = 1.0;
        double xoffset = 1.3f;
        double yoffset = -.2;
        double zoffset = 0.3f;
        Vec3 horzdir = direction.normalize();
        horzdir.yCoord = 0;
        horzdir = horzdir.normalize();
        double cx = shooter.posX + direction.xCoord * xoffset - direction.yCoord * horzdir.xCoord * yoffset - horzdir.zCoord * zoffset;
        double cy = shooter.posY + shooter.getEyeHeight() + direction.yCoord * xoffset + (1 - Math.abs(direction.yCoord)) * yoffset;
        double cz = shooter.posZ + direction.zCoord * xoffset - direction.yCoord * horzdir.zCoord * yoffset + horzdir.xCoord * zoffset;
        double dx = x - cx;
        double dy = y - cy;
        double dz = z - cz;
        double ratio = Math.sqrt(dx * dx + dy * dy + dz * dz);

        while (Math.abs(cx - x) > Math.abs(dx / ratio))
        {
            world.spawnParticle("townaura", cx, cy, cz, 0.0D, 0.0D, 0.0D);
            cx += dx * 0.1 / ratio;
            cy += dy * 0.1 / ratio;
            cz += dz * 0.1 / ratio;
        }
    }
}
