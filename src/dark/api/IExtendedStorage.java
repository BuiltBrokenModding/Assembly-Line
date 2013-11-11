package dark.api;

import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import universalelectricity.core.vector.Vector3;

/** Applied to blocks that store items in stacks above 64
 * 
 * @author DarkGuardsman */
public interface IExtendedStorage
{
    public ItemStack addStackToCrate(World world, Vector3 loc, ItemStack stack);
}
