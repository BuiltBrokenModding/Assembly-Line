package assemblyline.common.machine;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import universalelectricity.core.UniversalElectricity;
import universalelectricity.prefab.UETab;
import assemblyline.client.render.BlockRenderingHandler;
import assemblyline.common.machine.detector.BlockFilterable;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * A block that manipulates item movement between inventories.
 * 
 * @author Calclavia
 * 
 */
public class BlockManipulator extends BlockFilterable
{
	public BlockManipulator(int id)
	{
		super("manipulator", id, UniversalElectricity.machine, UETab.INSTANCE);
		this.setBlockBounds(0, 0, 0, 1, 0.3f, 1);
	}

	@Override
	public AxisAlignedBB getSelectedBoundingBoxFromPool(World par1World, int par2, int par3, int par4)
	{
		return AxisAlignedBB.getAABBPool().addOrModifyAABBInPool((double) par2, (double) par3, (double) par4, (double) par2 + 1, (double) par3 + 1, (double) par4 + 1);
	}

	@Override
	public boolean onSneakMachineActivated(World world, int x, int y, int z, EntityPlayer entityPlayer, int side, float hitX, float hitY, float hitZ)
	{
		TileEntity tileEntity = world.getBlockTileEntity(x, y, z);

		if (tileEntity instanceof TileEntityManipulator)
		{
			((TileEntityManipulator) tileEntity).selfPulse = !((TileEntityManipulator) tileEntity).selfPulse;
		}

		return true;
	}

	@Override
	public boolean onSneakUseWrench(World world, int x, int y, int z, EntityPlayer par5EntityPlayer, int side, float hitX, float hitY, float hitZ)
	{
		TileEntity tileEntity = world.getBlockTileEntity(x, y, z);

		if (tileEntity instanceof TileEntityManipulator)
		{
			((TileEntityManipulator) tileEntity).toggleOutput();
			return true;
		}

		return false;
	}

	@Override
	public TileEntity createNewTileEntity(World var1, int metadata)
	{
		return new TileEntityManipulator();
	}

	@SideOnly(Side.CLIENT)
	@Override
	public int getRenderType()
	{
		return BlockRenderingHandler.BLOCK_RENDER_ID;
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
}
