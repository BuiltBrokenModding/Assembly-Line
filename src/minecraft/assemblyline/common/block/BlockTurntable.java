package assemblyline.common.block;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import universalelectricity.core.vector.Vector3;
import universalelectricity.prefab.implement.IRotatable;
import assemblyline.common.TabAssemblyLine;

public class BlockTurntable extends BlockALMachine
{
	public BlockTurntable(int par1)
	{
		super(par1, Material.piston);
		this.setUnlocalizedName("turntable");
		this.setCreativeTab(TabAssemblyLine.INSTANCE);
	}

	@Override
	public void updateTick(World world, int x, int y, int z, Random par5Random)
	{
		this.updateTurntableState(world, x, y, z);
	}

	public static int determineOrientation(World world, int x, int y, int z, EntityPlayer entityPlayer)
	{
		if (MathHelper.abs((float) entityPlayer.posX - (float) x) < 2.0F && MathHelper.abs((float) entityPlayer.posZ - (float) z) < 2.0F)
		{
			double var5 = entityPlayer.posY + 1.82D - (double) entityPlayer.yOffset;

			if (var5 - (double) y > 2.0D)
			{
				return 1;
			}

			if ((double) y - var5 > 0.0D)
			{
				return 0;
			}
		}

		int var7 = MathHelper.floor_double((double) (entityPlayer.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;
		return var7 == 0 ? 2 : (var7 == 1 ? 5 : (var7 == 2 ? 3 : (var7 == 3 ? 4 : 0)));
	}

	@Override
	public void onBlockPlacedBy(World world, int x, int y, int z, EntityLiving par5EntityLiving, ItemStack stack)
	{
		int metadata = determineOrientation(world, x, y, z, (EntityPlayer) par5EntityLiving);
		world.setBlockMetadataWithNotify(x, y, z, metadata, 3);

		world.scheduleBlockUpdate(x, y, z, this.blockID, 20);
	}

	@Override
	public void onNeighborBlockChange(World world, int x, int y, int z, int side)
	{
		world.scheduleBlockUpdate(x, y, z, this.blockID, 20);
	}

	private void updateTurntableState(World world, int x, int y, int z)
	{
		if (world.isBlockIndirectlyGettingPowered(x, y, z))
		{
			try
			{
				ForgeDirection direction = ForgeDirection.getOrientation(world.getBlockMetadata(x, y, z));
				Vector3 position = new Vector3(x, y, z);
				position.modifyPositionFromSide(direction);

				IRotatable rotatable = null;
				TileEntity tileEntity = position.getTileEntity(world);
				int blockID = position.getBlockID(world);

				if (tileEntity instanceof IRotatable)
				{
					rotatable = ((IRotatable) tileEntity);
				}
				else if (Block.blocksList[blockID] instanceof IRotatable)
				{
					rotatable = ((IRotatable) Block.blocksList[blockID]);
				}

				if (rotatable != null)
				{
					int newDir = ((IRotatable) tileEntity).getDirection(world, x, y, z).ordinal();
					newDir++;

					while (newDir >= 6)
					{
						newDir -= 6;
					}

					while (newDir < 0)
					{
						newDir += 6;
					}

					rotatable.setDirection(world, x, y, z, ForgeDirection.getOrientation(newDir));

					world.markBlockForUpdate(position.intX(), position.intY(), position.intZ());
					world.playSoundEffect((double) x + 0.5D, (double) y + 0.5D, (double) z + 0.5D, "tile.piston.in", 0.5F, world.rand.nextFloat() * 0.15F + 0.6F);
				}
			}
			catch (Exception e)
			{
				System.out.println("Failed to rotate:");
				e.printStackTrace();
			}
		}
	}
}
