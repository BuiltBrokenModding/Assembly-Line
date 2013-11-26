package dark.core.common.machines;

import java.util.List;
import java.util.Random;
import java.util.Set;

import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Icon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.ForgeDirection;
import universalelectricity.core.UniversalElectricity;

import com.builtbroken.common.Pair;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import dark.core.client.renders.BlockRenderingHandler;
import dark.core.client.renders.RenderBasicMachine;
import dark.core.common.CommonProxy;
import dark.core.common.DMCreativeTab;
import dark.core.common.DarkMain;
import dark.core.helpers.MathHelper;
import dark.core.prefab.machine.BlockMachine;
import dark.core.registration.ModObjectRegistry.BlockBuildData;

public class BlockBasicMachine extends BlockMachine
{
    private Icon iconMachineSide;
    private Icon iconInput;
    private Icon iconOutput;

    private Icon iconCoalGenerator;
    private Icon iconBatteryBox;
    private Icon iconElectricFurnace;

    public BlockBasicMachine()
    {
        super(new BlockBuildData(BlockBasicMachine.class, "BasicMachine", UniversalElectricity.machine).setCreativeTab(DMCreativeTab.tabIndustrial));
        this.setStepSound(soundMetalFootstep);
    }

    @Override
    public void registerIcons(IconRegister par1IconRegister)
    {
        this.blockIcon = par1IconRegister.registerIcon(DarkMain.getInstance().PREFIX + "machine");
        this.iconInput = par1IconRegister.registerIcon(DarkMain.getInstance().PREFIX + "machine_input");
        this.iconOutput = par1IconRegister.registerIcon(DarkMain.getInstance().PREFIX + "machine_output");

        this.iconMachineSide = par1IconRegister.registerIcon(DarkMain.getInstance().PREFIX + "machine_side");
        this.iconCoalGenerator = par1IconRegister.registerIcon(DarkMain.getInstance().PREFIX + "coalGenerator");
        this.iconBatteryBox = par1IconRegister.registerIcon(DarkMain.getInstance().PREFIX + "batteryBox");
        this.iconElectricFurnace = par1IconRegister.registerIcon(DarkMain.getInstance().PREFIX + "electricFurnace");
    }

    @Override
    public Icon getIcon(int side, int metadata)
    {
        if (side == 0 || side == 1)
        {
            return this.blockIcon;
        }

        if (metadata >= BasicMachineData.ELECTRIC_FURNACE.startMeta)
        {
            metadata -= BasicMachineData.ELECTRIC_FURNACE.startMeta;

            // If it is the front side
            if (side == metadata + 2)
            {
                return this.iconInput;
            }
            // If it is the back side
            else if (side == ForgeDirection.getOrientation(metadata + 2).getOpposite().ordinal())
            {
                return this.iconElectricFurnace;
            }
        }
        else if (metadata >= BasicMachineData.BATTERY_BOX.startMeta)
        {
            metadata -= BasicMachineData.BATTERY_BOX.startMeta;

            // If it is the front side
            if (side == metadata + 2)
            {
                return this.iconOutput;
            }
            // If it is the back side
            else if (side == ForgeDirection.getOrientation(metadata + 2).getOpposite().ordinal())
            {
                return this.iconInput;
            }

            return this.iconBatteryBox;
        }
        else
        {
            // If it is the front side
            if (side == metadata + 2)
            {
                return this.iconOutput;
            }
            // If it is the back side
            else if (side == ForgeDirection.getOrientation(metadata + 2).getOpposite().ordinal())
            {
                return this.iconCoalGenerator;
            }
        }

        return this.iconMachineSide;
    }

    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase entityLiving, ItemStack itemStack)
    {
        super.onBlockPlacedBy(world, x, y, z, entityLiving, itemStack);
        int metadata = world.getBlockMetadata(x, y, z);

        int angle = MathHelper.floor_double((entityLiving.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;
        int change = 0;

        switch (angle)
        {
            case 0:
                change = 1;
                break;
            case 1:
                change = 2;
                break;
            case 2:
                change = 0;
                break;
            case 3:
                change = 3;
                break;
        }

        if (metadata >= BasicMachineData.ELECTRIC_FURNACE.startMeta)
        {
            world.setBlockMetadataWithNotify(x, y, z, BasicMachineData.ELECTRIC_FURNACE.startMeta + change, 3);
        }
        else if (metadata >= BasicMachineData.BATTERY_BOX.startMeta)
        {
            switch (angle)
            {
                case 0:
                    change = 3;
                    break;
                case 1:
                    change = 1;
                    break;
                case 2:
                    change = 2;
                    break;
                case 3:
                    change = 0;
                    break;
            }

            world.setBlockMetadataWithNotify(x, y, z, BasicMachineData.BATTERY_BOX.startMeta + change, 3);
        }
        else
        {
            world.setBlockMetadataWithNotify(x, y, z, BasicMachineData.GENERATOR_COAL.startMeta + change, 3);
        }
    }

    @Override
    public void randomDisplayTick(World par1World, int x, int y, int z, Random rand)
    {
        TileEntity tile = par1World.getBlockTileEntity(x, y, z);

        if (tile instanceof TileEntityBasicGenerator)
        {
            TileEntityBasicGenerator tileEntity = (TileEntityBasicGenerator) tile;
            if (tileEntity.generateWatts > 0)
            {
                int face = par1World.getBlockMetadata(x, y, z) % 4;
                float xx = x + 0.5F;
                float yy = y + 0.0F + rand.nextFloat() * 6.0F / 16.0F;
                float zz = z + 0.5F;
                float posTooner = 0.52F;
                float randPosChange = rand.nextFloat() * 0.6F - 0.3F;

                if (face == 3)
                {
                    par1World.spawnParticle("smoke", xx - posTooner, yy, zz + randPosChange, 0.0D, 0.0D, 0.0D);
                    par1World.spawnParticle("flame", xx - posTooner, yy, zz + randPosChange, 0.0D, 0.0D, 0.0D);
                }
                else if (face == 2)
                {
                    par1World.spawnParticle("smoke", xx + posTooner, yy, zz + randPosChange, 0.0D, 0.0D, 0.0D);
                    par1World.spawnParticle("flame", xx + posTooner, yy, zz + randPosChange, 0.0D, 0.0D, 0.0D);
                }
                else if (face == 1)
                {
                    par1World.spawnParticle("smoke", xx + randPosChange, yy, zz - posTooner, 0.0D, 0.0D, 0.0D);
                    par1World.spawnParticle("flame", xx + randPosChange, yy, zz - posTooner, 0.0D, 0.0D, 0.0D);
                }
                else if (face == 0)
                {
                    par1World.spawnParticle("smoke", xx + randPosChange, yy, zz + posTooner, 0.0D, 0.0D, 0.0D);
                    par1World.spawnParticle("flame", xx + randPosChange, yy, zz + posTooner, 0.0D, 0.0D, 0.0D);
                }
            }
        }
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
        for (BasicMachineData data : BasicMachineData.values())
        {
            list.add(new Pair<String, Class<? extends TileEntity>>("DC" + data.unlocalizedName, data.clazz));
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void getClientTileEntityRenderers(List<Pair<Class<? extends TileEntity>, TileEntitySpecialRenderer>> list)
    {
        list.add(new Pair(TileEntityBasicGenerator.class, new RenderBasicMachine()));
        list.add(new Pair(TileEntityElectricFurnace.class, new RenderBasicMachine()));
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
            par5EntityPlayer.openGui(DarkMain.getInstance(), BasicMachineData.values()[par1World.getBlockMetadata(x, y, z) / 4].guiID, par1World, x, y, z);
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
    public int getRenderType()
    {
        return BlockRenderingHandler.BLOCK_RENDER_ID;
    }

    @Override
    public TileEntity createTileEntity(World world, int metadata)
    {
        try
        {
            return BasicMachineData.values()[metadata / 4].clazz.newInstance();
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

    public static enum BasicMachineData
    {
        GENERATOR_COAL("coalgen", 0, CommonProxy.GUI_COAL_GEN, TileEntityBasicGenerator.class),
        GENERATOR_FUEL("fuelgen", 4, CommonProxy.GUI_FUEL_GEN, TileEntityBasicGenerator.class),
        BATTERY_BOX("batterybox", 8, CommonProxy.GUI_BATTERY_BOX, TileEntityBatteryBox.class),
        ELECTRIC_FURNACE("electricfurnace", 12, CommonProxy.GUI_FURNACE_ELEC, TileEntityElectricFurnace.class);

        public String unlocalizedName;
        public int startMeta, guiID;
        public boolean enabled = true;
        public boolean allowCrafting = true;
        public Class<? extends TileEntity> clazz;

        private BasicMachineData(String name, int meta, int guiID, Class<? extends TileEntity> clazz)
        {
            this.unlocalizedName = name;
            this.startMeta = meta;
            this.guiID = guiID;
            this.clazz = clazz;
        }
    }
}