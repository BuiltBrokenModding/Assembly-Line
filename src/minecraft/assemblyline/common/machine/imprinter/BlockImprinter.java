package assemblyline.common.machine.imprinter;

import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import universalelectricity.prefab.BlockMachine;
import assemblyline.common.AssemblyLine;
import assemblyline.common.CommonProxy;
import assemblyline.common.TabAssemblyLine;

public class BlockImprinter extends BlockMachine
{
	public BlockImprinter(int id, int texture)
	{
		super(id, Material.wood);
		this.blockIndexInTexture = texture;
		this.setBlockName("imprinter");
		this.setCreativeTab(TabAssemblyLine.INSTANCE);
		this.setTextureFile(AssemblyLine.BLOCK_TEXTURE_PATH);
	}

	@Override
	public void onBlockAdded(World world, int x, int y, int z)
	{
		super.onBlockAdded(world, x, y, z);
		System.out.println(String.format("Initializing instance of 'Crafting Simulator 2014' at coordinates (%d, %d, %d)...", x, y, z));
	}

	/**
	 * Returns the block texture based on the side being looked at. Args: side
	 */
	public int getBlockTextureFromSide(int side)
	{
		if (side == 0)
		{
			return this.blockIndexInTexture;

		}
		else if (side == 1) { return this.blockIndexInTexture + 1;

		}

		return this.blockIndexInTexture + 2;
	}

	/**
	 * Called upon block activation (right click on the block.)
	 */
	@Override
	public boolean onMachineActivated(World world, int x, int y, int z, EntityPlayer entityPlayer, int par6, float par7, float par8, float par9)
	{
		if (!world.isRemote)
		{
			entityPlayer.openGui(AssemblyLine.instance, CommonProxy.GUI_IMPRINTER, world, x, y, z);
		}

		return true;

	}

	@Override
	public TileEntity createNewTileEntity(World var1)
	{
		return new TileEntityImprinter();
	}
}
