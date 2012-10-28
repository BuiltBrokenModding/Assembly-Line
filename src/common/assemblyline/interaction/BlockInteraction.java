package assemblyline.interaction;

import net.minecraft.src.CreativeTabs;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.Material;
import net.minecraft.src.TileEntity;
import net.minecraft.src.World;
import universalelectricity.prefab.BlockMachine;
import assemblyline.AssemblyLine;
import assemblyline.render.RenderHelper;

/**
 * A metadata block containing a bunch of machines with direction.
 * @author Darkguardsman
 *
 */
public class BlockInteraction extends BlockMachine
{
	public static enum InteractMachineMetadata
	{
		EJECTOR("Ejector", 0), INJECTOR("Injector", 4);
		
		public String name;
		public int metadata;
		
		InteractMachineMetadata(String name, int metadata)
		{
			this.name = name;
			this.metadata = metadata;
		}
		
		public static InteractMachineMetadata getBase(int metadata)
		{
			if (metadata >= 0 && metadata < 4) { return InteractMachineMetadata.values()[0]; }
			else if (metadata >= 4 && metadata < 8) { return InteractMachineMetadata.values()[4]; }
			else if (metadata >= 8 && metadata < 12) { return InteractMachineMetadata.values()[8]; }
			else { return InteractMachineMetadata.values()[12]; }
		}
	}
	
	public BlockInteraction(int id)
	{
		super("Machine", id, Material.iron);
		this.setCreativeTab(CreativeTabs.tabTransport);
	}

	public int damageDropped(int metadata)
	{
		return InteractMachineMetadata.getBase(metadata).metadata;
	}

	public boolean onSneakUseWrench(World par1World, int x, int y, int z, EntityPlayer par5EntityPlayer)
	{
		return this.onSneakMachineActivated(par1World, x, y, z, par5EntityPlayer);
	}
	
	public boolean onSneakMachineActivated(World par1World, int x, int y, int z, EntityPlayer par5EntityPlayer)
	{
		if (!par1World.isRemote)
		{
			par5EntityPlayer.openGui(AssemblyLine.instance, 0, par1World, x, y, z);
			return true;
		}
		return true;
	}

	@Override
	public boolean onUseWrench(World par1World, int x, int y, int z, EntityPlayer par5EntityPlayer)
	{
		int metadata = par1World.getBlockMetadata(x, y, z);

		if (metadata == 3)
		{
			par1World.setBlockAndMetadataWithNotify(x, y, z, this.blockID, 0);
			return true;
		}
		else if (metadata == 7)
		{
			par1World.setBlockAndMetadataWithNotify(x, y, z, this.blockID, 4);
			return true;
		}
		else if (metadata == 11)
		{
			par1World.setBlockAndMetadataWithNotify(x, y, z, this.blockID, 8);
			return true;
		}
		else if (metadata == 15)
		{
			par1World.setBlockAndMetadataWithNotify(x, y, z, this.blockID, 12);
			return true;
		}
		else
		{
			par1World.setBlockAndMetadataWithNotify(x, y, z, this.blockID, metadata + 1);
			return true;
		}
	}

	/**
	 * gets the correct facing direction from meta
	 * data
	 * 
	 * @param meta
	 * @return facing direction(int)
	 */
	public byte getDirection(int meta)
	{

		switch (meta)
		{
			case 0:
				return 2;
			case 1:
				return 5;
			case 2:
				return 3;
			case 3:
				return 4;
			case 4:
				return 2;
			case 5:
				return 5;
			case 6:
				return 3;
			case 7:
				return 4;
			case 8:
				return 2;
			case 9:
				return 5;
			case 10:
				return 3;
			case 11:
				return 4;
			case 12:
				return 2;
			case 13:
				return 5;
			case 14:
				return 3;
			case 15:
				return 4;
		}
		return 0;
	}

	@Override
	public TileEntity createNewTileEntity(World var1, int metadata)
	{
		if (metadata >= 0 && metadata < 4) { return new TileEntityEjector(); }
		if (metadata >= 4 && metadata < 8) { return new TileEntityInjector(); }
		if (metadata >= 8 && metadata < 12) { return null; }
		if (metadata >= 12 && metadata < 16) { return null; }
		return null;
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
