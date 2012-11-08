package assemblyline.machines.crafter;

import net.minecraft.src.CreativeTabs;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.Material;
import net.minecraft.src.TileEntity;
import net.minecraft.src.World;
import universalelectricity.prefab.BlockMachine;
import assemblyline.AssemblyLine;

public class BlockCrafter extends BlockMachine
{
	protected BlockCrafter(int par1)
	{
		super("AutoCrafters", par1, Material.iron);
		this.setResistance(5.0f);
		this.setHardness(5.0f);
		this.setCreativeTab(CreativeTabs.tabTools);
	}

	public static enum CrafterType
	{
		CRAFTER("Crafter", 0, -1, TileEntityAutoCrafter.class);

		public String name;
		public int metadata;
		public int guiID;
		public Class<? extends TileEntity> tileEntity;

		CrafterType(String name, int metadata, int guiID, Class<? extends TileEntity> tileEntity)
		{
			this.name = name;
			this.metadata = metadata;
			this.guiID = guiID;
			this.tileEntity = tileEntity;
		}

		public static CrafterType get(int metadata)
		{
			for (CrafterType value : CrafterType.values())
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
			return metadata - CrafterType.get(metadata).metadata;
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

	@Override
	public TileEntity createNewTileEntity(World var1)
	{

		return null;
	}

	@Override
	public TileEntity createNewTileEntity(World var1, int metadata)
	{
		return CrafterType.get(metadata).instantiateTileEntity();
	}

	@Override
	public boolean onMachineActivated(World par1World, int x, int y, int z, EntityPlayer par5EntityPlayer)
	{
		if (!par1World.isRemote)
		{
			int metadata = par1World.getBlockMetadata(x, y, z);
			int guiID = CrafterType.get(metadata).metadata;
			if (guiID == -1)
				return false;
			par5EntityPlayer.openGui(AssemblyLine.instance, guiID, par1World, x, y, z);
			return true;
		}
		return true;
	}

	public boolean onSneakUseWrench(World par1World, int x, int y, int z, EntityPlayer par5EntityPlayer)
	{
		return false;
	}

	public int getRenderType()
	{
		return 0;
	}
}
