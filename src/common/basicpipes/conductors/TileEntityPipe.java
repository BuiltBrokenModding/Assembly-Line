package basicpipes.conductors;

import net.minecraft.src.EntityPlayer;
import net.minecraft.src.INetworkManager;
import net.minecraft.src.NBTTagCompound;
import net.minecraft.src.Packet;
import net.minecraft.src.Packet250CustomPayload;
import net.minecraft.src.TileEntity;
import net.minecraftforge.common.ForgeDirection;
import universalelectricity.core.Vector3;
import universalelectricity.prefab.network.IPacketReceiver;
import universalelectricity.prefab.network.PacketManager;
import basicpipes.pipes.api.ILiquidConsumer;
import basicpipes.pipes.api.ILiquidProducer;
import basicpipes.pipes.api.Liquid;
import basicpipes.pipes.api.MHelper;

import com.google.common.io.ByteArrayDataInput;

public class TileEntityPipe extends TileEntity implements ILiquidConsumer,
		IPacketReceiver {
	protected Liquid type = Liquid.DEFUALT;

	public int capacity = 2;
	public int presure = 0;
	public int connectedUnits = 0;
	public int liquidStored = 0;
	private int count = 0;
	private int count2 = 0;

	protected boolean firstUpdate = true;

	public TileEntity[] connectedBlocks = { null, null, null, null, null, null };

	public int getPressure() {
		return this.presure;
	}

	@Override
	public void updateEntity() {
		int highestPressure = 0;
		if (++count >= 5) {
			this.connectedBlocks = MHelper.getSourounding(worldObj, xCoord,
					yCoord, zCoord);
			for (int i = 0; i < 6; i++) {

				if (connectedBlocks[i] instanceof ILiquidConsumer
						&& ((ILiquidConsumer) connectedBlocks[i])
								.canRecieveLiquid(this.type, ForgeDirection
										.getOrientation(i).getOpposite())) {
					this.connectedUnits++;
					if (connectedBlocks[i] instanceof TileEntityPipe) {
						if (((TileEntityPipe) connectedBlocks[i]).getPressure() > highestPressure) {
							highestPressure = ((TileEntityPipe) connectedBlocks[i])
									.getPressure();
						}
					}
				} else if (connectedBlocks[i] instanceof ILiquidProducer
						&& ((ILiquidProducer) connectedBlocks[i])
								.canProduceLiquid(this.type, ForgeDirection
										.getOrientation(i).getOpposite())) {
					this.connectedUnits++;
					if (((ILiquidProducer) connectedBlocks[i])
							.canProducePresure(this.type,
									ForgeDirection.getOrientation(i))
							&& ((ILiquidProducer) connectedBlocks[i])
									.presureOutput(this.type, ForgeDirection
											.getOrientation(i).getOpposite()) > highestPressure) {
						highestPressure = ((ILiquidProducer) connectedBlocks[i])
								.presureOutput(this.type,
										ForgeDirection.getOrientation(i));
					}
				} else {
					connectedBlocks[i] = null;
				}
			}
			if (!worldObj.isRemote) {
				if (firstUpdate || count2++ >= 10) {
					count2 = 0;
					firstUpdate = false;
					Packet packet = PacketManager.getPacket("Pipes", this,
							new Object[] { this.type.ordinal() });
					PacketManager.sendPacketToClients(packet, worldObj,
							Vector3.get(this), 60);
				}
				this.presure = highestPressure - 1;
				for (int i = 0; i < 6; i++) {
					if (connectedBlocks[i] instanceof ILiquidProducer) {
						int vol = ((ILiquidProducer) connectedBlocks[i])
								.onProduceLiquid(this.type, this.capacity
										- this.liquidStored, ForgeDirection
										.getOrientation(i).getOpposite());
						this.liquidStored = Math.min(this.liquidStored + vol,
								this.capacity);
					}
					if (connectedBlocks[i] instanceof ILiquidConsumer
							&& this.liquidStored > 0 && this.presure > 0) {
						if (connectedBlocks[i] instanceof TileEntityPipe) {
							this.liquidStored--;
							int vol = ((ILiquidConsumer) connectedBlocks[i])
									.onReceiveLiquid(this.type, Math.max(
											this.liquidStored, 1),
											ForgeDirection.getOrientation(i)
													.getOpposite());
							this.liquidStored += vol;
						} else {
							this.liquidStored = ((ILiquidConsumer) connectedBlocks[i])
									.onReceiveLiquid(this.type,
											this.liquidStored, ForgeDirection
													.getOrientation(i)
													.getOpposite());
						}
					}
				}
			}
		}
	}

	// ---------------
	// liquid stuff
	// ---------------
	@Override
	public int onReceiveLiquid(Liquid type, int vol, ForgeDirection side) {
		if (type == this.type) {
			int rejectedVolume = Math.max((this.getStoredLiquid(type) + vol)
					- this.capacity, 0);
			this.liquidStored = Math.min(
					Math.max((liquidStored + vol - rejectedVolume), 0),
					this.capacity);
			return rejectedVolume;
		}
		return vol;
	}

	/**
	 * @return Return the stored volume in this pipe.
	 */
	@Override
	public int getStoredLiquid(Liquid type) {
		if (type == this.type) {
			return this.liquidStored;
		}
		return 0;
	}

	@Override
	public int getLiquidCapacity(Liquid type) {
		if (type == this.type) {
			return this.capacity;
		}
		return 0;
	}

	// find wether or not this side of X block can recieve X liquid type. Also
	// use to determine connection of a pipe
	@Override
	public boolean canRecieveLiquid(Liquid type, ForgeDirection side) {
		if (type == this.type) {
			return true;
		}
		return false;
	}

	// returns liquid type
	public Liquid getType() {
		return this.type;
	}

	// used by the item to set the liquid type on spawn
	public void setType(Liquid rType) {
		this.type = rType;

	}

	// ---------------------
	// data
	// --------------------
	@Override
	public void handlePacketData(INetworkManager network, int packetType,
			Packet250CustomPayload packet, EntityPlayer player,
			ByteArrayDataInput data) {
		try {
			int type = data.readInt();
			if (worldObj.isRemote) {
				this.type = Liquid.getLiquid(type);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * Reads a tile entity from NBT.
	 */
	public void readFromNBT(NBTTagCompound par1NBTTagCompound) {
		super.readFromNBT(par1NBTTagCompound);
		this.liquidStored = par1NBTTagCompound.getInteger("liquid");
		this.type = Liquid.getLiquid(par1NBTTagCompound.getInteger("type"));
	}

	/**
	 * Writes a tile entity to NBT.
	 */
	public void writeToNBT(NBTTagCompound par1NBTTagCompound) {
		super.writeToNBT(par1NBTTagCompound);
		par1NBTTagCompound.setInteger("liquid", this.liquidStored);
		par1NBTTagCompound.setInteger("type", this.type.ordinal());
	}
}
