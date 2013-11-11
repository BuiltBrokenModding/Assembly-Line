package dark.api;

import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import universalelectricity.core.vector.Vector3;

/** Applied to blocks that store items in stacks above 64
 * 
 * @author DarkGuardsman */
public interface IExtendedStorage
{
    public ItemStack addStackToStorage(ItemStack stack);
}
