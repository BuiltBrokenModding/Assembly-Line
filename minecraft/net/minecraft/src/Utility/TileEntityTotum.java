package net.minecraft.src.Utility;

import net.minecraft.src.*;
import net.minecraft.src.forge.ITextureProvider;
import net.minecraft.src.universalelectricity.UEIRotatable;
import net.minecraft.src.universalelectricity.components.UniversalComponents;

public class TileEntityTotum extends TileEntity implements ITextureProvider,UEIRotatable {
	 private int facing;
	 public void writeToNBT(NBTTagCompound par1NBTTagCompound)
	    {
	        super.writeToNBT(par1NBTTagCompound);
	        par1NBTTagCompound.setInteger("facing", (int)this.facing);
	        
	    }
	 public void readFromNBT(NBTTagCompound par1NBTTagCompound)
	    {
	        super.readFromNBT(par1NBTTagCompound);
	        this.facing = par1NBTTagCompound.getInteger("facing");
	    }
	 public TileEntity getSteamMachine(int i)
		{	
			
				int x = this.xCoord;
		    	int y = this.yCoord;
		    	int z = this.zCoord; 
	    		switch(i){
				case 0: y = y - 1;break;
				case 1: y = y + 1;break;
				case 2: z = z + 1;break;
				case 3: z = z - 1;break;
				case 4: x = x + 1;break;
				case 5: x = x - 1;break;
	    		}
			TileEntity aEntity = worldObj.getBlockTileEntity(x, y, z);			
			if(aEntity instanceof TileEntityTotum)
			{
				return aEntity;
			}
			
			return null;
		}
	public byte getDirection() {
		// TODO Auto-generated method stub
		return (byte) this.facing;
	}
	public void setDirection(byte facingDirection) {		
			this.facing = facingDirection;
	} 	
	@Override
	public String getTextureFile()
	{
		return "/eui/blocks.png";
	}

}
