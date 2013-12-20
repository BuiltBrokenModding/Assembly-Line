package dark.farmtech.machines;

import java.util.List;
import java.util.Set;

import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.Configuration;

import com.builtbroken.common.Pair;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import dark.core.prefab.machine.BlockMachine;
import dark.farmtech.FarmTech;

/** Mostly for looks but yields better cooking results than a normal furnace. Plus can create large
 * amounts of soup. This cooking pot should operate without a gui. Instead of using a gui a player
 * will need to click the pot with the items. This also means adding wood to the bottom and starting
 * it with a lighter. Water and food must also be added by hand.
 * 
 * 
 * @author DarkGuardsman */
public class BlockCookingPot extends BlockMachine
{
    public BlockCookingPot()
    {
        super(FarmTech.CONFIGURATION, "FTCookingPot", Material.iron);
    }

    @Override
    public TileEntity createTileEntity(World world, int meta)
    {
        int type = meta / 4;
        if (meta >= 0 && meta <= 3)
        {
            return CookingPots.values()[type].provider.createNewTileEntity(world);
        }
        return super.createTileEntity(world, meta);
    }

    @Override
    public TileEntity createNewTileEntity(World world)
    {
        return new TileEntityCookingPot();
    }

    @Override
    public void getTileEntities(int blockID, Set<Pair<String, Class<? extends TileEntity>>> list)
    {
        list.add(new Pair<String, Class<? extends TileEntity>>("FTCookingPot", TileEntityCookingPot.class));
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void getClientTileEntityRenderers(List<Pair<Class<? extends TileEntity>, TileEntitySpecialRenderer>> list)
    {

    }

    @Override
    public void loadExtraConfigs(Configuration config)
    {
        super.loadExtraConfigs(config);
    }

    public static enum CookingPots
    {
        STONE(new ITileEntityProvider()
        {

            @Override
            public TileEntity createNewTileEntity(World world)
            {
                return new TileEntityCookingPot();
            }
        }),
        IRON(new ITileEntityProvider()
        {

            @Override
            public TileEntity createNewTileEntity(World world)
            {
                return new TileEntityCookingPot();
            }
        }),
        STEEL(new ITileEntityProvider()
        {

            @Override
            public TileEntity createNewTileEntity(World world)
            {
                return new TileEntityCookingPot();
            }
        }),
        OBBY(new ITileEntityProvider()
        {

            @Override
            public TileEntity createNewTileEntity(World world)
            {
                return new TileEntityHellCookingPot();
            }
        });
        public final ITileEntityProvider provider;

        private CookingPots(ITileEntityProvider provider)
        {
            this.provider = provider;
        }
    }
}
