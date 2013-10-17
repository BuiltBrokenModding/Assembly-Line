package dark.assembly.common.armbot.command;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import universalelectricity.core.vector.Vector3;
import dark.api.al.IBelt;
import dark.api.al.coding.IProgramableMachine;
import dark.assembly.common.armbot.GrabDictionary;
import dark.assembly.common.armbot.TaskArmbot;
import dark.assembly.common.armbot.TaskBase;
import dark.assembly.common.machine.belt.TileEntityConveyorBelt;

/** Prefab for grab based commands
 *
 * @author DarkGuardsman */
public abstract class CommandGrabPrefab extends TaskArmbot
{
    public static final float radius = 0.5f;
    protected Vector3 armPos;
    protected IBelt belt;

    public CommandGrabPrefab(String name)
    {
        super(name, TaskType.DEFINEDPROCESS);
    }

    @Override
    public ProcessReturn onMethodCalled(World world, Vector3 location, IProgramableMachine armbot)
    {
        ProcessReturn re = super.onMethodCalled(world, location, armbot);
        if (re == ProcessReturn.CONTINUE)
        {
            this.armPos = this.armbot.getHandPos();
            TileEntity entity = this.armPos.getTileEntity(this.worldObj);
            if (entity == null)
            {
                entity = this.armPos.clone().translate(new Vector3(ForgeDirection.DOWN)).getTileEntity(this.worldObj);
            }
            if (entity instanceof IBelt)
            {
                this.belt = (IBelt) entity;
            }
            return ProcessReturn.CONTINUE;
        }
        return re;
    }

    @Override
    public ProcessReturn onUpdate()
    {
        super.onUpdate();

        if (this.armbot.getGrabbedObject() != null)
        {
            return ProcessReturn.DONE;
        }

        return ProcessReturn.CONTINUE;
    }
}
