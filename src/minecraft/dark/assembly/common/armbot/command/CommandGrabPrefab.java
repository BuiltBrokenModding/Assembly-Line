package dark.assembly.common.armbot.command;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import universalelectricity.core.vector.Vector3;
import dark.api.al.IBelt;
import dark.api.al.coding.IArmbot;
import dark.api.al.coding.IProgrammableMachine;
import dark.assembly.common.armbot.TaskBaseArmbot;

/** Prefab for grab based commands
 *
 * @author DarkGuardsman */
public abstract class CommandGrabPrefab extends TaskBaseArmbot
{
    public static final float radius = 0.5f;
    protected Vector3 armPos;
    protected IBelt belt;

    public CommandGrabPrefab(String name)
    {
        super(name);
    }

    @Override
    public ProcessReturn onMethodCalled()
    {
        ProcessReturn re = super.onMethodCalled();
        if (re == ProcessReturn.CONTINUE)
        {
            this.armPos = ((IArmbot) this.program.getMachine()).getHandPos();
            TileEntity entity = this.armPos.getTileEntity(this.program.getMachine().getLocation().left());
            if (entity == null)
            {
                entity = this.armPos.clone().translate(new Vector3(ForgeDirection.DOWN)).getTileEntity(this.program.getMachine().getLocation().left());
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

        if (((IArmbot) this.program.getMachine()).getGrabbedObject() != null)
        {
            return ProcessReturn.DONE;
        }

        return ProcessReturn.CONTINUE;
    }
}
