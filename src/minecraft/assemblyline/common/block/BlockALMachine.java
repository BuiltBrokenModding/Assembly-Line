package assemblyline.common.block;

import assemblyline.common.CommonProxy;
import universalelectricity.core.implement.IItemElectric;
import universalelectricity.prefab.BlockMachine;
import universalelectricity.prefab.implement.IToolConfigurator;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

public class BlockALMachine extends BlockMachine
{

	public BlockALMachine(int id, Material material)
	{
		super(id, material);
	}

	public BlockALMachine(int id, int textureIndex, Material material)
	{
		super(id, textureIndex, material);
	}

	@Deprecated
	public BlockALMachine(String string, int id, Material material)
	{
		this(id, material);
	}

	@Deprecated
	public BlockALMachine(String string, int id, Material material, CreativeTabs creativeTab)
	{
		this(string, id, material);
	}
	
	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ)
	{
		/**
		 * Check if the player is holding a wrench or an electric item. If so, do not open the GUI.
		 */
		if (player.inventory.getCurrentItem() != null)
		{
			if (CommonProxy.isHoldingBCWrench(player))
			{
				return this.onUseWrench(world, x, y, z, player, side, hitX, hitY, hitZ);
			}
		}
		
		return super.onBlockActivated(world, x, y, z, player, side, hitX, hitY, hitZ);
	}
	
}