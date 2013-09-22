package dark.core.common.items;

import java.util.List;

import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;
import net.minecraftforge.common.Configuration;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import dark.core.common.DarkMain;
import dark.core.prefab.items.ItemBasic;

/** A series of items that are derived from a basic material
 * 
 * @author DarkGuardsman */
public class ItemOreDirv extends ItemBasic
{
    public ItemOreDirv(int itemID, Configuration config)
    {
        super(itemID, "Metal_Parts", config);
        this.setHasSubtypes(true);
        this.setCreativeTab(CreativeTabs.tabMaterials);
    }

    @Override
    public String getUnlocalizedName(ItemStack itemStack)
    {
        if (itemStack != null)
        {
            return "item." + DarkMain.getInstance().PREFIX + EnumOrePart.getFullName(itemStack.getItemDamage());
        }
        else
        {
            return this.getUnlocalizedName();
        }
    }

    @Override
    public Icon getIconFromDamage(int i)
    {
        return EnumMaterial.getIcon(i);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void registerIcons(IconRegister iconRegister)
    {
        for (EnumMaterial mat : EnumMaterial.values())
        {
            mat.itemIcons = new Icon[EnumOrePart.values().length];
            for (EnumOrePart part : EnumOrePart.values())
            {
                if (mat.shouldCreateItem(part))
                {
                    mat.itemIcons[part.ordinal()] = iconRegister.registerIcon(DarkMain.getInstance().PREFIX + mat.simpleName + part.simpleName);
                }
            }
        }
    }

    @Override
    public void getSubItems(int par1, CreativeTabs par2CreativeTabs, List par3List)
    {
        for (EnumMaterial mat : EnumMaterial.values())
        {
            for (EnumOrePart part : EnumOrePart.values())
            {
                ItemStack stack = EnumMaterial.getStack(mat, part, 1);
                if (stack != null && mat.shouldCreateItem(part) && mat.itemIcons[part.ordinal()] != null)
                {
                    par3List.add(stack);
                }
            }
        }
    }

}
