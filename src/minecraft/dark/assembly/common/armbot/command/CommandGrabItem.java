package dark.assembly.common.armbot.command;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import universalelectricity.core.vector.Vector3;

import com.builtbroken.common.science.units.UnitHelper;

import dark.api.al.coding.IProgramableMachine;
import dark.api.al.coding.args.ArgumentIntData;
import dark.assembly.common.armbot.TaskBase;

public class CommandGrabItem extends CommandGrabPrefab
{
    ItemStack stack = null;

    public CommandGrabItem()
    {
        super("Grab-Item");
        this.defautlArguments.add(new ArgumentIntData("blockID", -1, Block.blocksList.length - 1, -1));
        this.defautlArguments.add(new ArgumentIntData("blockMeta", -1, 15, -1));
        this.defautlArguments.add(new ArgumentIntData("stackSize", -1, 64, -1));
    }

    @Override
    public ProcessReturn onMethodCalled(World world, Vector3 location, IProgramableMachine armbot)
    {
        super.onMethodCalled(world, location, armbot);

        int ammount = UnitHelper.tryToParseInt(this.getArg("stackSize"), -1);
        int blockID = UnitHelper.tryToParseInt(this.getArg("blockID"), -1);
        int blockMeta = UnitHelper.tryToParseInt(this.getArg("blockMeta"), 32767);

        if (blockID > 0)
        {
            stack = new ItemStack(blockID, ammount <= 0 ? 1 : ammount, blockMeta == -1 ? 32767 : blockMeta);
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
        List<EntityItem> found = this.worldObj.getEntitiesWithinAABB(EntityItem.class, AxisAlignedBB.getBoundingBox(this.armPos.x - radius, this.armPos.y - radius, this.armPos.z - radius, this.armPos.x + radius, this.armPos.y + radius, this.armPos.z + radius));

        if (found != null && found.size() > 0)
        {
            for (EntityItem item : found)
            {
                if (stack == null || item.getEntityItem().isItemEqual(stack))
                {
                    this.armbot.grab(item);
                    if (this.belt != null)
                    {
                        belt.ignoreEntity(item);
                    }
                    return ProcessReturn.DONE;
                }
            }
        }

        return ProcessReturn.CONTINUE;
    }

    @Override
    public TaskBase clone()
    {
        return new CommandGrabItem();
    }
}
