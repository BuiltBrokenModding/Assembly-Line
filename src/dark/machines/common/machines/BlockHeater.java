package dark.machines.common.machines;

import java.util.List;
import java.util.Set;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import net.minecraftforge.common.Configuration;

import com.builtbroken.common.Pair;

import dark.core.ModObjectRegistry.BlockBuildData;
import dark.core.prefab.machine.BlockMachine;
import dark.machines.common.CommonProxy;
import dark.machines.common.DarkMain;

public class BlockHeater extends BlockMachine
{

    public BlockHeater(BlockBuildData data)
    {
        super(data);
    }

    @Override
    public boolean onUseWrench(World world, int x, int y, int z, EntityPlayer entityPlayer, int side, float hitX, float hitY, float hitZ)
    {
        if (world.getBlockMetadata(x, y, z) % 4 < 3)
        {
            world.setBlockMetadataWithNotify(x, y, z, world.getBlockMetadata(x, y, z) + 1, 3);
            return true;
        }
        else
        {
            world.setBlockMetadataWithNotify(x, y, z, world.getBlockMetadata(x, y, z) - 3, 3);
            return true;
        }
    }

    @Override
    public void getTileEntities(int blockID, Set<Pair<String, Class<? extends TileEntity>>> list)
    {
        for (HeatMachineData data : HeatMachineData.values())
        {
            list.add(new Pair<String, Class<? extends TileEntity>>("DC" + data.unlocalizedName, data.clazz));
        }
    }

    @Override
    public void loadExtraConfigs(Configuration config)
    {
        super.loadExtraConfigs(config);
    }

    /** Called when the block is right clicked by the player */
    @Override
    public boolean onMachineActivated(World par1World, int x, int y, int z, EntityPlayer par5EntityPlayer, int side, float hitX, float hitY, float hitZ)
    {

        if (!par1World.isRemote)
        {
            par5EntityPlayer.openGui(DarkMain.getInstance(), HeatMachineData.values()[par1World.getBlockMetadata(x, y, z) / 4].guiID, par1World, x, y, z);
        }

        return true;
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
    public TileEntity createTileEntity(World world, int metadata)
    {
        try
        {
            return HeatMachineData.values()[metadata / 4].clazz.newInstance();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return null;

    }

    @Override
    public void getSubBlocks(int par1, CreativeTabs par2CreativeTabs, List par3List)
    {
        par3List.add(new ItemStack(par1, 1, 0));
        par3List.add(new ItemStack(par1, 1, 8));
        par3List.add(new ItemStack(par1, 1, 12));
    }

    @Override
    public int damageDropped(int metadata)
    {
        return metadata / 4;
    }

    @Override
    public ItemStack getPickBlock(MovingObjectPosition target, World world, int x, int y, int z)
    {
        int id = idPicked(world, x, y, z);

        if (id == 0)
        {
            return null;
        }

        Item item = Item.itemsList[id];

        if (item == null)
        {
            return null;
        }

        int metadata = getDamageValue(world, x, y, z);

        return new ItemStack(id, 1, metadata);
    }

    public static enum HeatMachineData
    {
        HEAT_COUPLE("themalcouple", 0, CommonProxy.GUI_COAL_GEN, TileEntitySteamGen.class),
        HEAT_EXCHANGE("heatexchanger", 4, CommonProxy.GUI_FUEL_GEN, TileEntitySteamGen.class),
        HEAT_PLATE("heatplate", 8, CommonProxy.GUI_BATTERY_BOX, null);

        public String unlocalizedName;
        public int startMeta, guiID;
        public boolean enabled = true;
        public boolean allowCrafting = true;
        public Class<? extends TileEntity> clazz;

        private HeatMachineData(String name, int meta, int guiID, Class<? extends TileEntity> clazz)
        {
            this.unlocalizedName = name;
            this.startMeta = meta;
            this.guiID = guiID;
            this.clazz = clazz;
        }
    }
}
