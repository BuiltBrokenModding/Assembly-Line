package dark.core.common.machines;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.Configuration;
import universalelectricity.core.UniversalElectricity;
import dark.core.client.renders.BlockRenderingHandler;
import dark.core.common.DMCreativeTab;
import dark.core.prefab.machine.BlockMachine;
import dark.core.registration.BlockConfigFile;
import dark.core.registration.ModObjectRegistry.BlockBuildData;
import dark.core.registration.BlockTileEntityInfo;

@BlockTileEntityInfo(tileEntities = { TileEntitySolarPanel.class }, tileEntitiesNames = { "DMSolarCell" })
public class BlockSolarPanel extends BlockMachine
{
    public static float tickRate = 10;
    public static float wattPerLightValue = .012f;

    public BlockSolarPanel()
    {
        super(new BlockBuildData(BlockSolarPanel.class, "BlockSolarPanel", UniversalElectricity.machine));
        this.setBlockBounds(0, 0, 0, 1f, .3f, 1f);
        this.setCreativeTab(DMCreativeTab.tabIndustrial);
    }

    @BlockConfigFile
    public void loadBlockConfig(Configuration config)
    {
        tickRate = config.get("settings", "PanelUpdateRate", tickRate).getInt();
        wattPerLightValue = (config.get("settings", "WattPerLightvalue", (int) wattPerLightValue * 1000, "Value * 15 equals full output").getInt() / 1000);
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
}
