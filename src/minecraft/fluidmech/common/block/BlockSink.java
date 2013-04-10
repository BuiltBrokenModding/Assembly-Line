package fluidmech.common.block;

import hydraulic.helpers.FluidHelper;
import hydraulic.helpers.MetaGroup;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumArmorMaterial;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.liquids.LiquidContainerRegistry;
import net.minecraftforge.liquids.LiquidStack;
import universalelectricity.prefab.block.BlockAdvanced;
import universalelectricity.prefab.tile.TileEntityAdvanced;
import fluidmech.client.render.BlockRenderHelper;
import fluidmech.common.TabFluidMech;
import fluidmech.common.tiles.TileEntitySink;

public class BlockSink extends BlockAdvanced
{
	public BlockSink(int par1)
	{
		super(par1, Material.iron);
		this.setUnlocalizedName("lmSink");
		this.setCreativeTab(TabFluidMech.INSTANCE);
		this.setResistance(4f);
		this.setHardness(4f);
	}

	@Override
	public TileEntity createNewTileEntity(World var1)
	{
		return new TileEntitySink();
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

			if (tileEntity instanceof TileEntitySink)
			{
				TileEntitySink tank = (TileEntitySink) tileEntity;

				// Handle filled containers
				if (liquid != null)
				{
					if (current.isItemEqual(new ItemStack(Item.potion)))
					{
						liquid = new LiquidStack(liquid.itemID, (LiquidContainerRegistry.BUCKET_VOLUME / 4), liquid.itemMeta);
					}
					int filled = tank.fill(ForgeDirection.UNKNOWN, liquid, true);

					if (filled != 0 && !entityplayer.capabilities.isCreativeMode)
					{
						entityplayer.inventory.setInventorySlotContents(entityplayer.inventory.currentItem, FluidHelper.consumeItem(current));
					}

					return true;

					// Handle empty containers
				}
				else
				{

					if (current.getItem() instanceof ItemArmor && ((ItemArmor) current.getItem()).getArmorMaterial() == EnumArmorMaterial.CLOTH)
					{
						ItemArmor var13 = (ItemArmor) current.getItem();
						var13.removeColor(current);
						return true;
					}
					LiquidStack stack = tank.getStoredLiquid();
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
							tank.drain(null, ammount, true);
							return true;
						}
					}
				}
			}
		}

		return false;

	}

	@Override
	public boolean onUseWrench(World par1World, int x, int y, int z, EntityPlayer par5EntityPlayer, int side, float hitX, float hitY, float hitZ)
	{
		int meta = par1World.getBlockMetadata(x, y, z);
		int g = MetaGroup.getGrouping(meta);
		TileEntity ent = par1World.getBlockTileEntity(x, y, z);
		int angle = MathHelper.floor_double((par5EntityPlayer.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;

		if (meta == (g * 4) + 3)
		{
			par1World.setBlockMetadataWithNotify(x, y, z, (g * 4), 3);
			return true;
		}
		else
		{
			par1World.setBlockMetadataWithNotify(x, y, z, meta + 1, 3);
			return true;
		}
		// return false;
	}

	@Override
	public void onBlockPlacedBy(World world, int x, int y, int z, EntityLiving par5EntityLiving, ItemStack itemStack)
	{
		int meta = world.getBlockMetadata(x, y, z);
		int angle = MathHelper.floor_double((par5EntityLiving.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;
		TileEntity ent = world.getBlockTileEntity(x, y, z);

		world.setBlockMetadataWithNotify(x, y, z, angle + MetaGroup.getGroupStartMeta(MetaGroup.getGrouping(meta)), 3);
		if (ent instanceof TileEntityAdvanced)
		{
			((TileEntityAdvanced) world.getBlockTileEntity(x, y, z)).initiate();
		}

		world.notifyBlocksOfNeighborChange(x, y, z, this.blockID);
	}

	@Override
	public ItemStack getPickBlock(MovingObjectPosition target, World world, int x, int y, int z)
	{
		int meta = world.getBlockMetadata(x, y, z);

		return new ItemStack(this, 1, 0);

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
}
