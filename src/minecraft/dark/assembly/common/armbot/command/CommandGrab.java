package dark.assembly.common.armbot.command;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import universalelectricity.core.vector.Vector3;
import dark.api.al.armbot.IArmbot;
import dark.api.al.armbot.ILogicDevice;
import dark.api.al.armbot.IDeviceTask.TaskType;
import dark.assembly.common.armbot.TaskBase;
import dark.assembly.common.armbot.TaskArmbot;
import dark.assembly.common.armbot.GrabDictionary;
import dark.assembly.common.machine.belt.TileEntityConveyorBelt;

/** Used by arms to search for entities in a region
 *
 * @author Calclavia */
public class CommandGrab extends TaskArmbot
{

    public static final float radius = 0.5f;
    /** If the grab command is specific to one entity this tell whether or not to grab the child
     * version of that entity. */
    public boolean child = false;

    private TileEntityConveyorBelt belt;
    /** The item to be collected. */
    private Class<? extends Entity> entityToInclude;

    public CommandGrab()
    {
        super("Grab", TaskType.DEFINEDPROCESS);
    }

    @Override
    public ProcessReturn onMethodCalled(World world, Vector3 location, ILogicDevice armbot)
    {
        super.onMethodCalled(world, location, armbot);
        this.entityToInclude = Entity.class;
        if (this.getArgs() != null && this.getArgs().length > 0 && this.getArgs()[0] != null)
        {

            if (this.getArg(0) instanceof String && (((String) this.getArg(0)).equalsIgnoreCase("baby") || ((String) this.getArg(0)).equalsIgnoreCase("child")))
            {
                child = true;
                if (this.getArgs().length > 1 && this.getArgs()[1] != null)
                {
                    this.entityToInclude = GrabDictionary.get(this.getArg(1)).getEntityClass();
                }
            }
            else
            {
                this.entityToInclude = GrabDictionary.get(this.getArg(0)).getEntityClass();
                if (this.getArg(1) instanceof String && (((String) this.getArg(1)).equalsIgnoreCase("baby") || ((String) this.getArg(1)).equalsIgnoreCase("child")))
                {
                    child = true;
                }
            }

        }
        return ProcessReturn.CONTINUE;
    }

    @Override
    public ProcessReturn onUpdate()
    {
        super.onUpdate();

        if (this.armbot.getGrabbedObjects().size() > 0)
        {
            return ProcessReturn.DONE;
        }

        Vector3 serachPosition = this.armbot.getHandPos();
        List<Entity> found = this.worldObj.getEntitiesWithinAABB(this.entityToInclude, AxisAlignedBB.getBoundingBox(serachPosition.x - radius, serachPosition.y - radius, serachPosition.z - radius, serachPosition.x + radius, serachPosition.y + radius, serachPosition.z + radius));

        TileEntity ent = serachPosition.getTileEntity(worldObj);
        Vector3 searchPostion2 = Vector3.add(serachPosition, new Vector3(0, -1, 0));
        TileEntity ent2 = searchPostion2.getTileEntity(worldObj);
        if (ent instanceof TileEntityConveyorBelt)
        {
            this.belt = (TileEntityConveyorBelt) ent;

        }
        else if (ent2 instanceof TileEntityConveyorBelt)
        {
            this.belt = (TileEntityConveyorBelt) ent2;
        }
        if (found != null && found.size() > 0)
        {
            for (int i = 0; i < found.size(); i++)
            {
                if (found.get(i) != null && !(found.get(i) instanceof EntityArrow) && !(found.get(i) instanceof EntityPlayer) && found.get(i).ridingEntity == null && (!(found.get(i) instanceof EntityAgeable) || (found.get(i) instanceof EntityAgeable && child == ((EntityAgeable) found.get(i)).isChild())))
                {
                    this.armbot.grab(found.get(i));
                    if (this.belt != null)
                    {
                        belt.ignoreEntity(found.get(i));
                    }
                    return ProcessReturn.DONE;
                }
            }
        }

        return ProcessReturn.CONTINUE;
    }

    @Override
    public TaskBase loadProgress(NBTTagCompound taskCompound)
    {
        super.loadProgress(taskCompound);
        this.child = taskCompound.getBoolean("child");
        this.entityToInclude = GrabDictionary.get(taskCompound.getString("name")).getEntityClass();
        return this;
    }

    @Override
    public NBTTagCompound saveProgress(NBTTagCompound taskCompound)
    {
        super.saveProgress(taskCompound);
        taskCompound.setBoolean("child", child);
        taskCompound.setString("name", ((this.entityToInclude != null) ? GrabDictionary.get(this.entityToInclude).getName() : ""));
        return taskCompound;
    }

    @Override
    public String toString()
    {
        String baby = "";
        String entity = "";
        if (this.entityToInclude != null)
        {
            entity = GrabDictionary.get(this.entityToInclude).getName();
            if (this.child)
            {
                // TODO do check for EntityAgable
                baby = "baby ";
            }
        }
        return "GRAB " + baby + entity;
    }

    @Override
    public TaskBase clone()
    {
        return new CommandGrab();
    }
}
