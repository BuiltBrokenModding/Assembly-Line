package dark.core;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.Configuration;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.registry.GameRegistry;
import dark.core.helpers.Pair;

/** Handler to make registering all parts of a block a bit easier
 *
 * @author DarkGuardsman */
public class BlockRegistry
{
    private static Configuration masterBlockConfig = new Configuration(new File(Loader.instance().getConfigDir(), "Dark/EnabledBlocks.cfg"));
    private static HashMap<Block, BlockData> blockMap = new HashMap<Block, BlockData>();

    /** Adds a block to the mapping to be registered next call */
    public static void addBlockToRegister(BlockData data)
    {
        synchronized (blockMap)
        {
            if (data != null && data.block != null && !blockMap.containsKey(data.block))
            {
                blockMap.put(data.block, data);
            }
        }
    }

    public static void addBlocks(List<BlockData> data)
    {
        for (BlockData entry : data)
        {
            BlockRegistry.addBlockToRegister(entry);
        }
    }

    /** Runs threw the list of entries and then registers all parts */
    public static void registerAllBlocks()
    {
        masterBlockConfig.load();
        synchronized (blockMap)
        {
            HashMap<String, TileEntity> tileList = new HashMap<String, TileEntity>();
            for (Entry<Block, BlockData> entry : blockMap.entrySet())
            {
                BlockData blockData = entry.getValue();
                Block block = blockData.block;
                if (!blockData.allowDisable || blockData.allowDisable && masterBlockConfig.get("EnabledList", "Enable_" + blockData.block.getUnlocalizedName(), true).getBoolean(true))
                {
                    if (blockData.itemBlock != null)
                    {
                        GameRegistry.registerBlock(blockData.block, blockData.itemBlock, blockData.modBlockID);
                    }
                    else
                    {
                        GameRegistry.registerBlock(blockData.block, blockData.modBlockID);
                    }
                    if (blockData.block instanceof IExtraObjectInfo)
                    {
                        if (((IExtraObjectInfo) block).hasExtraConfigs())
                        {
                            Configuration extraBlockConfig = new Configuration(new File(Loader.instance().getConfigDir(), "Dark/blocks/" + block.getUnlocalizedName() + ".cfg"));
                            extraBlockConfig.load();
                            ((IExtraObjectInfo) block).loadExtraConfigs(extraBlockConfig);
                            extraBlockConfig.save();
                        }
                        ((IExtraObjectInfo) block).loadOreNames();
                        Set<Pair<String, TileEntity>> tileListNew = new HashSet<Pair<String, TileEntity>>();
                        ((IExtraObjectInfo) block).getTileEntities(block.blockID, tileListNew);
                        for (Pair<String, TileEntity> par : tileListNew)
                        {
                            if (!tileList.containsKey(par.getKey()) && !tileList.containsValue(par.getValue()))
                            {
                                tileList.put(par.getKey(), par.getValue());
                            }
                            else if(par.getValue() != null)
                            {
                                System.out.println("BlockRegistry tried to list a tile or tileName that was already listed");
                                System.out.println("Tile>" + par.getValue().toString() + " | Name>" + par.getKey());
                            }
                        }
                    }
                }
            }
        }
        masterBlockConfig.save();
    }

    /** Used to store info on the block that will later be used to register all parts of the block */
    public static class BlockData
    {
        public Block block;
        public Class<? extends ItemBlock> itemBlock;
        public String modBlockID;
        public HashMap<String, TileEntity> tiles = new HashMap<String, TileEntity>();
        public boolean allowDisable = true;

        public BlockData(Block block, String name)
        {
            this.block = block;
            this.modBlockID = name;
        }

        public BlockData(Block block, Class<? extends ItemBlock> itemBlock, String name)
        {
            this(block, name);
            this.itemBlock = itemBlock;
        }

        /** Adds a tileEntity to be registered when this block is registered
         *
         * @param name - mod name for the tileEntity, should be unique
         * @param tile - new instance of the TileEntity to register */
        public BlockData addTileEntity(String name, TileEntity tile)
        {
            if (!this.tiles.containsKey(name))
            {
                this.tiles.put(name, tile);
            }
            return this;
        }
    }
}
