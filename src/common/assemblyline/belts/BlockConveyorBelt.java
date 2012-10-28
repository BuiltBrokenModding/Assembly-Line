package assemblyline.belts;

import net.minecraft.src.CreativeTabs;
import net.minecraft.src.EntityLiving;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.Material;
import net.minecraft.src.MathHelper;
import net.minecraft.src.TileEntity;
import net.minecraft.src.World;
import universalelectricity.prefab.BlockMachine;
import assemblyline.render.RenderHelper;

/**
 * The block for the actual conveyor belt!
 * @author Calclavia, DarkGuardsman
 */
public class BlockConveyorBelt extends BlockMachine
{
	public BlockConveyorBelt(int id)
	{
		super("Conveyor Belt", id, Material.wood);
		this.setBlockBounds(0, 0, 0, 1, 0.3f, 1);
		this.setTextureFile("/textures/items.png");
		this.blockIndexInTexture = 0;
		this.setCreativeTab(CreativeTabs.tabTransport);
	}
	@Override
	public void onBlockPlacedBy(World par1World, int x, int y, int z, EntityLiving par5EntityLiving)
	{
		int angle = MathHelper.floor_double((par5EntityLiving.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;
		par1World.setBlockAndMetadataWithNotify(x, y, z, this.blockID, angle);
	}
	
	@Override
	public boolean onUseWrench(World par1World, int x, int y, int z, EntityPlayer par5EntityPlayer)
	{
		int metadata = par1World.getBlockMetadata(x, y, z);
		
		if (metadata >= 0 && metadata < 4)
		{
			if (metadata == 3)
			{
				par1World.setBlockAndMetadataWithNotify(x, y, z, this.blockID, 0);
				return true;
			}
			else
			{
				par1World.setBlockAndMetadataWithNotify(x, y, z, this.blockID, metadata + 1); 
				return true;
			}
		}
		
		return true;
	}
	
	/**
	 * Returns the TileEntity used by this block.
	 */
	@Override
	public TileEntity createNewTileEntity(World var1,int meta)
    {
		if(meta >=0 && meta < 4)
		{
		return new TileEntityConveyorBelt();
		}
		return null;
	}
    
	@Override
	public int getRenderType()
	{
		return RenderHelper.BLOCK_RENDER_ID;
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
	
}
