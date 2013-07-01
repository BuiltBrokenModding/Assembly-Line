package dark.library.machine;

import java.util.EnumSet;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.oredict.OreDictionary;
import universalelectricity.core.UniversalElectricity;
import universalelectricity.core.block.IConnector;
import universalelectricity.core.block.IVoltage;
import universalelectricity.core.electricity.ElectricityNetworkHelper;
import universalelectricity.core.electricity.ElectricityPack;
import universalelectricity.prefab.tile.TileEntityElectrical;
import universalelectricity.prefab.tile.TileEntityElectricityRunnable;
import buildcraft.api.power.IPowerProvider;
import buildcraft.api.power.IPowerReceptor;
import buildcraft.api.power.PowerFramework;
import dark.core.PowerSystems;

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
			if ((this.runPowerless || PowerSystems.runPowerLess(powerList)) && this.wattsReceived < this.getWattBuffer())
			{
				this.wattsReceived += Math.max(this.getWattBuffer() - this.wattsReceived, 0);
			}
			else
			{
				this.doPowerUpdate();
			}
		}
	}

	public void doPowerUpdate()
	{
		// UNIVERSAL ELECTRICITY UPDATE
		if (!this.isDisabled())
		{
			ElectricityPack electricityPack = ElectricityNetworkHelper.consumeFromMultipleSides(this, this.getConsumingSides(), ElectricityPack.getFromWatts(this.getRequest(), this.getVoltage()));
			this.onReceive(electricityPack.voltage, electricityPack.amperes);
		}
		else
		{
			ElectricityNetworkHelper.consumeFromMultipleSides(this, new ElectricityPack());
		}

		// BUILDCRAFT POWER UPDATE
		if (PowerFramework.currentFramework != null)
		{
			if (this.powerProvider == null)
			{
				this.powerProvider = PowerFramework.currentFramework.createPowerProvider();
				this.powerProvider.configure(0, 0, Integer.MAX_VALUE, 0, Integer.MAX_VALUE);
			}
		}
		if (this.powerProvider != null)
		{
			float requiredEnergy = (float) (this.getRequest() * UniversalElectricity.TO_BC_RATIO);
			float energyReceived = this.powerProvider.useEnergy(0, requiredEnergy, true);
			this.onReceive(this.getVoltage(), (UniversalElectricity.BC3_RATIO * energyReceived) / this.getVoltage());
		}
		//TODO add other power systems
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
		return this.powerProvider;
	}

	@Override
	public void doWork()
	{
	}

	@Override
	public int powerRequest(ForgeDirection from)
	{
		if (this.canConnect(from))
		{
			return (int) Math.ceil(this.getRequest() * UniversalElectricity.TO_BC_RATIO);
		}

		return 0;
	}

	protected EnumSet<ForgeDirection> getConsumingSides()
	{
		return ElectricityNetworkHelper.getDirections(this);
	}

	/** Watts this tile needs a tick to function */
	public abstract double getRequest();

	public void onReceive(double voltage, double amperes)
	{
		if (voltage > this.getVoltage())
		{
			this.onDisable(2);
			return;
		}
		this.wattsReceived = Math.min(this.wattsReceived + (voltage * amperes), this.getWattBuffer());
	}

	/** @return The amount of internal buffer that may be stored within this machine. This will make
	 * the machine run smoother as electricity might not always be consistent. */
	public double getWattBuffer()
	{
		return this.getRequest() * 2;
	}

	/** Sets this machine to run without power only if the given stack match an ore directory name */
	public void toggleInfPower(ItemStack item)
	{
		for (ItemStack stack : OreDictionary.getOres(this.powerToggleItemID))
		{
			if (stack.isItemEqual(item))
			{
				this.runPowerless = !this.runPowerless;
				break;
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
}
