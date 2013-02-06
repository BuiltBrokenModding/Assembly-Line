package assemblyline.common.block;

import assemblyline.common.AssemblyLine;
import assemblyline.common.TabAssemblyLine;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;

public class BlockTurntable extends Block
{
	public BlockTurntable(int par1, int par2)
	{
		super(par1, par2, Material.piston);
		this.setCreativeTab(TabAssemblyLine.INSTANCE);
		this.setTextureFile(AssemblyLine.BLOCK_TEXTURE_PATH);
	}

	@Override
	public int getBlockTextureFromSide(int side)
	{
		if (side == 0)
		{
			return this.blockIndexInTexture;
		}
		else if (side == 1)
		{
			return this.blockIndexInTexture + 1;
		}
		
		return this.blockIndexInTexture + 2;
	}
}
