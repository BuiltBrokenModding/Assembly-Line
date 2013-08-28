package dark.core;

import java.util.HashMap;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.tileentity.TileEntity;

/** Handler to make registering all parts of a block a bit easier
 *
 * @author DarkGuardsman */
public class BlockRegistry
{
    private static HashMap<Block, BlockData> blockMap = new HashMap<Block, BlockData>();

    public static void addBlockToRegister(BlockData data)
    {
        if (data != null && data.block != null && !blockMap.containsKey(data.block))
        {
            blockMap.put(data.block, data);
        }
    }

    /** Used to store info on the block that will later be used to register all parts of the block */
    public static class BlockData
    {
        public Block block;
        public Class<? extends ItemBlock> itemBlock;
        public String modBlockID;
        public HashMap<String, TileEntity> tiles = new HashMap<String, TileEntity>();

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
