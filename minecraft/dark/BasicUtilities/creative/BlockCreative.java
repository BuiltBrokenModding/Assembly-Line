package dark.BasicUtilities.creative;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

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
