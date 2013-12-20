package dark.machines.machines;

import com.dark.IndustryTabs;
import com.dark.prefab.BlockMachine;

import net.minecraft.block.material.Material;
import net.minecraftforge.common.Configuration;
import universalelectricity.core.UniversalElectricity;
import dark.machines.CoreMachine;

public class BlockTransformer extends BlockMachine
{
    public BlockTransformer(Configuration config, String blockName, Material material)
    {
        super(CoreMachine.CONFIGURATION, "Transformer", UniversalElectricity.machine);
        this.setCreativeTab(IndustryTabs.tabIndustrial());
    }
}
