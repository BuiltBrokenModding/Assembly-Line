package com.builtbroken.assemblyline.content.parts;

import com.builtbroken.assemblyline.AssemblyLine;
import com.builtbroken.mc.core.Engine;
import com.builtbroken.mc.core.registry.implement.IPostInit;
import com.builtbroken.mc.core.registry.implement.IRegistryInit;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.IIcon;
import net.minecraftforge.oredict.OreDictionary;

import java.util.List;

/**
 * Simple item used to represent different parts used in recipes
 *
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 11/14/2016.
 */
public class ItemCraftingParts extends Item implements IRegistryInit, IPostInit
{
    public ItemCraftingParts()
    {
        this.setUnlocalizedName(AssemblyLine.PREFIX + "craftingPart");
        this.setCreativeTab(CreativeTabs.tabMaterials);
        this.setHasSubtypes(true);
    }

    @Override
    public void onPostInit()
    {
        ALParts.loadRecipes();
        for (ALParts part : ALParts.values())
        {
            if (part.recipes != null)
            {
                for (IRecipe recipe : part.recipes)
                {
                    if (recipe != null && recipe.getRecipeOutput() != null)
                    {
                        GameRegistry.addRecipe(recipe);
                    }
                    else if (Engine.runningAsDev)
                    {
                        AssemblyLine.INSTANCE.logger().error("Broken recipe for ItemCraftingPart[" + part + "] > " + recipe);
                    }
                }
            }
            part.recipes = null;
        }
    }

    @Override
    public void onRegistered()
    {
        for (ALParts part : ALParts.values())
        {
            if (part.oreName != null)
            {
                OreDictionary.registerOre(part.oreName, new ItemStack(AssemblyLine.itemParts, 1, part.ordinal()));
            }
        }
    }

    @Override
    public void onClientRegistered()
    {

    }

    @Override
    public String getUnlocalizedName(ItemStack stack)
    {
        if (stack.getItemDamage() >= 0 && stack.getItemDamage() < ALParts.values().length)
        {
            return super.getUnlocalizedName() + "." + ALParts.values()[stack.getItemDamage()].name;
        }
        return super.getUnlocalizedName();
    }

    @Override
    public void registerIcons(IIconRegister reg)
    {
        for (ALParts part : ALParts.values())
        {
            part.icon = reg.registerIcon(AssemblyLine.PREFIX + part.name);
        }
    }

    @Override
    public IIcon getIconFromDamage(int meta)
    {
        if (meta >= 0 && meta < ALParts.values().length)
        {
            return ALParts.values()[meta].icon;
        }
        return Items.string.getIconFromDamage(0);
    }

    @Override
    public void getSubItems(Item item, CreativeTabs tab, List items)
    {
        for (ALParts part : ALParts.values())
        {
            items.add(new ItemStack(item, 1, part.ordinal()));
        }
    }
}
