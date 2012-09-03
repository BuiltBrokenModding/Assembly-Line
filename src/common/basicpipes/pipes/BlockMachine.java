package basicpipes.pipes;

import java.util.ArrayList;
import net.minecraft.src.*;

import java.util.Random;

public class BlockMachine extends BlockContainer
{	
	
	public BlockMachine(int id)
	{
		super(id, Material.iron);
		this.setBlockName("Machine");
		this.setCreativeTab(CreativeTabs.tabBlock);
		this.setRequiresSelfNotify();
        this.blockIndexInTexture = 26;
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
    
    /**
     * The type of render function that is called for this block
    */
    public int getRenderType()
    {
        return -1;
    }
	
	/**
     * Returns the ID of the items to drop on destruction.
     */
    public int idDropped(int par1, Random par2Random, int par3)
    {
        return 0;
    }
	//Per tick
	public int conductorCapacity()
	{
		return 10;
	}	
	public void addCreativeItems(ArrayList itemList)     
	 {       
           
           itemList.add(new ItemStack(this, 1,0));
	 }

	@Override
	public TileEntity createNewTileEntity(World var1,int meta) {
		// TODO Auto-generated method stub
		if(meta < 4)
	    {    
			return new TileEntityPump();
	    }
		if(meta > 3 && meta < 8)
	    {    
			return new TileEntityCondenser();
	    }
		return null;
	}

	@Override
	public TileEntity createNewTileEntity(World var1) {
		// TODO Auto-generated method stub
		return null;
	}
	  @Override
	    public String getTextureFile()
	    {
	        return basicpipes.BasicPipesMain.textureFile + "/items.png";
	    }
 }


