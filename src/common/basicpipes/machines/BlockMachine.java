package basicpipes.machines;

import java.util.Random;

import net.minecraft.src.BlockContainer;
import net.minecraft.src.CreativeTabs;
import net.minecraft.src.Material;
import net.minecraft.src.TileEntity;
import net.minecraft.src.World;
import basicpipes.BasicPipesMain;

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
        return false;
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
        return this.blockID;
    }
    protected int damageDropped(int meta)
    {
    	if(meta < 4)
    	{
    		return 0;
    	}
    	if(meta > 3 && meta < 8)
    	{
    		return 4;
    	}
        return 0;
    }
	//Per tick
	public int conductorCapacity()
	{
		return 1;
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
		if(meta > 7 && meta < 12)
	    {    
			return new TileEntityValve();
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
			return BasicPipesMain.textureFile+"/Items.png";
		}
 }


