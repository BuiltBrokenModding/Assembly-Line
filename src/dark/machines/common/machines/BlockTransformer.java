package dark.machines.common.machines;

import net.minecraft.block.material.Material;
import net.minecraftforge.common.Configuration;
import universalelectricity.core.UniversalElectricity;
import dark.core.prefab.machine.BlockMachine;
import dark.machines.common.DMCreativeTab;
import dark.machines.common.DarkMain;

public class BlockTransformer extends BlockMachine
{
    public BlockTransformer(Configuration config, String blockName, Material material)
    {
        super(DarkMain.CONFIGURATION, "Transformer", UniversalElectricity.machine);
        this.setCreativeTab(DMCreativeTab.tabIndustrial);
    }
}
