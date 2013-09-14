package dark.assembly.common.machine.processor;

import java.util.Set;

import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Icon;
import net.minecraft.world.World;
import net.minecraftforge.common.Configuration;
import universalelectricity.core.UniversalElectricity;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import dark.assembly.client.render.BlockRenderingHandler;
import dark.assembly.common.AssemblyLine;
import dark.assembly.common.CommonProxy;
import dark.assembly.common.TabAssemblyLine;
import dark.core.common.DarkMain;
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
        if (meta == 0)
        {
            world.setBlockMetadataWithNotify(x, y, z, 1, 3);
            return true;
        }
        if (meta == 1)
        {
            world.setBlockMetadataWithNotify(x, y, z, 0, 3);
            return true;
        }
        return false;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IconRegister iconReg)
    {
        if (this.blockIcon == null)
        {
            this.blockIcon = iconReg.registerIcon(DarkMain.getInstance().PREFIX + "machine");
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public Icon getIcon(int par1, int par2)
    {
        return this.blockIcon;
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
        crusherWattPerTick = (float) (config.get("settings", "CrusherWattPerTick", 125).getDouble(125) / 1000);
        grinderWattPerTick = (float) (config.get("settings", "GrinderWattPerTick", 125).getDouble(125) / 1000);
        pressWattPerTick = (float) (config.get("settings", "PressWattPerTick", 200).getDouble(200) / 1000);
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

}
