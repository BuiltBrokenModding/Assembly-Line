package dark.api.reciepes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.minecraft.item.ItemStack;

import com.builtbroken.common.Pair;

/** Enum of machines that support a simple A -> B processor recipe format. More complex machine will
 * have there own recipe handlers
 *
 * @author Darkguardsman */
public enum ProcessorType
{
    CRUSHER(),
    GRINDER(),
    METAL_PRESS(),
    SHARPENING_STONE();
    public HashMap<Pair<Integer, Integer>, ProcessorRecipe> recipes = new HashMap();
    public HashMap<Pair<Integer, Integer>, ItemStack> altOutput = new HashMap();
    public List<Pair<Integer, Integer>> banList = new ArrayList();
}
