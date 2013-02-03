package liquidmechanics.common.block.liquids;

import liquidmechanics.api.liquids.LiquidFiniteStill;

public class BlockOil extends LiquidFiniteStill
{
    public BlockOil(int i)
    {
        super(i);
        this.setBlockName("OilStill");
    }
}
