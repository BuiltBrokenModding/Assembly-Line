package dark.fluid.common.pipes.tele;

import hydraulic.api.ColorCode;
import hydraulic.fluidnetwork.HydraulicNetwork;
import hydraulic.prefab.tile.TileEntityFluidDevice;

import java.util.HashMap;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.liquids.ILiquidTank;
import net.minecraftforge.liquids.LiquidContainerRegistry;
import net.minecraftforge.liquids.LiquidStack;

public class TileEntityEndPipe extends TileEntityFluidDevice implements INetworkConnector
{
	public static HashMap<TileEntityEndPipe, Integer> linkMap = new HashMap<TileEntityEndPipe, Integer>();
	
	private HydraulicNetwork network; 
	@Override
	public String getMeterReading(EntityPlayer user, ForgeDirection side)
	{
		return null;
	}

	@Override
	public boolean canPipeConnect(TileEntity entity, ForgeDirection dir)
	{
		return false;
	}

	@Override
	public double getMaxPressure(ForgeDirection side)
	{
		return 0;
	}

	@Override
	public int getMaxFlowRate(LiquidStack stack, ForgeDirection side)
	{
		return LiquidContainerRegistry.BUCKET_VOLUME * 10;
	}

	@Override
	public HydraulicNetwork getNetwork()
	{
		if(network == null)
		{
			network = new HydraulicNetwork(this.getColor(), this);
		}
		return network;
	}

	@Override
	public void setNetwork(HydraulicNetwork network)
	{
		this.network = network;
		
	}

	@Override
	public boolean onOverPressure(Boolean damageAllowed)
	{
		return false;
	}

	@Override
	public int getTankSize()
	{
		return 0;
	}

	@Override
	public ILiquidTank getTank()
	{
		return null;
	}

	@Override
	public void setTankContent(LiquidStack stack)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public ColorCode getColor()
	{
		return ColorCode.NONE;
	}

	@Override
	public void setColor(Object obj)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public TileEntity[] getAdjacentConnections()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void updateAdjacentConnections()
	{
		// TODO Auto-generated method stub
		
	}

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

	@Override
	public int getFrequency()
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setFrequency(int id)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getOwner()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setOwner(String username)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<INetworkConnector> getConnectedParts()
	{
		// TODO Auto-generated method stub
		return null;
	}

}
