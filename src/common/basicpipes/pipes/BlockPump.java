package basicpipes.pipes;

import java.util.ArrayList;
import net.minecraft.src.*;

import java.util.Random;

public class BlockPump extends BlockContainer
{	
	
	public BlockPump(int id)
	{
		super(id, Material.iron);
		this.setBlockName("Pump");
	}
    
    /**
     * Is this block (a) opaque and (b) a full 1m cube?  This determines whether or not to render the shared face of two
     * adjacent blocks and also whether the player can attach torches, redstone wire, etc to this block.
     */
    public boolean isOpaqueCube()
    {
        return false;
    }
    @Override
   	public int getBlockTexture(IBlockAccess par1iBlockAccess, int x, int y, int z, int side)
       {       	
       	int metadata = par1iBlockAccess.getBlockMetadata(x, y, z);
       	
       	if (side == 1)
           {
       		switch(metadata)
       		{
       			case 0: return 1;
       			case 1: return 3;
       			case 2: return 18;
       			case 3: return 5;
       		}
           } 
           	

               
               switch(metadata)
               {
               case 1: return 4;
               case 2: return 16;
               case 3: return 2;
               }
               return 0;
           
       	
   	}
       @Override
   	public int getBlockTextureFromSideAndMetadata(int side, int metadata)
   	{
       	if (side == 1)
           {
       		switch(metadata)
       		{
       			case 0: return 1;
       			case 1: return 3;
       			case 2: return 18;
       			case 3: return 5;
       		}
           }
           else
           {
           	//If it is the front side
           	if(side == 3)
           	{
           		switch(metadata)
           		{
           			case 0: return 19;
           			case 1: return 6;
           			case 2: return 17;
           			case 3: return 3;
           		}
           	}
           	//If it is the back side
           	else if(side == 2)
           	{
           		switch(metadata)
           		{
           			case 0: return this.blockIndexInTexture + 2;
           			case 1: return this.blockIndexInTexture + 3;
           			case 2: return this.blockIndexInTexture + 2;
           		}
           	}

           	switch(metadata)
               {
               case 1: return 4;
               case 2: return 16;
               case 3: return 2;
               }
           }
   		return 0;
           }
    /**
     * If this block doesn't render as an ordinary block it will return False (examples: signs, buttons, stairs, etc)
     */
    public boolean renderAsNormalBlock()
    {
        return false;
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
	 @Override
		public String getTextureFile() {
			// TODO Auto-generated method stub
			return "/eui/blocks.png";
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
		switch(meta)
	        {    
	        case 0: return new TileEntityPump();
	        }
		return null;
	}

	@Override
	public TileEntity createNewTileEntity(World var1) {
		// TODO Auto-generated method stub
		return null;
	}
 }


