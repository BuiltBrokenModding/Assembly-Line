package assemblyline.common.machine.crane;

import assemblyline.common.TabAssemblyLine;
import universalelectricity.prefab.BlockMachine;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;

public class BlockCraneParts extends BlockMachine
{
	public static final int RAIL_META = 0;

	public BlockCraneParts(int id)
	{
		super("CraneParts", id, Material.iron, TabAssemblyLine.INSTANCE);

	}

}
