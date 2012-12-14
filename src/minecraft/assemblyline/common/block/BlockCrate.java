package assemblyline.common.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import universalelectricity.prefab.UETab;

/**
 * A block that allows the placement of mass amount of a specific item within it.
 * It will be allowed to go on Conveyor Belts
 * @author Calclavia
 *
 */
public class BlockCrate extends Block
{
	public BlockCrate(int par1)
	{
		super(par1, Material.iron);
		this.blockIndexInTexture = 59;
		this.setBlockName("create");
		this.setCreativeTab(UETab.INSTANCE);
	}

	/**
	 * Returns the block texture based on the side being looked at. Args: side
	 */
	public int getBlockTextureFromSide(int par1)
	{
		return par1 == 1 ? this.blockIndexInTexture - 16 : (par1 == 0 ? Block.planks.getBlockTextureFromSide(0) : (par1 != 2 && par1 != 4 ? this.blockIndexInTexture : this.blockIndexInTexture + 1));
	}

	/**
	 * Called upon block activation (right click on the block.)
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
