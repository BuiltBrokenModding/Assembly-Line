package assemblyline.machine;

import net.minecraft.src.Block;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.Material;
import net.minecraft.src.World;
import universalelectricity.prefab.UETab;

public class BlockArchitectTable extends Block
{
	public BlockArchitectTable(int par1)
	{
		super(par1, Material.wood);
		this.blockIndexInTexture = 59;
		this.setBlockName("ArchitectTable");
		this.setCreativeTab(UETab.INSTANCE);
	}

	/**
	 * Returns the block texture based on the side
	 * being looked at. Args: side
	 */
	public int getBlockTextureFromSide(int par1)
	{
		return par1 == 1 ? this.blockIndexInTexture - 16 : (par1 == 0 ? Block.planks.getBlockTextureFromSide(0) : (par1 != 2 && par1 != 4 ? this.blockIndexInTexture : this.blockIndexInTexture + 1));
	}

	/**
	 * Called upon block activation (right click
	 * on the block.)
	 */
	public boolean onBlockActivated(World par1World, int par2, int par3, int par4, EntityPlayer par5EntityPlayer, int par6, float par7, float par8, float par9)
	{
		if (par1World.isRemote)
		{
			return true;
		}
		else
		{
			par5EntityPlayer.displayGUIWorkbench(par2, par3, par4);
			return true;
		}
	}

}
