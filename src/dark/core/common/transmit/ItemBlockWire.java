package dark.core.common.transmit;

import net.minecraft.util.Icon;
import dark.core.common.CoreRecipeLoader;
import dark.core.prefab.ItemBlockHolder;

public class ItemBlockWire extends ItemBlockHolder
{

    public ItemBlockWire(int id)
    {
        super(id);
    }

    @Override
    public Icon getIconFromDamage(int par1)
    {
        return CoreRecipeLoader.blockWire instanceof BlockWire ? ((BlockWire) CoreRecipeLoader.blockWire).wireIcon : CoreRecipeLoader.blockWire.getIcon(0, par1);
    }

}
