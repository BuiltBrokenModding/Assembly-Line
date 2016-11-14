package com.builtbroken.assemblyline.content.parts;

import com.builtbroken.assemblyline.AssemblyLine;
import com.builtbroken.mc.core.Engine;
import com.builtbroken.mc.core.content.parts.CraftingParts;
import com.builtbroken.mc.core.content.resources.items.ItemSheetMetal;
import com.builtbroken.mc.lib.helper.recipe.OreNames;
import com.builtbroken.mc.lib.helper.recipe.UniversalRecipe;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.IIcon;
import net.minecraftforge.oredict.ShapedOreRecipe;

import java.util.ArrayList;
import java.util.List;

/**
 * Enum of parts only used for crafting recipes or upgrades
 *
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 11/14/2016.
 */
public enum ALParts
{
    /** Base of all robotic arms */
    ROBOTIC_BASE("armBase", null),
    /** Robotic arm, no motors just frame & wires */
    ROBOTIC_ARM("roboticArm", null),
    /** Upper & Lower robotic arm */
    ROBOTIC_ARM_ASSEMBLY("armAssembly", null),
    /** Simple 3 finger robotic hand */
    ROBOTIC_HAND("handAssembly", null),
    /** Very basic logic controller for robotics */
    SIMPLE_LOGIC_BOX("simpleLogicBox", null);

    public final String oreName;
    public final String name;
    /** Only stored during load time, cleared when used */
    public List<IRecipe> recipes;
    protected IIcon icon;

    ALParts(String name, String oreName)
    {
        this.name = name;
        this.oreName = oreName;
    }

    /** Generates a stack for the part */
    public ItemStack toStack()
    {
        return new ItemStack(AssemblyLine.itemParts, 1, ordinal());
    }

    /**
     * Generates a stack
     *
     * @param sum - amount
     * @return stack with ammount
     */
    public ItemStack toStack(int sum)
    {
        return new ItemStack(Engine.itemCraftingParts, sum, ordinal());
    }

    /** Called to load the recipes into memory */
    public static void loadRecipes()
    {
        for (ALParts parts : values())
        {
            parts.recipes = new ArrayList();
        }

        final Object casingItem;
        if (Engine.itemSheetMetal != null)
        {
            casingItem = ItemSheetMetal.SheetMetal.EIGHTH.stack();
        }
        else
        {
            casingItem = OreNames.INGOT_IRON;
        }

        SIMPLE_LOGIC_BOX.recipes.add(new ShapedOreRecipe(ROBOTIC_BASE.toStack(), "IWI", "CTC", "IWI", 'W', OreNames.WIRE_COPPER, 'I', casingItem, 'T', UniversalRecipe.CIRCUIT_T2.get(), 'C', UniversalRecipe.CIRCUIT_T1.get()));

        ROBOTIC_BASE.recipes.add(new ShapedOreRecipe(ROBOTIC_BASE.toStack(), "IMI", "CLC", "III", 'M', CraftingParts.STEPPER_MOTOR.toStack(), 'I', casingItem, 'C', UniversalRecipe.CIRCUIT_T2.get()));

        ROBOTIC_ARM.recipes.add(new ShapedOreRecipe(ROBOTIC_ARM.toStack(), "IRI", "WRW", "IRI", 'R', OreNames.ROD_IRON, 'I', casingItem, 'W', OreNames.WIRE_COPPER));

        ROBOTIC_ARM_ASSEMBLY.recipes.add(new ShapedOreRecipe(ROBOTIC_ARM_ASSEMBLY.toStack(), "MI ", "AI ", "MAW", 'R', OreNames.ROD_IRON, 'I', casingItem, 'M', CraftingParts.STEPPER_MOTOR.toStack(), 'W', OreNames.WIRE_COPPER));

        ROBOTIC_HAND.recipes.add(new ShapedOreRecipe(ROBOTIC_HAND.toStack(), "RRR", "MMM", "CIC", 'R', OreNames.ROD_IRON, 'I', casingItem, 'M', CraftingParts.STEPPER_MOTOR.toStack(), 'C', UniversalRecipe.CIRCUIT_T2.get()));
    }
}
