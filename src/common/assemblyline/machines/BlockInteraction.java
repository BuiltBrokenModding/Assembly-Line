package assemblyline.machines;

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
 * A metadata block containing a bunch of machines with direction.
 * @author Darkguardsman, Calclavia
 *
 */
public class BlockInteraction extends BlockMachine
{
	public static enum MachineType
	{
		SORTER("Sorter", 0, TileEntitySorter.class),
		MANIPULATOR("Manipulator", 4, TileEntityManipulator.class),
		INVALID_1("Invalid", 8, null),
		INVALID_2("Invalid", 12, null);
		
		public String name;
		public int metadata;
		public Class<? extends TileEntity> tileEntity;
		
		MachineType(String name, int metadata, Class<? extends TileEntity> tileEntity)
		{
			this.name = name;
			this.metadata = metadata;
			this.tileEntity = tileEntity;
		}
		
		public static MachineType getBase(int metadata)
		{
			for(MachineType value : MachineType.values())
			{
				if(metadata >= value.metadata && metadata < value.metadata + 4)
				{
					return value;
				}
			}
			
			return null;
		}
		
		/**
		 * Gets the direction based on the metadata
		 * @return A direction value from 0 to 4.
		 */
		public static int getDirection(int metadata)
		{
			return metadata - MachineType.getBase(metadata).metadata;
		}
		
		/**
		 * @param currentDirection - An integer from 0 to 4.
		 * @return The metadata this block should change into.
		 */
		public int getNextDirectionMeta(int currentDirection)
		{
			currentDirection ++;
			
			if(currentDirection >= 4)
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
		return MachineType.getBase(metadata).metadata;
	}

	public boolean onMachineActivated(World par1World, int x, int y, int z, EntityPlayer par5EntityPlayer)
	{
		if (!par1World.isRemote)
		{
			int metadata = par1World.getBlockMetadata(x, y, z);
			par5EntityPlayer.openGui(AssemblyLine.instance, MachineType.getBase(metadata).metadata, par1World, x, y, z);
			return true;
		}
		return true;
	}
	
	@Override
	public boolean onUseWrench(World par1World, int x, int y, int z, EntityPlayer par5EntityPlayer)
	{		
		int metadata = par1World.getBlockMetadata(x, y, z);
		MachineType machineType = MachineType.getBase(metadata);
		par1World.setBlockAndMetadataWithNotify(x, y, z, this.blockID, machineType.getNextDirectionMeta(MachineType.getDirection(metadata)));
		return true;
	}
	
	@Override
	public boolean onSneakUseWrench(World par1World, int x, int y, int z, EntityPlayer par5EntityPlayer)
	{
		return this.onUseWrench(par1World, x, y, z, par5EntityPlayer);
	}

	@Override
	public TileEntity createNewTileEntity(World var1, int metadata)
	{
		return MachineType.getBase(metadata).instantiateTileEntity();
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
