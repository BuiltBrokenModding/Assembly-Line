package fluidmech.common.block;

import java.util.Random;

import fluidmech.common.FluidMech;
import fluidmech.common.TabFluidMech;
import fluidmech.common.machines.TileEntityReleaseValve;

import net.minecraft.block.material.Material;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import universalelectricity.prefab.BlockMachine;
import universalelectricity.prefab.implement.IRedstoneReceptor;

public class BlockReleaseValve extends BlockMachine
{
	public BlockReleaseValve(int par1)
	{
		super("eValve",par1, Material.iron,TabFluidMech.INSTANCE);
		this.setHardness(1f);
		this.setResistance(5f);
		this.setTextureFile(FluidMech.BLOCK_TEXTURE_FILE);
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
	public int getBlockTextureFromSideAndMetadata(int side, int meta)
	{
		return 0;
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
