package liquidmechanics.common.item;

import java.util.List;

import liquidmechanics.common.LiquidMechanics;
import liquidmechanics.common.TabLiquidMechanics;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class ItemValve extends ItemBlock
{
	int index = 26;
	private int spawnID;

	public ItemValve(int id)
	{
		super(id);
		this.setMaxDamage(0);
		this.setHasSubtypes(true);
		this.setIconIndex(10);
		this.setItemName("Machine");
		this.setCreativeTab(TabLiquidMechanics.INSTANCE);
	}

	@Override
	public int getIconFromDamage(int par1)
	{

		return par1 + index;
	}

	@Override
	public String getItemNameIS(ItemStack itemstack)
	{
		return itemstack.getItemDamage() == 0 ? "Pump" : "Conderser";// itemstack.getItemDamage() ==
																		// 4 ?
																		// "Condenser":"Unknown";
	}

	@Override
	public void getSubItems(int par1, CreativeTabs par2CreativeTabs, List par3List)
	{

		par3List.add(new ItemStack(this, 1, 0));
		par3List.add(new ItemStack(this, 1, 4));

	}

	public String getTextureFile()
	{
		return LiquidMechanics.ITEM_TEXTURE_FILE;
	}

	@Override
	public String getItemName()
	{
		return "Machines";
	}

	@Override
	public boolean onItemUse(ItemStack itemStack, EntityPlayer player, World par3World, int par4, int par5, int par6, int par7, float par8, float par9, float par10)
	{
		int blockID = par3World.getBlockId(par4, par5, par6);
		spawnID = LiquidMechanics.blockReleaseValve.blockID;
		if (blockID == Block.snow.blockID)
		{
			par7 = 1;
		}
		else if (blockID != Block.vine.blockID && blockID != Block.tallGrass.blockID && blockID != Block.deadBush.blockID)
		{
			if (par7 == 0)
			{
				--par5;
			}

			if (par7 == 1)
			{
				++par5;
			}

			if (par7 == 2)
			{
				--par6;
			}

			if (par7 == 3)
			{
				++par6;
			}

			if (par7 == 4)
			{
				--par4;
			}

			if (par7 == 5)
			{
				++par4;
			}
		}

		if (LiquidMechanics.blockPipe.canPlaceBlockAt(par3World, par4, par5, par6))
		{
			Block var9 = Block.blocksList[this.spawnID];
			par3World.editingBlocks = true;
			int angle = MathHelper.floor_double((double) (player.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;
			if (par3World.setBlockAndMetadataWithNotify(par4, par5, par6, var9.blockID, angle + itemStack.getItemDamage()))
			{
				if (par3World.getBlockId(par4, par5, par6) == var9.blockID)
				{

					Block.blocksList[this.spawnID].onBlockAdded(par3World, par4, par5, par6);
					Block.blocksList[this.spawnID].onBlockPlacedBy(par3World, par4, par5, par6, player);
					TileEntity blockEntity = par3World.getBlockTileEntity(par4, par5, par6);

				}

				--itemStack.stackSize;
				par3World.editingBlocks = false;
				return true;
			}
		}
		par3World.editingBlocks = false;
		return false;
	}

}