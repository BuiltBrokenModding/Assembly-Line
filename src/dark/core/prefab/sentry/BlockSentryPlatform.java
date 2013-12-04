package dark.core.prefab.sentry;

import net.minecraft.block.material.Material;
import dark.core.prefab.machine.BlockMachine;
import dark.machines.DarkMain;

/** Base platform for all sentry and turret's created to use for power, logic, and inventory
 * connections to the world.
 * 
 * @author DarkGuardsman */
public class BlockSentryPlatform extends BlockMachine
{

    public BlockSentryPlatform()
    {
        super(DarkMain.CONFIGURATION, "DMSentryPlatform", Material.iron);
        this.setResistance(100);
        this.setHardness(100);
    }

}
