package dark.core.network.fluid;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import dark.core.api.ColorCode;
import dark.core.api.INetworkPart;
import dark.core.tile.network.NetworkTileEntities;
import dark.fluid.api.INetworkFluidPart;

/** Side note: the network should act like this when done {@link http
 * ://www.e4training.com/hydraulic_calculators/B1.htm} as well as stay compatible with the forge
 * Liquids
 * 
 * @author Rseifert */
public class NetworkFluidContainers extends NetworkFluidTiles
{

	public NetworkFluidContainers(ColorCode color, INetworkPart... parts)
	{
		super(color, parts);
	}

	@Override
	public NetworkTileEntities newInstance()
	{
		return new NetworkFluidContainers(this.color);
	}

	@Override
	// TODO change this to place liquids at the bottom first
	public void balanceColletiveTank(boolean sumParts)
	{
		int volume = 0;
		Fluid fluid = null;

		if (sumParts)
		{
			for (INetworkPart par : this.networkMember)
			{
				if (par instanceof INetworkFluidPart)
				{
					INetworkFluidPart part = ((INetworkFluidPart) par);
					if (part.getTank() != null && part.getTank().getFluid() != null)
					{
						if (fluid == null)
						{
							fluid = part.getTank().getFluid().getFluid();
						}
						volume += part.getTank().getFluid().amount;
					}
				}
			}
			this.combinedStorage().setFluid(new FluidStack(fluid, volume));
			this.loadedLiquids = true;
		}
		if (this.combinedStorage().getFluid() != null && this.getNetworkMemebers().size() > 0)
		{
			this.cleanUpMembers();

			int lowestY = 255;
			int highestY = 0;
			for (INetworkPart part : this.getNetworkMemebers())
			{
				if (part instanceof TileEntity && ((TileEntity) part).yCoord < lowestY)
				{
					lowestY = ((TileEntity) part).yCoord;
				}
				if (part instanceof TileEntity && ((TileEntity) part).yCoord > highestY)
				{
					highestY = ((TileEntity) part).yCoord;
				}
			}
			fluid = this.combinedStorage().getFluid().getFluid();
			volume = this.combinedStorage().getFluid().amount;

			for (int y = lowestY; y <= highestY; y++)
			{
				List<INetworkFluidPart> parts = new ArrayList<INetworkFluidPart>();
				for (INetworkPart part : this.getNetworkMemebers())
				{
					if (part instanceof INetworkFluidPart && ((TileEntity) part).yCoord == y)
					{
						parts.add((INetworkFluidPart) part);
					}
				}
				int fillvolume = volume / parts.size();

				for (INetworkFluidPart part : parts)
				{
					part.setTankContent(null);
					int fill = Math.min(fillvolume, part.getTank().getCapacity());
					part.setTankContent(new FluidStack(fluid, fill));
					volume -= fill;
				}
				if (volume <= 0)
				{
					break;
				}
			}
		}

	}

	@Override
	public int storeFluidInSystem(FluidStack stack, boolean doFill)
	{
		int vol = this.combinedStorage().getFluid() != null ? this.combinedStorage().getFluid().amount : 0;
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
	public FluidStack drainFluidFromSystem(int maxDrain, boolean doDrain)
	{
		FluidStack vol = this.combinedStorage().getFluid();
		FluidStack stack = super.drainFluidFromSystem(maxDrain, doDrain);
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
	public void mergeDo(NetworkTileEntities network)
	{
		NetworkFluidContainers newNetwork = new NetworkFluidContainers(this.color);
		newNetwork.getNetworkMemebers().addAll(this.getNetworkMemebers());
		newNetwork.getNetworkMemebers().addAll(network.getNetworkMemebers());

		newNetwork.cleanUpMembers();
		newNetwork.balanceColletiveTank(true);
	}

	
}
