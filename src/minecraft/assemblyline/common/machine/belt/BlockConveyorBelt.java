package assemblyline.common.machine.belt;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import universalelectricity.core.UniversalElectricity;
import universalelectricity.core.vector.Vector3;
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

	/**
	 * Is this conveyor belt slanted towards a direction?
	 * 
	 * @return The ForgeDirection in which this conveyor belt is slanting against. The direction
	 * given is the high point of the slant. Return Unknown if not slanting.
	 */
	public ForgeDirection getSlant(World world, Vector3 position)
	{
		TileEntity t = position.getTileEntity(world);

		if (t != null)
		{
			if (t instanceof TileEntityConveyorBelt)
			{
				TileEntityConveyorBelt tileEntity = (TileEntityConveyorBelt) t;
				Vector3 highCheck = position.clone();
				highCheck.modifyPositionFromSide(tileEntity.getDirection());
			}
		}

		return ForgeDirection.UNKNOWN;
	}

	@Override
	public void onBlockPlacedBy(World world, int x, int y, int z, EntityLiving par5EntityLiving)
	{
		int angle = MathHelper.floor_double((par5EntityLiving.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;
		int change = 2;

		switch (angle)
		{
			case 0:
				change = 3;
				break;
			case 1:
				change = 4;
				break;
			case 2:
				change = 2;
				break;
			case 3:
				change = 5;
				break;

		}
		world.setBlockMetadataWithNotify(x, y, z, change);
	}

	@Override
	public boolean onUseWrench(World world, int x, int y, int z, EntityPlayer par5EntityPlayer, int side, float hitX, float hitY, float hitZ)
	{
		int original = world.getBlockMetadata(x, y, z);
		int change = 2;

		switch (original)
		{
			case 2:
				change = 5;
				break;
			case 5:
				change = 4;
				break;
			case 4:
				change = 3;
				break;
			case 3:
				change = 2;
				break;
		}

		world.setBlockMetadataWithNotify(x, y, z, change);

		return true;
	}

	/**
	 * Function WIP.
	 * 
	 * @author AtomicStryker
	 */
	@Override
	public void onEntityCollidedWithBlock(World world, int x, int y, int z, Entity entity)
	{
		TileEntityConveyorBelt tileEntity = (TileEntityConveyorBelt) world.getBlockTileEntity(x, y, z);

		// if (tileEntity.running)
		{
			ForgeDirection direction = tileEntity.getDirection();

			entity.addVelocity(direction.offsetX * tileEntity.speed, direction.offsetY * tileEntity.speed, direction.offsetZ * tileEntity.speed);
			entity.onGround = false;
			/*
			 * else if (metadata == 9) { entity.setVelocity(0.0D, 0.0D, 0.1D); entity.onGround =
			 * false; } else if (metadata == 10) { entity.setVelocity(-0.1D, 0.0D, 0.0D);
			 * entity.onGround = false; } else if (metadata == 11) { entity.setVelocity(0.0D, 0.0D,
			 * -0.1D); entity.onGround = false; }
			 */

			/*
			 * if (metadata == 4) { entity.setVelocity(0.1D, 0.2D, 0.0D); entity.onGround = false; }
			 * else if (metadata == 5) { entity.setVelocity(0.0D, 0.2D, 0.1D); entity.onGround =
			 * false; } else if (metadata == 6) { entity.setVelocity(-0.1D, 0.2D, 0.0D);
			 * entity.onGround = false; } else if (metadata == 7) { entity.setVelocity(0.0D, 0.2D,
			 * -0.1D); entity.onGround = false; } else if (metadata == 8) { entity.setVelocity(0.1D,
			 * 0.0D, 0.0D); entity.onGround = false; } else if (metadata == 9) {
			 * entity.setVelocity(0.0D, 0.0D, 0.1D); entity.onGround = false; } else if (metadata ==
			 * 10) { entity.setVelocity(-0.1D, 0.0D, 0.0D); entity.onGround = false; } else if
			 * (metadata == 11) { entity.setVelocity(0.0D, 0.0D, -0.1D); entity.onGround = false; }
			 * else if (metadata == 0) { if (entity.posZ > (double) z + 0.55D) {
			 * entity.setVelocity(0.05D, 0.0D, -0.05D); } else if (entity.posZ < (double) z + 0.45D)
			 * { entity.setVelocity(0.05D, 0.0D, 0.05D); } else { entity.setVelocity(0.1D, 0.0D,
			 * 0.0D); } } else if (metadata == 1) { if (entity.posX > (double) x + 0.55D) {
			 * entity.setVelocity(-0.05D, 0.0D, 0.05D); } else if (entity.posX < (double) x + 0.45D)
			 * { entity.setVelocity(0.05D, 0.0D, 0.05D); } else { entity.setVelocity(0.0D, 0.0D,
			 * 0.1D); } } else if (metadata == 2) { if (entity.posZ > (double) z + 0.55D) {
			 * entity.setVelocity(-0.05D, 0.0D, -0.05D); } else if (entity.posZ < (double) z +
			 * 0.45D) { entity.setVelocity(-0.05D, 0.0D, 0.05D); } else { entity.setVelocity(-0.1D,
			 * 0.0D, 0.0D); } } else if (metadata == 3) { if (entity.posX > (double) x + 0.55D) {
			 * entity.setVelocity(-0.05D, 0.0D, -0.05D); } else if (entity.posX < (double) x +
			 * 0.45D) { entity.setVelocity(0.05D, 0.0D, -0.05D); } else { entity.setVelocity(0.0D,
			 * 0.0D, -0.1D); } }
			 */
		}
	}

	/**
	 * Returns the TileEntity used by this block.
	 */
	@Override
	public TileEntity createNewTileEntity(World var1)
	{
		return new TileEntityConveyorBelt();
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
