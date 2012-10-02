package steampower.turbine;

import net.minecraft.src.EntityPlayer;
import net.minecraft.src.IInventory;
import net.minecraft.src.Item;
import net.minecraft.src.ItemStack;
import net.minecraft.src.NBTTagCompound;
import net.minecraft.src.NBTTagList;
import net.minecraft.src.NetworkManager;
import net.minecraft.src.Packet250CustomPayload;
import net.minecraft.src.TileEntity;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.common.ISidedInventory;
import steampower.TileEntityMachine;
import universalelectricity.network.IPacketReceiver;
import basicpipes.conductors.TileEntityRod;
import basicpipes.pipes.api.ILiquidConsumer;
import basicpipes.pipes.api.ILiquidProducer;
import basicpipes.pipes.api.IMechenical;
import basicpipes.pipes.api.Liquid;
import basicpipes.conductors.*;

import com.google.common.io.ByteArrayDataInput;

public class TileEntitySteamPiston extends TileEntityMachine implements IPacketReceiver,ILiquidConsumer,ILiquidProducer,IMechenical
{
	public int force = 0;
	public int aForce = 0;
	private int frictionLoad = 10;
	public int steam = 0;
	public int water = 0;
	public int maxWater = 1;
	public int maxSteam = 10;
	public int pos = 0; //max at 7
	private int tickCount = 0;
	private int runTime = 0;
	
	private ForgeDirection frontDir;
	private ForgeDirection backDir;
	
	public boolean running= false;
	
	@Override
	public void updateEntity()
	{
		if(tickCount++ >=10)
		{
			int meta = worldObj.getBlockMetadata(xCoord, yCoord, zCoord);
			int nMeta = 0;
			switch(meta)
			{
				case 0: nMeta = 2;break;
				case 1: nMeta = 4;break;
				case 2: nMeta = 3;break;
				case 3: nMeta = 5;break;
			}
			frontDir = ForgeDirection.getOrientation(nMeta);
			backDir = ForgeDirection.getOrientation(nMeta).getOpposite();
			if(!worldObj.isRemote)
			{
				if(this.runTime < 0 && this.steam > 0)
				{
					this.steam--;
					this.runTime+=60;
				}else
				if(this.runTime > 0)
				{
					this.running = true;
					this.runTime-=1;
				}else
				{
					this.running = false;
				}
				TileEntity ff = worldObj.getBlockTileEntity(xCoord+frontDir.offsetX, yCoord+1, zCoord+frontDir.offsetZ);
				TileEntity bb = worldObj.getBlockTileEntity(xCoord+backDir.offsetX, yCoord+1, zCoord+backDir.offsetZ);
				if(ff instanceof IMechenical)
				{
					((IMechenical) ff).applyForce(this.aForce);
				}
				if(bb instanceof TileEntitySteamPiston)
				{
					this.pos = ((TileEntitySteamPiston) bb).pos + 1;
					if(this.pos > 7)
					{
						pos = 0;
					}
				}else
				if(this.running)
				{
					this.pos += 1;
					if(this.pos > 7)
					{
						pos = 0;
					}
				}
			}
		}
	}
	
	
	//-------------------
	//Liquid and mechanical stuff
	//----------------
	@Override
	public double getForce(ForgeDirection side) {
		return aForce;
	}

	@Override
	public boolean canOutputSide(ForgeDirection side) {
		if(frontDir == side)
		{
			return true;
		}
		return false;
	}

	@Override
	public boolean canInputSide(ForgeDirection side) {
		if(backDir == side)
		{
			return true;
		}
		return false;
	}

	@Override
	public double applyForce(int force) {
		return aForce;
	}

	@Override
	public int onProduceLiquid(Liquid type, int vol, ForgeDirection side) {
		if(type == Liquid.WATER)
		{
			if(this.water > 0)
			{
				this.water -= 1;
				return 1;
			}
		}
		return 0;
	}

	@Override
	public boolean canProduceLiquid(Liquid type, ForgeDirection side) {
		if(type == Liquid.WATER)
		{
			return true;
		}
		return false;
	}

	@Override
	public boolean canProducePresure(Liquid type, ForgeDirection side) {
		if(type == Liquid.WATER)
		{
			return true;
		}
		return false;
	}

	@Override
	public int presureOutput(Liquid type, ForgeDirection side) {
		if(type == Liquid.WATER)
		{
			return 32;
		}
		return 0;
	}

	@Override
	public int onReceiveLiquid(Liquid type, int vol, ForgeDirection side) {
		if(type == Liquid.STEAM)
		{
			int rejectedSteam = Math.max((this.steam + vol) - this.getLiquidCapacity(Liquid.STEAM), 0);
			this.steam += vol - rejectedSteam;
			return rejectedSteam;
		}
		return 0;
	}

	@Override
	public boolean canRecieveLiquid(Liquid type, ForgeDirection forgeDirection) {
		if(type == Liquid.STEAM)
		{
			return true;
		}
		return false;
	}

	@Override
	public int getStoredLiquid(Liquid type) {
		if(type == Liquid.WATER)
		{
			return this.water;
		}else
		if(type == Liquid.STEAM)
		{
			return this.steam;
		}
		return 0;
	}

	@Override
	public int getLiquidCapacity(Liquid type) {
		if(type == Liquid.WATER)
		{
			return this.maxWater;
		}else
		if(type == Liquid.STEAM)
		{
			return this.maxSteam;
		}
		return 0;
	}
	//-------------------
	//Data
	//----------------
	public Object[] getSendData()
	{
		return new Object[]{steam,water,force,aForce,pos};
	}
	@Override
	public void handlePacketData(NetworkManager network,Packet250CustomPayload packet, EntityPlayer player,ByteArrayDataInput dataStream) {
		try
		{
			this.steam = dataStream.readInt();
			this.water = dataStream.readInt();
			this.force = dataStream.readInt();
			this.aForce = dataStream.readInt();
			this.pos = dataStream.readInt();
		}
		catch(Exception e)
		{
			System.out.print("SteamPistonDataFail");
			e.printStackTrace();
		}
		
	}
	public void readFromNBT(NBTTagCompound par1NBTTagCompound)
    {	     
    	super.readFromNBT(par1NBTTagCompound);
        this.runTime = par1NBTTagCompound.getShort("time");
        this.steam = par1NBTTagCompound.getInteger("steam");
        this.water = par1NBTTagCompound.getInteger("water");
    }

    /**
     * Writes a tile entity to NBT.
     */
    public void writeToNBT(NBTTagCompound par1NBTTagCompound)
    {
        super.writeToNBT(par1NBTTagCompound);
        par1NBTTagCompound.setShort("time", (short)this.runTime);
        par1NBTTagCompound.setInteger("steam", (int)this.steam);
        par1NBTTagCompound.setInteger("water", (int)this.water);
        
    }
	
}
