package assemblyline.common.imprinter;

import java.util.Random;

import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Icon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import assemblyline.common.AssemblyLine;
import assemblyline.common.CommonProxy;
import assemblyline.common.TabAssemblyLine;
import assemblyline.common.block.BlockALMachine;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockImprinter extends BlockALMachine
{
	Icon imprinter_side;
	Icon imprinter_top;
	Icon imprinter_bottom;

	public BlockImprinter(int id, int texture)
	{
		super(id, Material.wood);
		this.setUnlocalizedName("imprinter");
		this.setCreativeTab(TabAssemblyLine.INSTANCE);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerIcons(IconRegister iconReg)
	{
		this.imprinter_side = iconReg.registerIcon(AssemblyLine.TEXTURE_NAME_PREFIX + "imprinter_side");
		this.imprinter_top = iconReg.registerIcon(AssemblyLine.TEXTURE_NAME_PREFIX + "imprinter_top");
		this.imprinter_bottom = iconReg.registerIcon(AssemblyLine.TEXTURE_NAME_PREFIX + "imprinter_bottom");
	}

	@Override
	@SideOnly(Side.CLIENT)
	public Icon getBlockTexture(IBlockAccess world, int x, int y, int z, int side)
	{
		return getIcon(side, 0);
	}

	/**
	 * Returns the block texture based on the side being looked at. Args: side
	 */
	@Override
	public Icon getIcon(int side, int meta)
	{
		if (side == 1)
		{
			return this.imprinter_top;

		}
		else if (side == 0)
		{
			return this.imprinter_bottom;

		}

		return this.imprinter_side;
	}

	/**
	 * Called upon block activation (right click on the block.)
	 */
	@Override
	public boolean onMachineActivated(World world, int x, int y, int z, EntityPlayer entityPlayer, int par6, float par7, float par8, float par9)
	{
		if (!world.isRemote)
		{
			entityPlayer.openGui(AssemblyLine.instance, CommonProxy.GUI_IMPRINTER, world, x, y, z);
		}

		return true;

	}

	@Override
	public void dropEntireInventory(World par1World, int x, int y, int z, int par5, int par6)
	{
		TileEntity tileEntity = par1World.getBlockTileEntity(x, y, z);

		if (tileEntity != null)
		{
			if (tileEntity instanceof TileEntityImprinter)
			{
				TileEntityImprinter inventory = (TileEntityImprinter) tileEntity;

				for (int i = 0; i < inventory.getSizeInventory(); ++i)
				{
					ItemStack itemStack = inventory.getStackInSlot(i);

					if (itemStack != null)
					{
						Random random = new Random();
						float var8 = random.nextFloat() * 0.8F + 0.1F;
						float var9 = random.nextFloat() * 0.8F + 0.1F;
						float var10 = random.nextFloat() * 0.8F + 0.1F;

						while (itemStack.stackSize > 0)
						{
							int var11 = random.nextInt(21) + 10;

							if (var11 > itemStack.stackSize)
							{
								var11 = itemStack.stackSize;
							}

							itemStack.stackSize -= var11;

							if (i != inventory.craftingOutputSlot)
							{
								EntityItem entityItem = new EntityItem(par1World, (x + var8), (y + var9), (z + var10), new ItemStack(itemStack.itemID, var11, itemStack.getItemDamage()));

								if (itemStack.hasTagCompound())
								{
									entityItem.getEntityItem().setTagCompound((NBTTagCompound) itemStack.getTagCompound().copy());
								}

								float var13 = 0.05F;
								entityItem.motionX = ((float) random.nextGaussian() * var13);
								entityItem.motionY = ((float) random.nextGaussian() * var13 + 0.2F);
								entityItem.motionZ = ((float) random.nextGaussian() * var13);
								par1World.spawnEntityInWorld(entityItem);
							}
						}
					}
				}
			}
		}
	}

	@Override
	public boolean onUseWrench(World par1World, int x, int y, int z, EntityPlayer par5EntityPlayer, int side, float hitX, float hitY, float hitZ)
	{
		TileEntity tileEntity = par1World.getBlockTileEntity(x, y, z);

		if (tileEntity instanceof TileEntityImprinter)
		{
			((TileEntityImprinter) tileEntity).searchInventories = !((TileEntityImprinter) tileEntity).searchInventories;
			par1World.markBlockForUpdate(x, y, z);
			return true;
		}

		return false;
	}

	@Override
	public TileEntity createNewTileEntity(World var1)
	{
		return new TileEntityImprinter();
	}
}
