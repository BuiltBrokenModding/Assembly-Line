package dark.farmtech;

import java.util.Calendar;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import dark.core.common.RecipeLoader;
import dark.farmtech.item.ItemFarmFood;
import dark.farmtech.item.ItemFarmFood.FarmFood;

public class FTRecipeLoader extends RecipeLoader
{
    private static FTRecipeLoader instance;

    public static Item itemFood;

    public static FTRecipeLoader instance()
    {
        if (instance == null)
        {
            instance = new FTRecipeLoader();
        }
        return instance;
    }

    @Override
    public void loadRecipes()
    {
        super.loadRecipes();
        this.loadFood();
    }

    public void loadFood()
    {
        if (itemFood instanceof ItemFarmFood)
        {
            FurnaceRecipes.smelting().addSmelting(itemFood.itemID, FarmFood.TurkeyRaw.ordinal(), new ItemStack(itemFood.itemID, 1, FarmFood.TurkeyCooked.ordinal()), 0.3f);
            if (FarmTech.getDate().getA() == Calendar.OCTOBER)
            {
                //TODO load up hollow eve foods
            }
            else if (FarmTech.getDate().getA() == Calendar.NOVEMBER)
            {
                //TODO load up thanks giving foods
            }
            else if (FarmTech.getDate().getA() == Calendar.DECEMBER)
            {
                //TODO load up xmas and other holiday foods
            }
        }
    }
}
