package dark.core.render;

import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public abstract class RenderMachine extends TileEntitySpecialRenderer
{

	public RenderMachine()
	{
		
	}

	@Override
	public void renderTileEntityAt(TileEntity tileentity, double d0, double d1, double d2, float f)
	{
		// TODO Auto-generated method stub

	}

	/** Sudo method for setting the texture for current render 
	 * @param name */
	public void bindTextureByName(String domain, String name)
	{
		func_110628_a(new ResourceLocation(domain,name));
	}

	public void bindTextureByName(ResourceLocation name)
	{
		func_110628_a(name);
	}

	/** Gets the texture based on block and metadata mainly used by item/block inv render */
	public abstract ResourceLocation getTexture(int block, int meta);

}
