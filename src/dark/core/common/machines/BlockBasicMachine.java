package dark.core.common.machines;

import java.util.List;
import java.util.Random;
import java.util.Set;

import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import net.minecraftforge.common.Configuration;
import universalelectricity.core.UniversalElectricity;

import com.builtbroken.common.Pair;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import dark.core.client.renders.BlockRenderingHandler;
import dark.core.client.renders.RenderBasicMachine;
import dark.core.common.DMCreativeTab;
import dark.core.helpers.MathHelper;
import dark.core.prefab.machine.BlockMachine;
import dark.core.registration.ModObjectRegistry.BlockBuildData;

public class BlockBasicMachine extends BlockMachine
{

    public BlockBasicMachine()
    {
        super(new BlockBuildData(BlockBasicMachine.class, "BasicMachine", UniversalElectricity.machine).setCreativeTab(DMCreativeTab.tabIndustrial));
        this.setStepSound(soundMetalFootstep);
    }

    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase entityLiving, ItemStack itemStack)
    {
        super.onBlockPlacedBy(world, x, y, z, entityLiving, itemStack);
        int metadata = world.getBlockMetadata(x, y, z);
        int angle = MathHelper.floor_double((entityLiving.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;
        world.setBlockMetadataWithNotify(x, y, z, (metadata / 4) + angle, 3);
    }

    @Override
    public void randomDisplayTick(World par1World, int x, int y, int z, Random rand)
    {
        TileEntity tile = par1World.getBlockTileEntity(x, y, z);

        if (tile instanceof TileEntitySteamGen)
        {
            TileEntitySteamGen tileEntity = (TileEntitySteamGen) tile;
            if (tileEntity.isFunctioning())
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
        list.add(new Pair<String, Class<? extends TileEntity>>("DCTileSteamFan", TileEntitySteamFan.class));
        list.add(new Pair<String, Class<? extends TileEntity>>("DCTileSteamPiston", TileEntitySteamPiston.class));
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void getClientTileEntityRenderers(List<Pair<Class<? extends TileEntity>, TileEntitySpecialRenderer>> list)
    {
        list.add(new Pair(TileEntitySteamPiston.class, new RenderBasicMachine()));
        list.add(new Pair(TileEntitySteamFan.class, new RenderBasicMachine()));
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
        switch (metadata / 4)
        {
            case 0:
                return new TileEntitySteamFan();
            case 1:
                return new TileEntitySteamPiston();
            case 2:
                return new TileEntitySteamFan();
            case 3:
                return new TileEntitySteamFan();

        }
        return super.createTileEntity(world, metadata);

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
        return new ItemStack(this, 1, world.getBlockMetadata(x, y, z) / 4);
    }
}