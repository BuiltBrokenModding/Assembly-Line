package dark.assembly.common.machine.processor;

import java.util.List;
import java.util.Set;

import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Icon;
import net.minecraft.world.World;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.oredict.OreDictionary;
import universalelectricity.core.UniversalElectricity;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import dark.api.ProcessorRecipes.ProcessorType;
import dark.assembly.client.render.BlockRenderingHandler;
import dark.assembly.common.AssemblyLine;
import dark.assembly.common.CommonProxy;
import dark.assembly.common.TabAssemblyLine;
import dark.core.common.DarkMain;
import dark.core.common.items.EnumMeterials;
import dark.core.prefab.BlockMachine;
import dark.core.prefab.IExtraObjectInfo;
import dark.core.prefab.helpers.Pair;

public class BlockProcessor extends BlockMachine implements IExtraObjectInfo
{
    public static float crusherWattPerTick = .125f;
    public static float grinderWattPerTick = .125f;
    public static float pressWattPerTick = .2f;

    public BlockProcessor(int blockID)
    {
        super("OreProcessor", AssemblyLine.CONFIGURATION, blockID, UniversalElectricity.machine);
        this.setCreativeTab(TabAssemblyLine.INSTANCE);
    }

    @Override
    public boolean onMachineActivated(World world, int x, int y, int z, EntityPlayer entityPlayer, int side, float hitX, float hitY, float hitZ)
    {
        if (!world.isRemote)
        {
            entityPlayer.openGui(AssemblyLine.instance, CommonProxy.GUI_CRUSHER, world, x, y, z);
            return true;
        }
        return false;
    }

    @Override
    public boolean onSneakMachineActivated(World world, int x, int y, int z, EntityPlayer entityPlayer, int side, float hitX, float hitY, float hitZ)
    {
        //Open machine calibration menu
        return false;
    }

    @Override
    public boolean onUseWrench(World world, int x, int y, int z, EntityPlayer entityPlayer, int side, float hitX, float hitY, float hitZ)
    {
        int meta = world.getBlockMetadata(x, y, z);
        int g = meta % 4;
        if (g < 3)
        {
            world.setBlockMetadataWithNotify(x, y, z, meta + 1, 3);
            return true;
        }
        else
        {
            world.setBlockMetadataWithNotify(x, y, z, meta - 3, 3);
            return true;
        }
    }

    @Override
    public void getTileEntities(int blockID, Set<Pair<String, Class<? extends TileEntity>>> list)
    {
        list.add(new Pair<String, Class<? extends TileEntity>>("ALProcessor", TileEntityProcessor.class));
    }

    @Override
    public TileEntity createTileEntity(World world, int metadata)
    {
        return new TileEntityProcessor();
    }

    @Override
    public boolean hasExtraConfigs()
    {
        return true;
    }

    @Override
    public void loadExtraConfigs(Configuration config)
    {
        for (ProcessorData data : ProcessorData.values())
        {
            data.enabled = config.get(data.unlocalizedName, "Enabled", true).getBoolean(true);
            data.allowCrafting = config.get(data.unlocalizedName, "CanCraft", true).getBoolean(true);
            data.wattPerTick = (float) (config.get(data.unlocalizedName, "WattPerTick", data.wattPerTick).getDouble(data.wattPerTick) / 1000);
            data.processingTicks = config.get(data.unlocalizedName, "ProcessingTicks", data.processingTicks).getInt();
        }
    }

    @Override
    public void loadRecipes()
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void loadOreNames()
    {
        for (ProcessorData data : ProcessorData.values())
        {
            OreDictionary.registerOre(data.unlocalizedName + "OreProcessor", new ItemStack(this.blockID, 1, data.startMeta));
        }
    }

    @Override
    public boolean isOpaqueCube()
    {
        return false;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean renderAsNormalBlock()
    {
        return false;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public int getRenderType()
    {
        return BlockRenderingHandler.BLOCK_RENDER_ID;
    }

    @Override
    public void getSubBlocks(int par1, CreativeTabs par2CreativeTabs, List list)
    {
        for (ProcessorData data : ProcessorData.values())
        {
            if (data.enabled)
            {
                list.add(new ItemStack(par1, 1, data.startMeta));
            }
        }
    }

    public static enum ProcessorData
    {
        CRUSHER(ProcessorType.CRUSHER, "crusher", 125, 100, 0),
        GRINDER(ProcessorType.GRINDER, "grinder", 125, 120, 4),
        PRESS(ProcessorType.PRESS, "press", 200, 50, 8);
        public ProcessorType type;
        public String unlocalizedName;
        public float wattPerTick;
        public int processingTicks, startMeta;
        public boolean enabled = true;
        public boolean allowCrafting = true;

        private ProcessorData(ProcessorType type, String name, float watts, int ticks, int meta)
        {
            this.type = type;
            this.unlocalizedName = name;
            this.wattPerTick = watts;
            this.processingTicks = ticks;
            this.startMeta = meta;
        }
    }

}
