package liquidmechanics.common.block;

import liquidmechanics.client.render.BlockRenderHelper;
import liquidmechanics.common.LiquidMechanics;
import liquidmechanics.common.TabLiquidMechanics;
import liquidmechanics.common.handlers.DefautlLiquids;
import liquidmechanics.common.tileentity.TileEntityPump;
import liquidmechanics.common.tileentity.TileEntityTank;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.liquids.LiquidContainerRegistry;
import net.minecraftforge.liquids.LiquidStack;

public class BlockMachine extends BlockContainer
{
	public BlockMachine(int id)
	{
		super(id, Material.iron);
		this.setBlockName("Machine");
		this.setCreativeTab(TabLiquidMechanics.INSTANCE);
		this.setRequiresSelfNotify();
		this.blockIndexInTexture = 26;
		this.setHardness(1f);
		this.setResistance(5f);
	}

	@Override
	public boolean isOpaqueCube()
	{
		return false;
	}

	@Override
	public boolean renderAsNormalBlock()
	{
		return false;
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer entityplayer, int side, float hitX, float hitY, float hitZ)
	{
		ItemStack current = entityplayer.inventory.getCurrentItem();
		if (current != null)
		{

			LiquidStack liquid = LiquidContainerRegistry.getLiquidForFilledItem(current);

			TileEntity tileEntity = world.getBlockTileEntity(x, y, z);

			if (tileEntity instanceof TileEntityTank)
			{
				TileEntityTank tank = (TileEntityTank) tileEntity;

				// Handle filled containers
				if (liquid != null)
				{
					int filled = tank.fill(ForgeDirection.UNKNOWN, liquid, true);

					if (filled != 0 && !entityplayer.capabilities.isCreativeMode)
					{
						entityplayer.inventory.setInventorySlotContents(entityplayer.inventory.currentItem, DefautlLiquids.consumeItem(current));
					}

					return true;

					// Handle empty containers
				}
				else
				{

					LiquidStack stack = tank.tank.getLiquid();
					if (stack != null)
					{
						ItemStack liquidItem = LiquidContainerRegistry.fillLiquidContainer(stack, current);

						liquid = LiquidContainerRegistry.getLiquidForFilledItem(liquidItem);

						if (liquid != null)
						{
							if (!entityplayer.capabilities.isCreativeMode)
							{
								if (current.stackSize > 1)
								{
									if (!entityplayer.inventory.addItemStackToInventory(liquidItem))
										return false;
									else
									{
										entityplayer.inventory.setInventorySlotContents(entityplayer.inventory.currentItem, DefautlLiquids.consumeItem(current));
									}
								}
								else
								{
									entityplayer.inventory.setInventorySlotContents(entityplayer.inventory.currentItem, DefautlLiquids.consumeItem(current));
									entityplayer.inventory.setInventorySlotContents(entityplayer.inventory.currentItem, liquidItem);
								}
							}
							tank.tank.drain(liquid.amount, true);
							return true;
						}
					}
				}
			}
		}

		return false;

	}
	@Override
	public int getRenderType()
	{
		return BlockRenderHelper.renderID;
	}

	@Override
	public int damageDropped(int meta)
	{
		if (meta < 4) { return 0; }
		return meta;
	}

	@Override
	public ItemStack getPickBlock(MovingObjectPosition target, World world, int x, int y, int z)
	{
		int meta = world.getBlockMetadata(x, y, z);
		if (meta < 4)
			new ItemStack(LiquidMechanics.blockMachine, 1, 0);
		// if(meta == 4) ;
		TileEntity ent = world.getBlockTileEntity(x, y, z);
		if (ent instanceof TileEntityTank)
			new ItemStack(LiquidMechanics.itemTank, 1, ((TileEntityTank) ent).type.ordinal());
		return null;
	}

	@Override
	public TileEntity createNewTileEntity(World var1, int meta)
	{
		if (meta < 4) { return new TileEntityPump(); }
		if (meta == 4)
		{
			// return new TileEntityCondenser();
		}
		if (meta == 5) { return new TileEntityTank(); }
		return null;
	}

	@Override
	public TileEntity createNewTileEntity(World var1)
	{
		return null;
	}
}
