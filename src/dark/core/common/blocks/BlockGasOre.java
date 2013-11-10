package dark.core.common.blocks;

import universalelectricity.prefab.block.BlockTile;
import dark.api.IGasBlock;
import dark.core.common.DarkMain;
import dark.core.prefab.fluids.Gas;
import dark.core.prefab.machine.TileEntityNBTContainer;
import net.minecraft.block.material.Material;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.fluids.BlockFluidFinite;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidStack;

/** Gas that is designed to generate underground in the same way as an ore
 * 
 * TODO code actual gas behavior such as expanding to fill an area but at the same time losing
 * volume
 * 
 * @author DarkGuardsman */
public class BlockGasOre extends BlockTile implements IGasBlock
{

    public BlockGasOre()
    {
        super(DarkMain.CONFIGURATION.getBlock("GasBlock", DarkMain.getNextID()).getInt(), Material.air);
        this.setUnlocalizedName("DMBlockGas");
    }

    public void placeAndCreate(World world, int x, int y, int z, FluidStack stack)
    {
        world.setBlock(x, y, z, this.blockID, 0, 2);
        TileEntity entity = world.getBlockTileEntity(x, y, z);
        if (entity instanceof TileEntityNBTContainer)
        {
            ((TileEntityNBTContainer) entity).getSaveData().setCompoundTag("Fluid", stack.writeToNBT(new NBTTagCompound()));
        }
        world.markBlockForUpdate(x, y, z);
    }

    /* IFluidBlock */
    @Override
    public FluidStack drain(World world, int x, int y, int z, boolean doDrain)
    {
        TileEntity entity = world.getBlockTileEntity(x, y, z);
        if (entity instanceof TileEntityNBTContainer)
        {
            FluidStack fluid = FluidStack.loadFluidStackFromNBT(((TileEntityNBTContainer) entity).getSaveData().getCompoundTag("Fluid"));
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
        return null;
    }

    @Override
    public TileEntity createNewTileEntity(World var1)
    {
        return new TileEntityNBTContainer();
    }

}
