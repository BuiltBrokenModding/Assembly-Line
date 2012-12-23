package dark.BasicUtilities.Blocks;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraftforge.liquids.ILiquid;

public class BlockSteam extends Block implements ILiquid
{
    public static int blockID;
    public BlockSteam(int par1)
    {
        super(par1, Material.air);
        blockID = par1;
    }

    @Override
    public int stillLiquidId()
    {
        return blockID;
    }

    @Override
    public boolean isMetaSensitive()
    {
        return false;
    }

    @Override
    public int stillLiquidMeta()
    {
        return 0;
    }

}
