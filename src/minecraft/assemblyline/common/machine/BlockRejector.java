package assemblyline.common.machine;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import universalelectricity.core.UniversalElectricity;
import assemblyline.client.render.BlockRenderingHandler;
import assemblyline.common.TabAssemblyLine;
import assemblyline.common.machine.imprinter.BlockImprintable;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockRejector extends BlockImprintable
{
	public BlockRejector(int id)
	{
		super("rejector", id, UniversalElectricity.machine, TabAssemblyLine.INSTANCE);
	}

	@Override
	public TileEntity createNewTileEntity(World var1, int metadata)
	{
		return new TileEntityRejector();
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

	@SideOnly(Side.CLIENT)
	@Override
	public int getRenderType()
	{
		return BlockRenderingHandler.BLOCK_RENDER_ID;
	}

}
