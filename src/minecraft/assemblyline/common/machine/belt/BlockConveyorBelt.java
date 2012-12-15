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
import assemblyline.common.AssemblyLine;

/**
 * The block for the actual conveyor belt!
 * 
 * @author Calclavia, DarkGuardsman
 */
public class BlockConveyorBelt extends BlockMachine
{
	public enum SlantType
	{
		UP, DOWN
	}

	public BlockConveyorBelt(int id)
	{
		super("conveyorBelt", id, UniversalElectricity.machine);
		this.setBlockBounds(0, 0, 0, 1, 0.3f, 1);
		this.setCreativeTab(UETab.INSTANCE);
	}

	/**
	 * Checks the front and the back position to find any conveyor blocks either higher or lower
	 * than this block to determine if it this conveyor block needs to slant.
	 * 
	 * @return Returns of this belt is slanting up or down. Returns null if not slanting.
	 */
	public static SlantType getSlant(World world, Vector3 position)
	{
		TileEntity t = position.getTileEntity(world);

		if (t != null)
		{
			if (t instanceof TileEntityConveyorBelt)
			{
				TileEntityConveyorBelt tileEntity = (TileEntityConveyorBelt) t;
				Vector3 frontCheck = position.clone();
				frontCheck.modifyPositionFromSide(tileEntity.getDirection());
				Vector3 backCheck = position.clone();
				backCheck.modifyPositionFromSide(tileEntity.getDirection().getOpposite());

				if (Vector3.add(frontCheck, new Vector3(0, 1, 0)).getBlockID(world) == AssemblyLine.blockConveyorBelt.blockID && Vector3.add(backCheck, new Vector3(0, -1, 0)).getBlockID(world) == AssemblyLine.blockConveyorBelt.blockID)
				{
					return SlantType.UP;
				}
				else if (Vector3.add(frontCheck, new Vector3(0, -1, 0)).getBlockID(world) == AssemblyLine.blockConveyorBelt.blockID && Vector3.add(backCheck, new Vector3(0, 1, 0)).getBlockID(world) == AssemblyLine.blockConveyorBelt.blockID) { return SlantType.DOWN; }
			}
		}

		return null;
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

		if (tileEntity.running)
		{
			SlantType slantType = this.getSlant(world, new Vector3(x, y, z));
			ForgeDirection direction = tileEntity.getDirection();

			// Move the entity based on the conveyor belt's direction.
			entity.addVelocity(direction.offsetX * tileEntity.speed, 0, direction.offsetZ * tileEntity.speed);

			// Attempt to move entity to the center of the belt to prevent them from flying off.
			if (direction.offsetX != 0)
			{
				double difference = (z + 0.5) - entity.posZ;
				entity.motionZ += difference * 0.005;
			}
			else if (direction.offsetZ != 0)
			{
				double difference = (x + 0.5) - entity.posX;
				entity.motionX += difference * 0.005;
			}

			entity.onGround = false;

			if (slantType == SlantType.UP)
			{
				if (entity.motionY < 0.2)
				{
					entity.addVelocity(0, 0.2, 0);
				}
			}
			else if (slantType == SlantType.DOWN)
			{
				if (entity.motionY > -0.1)
				{
					entity.addVelocity(0, -0.1, 0);
				}
			}
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
