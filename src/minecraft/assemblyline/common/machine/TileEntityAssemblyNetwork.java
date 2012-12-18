package assemblyline.common.machine;

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.ForgeDirection;
import universalelectricity.core.electricity.ElectricityConnections;
import universalelectricity.core.electricity.ElectricityNetwork;
import universalelectricity.core.electricity.ElectricityPack;
import universalelectricity.core.vector.Vector3;
import universalelectricity.prefab.tile.TileEntityElectricityReceiver;
import assemblyline.common.machine.belt.TileEntityConveyorBelt;

/**
 * A class to be inherited by all machines on the assembly line. This will allow all machines to be
 * able to be powered through the powering of only one machine.
 * 
 * @author Calclavia
 * 
 */
public abstract class TileEntityAssemblyNetwork extends TileEntityElectricityReceiver
{
	/**
	 * The amount of watts received.
	 */
	public double wattsReceived = 0;

	/**
	 * The range in which power can be transfered.
	 */
	public int powerTransferRange = 0;

	public boolean isRunning()
	{
		return this.powerTransferRange > 0 || this.wattsReceived > this.getRequest().getWatts();
	}

	public void updatePowerTransferRange()
	{
		int maximumTransferRange = 0;

		for (int i = 0; i < 6; i++)
		{
			ForgeDirection direction = ForgeDirection.getOrientation(i);
			TileEntity tileEntity = worldObj.getBlockTileEntity(this.xCoord + direction.offsetX, this.yCoord + direction.offsetY, this.zCoord + direction.offsetZ);

			if (tileEntity != null)
			{
				if (tileEntity instanceof TileEntityAssemblyNetwork)
				{
					TileEntityAssemblyNetwork assemblyNetwork = (TileEntityAssemblyNetwork) tileEntity;

					if (assemblyNetwork.powerTransferRange > maximumTransferRange)
					{
						maximumTransferRange = assemblyNetwork.powerTransferRange;
					}
				}
			}
		}

		this.powerTransferRange = Math.max(maximumTransferRange - 1, 0);
	}

	@Override
	public void updateEntity()
	{
		super.updateEntity();

		if (!this.worldObj.isRemote)
		{
			for (Object obj : ElectricityConnections.getDirections(this).toArray())
			{
				if (obj != null)
				{
					ForgeDirection inputDirection = (ForgeDirection) obj;
					TileEntity inputTile = Vector3.getTileEntityFromSide(this.worldObj, new Vector3(this), inputDirection);

					ElectricityNetwork network = ElectricityNetwork.getNetworkFromTileEntity(inputTile, inputDirection);

					if (network != null)
					{
						if (this.wattsReceived >= this.getRequest().getWatts())
						{
							network.stopRequesting(this);
						}
						else
						{
							network.startRequesting(this, this.getRequest());
							this.wattsReceived += network.consumeElectricity(this).getWatts() * 2;
						}
					}
				}

			}
		}

		this.onUpdate();

		if (this.ticks % 10 == 0)
		{
			if (this.wattsReceived >= this.getRequest().getWatts())
			{
				this.wattsReceived = 0;
				this.powerTransferRange = this.getMaxTransferRange();
			}
			else
			{
				this.powerTransferRange = 0;
				this.updatePowerTransferRange();
			}
		}
	}

	protected void onUpdate()
	{
		
	}
	
	@Override
	public double getVoltage()
	{
		return 20;
	}

	protected ElectricityPack getRequest()
	{
		return new ElectricityPack(15, this.getVoltage());
	}

	protected int getMaxTransferRange()
	{
		return 20;
	}
}
