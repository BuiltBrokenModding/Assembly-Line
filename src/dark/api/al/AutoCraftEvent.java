package dark.api.al;

import com.dark.helpers.AutoCraftingManager.IAutoCrafter;

import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.event.Cancelable;
import net.minecraftforge.event.Event;
import universalelectricity.core.vector.Vector3;

/** Events called when an automated crafter is working on crafting an item
 * 
 * @author DarkGuardsman */
public class AutoCraftEvent extends Event
{
    World world;
    Vector3 spot;
    IAutoCrafter crafter;
    ItemStack craftingResult;

    public AutoCraftEvent(World world, Vector3 spot, IAutoCrafter craft, ItemStack stack)
    {
        this.world = world;
        this.spot = spot;
        this.crafter = craft;
        this.craftingResult = stack;
    }

    @Cancelable
    /** Called before a crafter checks if it can craft. Use this to cancel crafting */
    public static class PreCraft extends AutoCraftEvent
    {
        public PreCraft(World world, Vector3 spot, IAutoCrafter craft, ItemStack stack)
        {
            super(world, spot, craft, stack);
        }
    }

}
