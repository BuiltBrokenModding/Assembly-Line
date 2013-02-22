package assemblyline.common.machine;

import assemblyline.common.TabAssemblyLine;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import universalelectricity.prefab.BlockMachine;

public class BlockBeltSorter extends BlockMachine
{

	public BlockBeltSorter(int id)
	{
		super("BlockBeltSorter", id, Material.iron, TabAssemblyLine.INSTANCE);
	}
	
}
