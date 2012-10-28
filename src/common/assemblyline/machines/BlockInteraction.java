package assemblyline.machines;

import net.minecraft.src.AxisAlignedBB;
import net.minecraft.src.CreativeTabs;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.Material;
import net.minecraft.src.TileEntity;
import net.minecraft.src.World;
import net.minecraftforge.common.ForgeDirection;
import universalelectricity.prefab.BlockMachine;
import assemblyline.AssemblyLine;
import assemblyline.render.RenderHelper;

/**
 * A metadata block containing a bunch of machines
 * with direction.
 * 
 * @author Darkguardsman, Calclavia
 * 
 */
public class BlockInteraction extends BlockMachine
{
	public static enum MachineType
	{
		SORTER("Sorter", 0, TileEntitySorter.class), MANIPULATOR("Manipulator", 4, TileEntityManipulator.class), INVALID_1("Invalid", 8, null), INVALID_2("Invalid", 12, null);

		public String name;
		public int metadata;
		public Class<? extends TileEntity> tileEntity;

		MachineType(String name, int metadata, Class<? extends TileEntity> tileEntity)
		{
			this.name = name;
			this.metadata = metadata;
			this.tileEntity = tileEntity;
		}

		public static MachineType get(int metadata)
		{
			for (MachineType value : MachineType.values())
			{
				if (metadata >= value.metadata && metadata < value.metadata + 4) { return value; }
			}

			return null;
		}

		/**
		 * Gets the direction based on the
		 * metadata
		 * 
		 * @return A direction value from 0 to 4.
		 */
		public static int getDirection(int metadata)
		{
			return metadata - MachineType.get(metadata).metadata;
		}

		/**
		 * @param currentDirection
		 *            - An integer from 0 to 4.
		 * @return The metadata this block should
		 *         change into.
		 */
		public int getNextDirectionMeta(int currentDirection)
		{
			currentDirection++;

			if (currentDirection >= 4)
			{
				currentDirection = 0;
			}

			return currentDirection + this.metadata;
		}

		/**
		 * Creates a new TIleEntity.
		 */
		public TileEntity instantiateTileEntity()
		{
			try
			{
				return this.tileEntity.newInstance();
			}
			catch (Exception e)
			{
				e.printStackTrace();
				return null;
			}
		}
	}

	public BlockInteraction(int id)
	{
		super("Interaction Machine", id, Material.iron);
		this.setCreativeTab(CreativeTabs.tabTransport);
	}

	public int damageDropped(int metadata)
	{
		return MachineType.get(metadata).metadata;
	}

	public boolean onMachineActivated(World par1World, int x, int y, int z, EntityPlayer par5EntityPlayer)
	{
		if (!par1World.isRemote)
		{
			int metadata = par1World.getBlockMetadata(x, y, z);
			par5EntityPlayer.openGui(AssemblyLine.instance, MachineType.get(metadata).metadata, par1World, x, y, z);
			return true;
		}
		return true;
	}

	@Override
	public boolean onUseWrench(World par1World, int x, int y, int z, EntityPlayer par5EntityPlayer)
	{
		int metadata = par1World.getBlockMetadata(x, y, z);
		MachineType machineType = MachineType.get(metadata);
		par1World.setBlockAndMetadataWithNotify(x, y, z, this.blockID, machineType.getNextDirectionMeta(MachineType.getDirection(metadata)));
		return true;
	}

	@Override
	public boolean onSneakUseWrench(World par1World, int x, int y, int z, EntityPlayer par5EntityPlayer)
	{
		int metadata = par1World.getBlockMetadata(x, y, z);

		if (MachineType.get(metadata) == MachineType.MANIPULATOR)
		{
			((TileEntityManipulator) par1World.getBlockTileEntity(x, y, z)).isWrenchedToOutput = !((TileEntityManipulator) par1World.getBlockTileEntity(x, y, z)).isWrenchedToOutput;
			if (!par1World.isRemote)
				par5EntityPlayer.addChatMessage("Manipulator Output: " + ((TileEntityManipulator) par1World.getBlockTileEntity(x, y, z)).isWrenchedToOutput);
			return true;
		}
		else
		{
			return this.onUseWrench(par1World, x, y, z, par5EntityPlayer);
		}
	}

	/**
	 * Returns the bounding box of the wired
	 * rectangular prism to render.
	 */
	@Override
	public AxisAlignedBB getSelectedBoundingBoxFromPool(World par1World, int x, int y, int z)
	{
		return this.getCollisionBoundingBoxFromPool(par1World, x, y, z);
	}

	/**
	 * Returns a bounding box from the pool of
	 * bounding boxes (this means this box can
	 * change after the pool has been cleared to
	 * be reused)
	 */
	@Override
	public AxisAlignedBB getCollisionBoundingBoxFromPool(World par1World, int x, int y, int z)
	{
		int metadata = par1World.getBlockMetadata(x, y, z);

		if (MachineType.get(metadata) == MachineType.MANIPULATOR) { return AxisAlignedBB.getAABBPool().addOrModifyAABBInPool((double) x + this.minX, (double) y + this.minY, (double) z + this.minZ, (double) x + this.maxX, (double) y + 0.3f, (double) z + this.maxZ); }

		return AxisAlignedBB.getAABBPool().addOrModifyAABBInPool((double) x + this.minX, (double) y + this.minY, (double) z + this.minZ, (double) x + this.maxX, (double) y + this.maxY, (double) z + this.maxZ);
	}

	@Override
	public TileEntity createNewTileEntity(World var1, int metadata)
	{
		return MachineType.get(metadata).instantiateTileEntity();
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
