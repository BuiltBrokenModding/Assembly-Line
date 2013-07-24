package dark.fluid.common.machines;

import dark.fluid.common.FluidMech;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.util.Icon;
import net.minecraftforge.fluids.BlockFluidFinite;
import net.minecraftforge.fluids.Fluid;

public class BlockFluid extends BlockFluidFinite
{
	Icon flowing;
	Icon still;
	Fluid fluid;

	public BlockFluid(Fluid fluid, int id)
	{
		super(FluidMech.CONFIGURATION.getBlock(fluid.getName(), id).getInt(), fluid, Material.water);
		this.fluid = fluid;
	}

	@Override
	public void registerIcons(IconRegister par1IconRegister)
	{
		this.flowing = par1IconRegister.registerIcon(FluidMech.instance.PREFIX + this.getUnlocalizedName().replace("tile.", "") + "_flowing");
		this.still = par1IconRegister.registerIcon(FluidMech.instance.PREFIX + this.getUnlocalizedName().replace("tile.", "") + "_still");
		fluid.setIcons(still, flowing);
	}

	@Override
	public Icon getIcon(int par1, int par2)
	{
		return still;
	}

}
