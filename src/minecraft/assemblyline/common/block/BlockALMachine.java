package assemblyline.common.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ic2.api.Items;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.Icon;
import net.minecraft.world.World;
import universalelectricity.prefab.block.BlockAdvanced;
import buildcraft.api.tools.IToolWrench;

public class BlockALMachine extends BlockAdvanced
{
	public Icon machine_icon;
	public BlockALMachine(int id, Material material)
	{
		super(id, material);
	}
	
	@SideOnly(Side.CLIENT)
	@Override
	public void func_94332_a(IconRegister iconReg)
	{
		this.machine_icon = iconReg.func_94245_a("machine");
	}
	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ)
	{
		/**
		 * Check if the player is holding a wrench or an electric item. If so, do not open the GUI.
		 */
		if (player.inventory.getCurrentItem() != null)
		{
			if (isHoldingWrench(player))
			{
				if (player.isSneaking())
				{
					if (this.onSneakMachineActivated(world, x, y, z, player, side, hitX, hitY, hitZ))
						return true;
					if (this.onSneakUseWrench(world, x, y, z, player, side, hitX, hitY, hitZ))
						return true;
				}
				return this.onUseWrench(world, x, y, z, player, side, hitX, hitY, hitZ);
			}
		}
		return super.onBlockActivated(world, x, y, z, player, side, hitX, hitY, hitZ);
	}

	/**
	 * Checks if the player is holding a foreign wrench.
	 */
	public static boolean isHoldingWrench(EntityPlayer player)
	{
		if (player.getCurrentEquippedItem() != null)
		{
			return ((Items.getItem("wrench") != null && player.getCurrentEquippedItem().isItemEqual(Items.getItem("wrench"))) || player.getCurrentEquippedItem().getItem() instanceof IToolWrench);
		}

		return false;

	}
}