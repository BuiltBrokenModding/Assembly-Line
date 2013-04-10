package fluidmech.common.block;

import java.util.Random;

import net.minecraft.block.material.Material;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import universalelectricity.prefab.block.BlockAdvanced;
import fluidmech.common.FluidMech;
import fluidmech.common.TabFluidMech;
import fluidmech.common.tiles.TileEntityReleaseValve;

public class BlockReleaseValve extends BlockAdvanced
{
	public BlockReleaseValve(int par1)
	{
		super(par1, Material.iron);
		this.setCreativeTab(TabFluidMech.INSTANCE);
		this.setUnlocalizedName("eValve");
		this.setHardness(1f);
		this.setResistance(5f);
	}

	@Override
	public TileEntity createNewTileEntity(World var1)
	{
		return new TileEntityReleaseValve();
	}

	@Override
	public boolean canConnectRedstone(IBlockAccess world, int x, int y, int z, int side)
	{
		return true;
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
		return -1;
	}

	@Override
	public int damageDropped(int meta)
	{
		return 0;
	}

	@Override
	public int quantityDropped(Random par1Random)
	{
		return 1;
	}

	@Override
	public void onNeighborBlockChange(World par1World, int x, int y, int z, int side)
	{
		super.onNeighborBlockChange(par1World, x, y, z, side);

	}

	@Override
	public ItemStack getPickBlock(MovingObjectPosition target, World world, int x, int y, int z)
	{
		return new ItemStack(FluidMech.blockReleaseValve, 1, 0);
	}
}
