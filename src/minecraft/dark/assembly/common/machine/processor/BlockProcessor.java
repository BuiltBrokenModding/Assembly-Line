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
import dark.assembly.common.AssemblyLine;
import dark.assembly.common.TabAssemblyLine;
import dark.assembly.common.machine.processor.ProcessorRecipes.ProcessorType;
import dark.core.common.DarkMain;
import dark.core.prefab.BlockMachine;
import dark.core.prefab.IExtraObjectInfo;
import dark.core.prefab.helpers.Pair;

public class BlockProcessor extends BlockMachine implements IExtraObjectInfo
{

    public BlockProcessor(int blockID)
    {
        super("OreProcessor", AssemblyLine.CONFIGURATION, blockID, UniversalElectricity.machine);
        this.setCreativeTab(TabAssemblyLine.INSTANCE);
    }

    @Override
    public boolean onMachineActivated(World world, int x, int y, int z, EntityPlayer entityPlayer, int side, float hitX, float hitY, float hitZ)
    {
        //TODO activate GUI, and if GS is installed do user lock protection
        //Maybe later add support for button activation to cause animation of the crusher to activate
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
        if (metadata == 0 || metadata == 1)
        {
            return new TileEntityProcessor(ProcessorType.CRUSHER);
        }
        return super.createTileEntity(world, metadata);
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
