package fluidmech.common.pump;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Icon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import universalelectricity.prefab.block.BlockAdvanced;
import fluidmech.common.FluidMech;
import fluidmech.common.TabFluidMech;

public class BlockConstructionPump extends BlockAdvanced
{
	Icon inputIcon;
	Icon outputIcon;

	public BlockConstructionPump(int id)
	{
		super(id, Material.iron);
		this.setUnlocalizedName("lmConPump");
		this.setCreativeTab(TabFluidMech.INSTANCE);
		this.setHardness(1f);
		this.setResistance(5f);
	}

	@Override
	public void registerIcons(IconRegister par1IconRegister)
	{
		this.blockIcon = par1IconRegister.registerIcon(FluidMech.TEXTURE_NAME_PREFIX + "ironMachineSide");
		this.inputIcon = par1IconRegister.registerIcon(FluidMech.TEXTURE_NAME_PREFIX + "inputMachineSide");
		this.outputIcon = par1IconRegister.registerIcon(FluidMech.TEXTURE_NAME_PREFIX + "outputMachineSide");
	}

	@Override
	public Icon getBlockTextureFromSideAndMetadata(int par1, int par2)
	{
		return par1 != 1 && par1 != 0 ? this.blockIcon : this.inputIcon;
	}

	@Override
	public Icon getBlockTexture(IBlockAccess world, int x, int y, int z, int side)
	{
		TileEntity entity = world.getBlockTileEntity(x, y, z);
		ForgeDirection dir = ForgeDirection.getOrientation(side);
		if (entity instanceof TileEntityConstructionPump)
		{

			if (dir == ((TileEntityConstructionPump) entity).getFacing(false))
			{
				return this.outputIcon;
			}
			if (dir == ((TileEntityConstructionPump) entity).getFacing(true))
			{
				return this.inputIcon;
			}
		}
		return this.blockIcon;
	}

	@Override
	public boolean isOpaqueCube()
	{
		return false;
	}

	@Override
	public boolean renderAsNormalBlock()
	{
		return true;
	}

	@Override
	public int damageDropped(int meta)
	{
		return 0;
	}

	@Override
	public ItemStack getPickBlock(MovingObjectPosition target, World world, int x, int y, int z)
	{
		return new ItemStack(FluidMech.blockConPump, 1, 0);
	}

	@Override
	public void onBlockPlacedBy(World world, int x, int y, int z, EntityLiving p, ItemStack itemStack)
	{
	}

	@Override
	public TileEntity createNewTileEntity(World var1)
	{
		return new TileEntityConstructionPump();
	}

	@Override
	public void getSubBlocks(int par1, CreativeTabs par2CreativeTabs, List par3List)
	{
		par3List.add(new ItemStack(par1, 1, 0));
	}

	public boolean onSneakUseWrench(World world, int x, int y, int z, EntityPlayer entityPlayer, int side, float hitX, float hitY, float hitZ)
	{
		if (!world.isRemote)
		{
			world.setBlockMetadataWithNotify(x, y, z, side, 3);
			return true;
		}
		return this.onUseWrench(world, x, y, z, entityPlayer, side, hitX, hitY, hitZ);
	}
}
