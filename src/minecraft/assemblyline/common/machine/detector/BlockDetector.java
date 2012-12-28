package assemblyline.common.machine.detector;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import universalelectricity.core.UniversalElectricity;
import universalelectricity.prefab.UETab;
import assemblyline.common.AssemblyLine;

/**
 * @author Briman0094
 */
public class BlockDetector extends BlockFilterable
{
	public BlockDetector(int blockID, int texture)
	{
		super("detector", blockID, UniversalElectricity.machine, UETab.INSTANCE);
		this.blockIndexInTexture = texture;
		this.setTextureFile(AssemblyLine.BLOCK_TEXTURE_PATH);
	}

	@Override
	public int getBlockTexture(IBlockAccess iBlockAccess, int x, int y, int z, int side)
	{
		TileEntity tileEntity = iBlockAccess.getBlockTileEntity(x, y, z);
		if (tileEntity instanceof TileEntityDetector)
		{
			if (side == ForgeDirection.DOWN.ordinal())
			{
				if (((TileEntityDetector) tileEntity).isInverted())
				{
					return this.blockIndexInTexture + 2;

				}
				else
				{
					return this.blockIndexInTexture + 1;
				}
			}
		}

		return this.blockIndexInTexture;
	}

	@Override
	public int getBlockTextureFromSideAndMetadata(int side, int metadata)
	{
		if (side == ForgeDirection.DOWN.ordinal()) { return this.blockIndexInTexture + 1; }

		return this.blockIndexInTexture;
	}

	@Override
	public boolean onSneakUseWrench(World world, int x, int y, int z, EntityPlayer par5EntityPlayer, int side, float hitX, float hitY, float hitZ)
	{
		TileEntity tileEntity = world.getBlockTileEntity(x, y, z);

		if (tileEntity != null)
		{
			if (tileEntity instanceof TileEntityDetector)
			{
				((TileEntityDetector) tileEntity).toggleInversion();
				world.markBlockForRenderUpdate(x, y, z);
			}
		}

		return true;
	}

	@Override
	public void onNeighborBlockChange(World world, int x, int y, int z, int blockID)
	{
		if (!canBlockStay(world, x, y, z))
		{
			this.dropBlockAsItem(world, x, y, z, world.getBlockMetadata(x, y, z), 0);
			world.setBlockWithNotify(x, y, z, 0);
		}
	}

	@Override
	public boolean isOpaqueCube()
	{
		return false;
	}

	/*
	 * @Override public boolean renderAsNormalBlock() { return false; }
	 * 
	 * @SideOnly(Side.CLIENT)
	 * 
	 * @Override public int getRenderType() { return BlockRenderingHandler.BLOCK_RENDER_ID; }
	 */

	@Override
	public boolean isProvidingStrongPower(IBlockAccess world, int x, int y, int z, int direction)
	{
		if (direction == ForgeDirection.DOWN.ordinal())
		{
			TileEntity tileEntity = world.getBlockTileEntity(x, y, z);
			if (tileEntity != null)
			{
				if (tileEntity instanceof TileEntityDetector) { return ((TileEntityDetector) tileEntity).isPoweringTo(ForgeDirection.getOrientation(direction)); }
			}
		}

		return false;
	}

	@Override
	public boolean isProvidingWeakPower(IBlockAccess world, int x, int y, int z, int direction)
	{
		if (direction != ForgeDirection.DOWN.ordinal() && direction != ForgeDirection.UP.ordinal())
		{
			TileEntity tileEntity = world.getBlockTileEntity(x, y, z);

			if (tileEntity != null)
			{
				if (tileEntity instanceof TileEntityDetector) { return ((TileEntityDetector) tileEntity).isPoweringTo(ForgeDirection.getOrientation(direction)); }
			}
		}

		return false;
	}

	@Override
	public TileEntity createNewTileEntity(World world)
	{
		return new TileEntityDetector();
	}

}
