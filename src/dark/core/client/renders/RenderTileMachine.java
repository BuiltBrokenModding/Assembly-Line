package dark.core.client.renders;

import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public abstract class RenderTileMachine extends TileEntitySpecialRenderer
{

    public RenderTileMachine()
    {

    }

    @Override
    public void renderTileEntityAt(TileEntity tileEntity, double d, double d1, double d2, float f)
    {
        this.renderModel(tileEntity, d, d1, d2, f);
    }

    public abstract void renderModel(TileEntity tileEntity, double x, double y, double z, float size);

    /** Sudo method for setting the texture for current render
     * 
     * @param name */
    public void bindTextureByName(String domain, String name)
    {
        this.bindTexture(new ResourceLocation(domain, name));
    }

    /** Gets the texture based on block and metadata mainly used by item/block inv render */
    public abstract ResourceLocation getTexture(int block, int meta);

}
