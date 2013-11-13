package dark.assembly.machine.frame;

import net.minecraft.block.material.Material;
import dark.core.common.DarkMain;
import dark.core.prefab.machine.BlockMachine;

public class BlockFrame extends BlockMachine
{
    public BlockFrame()
    {
        super(DarkMain.CONFIGURATION, "DMFrame", Material.iron);
    }
}
