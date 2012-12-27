package assemblyline.common.machine.filter;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import universalelectricity.prefab.BlockMachine;
import universalelectricity.prefab.UETab;
import assemblyline.common.AssemblyLine;
import assemblyline.common.CommonProxy;

public class BlockStamper extends BlockMachine
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
	@Override
	public boolean onMachineActivated(World world, int x, int y, int z, EntityPlayer entityPlayer, int par6, float par7, float par8, float par9)
	{
		if (!world.isRemote)
		{
			entityPlayer.openGui(AssemblyLine.instance, CommonProxy.GUI_STAMPER, world, x, y, z);
		}

		return true;

	}

}
