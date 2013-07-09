package dark.core.network.fluid;


import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.liquids.ILiquidTank;
import net.minecraftforge.liquids.ITankContainer;
import net.minecraftforge.liquids.LiquidStack;
import dark.core.api.ColorCode;
import dark.core.api.INetworkPart;
import dark.core.hydraulic.helpers.FluidRestrictionHandler;
import dark.core.tile.network.NetworkTileEntities;
import dark.fluid.api.INetworkPipe;
import dark.helpers.ConnectionHelper;

/**
 * Side note: the network should act like this when done {@link http
 * ://www.e4training.com/hydraulic_calculators/B1.htm} as well as stay compatible with the forge
 * Liquids
 * 
 * @author Rseifert
 * 
 */
public class NetworkPipes extends NetworkFluidTiles
{

	/* MACHINES THAT USE THE PRESSURE SYSTEM TO DO WORK ** */
	private final HashMap<TileEntity, FluidPressurePack> pressureProducers = new HashMap<TileEntity, FluidPressurePack>();
	private final HashMap<TileEntity, FluidPressurePack> pressureLoads = new HashMap<TileEntity, FluidPressurePack>();

	/* PRESSURE OF THE NETWORK AS A TOTAL. ZERO AS IN NO PRESSURE */
	public double pressureProduced = 0, pressureLoad = 0;

	/* IS IT PROCESSING AN ADD LIQUID EVENT */
	private boolean processingRequest = false;

	public NetworkPipes(ColorCode color, INetworkPart... parts)
	{
		super(color, parts);
	}
	
	@Override
	public NetworkTileEntities newInstance()
	{
		return new NetworkPipes(this.color);
	}

	public boolean isPartOfNetwork(TileEntity ent)
	{
		return super.isPartOfNetwork(ent) || this.pressureLoads.containsKey(ent) || this.pressureProducers.containsKey(ent);
	}

	/**
	 * sets this tileEntity to produce a pressure and flow rate in the network
	 */
	public void startProducingPressure(TileEntity tileEntity, FluidPressurePack fluidPack)
	{
		if (tileEntity != null && fluidPack.liquidStack != null)
		{
			if ((this.combinedStorage().getLiquid() == null || fluidPack.liquidStack.isLiquidEqual(this.combinedStorage().getLiquid())) && fluidPack.liquidStack.amount > 0)
			{
				this.pressureProducers.put(tileEntity, fluidPack);
			}
		}
	}

	/**
	 * sets this tileEntity to produce a pressure and flow rate in the network
	 */
	public void startProducingPressure(TileEntity tileEntity, LiquidStack stack, double pressure)
	{
		this.startProducingPressure(tileEntity, new FluidPressurePack(stack, pressure));
	}

	/**
	 * is this tile entity producing a pressure
	 */
	public boolean isProducingPressure(TileEntity tileEntity)
	{
		return this.pressureProducers.containsKey(tileEntity);
	}

	/**
	 * Sets this tile entity to act as a load on the system
	 */
	public void addLoad(TileEntity tileEntity, FluidPressurePack fluidPack)
	{
		if (tileEntity != null && fluidPack.liquidStack != null && fluidPack.liquidStack.amount > 0)
		{
			this.pressureLoads.put(tileEntity, fluidPack);
		}
	}

	/**
	 * Sets this tile entity to act as a load on the system
	 */
	public void addLoad(TileEntity tileEntity, LiquidStack stack, double pressure)
	{
		this.addLoad(tileEntity, new FluidPressurePack(stack, pressure));
	}

	/**
	 * is this tileEntity a load in the network
	 */
	public boolean isLoad(TileEntity tileEntity)
	{
		return this.pressureLoads.containsKey(tileEntity);
	}

	/**
	 * @param ignoreTiles The TileEntities to ignore during this calculation. Null will make it not
	 * ignore any.
	 * @return The electricity produced in this electricity network
	 */
	public double getPressureProduced(TileEntity... ignoreTiles)
	{
		// TODO pressure is not added as a sum but rather as a collective sum of the largest
		// pressures. IF the pressure is to small it will be ignored and stop producing pressure.
		int totalPressure = 0;

		Iterator it = this.pressureProducers.entrySet().iterator();

		loop:
		while (it.hasNext())
		{
			Map.Entry pairs = (Map.Entry) it.next();

			if (pairs != null)
			{
				TileEntity tileEntity = (TileEntity) pairs.getKey();

				if (tileEntity == null)
				{
					it.remove();
					continue;
				}

				if (tileEntity.isInvalid())
				{
					it.remove();
					continue;
				}

				if (tileEntity.worldObj.getBlockTileEntity(tileEntity.xCoord, tileEntity.yCoord, tileEntity.zCoord) != tileEntity)
				{
					it.remove();
					continue;
				}

				if (ignoreTiles != null)
				{
					for (TileEntity ignoreTile : ignoreTiles)
					{
						if (tileEntity == ignoreTile)
						{
							continue loop;
						}
					}
				}

				FluidPressurePack pack = (FluidPressurePack) pairs.getValue();

				if (pairs.getKey() != null && pairs.getValue() != null && pack != null)
				{
					totalPressure += pack.pressure;
				}
			}
		}

		return totalPressure;
	}

	@Override
	public void removeTile(TileEntity ent)
	{
		super.removeTile(ent);
		this.pressureLoads.remove(ent);
		this.pressureProducers.remove(ent);
	}

	/**
	 * Adds FLuid to this network from one of the connected Pipes
	 * 
	 * @param source - Were this liquid came from
	 * @param stack - LiquidStack to be sent
	 * @param doFill - actually fill the tank or just check numbers
	 * @return the amount of liquid consumed from the init stack
	 */
	public int addFluidToNetwork(TileEntity source, LiquidStack stack, boolean doFill)
	{
		return this.addFluidToNetwork(source, stack, doFill, false);
	}

	/**
	 * Adds FLuid to this network from one of the connected Pipes
	 * 
	 * @param source - Were this liquid came from
	 * @param stack - LiquidStack to be sent
	 * @param doFill - actually fill the tank or just check numbers
	 * @param allowStore - allows the network to store this liquid in the pipes
	 * @return the amount of liquid consumed from the init stack
	 */
	public int addFluidToNetwork(TileEntity source, LiquidStack sta, boolean doFill, boolean allowStore)
	{
		int used = 0;
		LiquidStack prevCombined = this.combinedStorage().getLiquid();
		LiquidStack stack = sta.copy();

		if (!this.processingRequest && stack != null && FluidRestrictionHandler.isValidLiquid(color,stack))
		{
			this.processingRequest = true;

			if (this.combinedStorage().getLiquid() != null && !stack.isLiquidEqual(this.combinedStorage().getLiquid()))
			{
				this.causingMixing(null,this.combinedStorage().getLiquid(), stack);
			}
			if (stack.amount > this.getMaxFlow(stack))
			{
				stack = new LiquidStack(stack.itemID, this.getMaxFlow(stack), stack.itemMeta);
			}

			/* Main fill target to try to fill with the stack */
			ITankContainer primaryFill = null;
			int volume = Integer.MAX_VALUE;
			ForgeDirection fillDir = ForgeDirection.UNKNOWN;

			/* Secondary fill target if the main target is not found */
			ITankContainer secondayFill = null;
			int mostFill = 0;
			ForgeDirection otherFillDir = ForgeDirection.UNKNOWN;

			boolean found = false;

			/* FIND THE FILL TARGET FROM THE LIST OF FLUID RECIEVERS */
			for (ITankContainer tankContainer : connectedTanks)
			{
				if (tankContainer instanceof TileEntity && tankContainer != source && !(tankContainer instanceof INetworkPipe))
				{
					TileEntity[] connectedTiles = ConnectionHelper.getSurroundingTileEntities((TileEntity) tankContainer);

					for (int i = 0; i < 6; i++)
					{
						if (connectedTiles[i] instanceof INetworkPipe && ((INetworkPipe) connectedTiles[i]).getTileNetwork() == this)
						{
							ForgeDirection dir = ForgeDirection.getOrientation(i).getOpposite();
							ILiquidTank targetTank = tankContainer.getTank(dir, stack);
							int fill = tankContainer.fill(dir, stack, false);

							/* USE GET TANK FROM SIDE METHOD FIRST */
							if (targetTank != null)
							{
								LiquidStack stackStored = targetTank.getLiquid();
								if (stackStored == null)
								{
									primaryFill = tankContainer;
									found = true;
									fillDir = dir;
									break;
								}
								else if (stackStored.amount < targetTank.getCapacity() && stackStored.amount < volume)
								{
									primaryFill = tankContainer;
									volume = stackStored.amount;
								}
							}/* USE FILL METHOD IF GET TANK == NULL */
							else if (fill > 0 && fill > mostFill)
							{
								secondayFill = tankContainer;
								mostFill = fill;
								otherFillDir = dir;
							}
						}
					}
				}
				if (found)
				{
					break;
				}
			}// End of tank finder

			boolean filledMain = false;
			if (primaryFill != null)
			{
				used = primaryFill.fill(fillDir, stack, doFill);
				// System.out.println("Primary Target " + used + doFill);
			}
			else if (secondayFill != null)
			{
				used = secondayFill.fill(fillDir, stack, doFill);
				// System.out.println("Seconday Target " + used + doFill);
			}
			else if (allowStore)
			{
				used = this.storeFluidInSystem(stack, doFill);
				// System.out.println("Network Target filled for " + used + doFill);
				filledMain = true;
			}

			/* IF THE COMBINED STORAGE OF THE PIPES HAS LIQUID MOVE IT FIRST */
			if (!filledMain && used > 0 && this.combinedStorage().getLiquid() != null && this.combinedStorage().getLiquid().amount > 0)
			{

				LiquidStack drainStack = new LiquidStack(0, 0, 0);
				if (this.combinedStorage().getLiquid().amount >= used)
				{
					drainStack = this.combinedStorage().drain(used, doFill);
					used = 0;
				}
				else
				{
					int pUsed = used;
					used = Math.min(used, Math.max(used - this.combinedStorage().getLiquid().amount, 0));
					drainStack = this.combinedStorage().drain(pUsed - used, doFill);
				}
				// System.out.println("Pulling " + (drainStack != null ? drainStack.amount : 0) +
				// " from combined leaving " + (this.combinedStorage.getLiquid() != null ?
				// this.combinedStorage.getLiquid().amount : 0));

			}
			if (prevCombined != null && this.combinedStorage().getLiquid() != null && prevCombined.amount != this.combinedStorage().getLiquid().amount)
			{
				this.balanceColletiveTank(false);
			}
		}
		this.processingRequest = false;
		return used;
	}

	/**
	 * Gets the flow rate of the system using the lowest flow rate
	 */
	public int getMaxFlow(LiquidStack stack)
	{
		int flow = 1000;
		for (INetworkPart conductor : this.networkMember)
		{
			if (conductor instanceof INetworkPipe)
			{
				int cFlow = ((INetworkPipe) conductor).getMaxFlowRate(stack, ForgeDirection.UNKNOWN);
				if (cFlow < flow)
				{
					flow = cFlow;
				}
			}
		}
		return flow;
	}

	/**
	 * Updates after the pressure has changed a good bit
	 */
	public void onPresureChange()
	{
		this.cleanUpMembers();

		for (int i = 0; i < networkMember.size(); i++)
		{
			if (networkMember.get(i) instanceof INetworkPipe)
			{
				INetworkPipe part = (INetworkPipe) networkMember.get(i);
				if (part.getMaxPressure(ForgeDirection.UNKNOWN) < this.pressureProduced && part.onOverPressure(true))
				{
					this.networkMember.remove(part);
					this.cleanUpMembers();
				}
			}

		}
	}
	
	@Override
	public void mergeDo(NetworkTileEntities network)
	{
		NetworkPipes newNetwork = new NetworkPipes(this.color);
		newNetwork.getNetworkMemebers().addAll(this.getNetworkMemebers());
		newNetwork.getNetworkMemebers().addAll(network.getNetworkMemebers());

		newNetwork.cleanUpMembers();
		newNetwork.balanceColletiveTank(true);
	}

}
