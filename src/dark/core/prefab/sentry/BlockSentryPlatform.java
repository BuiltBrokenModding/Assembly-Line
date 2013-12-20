package dark.core.prefab.sentry;

import com.dark.prefab.BlockMachine;

import net.minecraft.block.material.Material;
import dark.machines.CoreMachine;

/** Base platform for all sentry and turret's created to use for power, logic, and inventory
 * connections to the world.
 * 
 * @author DarkGuardsman */
public class BlockSentryPlatform extends BlockMachine
{

    public BlockSentryPlatform()
    {
        super(CoreMachine.CONFIGURATION, "DMSentryPlatform", Material.iron);
        this.setResistance(100);
        this.setHardness(100);
    }

}
