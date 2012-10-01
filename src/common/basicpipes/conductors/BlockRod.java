package basicpipes.conductors;

import steampower.TileEntityMachine;
import net.minecraft.src.Block;
import net.minecraft.src.CreativeTabs;
import net.minecraft.src.EntityLiving;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.Material;
import net.minecraft.src.MathHelper;
import net.minecraft.src.TileEntity;
import net.minecraft.src.World;
import net.minecraftforge.common.ForgeDirection;

public class BlockRod extends universalelectricity.prefab.BlockMachine {

	public BlockRod(int par1) {
		super("MechanicRod", par1, Material.iron);
		this.setCreativeTab(CreativeTabs.tabRedstone);
	}
	@Override
	protected int damageDropped(int metadata)
	{
		return 0;
	}
	@Override
	public void onBlockPlacedBy(World world,int i,int j,int k, EntityLiving player)
	{
		int angle= MathHelper.floor_double((player.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;
		int meta = 0;
		ForgeDirection idr;
		switch(angle)
		{
			case 0: meta = 2;break;
			case 1: meta = 5;break;
			case 2: meta = 3;break;
			case 3: meta = 4;break;
		}
         world.setBlockAndMetadataWithUpdate(i, j, k,blockID, meta, true);
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
