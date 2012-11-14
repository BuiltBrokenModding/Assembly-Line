package dark.BasicUtilities.creative;

import net.minecraft.src.BlockContainer;
import net.minecraft.src.Material;
import net.minecraft.src.TileEntity;
import net.minecraft.src.World;

/**
 * A block that can only be accessed from the 
 * creative menu used to both test the mod
 * and other odd things
 * @author Rseifert
 *
 */
public class BlockCreative extends BlockContainer {

	protected BlockCreative(int par1) 
	{
		super(par1, Material.iron);
	}

	@Override
	public TileEntity createNewTileEntity(World var1) 
	{
		return null;
	}
	@Override
	public TileEntity createNewTileEntity(World var1, int meta) 
	{
		return null;
	}

}
