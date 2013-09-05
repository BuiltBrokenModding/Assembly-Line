package dark.core.prefab;

import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Icon;
import net.minecraft.world.World;
import net.minecraftforge.common.Configuration;
import universalelectricity.prefab.block.BlockTile;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import dark.api.parts.INetworkPart;
import dark.core.common.DarkMain;

/** Basic TileEntity Container class designed to be used by generic machines. It is suggested that
 * each mod using this create there own basic block extending this to reduce need to input config
 * file each time
 * 
 * @author Darkguardsman */
public abstract class BlockMachine extends BlockTile implements ITileEntityProvider
{
    /** @param name - The name the block will use for both the config and translation file
     * @param config - configuration reference used to pull blockID from
     * @param blockID - Default block id to be used for the config
     * @param material - Block material used for tool reference? */
    public BlockMachine(String name, Configuration config, int blockID, Material material)
    {
        super(config.getBlock(name, blockID).getInt(), material);
        this.isBlockContainer = true;
        this.setUnlocalizedName(name);
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

}
