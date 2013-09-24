package dark.assembly.common.machine;

import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Icon;
import net.minecraft.world.World;
import universalelectricity.core.UniversalElectricity;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import dark.assembly.client.render.BlockRenderingHandler;
import dark.assembly.common.AssemblyLine;
import dark.assembly.common.imprinter.prefab.BlockImprintable;
import dark.core.registration.ModObjectRegistry.BlockBuildData;

public class BlockRejector extends BlockImprintable
{
    @SideOnly(Side.CLIENT)
    protected Icon front;

    public BlockRejector()
    {
        super(new BlockBuildData(BlockRejector.class, "rejector", UniversalElectricity.machine));
    }

    @Override
    public TileEntity createNewTileEntity(World var1)
    {
        return new TileEntityRejector();
    }

    @SideOnly(Side.CLIENT)
    @Override
    public int getRenderType()
    {
        return this.zeroRendering ? 0 : BlockRenderingHandler.BLOCK_RENDER_ID;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void registerIcons(IconRegister par1IconRegister)
    {
        this.blockIcon = par1IconRegister.registerIcon(AssemblyLine.instance.PREFIX + "imprinter_bottom");
        this.front = par1IconRegister.registerIcon(AssemblyLine.instance.PREFIX + "disk_tray");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public Icon getIcon(int side, int metadata)
    {
        if (side == metadata)
        {
            return this.front;
        }
        return this.blockIcon;
    }

}
