package assemblyline.common.machine.filter;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import universalelectricity.prefab.UETab;

public class BlockStamper extends Block
{
	public BlockStamper(int id, int texture)
	{
		super(id, Material.wood);
		this.blockIndexInTexture = 59;
		this.setBlockName("stamper");
		this.setCreativeTab(UETab.INSTANCE);
	}

	/**
	 * Returns the block texture based on the side being looked at. Args: side
	 */
	public int getBlockTextureFromSide(int par1)
	{
		return Block.blockSteel.blockIndexInTexture;
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
