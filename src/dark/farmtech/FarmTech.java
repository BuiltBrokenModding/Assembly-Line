package dark.farmtech;

import java.io.File;
import java.util.Arrays;

import net.minecraft.block.Block;
import net.minecraft.block.BlockDispenser;
import net.minecraft.item.Item;
import net.minecraftforge.common.Configuration;
import universalelectricity.prefab.TranslationHelper;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.Mod.Metadata;
import cpw.mods.fml.common.ModMetadata;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import dark.api.farm.CropAutomationHandler;
import dark.api.farm.DecayMatterList;
import dark.core.common.DarkMain;
import dark.core.prefab.ItemBlockHolder;
import dark.core.prefab.ModPrefab;
import dark.core.registration.ModObjectRegistry;
import dark.farmtech.blocks.BlockFarmSoil;
import dark.farmtech.entities.EntityFarmEgg;
import dark.farmtech.entities.EnumBird;
import dark.farmtech.item.BehaviorDispenseEgg;
import dark.farmtech.item.ItemFarmEgg;

@Mod(modid = FarmTech.MOD_ID, name = FarmTech.MOD_NAME, version = FarmTech.VERSION, dependencies = "after:DarkCore", useMetadata = true)
@NetworkMod(clientSideRequired = true, serverSideRequired = false)
public class FarmTech extends ModPrefab
{
    public static final String MAJOR_VERSION = "@MAJOR@";
    public static final String MINOR_VERSION = "@MINOR@";
    public static final String REVIS_VERSION = "@REVIS@";
    public static final String BUILD_VERSION = "@BUILD@";
    public static final String VERSION = MAJOR_VERSION + "." + MINOR_VERSION + "." + REVIS_VERSION + "." + BUILD_VERSION;

    public static final String MOD_ID = "FarmTech";
    public static final String MOD_NAME = "Farm Tech";

    @Metadata(FarmTech.MOD_ID)
    public static ModMetadata meta;

    /* SUPPORTED LANGS */
    private static final String[] LANGUAGES_SUPPORTED = new String[] { "en_US" };

    /* CONFIG FILE */
    public static final Configuration CONFIGURATION = new Configuration(new File(Loader.instance().getConfigDir() + "/Dark/", MOD_NAME + ".cfg"));

    /* BLOCKS */
    public static Block blockFarmSoil;

    @SidedProxy(clientSide = "dark.farmtech.client.ClientProxy", serverSide = "dark.farmtech.CommonProxy")
    public static CommonProxy proxy;

    @Instance(FarmTech.MOD_NAME)
    public static FarmTech instance;

    public static int entitiesIds = 60;

    @Override
    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        instance = this;
        super.preInit(event);
        proxy.preInit();
    }

    @Override
    @EventHandler
    public void init(FMLInitializationEvent event)
    {
        super.init(event);
        proxy.init();

        /* LANG LOADING */
        FMLLog.info("[FarmTech] Loaded: " + TranslationHelper.loadLanguages(LANGUAGE_PATH, LANGUAGES_SUPPORTED) + " Languages.");

    }

    @Override
    @EventHandler
    public void postInit(FMLPostInitializationEvent event)
    {
        super.postInit(event);
        proxy.postInit();
        DecayMatterList.triggerPostBlockAddition();
        CropAutomationHandler.triggerPostBlockAddition();
    }

    @Override
    public String getDomain()
    {
        return "ft";
    }

    @Override
    public void registerObjects()
    {
        CONFIGURATION.load();

        blockFarmSoil = ModObjectRegistry.createNewBlock("FTBlockFarmSoil", FarmTech.MOD_ID, BlockFarmSoil.class, ItemBlockHolder.class);

        //String compostList = CONFIGURATION.get("DecayMatter", "List", "5::8000:1", "Items or blocks beyond the built in ones that can be turned into compost. Entries go BlockID:Meta:Time:Amount").getString();
        //DecayMatterList.parseConfigString(compostList);
        if (FarmTech.CONFIGURATION.get("Override", "Eggs", true).getBoolean(true))
        {
            Item.itemsList[Item.egg.itemID] = null;
            Item.egg = null;
            Item.egg = new ItemFarmEgg(88);
            GameRegistry.registerItem(Item.egg, "FTEgg", MOD_ID);
            EntityRegistry.registerGlobalEntityID(EntityFarmEgg.class, "FarmEgg", EntityRegistry.findGlobalUniqueEntityId());
            EntityRegistry.registerModEntity(EntityFarmEgg.class, "FarmEgg", entitiesIds++, this, 64, 1, true);
            BlockDispenser.dispenseBehaviorRegistry.putObject(Item.egg, new BehaviorDispenseEgg());
        }

        for (EnumBird bird : EnumBird.values())
        {
            if (bird != EnumBird.VANILLA_CHICKEN && CONFIGURATION.get("Entities", "Enable_" + bird.name(), true).getBoolean(true))
            {
                bird.register();
            }
        }

        CONFIGURATION.save();
    }

    @Override
    public void loadModMeta()
    {
        meta.modId = MOD_ID;
        meta.name = MOD_NAME;
        meta.description = "Farming addon for Darks Core Machine";
        meta.url = "http://www.universalelectricity.com/coremachine";

        meta.logoFile = TEXTURE_DIRECTORY + "GP_Banner.png";
        meta.version = DarkMain.VERSION;
        meta.authorList = Arrays.asList(new String[] { "DarkGuardsman", "LiQuiD" });
        meta.credits = "Please see the website.";
        meta.autogenerated = false;

    }

    @Override
    public void loadRecipes()
    {
        FTRecipeLoader.instance().loadRecipes();
    }

}
