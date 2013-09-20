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
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.registry.GameRegistry;
import dark.core.common.DarkMain;
import dark.core.prefab.IExtraObjectInfo;
import dark.core.prefab.helpers.Pair;
import dark.core.prefab.machine.BlockMachine;

/** Handler to make registering all parts of a mod's objects that are loaded into the game by forge
 *
 * @author DarkGuardsman */
public class ModObjectRegistry
{
    public static Configuration masterBlockConfig = new Configuration(new File(Loader.instance().getConfigDir(), "Dark/EnabledBlocks.cfg"));

    public static Block createNewBlock(String modID, Class<? extends Block> blockClass)
    {
        return ModObjectRegistry.createNewBlock(modID, blockClass, true);
    }

    public static Block createNewBlock(String modID, Class<? extends Block> blockClass, boolean canDisable)
    {
        return ModObjectRegistry.createNewBlock(modID, blockClass, null, canDisable);
    }

    public static Block createNewBlock(String modID, Class<? extends Block> blockClass, Class<? extends ItemBlock> itemClass)
    {
        return createNewBlock(modID, blockClass, itemClass, true);
    }

    public static Block createNewBlock(String modID, Class<? extends Block> blockClass, Class<? extends ItemBlock> itemClass, boolean canDisable)
    {
        Block block = null;
        if (blockClass != null && (!canDisable || canDisable && masterBlockConfig.get("Enabled_List", "Enabled_" + block.getUnlocalizedName().replace("tile.", ""), true).getBoolean(true)))
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
                ModObjectRegistry.registerBlock(block, itemClass, block.getUnlocalizedName().replace("tile.", ""), modID);
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
                        block = (Block) constructor.newInstance(buildData.config.getBlock(buildData.blockName, DarkMain.getNextID()), buildData.blockMaterial);

                    }
                }
                else
                {
                    constructor.setAccessible(true);
                    block = (Block) constructor.newInstance(buildData);

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
                // Read block class annotions
                for (Annotation annotian : block.getClass().getDeclaredAnnotations())
                {
                    if (annotian instanceof TileEntityUser)
                    {
                        Class<? extends TileEntity>[] tileEntities = ((TileEntityUser) annotian).tileEntities();
                        String[] tileEntitiesNames = ((TileEntityUser) annotian).tileEntitiesNames();

                        if (tileEntities != null && tileEntities.length > 0 && tileEntitiesNames != null && tileEntitiesNames.length > 0)
                        {
                            for (int i = 0; i < tileEntities.length && i < tileEntitiesNames.length; i++)
                            {
                                GameRegistry.registerTileEntityWithAlternatives(tileEntities[i], tileEntitiesNames[i], "DM" + tileEntitiesNames[i]);
                            }
                        }
                    }
                }

                // Read threw the block class looking for annotions on fields
                for (Method method : block.getClass().getMethods())
                {
                    for (Annotation annotian : method.getDeclaredAnnotations())
                    {
                        if (annotian instanceof BlockConfigFile)
                        {
                            Type[] types = method.getParameterTypes();
                            if (types.length == 1 && types[0] instanceof Configuration)
                            {
                                Configuration extraBlockConfig = new Configuration(new File(Loader.instance().getConfigDir(), "Dark/blocks/" + block.getUnlocalizedName() + ".cfg"));
                                extraBlockConfig.load();
                                try
                                {
                                    method.setAccessible(true);
                                    method.invoke(null, extraBlockConfig);
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

    public static void registerBlock(Block block, Class<? extends ItemBlock> itemClass, String name, String modID)
    {
        if (block != null && name != null && !name.isEmpty())
        {
            if (itemClass == null)
            {
                itemClass = ItemBlock.class;
            }
            GameRegistry.registerBlock(block, itemClass, name, modID);
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
