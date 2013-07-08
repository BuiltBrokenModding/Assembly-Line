package dark.library.machine;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.oredict.OreDictionary;
import universalelectricity.core.UniversalElectricity;
import universalelectricity.core.block.IConnector;
import universalelectricity.core.block.IVoltage;
import universalelectricity.core.electricity.ElectricityNetworkHelper;
import universalelectricity.core.electricity.ElectricityPack;
import universalelectricity.core.electricity.IElectricityNetwork;
import universalelectricity.core.vector.Vector3;
import universalelectricity.prefab.tile.TileEntityElectrical;
import buildcraft.api.power.IPowerProvider;
import buildcraft.api.power.IPowerReceptor;
import buildcraft.api.power.PowerFramework;
import buildcraft.api.power.PowerProvider;
import dark.core.PowerSystems;
import dark.core.api.INetworkPart;

public abstract class TileEntityRunnableMachine extends TileEntityElectrical implements IPowerReceptor, IConnector, IVoltage
{
	/** Forge Ore Directory name of the item to toggle power */
	public static String powerToggleItemID = "battery";
	/** Should this machine run without power */
	protected boolean runPowerless = false;
	/** BuildCraft power provider? */
	private IPowerProvider powerProvider;

	public double prevWatts, wattsReceived = 0;

	private PowerSystems[] powerList = new PowerSystems[] { PowerSystems.BUILDCRAFT, PowerSystems.MEKANISM };

	@Override
	public void updateEntity()
	{
		super.updateEntity();
		this.prevWatts = this.wattsReceived;
		if (!this.worldObj.isRemote)
		{
			if (!this.isDisabled() && (this.runPowerless || PowerSystems.runPowerLess(powerList)) && this.wattsReceived < this.getBattery(ForgeDirection.UNKNOWN))
			{
				this.wattsReceived += Math.max(this.getBattery(ForgeDirection.UNKNOWN) - this.wattsReceived, 0);
			}
			else
			{
				this.doPowerUpdate();
			}
		}
	}

	/** Any updating for power. Called seperate from the main update method too allow for inf power
	 * without unneeded power drain */
	public void doPowerUpdate()
	{
		// UNIVERSAL ELECTRICITY UPDATE
		if (!this.isDisabled())
		{
			ElectricityPack electricityPack = TileEntityRunnableMachine.consumeFromMultipleSides(this, this.getConsumingSides(), ElectricityPack.getFromWatts(this.getRequest(ForgeDirection.UNKNOWN), this.getVoltage()));
			this.onReceive(ForgeDirection.UNKNOWN, electricityPack.voltage, electricityPack.amperes);
		}
		else
		{
			ElectricityNetworkHelper.consumeFromMultipleSides(this, new ElectricityPack(0, 0));
		}
		//TODO add other power systems
	}

	@Override
	public int powerRequest(ForgeDirection from)
	{
		if (this.canConnect(from) && !this.runPowerless)
		{
			return (int) Math.ceil(this.getRequest(from) * UniversalElectricity.TO_BC_RATIO);
		}

		return 0;
	}

	protected EnumSet<ForgeDirection> getConsumingSides()
	{
		return ElectricityNetworkHelper.getDirections(this);
	}

	/** Watts this tile want to receive each tick
	 ** 
	 * @param side - If side == UNKNOWN then either the method that called it doesn't use sides or
	 * it was called from inside the machine or by the power provider. Return request equal to that
	 * off all sides at the given time */
	public abstract double getRequest(ForgeDirection side);

	/** Called when this tile gets power. Should equal getRequest or power will be wasted
	 * 
	 * @param voltage - E pressure
	 * @param amperes - E flow rate
	 * @param side - If side == UNKNOWN then either the method that called it doesn't use sides or
	 * it was called from inside the machine or by the power provider. Accept power as an internal
	 * amount */
	public void onReceive(ForgeDirection side, double voltage, double amperes)
	{
		if (voltage > this.getVoltage())
		{
			this.onDisable(2);
			return;
		}
		this.wattsReceived = Math.min(this.wattsReceived + (voltage * amperes), this.getBattery(side));
	}

	/** Amount of Watts the internal battery/cap can store.
	 * 
	 * @param side - If side == UNKNOWN then either the method that called it doesn't use sides or
	 * it was called from inside the machine or by the power provider. Return amount of all sides in
	 * this case */
	public double getBattery(ForgeDirection side)
	{
		return this.getRequest(side) * 2;
	}

	/** Amount of energy currently stored for use.
	 * 
	 * @param side - If side == UNKNOWN then either the method that called it doesn't use sides or
	 * it was called from inside the machine or by the power provider. Return amount of all sides in
	 * this case */
	public double getCurrentBattery(ForgeDirection side)
	{
		return this.wattsReceived;
	}

	/** Sets this machine to run without power only if the given stack match an ore directory name */
	public void toggleInfPower(ItemStack item)
	{
		if (item != null)
		{
			for (ItemStack stack : OreDictionary.getOres(TileEntityRunnableMachine.powerToggleItemID))
			{
				if (stack.isItemEqual(item))
				{
					this.runPowerless = !this.runPowerless;
					break;
				}
			}
		}
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt)
	{
		super.readFromNBT(nbt);
		this.wattsReceived = nbt.getDouble("wattsReceived");
		this.runPowerless = nbt.getBoolean("shouldPower");
		this.disabledTicks = nbt.getInteger("disabledTicks");
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt)
	{
		super.writeToNBT(nbt);
		nbt.setDouble("wattsReceived", this.wattsReceived);
		nbt.setBoolean("shouldPower", this.runPowerless);
		nbt.setInteger("disabledTicks", this.disabledTicks);
	}

	public static ElectricityPack consumeFromMultipleSides(TileEntity tileEntity, EnumSet<ForgeDirection> approachingDirection, ElectricityPack requestPack)
	{
		ElectricityPack consumedPack = new ElectricityPack();

		if (tileEntity != null && approachingDirection != null)
		{
			final List<IElectricityNetwork> connectedNetworks = getNetworksFromMultipleSides(tileEntity, approachingDirection);

			if (connectedNetworks.size() > 0)
			{
				/** Requests an even amount of electricity from all sides. */
				double wattsPerSide = (requestPack.getWatts() / connectedNetworks.size());
				double voltage = requestPack.voltage;
				/*TODO change this out to calculate if the network on each side can handle a larger request than another network to evenly distribute drain rather than asked the same of a network they may not be able to put out a larger amount*/
				for (IElectricityNetwork network : connectedNetworks)
				{
					if (wattsPerSide > 0 && requestPack.getWatts() > 0)
					{
						network.startRequesting(tileEntity, wattsPerSide / voltage, voltage);
						ElectricityPack receivedPack = network.consumeElectricity(tileEntity);
						consumedPack = ElectricityPack.getFromWatts(consumedPack.getWatts() + receivedPack.getWatts(), Math.max(consumedPack.voltage, receivedPack.voltage));
					}
					else
					{
						network.stopRequesting(tileEntity);
					}
				}
			}
		}

		return consumedPack;
	}

	public static List<IElectricityNetwork> getNetworksFromMultipleSides(TileEntity tileEntity, EnumSet<ForgeDirection> approachingDirection)
	{
		final List<IElectricityNetwork> connectedNetworks = new ArrayList<IElectricityNetwork>();

		for (ForgeDirection direction : ForgeDirection.VALID_DIRECTIONS)
		{
			if (approachingDirection.contains(direction))
			{
				Vector3 position = new Vector3(tileEntity);
				position.modifyPositionFromSide(direction);
				TileEntity outputConductor = position.getTileEntity(tileEntity.worldObj);
				IElectricityNetwork electricityNetwork = ElectricityNetworkHelper.getNetworkFromTileEntity(outputConductor, direction);

				if (electricityNetwork != null && !connectedNetworks.contains(connectedNetworks))
				{
					if (tileEntity instanceof INetworkPart && outputConductor instanceof INetworkPart && ((INetworkPart) tileEntity).getTileNetwork().isPartOfNetwork(outputConductor))
					{
						continue;
					}
					connectedNetworks.add(electricityNetwork);
				}
			}
		}

		return connectedNetworks;
	}

	/** Buildcraft */
	@Override
	public void setPowerProvider(IPowerProvider provider)
	{
		this.powerProvider = provider;
	}

	@Override
	public IPowerProvider getPowerProvider()
	{
		if (this.powerProvider == null)
		{
			this.powerProvider = new RunPowerProvider(this);
		}
		return this.powerProvider;
	}

	@Override
	public void doWork()
	{
	}

	class RunPowerProvider extends PowerProvider
	{
		public TileEntityRunnableMachine tileEntity;

		public RunPowerProvider(TileEntityRunnableMachine tile)
		{
			tileEntity = tile;
		}

		@Override
		public void receiveEnergy(float quantity, ForgeDirection from)
		{
			powerSources[from.ordinal()] = 2;

			tileEntity.onReceive(ForgeDirection.UNKNOWN, tileEntity.getVoltage(), (UniversalElectricity.BC3_RATIO * quantity) / tileEntity.getVoltage());

		}

		@Override
		public float useEnergy(float min, float max, boolean doUse)
		{
			float result = 0;

			if (tileEntity.wattsReceived >= min)
			{
				if (tileEntity.wattsReceived <= max)
				{
					result = (float) tileEntity.wattsReceived;
					if (doUse)
					{
						tileEntity.wattsReceived = 0;
					}
				}
				else
				{
					result = max;
					if (doUse)
					{
						tileEntity.wattsReceived -= max;
					}
				}
			}

			return result;

		}

		@Override
		public float getEnergyStored()
		{
			return (float) this.tileEntity.getCurrentBattery(ForgeDirection.UNKNOWN);
		}

		@Override
		public int getMaxEnergyReceived()
		{
			return (int) Math.ceil(this.tileEntity.getBattery(ForgeDirection.UNKNOWN));
		}

		@Override
		public int getMaxEnergyStored()
		{
			return (int) Math.ceil(this.tileEntity.getBattery(ForgeDirection.UNKNOWN));
		}
	}
}
