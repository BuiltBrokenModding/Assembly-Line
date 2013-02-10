package assemblyline.common.machine.crane;

import net.minecraft.entity.EntityLiving;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import universalelectricity.core.UniversalElectricity;
import universalelectricity.prefab.BlockMachine;
import assemblyline.client.render.BlockRenderingHandler;
import assemblyline.common.TabAssemblyLine;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockCraneController extends BlockMachine
{

	public BlockCraneController(int id)
	{
		super("cranecontroller", id, UniversalElectricity.machine);
		this.setResistance(5.0f);
		this.setHardness(5.0f);
		this.setCreativeTab(TabAssemblyLine.INSTANCE);
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
	public void onBlockPlacedBy(World world, int x, int y, int z, EntityLiving entity)
	{
		int rot = (int) Math.min((entity.rotationYaw - 45f) / 90f, 3);
		switch (rot)
		{
			case 0: // WEST
			{
				System.out.println("Facing west...");
				world.setBlockMetadataWithNotify(x, y, z, ForgeDirection.WEST.ordinal());
				break;
			}
			case 1: // NORTH
			{
				System.out.println("Facing north...");
				world.setBlockMetadataWithNotify(x, y, z, ForgeDirection.NORTH.ordinal());
				break;
			}
			case 2: // EAST
			{
				System.out.println("Facing east...");
				world.setBlockMetadataWithNotify(x, y, z, ForgeDirection.EAST.ordinal());
				break;
			}
			default: // SOUTH
			{
				System.out.println("Facing south...");
				world.setBlockMetadataWithNotify(x, y, z, ForgeDirection.SOUTH.ordinal());
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
	public TileEntity createNewTileEntity(World world, int metadata)
	{
		return new TileEntityCraneController();
	}

}
