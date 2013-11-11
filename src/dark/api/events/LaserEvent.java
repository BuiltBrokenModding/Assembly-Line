package dark.api.events;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.Cancelable;
import net.minecraftforge.event.Event;
import net.minecraftforge.event.entity.player.PlayerEvent;
import universalelectricity.core.vector.Vector3;

/** An event triggered by entities or tiles that create lasers
 * 
 * @author DarkGuardsman */
public class LaserEvent extends Event
{
    World world;
    Vector3 spot;
    Vector3 target;

    public LaserEvent(World world, Vector3 spot, Vector3 target)
    {
        this.world = world;
        this.spot = spot;
        this.target = target;
    }

    /** Called when a laser is fired */
    @Cancelable
    public static class LaserFireEvent extends LaserEvent
    {
        Object shooter;

        public LaserFireEvent(World world, Vector3 spot, Vector3 target, Object shooter)
        {
            super(world, spot, target);
            this.shooter = shooter;
        }
    }

    /** Called when a player fires a laser. Use this to cancel a laser hit event */
    @Cancelable
    public static class LaserFiredPlayerEvent extends LaserFireEvent
    {
        ItemStack laserItem;
        MovingObjectPosition hit;

        public LaserFiredPlayerEvent(EntityPlayer player, MovingObjectPosition hit, ItemStack stack)
        {
            super(player.worldObj, new Vector3(player), new Vector3(hit), player);
            this.laserItem = stack;
            this.hit = hit;
        }
    }

    /** Called when a laser is heating up a block to be mined */
    public static class LaserMeltBlockEvent extends LaserEvent
    {
        Object shooter;

        public LaserMeltBlockEvent(World world, Vector3 spot, Vector3 hit, Object shooter)
        {
            super(world, spot, hit);
            this.shooter = shooter;
        }
    }

    /** Called before a laser mines a block */
    @Cancelable
    public static class LaserMineBlockEvent extends LaserEvent
    {
        Object shooter;

        public LaserMineBlockEvent(World world, Vector3 spot, Vector3 hit, Object shooter)
        {
            super(world, spot, hit);
            this.shooter = shooter;
        }

    }

    public static boolean doLaserHarvestCheck(EntityPlayer player, Vector3 hit)
    {
        LaserEvent event = new LaserMineBlockEvent(player.worldObj, new Vector3(player), hit, player);
        MinecraftForge.EVENT_BUS.post(event);
        return !event.isCanceled();
    }
}
