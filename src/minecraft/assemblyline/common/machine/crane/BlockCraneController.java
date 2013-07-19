package assemblyline.common.machine.crane;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import universalelectricity.core.UniversalElectricity;
import assemblyline.client.render.BlockRenderingHandler;
import assemblyline.common.machine.BlockAssembly;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockCraneController extends BlockAssembly
{
	public BlockCraneController(int id)
	{
		super(id, UniversalElectricity.machine, "craneController");
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
	public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase entity, ItemStack stack)
	{
		int rot = (int) Math.min(((entity.rotationYaw + 315f) % 360f) / 90f, 3);
		switch (rot)
		{
			case 0: // WEST
			{
				world.setBlockMetadataWithNotify(x, y, z, ForgeDirection.WEST.ordinal(), 3);
				break;
			}
			case 1: // NORTH
			{
				world.setBlockMetadataWithNotify(x, y, z, ForgeDirection.NORTH.ordinal(), 3);
				break;
			}
			case 2: // EAST
			{
				world.setBlockMetadataWithNotify(x, y, z, ForgeDirection.EAST.ordinal(), 3);
				break;
			}
			default: // SOUTH
			{
				world.setBlockMetadataWithNotify(x, y, z, ForgeDirection.SOUTH.ordinal(), 3);
				break;
			}
		}
	}

	@SideOnly(Side.CLIENT)
	@Override
	public int getRenderType()
	{
		return BlockRenderingHandler.BLOCK_RENDER_ID;
	}

	@Override
	public TileEntity createNewTileEntity(World world)
	{
		return new TileEntityCraneController();
	}

}
