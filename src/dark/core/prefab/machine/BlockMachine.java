package dark.core.prefab.machine;

import java.util.Set;

import net.minecraft.block.ITileEntityProvider;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Icon;
import net.minecraft.world.World;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.oredict.OreDictionary;
import universalelectricity.prefab.block.BlockTile;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import dark.api.parts.INetworkPart;
import dark.core.common.DarkMain;
import dark.core.prefab.IExtraInfo.IExtraBlockInfo;
import dark.core.prefab.ModPrefab;
import dark.core.prefab.helpers.Pair;
import dark.core.registration.ModObjectRegistry.BlockBuildData;

/** Basic TileEntity Container class designed to be used by generic machines. It is suggested that
 * each mod using this create there own basic block extending this to reduce need to use build
 * data per block.
 *
 * @author Darkguardsman */
public abstract class BlockMachine extends BlockTile implements IExtraBlockInfo
{

    public boolean zeroAnimation, zeroSound, zeroRendering;

    public BlockMachine(BlockBuildData data)
    {
        super(data.config.getBlock(data.blockName, ModPrefab.getNextID()).getInt(), data.blockMaterial);
        this.setUnlocalizedName(data.blockName);
        this.setResistance(100f);
        if (data.creativeTab != null)
        {
            this.setCreativeTab(data.creativeTab);
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IconRegister iconReg)
    {
        if (this.blockIcon == null)
        {
            this.blockIcon = iconReg.registerIcon(DarkMain.getInstance().PREFIX + "machine");
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public Icon getIcon(int par1, int par2)
    {
        return this.blockIcon;
    }

    /** Called whenever the block is added into the world. Args: world, x, y, z */
    @Override
    public void onBlockAdded(World world, int x, int y, int z)
    {
        super.onBlockAdded(world, x, y, z);
        TileEntity tileEntity = world.getBlockTileEntity(x, y, z);
        if (tileEntity instanceof INetworkPart)
        {
            ((INetworkPart) tileEntity).refresh();
        }
    }

    @Override
    public void onNeighborBlockChange(World world, int x, int y, int z, int blockID)
    {
        super.onNeighborBlockChange(world, x, y, z, blockID);
        TileEntity tileEntity = world.getBlockTileEntity(x, y, z);

        if (tileEntity instanceof INetworkPart)
        {
            ((INetworkPart) tileEntity).refresh();
        }
    }

    @Override
    public TileEntity createTileEntity(World world, int metadata)
    {
        return super.createTileEntity(world, metadata);
    }

    @Override
    public void getTileEntities(int blockID, Set<Pair<String, Class<? extends TileEntity>>> list)
    {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean hasExtraConfigs()
    {
        return true;
    }

    @Override
    public void loadExtraConfigs(Configuration config)
    {
        if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT)
        {
            this.zeroAnimation = config.get("Effects--Not_Supported_By_All_Blocks", "disableAnimation", false, "Turns off animations of the block").getBoolean(false);
            this.zeroRendering = config.get("Effects--Not_Supported_By_All_Blocks", "disableRender", false, "Turns off the block render replacing it with a normal block").getBoolean(false);
            this.zeroSound = config.get("Effects--Not_Supported_By_All_Blocks", "disableSound", false, "Turns of sound of the block for any or its actions").getBoolean(false);
        }
    }

    @Override
    public void loadOreNames()
    {
        OreDictionary.registerOre(this.getUnlocalizedName().replace("tile.", ""), this);

    }

}
