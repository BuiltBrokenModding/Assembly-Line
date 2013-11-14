package dark.assembly.armbot;

import java.util.List;
import java.util.Random;

import com.builtbroken.common.Pair;

import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import universalelectricity.core.UniversalElectricity;
import universalelectricity.core.vector.Vector3;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import dark.assembly.client.render.BlockRenderingHandler;
import dark.assembly.client.render.RenderArmbot;
import dark.assembly.client.render.RenderCrate;
import dark.assembly.machine.BlockAssembly;
import dark.assembly.machine.TileEntityCrate;
import dark.core.interfaces.IMultiBlock;
import dark.core.registration.ModObjectRegistry.BlockBuildData;

public class BlockArmbot extends BlockAssembly
{
    public BlockArmbot()
    {
        super(new BlockBuildData(BlockArmbot.class, "armbot", UniversalElectricity.machine));
    }

    @Override
    public void onBlockAdded(World world, int x, int y, int z)
    {
        TileEntity tileEntity = world.getBlockTileEntity(x, y, z);

        if (tileEntity != null && tileEntity instanceof IMultiBlock)
        {
            ((IMultiBlock) tileEntity).onCreate(new Vector3(x, y, z));
        }
    }

    @Override
    public boolean onMachineActivated(World world, int x, int y, int z, EntityPlayer player, int par6, float par7, float par8, float par9)
    {
        TileEntity tileEntity = world.getBlockTileEntity(x, y, z);

        if (tileEntity != null && tileEntity instanceof IMultiBlock)
        {
            return ((IMultiBlock) tileEntity).onActivated(player);
        }

        return false;
    }

    @Override
    public void breakBlock(World world, int x, int y, int z, int par5, int par6)
    {
        TileEntity tileEntity = world.getBlockTileEntity(x, y, z);

        if (tileEntity != null && tileEntity instanceof IMultiBlock)
        {
            ((IMultiBlock) tileEntity).onDestroy(tileEntity);
        }

        this.dropBlockAsItem_do(world, x, y, z, new ItemStack(this));

        super.breakBlock(world, x, y, z, par5, par6);
    }

    @Override
    public int quantityDropped(Random par1Random)
    {
        return 0;
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public void getClientTileEntityRenderers(List<Pair<Class<? extends TileEntity>, TileEntitySpecialRenderer>> list)
    {
        list.add(new Pair<Class<? extends TileEntity>, TileEntitySpecialRenderer>(TileEntityArmbot.class, new RenderArmbot()));
    }

    @Override
    public TileEntity createNewTileEntity(World var1)
    {
        return new TileEntityArmbot();
    }

    @SideOnly(Side.CLIENT)
    @Override
    public int getRenderType()
    {
        return BlockRenderingHandler.BLOCK_RENDER_ID;
    }

    @Override
    public boolean isOpaqueCube()
    {
        return false;
    }

    @Override
    public boolean canProvidePower()
    {
        return true;
    }

}
