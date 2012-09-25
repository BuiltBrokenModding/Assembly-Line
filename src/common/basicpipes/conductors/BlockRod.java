package basicpipes.conductors;

import net.minecraft.src.Block;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.Material;
import net.minecraft.src.TileEntity;
import net.minecraft.src.World;

public class BlockRod extends universalelectricity.prefab.BlockMachine {

	public BlockRod(int par1) {
		super("MechanicRod", par1, Material.iron);
	}
	@Override
	protected int damageDropped(int metadata)
	{
		return 0;
	}
	@Override
	public boolean onUseWrench(World world, int x, int y, int z, EntityPlayer par5EntityPlayer)
	{
	   int meta = world.getBlockMetadata(x, y, z);
	   if(meta >= 5)
	   {
		   world.setBlockMetadataWithNotify(x, y, z, 0);
	   }
	   else
	   {
		   world.setBlockMetadataWithNotify(x,y,z,meta+1);
	   }
	   return true;
	}
	@Override
	public TileEntity createNewTileEntity(World var1)
	{
	   return new TileEntityRod();
	}
	 public boolean isOpaqueCube()
	    {
	        return false;
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
	        return -1;
	    }

}
