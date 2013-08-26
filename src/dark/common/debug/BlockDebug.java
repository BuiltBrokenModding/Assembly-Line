package dark.common.debug;

import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.Configuration;
import dark.core.DarkMain;
import dark.core.blocks.BlockMachine;

public class BlockDebug extends BlockMachine
{
    enum debugBlocks
    {
        INF_POWER("infPower", TileEntityInfSupply.class),
        INF_FLUID("infFluid", TileEntityInfFluid.class),
        VOID("void", TileEntityVoid.class),
        INF_LOAD("infLoad", TileEntityInfLoad.class);

        String name;
        Class<? extends TileEntity> clazz;

        private debugBlocks(String name, Class<? extends TileEntity> clazz)
        {
            this.name = name;
            this.clazz = clazz;
        }
    }

    public BlockDebug(int blockID, Configuration config)
    {
        super("DebugBlock", config, blockID, Material.clay);
    }

    @Override
    public TileEntity createTileEntity(World world, int metadata)
    {
        if (metadata < debugBlocks.values().length)
        {
            try
            {
                return debugBlocks.values()[metadata].clazz.newInstance();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        return super.createTileEntity(world, metadata);
    }

    @Override
    public TileEntity createNewTileEntity(World world)
    {
        // TODO Auto-generated method stub
        return null;
    }

}
