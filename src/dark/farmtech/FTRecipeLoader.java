package dark.farmtech;

import dark.core.common.RecipeLoader;

public class FTRecipeLoader extends RecipeLoader
{
    private static FTRecipeLoader instance;

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
    }
}
