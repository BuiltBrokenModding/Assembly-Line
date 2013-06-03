package dark.fluid.common.pipes;

import hydraulic.api.FluidRestrictionHandler;
import hydraulic.api.INetworkPipe;

import java.util.List;

import dark.fluid.common.FluidMech;
import dark.fluid.common.TabFluidMech;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import net.minecraftforge.liquids.ILiquidTank;
import universalelectricity.prefab.block.BlockAdvanced;

public class BlockPipe extends BlockAdvanced
{
	public BlockPipe(int id)
	{
		super(id, Material.iron);
		this.setBlockBounds(0.30F, 0.30F, 0.30F, 0.70F, 0.70F, 0.70F);
		this.setHardness(1f);
		this.setCreativeTab(TabFluidMech.INSTANCE);
		this.setUnlocalizedName("lmPipe");
		this.setResistance(3f);
		
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
	public int damageDropped(int par1)
	{
		return par1;
	}

	@Override
	public int getRenderType()
	{
		return -1;
	}

	@Override
	public void onBlockAdded(World world, int x, int y, int z)
	{
		super.onBlockAdded(world, x, y, z);

		TileEntity tileEntity = world.getBlockTileEntity(x, y, z);

		if (tileEntity instanceof INetworkPipe)
		{
			((INetworkPipe) tileEntity).updateNetworkConnections();
		}
	}

	@Override
	public void onNeighborBlockChange(World world, int x, int y, int z, int blockID)
	{
		TileEntity tileEntity = world.getBlockTileEntity(x, y, z);

		if (tileEntity instanceof INetworkPipe)
		{
			((INetworkPipe) tileEntity).updateNetworkConnections();
		}
	}

	@Override
	public TileEntity createNewTileEntity(World var1)
	{
		if(this.blockID == FluidMech.blockGenPipe.blockID)
		{
			return new TileEntityGenericPipe();
		}
		return new TileEntityPipe();
	}

	@Override
	public ItemStack getPickBlock(MovingObjectPosition target, World world, int x, int y, int z)
	{
		int meta = world.getBlockMetadata(x, y, z);
		int blockID = world.getBlockId(x, y, z);
		return new ItemStack(blockID, 1, meta);
	}

	@Override
	public void getSubBlocks(int par1, CreativeTabs par2CreativeTabs, List par3List)
	{
		for (int i = 0; i < 16; i++)
		{
			if (this.blockID == FluidMech.blockGenPipe.blockID || FluidRestrictionHandler.hasRestrictedStack(i))
			{
				par3List.add(new ItemStack(par1, 1, i));
			}
		}
	}

	@Override
	public void breakBlock(World world, int x, int y, int z, int par5, int par6)
	{
		super.breakBlock(world, x, y, z, par5, par6);
		TileEntity entity = world.getBlockTileEntity(x, y, z);
		if (entity instanceof TileEntityPipe)
		{
			ILiquidTank tank = ((TileEntityPipe) entity).getTank();
			if (tank != null && tank.getLiquid() != null && tank.getLiquid().amount > 0)
			{
				if (tank.getLiquid().itemID == Block.waterStill.blockID)
				{
					world.setBlock(x, y, z, Block.waterStill.blockID);
				}
				if (tank.getLiquid().itemID == Block.lavaStill.blockID)
				{
					world.setBlock(x, y, z, Block.lavaStill.blockID);
				}
			}
		}
	}
}
