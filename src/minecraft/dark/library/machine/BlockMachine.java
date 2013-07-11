package dark.library.machine;

import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import universalelectricity.prefab.block.BlockAdvanced;

public class BlockMachine extends BlockAdvanced implements ITileEntityProvider
{

	protected BlockMachine(int par1, Material par2Material)
	{
		super(par1, par2Material);
		this.isBlockContainer = true;
	}

	/** Called whenever the block is added into the world. Args: world, x, y, z */
	public void onBlockAdded(World par1World, int par2, int par3, int par4)
	{
		super.onBlockAdded(par1World, par2, par3, par4);
	}

	/** ejects contained items into the world, and notifies neighbours of an update, as appropriate */
	public void breakBlock(World par1World, int par2, int par3, int par4, int par5, int par6)
	{
		super.breakBlock(par1World, par2, par3, par4, par5, par6);
		this.dropEntireInventory(par1World, par2, par3, par4, par5, par6);
		par1World.removeBlockTileEntity(par2, par3, par4);
	}

	/** Called when the block receives a BlockEvent - see World.addBlockEvent. By default, passes it
	 * on to the tile entity at this location. Args: world, x, y, z, blockID, EventID, event
	 * parameter */
	public boolean onBlockEventReceived(World par1World, int par2, int par3, int par4, int par5, int par6)
	{
		super.onBlockEventReceived(par1World, par2, par3, par4, par5, par6);
		TileEntity tileentity = par1World.getBlockTileEntity(par2, par3, par4);
		return tileentity != null ? tileentity.receiveClientEvent(par5, par6) : false;
	}

	@Override
	public TileEntity createNewTileEntity(World world)
	{
		// TODO Auto-generated method stub
		return null;
	}
	
	public void dropEntireInventory(World par1World, int x, int y, int z, int par5, int par6)
	{
	}

}
