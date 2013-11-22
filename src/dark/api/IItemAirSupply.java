package dark.api;

import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;

/** Item that supplies air to an entity and prevents them from dying when under water, in gas, or
 * without air.
 * 
 * @author DarkGuardsman */
public interface IItemAirSupply
{
    /** Called when the entity is found to be in an area were the entity has no air. Does not work
     * with vinalla blocks or blocks from other mods. To support other mods simply do a per tick
     * update of the item and supply the entity with air. The reason for this method is to prevent
     * potion effects from gas poisoning or potion effects from sucking in fluids */
    public boolean canSupplyAir(Entity entity, ItemStack stack);

}
