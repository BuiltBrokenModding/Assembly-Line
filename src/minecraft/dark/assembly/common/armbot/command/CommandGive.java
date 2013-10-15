package dark.assembly.common.armbot.command;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.builtbroken.common.science.units.UnitHelper;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import universalelectricity.core.vector.Vector3;
import dark.api.al.armbot.Command;
import dark.api.al.armbot.IArmbot;
import dark.assembly.common.machine.InvInteractionHelper;

public class CommandGive extends Command
{

    private ItemStack stack;
    private int ammount = -1;

    public CommandGive()
    {
        super("give");
    }

    @Override
    public boolean onMethodCalled(World world, Vector3 location, IArmbot armbot, Object[] arguments)
    {
        super.onMethodCalled(world, location, armbot, arguments);

        if (this.getArgs().length > 1)
        {
            ammount = UnitHelper.tryToParseInt("" + this.getArg(1));
        }

        if (this.getArgs().length > 0)
        {
            stack = this.getItem("" + this.getArg(0), ammount == -1 ? 1 : ammount);
        }

        return true;

    }

    @Override
    public boolean onUpdate()
    {
        TileEntity targetTile = this.armbot.getHandPos().getTileEntity(this.worldObj);

        if (targetTile != null && this.armbot.getGrabbedObjects().size() > 0)
        {
            ForgeDirection direction = this.tileEntity.getFacingDirectionFromAngle();
            List<ItemStack> stacks = new ArrayList<ItemStack>();
            if (this.stack != null)
            {
                stacks.add(stack);
            }
            InvInteractionHelper invEx = new InvInteractionHelper(this.tileEntity.worldObj, new Vector3(this.tileEntity), stacks, false);

            Iterator<ItemStack> targetIt = this.tileEntity.getGrabbedItems().iterator();
            boolean flag = true;
            while (targetIt.hasNext())
            {
                ItemStack insertStack = targetIt.next();
                if (insertStack != null)
                {
                    ItemStack original = insertStack.copy();
                    insertStack = invEx.tryPlaceInPosition(insertStack, new Vector3(targetTile), direction.getOpposite());
                    flag = insertStack != null && insertStack.stackSize == original.stackSize;
                    if (insertStack == null || insertStack.stackSize <= 0)
                    {
                        targetIt.remove();
                        break;
                    }
                }
            }
            return flag;
        }
        return false;
    }

    @Override
    public String toString()
    {
        return super.toString() + " " + (stack != null ? stack.toString() : "1x???@???");
    }

    @Override
    public Command readFromNBT(NBTTagCompound taskCompound)
    {
        super.readFromNBT(taskCompound);
        this.stack = ItemStack.loadItemStackFromNBT(taskCompound.getCompoundTag("item"));
        return this;
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound taskCompound)
    {
        super.writeToNBT(taskCompound);
        if (stack != null)
        {
            NBTTagCompound tag = new NBTTagCompound();
            this.stack.writeToNBT(tag);
            taskCompound.setTag("item", tag);
        }
        return taskCompound;
    }

    @Override
    public Command clone()
    {
        return new CommandGive();
    }
}
