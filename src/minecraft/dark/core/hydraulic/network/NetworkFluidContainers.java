package dark.core.hydraulic.network;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.liquids.LiquidStack;
import dark.core.api.ColorCode;
import dark.core.api.network.INetworkPart;
import dark.core.api.network.fluid.INetworkFluidPart;
import dark.core.hydraulic.network.fluid.NetworkFluidTiles;

/**
 * Side note: the network should act like this when done {@link http
 * ://www.e4training.com/hydraulic_calculators/B1.htm} as well as stay compatible with the forge
 * Liquids
 * 
 * @author Rseifert
 * 
 */
public class NetworkFluidContainers extends NetworkFluidTiles
{

	public NetworkFluidContainers(ColorCode color, INetworkPart... parts)
	{
		super(color, parts);
	}

	@Override
	// TODO change this to place liquids at the bottom first
	public void balanceColletiveTank(boolean sumParts)
	{
		int volume = 0, itemID = 0, itemMeta = 0;

		if (sumParts)
		{
			for (INetworkPart par : this.networkMember)
			{
				if (par instanceof INetworkFluidPart)
				{
					INetworkFluidPart part = ((INetworkFluidPart) par);
					if (part.getTank() != null && part.getTank().getLiquid() != null)
					{
						if (itemID == 0)
						{
							itemID = part.getTank().getLiquid().itemID;
							itemMeta = part.getTank().getLiquid().itemMeta;
						}
						volume += part.getTank().getLiquid().amount;
					}
				}
			}
			this.combinedStorage().setLiquid(new LiquidStack(itemID, volume, itemMeta));
			this.loadedLiquids = true;
		}
		if (this.combinedStorage().getLiquid() != null && this.getNetworkMemebers().size() > 0)
		{
			this.cleanUpConductors();

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
			itemID = this.combinedStorage().getLiquid().itemID;
			itemMeta = this.combinedStorage().getLiquid().itemMeta;
			volume = this.combinedStorage().getLiquid().amount;

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
					part.setTankContent(new LiquidStack(itemID, fill, itemMeta));
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
	public void postMergeProcessing(NetworkTileEntities network)
	{
		NetworkFluidContainers newNetwork = new NetworkFluidContainers(this.color);
		newNetwork.getNetworkMemebers().addAll(this.getNetworkMemebers());
		newNetwork.getNetworkMemebers().addAll(network.getNetworkMemebers());

		newNetwork.cleanUpConductors();
		newNetwork.balanceColletiveTank(true);
	}
}
