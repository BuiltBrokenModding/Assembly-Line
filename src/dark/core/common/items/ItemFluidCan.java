package dark.core.common.items;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import dark.core.common.DMCreativeTab;
import dark.core.common.DarkMain;
import net.minecraft.item.Item;
import net.minecraft.util.Icon;

/** Small fluid can that is designed to store up to one bucket of fluid.
 * 
 * @author DarkGuardsman */
public class ItemFluidCan extends Item
{
    public static final String FLUID_NBT = "FluidStack";

    @SideOnly(Side.CLIENT)
    public Icon[] icons;

    public ItemFluidCan()
    {
        super(DarkMain.CONFIGURATION.getItem("FluidCan", DarkMain.getNextItemId()).getInt());
        this.setCreativeTab(DMCreativeTab.tabIndustrial);
    }
}
