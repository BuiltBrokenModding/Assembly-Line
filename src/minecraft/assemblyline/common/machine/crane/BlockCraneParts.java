package assemblyline.common.machine.crane;

import net.minecraft.block.material.Material;
import universalelectricity.prefab.BlockMachine;

public class BlockCraneParts extends BlockMachine
{
	public static final int RAIL_META = 0;

	public BlockCraneParts(int id)
	{
		super("CraneParts", id, Material.iron);
		// this.setCreativeTab(TabAssemblyLine.INSTANCE);
	}

}
