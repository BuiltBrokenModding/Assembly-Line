package assemblyline.common.block;

import ic2.api.Items;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.Icon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import universalelectricity.prefab.block.BlockAdvanced;
import assemblyline.common.AssemblyLine;
import buildcraft.api.tools.IToolWrench;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockALMachine extends BlockAdvanced
{
	public Icon machine_icon;

	public BlockALMachine(int id, Material material)
	{
		super(id, material);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerIcons(IconRegister iconReg)
	{
		this.machine_icon = iconReg.registerIcon(AssemblyLine.TEXTURE_NAME_PREFIX + "machine");
	}

	@Override
	@SideOnly(Side.CLIENT)
	public Icon getBlockTexture(IBlockAccess par1iBlockAccess, int par2, int par3, int par4, int par5)
	{
		return this.machine_icon;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public Icon getBlockTextureFromSideAndMetadata(int par1, int par2)
	{
		return this.machine_icon;
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ)
	{
		/**
		 * Check if the player is holding a wrench or an electric item. If so, do not open the GUI.
		 */
		if (player.inventory.getCurrentItem() != null)
		{
			System.out.println(player.inventory.getCurrentItem());
			if (isHoldingWrench(player))
			{
				if (player.isSneaking())
				{
					if (this.onSneakUseWrench(world, x, y, z, player, side, hitX, hitY, hitZ))
						return true;
					if (this.onSneakMachineActivated(world, x, y, z, player, side, hitX, hitY, hitZ))
						return true;
				}
				if (this.onUseWrench(world, x, y, z, player, side, hitX, hitY, hitZ))
					return true;
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
			return (AssemblyLine.ic2Wrench != null && player.getCurrentEquippedItem().isItemEqual(AssemblyLine.ic2Wrench) || player.getCurrentEquippedItem().getItem() instanceof IToolWrench);
		}

		return false;

	}
}