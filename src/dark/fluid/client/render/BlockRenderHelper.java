package dark.fluid.client.render;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.IBlockAccess;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import dark.core.prefab.ModPrefab;
import dark.fluid.client.model.ModelConstructionPump;
import dark.fluid.client.model.ModelPump;
import dark.fluid.client.model.ModelSink;
import dark.fluid.client.model.ModelTankSide;
import dark.fluid.common.FMRecipeLoader;
import dark.fluid.common.FluidMech;
import dark.mech.client.model.ModelGearRod;
import dark.mech.client.model.ModelGenerator;

@SideOnly(Side.CLIENT)
public class BlockRenderHelper implements ISimpleBlockRenderingHandler
{
    public static BlockRenderHelper instance = new BlockRenderHelper();
    public static int renderID = RenderingRegistry.getNextAvailableRenderId();
    private ModelPump modelPump = new ModelPump();
    private ModelGearRod modelRod = new ModelGearRod();
    private ModelGenerator modelGen = new ModelGenerator();
    private ModelSink sink = new ModelSink();
    private ModelConstructionPump conPump = new ModelConstructionPump();

    @Override
    public void renderInventoryBlock(Block block, int metadata, int modelID, RenderBlocks renderer)
    {
        GL11.glPushMatrix();
        if (FMRecipeLoader.blockPumpMachine != null && block.blockID == FMRecipeLoader.blockPumpMachine.blockID && metadata < 4)
        {
            GL11.glTranslatef(0.0F, 1.1F, 0.0F);
            GL11.glRotatef(180f, 0f, 0f, 1f);

            FMLClientHandler.instance().getClient().renderEngine.bindTexture(new ResourceLocation(FluidMech.instance.DOMAIN, ModPrefab.MODEL_DIRECTORY + "pumps/WaterPump.png"));
            modelPump.render(0.0725F);
            modelPump.renderMotion(0.0725F, 0);
        }
        else if (FMRecipeLoader.blockSink != null && block.blockID == FMRecipeLoader.blockSink.blockID)
        {
            GL11.glTranslatef(0.0F, .8F, 0.0F);
            GL11.glRotatef(180f, 0f, 0f, 1f);
            FMLClientHandler.instance().getClient().renderEngine.bindTexture(new ResourceLocation(FluidMech.instance.DOMAIN, ModPrefab.MODEL_DIRECTORY + "Sink.png"));
            sink.render(0.0565F);
        }
        else  if (FMRecipeLoader.blockRod != null && block.blockID == FMRecipeLoader.blockRod.blockID)
        {
            GL11.glTranslatef(0.0F, 1.5F, 0.0F);
            GL11.glRotatef(180f, 0f, 0f, 1f);
            FMLClientHandler.instance().getClient().renderEngine.bindTexture(new ResourceLocation(FluidMech.instance.DOMAIN, ModPrefab.MODEL_DIRECTORY + "mechanical/GearRod.png"));
            modelRod.render(0.0825F, 0);
        }
        else if (FMRecipeLoader.blockGenerator != null && block.blockID == FMRecipeLoader.blockGenerator.blockID)
        {
            GL11.glTranslatef(0.0F, 1.0F, 0.0F);
            GL11.glRotatef(180f, 0f, 0f, 1f);
            FMLClientHandler.instance().getClient().renderEngine.bindTexture(new ResourceLocation(FluidMech.instance.DOMAIN, ModPrefab.MODEL_DIRECTORY + "mechanical/Generator.png"));
            modelGen.render(null);
        }
        else if (FMRecipeLoader.blockConPump != null && block.blockID == FMRecipeLoader.blockConPump.blockID && metadata < 4)
        {
            GL11.glTranslatef(0.0F, 1.2F, 0.0F);
            GL11.glRotatef(180f, 0f, 0f, 1f);
            FMLClientHandler.instance().getClient().renderEngine.bindTexture(new ResourceLocation(FluidMech.instance.DOMAIN, ModPrefab.MODEL_DIRECTORY + "ConstructionPump.png"));
            conPump.render(0.0725F);
            conPump.renderMotor(0.0725F);

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
        return renderID;
    }
}
