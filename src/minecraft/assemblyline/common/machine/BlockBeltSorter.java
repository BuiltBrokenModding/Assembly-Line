package assemblyline.common.machine;

import net.minecraft.block.material.Material;
import assemblyline.common.TabAssemblyLine;
import assemblyline.common.block.BlockALMachine;

public class BlockBeltSorter extends BlockALMachine
{

	public BlockBeltSorter(int id)
	{
		super(id, Material.iron);
		this.setCreativeTab(TabAssemblyLine.INSTANCE);
		this.setUnlocalizedName("BeltSorter");
	}

}
