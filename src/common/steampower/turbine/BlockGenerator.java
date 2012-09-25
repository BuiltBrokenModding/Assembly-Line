package steampower.turbine;

import java.util.ArrayList;

import net.minecraft.src.CreativeTabs;
import net.minecraft.src.ItemStack;
import net.minecraft.src.Material;
import net.minecraft.src.TileEntity;
import net.minecraft.src.World;

public class BlockGenerator extends universalelectricity.prefab.BlockMachine {

	public BlockGenerator(int id) {
		super("Generator", id, Material.iron);
		this.setCreativeTab(CreativeTabs.tabBlock);
	}
	@Override
	public void addCreativeItems(ArrayList itemList)
    { 
            itemList.add(new ItemStack(this, 1,0));
    }
	 @Override
	    public boolean isOpaqueCube()
	    {
	        return false;
	    }
		@Override
		public boolean renderAsNormalBlock()
		{
		    return false;
		}
		@Override
		public int getRenderType()
		{
		   return -1;
		}
		@Override
		public TileEntity createNewTileEntity(World world)
	    {
			return new TileEntityGen();
		}
}
