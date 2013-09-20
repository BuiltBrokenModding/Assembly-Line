package dark.farmtech.machines;

import java.util.Set;

import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.Configuration;
import dark.core.prefab.IExtraObjectInfo;
import dark.core.prefab.helpers.Pair;
import dark.core.prefab.machine.BlockMachine;
import dark.core.registration.ModObjectRegistry.BlockBuildData;
import dark.farmtech.FarmTech;

/** Prefab class for all farm blocks to remove the need for some configuration of the super class
 * 
 * @author Darkguardsman */
public abstract class BlockFT extends BlockMachine implements IExtraObjectInfo
{
    private boolean hasConfigFile = false;

    public BlockFT(Class<? extends BlockMachine> blockClass, String name, Material material)
    {
        super(new BlockBuildData(blockClass, name, material).setCreativeTab(FarmTech.TabFarmTech));
    }

    @Override
    public void getTileEntities(int blockID, Set<Pair<String, Class<? extends TileEntity>>> list)
    {

    }

    @Override
    public boolean hasExtraConfigs()
    {
        return hasConfigFile;
    }

    @Override
    public void loadExtraConfigs(Configuration config)
    {

    }

    @Override
    public void loadRecipes()
    {

    }

    @Override
    public void loadOreNames()
    {

    }

}
