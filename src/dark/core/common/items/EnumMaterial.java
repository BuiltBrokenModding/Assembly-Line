package dark.core.common.items;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import dark.core.common.CoreRecipeLoader;

/** Class for storing materials, there icon names, sub items to be made from them or there sub ores
 *
 *
 * @author DarkGuardsman */
public enum EnumMaterial
{
    WOOD("Wood", EnumOrePart.INGOTS, EnumOrePart.PLATES, EnumOrePart.RUBBLE, EnumOrePart.ROD),
    STONE("Stone", EnumOrePart.INGOTS),
    COPPER("Copper"),
    TIN("Tin", EnumOrePart.GEARS, EnumOrePart.TUBE),
    IRON("Iron", EnumOrePart.INGOTS),
    OBBY("Obby", EnumOrePart.INGOTS, EnumOrePart.RUBBLE),
    LEAD("Lead", EnumOrePart.GEARS, EnumOrePart.TUBE),
    ALUMINIUM("Aluminum", EnumOrePart.GEARS, EnumOrePart.TUBE),
    SILVER("Silver", EnumOrePart.GEARS),
    GOLD("Gold", EnumOrePart.GEARS, EnumOrePart.INGOTS),
    COAL("Coal", EnumOrePart.GEARS, EnumOrePart.TUBE, EnumOrePart.PLATES, EnumOrePart.RUBBLE),
    STEEL("Steel", EnumOrePart.RUBBLE),
    BRONZE("Bronze", EnumOrePart.RUBBLE);

    /** Name of the material */
    public String simpleName;
    /** List of ore parts that to not be created for the material */
    public List<EnumOrePart> unneedItems;

    /** Limit by which each material is restricted by for creating orePart sub items */
    public static final int itemCountPerMaterial = 50;

    /** Client side only var used by ore items to store icon per material set */
    @SideOnly(Side.CLIENT)
    Icon[] itemIcons;

    private EnumMaterial(String name, EnumOrePart... enumOreParts)
    {
        this.simpleName = name;

        unneedItems = new ArrayList<EnumOrePart>();
        for (int i = 0; enumOreParts != null && i < enumOreParts.length; i++)
        {
            unneedItems.add(enumOreParts[i]);
        }
    }

    /** Creates a new item stack using material and part given. Uses a preset length of 50 for parts
     * enum so to prevent any unwanted changes in loading of itemStacks metadata.
     *
     * @param mat - material
     * @param part - part
     * @return new ItemStack created from the two enums as long as everything goes right */
    public static ItemStack getStack(EnumMaterial mat, EnumOrePart part, int ammount)
    {
        ItemStack reStack = null;
        if (CoreRecipeLoader.itemMetals instanceof ItemOreDirv && mat != null && part != null)
        {
            if (part == EnumOrePart.INGOTS)
            {
                switch (EnumMaterial.IRON)
                {
                    case IRON:
                        return new ItemStack(Item.ingotIron, 1);
                    case GOLD:
                        return new ItemStack(Item.ingotGold, 1);
                }
            }
            int meta = mat.ordinal() * itemCountPerMaterial;
            meta += part.ordinal();
            return new ItemStack(CoreRecipeLoader.itemMetals.itemID, ammount, meta);
        }
        return reStack;
    }

    public static Icon getIcon(int metadata)
    {
        int mat = metadata / EnumMaterial.itemCountPerMaterial;
        if (mat < EnumMaterial.values().length)
        {
            return EnumMaterial.values()[metadata / EnumMaterial.itemCountPerMaterial].itemIcons[metadata % EnumMaterial.itemCountPerMaterial];
        }
        return null;
    }

    public boolean shouldCreateItem(EnumOrePart part)
    {
        if (part == EnumOrePart.ROD || part == EnumOrePart.TUBE || part == EnumOrePart.RUBBLE)
        {
            return false;
        }
        return this.unneedItems == null || !this.unneedItems.contains(part);
    }
}
