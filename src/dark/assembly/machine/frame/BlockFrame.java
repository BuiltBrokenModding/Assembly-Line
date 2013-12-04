package dark.assembly.machine.frame;

import java.util.List;
import java.util.Set;

import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import com.builtbroken.common.Pair;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import dark.core.prefab.machine.BlockMachine;
import dark.machines.DarkMain;

public class BlockFrame extends BlockMachine
{
    public BlockFrame()
    {
        super(DarkMain.CONFIGURATION, "DMFrame", Material.iron);
    }

    @Override
    public TileEntity createTileEntity(World world, int metadata)
    {
        if (metadata >= 0 && metadata < 4)
        {
            return new TileEntityFrame();
        }
        else if (metadata >= 4 && metadata < 8)
        {
            return new TileEntityFrameMotor();
        }
        return super.createTileEntity(world, metadata);
    }

    @Override
    public void getTileEntities(int blockID, Set<Pair<String, Class<? extends TileEntity>>> list)
    {
        list.add(new Pair("DMFrame", TileEntityFrame.class));
        list.add(new Pair("DMFrameMotor", TileEntityFrameMotor.class));
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void getClientTileEntityRenderers(List<Pair<Class<? extends TileEntity>, TileEntitySpecialRenderer>> list)
    {

    }

    public enum FrameData
    {
        WOOD_FRAME(),
        IRON_FRAME(),
        STEEL_FRAME(),
        BRONZE_FRAME(),
        BASIC_MOTOR(),
        ADEPT_MOTOR(),
        ADVANCED_MOTOR(),
        ELITE_MOTOR();
    }
}
