package dark.core.common.machines;

import java.util.Set;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.Configuration;
import universalelectricity.core.UniversalElectricity;
import dark.core.client.renders.BlockRenderingHandler;
import dark.core.common.DMCreativeTab;
import dark.core.prefab.IExtraObjectInfo;
import dark.core.prefab.helpers.Pair;
import dark.core.prefab.machine.BlockMachine;
import dark.core.registration.ModObjectRegistry.BlockBuildData;

public class BlockSolarPanel extends BlockMachine implements IExtraObjectInfo
{
    public static float tickRate = 10;
    public static float wattPerLightValue = .012f;

    public BlockSolarPanel()
    {
        super(new BlockBuildData(BlockSolarPanel.class, "BlockSolarPanel", UniversalElectricity.machine));
        this.setBlockBounds(0, 0, 0, 1f, .3f, 1f);
        this.setCreativeTab(DMCreativeTab.tabIndustrial);
    }

    public TileEntity createNewTileEntity(World world)
    {
        return new TileEntitySolarPanel();
    }

    @Override
    public boolean isOpaqueCube()
    {
        return false;
    }

    @Override
    public boolean renderAsNormalBlock()
    {
        return false;
    }

    @Override
    public int getRenderType()
    {
        return BlockRenderingHandler.BLOCK_RENDER_ID;
    }

    @Override
    public void getTileEntities(int blockID, Set<Pair<String, Class<? extends TileEntity>>> list)
    {
       list.add(new Pair<String,Class<? extends TileEntity>>("DMSolarCell", TileEntitySolarPanel.class));

    }

    @Override
    public boolean hasExtraConfigs()
    {
        return true;
    }

    @Override
    public void loadExtraConfigs(Configuration config)
    {
        tickRate = config.get("settings", "PanelUpdateRate", tickRate).getInt();
        wattPerLightValue = (config.get("settings", "WattPerLightvalue", (int) wattPerLightValue * 1000, "Value * 15 equals full output").getInt() / 1000);
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
