package net.minecraft.src.eui;

import net.minecraft.src.*;
import net.minecraft.src.eui.boiler.TileEntityBoiler;
import net.minecraft.src.forge.ITextureProvider;
import net.minecraft.src.universalelectricity.electricity.TileEntityElectricUnit;
import net.minecraft.src.universalelectricity.extend.IRotatable;

public class TileEntityMachine extends TileEntityElectricUnit implements ITextureProvider, IRotatable
{
	private byte facing = 0;
	 
	public TileEntity getSteamMachine(int i)
	{	
			int x = this.xCoord;
	    	int y = this.yCoord;
	    	int z = this.zCoord; 
	    	
    		switch(i)
    		{
			case 0: y = y - 1;break;
			case 1: y = y + 1;break;
			case 2: z = z + 1;break;
			case 3: z = z - 1;break;
			case 4: x = x + 1;break;
			case 5: x = x - 1;break;
    		}
		TileEntity aEntity = worldObj.getBlockTileEntity(x, y, z);			
		if(aEntity instanceof TileEntityMachine)
		{
			return aEntity;
		}
		
		return null;
	}
		
	public byte getDirection()
	{
		return this.facing;
	}
	
	public void setDirection(byte facingDirection)
	{		
		this.facing = facingDirection;
	}
	
	@Override
	public String getTextureFile()
	{
		return "/eui/blocks.png";
	}
	
	@Override
	public void writeToNBT(NBTTagCompound par1NBTTagCompound)
	{
	        super.writeToNBT(par1NBTTagCompound);
	        par1NBTTagCompound.setByte("facing", (byte)this.facing);
	}
	
	@Override
	public void readFromNBT(NBTTagCompound par1NBTTagCompound)
	{
	        super.readFromNBT(par1NBTTagCompound);
	        this.facing = par1NBTTagCompound.getByte("facing");
	}

	@Override
	public float electricityRequest() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean canReceiveFromSide(byte side) {
		// TODO Auto-generated method stub
		return false;
	}
	

	public int getSizeInventory() {
		// TODO Auto-generated method stub
		return 0;
	}

	public ItemStack getStackInSlot(int var6) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
    public boolean canUpdate()
    {
        return true;
    }
}