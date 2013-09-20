package dark.core.client;

import java.awt.Color;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import universalelectricity.core.vector.Vector3;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import dark.core.client.renders.BlockRenderingHandler;
import dark.core.client.renders.RenderBlockWire;
import dark.core.client.renders.RenderBlockSolarPanel;
import dark.core.common.CommonProxy;
import dark.core.common.CoreRecipeLoader;
import dark.core.common.DarkMain;
import dark.core.common.machines.TileEntityBatteryBox;
import dark.core.common.machines.TileEntityCoalGenerator;
import dark.core.common.machines.TileEntityElectricFurnace;
import dark.core.common.machines.TileEntitySolarPanel;
import dark.core.common.transmit.TileEntityWire;
import dark.core.prefab.ModPrefab;

@SideOnly(Side.CLIENT)
public class ClientProxy extends CommonProxy
{

    /** Renders a laser beam from one power to another by a set color for a set time
     *
     * @param world - world this laser is to be rendered in
     * @param position - start vector3
     * @param target - end vector3
     * @param color - color of the beam
     * @param age - life of the beam in 1/20 secs */
    @Override
    public void renderBeam(World world, Vector3 position, Vector3 target, Color color, int age)
    {
        if (world.isRemote || FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT)
        {
            FMLClientHandler.instance().getClient().effectRenderer.addEffect(new FXBeam(world, position, target, color, ModPrefab.TEXTURE_DIRECTORY + "", age));
        }
    }

    @Override
    public void preInit()
    {
        RenderingRegistry.registerBlockHandler(new BlockRenderingHandler());
        //MinecraftForge.EVENT_BUS.register(SoundHandler.INSTANCE);
    }

    @Override
    public void init()
    {
        if (CoreRecipeLoader.blockWire != null)
        {
            ClientRegistry.bindTileEntitySpecialRenderer(TileEntityWire.class, new RenderBlockWire());
        }
        if (DarkMain.blockSolar != null)
        {
            ClientRegistry.bindTileEntitySpecialRenderer(TileEntitySolarPanel.class, new RenderBlockSolarPanel());
        }
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
    {
        TileEntity tileEntity = world.getBlockTileEntity(x, y, z);

        if (tileEntity != null)
        {
            if (tileEntity instanceof TileEntityBatteryBox)
            {
                return new GuiBatteryBox(player.inventory, ((TileEntityBatteryBox) tileEntity));
            }
            else if (tileEntity instanceof TileEntityCoalGenerator)
            {
                return new GuiCoalGenerator(player.inventory, ((TileEntityCoalGenerator) tileEntity));
            }
            else if (tileEntity instanceof TileEntityElectricFurnace)
            {
                return new GuiElectricFurnace(player.inventory, ((TileEntityElectricFurnace) tileEntity));
            }
        }

        return null;
    }
}
