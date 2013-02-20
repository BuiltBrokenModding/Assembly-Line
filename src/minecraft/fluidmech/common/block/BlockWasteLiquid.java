package fluidmech.common.block;

import net.minecraft.block.BlockFluid;
import net.minecraft.block.material.Material;
import net.minecraftforge.liquids.ILiquid;

public class BlockWasteLiquid extends BlockFluid implements ILiquid
{
    //TODO turn into a lava type liquid with chance of corrupting connected water blocks
    public BlockWasteLiquid(int par1)
    {
        super(par1, Material.water);
    }

    @Override
    public int stillLiquidId()
    {
        return this.blockID;
    }

    @Override
    public boolean isMetaSensitive()
    {
        return false;
    }

    @Override
    public int stillLiquidMeta()
    {
        return this.blockID;
    }

}
