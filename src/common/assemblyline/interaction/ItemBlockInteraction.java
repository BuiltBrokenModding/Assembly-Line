package assemblyline.interaction;

import java.util.List;

import net.minecraft.src.Block;
import net.minecraft.src.CreativeTabs;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.ItemBlock;
import net.minecraft.src.ItemStack;
import net.minecraft.src.MathHelper;
import net.minecraft.src.World;
import assemblyline.AssemblyLine;
import assemblyline.interaction.BlockInteraction.InteractMachineMetadata;

public class ItemBlockInteraction extends ItemBlock
{
	public ItemBlockInteraction(int par1)
	{
		super(par1);
		this.setHasSubtypes(true);
		this.setCreativeTab(CreativeTabs.tabRedstone);
	}

	public void getSubItems(int par1, CreativeTabs par2CreativeTabs, List par3List)
	{
		for (InteractMachineMetadata machine : InteractMachineMetadata.values())
		{
			par3List.add(new ItemStack(AssemblyLine.blockInteraction, 1, machine.metadata));
		}
	}

	public String getItemNameIS(ItemStack itemstack)
	{
		return InteractMachineMetadata.getBase(itemstack.getItemDamage()).name;
	}

	@Override
	public int getBlockID()
	{
		return AssemblyLine.blockInteraction.blockID;
	}

	@Override
	public boolean placeBlockAt(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ)
	{
		int angle = MathHelper.floor_double((player.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;
		if (!world.setBlockAndMetadataWithNotify(x, y, z, this.getBlockID(), stack.getItemDamage() + angle)) { return false; }

		if (world.getBlockId(x, y, z) == this.getBlockID())
		{
			Block.blocksList[this.getBlockID()].updateBlockMetadata(world, x, y, z, side, hitX, hitY, hitZ);
			Block.blocksList[this.getBlockID()].onBlockPlacedBy(world, x, y, z, player);
		}

		return true;
	}

}
