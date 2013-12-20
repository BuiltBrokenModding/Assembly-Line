package dark.machines.machines;

import net.minecraft.block.material.Material;
import net.minecraftforge.common.Configuration;
import universalelectricity.core.UniversalElectricity;

import com.builtbroken.assemblyline.AssemblyLine;
import com.builtbroken.minecraft.IndustryTabs;
import com.builtbroken.minecraft.prefab.BlockMachine;

public class BlockTransformer extends BlockMachine
{
    public BlockTransformer(Configuration config, String blockName, Material material)
    {
        super(AssemblyLine.CONFIGURATION, "Transformer", UniversalElectricity.machine);
        this.setCreativeTab(IndustryTabs.tabIndustrial());
    }
}
