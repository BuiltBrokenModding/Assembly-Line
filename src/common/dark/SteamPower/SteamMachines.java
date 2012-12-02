package dark.SteamPower;

import java.util.Random;

import net.minecraft.src.CreativeTabs;
import net.minecraft.src.EntityItem;
import net.minecraft.src.EntityLiving;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.ItemStack;
import net.minecraft.src.Material;
import net.minecraft.src.MathHelper;
import net.minecraft.src.NBTTagCompound;
import net.minecraft.src.TileEntity;
import net.minecraft.src.World;
import dark.Library.prefab.TileEntityMachine;
import dark.SteamPower.boiler.TileEntityBoiler;
import dark.SteamPower.firebox.TileEntityFireBox;

public class SteamMachines extends universalelectricity.prefab.BlockMachine
{

    public SteamMachines(int par1)
    {
        super("machine", par1, Material.iron);
        this.setRequiresSelfNotify();
        this.setCreativeTab(CreativeTabs.tabBlock);
        this.setHardness(1f);
        this.setResistance(3f);
    }

    @Override
    public int damageDropped(int metadata)
    {
        return metadata;
    }

    /**
     * Called upon block activation (left or right click on the block.). The
     * three integers represent x,y,z of the block.
     */
    @Override
    public boolean onMachineActivated(World world, int x, int y, int z, EntityPlayer player)
    {
        TileEntity blockEntity = (TileEntity) world.getBlockTileEntity(x, y, z);
        if (!world.isRemote && !player.isSneaking() && blockEntity instanceof TileEntityFireBox)
        {
            TileEntity var6 = (TileEntityFireBox) world.getBlockTileEntity(x, y, z);
            player.openGui(SteamPowerMain.instance, 0, world, x, y, z);
            return true;
        }

        return false;
    }

    @Override
    public TileEntity createNewTileEntity(World var1, int meta)
    {
        if (meta < 4) { return new TileEntityFireBox();}
        if (meta == 4){return new TileEntityBoiler();}
        return null;
    }

    /**
     * Called when the block is placed in the world.
     */
    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLiving user)
    {
        int angle = MathHelper.floor_double((user.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;
        int metadata = world.getBlockMetadata(x, y, z);
        TileEntityMachine ent = (TileEntityMachine) world.getBlockTileEntity(x, y, z);
        
        if(ent instanceof TileEntityFireBox)
        {
           world.setBlockMetadata(x, y, z, angle); 
        }
    }
    @Override
    public boolean isOpaqueCube()
    {
        return false;
    }

    @Override
    public boolean renderAsNormalBlock()
    {
        return false;
    }

    @Override
    public int getRenderType()
    {
        return ItemRenderHelperS.renderID;
    }

}
