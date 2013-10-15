package dark.assembly.common.armbot.command;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.ForgeDirection;
import universalelectricity.core.vector.Vector3;
import dark.api.al.armbot.Command;
import dark.assembly.common.machine.InvInteractionHelper;

public class CommandTake extends Command
{
    private ItemStack stack;

    @Override
    public void onStart()
    {
        int id = 0;
        int meta = 32767;
        int count = 1;

        if (this.getArgs().length > 0)
        {
            String block = this.getArg(0);
            if (block.contains(":"))
            {
                String[] blockID = block.split(":");
                id = Integer.parseInt(blockID[0]);
                meta = Integer.parseInt(blockID[1]);
            }
            else
            {
                id = Integer.parseInt(block);
            }
        }
        if (this.getArgs().length > 1)
        {
            count = this.getIntArg(1);
        }
        if (id == 0)
        {
            stack = null;
        }
        else
        {
            stack = new ItemStack(id, count, meta);
        }
    }

    @Override
    protected boolean onUpdate()
    {
        TileEntity targetTile = this.tileEntity.getHandPosition().getTileEntity(this.worldObj);

        if (targetTile != null && this.tileEntity.getGrabbedItems().size() <= 0)
        {
            ForgeDirection direction = this.tileEntity.getFacingDirectionFromAngle();
            List<ItemStack> stacks = new ArrayList<ItemStack>();
            if (this.stack != null)
            {
                stacks.add(stack);
            }
            InvInteractionHelper invEx = new InvInteractionHelper(this.tileEntity.worldObj, new Vector3(this.tileEntity), stacks, false);
            this.tileEntity.grabItem(invEx.tryGrabFromPosition(new Vector3(targetTile), direction, this.stack != null ? stack.stackSize : 1));
            return !(this.tileEntity.getGrabbedItems().size() > 0);

        }
        return true;
    }

    @Override
    public String toString()
    {
        return "Take " + (stack != null ? stack.toString() : "1x???@???  ");
    }

    @Override
    public void readFromNBT(NBTTagCompound taskCompound)
    {
        super.readFromNBT(taskCompound);
        this.stack = ItemStack.loadItemStackFromNBT(taskCompound.getCompoundTag("item"));
    }

    @Override
    public void writeToNBT(NBTTagCompound taskCompound)
    {
        super.writeToNBT(taskCompound);
        if (stack != null)
        {
            NBTTagCompound tag = new NBTTagCompound();
            this.stack.writeToNBT(tag);
            taskCompound.setTag("item", tag);
        }
    }
}
