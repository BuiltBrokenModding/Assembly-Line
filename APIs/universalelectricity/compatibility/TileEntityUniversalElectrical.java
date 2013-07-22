package universalelectricity.compatibility;

import ic2.api.Direction;
import ic2.api.energy.event.EnergyTileLoadEvent;
import ic2.api.energy.event.EnergyTileSourceEvent;
import ic2.api.energy.event.EnergyTileUnloadEvent;
import ic2.api.energy.tile.IEnergySink;
import ic2.api.energy.tile.IEnergySource;
import ic2.api.energy.tile.IEnergyTile;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.common.MinecraftForge;
import universalelectricity.core.electricity.ElectricityPack;
import universalelectricity.core.vector.Vector3;
import universalelectricity.prefab.tile.TileEntityElectrical;
import buildcraft.api.power.IPowerReceptor;
import buildcraft.api.power.PowerHandler;
import buildcraft.api.power.PowerHandler.PowerReceiver;
import buildcraft.api.power.PowerHandler.Type;

/**
 * A universal electricity tile used for tiles that consume or produce electricity.
 * 
 * Extend this class or use as a reference for your own implementation of compatible electrical
 * tiles.
 * 
 * @author micdoodle8, Calclavia
 * 
 */
public abstract class TileEntityUniversalElectrical extends TileEntityElectrical implements IEnergySink, IEnergySource, IPowerReceptor
{
	protected boolean isAddedToEnergyNet;
	public PowerHandler bcPowerHandler;
	public Type bcBlockType = Type.MACHINE;

	public void initiate()
	{
		super.initiate();
		this.bcPowerHandler = new PowerHandler(this, this.bcBlockType);
		this.bcPowerHandler.configure(0, 100, 0, (int) Math.ceil(this.getMaxEnergyStored() * Compatibility.BC3_RATIO));
	}

	@Override
	public void updateEntity()
	{
		super.updateEntity();

		// Register to the IC2 Network
		if (!this.worldObj.isRemote && !this.isAddedToEnergyNet)
		{
			if (Compatibility.isIndustrialCraft2Loaded())
			{
				MinecraftForge.EVENT_BUS.post(new EnergyTileLoadEvent(this));
			}

			this.isAddedToEnergyNet = true;
		}

		this.produce();
	}

	@Override
	public void produce()
	{
		if (!this.worldObj.isRemote)
		{
			for (ForgeDirection outputDirection : this.getOutputDirections())
			{
				this.produceUE(outputDirection);
				this.produceIC2(outputDirection);
				this.produceBuildCraft(outputDirection);
			}
		}

		if (Compatibility.isBuildcraftLoaded())
		{
			/**
			 * Cheat BuildCraft powerHandler and always empty energy inside of it.
			 */
			this.receiveElectricity(this.bcPowerHandler.getEnergyStored(), true);
			this.bcPowerHandler.setEnergy(0);
		}
	}

	public void produceIC2(ForgeDirection outputDirection)
	{
		if (!this.worldObj.isRemote)
		{
			float provide = this.getProvide(outputDirection);

			if (this.getEnergyStored() >= provide && provide > 0)
			{
				if (Compatibility.isIndustrialCraft2Loaded())
				{
					int ic2Provide = (int) Math.ceil(provide * Compatibility.TO_IC2_RATIO);

					EnergyTileSourceEvent event = new EnergyTileSourceEvent(this, ic2Provide);
					MinecraftForge.EVENT_BUS.post(event);
					this.setEnergyStored(this.getEnergyStored() - ((ic2Provide * Compatibility.IC2_RATIO) - (event.amount * Compatibility.IC2_RATIO)));

				}
			}
		}
	}

	public void produceBuildCraft(ForgeDirection outputDirection)
	{
		if (!this.worldObj.isRemote)
		{
			float provide = this.getProvide(outputDirection);

			if (this.getEnergyStored() >= provide && provide > 0)
			{
				if (Compatibility.isBuildcraftLoaded())
				{
					TileEntity tileEntity = new Vector3(this).modifyPositionFromSide(outputDirection).getTileEntity(this.worldObj);

					if (tileEntity instanceof IPowerReceptor)
					{
						PowerReceiver receiver = ((IPowerReceptor) tileEntity).getPowerReceiver(outputDirection.getOpposite());

						if (receiver != null)
						{
							float bc3Provide = provide * Compatibility.TO_BC_RATIO;
							float energyUsed = Math.min(receiver.receiveEnergy(this.bcBlockType, bc3Provide, outputDirection.getOpposite()), bc3Provide);
							this.setEnergyStored(this.getEnergyStored() - (bc3Provide - (energyUsed * Compatibility.TO_BC_RATIO)));
						}
					}
				}
			}
		}
	}

	/**
	 * IC2 Methods
	 */
	@Override
	public boolean acceptsEnergyFrom(TileEntity emitter, Direction direction)
	{
		return this.canConnect(direction.toForgeDirection());
	}

	@Override
	public boolean isAddedToEnergyNet()
	{
		return this.isAddedToEnergyNet;
	}

	@Override
	public void invalidate()
	{
		this.unloadTileIC2();
		super.invalidate();
	}

	@Override
	public void onChunkUnload()
	{
		this.unloadTileIC2();
		super.onChunkUnload();
	}

	private void unloadTileIC2()
	{
		if (this.isAddedToEnergyNet && this.worldObj != null)
		{
			if (Compatibility.isIndustrialCraft2Loaded())
			{
				MinecraftForge.EVENT_BUS.post(new EnergyTileUnloadEvent(this));
			}

			this.isAddedToEnergyNet = false;
		}
	}

	@Override
	public int demandsEnergy()
	{
		return (int) Math.ceil(this.getRequest(ForgeDirection.UNKNOWN) * Compatibility.TO_IC2_RATIO);
	}

	@Override
	public int injectEnergy(Direction directionFrom, int amount)
	{
		if (!directionFrom.toForgeDirection().equals(this.getInputDirections()))
		{
			return amount;
		}

		float convertedEnergy = amount * Compatibility.IC2_RATIO;

		ElectricityPack toSend = ElectricityPack.getFromWatts(convertedEnergy, this.getVoltage());

		int receive = (int) Math.floor(this.receiveElectricity(directionFrom.toForgeDirection(), toSend, true));

		// Return the difference, since injectEnergy returns left over energy, and
		// receiveElectricity returns energy used.
		return (int) Math.floor(amount - receive * Compatibility.TO_IC2_RATIO);
	}

	@Override
	public int getMaxEnergyOutput()
	{
		return (int) Math.ceil(this.getProvide(ForgeDirection.UNKNOWN));
	}

	@Override
	public boolean emitsEnergyTo(TileEntity receiver, Direction direction)
	{
		return receiver instanceof IEnergyTile && direction.toForgeDirection().equals(this.getOutputDirections());
	}

	@Override
	public int getMaxSafeInput()
	{
		return Integer.MAX_VALUE;
	}

	/**
	 * BuildCraft power support
	 */
	@Override
	public PowerReceiver getPowerReceiver(ForgeDirection side)
	{
		return this.bcPowerHandler.getPowerReceiver();
	}

	@Override
	public void doWork(PowerHandler workProvider)
	{

	}

	@Override
	public World getWorld()
	{
		return this.getWorldObj();
	}
}
