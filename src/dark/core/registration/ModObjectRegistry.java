package dark.core.registration;

import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemBlock;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.registry.GameRegistry;
import dark.core.common.DarkMain;
import dark.core.prefab.IExtraObjectInfo;
import dark.core.prefab.ModPrefab;
import dark.core.prefab.helpers.Pair;
import dark.core.prefab.machine.BlockMachine;

/** Handler to make registering all parts of a mod's objects that are loaded into the game by forge
 *
 * @author DarkGuardsman */
public class ModObjectRegistry
{
    public static Configuration masterBlockConfig = new Configuration(new File(Loader.instance().getConfigDir(), "Dark/EnabledBlocks.cfg"));

    public static Block createNewBlock(String name, String modID, Class<? extends Block> blockClass)
    {
        return ModObjectRegistry.createNewBlock(name, modID, blockClass, true);
    }

    public static Block createNewBlock(String name, String modID, Class<? extends Block> blockClass, boolean canDisable)
    {
        return ModObjectRegistry.createNewBlock(name, modID, blockClass, null, canDisable);
    }

    public static Block createNewBlock(String name, String modID, Class<? extends Block> blockClass, Class<? extends ItemBlock> itemClass)
    {
        return createNewBlock(name, modID, blockClass, itemClass, true);
    }

    public static Block createNewBlock(String name, String modID, Class<? extends Block> blockClass, Class<? extends ItemBlock> itemClass, boolean canDisable)
    {
        Block block = null;
        if (blockClass != null && (!canDisable || canDisable && masterBlockConfig.get("Enabled_List", "Enabled_" + name, true).getBoolean(true)))
        {
            try
            {
                block = blockClass.newInstance();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            if (block != null)
            {
                ModObjectRegistry.finishCreation(block, null);
                ModObjectRegistry.registerBlock(block, itemClass, name, modID);
            }
        }
        return block;
    }

    public static Block createNewBlock(String modID, BlockBuildData buildData)
    {
        Block block = null;
        Constructor constructor = null;
        try
        {
            if (buildData != null && (!buildData.allowDisable || buildData.allowDisable && masterBlockConfig.get("Enabled_List", "Enabled_" + buildData.blockName, true).getBoolean(true)))
            {
                try
                {
                    constructor = buildData.blockClass.getConstructor(BlockBuildData.class);
                }
                catch (Exception x)
                {
                    x.printStackTrace();
                }
                if (constructor == null)
                {
                    constructor = buildData.blockClass.getConstructor(Integer.class, Material.class);
                    if (constructor != null)
                    {

                        constructor.setAccessible(true);
                        Object obj = constructor.newInstance(buildData.config.getBlock(buildData.blockName, ModPrefab.getNextID()), buildData.blockMaterial);
                        if (obj instanceof Block)
                        {
                            block = (Block) obj;
                        }
                    }
                }
                else
                {
                    constructor.setAccessible(true);
                    Object obj = constructor.newInstance(buildData);
                    if (obj instanceof Block)
                    {
                        block = (Block) obj;
                    }

                }
                ModObjectRegistry.finishCreation(block, buildData);
                ModObjectRegistry.registerBlock(block, buildData.itemBlock, buildData.blockName, modID);
            }
        }
        catch (Exception x)
        {
            x.printStackTrace();
        }
        return block;
    }

    public static void finishCreation(Block block, BlockBuildData data)
    {
        if (data != null)
        {
            if (block != null)
            {
                BlockTileEntityInfo blockTileEntityInfo = block.getClass().getAnnotation(BlockTileEntityInfo.class);
                if (blockTileEntityInfo != null)
                {
                    System.out.println("\n\n\n\n[ModObjectRegistry] Reading tile entities for " + block.getUnlocalizedName());
                    Class<? extends TileEntity>[] tileEntities = blockTileEntityInfo.tileEntities();
                    String[] tileEntitiesNames = blockTileEntityInfo.tileEntitiesNames();

                    if (tileEntities != null && tileEntities.length > 0 && tileEntitiesNames != null && tileEntitiesNames.length > 0)
                    {
                        for (int i = 0; i < tileEntities.length && i < tileEntitiesNames.length; i++)
                        {
                            GameRegistry.registerTileEntityWithAlternatives(tileEntities[i], tileEntitiesNames[i], "DM" + tileEntitiesNames[i]);
                        }
                    }
                }

                // Read threw the block class looking for annotions on fields
                for (Method method : block.getClass().getMethods())
                {
                    System.out.println("[ModObjectRegistry] Reading class methods " + method.toGenericString());
                    for (Annotation annotian : method.getDeclaredAnnotations())
                    {
                        System.out.println("[ModObjectRegistry] Reading annotion " + annotian.toString());
                        if (annotian instanceof BlockConfigFile)
                        {

                            System.out.println("[ModObjectRegistry] Loading config file for " + block.getUnlocalizedName());
                            Type[] types = method.getParameterTypes();
                            if (types.length == 1 && types[0] instanceof Configuration)
                            {
                                Configuration extraBlockConfig = new Configuration(new File(Loader.instance().getConfigDir(), "Dark/blocks/" + block.getUnlocalizedName() + ".cfg"));
                                extraBlockConfig.load();
                                try
                                {
                                    method.setAccessible(true);
                                    method.invoke(extraBlockConfig);
                                }
                                catch (IllegalAccessException e)
                                {
                                    e.printStackTrace();
                                }
                                catch (IllegalArgumentException e)
                                {
                                    e.printStackTrace();
                                }
                                catch (InvocationTargetException e)
                                {
                                    e.printStackTrace();
                                }
                                extraBlockConfig.save();

                            }
                            break;
                        }
                    }
                }

                // Load data from BlockBuildData
                if (data.tiles != null)
                {
                    for (Pair<String, Class<? extends TileEntity>> par : data.tiles)
                    {
                        GameRegistry.registerTileEntityWithAlternatives(par.getValue(), par.getKey(), "DM" + par.getKey());
                    }
                }
                if (data.creativeTab != null)
                {
                    block.setCreativeTab(data.creativeTab);
                }
            }
        }
        if (block instanceof IExtraObjectInfo)
        {
            if (((IExtraObjectInfo) block).hasExtraConfigs())
            {
                Configuration extraBlockConfig = new Configuration(new File(Loader.instance().getConfigDir(), "Dark/blocks/" + block.getUnlocalizedName() + ".cfg"));
                extraBlockConfig.load();
                ((IExtraObjectInfo) block).loadExtraConfigs(extraBlockConfig);
                extraBlockConfig.save();
            }
            ((IExtraObjectInfo) block).loadOreNames();
            Set<Pair<String, Class<? extends TileEntity>>> tileListNew = new HashSet<Pair<String, Class<? extends TileEntity>>>();
            ((IExtraObjectInfo) block).getTileEntities(block.blockID, tileListNew);
            for (Pair<String, Class<? extends TileEntity>> par : tileListNew)
            {
                GameRegistry.registerTileEntityWithAlternatives(par.getValue(), par.getKey(), "DM" + par.getKey());
            }
        }

    }

    public static Block createNewFluidBlock(String modDomainPrefix, Configuration config, Fluid fluid)
    {
        Block fluidBlock = null;
        Fluid fluidActual = null;
        if (config != null && fluid != null && config.get("general", "EnableOilFluid", true).getBoolean(true) && FluidRegistry.getFluid(fluid.getName()) == null)
        {
            FluidRegistry.registerFluid(fluid);
            fluidActual = FluidRegistry.getFluid(fluid.getName());
            if (fluidActual == null)
            {
                fluidActual = fluid;
            }

            if (fluidActual.getBlockID() == -1 && masterBlockConfig.get("Enabled_List", "Enabled_" + fluid.getName() + "Block", true).getBoolean(true))
            {
                fluidBlock = new BlockFluid(modDomainPrefix, fluidActual, config).setUnlocalizedName("tile.Fluid." + fluid.getName());
                GameRegistry.registerBlock(fluidBlock, "DMBlockFluid" + fluid.getName());
            }
            else
            {
                fluidBlock = Block.blocksList[fluid.getBlockID()];
            }
        }

        return fluidBlock;
    }

    public static void registerBlock(Block block, Class<? extends ItemBlock> itemClass, String name, String modID)
    {
        if (block != null && name != null)
        {
            GameRegistry.registerBlock(block, itemClass == null ? ItemBlock.class : itemClass, name, modID);
        }
    }

    public static class BlockBuildData
    {
        public Class<? extends BlockMachine> blockClass;
        public Class<? extends ItemBlock> itemBlock;

        public String blockName;
        public Material blockMaterial;
        public Configuration config = DarkMain.CONFIGURATION;
        public CreativeTabs creativeTab;

        public Set<Pair<String, Class<? extends TileEntity>>> tiles = new HashSet<Pair<String, Class<? extends TileEntity>>>();
        public boolean allowDisable = true;

        public BlockBuildData(Class<? extends Block> blockClass, String name, Material material)
        {
            this.blockName = name;
            this.blockMaterial = material;
        }

        public BlockBuildData setConfigProvider(Configuration config)
        {
            if (config != null)
            {
                this.config = config;
            }
            return this;
        }

        public BlockBuildData setCreativeTab(CreativeTabs tab)
        {
            if (tab != null)
            {
                this.creativeTab = tab;
            }
            return this;
        }

        public BlockBuildData setItemBlock(Class<? extends ItemBlock> itemClass)
        {
            this.itemBlock = itemClass;
            return this;
        }

        /** Should there be an option to allow the user to disable this block */
        public BlockBuildData canDisable(boolean yes)
        {
            this.allowDisable = yes;
            return this;
        }

        /** Adds a tileEntity to be registered when this block is registered
         *
         * @param name - mod name for the tileEntity, should be unique
         * @param class1 - new instance of the TileEntity to register */
        public BlockBuildData addTileEntity(String name, Class<? extends TileEntity> class1)
        {
            if (name != null && class1 != null)
            {
                Pair<String, Class<? extends TileEntity>> pair = new Pair<String, Class<? extends TileEntity>>(name, class1);
                if (!this.tiles.contains(pair))
                {
                    this.tiles.add(pair);
                }
            }
            return this;
        }

        public BlockBuildData addTileEntity(Class<? extends TileEntity> class1, String string)
        {
            return addTileEntity(string, class1);
        }
    }
}
