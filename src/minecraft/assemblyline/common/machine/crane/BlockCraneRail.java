package assemblyline.common.machine.crane;

import net.minecraft.block.material.Material;
import universalelectricity.prefab.BlockMachine;

public class BlockCraneRail extends BlockMachine
{
	public static final int RAIL_META = 0;

	public BlockCraneRail(int id)
	{
		super("CraneParts", id, Material.iron);
		// this.setCreativeTab(TabAssemblyLine.INSTANCE);
	}

}
