package assemblyline.common.machine.belt;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import universalelectricity.core.UniversalElectricity;
import universalelectricity.prefab.BlockMachine;
import assemblyline.client.render.BlockRenderingHandler;
import assemblyline.common.TabAssemblyLine;
import assemblyline.common.machine.belt.TileEntityConveyorBelt.SlantType;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

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
		this.setCreativeTab(TabAssemblyLine.INSTANCE);
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
				change = 4;
				break;
			case 3:
				change = 5;
				break;
			case 4:
				change = 3;
				break;
			case 5:
				change = 2;
				break;

		}

		world.setBlockMetadataWithNotify(x, y, z, change);

		return true;
	}

	@Override
	public boolean onSneakUseWrench(World world, int x, int y, int z, EntityPlayer par5EntityPlayer, int side, float hitX, float hitY, float hitZ)
	{
		TileEntityConveyorBelt tileEntity = (TileEntityConveyorBelt) world.getBlockTileEntity(x, y, z);

		int slantOrdinal = tileEntity.getSlant().ordinal() + 1;

		if (slantOrdinal >= SlantType.values().length)
		{
			slantOrdinal = 0;
		}

		tileEntity.setSlant(SlantType.values()[slantOrdinal]);

		return true;
	}

	/**
	 * Moves the entity if the conductor is powered.
	 */
	@Override
	public void onEntityCollidedWithBlock(World world, int x, int y, int z, Entity entity)
	{
		TileEntityConveyorBelt tileEntity = (TileEntityConveyorBelt) world.getBlockTileEntity(x, y, z);
		tileEntity.updatePowerTransferRange();

		if (tileEntity.isRunning())
		{
			float acceleration = tileEntity.acceleration;
			float maxSpeed = tileEntity.maxSpeed;

			SlantType slantType = tileEntity.getSlant();
			ForgeDirection direction = tileEntity.getDirection();

			if (entity instanceof EntityLiving)
			{
				acceleration *= 5;
				maxSpeed *= 10;
			}

			// Move the entity based on the conveyor belt's direction.
			entity.addVelocity(direction.offsetX * acceleration, 0, direction.offsetZ * acceleration);

			if (direction.offsetX != 0 && Math.abs(entity.motionX) > maxSpeed)
			{
				entity.motionX = direction.offsetX * maxSpeed;
				entity.motionZ = 0;
			}

			if (direction.offsetZ != 0 && Math.abs(entity.motionZ) > maxSpeed)
			{
				entity.motionZ = direction.offsetZ * maxSpeed;
				entity.motionX = 0;
			}

			if (entity instanceof EntityItem)
			{
				if (direction.offsetX != 0)
				{
					double difference = (z + 0.5) - entity.posZ;
					entity.motionZ += difference * 0.006;
					// entity.posZ = z + 0.5;
				}
				else if (direction.offsetZ != 0)
				{
					double difference = (x + 0.5) - entity.posX;
					entity.motionX += difference * 0.006;
					// /entity.posX = x + 0.5;
				}

				((EntityItem) entity).age++;
				((EntityItem) entity).delayBeforeCanPickup = 2;
				entity.onGround = false;
			}

			if (slantType == SlantType.UP)
			{
				if (entity.motionY < 0.3)
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

	@SideOnly(Side.CLIENT)
	@Override
	public int getRenderType()
	{
		return BlockRenderingHandler.BLOCK_RENDER_ID;
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
