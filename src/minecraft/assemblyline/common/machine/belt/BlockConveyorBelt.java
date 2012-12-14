package assemblyline.common.machine.belt;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import universalelectricity.core.UniversalElectricity;
import universalelectricity.prefab.BlockMachine;
import universalelectricity.prefab.UETab;
import assemblyline.client.render.RenderHelper;

/**
 * The block for the actual conveyor belt!
 * 
 * @author Calclavia, DarkGuardsman
 */
public class BlockConveyorBelt extends BlockMachine
{
	public BlockConveyorBelt(int id)
	{
		super("conveyorBelt", id, UniversalElectricity.machine);
		this.setBlockBounds(0, 0, 0, 1, 0.3f, 1);
		this.setCreativeTab(UETab.INSTANCE);
	}

	@Override
	public void onBlockPlacedBy(World world, int x, int y, int z, EntityLiving par5EntityLiving)
	{
		int meta = world.getBlockMetadata(x, y, z);
		int angle = MathHelper.floor_double((par5EntityLiving.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;
		world.setBlockMetadataWithNotify(x, y, z, meta + angle);
	}

	@Override
	public boolean onUseWrench(World par1World, int x, int y, int z, EntityPlayer par5EntityPlayer, int side, float hitX, float hitY, float hitZ)
	{
		int metadata = par1World.getBlockMetadata(x, y, z);

		if (metadata >= 0 && metadata < 8)
		{
			if (metadata >= 3)
			{
				par1World.setBlockAndMetadataWithNotify(x, y, z, this.blockID, 0);
				return true;
			}
			else if (metadata >= 7)
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
	 * @author AtomicStryker
	 */
	//@Override
	public void onEntityCollidedWithBlockTest(World world, int x, int y, int z, Entity entity)
	{
		TileEntityConveyorBelt tileEntity = (TileEntityConveyorBelt) world.getBlockTileEntity(x, y, z);

		if (tileEntity.running)
		{
			int metadata = tileEntity.getBeltDirection();
			if (metadata == 4)
			{
				entity.setVelocity(0.1D, 0.2D, 0.0D);
				entity.onGround = false;
			}
			else if (metadata == 5)
			{
				entity.setVelocity(0.0D, 0.2D, 0.1D);
				entity.onGround = false;
			}
			else if (metadata == 6)
			{
				entity.setVelocity(-0.1D, 0.2D, 0.0D);
				entity.onGround = false;
			}
			else if (metadata == 7)
			{
				entity.setVelocity(0.0D, 0.2D, -0.1D);
				entity.onGround = false;
			}
			else if (metadata == 8)
			{
				entity.setVelocity(0.1D, 0.0D, 0.0D);
				entity.onGround = false;
			}
			else if (metadata == 9)
			{
				entity.setVelocity(0.0D, 0.0D, 0.1D);
				entity.onGround = false;
			}
			else if (metadata == 10)
			{
				entity.setVelocity(-0.1D, 0.0D, 0.0D);
				entity.onGround = false;
			}
			else if (metadata == 11)
			{
				entity.setVelocity(0.0D, 0.0D, -0.1D);
				entity.onGround = false;
			}
			else if (metadata == 0)
			{
				if (entity.posZ > (double) z + 0.55D)
				{
					entity.setVelocity(0.05D, 0.0D, -0.05D);
				}
				else if (entity.posZ < (double) z + 0.45D)
				{
					entity.setVelocity(0.05D, 0.0D, 0.05D);
				}
				else
				{
					entity.setVelocity(0.1D, 0.0D, 0.0D);
				}
			}
			else if (metadata == 1)
			{
				if (entity.posX > (double) x + 0.55D)
				{
					entity.setVelocity(-0.05D, 0.0D, 0.05D);
				}
				else if (entity.posX < (double) x + 0.45D)
				{
					entity.setVelocity(0.05D, 0.0D, 0.05D);
				}
				else
				{
					entity.setVelocity(0.0D, 0.0D, 0.1D);
				}
			}
			else if (metadata == 2)
			{
				if (entity.posZ > (double) z + 0.55D)
				{
					entity.setVelocity(-0.05D, 0.0D, -0.05D);
				}
				else if (entity.posZ < (double) z + 0.45D)
				{
					entity.setVelocity(-0.05D, 0.0D, 0.05D);
				}
				else
				{
					entity.setVelocity(-0.1D, 0.0D, 0.0D);
				}
			}
			else if (metadata == 3)
			{
				if (entity.posX > (double) x + 0.55D)
				{
					entity.setVelocity(-0.05D, 0.0D, -0.05D);
				}
				else if (entity.posX < (double) x + 0.45D)
				{
					entity.setVelocity(0.05D, 0.0D, -0.05D);
				}
				else
				{
					entity.setVelocity(0.0D, 0.0D, -0.1D);
				}
			}
		}
	}

	/**
	 * Returns the TileEntity used by this block.
	 */
	@Override
	public TileEntity createNewTileEntity(World var1, int metadata)
	{
		if (metadata >= 0 && metadata < 4) { return new TileEntityConveyorBelt(); }
		if (metadata >= 4 && metadata < 8) { return new TileEntityCoveredBelt(); }
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
