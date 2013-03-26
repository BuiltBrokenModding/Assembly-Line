package fluidmech.common.machines.pipes;

import hydraulic.api.IFluidNetworkPart;

import java.util.List;

import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import universalelectricity.core.block.IConductor;
import universalelectricity.prefab.block.BlockAdvanced;
import fluidmech.common.FluidMech;
import fluidmech.common.TabFluidMech;

public class BlockNetworkPipe extends BlockAdvanced
{
	public BlockNetworkPipe(int id)
	{
		super(id, Material.iron);
		this.setBlockBounds(0.30F, 0.30F, 0.30F, 0.70F, 0.70F, 0.70F);
		this.setHardness(1f);
		this.setCreativeTab(TabFluidMech.INSTANCE);
		this.setUnlocalizedName("lmNPipe");
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

		if (tileEntity instanceof IFluidNetworkPart)
		{
			((IFluidNetworkPart) tileEntity).updateAdjacentConnections();
		}
	}

	
	@Override
	public void onNeighborBlockChange(World world, int x, int y, int z, int blockID)
	{
		TileEntity tileEntity = world.getBlockTileEntity(x, y, z);

		if (tileEntity instanceof IFluidNetworkPart)
		{
			((IFluidNetworkPart) tileEntity).updateAdjacentConnections();
		}
	}
	
	@Override
	public TileEntity createNewTileEntity(World var1)
	{
		return new TileEntityNetworkPipe();
	}

	@Override
	public ItemStack getPickBlock(MovingObjectPosition target, World world, int x, int y, int z)
	{
		int meta = world.getBlockMetadata(x, y, z);
		return new ItemStack(FluidMech.blockNetPipe, 1, meta);
	}

	@Override
	public void getSubBlocks(int par1, CreativeTabs par2CreativeTabs, List par3List)
	{
		for (int i = 0; i < 16; i++)
		{
			par3List.add(new ItemStack(par1, 1, i));
		}
	}
}
