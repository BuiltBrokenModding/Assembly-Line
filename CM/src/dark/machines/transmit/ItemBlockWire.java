package dark.machines.transmit;

import com.dark.prefab.ItemBlockHolder;

import net.minecraft.util.Icon;
import dark.machines.CoreRecipeLoader;

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
