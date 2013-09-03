package dark.assembly.common.armbot.command;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.ForgeDirection;
import universalelectricity.core.vector.Vector3;
import dark.core.prefab.helpers.ItemWorldHelper;

public class CommandPowerTo extends Command
{
    private int duration;
    private int ticksRan;

    @Override
    public void onTaskStart()
    {
        this.duration = 0;
        this.ticksRan = 0;

        if (this.getArgs().length > 0)
        {
            this.duration = this.getIntArg(0);
        }

        if (this.duration <= 30)
        {
            this.duration = 30;
        }
    }

    @Override
    protected boolean doTask()
    {
        super.doTask();
        if (this.tileEntity.isProvidingPower && this.ticksRan >= duration)
        {
            powerBlock(false);
            return false;
        }
        else if (this.tileEntity.isProvidingPower)
        {
            Vector3 loc = this.tileEntity.getHandPosition();
            world.spawnParticle("smoke", loc.x, loc.y, loc.z, 0.0D, 0.0D, 0.0D);
            world.spawnParticle("flame", loc.x, loc.y, loc.z, 0.0D, 0.0D, 0.0D);
        }

        Block block = Block.blocksList[this.world.getBlockId(tileEntity.getHandPosition().intX(), tileEntity.getHandPosition().intY(), tileEntity.getHandPosition().intZ())];
        TileEntity targetTile = this.tileEntity.getHandPosition().getTileEntity(this.world);

        if (this.tileEntity.getGrabbedItems().size() > 0)
        {
            List<ItemStack> stacks = new ArrayList<ItemStack>();
            stacks.add(new ItemStack(Block.torchRedstoneActive, 1, 0));
            stacks.add(new ItemStack(Block.torchRedstoneIdle, 1, 0));
            if (ItemWorldHelper.filterItems(this.tileEntity.getGrabbedItems(), stacks).size() > 0)
            {
                this.powerBlock(true);
            }
        }

        this.ticksRan++;
        return true;
    }

    public void powerBlock(boolean on)
    {
        if (!on)
        {
            this.tileEntity.isProvidingPower = false;
        }
        else
        {
            this.tileEntity.isProvidingPower = true;
        }
        int id = this.tileEntity.worldObj.getBlockId(this.tileEntity.xCoord, this.tileEntity.yCoord, this.tileEntity.zCoord);
        for (int i = 2; i < 6; i++)
        {
            ForgeDirection dir = ForgeDirection.getOrientation(i);
            this.world.notifyBlocksOfNeighborChange(this.tileEntity.xCoord + dir.offsetX, this.tileEntity.yCoord + dir.offsetY, this.tileEntity.zCoord + dir.offsetZ, id);
        }
    }

    @Override
    public String toString()
    {
        return "POWERTO " + Integer.toString(this.duration);
    }

    @Override
    public void readFromNBT(NBTTagCompound taskCompound)
    {
        super.readFromNBT(taskCompound);
        this.duration = taskCompound.getInteger("useTimes");
        this.ticksRan = taskCompound.getInteger("useCurTimes");
    }

    @Override
    public void writeToNBT(NBTTagCompound taskCompound)
    {
        super.writeToNBT(taskCompound);
        taskCompound.setInteger("useTimes", this.duration);
        taskCompound.setInteger("useCurTimes", this.ticksRan);
    }
}
