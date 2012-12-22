package dark.BasicUtilities.machines;

import java.util.Random;

import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import dark.BasicUtilities.BasicUtilitiesMain;

public class BlockValve extends universalelectricity.prefab.BlockMachine
{	
	
	public BlockValve(int id)
	{
		super("Valve", id, Material.iron);
		this.setBlockName("Valve");
		this.setCreativeTab(CreativeTabs.tabRedstone);
		this.setRequiresSelfNotify();
        this.blockIndexInTexture = 26;
	}
	 public boolean onMachineActivated(World world, int x, int y, int z, EntityPlayer player)
	    {
		 TileEntity te = world.getBlockTileEntity(x, y, z);
		 int meta = world.getBlockMetadata(x, y, z);
		 if(te instanceof TileEntityValve)
		 {
			 TileEntityValve valve = (TileEntityValve) te;
			 if(meta < 8)
			 {
				 if(!valve.on){
				 valve.on = true;
				 }else{
				 valve.on = false;
				 }
			 }
		 }
	        return false;
	    }
	//rotation valve around y axis
   public boolean onUseWrench(World world, int x, int y, int z, EntityPlayer player)
   {
	   int meta = world.getBlockMetadata(x, y, z);
	   if(meta < 4)
	   {
		   if(meta == 3)
		   {
			   world.setBlockMetadataWithNotify(x, y, z, 0);
		   }
		   else
		   {
			   world.setBlockMetadataWithNotify(x, y, z, meta+1); 
		   }
		   return true;
	   }
	   if(meta > 3 && meta < 8)
	   {
		   if(meta == 7)
		   {
			   world.setBlockMetadataWithNotify(x, y, z, 4);
		   }
		   else
		   {
			   world.setBlockMetadataWithNotify(x, y, z, meta+1); 
		   }
		   return true;
	   }
	   if(meta > 7 && meta < 12)
	   {
		   if(meta == 11)
		   {
			   world.setBlockMetadataWithNotify(x, y, z, 8);
		   }
		   else
		   {
			   world.setBlockMetadataWithNotify(x, y, z, meta+1); 
		   }
		   return true;
	   }
	   if(meta > 11 && meta < 16)
	   {
		   if(meta == 15)
		   {
			   world.setBlockMetadataWithNotify(x, y, z, 12);
		   }
		   else
		   {
			   world.setBlockMetadataWithNotify(x, y, z, meta+1); 
		   }
		   return true;
	   }
       return false;
   }
   
   //one shift click inverts pipe to face up
   public boolean onSneakUseWrench(World world, int x, int y, int z, EntityPlayer player)
   {
	   int meta = world.getBlockMetadata(x, y, z);
	   if(meta < 4)
	   {
		   world.setBlockMetadataWithNotify(x, y, z, meta+4);
		   return true;
	   }
	   if(meta > 3 && meta < 8)
	   {
		 world.setBlockMetadataWithNotify(x, y, z, meta-4); 
		   return true;
	   }
	   if(meta > 7 && meta < 12)
	   {
		  
			   world.setBlockMetadataWithNotify(x, y, z, meta+4); 
		   
		   return true;
	   }
	   if(meta > 11 && meta < 16)
	   {
		   
			   world.setBlockMetadataWithNotify(x, y, z, meta-4); 
		   
		   return true;
	   }
   	return false;
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
    public int damageDropped(int meta)
    {
    	if(meta < 8)
    	{
    		return 0;
    	}
    	if(meta < 16 && meta > 7)
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
	public TileEntity createNewTileEntity(World var1) {
		// TODO Auto-generated method stub
		return new TileEntityValve();
	}
	  @Override
	  public String getTextureFile() {
			return BasicUtilitiesMain.ITEM_PNG;
		}
 }


