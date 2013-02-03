package liquidmechanics.common.block.liquids;

import liquidmechanics.api.liquids.LiquidFiniteFlowing;

public class BlockOilFlowing extends LiquidFiniteFlowing
{
    public BlockOilFlowing(int i)
    {
        super(i);
        this.setBlockName("OilFlowing");
    }
}
