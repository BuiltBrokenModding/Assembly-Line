package dark.fluid.common.pipes;

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidStack;
import universalelectricity.core.vector.Vector3;
import universalelectricity.core.vector.VectorHelper;
import dark.api.ColorCode;
import dark.core.prefab.tilenetwork.fluid.NetworkPipes;

public class TileEntityGenericPipe extends TileEntityPipe
{
    @Override
    public int fill(ForgeDirection from, FluidStack resource, boolean doFill)
    {
        if (resource == null)
        {
            return 0;
        }
        TileEntity tile = VectorHelper.getTileEntityFromSide(this.worldObj, new Vector3(this), from);
        return ((NetworkPipes) this.getTileNetwork()).addFluidToNetwork(tile, resource, doFill);
    }

    @Override
    public boolean canTileConnect(Connection type, ForgeDirection dir)
    {
        TileEntity entity = new Vector3(this).modifyPositionFromSide(dir).getTileEntity(this.worldObj);
        Vector3 vec = new Vector3(entity);
        int meta = vec.getBlockMetadata(this.worldObj);
        int blockID = vec.getBlockID(this.worldObj);

        if (entity instanceof TileEntityPipe && blockID == this.getBlockType().blockID)
        {
            return meta == this.getBlockMetadata();
        }

        return super.canTileConnect(type, dir);
    }

    @Override
    public int getMaxFlowRate(Fluid stack, ForgeDirection side)
    {
        return FluidContainerRegistry.BUCKET_VOLUME;
    }

    @Override
    public ColorCode getColor()
    {
        return ColorCode.UNKOWN;
    }
}
