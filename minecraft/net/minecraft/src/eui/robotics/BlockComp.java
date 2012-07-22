package net.minecraft.src.eui.robotics;

import java.util.List;

import net.minecraft.src.Block;
import net.minecraft.src.IBlockAccess;
import net.minecraft.src.Material;
import net.minecraft.src.TileEntity;
import net.minecraft.src.eui.TileEntityNuller;
import net.minecraft.src.eui.boiler.TileEntityBoiler;
import net.minecraft.src.eui.burner.TileEntityFireBox;
import net.minecraft.src.eui.turbine.TileEntityGenerator;
import net.minecraft.src.universalelectricity.Vector3;

public class BlockComp extends net.minecraft.src.universalelectricity.extend.BlockMachine {

	public BlockComp(int par1) {
		super("RobotMachine", par1, Material.iron);
		// TODO Auto-generated constructor stub
	}
	public int getBlockTexture(IBlockAccess par1iBlockAccess, int x, int y, int z, int side)
    {
		return 0;
    }
	public int getBlockTextureFromSideAndMetadata(int side, int metadata)
	{
		return 0;
	}
	@Override
	public TileEntity getBlockEntity() {
		// TODO Auto-generated method stub
		return null;
	}
	 @Override
	    public TileEntity getBlockEntity(int meta)
	    {
	        switch(meta)
	        {  
	        case 0: return new TileEntityComp();
	        case 1: return new TileEntityComp();
	        case 2: return new TileEntityComp();
	        case 3: return new TileEntityComp();
	        }
			return null;
	    }
	@Override
	public String getTextureFile() {
		// TODO Auto-generated method stub
		return "/eui/blocks.png";
	}
	    @Override
    public boolean isOpaqueCube()
    {
        return false;
    }
	public int getRenderType()
	{
	   return 0;
	}

}
