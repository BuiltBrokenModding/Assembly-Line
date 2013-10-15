package dark.assembly.common.armbot.command;

import java.util.ArrayList;

import net.minecraft.block.Block;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import universalelectricity.core.vector.Vector3;
import dark.api.al.armbot.Command;
import dark.core.prefab.helpers.ItemWorldHelper;

/** Used by arms to break a specific block in a position.
 *
 * @author Calclavia */
public class CommandBreak extends Command
{
    public CommandBreak()
    {
        super("break");
    }

    public CommandBreak(String name)
    {
        super(name);
    }

    int BREAK_TIME = 30;
    boolean keep = false;

    @Override
    public boolean onUpdate()
    {
        super.onUpdate();

        Vector3 serachPosition = this.armbot.getHandPos();

        Block block = Block.blocksList[serachPosition.getBlockID(this.worldObj)];

        if (block != null && BREAK_TIME <= this.ticks)
        {
            ArrayList<ItemStack> items = block.getBlockDropped(this.worldObj, serachPosition.intX(), serachPosition.intY(), serachPosition.intZ(), serachPosition.getBlockMetadata(worldObj), 0);

            if (!this.keep || items.size() > 1)
            {
                ItemWorldHelper.dropBlockAsItem(this.worldObj, serachPosition);
            }
            else
            {
                this.armbot.grab(new EntityItem(this.worldObj, serachPosition.intX() + 0.5D, serachPosition.intY() + 0.5D, serachPosition.intZ() + 0.5D, items.get(0)));
            }

            worldObj.setBlock(serachPosition.intX(), serachPosition.intY(), serachPosition.intZ(), 0, 0, 3);
            return false;
        }

        /** Notes on break command Beds Break Wrong Multi blocks don't work */
        return true;
    }

    @Override
    public Command clone()
    {
        return new CommandBreak();
    }
}
