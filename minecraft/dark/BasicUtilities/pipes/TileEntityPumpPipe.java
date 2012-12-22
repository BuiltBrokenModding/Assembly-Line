package dark.BasicUtilities.pipes;

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.liquids.ILiquidTank;
import net.minecraftforge.liquids.LiquidStack;
import dark.BasicUtilities.api.ITankOutputer;
import dark.BasicUtilities.api.Liquid;

public class TileEntityPumpPipe extends TileEntity implements ITankOutputer
{
    private ForgeDirection input;
    private Liquid outputType = Liquid.DEFUALT;
    
    @Override
    public int fill(ForgeDirection from, LiquidStack resource, boolean doFill)
    {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public int fill(int tankIndex, LiquidStack resource, boolean doFill)
    {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public LiquidStack drain(ForgeDirection from, int maxDrain, boolean doDrain)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public LiquidStack drain(int tankIndex, int maxDrain, boolean doDrain)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ILiquidTank[] getTanks(ForgeDirection direction)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ILiquidTank getTank(ForgeDirection direction, LiquidStack type)
    {
        // TODO Auto-generated method stub
        return null;
    }

    public int presureOutput(Liquid type, ForgeDirection dir)
    {
        if(dir != this.input.getOpposite() && type == this.outputType)
        {
            return type.defaultPresure;
        }
        return 0;
    }

    @Override
    public boolean canPressureToo(Liquid type, ForgeDirection dir)
    {
        if(type == this.outputType && dir != this.input.getOpposite()) return true;
        return false;
    }

}
