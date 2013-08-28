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
import dark.fluid.client.model.ModelConstructionPump;
import dark.fluid.client.model.ModelPump;
import dark.fluid.client.model.ModelSink;
import dark.fluid.client.model.ModelTankSide;
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
    private ModelTankSide tank = new ModelTankSide();
    private ModelSink sink = new ModelSink();
    private ModelConstructionPump conPump = new ModelConstructionPump();

    @Override
    public void renderInventoryBlock(Block block, int metadata, int modelID, RenderBlocks renderer)
    {
        GL11.glPushMatrix();
        if (block.blockID == FluidMech.recipeLoader.blockMachine.blockID && metadata < 4)
        {
            GL11.glTranslatef((float) 0.0F, (float) 1.1F, (float) 0.0F);
            GL11.glRotatef(180f, 0f, 0f, 1f);

            FMLClientHandler.instance().getClient().renderEngine.func_110577_a(new ResourceLocation(FluidMech.instance.DOMAIN, FluidMech.MODEL_DIRECTORY + "pumps/WaterPump.png"));
            modelPump.render(0.0725F);
            modelPump.renderMotion(0.0725F, 0);
        }
        else if (block.blockID == FluidMech.recipeLoader.blockSink.blockID)
        {
            GL11.glTranslatef((float) 0.0F, (float) .8F, (float) 0.0F);
            GL11.glRotatef(180f, 0f, 0f, 1f);
            FMLClientHandler.instance().getClient().renderEngine.func_110577_a(new ResourceLocation(FluidMech.instance.DOMAIN, FluidMech.MODEL_DIRECTORY + "Sink.png"));
            sink.render(0.0565F);
        }
        else if (block.blockID == FluidMech.recipeLoader.blockTank.blockID)
        {
            FMLClientHandler.instance().getClient().renderEngine.func_110577_a(new ResourceLocation(FluidMech.instance.getDomain(), metadata == 1 ? "/textures/blocks/obsidian.png" : "/textures/blocks/iron_block.png"));
            GL11.glTranslatef((float) 0.0F, (float) -0.9F, (float) 0.0F);
            tank.render(0.0625F, false, false, false, false);
            GL11.glRotatef(90f, 0f, 1f, 0f);
            tank.render(0.0625F, false, false, false, false);
            GL11.glRotatef(90f, 0f, 1f, 0f);
            tank.render(0.0625F, false, false, false, false);
            GL11.glRotatef(90f, 0f, 1f, 0f);
            tank.render(0.0625F, false, false, false, false);
        }
        if (block.blockID == FluidMech.recipeLoader.blockRod.blockID)
        {
            GL11.glTranslatef((float) 0.0F, (float) 1.5F, (float) 0.0F);
            GL11.glRotatef(180f, 0f, 0f, 1f);
            FMLClientHandler.instance().getClient().renderEngine.func_110577_a(new ResourceLocation(FluidMech.instance.DOMAIN, FluidMech.MODEL_DIRECTORY + "mechanical/GearRod.png"));
            modelRod.render(0.0825F, 0);
        }
        else if (block.blockID == FluidMech.recipeLoader.blockGenerator.blockID)
        {
            GL11.glTranslatef((float) 0.0F, (float) 1.0F, (float) 0.0F);
            GL11.glRotatef(180f, 0f, 0f, 1f);
            FMLClientHandler.instance().getClient().renderEngine.func_110577_a(new ResourceLocation(FluidMech.instance.DOMAIN, FluidMech.MODEL_DIRECTORY + "mechanical/Generator.png"));
            modelGen.render(null);
        }
        else if (block.blockID == FluidMech.recipeLoader.blockConPump.blockID && metadata < 4)
        {
            GL11.glTranslatef((float) 0.0F, (float) 1.2F, (float) 0.0F);
            GL11.glRotatef(180f, 0f, 0f, 1f);
            FMLClientHandler.instance().getClient().renderEngine.func_110577_a(new ResourceLocation(FluidMech.instance.DOMAIN, FluidMech.MODEL_DIRECTORY + "ConstructionPump.png"));
            conPump.render(0.0725F);
            conPump.renderMotor(0.0725F);

        }
        GL11.glPopMatrix();
    }

    public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer)
    {
        return false;
    }

    public boolean shouldRender3DInInventory()
    {

        return true;
    }

    public int getRenderId()
    {
        return renderID;
    }
}
