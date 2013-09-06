package dark.assembly.common.machine;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import universalelectricity.core.UniversalElectricity;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import dark.assembly.client.render.BlockRenderingHandler;
import dark.assembly.common.TabAssemblyLine;
import dark.assembly.common.imprinter.prefab.BlockImprintable;

public class BlockRejector extends BlockImprintable
{
    public BlockRejector(int id)
    {
        super("rejector", id, UniversalElectricity.machine, TabAssemblyLine.INSTANCE);
    }

    @Override
    public TileEntity createNewTileEntity(World var1)
    {
        return new TileEntityRejector();
    }

    @Override
    public boolean isOpaqueCube()
    {
        return false;
    }

    @Override@SideOnly(Side.CLIENT)
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
