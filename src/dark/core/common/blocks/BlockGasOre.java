package dark.core.common.blocks;

import java.util.List;
import java.util.Set;

import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatMessageComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import universalelectricity.prefab.block.BlockTile;

import com.builtbroken.common.Pair;

import dark.api.IGasBlock;
import dark.core.common.CoreRecipeLoader;
import dark.core.common.DMCreativeTab;
import dark.core.common.DarkMain;
import dark.core.interfaces.IExtraInfo.IExtraBlockInfo;
import dark.core.prefab.fluids.EnumGas;

/** Gas that is designed to generate underground in the same way as an ore
 * 
 * TODO code actual gas behavior such as expanding to fill an area but at the same time losing
 * volume
 * 
 * @author DarkGuardsman */
public class BlockGasOre extends BlockTile implements IGasBlock, IExtraBlockInfo
{

    public BlockGasOre()
    {
        super(DarkMain.CONFIGURATION.getBlock("GasBlock", DarkMain.getNextID()).getInt(), Material.air);
        this.setUnlocalizedName("DMBlockGas");
        this.setCreativeTab(DMCreativeTab.tabIndustrial);
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer entityPlayer, int side, float hitX, float hitY, float hitZ)
    {
        TileEntity entity = world.getBlockTileEntity(x, y, z);
        if (entity instanceof TileEntityGasBlock)
        {
            FluidStack fluid = ((TileEntityGasBlock) entity).getFluidStack();
            entityPlayer.sendChatToPlayer(ChatMessageComponent.createFromText("Gas Stack: " + (fluid != null ? fluid.getFluid().getName() : "null")));
        }
        return true;
    }

    public static void placeAndCreate(World world, int x, int y, int z, FluidStack stack)
    {
        if (stack != null)
        {
            world.setBlock(x, y, z, CoreRecipeLoader.blockGas.blockID, 0, 2);
            TileEntity entity = world.getBlockTileEntity(x, y, z);
            if (entity instanceof TileEntityGasBlock)
            {
                ((TileEntityGasBlock) entity).setStack(stack);
            }
        }
    }

    /* IFluidBlock */
    @Override
    public FluidStack drain(World world, int x, int y, int z, boolean doDrain)
    {
        TileEntity entity = world.getBlockTileEntity(x, y, z);
        if (entity instanceof TileEntityGasBlock)
        {
            FluidStack fluid = ((TileEntityGasBlock) entity).getFluidStack();
            if (doDrain || fluid == null)
            {
                world.setBlockToAir(x, y, z);
            }
            return fluid;
        }
        return null;
    }

    @Override
    public boolean canDrain(World world, int x, int y, int z)
    {
        return true;
    }

    @Override
    public boolean canFrackerHarvest(TileEntity entity)
    {
        return true;
    }

    @Override
    public Fluid getFluid()
    {
        return EnumGas.NATURAL_GAS.getGas();
    }

    @Override
    public TileEntity createNewTileEntity(World var1)
    {
        return new TileEntityGasBlock();
    }

    @Override
    public boolean hasExtraConfigs()
    {
        return false;
    }

    @Override
    public void loadExtraConfigs(Configuration config)
    {
        //TODO add configs for spread rate,and update rate

    }

    @Override
    public void loadOreNames()
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void getTileEntities(int blockID, Set<Pair<String, Class<? extends TileEntity>>> list)
    {
        list.add(new Pair<String, Class<? extends TileEntity>>("DMTileGas", TileEntityGasBlock.class));

    }

    @Override
    public void getClientTileEntityRenderers(List<Pair<Class<? extends TileEntity>, TileEntitySpecialRenderer>> list)
    {
        // TODO Auto-generated method stub

    }

}
