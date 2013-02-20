package fluidmech.common.block;

import java.util.Random;

import fluidmech.common.FluidMech;
import fluidmech.common.TabLiquidMechanics;
import fluidmech.common.tileentity.TileEntityReleaseValve;

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
		super("eValve",par1, Material.iron,TabLiquidMechanics.INSTANCE);
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
	public void onBlockAdded(World par1World, int x, int y, int z)
	{
		this.checkForPower(par1World, x, y, z);
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
		this.checkForPower(par1World, x, y, z);

	}

	@Override
	public ItemStack getPickBlock(MovingObjectPosition target, World world, int x, int y, int z)
	{
		return new ItemStack(FluidMech.blockReleaseValve, 1, 0);
	}

	public static void checkForPower(World world, int x, int y, int z)
	{
		TileEntity tileEntity = world.getBlockTileEntity(x, y, z);

		if (tileEntity instanceof TileEntityReleaseValve)
		{
			boolean powered = ((TileEntityReleaseValve) tileEntity).isPowered;
			boolean beingPowered = world.isBlockIndirectlyGettingPowered(x, y, z) || world.isBlockGettingPowered(x, y, z);
			if (powered && !beingPowered)
			{
				((IRedstoneReceptor) world.getBlockTileEntity(x, y, z)).onPowerOff();
			}
			else if (!powered && beingPowered)
			{
				((IRedstoneReceptor) world.getBlockTileEntity(x, y, z)).onPowerOn();
			}
		}
	}

}
