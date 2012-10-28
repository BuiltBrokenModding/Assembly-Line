package assemblyline.crafting;

import net.minecraft.src.BlockContainer;
import net.minecraft.src.CreativeTabs;
import net.minecraft.src.Material;
import net.minecraft.src.TileEntity;
import net.minecraft.src.World;

public class BlockCrafter extends BlockContainer
{
	protected BlockCrafter(int par1) 
	{
		super(par1, Material.iron);
		this.setResistance(5.0f);
		this.setHardness(5.0f);
		this.setCreativeTab(CreativeTabs.tabTools);
	}

	@Override
	public TileEntity createNewTileEntity(World var1) {
		
		return null;
	}
	@Override
	public TileEntity createNewTileEntity(World var1, int meta) {
		if(meta >= 0 && meta < 4)
		{
			return new TileEntityAutoCrafter();
		}
		if(meta >= 4 && meta < 8)
		{
			return new TileEntityCraftingArm();
		}
		if(meta >= 8 && meta < 12)
		{
			
		}
		if(meta >= 12 && meta < 16)
		{
			
		}
		return null;
	}

}
