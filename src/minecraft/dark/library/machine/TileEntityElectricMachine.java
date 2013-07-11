package dark.library.machine;

import java.util.Random;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.ForgeDirection;
import universalelectricity.core.block.IDisableable;
import universalelectricity.core.block.IElectrical;
import universalelectricity.core.block.IElectricalStorage;
import universalelectricity.core.electricity.ElectricityPack;
import universalelectricity.prefab.tile.ElectricityHandler;
import universalelectricity.prefab.tile.TileEntityAdvanced;
import buildcraft.api.power.IPowerProvider;
import buildcraft.api.power.IPowerReceptor;
import dark.core.PowerSystems;

public abstract class TileEntityElectricMachine extends TileEntityAdvanced implements IDisableable, IElectrical, IElectricalStorage, IPowerReceptor
{
	/** Internal Battery & Energy handler */
	private ElectricityHandler electricityHandler;
	/** Rool the dice and see what you get */
	protected Random random = new Random();
	
	/** Remaining ticks of time to remain disabled */
	protected int ticksDisabled = 0;
	/** Max energy storage limit */
	protected float maxEnergy;
	/** Energy needed to run per tick regardless of function */
	protected float tickEnergy;
	
	
	/** Should this machine run without power */
	protected boolean runWithOutPower = false;
	
	/** BuildCraft power provider? */
	private IPowerProvider powerProvider;


	public TileEntityElectricMachine(float d, float d2)
	{
		this.maxEnergy = d;
		this.tickEnergy = d2;
	}

	@Override
	public void updateEntity()
	{
		super.updateEntity();

		if (this.ticksDisabled > 0)
		{
			this.ticksDisabled--;
			this.whileDisable();
		}
	}

	public ElectricityHandler ElectricHandler()
	{
		if (this.electricityHandler == null)
		{
			this.electricityHandler = new ElectricityHandler(this, 0, this.maxEnergy);
		}
		return this.electricityHandler;
	}

	@Override
	public boolean canConnect(ForgeDirection direction)
	{
		return true;
	}

	@Override
	public float receiveElectricity(ForgeDirection from, ElectricityPack receive, boolean doReceive)
	{
		if (this.runWithOutPower || receive == null || !this.canConnect(from))
		{
			return 0;
		}
		if (receive != null && receive.voltage > (Math.sqrt(2) * this.getVoltage()) && this.random.nextBoolean())
		{
			if (doReceive)
			{
				this.onDisable(20 + this.random.nextInt(100));
			}
			return 0;
		}
		return this.ElectricHandler().receiveElectricity(receive, doReceive);
	}

	@Override
	public ElectricityPack provideElectricity(ForgeDirection from, ElectricityPack request, boolean doProvide)
	{
		if (from == ForgeDirection.UNKNOWN)
		{
			return this.electricityHandler.provideElectricity(request, doProvide);
		}
		return null;
	}

	@Override
	public float getVoltage()
	{
		return 240;
	}

	@Override
	public void setEnergyStored(float energy)
	{
		this.ElectricHandler().setEnergyStored(energy);
	}

	@Override
	public float getEnergyStored()
	{
		return this.ElectricHandler().getEnergyStored();
	}

	@Override
	public float getMaxEnergyStored()
	{
		return this.ElectricHandler().getMaxEnergyStored();
	}

	/** Called every tick while this tile entity is disabled. */
	protected void whileDisable()
	{
		//TODO generate electric sparks
	}

	@Override
	public void onDisable(int duration)
	{
		this.ticksDisabled = duration;
	}

	@Override
	public boolean isDisabled()
	{
		return this.ticksDisabled > 0;
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
			this.powerProvider = new BcToUeProvider(this);
		}
		return this.powerProvider;
	}

	@Override
	public void doWork()
	{
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt)
	{
		super.readFromNBT(nbt);
		this.ElectricHandler().readFromNBT(nbt);
		this.ticksDisabled = nbt.getInteger("disabledTicks");
		this.runWithOutPower = nbt.getBoolean("shouldPower");
		if (nbt.hasKey("wattsReceived"))
		{
			this.ElectricHandler().setEnergyStored((float) nbt.getDouble("wattsReceived"));
		}
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt)
	{
		super.writeToNBT(nbt);
		this.ElectricHandler().writeToNBT(nbt);
		nbt.setInteger("disabledTicks", this.ticksDisabled);
		nbt.setBoolean("shouldPower", this.runWithOutPower);
	}
}
