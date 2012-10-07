package basicpipes.machines;

import java.util.Random;

import net.minecraft.src.BlockContainer;
import net.minecraft.src.CreativeTabs;
import net.minecraft.src.Material;
import net.minecraft.src.TileEntity;
import net.minecraft.src.World;
import basicpipes.BasicPipesMain;
import basicpipes.ItemRenderHelper;

public class BlockMachine extends BlockContainer
{	
	
	public BlockMachine(int id)
	{
		super(id, Material.iron);
		this.setBlockName("Machine");
		this.setCreativeTab(CreativeTabs.tabBlock);
		this.setRequiresSelfNotify();
        this.blockIndexInTexture = 26;
        this.setHardness(1f);
		this.setResistance(3f);
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
        return  ItemRenderHelper.renderID;
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


