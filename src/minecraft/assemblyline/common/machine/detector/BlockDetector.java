package assemblyline.common.machine.detector;

import java.util.Random;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import universalelectricity.core.UniversalElectricity;
import universalelectricity.prefab.BlockMachine;
import universalelectricity.prefab.UETab;
import assemblyline.client.ClientProxy;
import assemblyline.client.render.BlockRenderingHandler;
import assemblyline.common.AssemblyLine;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * @author Briman0094
 */
public class BlockDetector extends BlockMachine
{
	private Random random = new Random();

	public BlockDetector(int blockID, int texture)
	{
		super("detector", blockID, UniversalElectricity.machine, UETab.INSTANCE);
		this.blockIndexInTexture = texture;
	}

	@Override
	public int getBlockTexture(IBlockAccess iBlockAccess, int x, int y, int z, int side)
	{
		TileEntity tileEntity = iBlockAccess.getBlockTileEntity(x, y, z);
		if (tileEntity instanceof TileEntityDetector)
		{
			if (side == ForgeDirection.DOWN.ordinal())
			{
				if (((TileEntityDetector) tileEntity).isInverted())
				{
					return this.blockIndexInTexture + 2;

				}
				else
				{
					return this.blockIndexInTexture + 1;
				}
			}
		}

		return this.blockIndexInTexture;
	}

	@Override
	public int getBlockTextureFromSideAndMetadata(int side, int metadata)
	{
		if (side == ForgeDirection.DOWN.ordinal()) { return this.blockIndexInTexture + 1; }

		return this.blockIndexInTexture;
	}

	@Override
	public boolean onMachineActivated(World world, int x, int y, int z, EntityPlayer player, int par6, float par7, float par8, float par9)
	{
		TileEntity tileEntity = world.getBlockTileEntity(x, y, z);

		if (tileEntity != null)
		{
			if (tileEntity instanceof TileEntityDetector)
			{
				player.openGui(AssemblyLine.instance, ClientProxy.GUI_DETECTOR, world, x, y, z);
				return true;
			}
		}

		return false;
	}

	@Override
	public boolean onSneakUseWrench(World world, int x, int y, int z, EntityPlayer par5EntityPlayer, int side, float hitX, float hitY, float hitZ)
	{
		TileEntity tileEntity = world.getBlockTileEntity(x, y, z);

		if (tileEntity != null)
		{
			if (tileEntity instanceof TileEntityDetector)
			{
				((TileEntityDetector) tileEntity).toggleInversion();
			}
		}

		return true;
	}

	@Override
	public void breakBlock(World world, int x, int y, int z, int int1, int int2)
	{
		TileEntityDetector te = (TileEntityDetector) world.getBlockTileEntity(x, y, z);

		if (te != null)
		{
			for (int i = 0; i < te.getSizeInventory(); ++i)
			{
				ItemStack stack = te.getStackInSlot(i);

				if (stack != null)
				{
					float xShift = this.random.nextFloat() * 0.8F + 0.1F;
					float yShift = this.random.nextFloat() * 0.8F + 0.1F;
					EntityItem eI;

					for (float zShift = this.random.nextFloat() * 0.8F + 0.1F; stack.stackSize > 0; world.spawnEntityInWorld(eI))
					{
						int count = this.random.nextInt(21) + 10;

						if (count > stack.stackSize)
						{
							count = stack.stackSize;
						}

						stack.stackSize -= count;
						eI = new EntityItem(world, (double) ((float) x + xShift), (double) ((float) y + yShift), (double) ((float) z + zShift), new ItemStack(stack.itemID, count, stack.getItemDamage()));
						float var15 = 0.05F;
						eI.motionX = (double) ((float) this.random.nextGaussian() * var15);
						eI.motionY = (double) ((float) this.random.nextGaussian() * var15 + 0.2F);
						eI.motionZ = (double) ((float) this.random.nextGaussian() * var15);

						if (stack.hasTagCompound())
						{
							eI.func_92014_d().setTagCompound((NBTTagCompound) stack.getTagCompound().copy());
						}
					}
				}
			}
		}

		super.breakBlock(world, x, y, z, int1, int2);
	}

	@Override
	public void onNeighborBlockChange(World world, int x, int y, int z, int blockID)
	{
		if (!canBlockStay(world, x, y, z))
		{
			this.dropBlockAsItem(world, x, y, z, world.getBlockMetadata(x, y, z), 0);
			world.setBlockWithNotify(x, y, z, 0);
		}
	}

	@Override
	public boolean isOpaqueCube()
	{
		return false;
	}

	/*
	 * @Override public boolean renderAsNormalBlock() { return false; }
	 * 
	 * @SideOnly(Side.CLIENT)
	 * 
	 * @Override public int getRenderType() { return BlockRenderingHandler.BLOCK_RENDER_ID; }
	 */

	@Override
	public boolean isProvidingStrongPower(IBlockAccess world, int x, int y, int z, int direction)
	{
		if (direction == ForgeDirection.DOWN.ordinal())
		{
			TileEntity tileEntity = world.getBlockTileEntity(x, y, z);
			if (tileEntity != null)
			{
				if (tileEntity instanceof TileEntityDetector) { return ((TileEntityDetector) tileEntity).isPoweringTo(ForgeDirection.getOrientation(direction)); }
			}
		}

		return false;
	}

	@Override
	public boolean isProvidingWeakPower(IBlockAccess world, int x, int y, int z, int direction)
	{
		if (direction != ForgeDirection.DOWN.ordinal() && direction != ForgeDirection.UP.ordinal())
		{
			TileEntity tileEntity = world.getBlockTileEntity(x, y, z);

			if (tileEntity != null)
			{
				if (tileEntity instanceof TileEntityDetector) { return ((TileEntityDetector) tileEntity).isPoweringTo(ForgeDirection.getOrientation(direction)); }
			}
		}

		return false;
	}

	@Override
	public boolean canBlockStay(World world, int x, int y, int z)
	{
		if (world.isBlockSolidOnSide(x, y + 1, z, ForgeDirection.DOWN))
			return true;

		return false;
	}

	@Override
	public boolean canPlaceBlockAt(World world, int x, int y, int z)
	{
		return canBlockStay(world, x, y, z);
	}

	@Override
	public TileEntity createNewTileEntity(World world)
	{
		return new TileEntityDetector();
	}

}
