package assemblyline.common.block;

import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.util.Icon;
import net.minecraft.world.IBlockAccess;
import universalelectricity.prefab.block.BlockAdvanced;
import assemblyline.common.AssemblyLine;
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
	public Icon getIcon(int par1, int par2)
	{
		return this.machine_icon;
	}
	
}