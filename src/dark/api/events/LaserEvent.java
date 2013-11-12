package dark.api.events;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.Cancelable;
import net.minecraftforge.event.Event;
import universalelectricity.core.vector.Vector3;

/** An event triggered by entities or tiles that create lasers
 * 
 * @author DarkGuardsman */
public class LaserEvent extends Event
{
    public World world;
    public Vector3 spot;
    public Vector3 target;

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
        public Object shooter;

        public LaserFireEvent(World world, Vector3 spot, Vector3 target, Object shooter)
        {
            super(world, spot, target);
            this.shooter = shooter;
        }

        public LaserFireEvent(TileEntity tileEntity, MovingObjectPosition hit)
        {
            super(tileEntity.worldObj, new Vector3(tileEntity), new Vector3(hit));
            this.shooter = tileEntity;
        }
    }

    /** Called when a player fires a laser. Use this to cancel a laser hit event */
    @Cancelable
    public static class LaserFiredPlayerEvent extends LaserFireEvent
    {
        public ItemStack laserItem;
        public MovingObjectPosition hit;

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
        public Object shooter;

        public LaserMeltBlockEvent(World world, Vector3 spot, Vector3 hit, Object shooter)
        {
            super(world, spot, hit);
            this.shooter = shooter;
        }
    }

    /** Use this to change what drops when the laser finishes mining a block */
    public static class LaserDropItemEvent extends LaserEvent
    {
        public List<ItemStack> items;

        public LaserDropItemEvent(World world, Vector3 spot, Vector3 hit, List<ItemStack> items)
        {
            super(world, spot, hit);
            this.items = items;
        }
    }

    /** Called before a laser mines a block */
    @Cancelable
    public static class LaserMineBlockEvent extends LaserEvent
    {
        public Object shooter;

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
