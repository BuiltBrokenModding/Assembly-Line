package dark.core.common.items;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.material.Material;

/** Enum to store tools that can be created from the material sheet.
 *
 * @author DarkGuardsman */
public enum EnumTool
{
    PICKAX("Pick-Ax", Material.rock, Material.iron, Material.ice, Material.anvil, Material.glass, Material.tnt, Material.piston),
    AX("Wood-Ax", Material.wood, Material.pumpkin, Material.plants, Material.vine),
    SPADE("Shade", Material.sand, Material.snow, Material.clay, Material.craftedSnow, Material.grass, Material.ground),
    NA3(),
    NA4(),
    NA5(),
    NA6(),
    NA7(),
    NA8(),
    NA9();

    public final List<Material> effecticVsMaterials = new ArrayList<Material>();
    public String name = "tool";
    public boolean enabled = false;

    private EnumTool()
    {

    }

    private EnumTool(String name, Material... mats)
    {
        this.name = name;
        this.enabled = true;
        this.setEffectiveList(mats);
    }

    public void setEffectiveList(Material... blocks)
    {
        for (Material block : blocks)
        {
            this.addEffectiveBlock(block);
        }
    }

    public void addEffectiveBlock(Material block)
    {
        if (block != null)
        {
            this.effecticVsMaterials.add(block);
        }
    }

    public static String getFullName(int meta)
    {
        EnumMaterial mat = EnumMaterial.getToolMatFromMeta(meta);
        EnumTool tool = EnumMaterial.getToolFromMeta(meta);
        if (mat != null && tool != null)
        {
            return mat.simpleName + tool.name;
        }
        return "CommonTool[" + meta + "]";
    }
}
