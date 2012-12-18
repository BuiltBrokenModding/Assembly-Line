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

	public boolean isBeingPowered()
	{
		if (this.wattsReceived > 0) { return true; }

		int maximumTransferRange = 0;

		for (int i = 2; i < 6; i++)
		{
			ForgeDirection direction = ForgeDirection.getOrientation(i);
			TileEntity tileEntity = worldObj.getBlockTileEntity(xCoord - direction.offsetX, yCoord, zCoord - direction.offsetZ);

			if (tileEntity instanceof TileEntityConveyorBelt)
			{
				TileEntityConveyorBelt belt = (TileEntityConveyorBelt) tileEntity;

				if (belt.powerTransferRange > maximumTransferRange)
				{
					maximumTransferRange = belt.powerTransferRange;
				}
			}
		}

		this.powerTransferRange = maximumTransferRange - 1;

		return this.powerTransferRange > 0;
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
							this.wattsReceived += network.consumeElectricity(this).getWatts();
						}

					}
				}

			}

			if (this.wattsReceived >= this.getRequest().getWatts())
			{
				this.wattsReceived = 0;
				this.powerTransferRange = this.getMaxTransferRange();
			}
			else
			{
				this.powerTransferRange = 0;
			}
		}
	}

	protected abstract ElectricityPack getRequest();

	protected abstract int getMaxTransferRange();

}
