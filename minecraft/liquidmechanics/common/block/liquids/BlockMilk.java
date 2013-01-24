package liquidmechanics.common.block.liquids;

import liquidmechanics.api.liquids.LiquidFiniteStill;

public class BlockMilk extends LiquidFiniteStill
{
    public BlockMilk(int i)
    {
        super(i);
        this.setBlockName("MilkStill");
    }
}
