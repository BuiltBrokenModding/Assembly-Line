package dark.library.machine;

import ic2.api.Direction;
import ic2.api.energy.event.EnergyTileLoadEvent;
import ic2.api.energy.event.EnergyTileUnloadEvent;
import ic2.api.energy.tile.IEnergySink;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.oredict.OreDictionary;
import universalelectricity.core.UniversalElectricity;
import universalelectricity.core.block.IConnector;
import universalelectricity.core.block.IVoltage;
import universalelectricity.core.electricity.ElectricityPack;
import universalelectricity.prefab.tile.TileEntityElectricityRunnable;
import buildcraft.api.power.IPowerProvider;
import buildcraft.api.power.IPowerReceptor;
import buildcraft.api.power.PowerFramework;
import dark.core.PowerSystems;

public abstract class TileEntityRunnableMachine extends TileEntityElectricityRunnable implements IPowerReceptor, IEnergySink, IConnector, IVoltage
{
	public static String powerToggleItemID = "battery";

	protected boolean runPowerless = false;

	private IPowerProvider powerProvider;

	public TileEntityRunnableMachine()
	{

	}

	@Override
	public void initiate()
	{
		super.initiate();
		MinecraftForge.EVENT_BUS.post(new EnergyTileLoadEvent(this));
	}

	@Override
	public void invalidate()
	{
		MinecraftForge.EVENT_BUS.post(new EnergyTileUnloadEvent(this));
		super.invalidate();
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt)
	{
		super.readFromNBT(nbt);
		this.wattsReceived = nbt.getDouble("wattsReceived");
		this.runPowerless = nbt.getBoolean("shouldPower");
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt)
	{
		super.writeToNBT(nbt);
		nbt.setDouble("wattsReceived", this.wattsReceived);
		nbt.setBoolean("shouldPower", this.runPowerless);
	}

	/**
	 * Sets this machine to run without power only if the given stack match an ore directory name
	 */
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
	public void updateEntity()
	{
		super.updateEntity();

		if (this.wattsReceived < this.getWattBuffer() && (this.runPowerless || PowerSystems.runPowerLess(PowerSystems.INDUSTRIALCRAFT, PowerSystems.BUILDCRAFT, PowerSystems.MEKANISM)))
		{
			this.wattsReceived += Math.max(this.getWattBuffer() - this.wattsReceived, 0);
		}
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
			int requiredEnergy = (int) (this.getRequest().getWatts() * UniversalElectricity.TO_BC_RATIO);
			float energyReceived = this.powerProvider.useEnergy(requiredEnergy, requiredEnergy, true);
			this.onReceive(ElectricityPack.getFromWatts(UniversalElectricity.BC3_RATIO * energyReceived, this.getVoltage()));
		}
	}

	/**
	 * IC2
	 */
	@Override
	public boolean acceptsEnergyFrom(TileEntity emitter, Direction direction)
	{
		if (this.getConsumingSides() != null)
		{
			return this.getConsumingSides().contains(direction.toForgeDirection());
		}
		else
		{
			return true;
		}
	}

	@Override
	public boolean isAddedToEnergyNet()
	{
		return this.ticks > 0;
	}

	@Override
	public int demandsEnergy()
	{
		return (int) Math.ceil(this.getRequest().getWatts() * UniversalElectricity.TO_IC2_RATIO);
	}

	@Override
	public int injectEnergy(Direction direction, int i)
	{
		double givenElectricity = i * UniversalElectricity.IC2_RATIO;
		double rejects = 0;

		if (givenElectricity > this.getWattBuffer())
		{
			rejects = givenElectricity - this.getRequest().getWatts();
		}

		this.onReceive(new ElectricityPack(givenElectricity / this.getVoltage(), this.getVoltage()));

		return (int) (rejects * UniversalElectricity.TO_IC2_RATIO);
	}

	@Override
	public int getMaxSafeInput()
	{
		return 2048;
	}

	/**
	 * Buildcraft
	 */
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
			return (int) (this.getRequest().getWatts() * UniversalElectricity.TO_BC_RATIO);
		}

		return 0;
	}
}
