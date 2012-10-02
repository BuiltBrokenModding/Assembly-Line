package steampower.turbine;

import java.util.Random;

import net.minecraft.src.EntityLiving;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.Material;
import net.minecraft.src.MathHelper;
import net.minecraft.src.TileEntity;
import net.minecraft.src.World;
import steampower.SteamPowerMain;
import steampower.TileEntityMachine;

public class BlockSteamPiston extends universalelectricity.prefab.BlockMachine{

	public BlockSteamPiston(int par1) {
		super("SteamEngine", par1, Material.iron);
		
	}
	@Override
	public boolean onMachineActivated(World par1World, int x, int y, int z, EntityPlayer par5EntityPlayer)
    {
		if (par1World.isRemote)
        {
            return true;
        }
        else
        {
            TileEntity blockEntity = (TileEntity)par1World.getBlockTileEntity(x, y, z);

            if (blockEntity != null)
            {
            	
            	if(blockEntity instanceof TileEntitySteamPiston)
            	{
            	par5EntityPlayer.openGui(SteamPowerMain.instance, 2, par1World, x, y, z);
            	}
            	if(blockEntity instanceof TileEntitytopGen)
            	{
            	par5EntityPlayer.openGui(SteamPowerMain.instance, 2, par1World, x, y-1, z);
            	}
            }
            return true;
        }
    }
	@Override
	 public boolean onUseWrench(World par1World, int x, int y, int z, EntityPlayer par5EntityPlayer)
	    {
		 int angle = MathHelper.floor_double((par5EntityPlayer.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;
	        int metadata = par1World.getBlockMetadata(x, y, z);
	        if(metadata < 3)
	        {
	        	par1World.setBlockAndMetadata(x, y, z, blockID, metadata+angle);
	        }
	        else
	        {
	        	par1World.setBlockAndMetadata(x, y, z, blockID, 0);
	        }
		        return true;
	    }
	  public TileEntity createNewTileEntity(World var1)
	    {
		  return null;
	    }
	  public void breakBlock(World world, int x, int y, int z,int par5, int par6)
	  {
	  super.breakBlock(world, x, y, z, par5, par6);
	  int meta = world.getBlockMetadata(x, y, z);
	  if(meta < 4)
	  {
		  if(world.getBlockId(x, y+1, z) == this.blockID)
		  {
			  if(world.getBlockMetadata(x, y, z)> 4)
			  {
				  world.setBlockAndMetadataWithUpdate(x, y, z, 0, 0, true);
			  }
		  }
	  }
	  else
		  if(meta > 4)
		  {
			  if(world.getBlockId(x, y-1, z) == this.blockID)
			  {
				  if(world.getBlockMetadata(x, y, z)< 4)
				  {
					  world.setBlockAndMetadataWithUpdate(x, y, z, 0, 0, true);
				  }
			  }
		  }
	  }
	  @Override
	    public boolean isOpaqueCube()
	    {
	        return false;
	    }
		@Override
		public boolean renderAsNormalBlock()
		{
		    return false;
		}
		@Override
		public int getRenderType()
		{
		   return -1;
		}
		@Override
		public TileEntity createNewTileEntity(World world, int metadata)
	    {
			if(metadata >= 0 && metadata < 4)
			{	
				return new TileEntitySteamPiston();
			}
			if(metadata == 14)
			{
				return new TileEntitytopGen();
			}
			return null;
		}
		 public void onNeighborBlockChange(World par1World, int par2, int par3, int par4, int par5)
		    {
		        int meta = par1World.getBlockMetadata(par2, par3, par4);
		        if (meta < 4)
		        {
		            if (par1World.getBlockId(par2, par3 + 1, par4) != this.blockID)
		            {
		                par1World.setBlockWithNotify(par2, par3, par4, 0);
		            }
		        }
		        else
		        {
		            if (par1World.getBlockId(par2, par3 - 1, par4) != this.blockID)
		            {
		                par1World.setBlockWithNotify(par2, par3, par4, 0);
		            }
		        } 
		    }
		 @Override
		 public int idDropped(int par1, Random par2Random, int par3)
		    {
		        return SteamPowerMain.itemEngine.shiftedIndex;
		    }
		@Override
		public boolean canPlaceBlockAt(World par1World, int par2, int par3, int par4)
	    {
	        int var5 = par1World.getBlockId(par2, par3, par4);
	        int var6 = par1World.getBlockId(par2, par3+1, par4);
	        return (var5 == 0 || blocksList[var5].blockMaterial.isGroundCover()) && (var6 == 0 || blocksList[var6].blockMaterial.isGroundCover());
	    }
}
