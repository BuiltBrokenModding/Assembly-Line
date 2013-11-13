package dark.assembly.machine.red;

import net.minecraft.block.material.Material;
import dark.core.common.DarkMain;
import dark.core.prefab.machine.BlockMachine;

/** This will be a piston that can extend from 1 - 20 depending on teir and user settings
 *
 * @author Guardsman */
public class BlockPistonPlus extends BlockMachine
{

    public BlockPistonPlus()
    {
        super(DarkMain.CONFIGURATION, "DMPiston", Material.piston);
    }

}
