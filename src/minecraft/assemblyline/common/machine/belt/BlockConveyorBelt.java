package assemblyline.common.machine.belt;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
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
	public void setBlockBoundsBasedOnState(IBlockAccess world, int x, int y, int z)
	{
		TileEntity t = world.getBlockTileEntity(x, y, z);

		if (t != null && t instanceof TileEntityConveyorBelt)
		{
			TileEntityConveyorBelt tileEntity = (TileEntityConveyorBelt) t;

			if (tileEntity.getSlant() == SlantType.UP || tileEntity.getSlant() == SlantType.DOWN)
			{
				this.setBlockBounds(0f, 0f, 0f, 1f, 0.98f, 1f);
				return;
			}
			if (tileEntity.getSlant() == SlantType.TOP)
			{
				this.setBlockBounds(0f, 0.68f, 0f, 1f, 0.98f, 1f);
				return;
			}
		}

		this.setBlockBounds(0f, 0f, 0f, 1f, 0.3f, 1f);
	}

	@Override
	public AxisAlignedBB getSelectedBoundingBoxFromPool(World world, int x, int y, int z)
	{
		TileEntity t = world.getBlockTileEntity(x, y, z);

		if (t != null && t instanceof TileEntityConveyorBelt)
		{
			TileEntityConveyorBelt tileEntity = (TileEntityConveyorBelt) t;

			if (tileEntity.getSlant() == SlantType.UP || tileEntity.getSlant() == SlantType.DOWN) { return AxisAlignedBB.getAABBPool().addOrModifyAABBInPool((double) x + this.minX, (double) y + this.minY, (double) z + this.minZ, (double) x + 1, (double) y + 1, (double) z + 1); }
			if (tileEntity.getSlant() == SlantType.TOP) { return AxisAlignedBB.getAABBPool().addOrModifyAABBInPool((double) x + this.minX, (double) y + 0.68f, (double) z + this.minZ, (double) x + this.maxX, (double) y + 0.98f, (double) z + this.maxZ); }
		}

		return AxisAlignedBB.getAABBPool().addOrModifyAABBInPool((double) x + this.minX, (double) y + this.minY, (double) z + this.minZ, (double) x + this.maxX, (double) y + this.maxY, (double) z + this.maxZ);
	}

	@Override
	public void addCollidingBlockToList(World world, int x, int y, int z, AxisAlignedBB par5AxisAlignedBB, List par6List, Entity par7Entity)
	{
		TileEntity t = world.getBlockTileEntity(x, y, z);

		if (t != null && t instanceof TileEntityConveyorBelt)
		{
			TileEntityConveyorBelt tileEntity = (TileEntityConveyorBelt) t;

			if (tileEntity.getSlant() == SlantType.UP || tileEntity.getSlant() == SlantType.DOWN)
			{
				AxisAlignedBB boundBottom = AxisAlignedBB.getAABBPool().addOrModifyAABBInPool(x, y, z, x + 1, y + 0.3, z + 1);
				AxisAlignedBB boundTop = null;

				ForgeDirection direction = tileEntity.getDirection();

				if (tileEntity.getSlant() == SlantType.UP)
				{
					if (direction.offsetX > 0)
					{
						boundTop = AxisAlignedBB.getAABBPool().addOrModifyAABBInPool(x + (float) direction.offsetX / 2, y, z, x + 1, y + 0.8, z + 1);
					}
					else if (direction.offsetX < 0)
					{
						boundTop = AxisAlignedBB.getAABBPool().addOrModifyAABBInPool(x, y, z, x + (float) direction.offsetX / -2, y + 0.8, z + 1);
					}
					else if (direction.offsetZ > 0)
					{
						boundTop = AxisAlignedBB.getAABBPool().addOrModifyAABBInPool(x, y, z + (float) direction.offsetZ / 2, x + 1, y + 0.8, z + 1);
					}
					else if (direction.offsetZ < 0)
					{
						boundTop = AxisAlignedBB.getAABBPool().addOrModifyAABBInPool(x, y, z, x + 1, y + 0.8, z + (float) direction.offsetZ / -2);
					}
				}
				else if (tileEntity.getSlant() == SlantType.DOWN)
				{
					if (direction.offsetX > 0)
					{
						boundTop = AxisAlignedBB.getAABBPool().addOrModifyAABBInPool(x, y, z, x + (float) direction.offsetX / 2, y + 0.8, z + 1);
					}
					else if (direction.offsetX < 0)
					{
						boundTop = AxisAlignedBB.getAABBPool().addOrModifyAABBInPool(x + (float) direction.offsetX / -2, y, z, x + 1, y + 0.8, z + 1);
					}
					else if (direction.offsetZ > 0)
					{
						boundTop = AxisAlignedBB.getAABBPool().addOrModifyAABBInPool(x, y, z, x + 1, y + 0.8, z + (float) direction.offsetZ / 2);
					}
					else if (direction.offsetZ < 0)
					{
						boundTop = AxisAlignedBB.getAABBPool().addOrModifyAABBInPool(x, y, z + (float) direction.offsetZ / -2, x + 1, y + 0.8, z + 1);
					}
				}

				if (par5AxisAlignedBB.intersectsWith(boundBottom))
				{
					par6List.add(boundBottom);
				}
				if (boundTop != null && par5AxisAlignedBB.intersectsWith(boundTop))
				{
					par6List.add(boundTop);
				}

				return;
			}

			if (tileEntity.getSlant() == SlantType.TOP)
			{
				AxisAlignedBB newBounds = AxisAlignedBB.getAABBPool().addOrModifyAABBInPool(x, y + 0.68, z, x + 1, y + 0.98, z + 1);

				if (newBounds != null && par5AxisAlignedBB.intersectsWith(newBounds))
				{
					par6List.add(newBounds);
				}

				return;
			}
		}

		AxisAlignedBB newBounds = AxisAlignedBB.getAABBPool().addOrModifyAABBInPool(x, y, z, x + 1, y + 0.3, z + 1);

		if (newBounds != null && par5AxisAlignedBB.intersectsWith(newBounds))
		{
			par6List.add(newBounds);
		}
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

			entity.motionY += 0.0125f;

			if (entity instanceof EntityItem)
			{
				if (direction.offsetX != 0)
				{
					double difference = (z + 0.5) - entity.posZ;
					entity.motionZ += difference * 0.1;
					// entity.posZ = z + 0.5;
				}
				else if (direction.offsetZ != 0)
				{
					double difference = (x + 0.5) - entity.posX;
					entity.motionX += difference * 0.1;
					// /entity.posX = x + 0.5;
				}

				((EntityItem) entity).age++;

				boolean foundSneaking = false;
				for (EntityPlayer player : (List<EntityPlayer>) world.getEntitiesWithinAABB(EntityPlayer.class, AxisAlignedBB.getBoundingBox(x - 1, y - 1, z - 1, x + 1, y + 1, z + 1)))
				{
					if (player.isSneaking())
						foundSneaking = true;
				}

				if (foundSneaking)
					((EntityItem) entity).delayBeforeCanPickup = 0;
				else
					((EntityItem) entity).delayBeforeCanPickup = 20;
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

	@Override
	public int damageDropped(int par1)
	{
		return 0;
	}
}
