package dark.fluid.common.machines;


import java.util.List;

import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.liquids.LiquidContainerRegistry;
import net.minecraftforge.liquids.LiquidStack;
import universalelectricity.prefab.block.BlockAdvanced;
import dark.core.api.INetworkPart;
import dark.core.hydraulic.helpers.FluidHelper;
import dark.core.hydraulic.helpers.FluidRestrictionHandler;
import dark.fluid.client.render.BlockRenderHelper;
import dark.fluid.common.TabFluidMech;

public class BlockTank extends BlockAdvanced
{

	public BlockTank(int id)
	{
		super(id, Material.rock);
		this.setCreativeTab(TabFluidMech.INSTANCE);
		this.setUnlocalizedName("lmTank");
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
	public int getRenderType()
	{
		return BlockRenderHelper.renderID;
	}

	@Override
	public int damageDropped(int meta)
	{
		return meta;
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer entityplayer, int side, float hitX, float hitY, float hitZ)
	{
		if (entityplayer.isSneaking())
		{
			return false;
		}
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
					if (current.isItemEqual(new ItemStack(Item.potion)))
					{
						liquid = new LiquidStack(liquid.itemID, (LiquidContainerRegistry.BUCKET_VOLUME / 4), liquid.itemMeta);
					}
					int filled = tank.fill(ForgeDirection.getOrientation(side), liquid, true);

					if (filled != 0 && !entityplayer.capabilities.isCreativeMode)
					{
						entityplayer.inventory.setInventorySlotContents(entityplayer.inventory.currentItem, FluidHelper.consumeItem(current));
					}

					return true;

					// Handle empty containers
				}
				else
				{

					LiquidStack stack = tank.getTank().getLiquid();
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
										entityplayer.inventory.setInventorySlotContents(entityplayer.inventory.currentItem, FluidHelper.consumeItem(current));
									}
								}
								else
								{
									entityplayer.inventory.setInventorySlotContents(entityplayer.inventory.currentItem, FluidHelper.consumeItem(current));
									entityplayer.inventory.setInventorySlotContents(entityplayer.inventory.currentItem, liquidItem);
								}
							}
							int ammount = liquid.amount;
							if (current.isItemEqual(new ItemStack(Item.glassBottle)))
							{
								ammount = (LiquidContainerRegistry.BUCKET_VOLUME / 4);
							}
							tank.drain(ForgeDirection.getOrientation(side), ammount, true);
							return true;
						}
					}
				}
			}
		}

		return false;

	}

	@Override
	public TileEntity createNewTileEntity(World var1)
	{
		return new TileEntityTank();
	}

	@Override
	public ItemStack getPickBlock(MovingObjectPosition target, World world, int x, int y, int z)
	{
		int meta = world.getBlockMetadata(x, y, z);

		return new ItemStack(this, 1, meta);

	}

	@Override
	public void getSubBlocks(int par1, CreativeTabs par2CreativeTabs, List par3List)
	{
		for (int i = 0; i < 16; i++)
		{
			if (FluidRestrictionHandler.hasRestrictedStack(i))
			{
				par3List.add(new ItemStack(par1, 1, i));
			}
		}
	}

	@Override
	public void onBlockAdded(World world, int x, int y, int z)
	{
		super.onBlockAdded(world, x, y, z);

		TileEntity tileEntity = world.getBlockTileEntity(x, y, z);

		if (tileEntity instanceof INetworkPart)
		{
			((INetworkPart) tileEntity).updateNetworkConnections();
		}
	}

	@Override
	public void onNeighborBlockChange(World world, int x, int y, int z, int blockID)
	{
		TileEntity tileEntity = world.getBlockTileEntity(x, y, z);

		if (tileEntity instanceof INetworkPart)
		{
			((INetworkPart) tileEntity).updateNetworkConnections();
		}
	}
}
