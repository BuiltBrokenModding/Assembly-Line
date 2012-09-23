package aa;

import java.util.ArrayList;
import java.util.Random;

import net.minecraft.src.Block;
import net.minecraft.src.BlockContainer;
import net.minecraft.src.CreativeTabs;
import net.minecraft.src.ItemStack;
import net.minecraft.src.Material;
import net.minecraft.src.TileEntity;
import net.minecraft.src.World;

public class BlockDev extends BlockContainer {

	public BlockDev(int par1) {
		super(par1, Material.rock);
		this.setBlockName("Machine");
		this.setCreativeTab(CreativeTabs.tabBlock);
		//this.setRequiresSelfNotify();
       // this.blockIndexInTexture = 26;
	}
    public boolean isOpaqueCube()
    {
        return false;
    }
    
    public boolean renderAsNormalBlock()
    {
    	//TODO change later when custom models are added
        return true;
    }
    public int getBlockTextureFromSideAndMetadata(int side, int meta)
    {
    	if(meta == 0)
    	{
	    	if(side == 0 || side == 1)
	    	{
	    		return 1;
	    	}
    	}
        return 0;
    }
    /**
     * The type of render function that is called for this block
    */
    public int getRenderType()
    {
        return 0;
    }
	
	/**
     * Returns the ID of the items to drop on destruction.
     */
    public int idDropped(int par1, Random par2Random, int par3)
    {
        return 0;
    }
	public void addCreativeItems(ArrayList itemList)     
	 {       
           
           itemList.add(new ItemStack(this, 1,0));
	 }

	@Override
	public TileEntity createNewTileEntity(World var1,int meta) {
		// TODO Auto-generated method stub
		if(meta == 0)
	    {    
			return new TileEntityAntiMob();
	    }
		return null;
	}

	@Override
	public TileEntity createNewTileEntity(World var1) {
		// TODO Auto-generated method stub
		return null;
	}
	  @Override
	  public String getTextureFile() {
			return "/textures/DevBlocks.png";}

}
