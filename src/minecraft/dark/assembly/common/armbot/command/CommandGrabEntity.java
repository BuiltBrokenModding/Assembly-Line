package dark.assembly.common.armbot.command;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import universalelectricity.core.vector.Vector3;
import dark.api.al.coding.IProgramableMachine;
import dark.api.al.coding.args.ArgumentData;
import dark.api.al.coding.args.ArgumentListData;
import dark.assembly.common.armbot.GrabDictionary;
import dark.assembly.common.armbot.TaskBase;

public class CommandGrabEntity extends CommandGrabPrefab
{
    /** If the grab command is specific to one entity this tell whether or not to grab the child
     * version of that entity. */
    public boolean child = false;
    /** The item to be collected. */
    private Class<? extends Entity> entityToInclude;

    @SuppressWarnings("unchecked")
    public CommandGrabEntity()
    {
        super("Grab-Entity");
        this.defautlArguments.add(new ArgumentData("child", false));
        this.defautlArguments.add(new ArgumentListData("Entity", Entity.class, GrabDictionary.getList().toArray(new Object[1])));
    }

    @Override
    public ProcessReturn onMethodCalled(World world, Vector3 location, IProgramableMachine armbot)
    {
        super.onMethodCalled(world, location, armbot);
        this.entityToInclude = Entity.class;
        try
        {
            if (this.getArg("Entity") instanceof Class)
            {
                this.entityToInclude = (Class<? extends Entity>) this.getArg("Entity");
            }
        }
        catch (Exception e)
        {

        }
        if(this.getArg("child") instanceof Boolean)
        {
            this.child = (boolean) this.getArg("child");
        }

        return ProcessReturn.CONTINUE;
    }

    @Override
    public ProcessReturn onUpdate()
    {
        super.onUpdate();

        if (this.armbot.getGrabbedObject() != null)
        {
            return ProcessReturn.DONE;
        }
        List<Entity> found = this.worldObj.getEntitiesWithinAABB(entityToInclude, AxisAlignedBB.getBoundingBox(this.armPos.x - radius, this.armPos.y - radius, this.armPos.z - radius, this.armPos.x + radius, this.armPos.y + radius, this.armPos.z + radius));

        if (found != null && found.size() > 0)
        {
            for (Entity entity : found)
            {
                if ((entity != null && !(entity instanceof EntityArrow) && !(entity instanceof EntityPlayer) && (!(entity instanceof EntityAgeable) || (entity instanceof EntityAgeable && child == ((EntityAgeable) entity).isChild()))))
                {
                    this.armbot.grab(entity);
                    if (this.belt != null)
                    {
                        belt.ignoreEntity(entity);
                    }
                    return ProcessReturn.DONE;
                }
            }
        }

        return ProcessReturn.CONTINUE;
    }

    @Override
    public TaskBase load(NBTTagCompound taskCompound)
    {
        super.loadProgress(taskCompound);
        this.child = taskCompound.getBoolean("child");
        this.entityToInclude = GrabDictionary.get(taskCompound.getString("name")).getEntityClass();
        return this;
    }

    @Override
    public NBTTagCompound save(NBTTagCompound taskCompound)
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
        return new CommandGrabEntity();
    }

}
