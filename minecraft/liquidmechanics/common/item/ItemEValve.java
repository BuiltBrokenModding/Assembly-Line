package liquidmechanics.common.item;

import java.util.List;

import liquidmechanics.api.helpers.Liquid;
import liquidmechanics.common.LiquidMechanics;
import liquidmechanics.common.tileentity.TileEntityReleaseValve;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class ItemEValve extends ItemBlock
{
	int index = 32;// 32 + 4 rows alloted to pipes
	private int spawnID;

	public ItemEValve(int id)
	{
		super(id);
		this.setMaxDamage(0);
		this.setHasSubtypes(true);
		this.setItemName("eValve");
		this.setCreativeTab(CreativeTabs.tabRedstone);
	}

	@Override
	public String getItemNameIS(ItemStack itemstack)
	{
		return "eValve";
	}

	@Override
	public void getSubItems(int par1, CreativeTabs par2CreativeTabs, List par3List)
	{
		for (int i = 0; i < Liquid.values().length - 1; i++)
		{
			par3List.add(new ItemStack(this, 1, i));
		}
	}

	@Override
	public String getTextureFile()
	{
		return LiquidMechanics.BLOCK_TEXTURE_FILE;
	}

	@Override
	public String getItemName()
	{
		return "Pipes";
	}

	@Override
	public boolean onItemUse(ItemStack itemstack, EntityPlayer player, World world, int x, int y, int z, int side, float par8, float par9, float par10)
	{
		int blockID = world.getBlockId(x, y, z);
		spawnID = LiquidMechanics.blockReleaseValve.blockID;
		int angle = MathHelper.floor_double((player.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;
		if (blockID == Block.snow.blockID)
		{
			side = 1;
		}
		else if (blockID != Block.vine.blockID && blockID != Block.tallGrass.blockID && blockID != Block.deadBush.blockID)
		{
			if (side == 0)
			{
				--y;
			}

			if (side == 1)
			{
				++y;
			}

			if (side == 2)
			{
				--z;
			}

			if (side == 3)
			{
				++z;
			}

			if (side == 4)
			{
				--x;
			}

			if (side == 5)
			{
				++x;
			}
		}

		if (LiquidMechanics.blockPipe.canPlaceBlockAt(world, x, y, z))
		{
			Block var9 = Block.blocksList[this.spawnID];
			world.editingBlocks = true;
			if (world.setBlockWithNotify(x, y, z, var9.blockID))
			{
				if (world.getBlockId(x, y, z) == var9.blockID)
				{

					Block.blocksList[this.spawnID].onBlockAdded(world, x, y, z);
					Block.blocksList[this.spawnID].onBlockPlacedBy(world, x, y, z, player);
					TileEntity blockEntity = world.getBlockTileEntity(x, y, z);
					if (blockEntity instanceof TileEntityReleaseValve)
					{
						TileEntityReleaseValve pipeEntity = (TileEntityReleaseValve) blockEntity;
						Liquid dm = Liquid.getLiquid(itemstack.getItemDamage());
						pipeEntity.setType(dm);
						pipeEntity.tank.setLiquid(Liquid.getStack(dm, 1));
						world.setBlockMetadata(x, y, z, dm.ordinal() & 15);
					}
				}

				--itemstack.stackSize;
				world.editingBlocks = false;
				return true;
			}
		}
		world.editingBlocks = false;
		return false;
	}

}