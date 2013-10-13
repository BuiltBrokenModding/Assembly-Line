package dark.core.registration;

import java.io.File;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemBlock;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;

import com.builtbroken.common.Pair;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.SidedProxy;
import dark.core.common.DarkMain;
import dark.core.prefab.IExtraInfo;
import dark.core.prefab.IExtraInfo.IExtraBlockInfo;
import dark.core.prefab.ModPrefab;
import dark.core.prefab.machine.BlockMachine;

/** Handler to make registering all parts of a mod's objects that are loaded into the game by forge
 *
 * @author DarkGuardsman */
public class ModObjectRegistry
{
    public static HashMap<Block, String> registredBlocks = new HashMap<Block, String>();

    @SidedProxy(clientSide = "dark.core.registration.ClientRegistryProxy", serverSide = "dark.core.registration.RegistryProxy")
    public static RegistryProxy proxy;

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
            //TODO redesign to catch blockID conflict
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
                registredBlocks.put(block, name);
                proxy.registerBlock(block, itemClass, name, modID);
                ModObjectRegistry.finishCreation(block, null);
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
                proxy.registerBlock(block, buildData.itemBlock, buildData.blockName, modID);
                ModObjectRegistry.finishCreation(block, buildData);
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
                // Load data from BlockBuildData
                if (data.tiles != null)
                {
                    for (Pair<String, Class<? extends TileEntity>> par : data.tiles)
                    {
                        proxy.regiserTileEntity(par.left(), par.right());
                    }
                }
                if (data.creativeTab != null)
                {
                    block.setCreativeTab(data.creativeTab);
                }
            }
        }
        if (block instanceof IExtraInfo)
        {
            if (((IExtraInfo) block).hasExtraConfigs())
            {
                Configuration extraBlockConfig = new Configuration(new File(Loader.instance().getConfigDir(), "Dark/blocks/" + block.getUnlocalizedName() + ".cfg"));
                extraBlockConfig.load();
                ((IExtraInfo) block).loadExtraConfigs(extraBlockConfig);
                extraBlockConfig.save();
            }
            if (block instanceof IExtraBlockInfo)
            {
                ((IExtraBlockInfo) block).loadOreNames();
                Set<Pair<String, Class<? extends TileEntity>>> tileListNew = new HashSet<Pair<String, Class<? extends TileEntity>>>();
                ((IExtraBlockInfo) block).getTileEntities(block.blockID, tileListNew);
                for (Pair<String, Class<? extends TileEntity>> par : tileListNew)
                {
                    proxy.regiserTileEntity(par.left(), par.right());
                }
            }
        }

    }

    /** Method to get block via name
    *
    * @param blockName
    * @return Block requested */
   public static Block getBlock(String blockName)
   {
       for (Entry<Block,String> entry : registredBlocks.entrySet())
       {
           String name = entry.getKey().getUnlocalizedName().replace("tile.", "");
           if (name.equalsIgnoreCase(blockName))
           {
               return entry.getKey();
           }
       }
       return null;
   }

   /** Method to get block via id
    *
    * @param blockID
    * @return Block requested */
   public static Block getBlock(int blockID)
   {
       for (Entry<Block,String> entry : registredBlocks.entrySet())
       {
           if (entry.getKey().blockID == blockID)
           {
               return entry.getKey();
           }
       }
       return null;
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
                proxy.registerBlock(fluidBlock, null, "DMBlockFluid" + fluid.getName(), modDomainPrefix);
            }
            else
            {
                fluidBlock = Block.blocksList[fluid.getBlockID()];
            }
        }

        return fluidBlock;
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
