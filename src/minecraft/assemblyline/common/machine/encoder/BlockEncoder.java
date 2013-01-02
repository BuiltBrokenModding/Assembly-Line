package assemblyline.common.machine.encoder;

import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import universalelectricity.prefab.BlockMachine;
import universalelectricity.prefab.UETab;
import assemblyline.common.AssemblyLine;
import assemblyline.common.CommonProxy;

public class BlockEncoder extends BlockMachine
{
	public BlockEncoder(int id, int texture)
	{
		super(id, Material.wood);
		this.blockIndexInTexture = 4;
		this.setBlockName("encoder");
		this.setCreativeTab(UETab.INSTANCE);
		this.setTextureFile(AssemblyLine.BLOCK_TEXTURE_PATH);
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
			entityPlayer.openGui(AssemblyLine.instance, CommonProxy.GUI_ENCODER, world, x, y, z);
		}

		return true;

	}
	
	@Override
	public TileEntity createNewTileEntity(World world)
	{
		return createNewTileEntity(world, 0);
	}
	
	@Override
	public TileEntity createNewTileEntity(World world, int metadata)
	{
		return new TileEntityEncoder();
	}
}
