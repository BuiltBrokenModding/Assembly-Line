package dark.fluid.common;

import net.minecraft.block.material.Material;
import net.minecraftforge.common.Configuration;
import dark.core.prefab.BlockMachine;
import dark.core.prefab.IExtraObjectInfo;

public abstract class BlockFM extends BlockMachine implements IExtraObjectInfo
{

    public BlockFM(String name, int blockID, Material material)
    {
        super(name, FluidMech.CONFIGURATION, blockID, material);
        this.setCreativeTab(FluidMech.TabFluidMech);
    }

    @Override
    public boolean hasExtraConfigs()
    {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void loadExtraConfigs(Configuration config)
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void loadRecipes()
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void loadOreNames()
    {
        // TODO Auto-generated method stub

    }

}
