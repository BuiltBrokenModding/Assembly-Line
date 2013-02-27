package assemblyline.common.machine;

import assemblyline.common.TabAssemblyLine;
import assemblyline.common.block.BlockALMachine;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;

public class BlockBeltSorter extends BlockALMachine
{

	public BlockBeltSorter(int id)
	{
		super("BlockBeltSorter", id, Material.iron, TabAssemblyLine.INSTANCE);
	}
	
}
