package hydraulic.network;

import hydraulic.api.ColorCode;
import hydraulic.api.INetworkPart;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.liquids.LiquidStack;

/**
 * Side note: the network should act like this when done {@link http
 * ://www.e4training.com/hydraulic_calculators/B1.htm} as well as stay compatible with the forge
 * Liquids
 * 
 * @author Rseifert
 * 
 */
public class ContainerNetwork extends FluidNetwork
{

	public ContainerNetwork(ColorCode color, INetworkPart... parts)
	{
		super(color, parts);
	}

	@Override
	// TODO change this to place liquids at the bottom first
	public void balanceColletiveTank(boolean sumParts)
	{
		super.balanceColletiveTank(sumParts);
	}

	@Override
	public int storeFluidInSystem(LiquidStack stack, boolean doFill)
	{
		int vol = this.combinedStorage().getLiquid() != null ? this.combinedStorage().getLiquid().amount : 0;
		int filled = super.storeFluidInSystem(stack, doFill);
		if (vol != filled)
		{
			for (INetworkPart part : this.getNetworkMemebers())
			{
				if (part instanceof TileEntity)
				{
					TileEntity ent = ((TileEntity) part);
					ent.worldObj.markBlockForUpdate(ent.xCoord, ent.yCoord, ent.zCoord);
				}
			}
		}
		return filled;
	}

	@Override
	public LiquidStack drainFluidFromSystem(int maxDrain, boolean doDrain)
	{
		LiquidStack vol = this.combinedStorage().getLiquid();
		LiquidStack stack = super.drainFluidFromSystem(maxDrain, doDrain);
		boolean flag = false;
		if (vol != null)
		{
			if (stack == null)
			{
				flag = true;
			}
			else if (stack.amount != vol.amount)
			{
				flag = true;
			}
		}
		if (flag)
		{
			for (INetworkPart part : this.getNetworkMemebers())
			{
				if (part instanceof TileEntity)
				{
					TileEntity ent = ((TileEntity) part);
					ent.worldObj.markBlockForUpdate(ent.xCoord, ent.yCoord, ent.zCoord);
				}
			}
		}
		return stack;
	}

	@Override
	public void postMergeProcessing(TileNetwork network)
	{
		ContainerNetwork newNetwork = new ContainerNetwork(this.color);
		newNetwork.getNetworkMemebers().addAll(this.getNetworkMemebers());
		newNetwork.getNetworkMemebers().addAll(network.getNetworkMemebers());

		newNetwork.cleanUpConductors();
		newNetwork.balanceColletiveTank(true);
	}
}
