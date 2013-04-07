package fluidmech.common.pump;

import fluidmech.common.TabFluidMech;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import universalelectricity.prefab.block.BlockAdvanced;

public class BlockDrain extends BlockAdvanced
{

	public BlockDrain(int id)
	{
		super(id, Material.iron);
		this.setCreativeTab(TabFluidMech.INSTANCE);
		this.setUnlocalizedName("lmDrain");
	}

	@Override
	public TileEntity createNewTileEntity(World var1)
	{
		return new TileEntityDrain();
	}
}
