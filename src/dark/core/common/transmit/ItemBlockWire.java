package dark.core.common.transmit;

import net.minecraft.util.Icon;
import dark.core.common.CoreRecipeLoader;
import dark.core.prefab.items.ItemBlockHolder;

public class ItemBlockWire extends ItemBlockHolder
{

    public ItemBlockWire(int id)
    {
        super(id);
    }

    public Icon getIconFromDamage(int par1)
    {
        return CoreRecipeLoader.blockWire instanceof BlockWire ? ((BlockWire) CoreRecipeLoader.blockWire).wireIcon : CoreRecipeLoader.blockWire.getIcon(0, par1);
    }

}
