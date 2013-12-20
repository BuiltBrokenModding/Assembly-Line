package dark.machines.client.renders;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.IBlockAccess;

import org.lwjgl.opengl.GL11;

import com.builtbroken.assemblyline.ALRecipeLoader;
import com.builtbroken.assemblyline.AssemblyLine;
import com.builtbroken.minecraft.DarkCore;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import dark.machines.client.models.ModelMachine;
import dark.machines.client.models.ModelSolarPanel;

@SideOnly(Side.CLIENT)
public class BlockRenderingHandler implements ISimpleBlockRenderingHandler
{
    public static BlockRenderingHandler instance = new BlockRenderingHandler();
    public static final int BLOCK_RENDER_ID = RenderingRegistry.getNextAvailableRenderId();
    private static ModelSolarPanel solarPanelModel = new ModelSolarPanel();

    @Override
    public void renderInventoryBlock(Block block, int metadata, int modelID, RenderBlocks renderer)
    {
        GL11.glPushMatrix();
        if (ALRecipeLoader.blockSolar != null && block.blockID == ALRecipeLoader.blockSolar.blockID)
        {
            FMLClientHandler.instance().getClient().renderEngine.bindTexture(RenderBlockSolarPanel.TEXTURE);
            GL11.glTranslatef(0.0F, 1.1F, 0.0F);
            GL11.glRotatef(180f, 0f, 0f, 1f);
            solarPanelModel.render(0.0625F);
        }
        else if (ALRecipeLoader.blockSteamGen != null && block.blockID == ALRecipeLoader.blockSteamGen.blockID)
        {
            ModelMachine model = RenderSteamGen.getModel(metadata);
            FMLClientHandler.instance().getClient().renderEngine.bindTexture(RenderSteamGen.getTexture(metadata));
            GL11.glTranslatef(0.0F, 1.1F, 0.0F);
            GL11.glRotatef(180f, 0f, 0f, 1f);
            model.render(0.0625F);
        }
        GL11.glPopMatrix();
    }

    @Override
    public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer)
    {
        return false;
    }

    @Override
    public boolean shouldRender3DInInventory()
    {
        return true;
    }

    @Override
    public int getRenderId()
    {
        return BLOCK_RENDER_ID;
    }
}
