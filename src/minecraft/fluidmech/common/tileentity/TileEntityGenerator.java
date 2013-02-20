package fluidmech.common.tileentity;

import hydraulic.core.implement.IReadOut;

import java.util.EnumSet;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.ForgeDirection;
import universalelectricity.core.electricity.ElectricityConnections;
import universalelectricity.core.electricity.ElectricityNetwork;
import universalelectricity.core.implement.IConductor;
import universalelectricity.core.vector.Vector3;
import universalelectricity.prefab.implement.IRedstoneReceptor;
import universalelectricity.prefab.network.IPacketReceiver;
import universalelectricity.prefab.tile.TileEntityElectricityProducer;

import com.google.common.io.ByteArrayDataInput;

import fluidmech.api.helpers.connectionHelper;
import fluidmech.api.mech.IForce;
import fluidmech.common.FluidMech;
import fluidmech.common.MetaGroup;
import fluidmech.common.block.BlockGenerator;


public class TileEntityGenerator extends TileEntityElectricityProducer implements IPacketReceiver, IForce, IReadOut, IRedstoneReceptor
{
	public boolean isPowered = false;

	ForgeDirection facing = ForgeDirection.DOWN;

	public int force = 0;// current total force
	public int aForce = 0;// force this unit can apply
	public int pos = 0;// current pos of rotation max of 8
	public int disableTicks = 0;// time disabled
	public int tCount = 0;
	double WATTS_PER_TICK = 500;
	double joulesReceived = 0;
	double genAmmount = 0;// watt output of machine

	IConductor[] wires = { null, null, null, null, null, null };

	public boolean needUpdate()
	{
		return false;
	}

	public void initiate()
	{
		this.registerConnections();
		this.worldObj.notifyBlocksOfNeighborChange(this.xCoord, this.yCoord, this.zCoord, FluidMech.blockGenerator.blockID);
	}

	public void registerConnections()
	{
		int notchMeta = MetaGroup.getFacingMeta(worldObj.getBlockMetadata(xCoord, yCoord, zCoord));
		ForgeDirection facing = ForgeDirection.getOrientation(notchMeta).getOpposite();
		ForgeDirection[] dirs = new ForgeDirection[] { ForgeDirection.UNKNOWN, ForgeDirection.UNKNOWN, ForgeDirection.UNKNOWN, ForgeDirection.UNKNOWN, ForgeDirection.UNKNOWN, ForgeDirection.UNKNOWN };
		ElectricityConnections.registerConnector(this, EnumSet.of(facing.getOpposite()));
		for (int i = 2; i < 6; i++)
		{
			ForgeDirection dir = ForgeDirection.getOrientation(i);
			if (dir != facing && dir != facing.getOpposite())
			{
				dirs[i] = dir;
			}
		}
		ElectricityConnections.registerConnector(this, EnumSet.of(dirs[0], dirs[1], dirs[2], dirs[3], dirs[4], dirs[5]));
	}

	@Override
	public void updateEntity()
	{
		this.genAmmount = Math.abs(force / this.getVoltage());
		// wire count update
		int wireCount = 0;
		TileEntity[] ents = connectionHelper.getSurroundingTileEntities(worldObj, xCoord, yCoord, zCoord);
		this.wires = new IConductor[6];
		for (int i = 0; i < ents.length; i++)
		{
			if (ents[i] instanceof IConductor)
			{
				this.wires[i] = (IConductor) ents[i];
				wireCount++;
			}
		}// end wire count
		if (tCount-- <= 0)
		{
			BlockGenerator.checkForPower(worldObj, xCoord, yCoord, zCoord);
			tCount = 10;
			if (this.force > 0 || this.isPowered)
			{
				this.pos++;
				if (force < 0)
					pos -= 2;
				if (pos >= 8)
					pos = 0;
			}
		}
		int notchMeta = MetaGroup.getFacingMeta(worldObj.getBlockMetadata(xCoord, yCoord, zCoord));
		facing = ForgeDirection.getOrientation(notchMeta).getOpposite();
		TileEntity ent = worldObj.getBlockTileEntity(xCoord + facing.offsetX, yCoord + facing.offsetY, zCoord + facing.offsetZ);

		if (!this.worldObj.isRemote)
		{

			if (!this.isPowered)
			{

				for (int i = 2; i < 6; i++)
				{
					ForgeDirection dir = ForgeDirection.getOrientation(i);
					if (dir != facing && dir != facing.getOpposite())
					{

						TileEntity outputTile = Vector3.getConnectorFromSide(this.worldObj, new Vector3(this), dir);
						ElectricityNetwork network = ElectricityNetwork.getNetworkFromTileEntity(outputTile, dir);
						if (network != null)
						{
							if (network.getRequest().getWatts() > 0)
							{
								network.startProducing(this, (this.genAmmount), this.getVoltage());
							}
							else
							{
								network.stopProducing(this);
							}
						}
					}
				}
			}
			else
			{
				for (int i = 2; i < 6; i++)
				{
					ForgeDirection dir = ForgeDirection.getOrientation(i);
					if (dir != facing && dir != facing.getOpposite())
					{
						TileEntity inputTile = Vector3.getTileEntityFromSide(this.worldObj, new Vector3(this), dir);
						ElectricityNetwork network = ElectricityNetwork.getNetworkFromTileEntity(inputTile, dir);
						if (network != null)
						{

							if (this.joulesReceived < this.WATTS_PER_TICK)
							{
								network.startRequesting(this, WATTS_PER_TICK / this.getVoltage(), this.getVoltage());
								this.joulesReceived = Math.max(Math.min(this.joulesReceived + network.consumeElectricity(this).getWatts(), WATTS_PER_TICK), 0);
							}
							else
							{
								network.stopRequesting(this);
							}
						}
					}
				}
				if (this.joulesReceived >= this.WATTS_PER_TICK - 50)
				{
					joulesReceived -= this.WATTS_PER_TICK;
					TileEntity rod = Vector3.getTileEntityFromSide(worldObj, new Vector3(this), facing);
					if (rod instanceof IForce && ((IForce) rod).canInputSide(facing))
					{
						((IForce) rod).applyForce(10000);
					}
					else if (rod instanceof IForce && ((IForce) rod).canOutputSide(facing))
					{
						((IForce) rod).applyForce(-10000);
					}
				}

			}
		}

		super.updateEntity();
	}

	public void outputEnergy(ElectricityNetwork network, IConductor connectedElectricUnit, TileEntity outputTile)
	{
		if (network != null)
		{
			if (network.getRequest().getWatts() > 0)
			{
				connectedElectricUnit = (IConductor) outputTile;
			}
			else
			{
				connectedElectricUnit = null;
			}
		}
		else
		{
			connectedElectricUnit = null;
		}

		if (connectedElectricUnit != null)
		{
			if (this.genAmmount > 0)
			{
				connectedElectricUnit.getNetwork().startProducing(this, (this.genAmmount / this.getVoltage()) / 20, this.getVoltage());
			}
			else
			{
				connectedElectricUnit.getNetwork().stopProducing(this);
			}
		}

	}

	// ------------------------------
	// Data handling
	// ------------------------------
	@Override
	public void handlePacketData(INetworkManager network, int packetType, Packet250CustomPayload packet, EntityPlayer player, ByteArrayDataInput data)
	{
		// TODO Auto-generated method stub

	}

	// ------------------------------
	// Mechanics
	// ------------------------------
	@Override
	public int getForceSide(ForgeDirection side)
	{
		if (side == facing.getOpposite()) { return aForce; }
		return 0;
	}

	@Override
	public int getForce()
	{
		return this.force;
	}

	@Override
	public boolean canOutputSide(ForgeDirection side)
	{
		if (side == facing) { return true; }
		return false;
	}

	@Override
	public boolean canInputSide(ForgeDirection side)
	{
		if (side == facing || side == facing.getOpposite()) { return true; }
		return false;
	}

	@Override
	public int applyForce(int force)
	{
		this.force = force;
		return force;
	}

	@Override
	public int getAnimationPos()
	{
		return pos;
	}

	// ------------------------------
	// Electric
	// ------------------------------
	@Override
	public void onDisable(int duration)
	{
		this.disableTicks = duration;
	}

	@Override
	public boolean isDisabled()
	{
		if (disableTicks-- <= 0) { return false; }
		return true;
	}

	@Override
	public double getVoltage(Object... data)
	{
        return 120;
	    
	}

	@Override
	public String getMeterReading(EntityPlayer user, ForgeDirection side)
	{
		if (this.isPowered)
			return "Outputing Force " + this.joulesReceived + "J " + "pos " + this.pos;
		return this.force + "N Input " + this.genAmmount + "W output " + "pos " + this.pos;
	}

	// ------------------------------
	// redSand
	// ------------------------------
	@Override
	public void onPowerOn()
	{
		this.isPowered = true;

	}

	@Override
	public void onPowerOff()
	{
		this.isPowered = false;

	}

}
