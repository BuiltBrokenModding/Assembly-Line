package assemblyline.common.machine.crane;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import universalelectricity.core.UniversalElectricity;
import universalelectricity.prefab.BlockMachine;
import assemblyline.client.render.BlockRenderingHandler;
import assemblyline.common.TabAssemblyLine;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockCraneFrame extends BlockMachine
{
	public BlockCraneFrame(int id)
	{
		super("craneFrame", id, UniversalElectricity.machine);
		this.setCreativeTab(TabAssemblyLine.INSTANCE);
		this.setBlockBounds(0.25f, 0.25f, 0.25f, 0.75f, 0.75f, 0.75f);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public AxisAlignedBB getSelectedBoundingBoxFromPool(World world, int x, int y, int z)
	{
		return super.getSelectedBoundingBoxFromPool(world, x, y, z);
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z)
	{
		TileEntity tE = world.getBlockTileEntity(x, y, z);
		if (tE != null && tE instanceof TileEntityCraneRail)
		{
			AxisAlignedBB middle = AxisAlignedBB.getBoundingBox(0.25f, 0.25f, 0.25f, 0.75f, 0.75f, 0.75f);
			AxisAlignedBB up = AxisAlignedBB.getBoundingBox(0.25f, 0.75f, 0.25f, 0.75f, 1.0f, 0.75f);
			AxisAlignedBB down = AxisAlignedBB.getBoundingBox(0.25f, 0.0f, 0.25f, 0.75f, 0.25f, 0.75f);
			AxisAlignedBB left = AxisAlignedBB.getBoundingBox(0.0f, 0.25f, 0.25f, 0.25f, 0.75f, 0.75f);
			AxisAlignedBB right = AxisAlignedBB.getBoundingBox(0.75f, 0.25f, 0.25f, 1.0f, 0.75f, 0.75f);
			AxisAlignedBB front = AxisAlignedBB.getBoundingBox(0.25f, 0.25f, 0.0f, 0.75f, 0.75f, 0.25f);
			AxisAlignedBB back = AxisAlignedBB.getBoundingBox(0.25f, 0.25f, 0.75f, 0.75f, 0.75f, 1.0f);
			boolean connectUp = CraneHelper.canFrameConnectTo(tE, x, y + 1, z, ForgeDirection.DOWN);
			boolean connectDown = CraneHelper.canFrameConnectTo(tE, x, y - 1, z, ForgeDirection.UP);
			// EAST, X-
			boolean connectLeft = CraneHelper.canFrameConnectTo(tE, x - 1, y, z, ForgeDirection.EAST);
			// WAST, X+
			boolean connectRight = CraneHelper.canFrameConnectTo(tE, x + 1, y, z, ForgeDirection.WEST);
			// SOUTH, Z-
			boolean connectFront = CraneHelper.canFrameConnectTo(tE, x, y, z - 1, ForgeDirection.SOUTH);
			// NORTH, Z+
			boolean connectBack = CraneHelper.canFrameConnectTo(tE, x, y, z + 1, ForgeDirection.NORTH);
			if (connectUp)
			{
				middle.maxY = up.maxY;
			}
			if (connectDown)
			{
				middle.minY = down.minY;
			}
			if (connectLeft)
			{
				middle.minX = left.minX;
			}
			if (connectRight)
			{
				middle.maxX = right.maxX;
			}
			if (connectFront)
			{
				middle.minZ = front.minZ;
			}
			if (connectBack)
			{
				middle.maxZ = back.maxZ;
			}
			setBlockBounds((float) middle.minX, (float) middle.minY, (float) middle.minZ, (float) middle.maxX, (float) middle.maxY, (float) middle.maxZ);
			middle.offset(x, y, z);
			return middle;
		}
		return super.getCollisionBoundingBoxFromPool(world, x, y, z);
	}

	@Override
	public void setBlockBoundsBasedOnState(IBlockAccess world, int x, int y, int z)
	{
		TileEntity tE = world.getBlockTileEntity(x, y, z);
		if (tE != null && tE instanceof TileEntityCraneRail)
		{
			AxisAlignedBB middle = AxisAlignedBB.getBoundingBox(0.25f, 0.25f, 0.25f, 0.75f, 0.75f, 0.75f);
			AxisAlignedBB up = AxisAlignedBB.getBoundingBox(0.25f, 0.75f, 0.25f, 0.75f, 1.0f, 0.75f);
			AxisAlignedBB down = AxisAlignedBB.getBoundingBox(0.25f, 0.0f, 0.25f, 0.75f, 0.25f, 0.75f);
			AxisAlignedBB left = AxisAlignedBB.getBoundingBox(0.0f, 0.25f, 0.25f, 0.25f, 0.75f, 0.75f);
			AxisAlignedBB right = AxisAlignedBB.getBoundingBox(0.75f, 0.25f, 0.25f, 1.0f, 0.75f, 0.75f);
			AxisAlignedBB front = AxisAlignedBB.getBoundingBox(0.25f, 0.25f, 0.0f, 0.75f, 0.75f, 0.25f);
			AxisAlignedBB back = AxisAlignedBB.getBoundingBox(0.25f, 0.25f, 0.75f, 0.75f, 0.75f, 1.0f);
			boolean connectUp = CraneHelper.canFrameConnectTo(tE, x, y + 1, z, ForgeDirection.DOWN);
			boolean connectDown = CraneHelper.canFrameConnectTo(tE, x, y - 1, z, ForgeDirection.UP);
			// EAST, X-
			boolean connectLeft = CraneHelper.canFrameConnectTo(tE, x - 1, y, z, ForgeDirection.EAST);
			// WAST, X+
			boolean connectRight = CraneHelper.canFrameConnectTo(tE, x + 1, y, z, ForgeDirection.WEST);
			// SOUTH, Z-
			boolean connectFront = CraneHelper.canFrameConnectTo(tE, x, y, z - 1, ForgeDirection.SOUTH);
			// NORTH, Z+
			boolean connectBack = CraneHelper.canFrameConnectTo(tE, x, y, z + 1, ForgeDirection.NORTH);
			if (connectUp)
			{
				middle.maxY = up.maxY;
			}
			if (connectDown)
			{
				middle.minY = down.minY;
			}
			if (connectLeft)
			{
				middle.minX = left.minX;
			}
			if (connectRight)
			{
				middle.maxX = right.maxX;
			}
			if (connectFront)
			{
				middle.minZ = front.minZ;
			}
			if (connectBack)
			{
				middle.maxZ = back.maxZ;
			}
			setBlockBounds((float) middle.minX, (float) middle.minY, (float) middle.minZ, (float) middle.maxX, (float) middle.maxY, (float) middle.maxZ);
			return;
		}
		setBlockBounds(0.25f, 0.25f, 0.25f, 0.75f, 0.75f, 0.75f);
	}

	@Override
	public TileEntity createNewTileEntity(World world, int metadata)
	{
		return new TileEntityCraneRail();
	}

	@Override
	public boolean renderAsNormalBlock()
	{
		return false;
	}

	@Override
	public boolean isOpaqueCube()
	{
		return false;
	}

	@Override
	public int getRenderType()
	{
		return BlockRenderingHandler.BLOCK_RENDER_ID;
	}
}
