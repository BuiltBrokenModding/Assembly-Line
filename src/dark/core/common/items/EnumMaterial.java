package dark.core.common.items;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.EnumToolMaterial;
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
    WOOD("Wood", EnumToolMaterial.WOOD, EnumOrePart.INGOTS, EnumOrePart.PLATES, EnumOrePart.RUBBLE, EnumOrePart.ROD, EnumOrePart.GEARS),
    STONE("Stone", EnumToolMaterial.STONE, EnumOrePart.INGOTS, EnumOrePart.SCRAPS),
    IRON("Iron", EnumToolMaterial.IRON, EnumOrePart.INGOTS),
    OBBY("Obby", true, 7.0f, 500, EnumOrePart.INGOTS, EnumOrePart.RUBBLE, EnumOrePart.SCRAPS, EnumOrePart.PLATES),
    GOLD("Gold", EnumToolMaterial.GOLD, EnumOrePart.GEARS, EnumOrePart.INGOTS),
    COAL("Coal", EnumToolMaterial.WOOD, EnumOrePart.GEARS, EnumOrePart.TUBE, EnumOrePart.PLATES, EnumOrePart.RUBBLE, EnumOrePart.SCRAPS),

    COPPER("Copper", true, 3.5f, 79),
    TIN("Tin", true, 2.0f, 50, EnumOrePart.GEARS, EnumOrePart.TUBE),
    LEAD("Lead", false, 0, 0, EnumOrePart.GEARS, EnumOrePart.TUBE),
    ALUMINIUM("Aluminum", true, 5.0f, 100, EnumOrePart.GEARS, EnumOrePart.TUBE),
    SILVER("Silver", true, 11.0f, 30, EnumOrePart.GEARS),
    STEEL("Steel", true, 7.0f, 1000, EnumOrePart.RUBBLE),
    BRONZE("Bronze", true, 6.5f, 560, EnumOrePart.RUBBLE);

    /** Name of the material */
    public String simpleName;
    /** List of ore parts that to not be created for the material */
    public List<EnumOrePart> unneedItems;

    public boolean hasTools = false;

    /** Limit by which each material is restricted by for creating orePart sub items */
    public static final int itemCountPerMaterial = 50;
    public static final int toolCountPerMaterial = 10;

    /** Client side only var used by ore items to store icon per material set */
    @SideOnly(Side.CLIENT)
    Icon[] itemIcons;

    @SideOnly(Side.CLIENT)
    Icon[] toolIcons;

    public float materialEffectiveness = 2.0f;
    public int maxUses = 100;

    private EnumMaterial(String name, EnumToolMaterial material, EnumOrePart... enumOreParts)
    {
        this(name, false, material.getEfficiencyOnProperMaterial(), material.getMaxUses(), enumOreParts);
    }

    private EnumMaterial(String name, boolean tool, float effectiveness, int toolUses, EnumOrePart... enumOreParts)
    {
        this.simpleName = name;
        this.hasTools = tool;
        this.materialEffectiveness = effectiveness;
        this.maxUses = toolUses;
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

    public static Icon getToolIcon(int metadata)
    {
        int mat = getToolMatFromMeta(metadata).ordinal();
        int tool = getToolFromMeta(metadata).ordinal();
        if (mat < EnumMaterial.values().length)
        {
            if (EnumMaterial.values()[mat].toolIcons == null)
            {
                EnumMaterial.values()[mat].toolIcons = new Icon[toolCountPerMaterial];
            }
            if (tool < EnumMaterial.values()[mat].toolIcons.length)
            {
                return EnumMaterial.values()[mat].toolIcons[tool];
            }
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

    public static ItemStack getTool(EnumTool tool, EnumMaterial mat)
    {
        return mat.getTool(tool);
    }

    public ItemStack getTool(EnumTool tool)
    {
        ItemStack stack = null;
        if (CoreRecipeLoader.itemDiggingTool instanceof ItemCommonTool)
        {
            stack = new ItemStack(CoreRecipeLoader.itemDiggingTool.itemID, 1, (this.ordinal() * toolCountPerMaterial) + tool.ordinal());
        }
        return stack;
    }

    public static EnumTool getToolFromMeta(int meta)
    {
        return EnumTool.values()[meta % toolCountPerMaterial];
    }

    public static EnumMaterial getToolMatFromMeta(int meta)
    {
        return EnumMaterial.values()[meta / EnumMaterial.toolCountPerMaterial];
    }
}
