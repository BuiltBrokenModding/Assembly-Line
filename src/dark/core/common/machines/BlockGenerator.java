package dark.core.common.machines;

import net.minecraft.block.material.Material;
import net.minecraftforge.common.Configuration;
import dark.core.common.DarkMain;
import dark.core.prefab.BlockMachine;

public class BlockGenerator extends BlockMachine
{

    public BlockGenerator(String name, Configuration config, int blockID, Material material)
    {
        super("generator", DarkMain.CONFIGURATION, blockID, material);
        // TODO Auto-generated constructor stub
    }

}
