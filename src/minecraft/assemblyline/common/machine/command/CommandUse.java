package assemblyline.common.machine.command;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLever;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.ISidedInventory;
import assemblyline.api.IArmbotUseable;

public class CommandUse extends Command
{
	private int times;
	private int curTimes;

	@Override
	public void onTaskStart()
	{
		this.times = 0;
		this.curTimes = 0;

		if (this.getArgs().length > 0)
		{
			this.times = this.getIntArg(0);
		}

		if (this.times <= 0)
			this.times = 1;
	}

	@Override
	protected boolean doTask()
	{
		Block block = Block.blocksList[this.world.getBlockId(tileEntity.getHandPosition().intX(), tileEntity.getHandPosition().intY(), tileEntity.getHandPosition().intZ())];
		TileEntity targetTile = this.tileEntity.getHandPosition().getTileEntity(this.world);

		if (targetTile != null)
		{
			if (targetTile instanceof IArmbotUseable)
			{
				((IArmbotUseable) targetTile).onUse(this.tileEntity, this.getArgs());
			}
			else if (targetTile instanceof ISidedInventory)
			{
				// TODO add IInventory side behavior for placing and taking items.
				if(tileEntity.getGrabbedEntities().size() > 0)
				{
					//add items to inv
				}else
				{
					//remove items from inv
				}
			}
			else if (block == Block.lever)
			{
				block.onBlockActivated(this.world, tileEntity.getHandPosition().intX(), tileEntity.getHandPosition().intY(), tileEntity.getHandPosition().intZ(), null, 0, 0, 0, 0);
			}

			
		}

		this.curTimes++;

		if (this.curTimes >= this.times)
			return false;

		return true;
	}

	@Override
	public String toString()
	{
		return "USE " + Integer.toString(this.times);
	}

	@Override
	public void readFromNBT(NBTTagCompound taskCompound)
	{
		super.readFromNBT(taskCompound);
		this.times = taskCompound.getInteger("useTimes");
		this.curTimes = taskCompound.getInteger("useCurTimes");
	}

	@Override
	public void writeToNBT(NBTTagCompound taskCompound)
	{
		super.writeToNBT(taskCompound);
		taskCompound.setInteger("useTimes", this.times);
		taskCompound.setInteger("useCurTimes", this.curTimes);
	}
}
