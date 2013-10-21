package dark.core.common.items;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import dark.core.common.CoreRecipeLoader;
import dark.core.common.DarkMain;

/** Class for storing materials, there icon names, sub items to be made from them or there sub ores
 *
 *
 * @author DarkGuardsman */
public enum EnumMaterial
{
    WOOD("Wood", false, EnumOrePart.INGOTS, EnumOrePart.PLATES, EnumOrePart.RUBBLE, EnumOrePart.ROD, EnumOrePart.GEARS),
    STONE("Stone", false, EnumOrePart.INGOTS, EnumOrePart.SCRAPS),
    IRON("Iron", false, EnumOrePart.INGOTS),
    OBBY("Obby", true, EnumOrePart.INGOTS, EnumOrePart.RUBBLE, EnumOrePart.SCRAPS, EnumOrePart.PLATES),
    GOLD("Gold", false, EnumOrePart.GEARS, EnumOrePart.INGOTS),
    COAL("Coal", false, EnumOrePart.GEARS, EnumOrePart.TUBE, EnumOrePart.PLATES, EnumOrePart.RUBBLE, EnumOrePart.SCRAPS),

    COPPER("Copper", true),
    TIN("Tin", true, EnumOrePart.GEARS, EnumOrePart.TUBE),
    LEAD("Lead", true, EnumOrePart.GEARS, EnumOrePart.TUBE),
    ALUMINIUM("Aluminum", true, EnumOrePart.GEARS, EnumOrePart.TUBE),
    SILVER("Silver", true, EnumOrePart.GEARS),
    STEEL("Steel", true, EnumOrePart.RUBBLE),
    BRONZE("Bronze", true, EnumOrePart.RUBBLE);

    /** Name of the material */
    public String simpleName;
    /** List of ore parts that to not be created for the material */
    public List<EnumOrePart> unneedItems;

    public boolean hasTools = false;

    /** Limit by which each material is restricted by for creating orePart sub items */
    public static final int itemCountPerMaterial = 50;

    /** Client side only var used by ore items to store icon per material set */
    @SideOnly(Side.CLIENT)
    Icon[] itemIcons;

    private EnumMaterial(String name, boolean tool, EnumOrePart... enumOreParts)
    {
        this.simpleName = name;
        this.hasTools = tool;
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
                if (mat == EnumMaterial.IRON)
                {
                    return new ItemStack(Item.ingotIron, 1);
                }
                else if (mat == EnumMaterial.GOLD)
                {
                    return new ItemStack(Item.ingotGold, 1);
                }
            }
            int meta = mat.ordinal() * itemCountPerMaterial;
            meta += part.ordinal();
            return new ItemStack(CoreRecipeLoader.itemMetals.itemID, ammount, meta);
        }
        return reStack;
    }

    public ItemStack getStack(EnumOrePart part, int ammount)
    {
        return getStack(this, part, ammount);
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
        if (part == EnumOrePart.ROD || part == EnumOrePart.TUBE)
        {
            return false;
        }
        return this.unneedItems == null || !this.unneedItems.contains(part);
    }

    public boolean shouldCreateTool()
    {
        return this.hasTools;
    }

    public static ItemStack getTool(EnumTools tool, EnumMaterial mat)
    {
        return mat.getTool(tool);
    }

    public ItemStack getTool(EnumTools tool)
    {
        ItemStack stack = null;
        if (DarkMain.recipeLoader.itemDiggingTool instanceof ItemTool)
        {
            stack = new ItemStack(DarkMain.recipeLoader.itemDiggingTool.itemID, (this.ordinal() * 10) + tool.ordinal(), 1);
        }
        return stack;
    }
}
