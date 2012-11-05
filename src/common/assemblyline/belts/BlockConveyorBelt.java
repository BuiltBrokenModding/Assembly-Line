package assemblyline.belts;

import net.minecraft.src.CreativeTabs;
import net.minecraft.src.EntityLiving;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.MathHelper;
import net.minecraft.src.TileEntity;
import net.minecraft.src.World;
import universalelectricity.core.UniversalElectricity;
import universalelectricity.prefab.BlockMachine;
import assemblyline.render.RenderHelper;

/**
 * The block for the actual conveyor belt!
 * 
 * @author Calclavia, DarkGuardsman
 */
public class BlockConveyorBelt extends BlockMachine
{
	public BlockConveyorBelt(int id)
	{
		super("Conveyor Belt", id, UniversalElectricity.machine);
		this.setBlockBounds(0, 0, 0, 1, 0.3f, 1);
		this.setCreativeTab(CreativeTabs.tabTransport);
	}

	@Override
	public void onBlockPlacedBy(World world, int x, int y, int z, EntityLiving par5EntityLiving)
	{
		int meta = world.getBlockMetadata(x, y, z);
		int angle = MathHelper.floor_double((par5EntityLiving.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;
		world.setBlockMetadataWithNotify(x, y, z, meta + angle);
	}

	@Override
	public boolean onUseWrench(World par1World, int x, int y, int z, EntityPlayer par5EntityPlayer)
	{
		int metadata = par1World.getBlockMetadata(x, y, z);

		if (metadata >= 0 && metadata < 8)
		{
			if (metadata >= 3)
			{
				par1World.setBlockAndMetadataWithNotify(x, y, z, this.blockID, 0);
				return true;
			}
			else
			if (metadata >= 7)
			{
				par1World.setBlockAndMetadataWithNotify(x, y, z, this.blockID, 4);
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
	public TileEntity createNewTileEntity(World var1, int metadata)
	{
		if (metadata >= 0 && metadata < 4) { return new TileEntityConveyorBelt(); }
		if (metadata >= 4 && metadata < 8) { return new TileEntityCoveredBelt(); }
		//if (metadata >= 8 && metadata < 12) { //TODO vertical Belt }
		//if (metadata >= 12 && metadata < 16) { //TODO IDK}

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
